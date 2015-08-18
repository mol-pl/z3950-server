/*
 * Title:       JDBCResultSet
 * @version:    $Id: JDBCResultSet.java,v 1.15 2005/10/20 15:39:53 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

/*
 * $Log: JDBCResultSet.java,v $
 * Revision 1.15  2005/10/20 15:39:53  ibbo
 * Temp files locations can now be influenced with the JVM flag com.k_int.inode.tmpdir
 *
 * Revision 1.14  2005/10/20 14:18:03  ibbo
 * updated
 *
 * Revision 1.13  2005/10/18 12:13:18  ibbo
 * Updated
 *
 * Revision 1.12  2005/02/18 09:24:22  ibbo
 * Added getResultSet info to all RS impls
 *
 * Revision 1.11  2004/10/31 15:52:46  ibbo
 * Updated
 *
 * Revision 1.10  2004/10/31 12:21:22  ibbo
 * Database criteria added
 *
 * Revision 1.9  2004/10/29 10:11:23  ibbo
 * Minor revision to aggregating result set close functions
 *
 * Revision 1.8  2004/10/28 15:13:46  ibbo
 * JDBC Query can now map a use attribute onto multiple database columns which
 * will be OR'd together
 *
 * Revision 1.7  2004/10/28 12:31:41  ibbo
 * Moved to new framework for constructing searchable which passes in ApplicationContext
 *
 * Revision 1.6  2004/10/27 14:41:20  ibbo
 * XML record export working
 *
 * Revision 1.5  2004/10/26 16:42:23  ibbo
 *
 * Updated
 *
 * Revision 1.4  2004/10/26 15:30:52  ibbo
 * Updated
 *
 * Revision 1.3  2004/10/26 11:28:38  ibbo
 * Updated
 *
 * Revision 1.2  2004/10/24 15:33:58  ibbo
 * Updated
 *
 * Revision 1.1  2004/10/24 15:18:31  ibbo
 * updated
 *
 */

package org.jzkit.search.provider.jdbc;

import java.util.*;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;

import com.k_int.sql.sql_syntax.*;
import com.k_int.sql.qm_to_sql.*;
import com.k_int.sql.data_dictionary.*;
import java.io.StringWriter;

import java.sql.Connection;
import javax.sql.DataSource;
import jdbm.*;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import jdbm.helper.LongComparator;
import jdbm.btree.BTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jzkit.search.util.OAIHeaderInfo;

/**
 * @author Ian Ibbotson
 * @version $Id: JDBCResultSet.java,v 1.15 2005/10/20 15:39:53 ibbo Exp $
 */ 
public class JDBCResultSet extends AbstractIRResultSet implements IRResultSet {

  private static Log log = LogFactory.getLog(AbstractIRResultSet.class);
  int num_hits = 0;
  private jdbm.RecordManager recman = null;
  private BTree tree = null;

  // private String base_entity_name = null;
  private com.k_int.sql.data_dictionary.Dictionary data_dictionary = null;
  private DataSource datasource = null;
  private SQLDialect dialect = null;
  private RecordTemplatesConfig templates = null;
  private JDBCSearchable owner = null;
  private String results_file_name;

  private static long instance_counter = 0;

  private JDBCResultSet() {
    log.info("JDBCResultSet() "+(++instance_counter));
  }

  public JDBCResultSet(JDBCSearchable owner) {
    super();
    this.owner = owner;
    log.info("New JDBCResultSet:"+(++instance_counter));
  }

  protected void finalize() {
    log.info("JDBCResultSet::finalize"+(--instance_counter));
  }

  public void init(// String base_entity_name,
                   com.k_int.sql.data_dictionary.Dictionary data_dictionary,
                   DataSource datasource,
                   SQLDialect dialect,
                   RecordTemplatesConfig templates) {

    // this.base_entity_name = base_entity_name;
    this.data_dictionary = data_dictionary;
    this.datasource = datasource;
    this.dialect = dialect;
    this.templates=templates;

    // Set up temp results file
    try {
      java.io.File results_file = null;
      String dir = System.getProperty("com.k_int.inode.tmpdir");
      if ( dir != null )
        results_file = java.io.File.createTempFile("JDBCRS","jdbm",new java.io.File(dir));
      else
        results_file = java.io.File.createTempFile("JDBCRS","jdbm");

      results_file_name = results_file.toString();
                                                                                                                                          
      java.util.Properties props = new java.util.Properties();
      props.put( RecordManagerOptions.CACHE_SIZE, "100" );
      props.put( RecordManagerOptions.DISABLE_TRANSACTIONS, "true" );
      recman = RecordManagerFactory.createRecordManager(results_file_name,props);
      results_file.delete();
      tree = BTree.createInstance(recman,new LongComparator());
    }
    catch ( java.io.IOException ioe ) {
      ioe.printStackTrace();
    }
  }

