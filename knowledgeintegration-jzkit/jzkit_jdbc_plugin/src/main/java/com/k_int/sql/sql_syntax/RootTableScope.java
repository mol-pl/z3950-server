package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       TableScope
 * Version:     $Id: RootTableScope.java,v 1.1 2005/03/08 19:28:08 ibbo Exp $
 * Author:      Ian Ibbotson
 */
public class RootTableScope extends TableScope {
  private SQLStatement parent_statement = null;

  public RootTableScope(SQLStatement parent_statement, String base_table_name, String alias) {
    super(base_table_name, alias);
    this.parent_statement = parent_statement;
  }

  public RootTableScope(SQLStatement parent_statement, String base_table_name) {
    this(parent_statement, base_table_name, null);
  }
}
