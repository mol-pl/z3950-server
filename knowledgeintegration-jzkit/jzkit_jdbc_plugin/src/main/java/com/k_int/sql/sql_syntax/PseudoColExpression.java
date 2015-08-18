package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       PseudoColExpression
 * Version:     $Id: PseudoColExpression.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */

public class PseudoColExpression extends Expression
{
    String col_name = null;

    public PseudoColExpression(String col_name)
    {
        this.col_name = col_name;
    }

    public void outputSQL(StringWriter sw)
    {
        sw.write(col_name);
    }

}
