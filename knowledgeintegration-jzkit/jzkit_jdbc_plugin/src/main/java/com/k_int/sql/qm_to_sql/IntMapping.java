package com.k_int.sql.qm_to_sql;

/**
 * DateMapping
 *       Title: DateMapping
 * Description: Allows us to describe a search mapping for date searching
 *     @author: Ian Ibbotson (ian.ibbotson@sun.com)
 *    @version: $Id: IntMapping.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 *
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="104"
 */
public class IntMapping extends BaseMapping
{
  private IntMapping() {
  }

  public IntMapping(String access_path) {
    super(access_path);
  }
}
