package org.jzkit.search.provider.jdbc;

import java.util.*;

public class AttrInfo {

  public String attr_name;
  public String element_name;
  public EntityMapping child_mapping;

  public AttrInfo() {
  }

  public AttrInfo(String attr_name, String element_name) {
    this.attr_name=attr_name;
    this.element_name=element_name;
  }

  public AttrInfo(String attr_name, String element_name, EntityMapping child_mapping) {
    this.attr_name=attr_name;
    this.element_name=element_name;
    this.child_mapping=child_mapping;
  }

  public String getAttrName() {
    return attr_name;
  }

  public void setAttrName(String attr_name) {
    this.attr_name = attr_name;
  }

  public String getElementName() {
    return element_name;
  }

  public void setElementName(String element_name) {
    this.element_name = element_name;
  }

  public EntityMapping getChildMapping() {
    return child_mapping;
  }

  public void setChildMapping(EntityMapping child_mapping) {
    this.child_mapping = child_mapping;
  }

}
