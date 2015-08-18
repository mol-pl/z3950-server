package org.jzkit.search.provider.jdbc;


import java.util.*;

/**
 * Title:
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: Mapping.java,v 1.1 2004/10/27 14:42:42 ibbo Exp $
 * @hibernate.class  table="JZ_JDBC_TEMPLATE_MAPPING" dynamic-update="true" dynamic-insert="true" lazy="false"
 */
public class JDBCCollectionMappingInfo {

  private Long id; // Used only in database config mode

  private String entity_name = null;
  private Map record_spec_to_template_map = null;
  private Map record_archetypes_map = null;

  public JDBCCollectionMappingInfo() {
  }

  public JDBCCollectionMappingInfo(String entity_name,
                                   Map record_spec_to_template_map,
                                   Map record_archetypes_map) {
    this.entity_name = entity_name;
    this.record_spec_to_template_map = record_spec_to_template_map;
    this.record_archetypes_map = record_archetypes_map;
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
   * @hibernate.map cascade="all" lazy="false" table="JZ_JDBC_TMAPPING_FACTORY_LINK"
   * @hibernate.collection-key column="MAPPING_INFO_ID"
   * @hibernate.collection-index column="CONCRETE_SPEC" type="string" length="100"
   * @hibernate.collection-many-to-many column="JZ_JDBC_FRAGMENT_FACTORY" class="org.jzkit.search.provider.jdbc.TemplateFragmentFactory"
   */
  public void setRecordSpecToTemplateMap(Map record_spec_to_template_map) {
    this.record_spec_to_template_map = record_spec_to_template_map;
  }

  public Map getRecordSpecToTemplateMap() {
    return record_spec_to_template_map;
  }

  /**
   * @hibernate.map cascade="all" lazy="false" table="JZ_JDBC_TMAPPING_ARCHETYPE"
   * @hibernate.collection-key column="MAPPING_INFO_ID"
   * @hibernate.collection-index column="ARCHETYPE" type="string" length="40"
   * @hibernate.collection-element column="CONCRETE_SPEC" type="string" length="100"
   *
   * for example:  String to string map "F" to "xml:ENT:F" etc.] 
   * **DEPRECATED**
   *
   */
  public void setRecordArchetypesMap(Map record_spec_to_template_map) {
    this.record_archetypes_map = record_archetypes_map;
  }

  public Map getRecordArchetypesMap() {
    return record_archetypes_map;
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

}
