package com.k_int.sql.sql_syntax;

import java.io.StringWriter;
import java.util.*;

/**
 * Title:       SQLDialect
 * @version:    $Id: SQLDialect.java,v 1.7 2005/08/15 18:39:16 ibbo Exp $
 * @author:     Ian Ibbotson 
 */
public abstract class SQLDialect {

  private static String SELECT_KEYWORD = "Select "; // Select <sl> From <tl> Where <wc> Group by <gb> Order By <o> Having <h>
  private static String UPDATE_KEYWORD = "Update "; // Update <tl> set <sl> where <wc> 
  private static String INSERT_KEYWORD = "Insert "; // Insert into <tl> [ values | expression ]
  private static String DELETE_KEYWORD = "Delete "; // Delete from <tl> where <wc>
  private static String DISTINCT_KEYWORD = "Distinct ";
  private static String FROM_KEYWORD = "From ";
  private static String INTO_KEYWORD = "Into ";
  private static String WHERE_KEYWORD = "Where ";
  private static String ORDER_BY_KEYWORD = "Order By ";
  private static String SET_KEYWORD = "Set ";

  public SQLDialect() {
  }

  private void emitExpressionList(StringWriter sw, Iterator e) {
    while (e.hasNext()) {
      Object field = e.next();
      if ( field instanceof BaseExpression ) {
        emitExpression(sw,(BaseExpression)field);
      }
      else {
        sw.write(field.toString());
      }

      if ( e.hasNext() )
        sw.write(", ");
    } 
  }

  private void emitFromClause(StringWriter sw,Iterator e) {
    sw.write(FROM_KEYWORD);
    emitTableScopes(sw,e);
  }

  private void emitTableScopes(StringWriter sw,Iterator e) {
    if ( e.hasNext() ) {
      while ( e.hasNext() ) {
        TableScope ts = (TableScope)e.next();
        sw.write( ts.getTable() + " " + ts.getSQLToken() );

        emitChildScopes(sw, ts.getChildScopes().iterator());
        if ( e.hasNext() ) {
          sw.write(", ");
        }
      }
      sw.write("\n");
    }
  }

  private void emitInsertTableScopes(StringWriter sw,Iterator e) {
    while ( e.hasNext() ) {
      TableScope ts = (TableScope)e.next();
      sw.write( ts.getTable() );
      if ( e.hasNext() ) {
        sw.write(", ");
      }
    }
  }

  private void emitChildScopes(StringWriter sw,Iterator e) {
    while ( e.hasNext() ) {
      JoinTableScope ts = (JoinTableScope)e.next();

      switch ( ts.getJoinType() ) {
        case 2:
          sw.write("\n LEFT OUTER JOIN ");
          break;
        default:
          sw.write("\n JOIN ");
          break;
      }

      sw.write( ts.getTable() );
      sw.write(" AS ");
      sw.write( ts.getSQLToken() );
      if ( ( ts.getJoinCondition() != null ) && ( ts.getJoinCondition().countChildClauses() > 0 ) ) {
        sw.write(" ON ");
        ts.getJoinCondition().outputSQL(sw);
      }

      emitChildScopes(sw, ts.getChildScopes().iterator());
    }
  }

  private void emitWhereClause(StringWriter sw, BaseWhereCondition filter) {
    if ( ( null != filter ) && ( filter.countChildClauses() > 0 ) ) {
      sw.write(WHERE_KEYWORD);
      filter.outputSQL(sw);
    }
  }

  private void emitOrderByClause(StringWriter sw, Iterator e) {
    if ( e.hasNext() ) {
      sw.write(ORDER_BY_KEYWORD);

      while ( e.hasNext() ) {
        OrderByExpression obe = (OrderByExpression) e.next();

        obe.outputSQL(sw);

	if ( e.hasNext() )
          sw.write(", ");
      }
    }
  }

  public void emitCountStatement(StringWriter sw, SelectStatement select_statement)
  {
    Iterator e = select_statement.enumerateTableScopes();
    TableScope first_table_scope = (TableScope) e.next();
    sw.write(SELECT_KEYWORD);
    emitCountExpression(sw,new CountExpression(new InternalRowIdentifierExpression(first_table_scope)));
    sw.write("\n");
    emitFromClause(sw,select_statement.enumerateRootTableScopes());
    emitWhereClause(sw,select_statement.getCondition());
  }

  public void emitSelectStatement(StringWriter sw, SelectStatement select_statement)
  {
    sw.write(SELECT_KEYWORD);

    if ( select_statement.getDistinct() )
    {
      sw.write(DISTINCT_KEYWORD);
    }

    emitExpressionList(sw,select_statement.enumerateFields());
    sw.write("\n");
    emitFromClause(sw,select_statement.enumerateRootTableScopes());
    emitWhereClause(sw,select_statement.getCondition());
    sw.write("\n");
    // Group by here
    emitOrderByClause(sw,select_statement.getSortList());
    // Having by here
  }

