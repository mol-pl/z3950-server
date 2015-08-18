package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       SequenceNumExpression
 * Version:     $Id: SequenceNumExpression.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public abstract class SequenceNumExpression extends Expression
{
    protected String seq_name = null;

    public SequenceNumExpression(String seq_name)
    {
        this.seq_name = seq_name;
    }

    public abstract void outputSQL(StringWriter sw);

}
