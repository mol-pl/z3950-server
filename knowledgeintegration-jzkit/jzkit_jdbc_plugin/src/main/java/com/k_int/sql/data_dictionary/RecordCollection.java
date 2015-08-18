package com.k_int.sql.data_dictionary;

import java.sql.*;
import java.util.Iterator;

/**
 * Title:       RecordCollection
 * @version:    $Id: RecordCollection.java,v 1.3 2004/10/28 09:54:25 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description:
 */

public abstract class RecordCollection
{
  // Rows of this result set must conform to the pattern : 'LocalHost':Repository:TableName:Cols....
  // Headings might be Scheme:Repository:Collection:Attrs
  // Also, the attributes must contain all the components of any key defined for that entity

  public RecordCollection()
  {
  }

  public abstract int size();

  public Entity get(String pos)
  {
    // Convert rownum to integer
    int i = Integer.parseInt(pos);
    return get(i);
  }

  public abstract Entity get(int pos);
  public abstract int add(Entity e);
  public abstract Iterator iterator();
}
