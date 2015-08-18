package com.k_int.sql.data_dictionary;

import java.util.Vector;
/**
 * Title:       ImportedKeyAttribute
 * @version:    $Id: ImportedKeyAttribute.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description:
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="210"
 */

public class ImportedKeyAttribute extends DatabaseLinkAttribute {

  public ImportedKeyAttribute() {
    this.datatype = AttributeDefinition.LINK_ATTR_TYPECODE;
  }

  public ImportedKeyAttribute(String attrname, String LinkedTable) {
    super (attrname, LinkedTable, AttributeDefinition.LINK_ATTR_TYPECODE);
  }

}
