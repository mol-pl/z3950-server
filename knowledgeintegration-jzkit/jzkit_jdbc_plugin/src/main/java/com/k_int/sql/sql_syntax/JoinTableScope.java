package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       TableScope
 * Version:     $Id: JoinTableScope.java,v 1.1 2005/03/08 19:28:08 ibbo Exp $
 * Author:      Ian Ibbotson
 */
public class JoinTableScope extends TableScope {

  private TableScope parent;
  private BaseWhereCondition join_on_condition;
  private int join_type;

  public static int NATURAL_JOIN = 0;
  public static int INNER_JOIN = 0;
  public static int LEFT_JOIN = 1;
  public static int LEFT_OUTER_JOIN = 1;

  public JoinTableScope(String table_name,
                        String alias,
                        TableScope parent,
                        int join_type) {
    super(table_name, alias);
    this.parent = parent;
    this.join_type = join_type;
  }

  public TableScope getParentScope() {
    return parent;
  }

  public BaseWhereCondition getJoinCondition() {
    return join_on_condition;
  }

  public int getJoinType() {
    return join_type;
  }

  public void addOnCondition(BaseWhereCondition c) {
    this.join_on_condition = c;
  }

}
