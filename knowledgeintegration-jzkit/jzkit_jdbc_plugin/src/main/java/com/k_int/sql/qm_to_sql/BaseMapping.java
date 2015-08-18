package com.k_int.sql.qm_to_sql;


/**
 * BaseMapping
 *       Title: BaseMapping
 * Description: Base class for mappings..
 *     @author: Ian Ibbotson (ian.ibbotson@sun.com)
 *    @version: $Id: BaseMapping.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * @hibernate.class  table="JZ_JDBC_QMTOSQL_ATTR_MAPPING" dynamic-update="true" dynamic-insert="true" discriminator-value="0" lazy="false"
 * @hibernate.discriminator column="MAPPING_TYPE" type="integer"
 */
public class BaseMapping implements AttrMapping {

  public Long id; // Only used in DB Config mode
  public String access_path;

  public BaseMapping() {
  }

  public BaseMapping(String access_path) {
    this.access_path=access_path;
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
   * The database access path to the value column holding a collection name for this entity. Optional.
   * @hibernate.property
   * @hibernate.column name="ACCESS_PATH" length="128"
   */
  public String getAccessPath() {
    return access_path;
  }

  public void setAccessPath(String access_path) {
    this.access_path = access_path;
  }
}
