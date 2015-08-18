package com.k_int.sql.qm_to_sql;

/**
 * Title: SimpleTextMapping
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Chris Kilgour
 * @version:        
 *
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="102"
 */
public class SubQueryFilter extends BaseMapping {

  public String query;
  public String relation = "in";

  private SubQueryFilter() {
  }

  public SubQueryFilter(String access_path, String query) {
    super(access_path);
    this.query=query;
  }

  public SubQueryFilter(String access_path, String query, String relation) {
    super(access_path);
    this.query=query;
    this.relation=relation;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="FILTER_VALUE" length="256"
   */
  public String getQuery() {
    return query;
  }

  public void setQuery(String value) {
    this.query = value;
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
