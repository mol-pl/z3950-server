package com.k_int.sql.qm_to_sql;

import java.util.*;
import java.beans.*;
import java.net.URL;
import java.io.BufferedInputStream;
import org.springframework.context.*;

/**
 * QMToSQLInMemoryConfig
 * Title:
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: QMToSQLInMemoryConfig.java,v 1.5 2004/10/31 12:21:22 ibbo Exp $
 */
public class QMToSQLInMemoryConfig implements QMToSQLConfig, org.springframework.context.ApplicationContextAware {

  private org.springframework.context.ApplicationContext ctx;

  public Map collections = new HashMap();
  // public String[] default_collections;
  public Map database_names_map = new HashMap();

  // public void setDefault(String[] default_collections) {
  //   this.default_collections = default_collections;
  // }

  public void registerDatabaseMap(Map database_names_map) {
    this.database_names_map = database_names_map;
  }

  public DatabaseMapping lookupDatabaseMapping(String public_collection_id) {
    return (DatabaseMapping) database_names_map.get(public_collection_id);
  }

  public void registerAttrMap(String collection_id, AttrMap map) {
    collections.put(collection_id, map);
  }

  public Iterator getEntityAttrMaps() {
    return collections.values().iterator();
  }

  public Iterator getDatabaseMappings() {
    return database_names_map.values().iterator();
  }

  public AttrMap lookupAttrMap(String collection_id) {
    return (AttrMap) collections.get(collection_id);
  }

  public static QMToSQLConfig getConfig(String resource_path) {
    QMToSQLConfig result = null;
                                                                                                                                          
    // This fragment uses XML Serialization to try and create a new
    try
    {
      // Use the config object to resolve the layout_id into a resource
      // URL view_definition = ClassLoader.getSystemResource(resource_path);
      URL view_definition = XMLConfigHelper.class.getResource(resource_path);
      // XMLDecoder d = new XMLDecoder( new BufferedInputStream( view_definition.openStream() ), this );
      // Don't need a context object ( I hope )
      XMLDecoder d = new XMLDecoder( new BufferedInputStream( view_definition.openStream() ) );
      result = (QMToSQLConfig) d.readObject();
      d.close();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
                                                                                                                                          
    return result;
  }

  public void addDatabaseMapping(String public_collection_name, 
                                 String base_entity_name, 
                                 String collection_code) {
    DatabaseMapping dm = new DatabaseMapping(public_collection_name,base_entity_name,collection_code);
    database_names_map.put(public_collection_name, dm);
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

}
