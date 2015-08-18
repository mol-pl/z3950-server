package org.jzkit.search.provider.jdbc;


import java.util.*;

/**
 * Title:
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: EntityMapping.java,v 1.2 2004/10/28 12:31:41 ibbo Exp $
 *
 * @hibernate.class  table="JZ_JDBC_ENTITY_MAPPING" dynamic-update="true" dynamic-insert="true" lazy="false"
 *
 */
public class EntityMapping implements Mapping {

  private Long id;  // Only used in JDBC config mode

  private String element_name;
  private List child_elements = new ArrayList();

  public EntityMapping() {
  }

  public EntityMapping(String element_name_for_entity) {
    this.element_name = element_name_for_entity;
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

  public void addAttribute(String attr_name, String element_name) {
    child_elements.add(new AttrInfo(attr_name, element_name));
  }

  public void addAttribute(String attr_name, String element_name, EntityMapping child_mapping) {
    child_elements.add(new AttrInfo(attr_name, element_name, child_mapping));
  }
                       
 
  /**
   * @hibernate.property
   * @hibernate.column name="ELEMENT_NAME" length="128"
   */
  public String getElementName() {
    return element_name;
  }

  public void setElementName(String element_name) {
    this.element_name = element_name;
  }

  public List getChildElements() {
    return child_elements;
  }

  public void setChildElements(List child_elements) {
    this.child_elements = child_elements;
  }
}
