package com.k_int.sql.sql_syntax;

import java.io.StringWriter;
import java.util.*;

/**
 * Title:       DefaultSQLEmitter
 * @version:    $Id: DefaultSQLDialect.java,v 1.6 2005/08/15 18:39:16 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description: 
 */
public class DefaultSQLDialect extends SQLDialect
{
  protected void emitInternalRowIdentifier(StringWriter sw, InternalRowIdentifierExpression identifier)
  {
    // sw.write(identifier.getScope().getAlias());
    sw.write(identifier.getScope().getSQLToken());
    sw.write(".UniqueRowIdentifier");
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

  public BaseWhereCondition freeTextExpression(List field_list, Expression value, boolean bool_mode) {
    return null;
  }

  public BaseWhereCondition createSpatialExpression(Expression geo_column, Expression wkt_value) {
    return null;
  }

  public Expression WKTToGeometry(Expression wkt_value) {
    return null;
  }

  public Expression freeTextScore(List free_text_target, Expression value, boolean bool_mode) {
    return null;
  }

  // Should the engine add free text quote strings to the actual term. For example, SQLServer needs wildcard terms to be enclosed in "" ie, "ter*"
  public boolean freeTextTermNeedsQuoting(String term) {
    return false;
  }

  // What quote char should be used
  public char freeTextQuoteChar() {
    return '"';
  }

  public String processFreeTextTermList(List terms, String relation) {
    return null;
  }

}
