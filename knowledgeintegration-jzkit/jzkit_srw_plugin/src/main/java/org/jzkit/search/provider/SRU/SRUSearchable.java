/**
 * Title:       SRUSearchable
 * @version:    $Id: SRUSearchable.java,v 1.1 2005/10/30 13:42:40 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

/*
 * $Log: SRUSearchable.java,v $
 * Revision 1.1  2005/10/30 13:42:40  ibbo
 * updated - added new SRU classes to work around some CQL issues, etc
 *
 */

package org.jzkit.search.provider.SRU;

import java.util.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.provider.iface.*;
import java.util.logging.*;
import org.jzkit.search.util.QueryModel.CQLString.*;
import org.jzkit.search.util.QueryModel.*;
import org.springframework.context.*;

/**
 * @author Ian Ibbotson
 * @version $Id: SRUSearchable.java,v 1.1 2005/10/30 13:42:40 ibbo Exp $
 */ 
public class SRUSearchable implements Searchable {

  private Logger log = Logger.getLogger(SRUSearchable.class.getName());
  private String base_url;
  private String code;
  private Map archetypes = new HashMap();
  private ApplicationContext ctx = null;

  public SRUSearchable() {
    log.fine("new SRUSearchable");
  }

  public void close() {
  }

  public void setBaseURL(String base_url) { 
    this.base_url = base_url; 
  }

  public void setCode(String code) { 
    this.code = code; 
  }

  public String getBaseURL() { 
    return base_url; 
  }

  public String getCode() { 
    return code; 
  }

  public IRResultSet evaluate(IRQuery q) {
    return evaluate(q, null);
  }

  public IRResultSet evaluate(IRQuery q, Object user_info) {
    return evaluate(q,user_info,null);
  }

  public IRResultSet evaluate(IRQuery q, Object user_info, Observer[] observers) {

    SRUResultSet result = null;

    try {
      result = new SRUResultSet(observers, base_url, getCQLString(q), code);
      result.setStatus(IRResultSetStatus.COMPLETE);
    }
    catch ( Exception e ) {
      result.setStatus(IRResultSetStatus.FAILURE);
      e.printStackTrace();
    }

    return result;
  }

  private String getCQLString(IRQuery q) throws InvalidQueryException {
    // Here we need to get an instance of CQLQuery.. So if thats not what we have been passed,
    // this is the place to create one.
    log.fine("Class of IRQuery: "+q.getQueryModel().getClass().getName());

    CQLString cql_string = null;
    QueryModel qm = q.getQueryModel();

    if ( qm instanceof CQLString )
      cql_string = (CQLString)qm;
    else
      cql_string = CQLBuilder.buildFrom(qm, ctx);

    String retval = cql_string.toString();
    log.fine("cql to use is : "+retval);

    // return "dc.title=science";
    return retval;
  }

  public void setRecordArchetypes(Map archetypes) {
    this.archetypes = archetypes;
  }
                                                                                                                                          
  public Map getRecordArchetypes() {
    return archetypes;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

}
