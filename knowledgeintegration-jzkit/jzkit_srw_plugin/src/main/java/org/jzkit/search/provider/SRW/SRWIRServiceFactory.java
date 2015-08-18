/**
 * Title:       SRWIRServiceDescription
 * @version:    $Id: SRWIRServiceFactory.java,v 1.5 2004/11/27 09:18:57 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

/*
 * $Log: SRWIRServiceFactory.java,v $
 * Revision 1.5  2004/11/27 09:18:57  ibbo
 * Updated to make app context available more widely
 *
 * Revision 1.4  2004/10/05 18:36:51  ibbo
 * Made srw work again
 *
 * Revision 1.3  2004/09/16 12:38:01  ibbo
 * Fixed bug in simple aggregating result set
 *
 * Revision 1.2  2004/09/13 16:24:14  ibbo
 * Updated
 *
 * Revision 1.1  2004/08/26 13:41:55  ibbo
 * Updated
 *
 * Revision 1.3  2004/08/24 20:23:42  ibbo
 * Updated to new wsdl
 *
 * Revision 1.2  2004/07/09 16:53:05  ibbo
 * rename table
 *
 */
package org.jzkit.search.provider.SRW;

import java.util.*;
import java.util.logging.Logger;
import org.jzkit.search.provider.iface.*;
import org.springframework.context.*;

/**
 * @author Ian Ibbotson
 */
public class SRWIRServiceFactory implements SearchServiceFactory {

  private Logger log = Logger.getLogger(SRWIRServiceFactory.class.getName());

  private String base_url;
  private String code;
  private Map record_archetypes = new HashMap();
  private ApplicationContext ctx;
  private String[] property_names = new String[] { "baseURL" };

  public SRWIRServiceFactory() {
  }

  public SRWIRServiceFactory(String base_url, String code) {
    this.base_url = base_url;
    this.code = code;
  }


  public Searchable newSearchable() {
    return newSearchable(null);
  }

  public Searchable newSearchable(ServiceUserInformation user_info) {
    SRWSearchable result = new SRWSearchable();
    result.setApplicationContext(ctx);

    log.fine("newSeachable, base_url="+base_url+",code="+code);

    result.setBaseURL(base_url);
    result.setCode(code);
    result.setRecordArchetypes(record_archetypes);

    return result;
  }

  public String getBaseURL() {
    return base_url;
  }

  public void setBaseURL(String base_url) {
    this.base_url = base_url;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Map getRecordArchetypes() {
    return record_archetypes;
  }

  public String[] getPropertyNames() {
    return property_names;
  }
                                                                                                                                                             
  public void setRecordArchetypes(Map record_archetypes) {
    this.record_archetypes = record_archetypes;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

}
