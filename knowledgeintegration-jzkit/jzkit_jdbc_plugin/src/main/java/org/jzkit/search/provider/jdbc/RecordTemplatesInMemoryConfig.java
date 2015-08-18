package org.jzkit.search.provider.jdbc;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;
import java.beans.*;
import java.net.URL;
import java.io.BufferedInputStream;

import java.util.*;

/**
 * Title:
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: RecordTemplatesInMemoryConfig.java,v 1.2 2004/10/28 12:31:41 ibbo Exp $
 */
public class RecordTemplatesInMemoryConfig implements RecordTemplatesConfig {

  public Map entity_template_map = null;

  public RecordTemplatesInMemoryConfig() {
  }

  public RecordTemplatesInMemoryConfig(Map entity_template_map) {
    this.entity_template_map = entity_template_map;
  }

  public JDBCCollectionMappingInfo lookupEntityTemplateMappingInfo(String entity_name) {
    return (JDBCCollectionMappingInfo) entity_template_map.get(entity_name);
  }

  public static RecordTemplatesConfig getConfig(String resource_path) {
    RecordTemplatesInMemoryConfig result = null;
                                                                                                                                          
    try {
      URL view_definition = RecordTemplatesInMemoryConfig.class.getResource(resource_path);
      XMLDecoder d = new XMLDecoder( new BufferedInputStream( view_definition.openStream() ) );
      Object o = d.readObject();
      // System.err.println("Instanceof "+o.getClass().getName());
      result = (RecordTemplatesInMemoryConfig) o;
      d.close();
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }
    return result;
  }

  public Iterator getTemplates() {
    return entity_template_map.values().iterator();
  }
}
