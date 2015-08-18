/**
 * Title:       SRWIRServiceDescription
 * @version:    $Id: JDBCSearchServiceFactory.java,v 1.9 2004/11/27 09:18:57 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

/*
 * $Log: JDBCSearchServiceFactory.java,v $
 * Revision 1.9  2004/11/27 09:18:57  ibbo
 * Updated to make app context available more widely
 *
 * Revision 1.8  2004/10/29 16:39:23  ibbo
 * Added MySQL Match Against Functionality for free text searching
 *
 * Revision 1.7  2004/10/27 14:41:20  ibbo
 * XML record export working
 *
 * Revision 1.6  2004/10/26 16:42:23  ibbo
 *
 * Updated
 *
 * Revision 1.5  2004/10/26 15:30:52  ibbo
 * Updated
 *
 * Revision 1.4  2004/10/26 11:28:38  ibbo
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
 *
 */
package org.jzkit.search.provider.jdbc;

import java.util.*;
import java.util.logging.Logger;
import org.jzkit.search.provider.iface.*;
import org.springframework.context.*;
import javax.sql.*;

/**
 * @author Ian Ibbotson
 */
public class JDBCSearchServiceFactory implements SearchServiceFactory {

  private Logger log = Logger.getLogger(JDBCSearchServiceFactory.class.getName());

  private String persistence_defs;
  private String access_point_defs;
  private String datasource_name;
  private String templates_name;
  private Map record_archetypes = new HashMap();
  private ApplicationContext app_context;

  public JDBCSearchServiceFactory() {
  }

  public JDBCSearchServiceFactory(ApplicationContext app_context,
                                  String persistence_defs, 
                                  String access_point_defs, 
                                  String datasource_name,
                                  String templates_name) {
    this.persistence_defs = persistence_defs;
    this.access_point_defs = access_point_defs;
    this.datasource_name = datasource_name;
    this.templates_name = templates_name;
    this.app_context = app_context;
  }


  public Searchable newSearchable() {
    return newSearchable(null);
  }

  public Searchable newSearchable(ServiceUserInformation user_info) {
    JDBCSearchable result = new JDBCSearchable();

    log.fine("newJDBCSeachable");
    result.setApplicationContext(app_context);
    result.setRecordArchetypes(record_archetypes);
    result.setDatasourceName(datasource_name);
    result.setDictionaryName(persistence_defs);
    result.setAccessPathsConfigName(access_point_defs);
    result.setTemplatesConfigName(templates_name);

    return result;
  }

  public String getPersistenceDefinitions() {
    return persistence_defs;
  }

  public void setPersistenceDefinitions(String persistence_defs) {
    this.persistence_defs = persistence_defs;
  }

  public String getAccessPointDefinitions() {
    return access_point_defs;
  }

  public void setAccessPointDefinitions(String access_point_defs) {
    this.access_point_defs = access_point_defs;
  }

  public String getDatasourceId() {
    return datasource_name;
  }

  public void setDatasourceId(String datasource_name) {
    this.datasource_name = datasource_name;
  }

  public Map getRecordArchetypes() {
    return record_archetypes;
  }

  public void setRecordArchetypes(Map record_archetypes) {
    this.record_archetypes = record_archetypes;
  }

  public void setTemplatesName(String templates_name) {
    this.templates_name = templates_name;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.app_context = ctx;
  }
}
