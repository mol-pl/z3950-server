package com.k_int.sql.sql_syntax.provider.mysql;

import java.sql.*;
import java.util.*;
import java.io.StringWriter;

import com.k_int.sql.sql_syntax.*;

/**
 * Title:       MatchExpression
 * Version:     $Id: MatchWhereExpression.java,v 1.1 2005/08/15 18:39:16 ibbo Exp $
 * Author:      Ian Ibbotson
 * Company:
 * Description: Base class for building SQL Insert statements
 */
public class MatchWhereExpression extends BaseWhereCondition {
    List lhs=null;
    Expression rhs=null;
    boolean bool_mode;

    public MatchWhereExpression(List lhs, Expression rhs, boolean bool_mode) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.bool_mode = bool_mode;
    }

    public void outputSQL(StringWriter sw) {
        sw.write(" MATCH ( ");
        for ( Iterator i = lhs.iterator(); i.hasNext(); ) {
          ((Expression)(i.next())).outputSQL(sw);
          if ( i.hasNext() ) {
            sw.write(", ");
          }
        }
        sw.write(" ) AGAINST ( ");
        rhs.outputSQL(sw);
        if ( bool_mode ) {
          sw.write(" IN BOOLEAN MODE");
        }
        sw.write(" ) ");
    }

    public int countChildClauses()
    {
      return 1;
    }

}

