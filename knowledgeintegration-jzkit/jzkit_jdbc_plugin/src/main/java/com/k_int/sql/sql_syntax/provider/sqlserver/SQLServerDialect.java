/*
 * Title:       OracleSQLEmitter
 * Version:     $Id: MySQLDialect.java,v 1.6 2005/08/15 18:39:16 ibbo Exp $
 * Copyright:   
 * Author:      Ian Ibbotson
 * Company:     
 * Description: 
 */

package com.k_int.sql.sql_syntax.provider.sqlserver;

import java.sql.*;
import java.util.*;
import java.io.StringWriter;
import com.k_int.sql.sql_syntax.*;

public class SQLServerDialect extends SQLDialect {

  public static final String ROWID = ".rowid";

  // We just use the default emitter for now.
  // public void emit(StringWrtiter sw, SelectStatement select_statement)
  // public void emit(StringWriter sw, DeleteStatement delete_statement)
  // public void emit(StringWriter sw, DeleteStatement update_statement)
  protected void emitInternalRowIdentifier(StringWriter sw, InternalRowIdentifierExpression identifier)
  {
    sw.write(identifier.getScope().getAlias());
    sw.write(ROWID);
  }

  /** Does the emitter support auto increment fields? */
  public boolean supportedAutoIncrementFields()
  {
    return false;
  }

  /** emit the statement which should be used to retrieve the last value of an auto-increment column.
   */
  public void emitPostInsertAutoIncrementFetchSelect(StringWriter sw)
  {
  }

  public Expression getNextSeqnoExpression(String seq_name)
  {
    return null;
  }

  public Expression freeTextExpression(String target_column) {
    return null;
  }

  public BaseWhereCondition freeTextExpression(List free_text_target, Expression value, boolean bool_mode) {
    return new com.k_int.sql.sql_syntax.provider.sqlserver.ContainsExpression(free_text_target, value);
  }


  public BaseWhereCondition createSpatialExpression(Expression geo_column, Expression wkt_value) {
    return new com.k_int.sql.sql_syntax.provider.mysql.MBRContainsExpression(geo_column, wkt_value);
  }

  public Expression WKTToGeometry(Expression wkt_value) {
    return new FunctionExpression("GeomFromText",wkt_value);
  }

  public Expression freeTextScore(List free_text_target, Expression value, boolean bool_mode) {
    return null;
  }

  // Should the engine add free text quote strings to the actual term. For example, SQLServer needs wildcard terms to be enclosed in "" ie, "ter*"
  public boolean freeTextTermNeedsQuoting(String term) {
    return true;
  }

  // What quote char should be used
  public char freeTextQuoteChar() {
    return '"';
  }

  public String processFreeTextTermList(List terms, String relation) {
    java.io.StringWriter sw = new java.io.StringWriter();

    for ( java.util.Iterator i = terms.iterator(); i.hasNext(); ) {
      sw.write(i.next().toString());
      if ( i.hasNext() ) {
        sw.write(" ");
      }
    }
    return sw.toString();
  }

}
