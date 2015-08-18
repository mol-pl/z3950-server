package com.k_int.sql.qm_to_sql;

/**
 * Title: SimpleTextMapping
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: SimpleTextMapping.java,v 1.2 2004/11/21 13:02:31 ibbo Exp $
 *
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="102"
 */
public class FilterAttrMapping extends BaseMapping {

  public String value;
  public String relation = "=";

  private FilterAttrMapping() {
  }

  public FilterAttrMapping(String access_path, String value) {
    super(access_path);
    this.value=value;
  }

  public FilterAttrMapping(String access_path, String value, String relation) {
    super(access_path);
    this.value=value;
    this.relation=relation;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="FILTER_VALUE" length="256"
   */
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="RELATION" length="10"
   */
  public String getRelation() {
    return relation;
  }

  public void setRelation(String relation) {
    this.relation = relation;
  }

}
