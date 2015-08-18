package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       MembershipCondition
 * Version:     $Id: MembershipCondition.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class MembershipCondition extends BaseWhereCondition
{
    public BaseExpression lhs = null;
    public boolean invert = false ;
    public BaseExpression rhs = null;

    public MembershipCondition(BaseExpression lhs, boolean invert, BaseExpression rhs)
    {
        this.lhs = lhs;
        this.invert = invert;
        this.rhs = rhs;
    }

    public void outputSQL(StringWriter sw)
    {
        sw.write(" ( ");
        lhs.outputSQL(sw);
        if ( invert )
          sw.write(" not ");
        sw.write(" in ");
        // Don't assume we need braces around the following clause.. it might be a subselect
        rhs.outputSQL(sw);
        sw.write(" ) ");
    }

    public int countChildClauses()
    {
      return 1;
    }

}
