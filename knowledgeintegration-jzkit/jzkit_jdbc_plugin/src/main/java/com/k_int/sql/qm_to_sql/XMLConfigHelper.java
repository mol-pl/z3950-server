package com.k_int.sql.qm_to_sql;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.beans.*;
import java.net.URL;
import java.io.BufferedInputStream;

/**
 * XMLConfigHelper.
 * @version $Id: XMLConfigHelper.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * @Author Ian Ibbotson (ian.ibbotson@sun.com)
 */
public class XMLConfigHelper
{
  public static QMToSQLConfig getConfig(String resource_path)
  {
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
}
