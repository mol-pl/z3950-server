package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       ExpressionList
 * Version:     $Id: ExpressionList.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: 
 */
public class ExpressionList extends BaseExpression
{
  private Vector value_list = new Vector();
  
  public void addExpression(BaseExpression value)
  {
    value_list.add(value);
  }

  public Enumeration enumerateValues()
  {
    return value_list.elements();
  }

  public void outputSQL(StringWriter sw)
  {
    sw.write(" ( ");

    for ( Enumeration e = value_list.elements(); e.hasMoreElements(); )
    {
       BaseExpression exp = (BaseExpression) e.nextElement();
       exp.outputSQL(sw);
       if ( e.hasMoreElements() )
         sw.write(", ");
    }

    sw.write(" ) ");
  }

}
