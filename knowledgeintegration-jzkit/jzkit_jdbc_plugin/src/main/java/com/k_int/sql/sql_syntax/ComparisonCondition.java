package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       ComparisonCondition
 * Version:     $Id: ComparisonCondition.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class ComparisonCondition extends BaseWhereCondition
{
    BaseExpression lhs = null;
    String op = null;
    BaseExpression rhs = null;

    public ComparisonCondition(BaseExpression lhs, String op, BaseExpression rhs)
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
        sw.write(" ");
        rhs.outputSQL(sw);
        sw.write(" ) ");
    }

    public int countChildClauses()
    {
      return 1;
    }
}
