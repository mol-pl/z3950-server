package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       SetClause
 * Version:     $Id: SetClause.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class SetClause
{
    String col=null;
    Expression value=null;

    public SetClause(String col, Expression value)
    {
        this.col = col;
        this.value = value;
    }

    public void outputSQL(StringWriter sw)
    {
        sw.write(" ");
        sw.write(col);
        sw.write(" = ");
        value.outputSQL(sw);
    }
}
