package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.*;
import java.io.StringWriter;

/**
 * Title:       TableScope
 * Version:     $Id: TableScope.java,v 1.2 2005/03/08 19:25:44 ibbo Exp $
 * Author:      Ian Ibbotson
 */
public abstract class TableScope {
  private String base_table_name = null;
  private String alias=null;
  private List child_scopes = new ArrayList();

  public TableScope(String base_table_name, String alias) {
    this.base_table_name = base_table_name;
    this.alias = alias;
  }

  public String getAlias() {
    if ( alias == null )
      return base_table_name;
    return alias;
  }

  public String getTable() {
    return base_table_name;
  }

  public String getSQLToken() {
    return "alias"+this.hashCode();
  }

  public List getChildScopes() {
    return child_scopes;
  }

  public void registerChildScope(TableScope child) {
    child_scopes.add(child);
  }
}
