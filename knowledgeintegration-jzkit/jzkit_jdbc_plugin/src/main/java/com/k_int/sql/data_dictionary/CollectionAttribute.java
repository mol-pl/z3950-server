package com.k_int.sql.data_dictionary;

/**
 * Title:       CollectionAttribute
 * @version:    $Id: CollectionAttribute.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description:
 *
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="220"
 */
public class CollectionAttribute extends DatabaseLinkAttribute
{
  public CollectionAttribute() {
    this.datatype = AttributeDefinition.COLLECTION_ATTR_TYPECODE;
  }

  public CollectionAttribute(String attrname, String LinkedTable)
  {
    super (attrname, LinkedTable, AttributeDefinition.COLLECTION_ATTR_TYPECODE);
  }
}
