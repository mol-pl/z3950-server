package com.k_int.sql.data_dictionary;

/**
 * Title:       CorrespondingKeyPair
 * @version:    $Id: CorrespondingKeyPair.java,v 1.2 2004/10/28 09:54:25 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description: Records the relationship between a tables foreign keys and a related table
 *
 */
public class CorrespondingKeyPair
{
  private String local_attr_name = null;
  private String related_attr_name = null;

  public CorrespondingKeyPair() {
  }

  public CorrespondingKeyPair(String local_attr_name, String related_attr_name) {
    this.local_attr_name = local_attr_name;
    this.related_attr_name = related_attr_name;
  }

  public String getLocalAttrName() {
    return local_attr_name;
  }

  public void setLocalAttrName(String local_attr_name) {
    this.local_attr_name = local_attr_name;
  }

  public String getRelatedAttrName() {
    return related_attr_name;
  }

  public void setRelatedAttrName(String related_attr_name) {
    this.related_attr_name = related_attr_name;
  }

  public String toString() {
    return "CorrespondingKeyPair "+local_attr_name+"="+related_attr_name;
  }
}
