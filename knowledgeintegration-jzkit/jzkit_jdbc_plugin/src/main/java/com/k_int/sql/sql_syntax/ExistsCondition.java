package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       ExistsCondition
 * Version:     $Id: ExistsCondition.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class ExistsCondition extends BaseWhereCondition
{
    public ExistsCondition(String subquery)
    {
    }

    public int countChildClauses()
    {
      return 1;
    }

}
