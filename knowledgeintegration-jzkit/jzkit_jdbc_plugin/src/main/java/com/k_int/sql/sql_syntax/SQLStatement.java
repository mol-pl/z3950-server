package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.*;

/**
 * Title:       SQLStatement
 * Version:     $Id: SQLStatement.java,v 1.2 2005/03/08 19:25:44 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL statements
 */
public class SQLStatement {

  protected List root_scopes = new ArrayList();
  protected List table_scopes = new ArrayList();
  protected Map table_count = new HashMap();
  protected List fields = new ArrayList();
  protected List values_list = new ArrayList();
  protected int bind_var_count=0;

  public SQLStatement() {
  }

  public RootTableScope addTable(String table_name) {
    int i_count = 0;
    Integer i = (Integer)table_count.get(table_name);

    if ( null == i )
      i_count=1;
    else
      i_count = (i.intValue())+1;

    table_count.put(table_name, new Integer(i_count));

    String alias = null;

    if ( i_count==1 )
      alias=table_name;
    else
      alias=table_name+i_count;

    RootTableScope ts = new RootTableScope(this,table_name,alias);
    table_scopes.add(ts);

    root_scopes.add(ts);

    return ts;
  }

  public RootTableScope addTable(String table_name, String alias) {
    int i_count = 0;
    Integer i = (Integer)table_count.get(table_name);

    if ( null == i )
      i_count=1;
    else
      i_count = (i.intValue())+1;

    table_count.put(table_name, new Integer(i_count));

    RootTableScope ts = new RootTableScope(this,table_name,alias);
    table_scopes.add(ts);
    root_scopes.add(ts);

    return ts;
  }

  public JoinTableScope addJoin(String table_name,
                                String alias,
                                TableScope parent,
                                int join_type) {
    int i_count = 0;

    Integer i = (Integer)table_count.get(table_name);

    if ( null == i )
      i_count=1;
    else
      i_count = (i.intValue())+1;

    table_count.put(table_name, new Integer(i_count));

    JoinTableScope ts = new JoinTableScope(table_name,
                                           alias,
                                           parent,
                                           join_type);

    table_scopes.add(ts);
    parent.registerChildScope(ts);

    return ts;
  }

  public JoinTableScope addJoin(String table_name,
                                String alias,
                                TableScope parent,
                                String[][] key_pairs,
                                int join_type) {

    JoinTableScope ts = addJoin(table_name, alias, parent, join_type);

    ConditionCombination r = new ConditionCombination("AND");
    for ( int c=0; c<key_pairs.length; c++ ) {
      r.addCondition( new ComparisonCondition( new ScopedColumnExpression(parent, key_pairs[c][0]), "=", new ScopedColumnExpression(ts,key_pairs[c][1])));
    }

    ts.addOnCondition(r);

    return ts;
  }

  public TableScope lookupTableScope(String tablename) {
    return lookupScope(tablename);
  }

  public TableScope lookupScope(String scopename) {
    for ( Iterator e = table_scopes.iterator(); e.hasNext(); ) {
      TableScope ts = (TableScope)e.next();
      if ( ts.getAlias().equals(scopename) )
        return ts;
    }
    return null;
  }

  public void addField(BaseExpression e) {
    fields.add(e);
  }

  public Iterator enumerateFields() {
    return fields.iterator();
  }

  public Iterator enumerateTableScopes() {
    return table_scopes.iterator();
  }

  public Iterator enumerateRootTableScopes() {
    return root_scopes.iterator();
  }

}
