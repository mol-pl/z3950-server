package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       ScopedColumnExpression
 * Version:     $Id: ScopedColumnExpression.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 */
public class ScopedColumnExpression extends Expression
{
    TableScope scope = null;
    String colname = null;


    public ScopedColumnExpression(TableScope scope, String colname)
    {
        this.scope = scope;
        this.colname = colname;
    }

    public void outputSQL(StringWriter sw)
    {
        // if ( ( null != scope ) && ( null != scope.getAlias() ) && ( ! scope.getAlias().equals("") ) )
        if ( ( null != scope ) && ( null != scope.getSQLToken() ) )
        {
            sw.write(scope.getSQLToken());
            sw.write(".");
        }
        sw.write(colname);
    }

    public String getColumnName()
    {
      return colname;
    }

    public TableScope getTableScope()
    {
      return scope;
    }

}
