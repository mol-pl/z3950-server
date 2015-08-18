package com.k_int.sql.qm_to_sql;

/**
 *       Title: DatabaseMapping
 * Description: Map a collection  name to a table / tables + collection identifier column
 *     @author: Ian Ibbotson (ian.ibbotson@k-int.com)
 *    @version: $Id: DatabaseMapping.java,v 1.2 2004/10/31 12:21:22 ibbo Exp $
 *
 * This class is the target for a map from a public "database name" (In the sense of a z39.50 database name, or an
 * JZKit2 SRW Server /srw/database/ URL (or /srw/landscape)) on to an internal database table (entity)
 * and optionally collection attribute (Collection_name may be null).
 * Multiple external collection names can map onto a single internal entity. For example, in
 * iNode, the resource table has a collection attribute.. we often map as follows
 * <ul>
 *    <li>MyLocalResourcesCollName -> new DatabaseMapping("Resource", "LOCAL")<br/>
 *      Meaning please map the public database name MyLocalResourcesCollName onto the Resources Table and use the LOCAL
 *      collection identifier.
 *    <li>AllResources -> new DatabaseMapping("Resource")<br>
 *      Means all resources.
 * </ul>
 *
 * @hibernate.class  table="JZ_JDBC_DATABASE_MAPPING" dynmic-update="true" dynamic-insert="true"
 */
public class DatabaseMapping {
  
  private Long id; // Only used when in database config mode.

  public String public_name;
  public String entity_name;
  public String collection_name;

  public DatabaseMapping() {
  }

  public DatabaseMapping(String public_name, String entity_name, String collection_name) {
    this.entity_name = entity_name;
    this.collection_name = collection_name;
    this.public_name = public_name;
  }

  public DatabaseMapping(String public_name, String entity_name) {
    this.public_name = public_name;
    this.entity_name = entity_name;
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
   * @hibernate.column name="PUBLIC_NAME" length="128" unique="true" not-null="true" unique-key="DB_MAP_PUB_NAME"
   */
  public String getPublicName() {
    return public_name;
  }

  public void setPublicName(String public_name) {
    this.public_name = public_name;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="ENTITY_NAME" length="128"
   */
  public String getEntityName() {
    return entity_name;
  }

  public void setEntityName(String entity_name) {
    this.entity_name = entity_name;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="COLLECTION_NAME" length="128"
   */
  public String getCollectionName() {
    return collection_name;
  }

  public void setCollectionName(String collection_name) {
    this.collection_name = collection_name;
  }

}
