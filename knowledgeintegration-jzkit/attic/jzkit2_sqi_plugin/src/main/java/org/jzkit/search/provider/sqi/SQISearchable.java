/*
 * $Log: SQISearchable.java,v $
 * Revision 1.3  2005/06/21 10:53:08  ibbo
 * Updated
 *
 * Revision 1.2  2005/04/08 08:21:42  ibbo
 * Updated
 *
 * Revision 1.1  2005/04/08 07:56:00  ibbo
 * Added
 *
 */

package org.jzkit.search.provider.sqi;

import java.util.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.QueryModel.CQLString.*;
import org.jzkit.search.util.QueryModel.*;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.elena.service.sqi.target.*;
import org.elena.service.sessionmgmt.*;
import org.elena.tools.sessionmgmt.MD5.*;

import java.net.URL;

/**
 * Title:       SQISearchable
 * @version:    $Id: SQISearchable.java,v 1.3 2005/06/21 10:53:08 ibbo Exp $
 * Copyright:   Copyright (C) 1999-2005 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

public class SQISearchable implements Searchable {

  private String base_url;
  private Map archetypes = new HashMap();
  private ApplicationContext ctx = null;
  private static Log log = LogFactory.getLog(SQISearchable.class);
  private Query port = null;

  private String session_id;

  public SQISearchable() {
    log.debug("new SQISearchable");
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

    if ( port == null )
      setup();

    SQIResultSet result = null;

    try {
      String str_result = port.synchronousQuery(session_id, "Hello Query");
      log.debug("result = "+str_result);
      result = new SQIResultSet(observers, base_url);
      result.setStatus(IRResultSetStatus.COMPLETE);
    }
    catch ( Exception e ) {
      result.setStatus(IRResultSetStatus.FAILURE);
      e.printStackTrace();
    }

    return result;
  }

  public void setBaseURL(String base_url) {
    this.base_url = base_url;
  }

  public String getBaseURL() {
    return base_url;
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

  private void setup() {
    try {
      SessionManagementService sess_mgt_service = new SessionManagementServiceLocator();
      SessionManagement session_mgt = sess_mgt_service.getSessionManagement(new URL(base_url+"/SessionManagement"));
      // session_id = session_mgt.createSession("username",MD5.getMD5FromString("passwordMD5"));
      session_id = session_mgt.createAnonymousSession();
      log.debug("New session id : "+session_id);
      QueryService query_service = new QueryServiceLocator();
      port = query_service.getQuery(new URL(base_url+"/Query"));
    }
    catch ( java.net.MalformedURLException murle ) {
      murle.printStackTrace();
    }
    catch ( javax.xml.rpc.ServiceException se ) {
      se.printStackTrace();
    }
    catch ( java.rmi.RemoteException re ) {
      re.printStackTrace();
    }
  }
}
