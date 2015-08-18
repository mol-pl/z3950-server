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
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="105"
 */
public class SimpleTextMapping extends BaseMapping {

  // 0=Nothing, 1=Norm Case 1 (Lowercase, trim, etc)
  public int pre_search_action = 0;

  private SimpleTextMapping() {
  }

  public SimpleTextMapping(String access_path) {
    super(access_path);
  }

  public SimpleTextMapping(String access_path, int pre_search_action) {
    super(access_path);
    this.pre_search_action = pre_search_action;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="SIMPLE_TXT_PRE_SEARCH_ACTION"
   */
  public int getPreSearchAction() {
    return pre_search_action;
  }

  public void setPreSearchAction(int pre_search_action) {
    this.pre_search_action = pre_search_action;
  }
}
