package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       InsertStatementBuilder
 * Version:     $Id: InsertStatement.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class InsertStatement extends SQLStatement
{
  private InsertDataClause insert_data_clause = null;

  public InsertStatement()
  {
  }

  public void setInsertDataClause(InsertDataClause insert_data_clause)
  {
    this.insert_data_clause = insert_data_clause;
  }

  public void addInsertColumnSpec(Expression e)
  {
    fields.add(e);
  }

  public InsertDataClause getInsertDataClause()
  {
    return insert_data_clause;
  }

  public String toString()
  {
    // System.err.println("InsertStatement::toString()");
    StringWriter sw = new StringWriter();
    new DefaultSQLDialect().emitInsertStatement(sw,this);
    return sw.toString();
  }
}
