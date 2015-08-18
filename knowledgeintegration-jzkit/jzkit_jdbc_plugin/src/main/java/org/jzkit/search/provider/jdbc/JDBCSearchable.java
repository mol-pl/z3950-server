/**
 * Title:       SRWSearchable
 * @version:    $Id: JDBCSearchable.java,v 1.19 2005/10/18 12:13:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

/*
 * $Log: JDBCSearchable.java,v $
 * Revision 1.19  2005/10/18 12:13:18  ibbo
 * Updated
 *
 * Revision 1.18  2005/10/12 10:30:17  ibbo
 * Updated
 *
 * Revision 1.17  2005/10/12 10:20:14  ibbo
 * Added sort bind variables seperate to where bv's
 *
 * Revision 1.16  2004/11/18 14:56:27  ibbo
 * Added appctx parameter to call toInternalQueryModel
 *
 * Revision 1.15  2004/11/04 12:50:01  ibbo
 * Added gazeteer interface
 *
 * Revision 1.14  2004/10/31 12:21:22  ibbo
 * Database criteria added
 *
 * Revision 1.13  2004/10/29 16:39:23  ibbo
 * Added MySQL Match Against Functionality for free text searching
 *
 * Revision 1.12  2004/10/28 15:13:46  ibbo
 * JDBC Query can now map a use attribute onto multiple database columns which
 * will be OR'd together
 *
 * Revision 1.11  2004/10/28 12:54:23  ibbo
 * Updated
 *
 * Revision 1.10  2004/10/28 12:31:41  ibbo
 * Moved to new framework for constructing searchable which passes in ApplicationContext
 *
 * Revision 1.9  2004/10/27 14:41:20  ibbo
 * XML record export working
 *
 * Revision 1.8  2004/10/26 16:42:23  ibbo
 *
 * Updated
 *
 * Revision 1.7  2004/10/26 15:30:52  ibbo
 * Updated
 *
 * Revision 1.6  2004/10/26 11:28:38  ibbo
 * Updated
 *
 * Revision 1.5  2004/10/24 15:18:31  ibbo
 * updated
 *
 * Revision 1.4  2004/10/24 11:56:36  ibbo
 * Updated
 *
 * Revision 1.3  2004/10/22 17:42:21  ibbo
 * Updated
 *
 * Revision 1.2  2004/10/22 16:59:59  ibbo
 * Updated
 *
 * Revision 1.1  2004/10/22 09:24:28  ibbo
 * Added jdbc plugin
 *
 */

package org.jzkit.search.provider.jdbc;

import java.util.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.provider.iface.*;
import java.util.logging.*;
import org.jzkit.search.util.QueryModel.*;
import javax.sql.*;
import java.sql.*;

import com.k_int.sql.sql_syntax.*;
import com.k_int.sql.qm_to_sql.*;
import com.k_int.sql.data_dictionary.*;
import java.io.StringWriter;

import jdbm.*;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import jdbm.helper.LongComparator;
import jdbm.btree.BTree;
import org.springframework.context.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ian Ibbotson
 * @version $Id: JDBCSearchable.java,v 1.19 2005/10/18 12:13:18 ibbo Exp $
 */ 
public class JDBCSearchable implements Searchable {

  private static Log log = LogFactory.getLog(JDBCSearchable.class);
  private Map archetypes = new HashMap();
  
  private String datasource_name = null;
  private String dictionary_name = null;
  private String templates_config_name = null;
  private String access_paths_config_name = null;
  private String sql_dialect = null;

  private DataSource datasource = null;
  private com.k_int.sql.data_dictionary.Dictionary dictionary = null;
  private com.k_int.sql.qm_to_sql.QMToSQLConfig access_paths = null;
  private RecordTemplatesConfig templates = null;

  private ApplicationContext ctx = null;

  private boolean setup_completed = false;

  public JDBCSearchable() {
  }

  public void close() {
  }

  public IRResultSet evaluate(IRQuery q) {
    return evaluate(q, null);
  }

  public IRResultSet evaluate(IRQuery q, Object user_info) {
    return evaluate(q,user_info,null);
  }

