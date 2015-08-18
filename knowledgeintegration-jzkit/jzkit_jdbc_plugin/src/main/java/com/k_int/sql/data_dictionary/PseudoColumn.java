package com.k_int.sql.data_dictionary;

import java.sql.*;

/**
 * Title:       PseudoColumn
 * @version:    $Id: PseudoColumn.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description:
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="300"
 *
 */
public class PseudoColumn extends AttributeDefinition {

  private String col_name = null;

  public PseudoColumn() {
    this.datatype = PSEUDO_COLUMN;
  }

  public PseudoColumn(String attrname, String col_name) {
    super(attrname, 4);
    this.col_name = col_name;
  }

  public PseudoColumn(String col_name) {
    super(col_name, 4);
    this.col_name = col_name;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="PSEUDO_COL_NAME" length="128"
   */
  public String getColName() {
    return col_name;
  }

  public void setColName(String col_name) {
    this.col_name = col_name;
  }
}
