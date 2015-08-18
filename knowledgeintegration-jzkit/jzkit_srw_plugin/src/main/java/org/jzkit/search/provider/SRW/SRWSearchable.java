/**
 * Title:       SRWSearchable
 * @version:    $Id: SRWSearchable.java,v 1.7 2004/11/19 16:42:50 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

/*
 * $Log: SRWSearchable.java,v $
 * Revision 1.7  2004/11/19 16:42:50  ibbo
 * Updated
 *
 * Revision 1.6  2004/10/28 12:54:23  ibbo
 * Updated
 *
 * Revision 1.5  2004/10/05 18:36:51  ibbo
 * Made srw work again
 *
 * Revision 1.4  2004/10/04 11:20:58  ibbo
 * Updated
 *
 * Revision 1.3  2004/09/24 16:46:21  ibbo
 * All final result set objects now implement IRResultSet directly instead of
 * inheriting the implementation from the Abstract base class.. This seems to
 * work better for trmi
 *
 * Revision 1.2  2004/09/13 16:24:14  ibbo
 * Updated
 *
 * Revision 1.1.1.1  2004/06/18 06:38:40  ibbo
 * Initial
 *
 * Revision 1.1.1.1  2004/03/08 15:25:38  ibbo
 * Initial Import
 *
 * Revision 1.2  2004/02/07 17:42:51  ibbo
 * Updated
 *
 * Revision 1.1.1.1  2003/12/05 16:30:44  ibbo
 * Initial Import
 *
 * Revision 1.4  2003/11/23 14:44:28  ibbo
 * If conversion returned null, subsequent methods throw NPE.
 *
 * Revision 1.3  2003/11/23 14:41:16  ibbo
 * Improved SRW hadling
 *
 * Revision 1.2  2003/11/16 16:02:36  ibbo
 * Imported SRW fragments
 *
 * Revision 1.1.1.1  2003/11/16 15:10:42  ibbo
 * Initial import
 *
 */

package org.jzkit.search.provider.SRW;

import java.util.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.provider.iface.*;
import java.util.logging.*;
import org.jzkit.search.util.QueryModel.CQLString.*;
import org.jzkit.search.util.QueryModel.*;
import org.springframework.context.*;

/**
 * @author Ian Ibbotson
 * @version $Id: SRWSearchable.java,v 1.7 2004/11/19 16:42:50 ibbo Exp $
 */ 
public class SRWSearchable implements Searchable {

  private Logger log = Logger.getLogger(SRWSearchable.class.getName());
  private String base_url;
  private String code;
  private Map archetypes = new HashMap();
  private ApplicationContext ctx = null;

  public SRWSearchable() {
    log.fine("new SRWSearchable");
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

    SRWResultSet result = null;

    try {
      result = new SRWResultSet(observers, base_url, getCQLString(q), code);
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
