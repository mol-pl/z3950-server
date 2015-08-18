package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.*;
import java.io.StringWriter;

/**
 * Title:       SelectStatement
 * Version:     $Id: SelectStatement.java,v 1.3 2005/08/15 18:39:16 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Select statements
 */
public class SelectStatement extends RestrictableSQLStatement {
  private List sort_order = new ArrayList();
  private boolean distinct = false;

  public SelectStatement() {
    super();
  }

  public SelectStatement(boolean distinct) {
    super();
    this.distinct = distinct;
  }

  public void addSelectExpression(BaseExpression exp) {
    fields.add(exp);
  }

  public void sortBy(OrderByExpression exp) {
    sort_order.add(exp);
  }

  public String toString() {
    StringWriter sw = new StringWriter();
    new DefaultSQLDialect().emitSelectStatement(sw,this);
    return sw.toString();
  }

  public String toCountString() {
    StringWriter sw = new StringWriter();
    new DefaultSQLDialect().emitSelectStatement(sw,this);
    return sw.toString();
  }

  public Iterator getSortList() {
    return sort_order.iterator();
  }

  public boolean getDistinct() {
    return distinct;
  }

  public void setDistinct(boolean distinct) {
    this.distinct = distinct;
  }
}
