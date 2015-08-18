package com.k_int.sql.data_dictionary;
/**
 * Title:       EntityKey
 * @version:    $Id: EntityKey.java,v 1.4 2004/10/26 15:30:52 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description:
 */

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.sql.*;
import java.io.StringWriter;
import java.io.IOException;
import org.apache.commons.logging.*;


public class EntityKey implements java.io.Serializable {

  private java.util.Map key_components = new java.util.HashMap();
  protected static Log log = LogFactory.getLog(EntityKey.class);

  public EntityKey(EntityTemplate et, Object o ) {
    if ( o.getClass().isArray() ) {
      int counter = 0;
      Object[] keys = (Object[]) o;
      for ( Iterator e = et.getKeyAttrs(); e.hasNext() ;) {
        String key_attr_name = (String)e.next();
        key_components.put(key_attr_name,keys[counter++]);
      }
    }
    else {
      String key_col = (String) et.getKeyAttrs().next();
      key_components.put(key_col, o);
    }
  }

  public EntityKey(EntityTemplate et, ResultSet rs) {
    try {
      // Get the primary key attributes from the EntityTemplate
      for ( Iterator e = et.getKeyAttrs(); e.hasNext() ;) {
        // For each attribute name, extract the value from the result set row
        String key_attr_name = (String)e.next();
        DatabaseColAttribute dca = (DatabaseColAttribute) et.getAttributeDefinition(key_attr_name);
        int colpos = rs.findColumn(dca.getColName());
        Object o = null;

        switch( rs.getMetaData().getColumnType(colpos)) {
          case 2:
            o = new Integer(rs.getInt(colpos));
            break;
          case java.sql.Types.VARCHAR:
            o = rs.getString(colpos);
            break;
          default:
            o = rs.getObject(colpos);
            break;
        }

        key_components.put(key_attr_name, o);
      }
    }
    catch ( SQLException sqle ) {
      sqle.printStackTrace();
    }
    catch ( NullPointerException npe ) {
      npe.printStackTrace();
    }
    catch ( com.k_int.sql.data_dictionary.UnknownAccessPointException uape ) {
      uape.printStackTrace();
    }
  }

  public EntityKey(Entity ent) throws UnknownAccessPointException {
    EntityTemplate et = ent.getTemplate();

    // Get the primary key attributes from the EntityTemplate
    for ( Iterator e = et.getKeyAttrs(); e.hasNext() ;) {
      String key_attr_name = (String)e.next();
      key_components.put(key_attr_name, ent.get(key_attr_name));
    }
  }

  public EntityKey() {
  }

  public String toString() {
    StringWriter sw = new StringWriter();

    try {
      for ( Iterator e = getAttrNames(); e.hasNext() ;) {
        String attrname = (String)e.next();
        Object attrval = key_components.get(attrname);

          sw.write(attrname);
          sw.write("='");
          sw.write(""+attrval);
          sw.write("'");

          if ( e.hasNext() )
            sw.write(",");
      }
    }
    catch ( Exception e ) {
      log.warn("Unable to assemble key string",e);
      e.printStackTrace();
    }

    return sw.toString();
  }

  public java.util.Map getKeyMap() {
    return key_components;
  }

  public void addKeyComponent(String attrname, Object attrval) {
    key_components.put(attrname, attrval);
  }

  public int numComponents() {
    return key_components.size();
  }

  public Iterator getAttrNames() {
    return key_components.keySet().iterator();
  }

  public Object getAttrValue(String attrname) {
    return key_components.get(attrname);
  }
}