  // Fragment Source methods
  public InformationFragment[] getFragment(int starting_fragment,
                                           int count,
                                           RecordFormatSpecification spec) throws IRResultSetException {

    log.debug("getFragment "+starting_fragment+","+count+","+spec);


    InformationFragment[] result = new InformationFragment[count];

    Connection c = null;
    PersistenceContext ctx = null;

    try {
      c = datasource.getConnection();
      ctx = new PersistenceContext(data_dictionary, "local", dialect, c);

      for ( int i=0; i<count; i++ ) {
        OID oid = (OID) tree.find(new Long((starting_fragment-1)+i));

        // System.err.println("processing "+oid);

        // Step one.. Look up the map for this collection
        // Map templates_map = (Map) templates.getMap().get(oid.getCollection());
        Map templates_map = null;
        Map archetype_map = null;

        JDBCCollectionMappingInfo mapping_info = templates.lookupEntityTemplateMappingInfo(oid.getCollection());

        templates_map = mapping_info.getRecordSpecToTemplateMap();
        archetype_map = mapping_info.getRecordArchetypesMap();

        FragmentFactory fragment_factory = (FragmentFactory) templates_map.get(spec.toString());

        if ( fragment_factory != null ) {
          Entity e = ctx.deserialise(oid);
          EntityTemplate et = e.getTemplate();

          ExplicitRecordFormatSpecification rec_spec = null;
          if ( ( et.getDiscriminatorAttrName() != null ) && ( et.getDiscriminatorAttrName().length() > 0 ) ) {
            rec_spec = new ExplicitRecordFormatSpecification("xml:"+et.getEntityName()+"-"+e.get(et.getDiscriminatorAttrName())+":F");
          }
          else {
            rec_spec = new ExplicitRecordFormatSpecification("xml:"+et.getEntityName()+":F");
          }

          result[i] = new org.jzkit.search.util.RecordModel.DOMTree("repos",
                                                                    "coll",
                                                                    oid.toString(),
                                                                    fragment_factory.createFragment(e),
                                                                    rec_spec);


          // Add a new OAIInfoStruct if supported for this entity (And if requested?)
          //
          if ( ( et.getOAIHeaderSupported() != null ) && ( et.getOAIHeaderSupported().equals(Boolean.TRUE) ) ) {
            try {
              Date date_added = (Date) e.get(et.getDateAddedAttrName());
              Date date_modified = (Date) e.get(et.getDateLastModifiedAttrName());
              Date date_deleted = (Date) e.get(et.getDateDeletedAttrName());
              // Boolean deleted_flag = (Boolean) e.get(et.getDeletedFlagAttrName());
              Boolean deleted_flag = Boolean.FALSE;
              String identifier = e.getOID().toString();
              String coll_names = "";

              log.debug("Adding OAIHeaderInfo");
              result[i].getExtendedInfo().put("OAIHeaderInfo",new OAIHeaderInfo(date_added, 
                                                                                date_modified, 
                                                                                date_deleted, 
                                                                                deleted_flag.booleanValue(),
                                                                                identifier,
                                                                                coll_names));
            }
            catch ( com.k_int.sql.data_dictionary.UnknownAccessPointException uape ) {
              uape.printStackTrace();
            }
          }
        }
        else {
          log.warn("No fragment factory available for spec "+spec);
          throw new IRResultSetException("No fragment factory available for spec "+spec);
        }
      }

    }
    catch ( java.io.IOException ioe ) {
      throw new IRResultSetException("Problem retrieving record",ioe);
    }
    catch ( java.sql.SQLException sqle ) {
      throw new IRResultSetException("Problem retrieving record",sqle);
    }
    catch ( com.k_int.sql.data_dictionary.UnknownCollectionException uce ) {
      throw new IRResultSetException("Problem retrieving record",uce);
    }
    catch ( com.k_int.sql.data_dictionary.UnknownAccessPointException uape ) {
      throw new IRResultSetException("Problem retrieving record",uape);
    }
    finally {
      ctx.close();
    }

    log.debug("getFragment::return");
    return result;
  }

  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification spec,
                               IFSNotificationTarget target) {
    log.debug("asyncGetFragment "+starting_fragment+","+count+","+spec);
    try
    {
      InformationFragment[] result = getFragment(starting_fragment,count,spec);
      target.notifyRecords(result);
    }
    catch ( IRResultSetException re )
    {
      re.printStackTrace();
      target.notifyError("JDBC", new Integer(0), "No reason", re);
    }
  }

  /** Current number of fragments available */
  public int getFragmentCount() {
    return num_hits;
  }

  /** The size of the result set (Estimated or known) */
  public int getRecordAvailableHWM() {
    return num_hits;
  }

  /** Release all resources and shut down the object */
  public void close() {
    log.info("JDBCResultSet::close() ");
    try {
      recman.close();

      log.info("Deleting JDBC Results "+results_file_name+"[.db,.lg]");

      java.io.File f = new java.io.File(results_file_name+".db");
      f.delete();
      f=null;
      f = new java.io.File(results_file_name+".lg");
      f.delete();
      f=null;
    }
    catch ( java.io.IOException ioe ) {
      log.warn("Problem deleting temp files",ioe);
      ioe.printStackTrace();
    }
  }

  public void add(OID key) {
    try {
      tree.insert(new Long(num_hits++),key,false);
    }
    catch ( java.io.IOException ioe ) {
      ioe.printStackTrace();
    }
  }

  protected void commit() {
    try {
      recman.commit();
    }
    catch ( java.io.IOException ioe ) {
      ioe.printStackTrace();
    }
  }

  public IRResultSetInfo getResultSetInfo() {
    return new IRResultSetInfo(getResultSetName(),
                               "JDBC",
                               null,
                               getFragmentCount(),
                               getStatus(),
                               null);
  }
}
