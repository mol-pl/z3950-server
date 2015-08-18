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
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="108"
 */
public class JoinFilter extends BaseMapping {

  public String target_path;
  public String relation="=";

  private JoinFilter() {
  }

  public JoinFilter(String access_path, String target_path) {
    super(access_path);
    this.target_path=target_path;
  }

  public JoinFilter(String access_path, String target_path, String relation) {
    super(access_path);
    this.target_path=target_path;
    this.relation=relation;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="TARGET_PATH" length="256"
   */
  public String getTargetPath() {
    return target_path;
  }

  public void setTargetPath(String target_path) {
    this.target_path = target_path;
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
