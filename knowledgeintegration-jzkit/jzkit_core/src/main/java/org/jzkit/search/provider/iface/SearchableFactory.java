package org.jzkit.search.provider.iface;

import java.util.*;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.*;

/**
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * Company:     Knowledge Integration Ltd.
 * License:     A license.txt file should is distributed along with this software
 *
 * @author Ian Ibbotson
 * @version $Id: SearchServiceFactory.java,v 1.5 2004/11/27 09:18:57 ibbo Exp $
 * @see org.jzkit.SearchProvider.iface.Searchable
 */ 
public class SearchableFactory implements ApplicationContextAware {

  private ApplicationContext ctx;
  private java.util.Map plugin_map = new java.util.HashMap();

  public static Log log = LogFactory.getLog(SearchableFactory.class);

  public SearchableFactory() {
  }

  public void init() {
    log.debug("SearchableFactory scanning for plugins....");
    java.util.Map backend_plugins = ctx.getBeansOfType(org.jzkit.search.provider.iface.JZKitPluginMetadata.class);
    if ( backend_plugins != null ) {
      for ( java.util.Iterator i = backend_plugins.entrySet().iterator(); i.hasNext(); ) {
        Map.Entry e = (Map.Entry) i.next();
        log.debug("Registered: "+e.getKey()+" = "+e.getValue());
        org.jzkit.search.provider.iface.JZKitPluginMetadata plugin_metadata = (org.jzkit.search.provider.iface.JZKitPluginMetadata) e.getValue();
        plugin_map.put(plugin_metadata.getPluginCode(),plugin_metadata.getPluginClassName());
        log.debug("setting "+plugin_metadata.getPluginCode()+"="+plugin_metadata.getPluginClassName());
      }
    }
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }


  public Searchable create(IRServiceDescriptor connection_descriptor) throws SearchException {
    System.err.println("Create service "+connection_descriptor);
    Searchable result = null;

    if ( ( connection_descriptor != null ) && ( connection_descriptor.getProtocol() != null ) ) {
      String class_name = (String) plugin_map.get(connection_descriptor.getProtocol());
      try {
        System.err.println("New instance of "+class_name);
        Class searchable_class = Class.forName(class_name);
        result = (Searchable) searchable_class.newInstance();
        result.setApplicationContext(ctx);
  
        for ( java.util.Iterator i = connection_descriptor.getPreferences().keySet().iterator(); i.hasNext(); ) {
          String property_name = (String)(i.next());
          Object property_value = connection_descriptor.getPreferences().get(property_name);
          System.err.println("Setting "+property_name+" to object of class "+property_value.getClass()+" value="+property_value);
          BeanUtils.setProperty(result, property_name, property_value);
        }

        // result.setRecordArchetypes(record_syntax_archetypes);
      }
      catch ( java.lang.ClassNotFoundException cnfe ) {
        throw new SearchException("Unable to locate searchable class: "+class_name, cnfe);
      }
      catch ( java.lang.InstantiationException ie ) {
        throw new SearchException("Probem creating new SearchServiceFactory - "+class_name+" ie",ie);
      }
      catch ( java.lang.IllegalAccessException iae ) {
        throw new SearchException("Probem creating new SearchServiceFactory",iae);
      }
      catch ( java.lang.reflect.InvocationTargetException ite ) {
        throw new SearchException("Probem creating new SearchServiceFactory",ite);
      }
    }

    return result;
  }

  public Searchable create(String connection_descriptor_string) throws SearchException {
    return create(new IRServiceDescriptor(connection_descriptor_string));
  }

}
