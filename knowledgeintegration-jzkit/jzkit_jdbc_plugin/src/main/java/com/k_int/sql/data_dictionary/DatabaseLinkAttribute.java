package com.k_int.sql.data_dictionary;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Title:       DatabaseLinkAttribute
 * @version:    $Id: DatabaseLinkAttribute.java,v 1.2 2004/10/28 09:54:25 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description:
 *
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="200"
 */

public class DatabaseLinkAttribute extends AttributeDefinition
{
  String reference_to;
  List key_pairs = new ArrayList();

  /** The <code>Log</code> instance for this application.  */

  public DatabaseLinkAttribute() {
  }

  public DatabaseLinkAttribute(String attrname, String LinkedTable, int type) {
    super (attrname, type);
    reference_to = LinkedTable;
  }

  public void addKeyPair(String local_attr_name,
                         String related_attr_name,
                         int seq) {
    key_pairs.add(new CorrespondingKeyPair(local_attr_name,related_attr_name));
  }

  public Iterator getCorrespondingKeyPairs() {
    return key_pairs.iterator();
  }

  /**
   * @hibernate.property
   * @hibernate.column name="RELATED_ENTITY_NAME" length="128"
   */
  public String getRelatedEntityName() {
    return reference_to;
  }

  public void setRelatedEntityName(String reference_to) {
    this.reference_to = reference_to;
  }

  // Needs to be in map file, with a VO for key pair
  public List getKeyPairs() {
    return key_pairs;
  }

  public void setKeyPairs(List key_pairs) {
    this.key_pairs = key_pairs;
  }
}
