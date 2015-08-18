/*
 * $Log: SQIIRServiceFactory.java,v $
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

import org.jzkit.search.provider.iface.*;
import org.springframework.context.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Title:       SQIIRServiceDescription
 * @version:    $Id: SQIIRServiceFactory.java,v 1.3 2005/06/21 10:53:08 ibbo Exp $
 * Copyright:   Copyright (C) 1999-2005 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

public class SQIIRServiceFactory implements SearchServiceFactory {

  private String base_url;
  private Map record_archetypes = new HashMap();
  private ApplicationContext ctx;
  private static Log log = LogFactory.getLog(SQIIRServiceFactory.class);

  public SQIIRServiceFactory() {
  }

  public SQIIRServiceFactory(String base_url) {
    this.base_url = base_url;
  }


  public Searchable newSearchable() {
    return newSearchable(null);
  }

  public Searchable newSearchable(ServiceUserInformation user_info) {
    SQISearchable result = new SQISearchable();
    result.setApplicationContext(ctx);
    result.setRecordArchetypes(record_archetypes);
    result.setBaseURL(base_url);
    return result;
  }

  public Map getRecordArchetypes() {
    return record_archetypes;
  }
                                                                                                                                                             
  public void setRecordArchetypes(Map record_archetypes) {
    this.record_archetypes = record_archetypes;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

}
