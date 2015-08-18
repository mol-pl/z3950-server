package com.k_int.sql.qm_to_sql;

/**
 * FreeTextMapping
 * Title: Mapping
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: FreeTextMapping.java,v 1.2 2005/10/06 08:30:15 ibbo Exp $
 *
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="103"
 */
public class FreeTextMapping extends BaseMapping {

  private FreeTextMapping() {
  }

  public FreeTextMapping(String access_path) {
    super(access_path);
  }
}
