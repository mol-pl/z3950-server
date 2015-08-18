package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.StringWriter;

/**
 * Title:       UpdateStatement
 * Version:     $Id: UpdateStatement.java,v 1.2 2005/03/08 19:25:44 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class UpdateStatement extends RestrictableSQLStatement
{
  private List assignments = new ArrayList();

  public UpdateStatement()
  {
    super();
  }

  public String toString()
  {
    // System.err.println("InsertStatement::toString()");
    StringWriter sw = new StringWriter();
    new DefaultSQLDialect().emitUpdateStatement(sw,this);
    return sw.toString();
  }

  public void set(ColumnExpression col, Expression value)
  {
    assignments.add(new AssignmentExpression(col,value));
  }

  public void set(ScopedColumnExpression col, Expression value)
  {
    assignments.add(new AssignmentExpression(col,value));
  }

  public Iterator enumerateAssignments()
  {
    return assignments.iterator();
  }
}
