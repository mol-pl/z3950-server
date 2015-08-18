package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       SubQueryCondition
 * Version:     
 * Author:      Chris Kilgour
 * Description: Class for generating a subquery condition
 */
public class SubQueryCondition extends BaseWhereCondition
{
    BaseExpression lhs = null;
    String op = null;
    String rhs = null;

    public SubQueryCondition(BaseExpression lhs, String op, String rhs)
    {
        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
    }

    public void outputSQL(StringWriter sw)
    {
        sw.write(" ( ");
        lhs.outputSQL(sw);
        sw.write(" ");
        sw.write(op);
        sw.write(" ( ");
        sw.write(rhs);
        sw.write(" ) ) ");
    }

    public int countChildClauses()
    {
      return 1;
    }
}
