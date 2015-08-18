package com.k_int.sql.qm_to_sql;


/**
 *  Actual mapping info
 * Title:           Mapping
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: CaseInsensitiveTextMapping.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 *
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="100"
 */
public class CaseInsensitiveTextMapping extends BaseMapping
{
  private CaseInsensitiveTextMapping() {
  }

  public CaseInsensitiveTextMapping(String access_path) {
    super(access_path);
  }
}
