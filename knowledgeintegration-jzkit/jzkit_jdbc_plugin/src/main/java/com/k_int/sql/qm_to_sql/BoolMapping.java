package com.k_int.sql.qm_to_sql;


/**
 * BoolMapping
 *       Title: BoolMapping
 * Description: Allows us to describe a search mapping for date searching
 *     @author: Ian Ibbotson (ian.ibbotson@sun.com)
 *    @version: $Id: BoolMapping.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 */
public class BoolMapping extends BaseMapping
{
  private BoolMapping() {
  }

  public BoolMapping(String access_path) {
    super(access_path);
  }
}