  private void emitValuesClause(StringWriter sw,InsertValuesClause ivc)
  {
    sw.write(" Values ( ");

    for ( Iterator vals = ivc.enumerateValues(); vals.hasNext(); )
    {
      Expression exp = (Expression)(vals.next());
      emitExpression(sw, exp);
      if ( vals.hasNext() )
        sw.write(",");
    }

    sw.write(" ) ");
  }

  public void emitInsertStatement(StringWriter sw, InsertStatement insert_statement)
  {
    // System.err.println("emit insert");
    sw.write(INSERT_KEYWORD);
    sw.write(INTO_KEYWORD);
    emitInsertTableScopes(sw,insert_statement.enumerateTableScopes());
    sw.write(" ");

    Iterator fields = insert_statement.enumerateFields();
    if ( fields.hasNext() ) {
      sw.write(" ( ");
      emitExpressionList(sw,fields);
      sw.write(" ) ");
    }

    InsertDataClause ids = insert_statement.getInsertDataClause();
    if ( ids instanceof InsertSubquery )
      emitSelectStatement(sw,(SelectStatement)((InsertSubquery)ids).getSubQuery());
    else
      emitValuesClause(sw,(InsertValuesClause)ids);
  }

  public void emitDeleteStatement(StringWriter sw, DeleteStatement delete_statement)
  {
    sw.write(DELETE_KEYWORD);
    sw.write("\n");
    emitFromClause(sw,delete_statement.enumerateRootTableScopes());
    emitWhereClause(sw,delete_statement.getCondition());
    sw.write("\n");
  }

  public void emitUpdateStatement(StringWriter sw, UpdateStatement update_statement) {
    sw.write(UPDATE_KEYWORD);
    sw.write(" ");
    emitInsertTableScopes(sw,update_statement.enumerateTableScopes());
    sw.write("\n");
    sw.write(SET_KEYWORD);
    emitExpressionList(sw, update_statement.enumerateAssignments());
    sw.write("\n");
    emitWhereClause(sw,update_statement.getCondition());
  }

  public void emitExpression(StringWriter sw, BaseExpression be) {
    if ( be instanceof ColumnExpression )
      ((ColumnExpression)be).outputSQL(sw);
    else if ( be instanceof ScopedColumnExpression )
      ((ScopedColumnExpression)be).outputSQL(sw);
    else if ( be instanceof CountExpression )
      emitCountExpression(sw, (CountExpression) be);
    else if ( be instanceof InternalRowIdentifierExpression )
      emitInternalRowIdentifier(sw, (InternalRowIdentifierExpression)be);
    else if ( be instanceof BindVariableExpression )
      sw.write("?");
    else if ( be instanceof ValueExpression )
      ((ValueExpression)be).outputSQL(sw);
    else if ( be instanceof AssignmentExpression )
      emitAssignment(sw, (AssignmentExpression)be);
    else
    {
      // consider calling outputSQL here for all but degenerate case.
      sw.write("**UNHANDLED EXPRESSION SUBCLASS IN emitExpression** "+be.getClass());
    }
  }

  public void emitAssignment(StringWriter sw, AssignmentExpression ce)
  {
    emitExpression(sw, ce.getLHS());
    sw.write("=");
    emitExpression(sw, ce.getRHS());
  }

  public void emitCountExpression(StringWriter sw, CountExpression ce)
  {
    sw.write("count(");
    emitExpression(sw,ce.getParam());
    sw.write(")");
  }

  protected abstract void emitInternalRowIdentifier(StringWriter sw, InternalRowIdentifierExpression identifier);

  /** Does the emitter support auto increment fields? */
  public abstract boolean supportedAutoIncrementFields();

  /** emit the statement which should be used to retrieve the last value of an auto-increment column.
   */
  public abstract void emitPostInsertAutoIncrementFetchSelect(StringWriter sw);

  public abstract Expression getNextSeqnoExpression(String seq_name);

  /**
   * Add a free text expression looking for the specified value in any of the identified column expressions
   */
  public abstract BaseWhereCondition freeTextExpression(List free_text_target, Expression value, boolean bool_mode);
  public abstract Expression freeTextScore(List free_text_target, Expression value, boolean bool_mode);
  public abstract BaseWhereCondition createSpatialExpression(Expression geo_column, Expression geometry);
  public abstract Expression WKTToGeometry(Expression wkt_value);

  // Should the engine add free text quote strings to the actual term. For example, SQLServer needs wildcard terms to be enclosed in "" ie, "ter*"
  public abstract boolean freeTextTermNeedsQuoting(String term);

  // What quote char should be used
  public abstract char freeTextQuoteChar();

  public abstract String processFreeTextTermList(List terms, String relation);
}
