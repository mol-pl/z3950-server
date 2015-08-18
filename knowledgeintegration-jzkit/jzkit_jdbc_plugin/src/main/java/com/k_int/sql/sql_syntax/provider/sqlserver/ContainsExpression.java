package com.k_int.sql.sql_syntax.provider.sqlserver;

import java.sql.*;
import java.util.*;
import java.io.StringWriter;

import com.k_int.sql.sql_syntax.*;

/**
 * Title:       MatchExpression
 * Version:     $Id: MatchExpression.java,v 1.3 2005/08/15 18:39:16 ibbo Exp $
 * Author:      Ian Ibbotson
 * Company:
 * Description: Base class for building SQL Insert statements
 */
public class ContainsExpression extends BaseWhereCondition {
    List lhs=null;
    Expression rhs=null;
    boolean bool_mode;

    public ContainsExpression(List lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void outputSQL(StringWriter sw) {

        sw.write(" CONTAINS ( ");

        if ( lhs.size() > 1 ) 
          sw.write(" ( ");

        for ( Iterator i = lhs.iterator(); i.hasNext(); ) {
          ((Expression)(i.next())).outputSQL(sw);
          if ( i.hasNext() ) {
            sw.write(", ");
          }
        }

        if ( lhs.size() > 1 ) 
          sw.write(" ) ");

        sw.write(" , ");
        rhs.outputSQL(sw);

        sw.write(" ) ");
    }

    public int countChildClauses()
    {
      return 1;
    }

}

