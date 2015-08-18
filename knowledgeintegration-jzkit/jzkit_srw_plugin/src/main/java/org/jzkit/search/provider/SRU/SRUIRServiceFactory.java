/**
 * Title:       SRUIRServiceDescription
 * @version:    $Id: SRUIRServiceFactory.java,v 1.1 2005/10/30 13:42:40 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

/*
 * $Log: SRUIRServiceFactory.java,v $
 * Revision 1.1  2005/10/30 13:42:40  ibbo
 * updated - added new SRU classes to work around some CQL issues, etc
 *
 *
 */
package org.jzkit.search.provider.SRU;

import java.util.*;
import java.util.logging.Logger;
import org.jzkit.search.provider.iface.*;
import org.springframework.context.*;

/**
 * @author Ian Ibbotson
 */
public class SRUIRServiceFactory implements SearchServiceFactory {

  private Logger log = Logger.getLogger(SRUIRServiceFactory.class.getName());

  private String base_url;
  private String code;
  private Map record_archetypes = new HashMap();
  private ApplicationContext ctx;
  private String[] property_names = new String[] { "baseURL" };

  public SRUIRServiceFactory() {
  }

  public SRUIRServiceFactory(String base_url, String code) {
    this.base_url = base_url;
    this.code = code;
  }


  public Searchable newSearchable() {
    return newSearchable(null);
  }

  public Searchable newSearchable(ServiceUserInformation user_info) {
    SRUSearchable result = new SRUSearchable();
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
