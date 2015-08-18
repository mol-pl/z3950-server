package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       RangeCheckCondition
 * Version:     $Id: RangeCheckCondition.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class RangeCheckCondition extends BaseWhereCondition
{
    public RangeCheckCondition(Expression op, boolean invert, Expression lower_bound_expression, Expression upper_bound_expression)
    {
    }

    public int countChildClauses()
    {
      return 1;
    }

}
