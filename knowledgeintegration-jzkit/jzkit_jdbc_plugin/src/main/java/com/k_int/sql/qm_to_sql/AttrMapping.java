package com.k_int.sql.qm_to_sql;

/**
 * Interface implemented by the various mapping different access point mapping types.
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: AttrMapping.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 *
 * $Log: AttrMapping.java,v $
 * Revision 1.1  2004/10/22 09:24:28  ibbo
 * Added jdbc plugin
 *
 * Revision 1.4  2003/11/26 12:12:33  iibbotson
 * More comment work
 *
 */
public interface AttrMapping
{
  /**
   * Return the actual access path which should be used for this mapping.
   * @return A . seperated string defining a database access path as defined in the Dictionary config file
   */
  public String getAccessPath();
}
