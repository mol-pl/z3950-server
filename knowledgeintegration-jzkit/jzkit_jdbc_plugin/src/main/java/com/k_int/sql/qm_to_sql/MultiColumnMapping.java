package com.k_int.sql.qm_to_sql;

import java.util.*;

/**
 * Title: MultiColumnMapping
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id$
 *
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="107"
 */
public class MultiColumnMapping extends BaseMapping {

  List child_mappings = new ArrayList();

  public MultiColumnMapping() {
  }

  public MultiColumnMapping(BaseMapping[] child_mappings) {
    System.err.println("New multi column mapping of size "+child_mappings.length);
    for ( int i=0; i<child_mappings.length;i++) {
      this.child_mappings.add(child_mappings[i]);
    }
  }

  public BaseMapping[] getMappingsArray() {
    return (BaseMapping[]) child_mappings.toArray(new BaseMapping[0]);
  }

  /**
   * @hibernate.list cascade="all" lazy="false"
   * @hibernate.collection-key column="PARENT_MAPPING_ID"
   * @hibernate.collection-index column="PARENT_MAPPING_IDX"
   * @hibernate.collection-one-to-many class="com.k_int.sql.qm_to_sql.BaseMapping"
   */
  public List getChildMappings() {
    return child_mappings;
  }

  public void setChildMappings(List child_mappings) {
    this.child_mappings = child_mappings;
  }
}