  public IRResultSet evaluate(IRQuery q, Object user_info, Observer[] observers) {

    checkSetup();

    log.debug("create JDBC Result Set");
    JDBCResultSet result = new JDBCResultSet(this);

    try {
      log.debug("get Dialect - "+sql_dialect);

      if ( sql_dialect == null ) 
        throw new JDBCConfigException("NO SQL Dialect");

      // SQL Dialect that knows about the odd bits of oracle such as seqno, dual, contains, etc.
      Class dialect_class = Class.forName(sql_dialect);

      com.k_int.sql.sql_syntax.SQLDialect dialect = (com.k_int.sql.sql_syntax.SQLDialect) dialect_class.newInstance(); // new com.k_int.sql.sql_syntax.provider.MySQLDialect();
                                                                                                                                          
      log.debug("init jdbc result set");
      result.init(dictionary,datasource,dialect,templates);

      log.debug("Generate SQL");
      Vector where_bind_variables = new Vector();
      Vector sort_bind_variables = new Vector();

      // An object representation of the SQL Select Statement we will add clauses to for this query
      // true param selects "Distinct" keyword
      SelectStatement select_statement = new SelectStatement(true);
                                                                                                                                          
      // Create the worker class that converts an internal query model into SQL
      SQLQueryVisitor qv = new SQLQueryVisitor(dictionary,
                                               dialect,
                                               q.getCollections(),
                                               access_paths,
                                               select_statement,
                                               where_bind_variables,
                                               sort_bind_variables,
                                               new ArrayList(),
                                               ctx);
                                                                                                                                          
      EntityTemplate et = qv.getBaseEntityTemplate();
                                                                                                                                          
      // Do the work of creating the SQL
      qv.visit(q.getQueryModel().toInternalQueryModel(ctx));
                                                                                                                                          
      // Get the result.
      StringWriter sql_string_writer = new StringWriter();
      dialect.emitSelectStatement(sql_string_writer,select_statement);
      String sql = sql_string_writer.toString();

      log.debug("SQL:"+sql+", bv="+where_bind_variables+", sort bv="+sort_bind_variables);

      // Execute the SQL
      Connection c = datasource.getConnection();

      PreparedStatement s = c.prepareStatement(sql);
      s.clearParameters();

      int counter = 1;
      for ( Iterator i = where_bind_variables.iterator(); i.hasNext(); ) {
        s.setObject(counter++,i.next());
      }
      for ( Iterator i = sort_bind_variables.iterator(); i.hasNext(); ) {
        s.setObject(counter++,i.next());
      }

      ResultSet r = s.executeQuery();


      int col_count = r.getMetaData().getColumnCount();

      // We store the matching keys in an external data file for later use so we can release this
      // connection back to the pool asap.
      while ( r.next() ) {
        EntityKey ek = new EntityKey(et, r);
        OID key = new OID(datasource_name,et.getEntityName(),  new EntityKey(et, r));
        result.add(key);
      }

      result.commit();

      r.close();
      s.close();
      c.close();

      result.setStatus(IRResultSetStatus.COMPLETE);
    }
    catch ( UnknownCollectionException unknown_collection_exception ) {
      log.warn("problem evaluating query ",unknown_collection_exception);
      result.setStatus(IRResultSetStatus.FAILURE);
    }
    catch ( UnknownAccessPointException unknown_access_point_exception ) {
      log.warn("problem evaluating query ",unknown_access_point_exception);
      result.setStatus(IRResultSetStatus.FAILURE);
    }
    catch ( InvalidQueryException invalid_query_exception ) {
      log.warn("problem evaluating query ", invalid_query_exception);
      result.setStatus(IRResultSetStatus.FAILURE);
    }
    catch ( java.io.IOException input_output_exception ) {
      log.warn("problem evaluating query ", input_output_exception);
      result.setStatus(IRResultSetStatus.FAILURE);
    }
    catch ( java.sql.SQLException sqle ) {
      sqle.printStackTrace();
      result.setStatus(IRResultSetStatus.FAILURE);
    }
    catch ( java.lang.ClassNotFoundException cnfe ) {
      cnfe.printStackTrace();
      result.setStatus(IRResultSetStatus.FAILURE);
    }
    catch ( java.lang.InstantiationException ie ) {
      ie.printStackTrace();
      result.setStatus(IRResultSetStatus.FAILURE);
    }
    catch ( java.lang.IllegalAccessException iae ) {
      iae.printStackTrace();
      result.setStatus(IRResultSetStatus.FAILURE);
    }
    catch ( JDBCConfigException jdbc_config_exception ) {
      jdbc_config_exception.printStackTrace();
      result.setStatus(IRResultSetStatus.FAILURE);
    }
    finally {
      log.debug("evaluate complete");
    }

    return result;
  }

  public void setRecordArchetypes(Map archetypes) {
    this.archetypes = archetypes;
  }
                                                                                                                                          
  public Map getRecordArchetypes() {
    return archetypes;
  }

  public void setDatasourceName(String datasource_name) {
    this.datasource_name = datasource_name;
  }

  public String getDataSourceName() {
    return datasource_name;
  }

  public void setDictionaryName(String dictionary_name) {
    this.dictionary_name = dictionary_name;
  }

  public String getDictionaryName() {
    return dictionary_name;
  }

  public void setAccessPathsConfigName(String access_paths_config_name) {
    this.access_paths_config_name = access_paths_config_name;
  }

  public String getAccessPathsConfigName() {
    return access_paths_config_name;
  }

  public void setTemplatesConfigName(String templates_config_name) {
    this.templates_config_name = templates_config_name;
  }

  public String getTemplatesConfigName() {
    return templates_config_name;
  }

  public void setSQLDialect(String sql_dialect) {
    this.sql_dialect = sql_dialect;
  }

  public String getSQLDialect() {
    return sql_dialect;
  }

  private synchronized void checkSetup() {
    if ( !setup_completed ) {
      try {
        log.debug("checkSetup");
        log.debug("templates..."+templates_config_name);
        this.templates = (RecordTemplatesConfig) ctx.getBean(templates_config_name);
        log.debug("access paths..."+access_paths_config_name);
        this.access_paths = (com.k_int.sql.qm_to_sql.QMToSQLConfig) ctx.getBean(access_paths_config_name);
        log.debug("dictionary..."+dictionary_name);
        this.dictionary = (com.k_int.sql.data_dictionary.Dictionary)  ctx.getBean(dictionary_name);
        log.debug("datasource... "+datasource_name);
        this.datasource = (DataSource) ctx.getBean(datasource_name);
        log.debug("setup completed...");
        setup_completed = true;
      }
      catch ( Exception e ) {
        log.warn("Problem configuring JDBC Searchable",e);
      }
    }
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

}
