package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       FunctionExpression
 * Version:     $Id: OrderByExpression.java,v 1.1 2005/08/15 18:39:16 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class OrderByExpression extends Expression
{
    Expression spec=null;
    boolean ascending=true;

    public OrderByExpression(Expression spec, boolean ascending) {
      this.spec = spec;
      this.ascending = ascending;
    }

    public void outputSQL(StringWriter sw) {
      
      sw.write(" ");
      spec.outputSQL(sw);
      if ( ascending ) 
        sw.write(" ASC ");
      else 
        sw.write(" DESC ");
    }
}
