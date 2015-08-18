package com.k_int.sql.data_dictionary;

import java.util.Map;
import java.util.HashMap;

/**
 * Title:       AccessPathComponent
 * @version:    $Id: AccessPathComponent.java,v 1.3 2005/03/08 19:25:44 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description: A component in an access path such as the "A" in "A.B.C.D.E"
 */
public class AccessPathComponent
{
  public EntityTemplate entity_metadata=null;
  public AttributeDefinition component_value=null;
  public boolean reuse_context=true;
  public int join_type = 1;  // 1=Natural, 2=Left outer
  public Map additional_constraints = new HashMap();

  private AccessPathComponent() {
  }

  public AccessPathComponent(EntityTemplate entity_metadata,
                             AttributeDefinition component_value, 
                             boolean reuse_context,
                             int join_type) {
    this.entity_metadata=entity_metadata;
    this.component_value=component_value;
    this.reuse_context=reuse_context;
    this.join_type=join_type;
  }

  public AccessPathComponent(EntityTemplate entity_metadata,
                             AttributeDefinition component_value, 
                             boolean reuse_context) {
    this(entity_metadata,component_value,reuse_context,1);
  }

  public AccessPathComponent(AttributeDefinition component_value) {
    this.component_value=component_value;
  }


  public AttributeDefinition getAttributeMetadata() {
    return component_value;
  }

  public boolean getReuseContext() {
    return reuse_context;
  }

  public EntityTemplate getEntityMetadata() {
    return entity_metadata;
  }

  public int getJoinType() {
    return join_type;
  }

  public void addConstraint(String name, String value) {
    additional_constraints.put(name,value);
  }

  public Map getConstraints() {
    return additional_constraints;
  }
}
