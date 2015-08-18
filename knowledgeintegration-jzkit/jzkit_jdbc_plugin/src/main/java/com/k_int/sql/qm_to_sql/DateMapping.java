package com.k_int.sql.qm_to_sql;

/**
 * DateMapping
 *       Title: DateMapping
 * Description: Allows us to describe a search mapping for date searching
 *     @author: Ian Ibbotson (ian.ibbotson@sun.com)
 *    @version: $Id: DateMapping.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 *
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="101"
 */
public class DateMapping extends BaseMapping {

  private DateMapping() {
  }

  public DateMapping(String access_path) {
    super(access_path);
  }
}
