package com.k_int.sql.qm_to_sql;

/**
 * Title: Gazeteer
 * Description:     Lets the server talk to a gazetter to resolve place names, etc.
 * Copyright:       
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@k-int.com)
 * @version:        $Id: Gazeteer.java,v 1.2 2004/11/05 17:34:49 ibbo Exp $
 */
public interface Gazeteer {
  public String lookupWKTForPlaceName(String place_name);
  public String lookupWKTForNearPlaceName(String place_name, long near);

  public PlaceReferenceDTO[] resolve(String place_name);
}
