package com.k_int.sql.qm_to_sql;

import java.util.*;

/**
 * The per "collection" (Person, Case, etc) mapping for short-form access point names
 * to long-form information that describe what SQL should be generated given an instance
 * of that particular access point in the query model. <br.>
 * For example, for the Resource entity in inode, we map dc:title onto the Title attribute.
 *
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: AttrMap.java,v 1.3 2004/10/31 12:21:22 ibbo Exp $
 * @hibernate.class  table="JZ_JDBC_ENTITY_ATTR_MAP" dynmic-update="true" dynamic-insert="true"
 */
public class AttrMap {

  private Long id; // Used only for database config.

  protected Map mappings = new HashMap();
  protected String base_entity_name;
  protected String collection_attr_path;
  protected List filter_conditions = new ArrayList();

  public AttrMap() {
  }

  /**
   * Constructor : New AttrMap for the identified base table.
   * @param base_entity_name name of the base database this map relates to.
   */
  public AttrMap(String base_entity_name) {
    this.base_entity_name = base_entity_name;
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
   * Look up the mapping information for the given access point.
   * @param id Id of the access point we are looking up, e.g. "Surname"
   * @return the associated AttrMapping or null if none found.
   */
  public AttrMapping[] lookupAttr(String id) {
    AttrMapping[] result = null;
    BaseMapping m = (BaseMapping) mappings.get(id);

    if ( m != null ) {
      if ( m instanceof MultiColumnMapping ) {
        result = (AttrMapping[]) ((MultiColumnMapping)m).getMappingsArray();
      }
      else {
        result = new AttrMapping[] {m};
      }
    }

    return result;
  }

  /**
   * Add the specified mapping for the given ID.
   * @param id Access Point this mapping relates to
   * @param m The mapping information (Database access path, mapping type, etc)
   */
  public void addMapping(String id, BaseMapping m) {
    mappings.put(id, m);
  }

  /**
   * Add the specified mapping for the given ID.
   * @param id Access Point this mapping relates to
   * @param m The mapping information (Database access path, mapping type, etc)
   */
  public void addMapping(String id, BaseMapping[] m) {
    mappings.put(id, new MultiColumnMapping(m));
  }

  /**
   * get the base table this mapping refers to 
   * @hibernate.property
   * @hibernate.column name="BASE_ENTITY_NAME" length="128"
   */
  public String getBaseEntityName() {
    return base_entity_name;
  }

  public void setBaseEntityName(String base_entity_name) {
    this.base_entity_name = base_entity_name;
  }

  /**
   * The database access path to the value column holding a collection name for this entity. Optional.
   * @hibernate.property
   * @hibernate.column name="COLLECTION_ATTR_PATH" length="128"
   */
  public String getCollectionAttribute() {
    return collection_attr_path;
  }

  public void setCollectionAttribute(String collection_attr_path) {
    this.collection_attr_path = collection_attr_path;
  }

  public void addFilterCondition(AttrMapping m) {
    filter_conditions.add(m);
  }

  public List getFilterConditions() {
    return filter_conditions;
  }

  /**
   * @hibernate.map cascade="all" lazy="false" table="JZ_JDBC_ATTR_MAP_ENTRY"
   * @hibernate.collection-key column="JZ_DB_ATTR_MAP_ID"
   * @hibernate.collection-index column="ACCESS_POINT" type="string" length="128"
   * @hibernate.collection-many-to-many column="ATTR_MAP" class="com.k_int.sql.qm_to_sql.BaseMapping"
   */
  public Map getMappings() {
    return mappings;
  }

  public void setMappings(Map mappings) {
    this.mappings = mappings;
  }
}
