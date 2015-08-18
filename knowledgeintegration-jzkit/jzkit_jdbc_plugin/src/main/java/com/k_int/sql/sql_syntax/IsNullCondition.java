package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       IsNullCondition
 * Version:     $Id: IsNullCondition.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Copyright:
 * Author:      Ian Ibbotson
 * Company:
 * Description: Base class for building SQL Insert statements
 */
public class IsNullCondition extends BaseWhereCondition
{
    public IsNullCondition(Expression lhs, boolean invert)
    {
    }

    public int countChildClauses()
    {
      return 1;
    }

}
