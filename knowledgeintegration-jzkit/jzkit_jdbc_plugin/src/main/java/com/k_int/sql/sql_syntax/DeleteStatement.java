package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       DeleteStatementBuilder
 * Version:     $Id: DeleteStatement.java,v 1.2 2005/03/08 19:25:44 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Delete statements
 */
public class DeleteStatement extends RestrictableSQLStatement
{
    public DeleteStatement()
    {
    }

    public String toString()
    {
        StringWriter sw = new StringWriter();

        sw.write("Delete from ");
        sw.write(((TableScope)table_scopes.get(0)).getTable());

        // Iterator ve = values_list.elements() ;

        if ( null != filter ) {
            sw.write(" where ");
            filter.outputSQL(sw);
        }

        return sw.toString();
    }
}
