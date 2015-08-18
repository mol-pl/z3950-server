package com.k_int.sql.data_dictionary;

import java.util.Properties;
/**
 * Title:       DatabaseColAttribute
 * @version:    $Id: DatabaseColAttribute.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description: 
 *
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="100"
 */
public class DatabaseColAttribute extends AttributeDefinition
{
  String attr_col_name = null;
  int attr_sql_typecode=0;       // From java.sql.types
  String attr_udt_classname;     // For complex types
  int attr_max_len=0;
  int attr_nullable=0;
  Properties attr_props = null;

  public DatabaseColAttribute() {
    this.datatype=DB_COLUMN_ATTR_TYPECODE;
  }

  public DatabaseColAttribute(String attrname,
                              String col_name,
                              int typecode,
                              int max_len,
                              int nullable) {
    super (attrname, 3);
    // System.err.println("New DatabaseColAttr");
    attr_col_name = col_name;
    attr_sql_typecode = typecode;
    attr_max_len = max_len;
    attr_nullable = nullable;
  }

  public DatabaseColAttribute(String attrname,
                              String col_name, 
                              int typecode, 
                              String udt_classname, 
                              int max_len, 
                              int nullable, 
                              Properties attr_props) {
    super (attrname, 3);
    // System.err.println("New DatabaseColAttr 2");
    attr_col_name = col_name;
    attr_sql_typecode = typecode;
    attr_udt_classname = udt_classname;
    attr_max_len = max_len;
    attr_nullable = nullable;
    this.attr_props = attr_props;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="COL_ATTR_NAME" length="128"
   */
  public String getColName() {
    return attr_col_name;
  }

  public void setColName(String attr_col_name) {
    this.attr_col_name = attr_col_name;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="COL_ATTR_TYPECODE"
   */
  public int getSQLTypeCode() {
    return attr_sql_typecode;
  }

  public void setSQLTypeCode(int attr_sql_typecode) {
    this.attr_sql_typecode = attr_sql_typecode;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="COL_ATTR_UDTCLASS" length="128"
   */
  public String getUDTClassName() {
    return attr_udt_classname;
  }

  public void setUDTClassName(String attr_udt_classname) {
    this.attr_udt_classname = attr_udt_classname;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="COL_ATTR_MAXLEN"
   */
  public int getMaxLen() {
    return attr_max_len;
  }

  public void setMaxLen(int attr_max_len) {
    this.attr_max_len = attr_max_len;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="COL_ATTR_NULLABLE"
   */
  public int getNullable() {
    return attr_nullable;
  }

  public void setNullable(int attr_nullable) {
    this.attr_nullable = attr_nullable;
  }

  public Properties getAttrProps() {
    return attr_props;
  }
}
