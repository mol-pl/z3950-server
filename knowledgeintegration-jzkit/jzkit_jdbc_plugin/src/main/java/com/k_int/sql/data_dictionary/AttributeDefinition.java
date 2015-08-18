package com.k_int.sql.data_dictionary;
import java.util.Vector;

/**
 * Title:       AttributeDefinition
 * @version:    $Id: AttributeDefinition.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Copyright:
 * @author:     Ian Ibbotson
 * Company:
 * Description:
 *
 * @hibernate.class  table="JZ_JDBC_ATTRDEF" dynamic-update="true" dynamic-insert="true" discriminator-value="0"
 * @hibernate.discriminator column="ATTR_TYPE" type="integer"
 */
public class AttributeDefinition
{
  public static final int LINK_ATTR_TYPECODE = 1;             // This entity:1 relationship to another table
  public static final int COLLECTION_ATTR_TYPECODE = 2;       // This entity:M relationshup to another table
  public static final int DB_COLUMN_ATTR_TYPECODE=3;          // A Geniune value of the table (query really)
  public static final int PSEUDO_COLUMN=4;                    // Pseudo column like rowid.

  // Internal typecode
  protected int datatype;
  private Long id;
  private String attrname;
  private Vector capabilities = new Vector();

  public AttributeDefinition() {
  }

  public AttributeDefinition(String attrname, int type) {
    datatype = type;
    this.attrname = attrname;
  }

  /**
   * @hibernate.id  generator-class="native" column="ID"
   */
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="ATTRIBUTE_NAME" length="128"
   */
  public String getAttributeName() {
    return attrname;
  }

  public void setAttributeName(String attrname) {
    this.attrname = attrname;
  }

  public int getType() {
    return datatype;
  }

  public void addCapability(String s)
  {
    capabilities.add(s);
  }

  public boolean isCapableOf(String s)
  {
    return capabilities.contains(s);
  }
}

