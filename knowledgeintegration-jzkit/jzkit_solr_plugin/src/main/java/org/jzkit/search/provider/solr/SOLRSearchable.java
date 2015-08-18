/**
 * Title:       SOLRSearchable
 * @version:    $Id: SOLRSearchable.java,v 1.1 2005/10/30 13:42:40 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

package org.jzkit.search.provider.solr;

import java.util.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.QueryFormatter.QueryFormatter;
import org.jzkit.search.provider.iface.*;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SOLRSearchable implements Searchable {

  private static Log log = LogFactory.getLog(SOLRSearchable.class);
  private String base_url;
  private String code;
  // The default query formatter
  private String query_formatter = "QF-SOLR-STD";
  private Map archetypes = new HashMap();
  private ApplicationContext ctx = null;
  private Map<String,String> field_lists = new HashMap<String,String>();

  public SOLRSearchable() {
    log.debug("new SOLRSearchable");
  }

  public void close() {
  }

  public void setBaseURL(String base_url) { 
    this.base_url = base_url; 
  }

  public void setCode(String code) { 
    this.code = code; 
  }

  public void setQueryFormatter(String query_formatter) {
    this.query_formatter = query_formatter;
  }

  public String getBaseURL() { 
    return base_url; 
  }

  public String getCode() { 
    return code; 
  }

  public String getQueryFormatter() {
    return query_formatter;
  }

  public IRResultSet evaluate(IRQuery q) {
    return evaluate(q, null);
  }

  public IRResultSet evaluate(IRQuery q, Object user_info) {
    return evaluate(q,user_info,null);
  }

  public IRResultSet evaluate(IRQuery q, Object user_info, Observer[] observers) {
    SOLRResultSet result = null;
    try {
      log.debug("Casting internal query tree using "+query_formatter);
      QueryFormatter formatter = (QueryFormatter) ctx.getBean(query_formatter);
      String query = formatter.format(q.getQueryModel().toInternalQueryModel(ctx));
      result = new SOLRResultSet(observers, base_url, query, code,field_lists, q);
      result.countHits();
      result.setStatus(IRResultSetStatus.COMPLETE);
    }
    catch ( Exception e ) {
      // e.printStackTrace();
      if ( result == null ) {
        result = new SOLRResultSet(observers, base_url, "", code,field_lists, q);
      }
      log.error("Evaluate failed... Setting search status to fail and logging exception as message",e);
      result.setStatus(IRResultSetStatus.FAILURE);
      result.postMessage(e.getMessage());
    }

    return result;
  }

  public void setRecordArchetypes(Map archetypes) {
    this.archetypes = archetypes;
  }
                                                                                                                                          
  public Map getRecordArchetypes() {
    return archetypes;
  }

  public void setFieldList(Map<String,String> field_lists) {
    this.field_lists = field_lists;
  }

  public Map<String,String> getFieldList() {
    return field_lists;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

}
