package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       ValueExpression
 * Version:     $Id: ValueExpression.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class ValueExpression extends Expression
{
    Object value = null;

    public ValueExpression(Object value)
    {
        // System.err.println("ValueExpression::ValueExpression("+value+")");
        this.value = value;
    }

    public ValueExpression(int value)
    {
        // System.err.println("ValueExpression::ValueExpression("+value+")");
        this.value = new Integer(value);
    }

    public void outputSQL(StringWriter sw)
    {
        if ( null != value )
        {
            if ( value instanceof String )
            {
                sw.write('\'');
                sw.write(value.toString());
                sw.write('\'');
            }
            else
                sw.write(value.toString());
        }
        else
            sw.write("NULL");
    }

}
