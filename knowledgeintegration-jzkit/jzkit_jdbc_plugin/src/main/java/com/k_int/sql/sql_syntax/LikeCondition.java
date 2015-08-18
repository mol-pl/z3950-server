package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       LikeCondition
 * Version:     $Id: LikeCondition.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Company:
 * Description: Base class for building SQL Insert statements
 */
public class LikeCondition extends BaseWhereCondition
{
    Expression lhs=null;
    boolean invert=false;
    Expression rhs=null;
    String escape=null;

    public LikeCondition(Expression lhs, boolean invert, Expression rhs, String escape)
    {
        this.lhs = lhs;
        this.invert = invert;
        this.rhs=rhs;
        this.escape=escape;
    }

    public void outputSQL(StringWriter sw)
    {
        sw.write(" ( ");
        lhs.outputSQL(sw);
        sw.write(" LIKE ");
        rhs.outputSQL(sw);
        sw.write(" ) ");
    }

    public int countChildClauses()
    {
      return 1;
    }

}

