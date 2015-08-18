package com.k_int.sql.data_dictionary;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.beans.*;
import java.net.URL;
import java.io.BufferedInputStream;

public class XMLConfigHelper
{
  public static Dictionary getConfig(String resource_path)
  {
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
