package com.k_int.sql.data_dictionary;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.beans.*;
import java.net.URL;
import java.io.BufferedInputStream;


public class InMemoryConfig implements Dictionary {

  private Map entity_defs = new HashMap();
  private String dictionary_name;

  public InMemoryConfig() {
    // System.err.println("Created new InMemoryConfig");
  }

  public String getDictionaryName() {
    return dictionary_name;
  }

  public void setDictionaryName(String d) {
    // System.err.println("setDictionaryName "+d);
    this.dictionary_name = d;
  }

  public Iterator getAllDefs() {
    return entity_defs.values().iterator();
  }

  public EntityTemplate lookup(String entity_name) throws UnknownCollectionException {
    EntityTemplate et = (EntityTemplate)entity_defs.get(entity_name);
    if ( et == null )
      throw new UnknownCollectionException("Unknown collection looking up "+entity_name);
    return et;
  }

  public void addEntity(EntityTemplate t) {
    // System.err.println("addEntity "+t);

    entity_defs.put(t.getEntityName(), t);
  }

  public void addEntity(String t) {
    // System.err.println("addEntity"+t);
  }

  public static Dictionary getConfig(String resource_path) {
    Dictionary result = null;
                                                                                                                                          
    // This fragment uses XML Serialization to try and create a new
    try
    {
      // Use the config object to resolve the layout_id into a resource
      // URL view_definition = ClassLoader.getSystemResource(resource_path);
      URL view_definition = XMLConfigHelper.class.getResource(resource_path);
      // XMLDecoder d = new XMLDecoder( new BufferedInputStream( view_definition.openStream() ), this );
      // Don't need a context object ( I hope )
                                                                                                                                          
      XMLDecoder d = new XMLDecoder( new BufferedInputStream( view_definition.openStream() ) );
      result = (Dictionary) d.readObject();
      d.close();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
                                                                                                                                          
    return result;
  }

}
