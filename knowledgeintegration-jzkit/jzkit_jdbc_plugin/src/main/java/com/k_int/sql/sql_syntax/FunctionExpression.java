package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       FunctionExpression
 * Version:     $Id: FunctionExpression.java,v 1.2 2005/07/09 09:35:41 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class FunctionExpression extends Expression
{
    String function_name = null;
    Vector parameters = new Vector();

    public FunctionExpression(String function_name) {
      this.function_name = function_name;
    }

    public FunctionExpression(String function_name, BaseExpression param1) {
      this.function_name = function_name;
      addParameter(param1);
    }

    public void outputSQL(StringWriter sw) {
      sw.write(function_name);
      sw.write("(");
      
      for ( Enumeration e = parameters.elements(); e.hasMoreElements(); ) {
        BaseExpression be = (BaseExpression)(e.nextElement());
        be.outputSQL(sw);

        if ( e.hasMoreElements() )
          sw.write(", ");
      }

      sw.write(")");
    }

    public void addParameter(BaseExpression exp) {
      parameters.add(exp);
    }
}
