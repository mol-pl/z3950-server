package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Title:       RestrictableSQLStatement
 * Version:     $Id: RestrictableSQLStatement.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL statements
 */
public abstract class RestrictableSQLStatement extends SQLStatement implements Restrictable
{
    protected BaseWhereCondition filter = null;

    public RestrictableSQLStatement()
    {
      super();
    }

    public void addCondition(BaseWhereCondition c)
    {   
        filter=c;
    }

    public BaseWhereCondition getCondition()
    {
      return filter;
    }
}
