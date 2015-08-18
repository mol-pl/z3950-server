package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;
import java.util.StringTokenizer;

/**
 * Title:       BoundPolygonExpression
 * Version:     $Id: BoundPolygonExpression.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class BoundPolygonExpression extends Expression
{
  Vector v = new Vector();

  public BoundPolygonExpression(String coordinate_pair_string)
  {
    // Should add strong validation in here and check for
    // even number of pairs and that last X,Y = first X,Y

    StringTokenizer st = new StringTokenizer(coordinate_pair_string,", ",false);
    while (st.hasMoreTokens()) 
    {
      v.add(st.nextToken());
    }
  }

  public void outputSQL(StringWriter sw)
  {
    sw.write("MDSYS.SDO_ORDINATE_ARRAY(");

    // Always convert a rectangle into a bound polygon
    if ( v.size() == 4 )
    {
      // Process as an envelope of N W S E
      // Becomes W,S,E,S,E,N,W,N,W,S = 1,2,3,2,3,0,1,0,1,2
      sw.write(v.get(1).toString()); sw.write(", "); sw.write(v.get(2).toString());
      sw.write(", ");
      sw.write(v.get(3).toString()); sw.write(", "); sw.write(v.get(2).toString());
      sw.write(", ");
      sw.write(v.get(3).toString()); sw.write(", "); sw.write(v.get(0).toString());
      sw.write(", ");
      sw.write(v.get(1).toString()); sw.write(", "); sw.write(v.get(0).toString());
      sw.write(", ");
      sw.write(v.get(1).toString()); sw.write(", "); sw.write(v.get(2).toString());
    }
    else
    {
      // Process as real bound polygon of X1,Y1,X2,Y2,X3,Y3
      for (Enumeration e = v.elements() ; e.hasMoreElements() ;) 
      {
        sw.write(e.nextElement().toString());
        if ( e.hasMoreElements() )
          sw.write(", ");
      }
    }
    sw.write(")");
  }

  public int countPairs()
  {
    return (int) ( v.size() / 2 );
  }

  public boolean isClosedPolygon()
  {
    if ( v.size() >= 4 )
    {
      int last_pair = v.size() - 2;
      if ( ( v.elementAt(0).toString().equals ( v.elementAt(last_pair).toString() ) ) &&
           ( v.elementAt(1).toString().equals ( v.elementAt(last_pair+1).toString() ) ) )
        return true;
    }

    return false;
  }

  public boolean isBoundingRectangle()
  {
    // if ( v.size() == 4 )
    //   return true;

    return false;
  }
}
