package com.k_int.sql.sql_syntax.provider.mysql;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

import com.k_int.sql.sql_syntax.*;

/**
 * Title:     MatchExpression
 * Version:   $Id: MBRContainsExpression.java,v 1.2 2005/07/09 09:35:41 ibbo Exp $
 * Author:    Ian Ibbotson
 * Company:
 * Description: Base class for building SQL Insert statements
 */
public class MBRContainsExpression extends BaseWhereCondition
{
  private Expression geo_column=null;
  private Expression wkt_string=null;

  public MBRContainsExpression(Expression geo_column, Expression wkt_string) {
    this.geo_column = geo_column;
    this.wkt_string = wkt_string;
  }

  public void outputSQL(StringWriter sw)
  {
    sw.write(" MBRContains(");
    wkt_string.outputSQL(sw);
    sw.write(", ");
    geo_column.outputSQL(sw);
    sw.write(")");
  }

  public int countChildClauses()
  {
    return 1;
  }

}

