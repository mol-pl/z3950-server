package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       AssignmentExpression
 * Version:     $Id: AssignmentExpression.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class AssignmentExpression extends BaseExpression
{
  BaseExpression lhs = null;
  BaseExpression rhs = null;

  public AssignmentExpression(BaseExpression lhs, BaseExpression rhs)
  {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public BaseExpression getLHS()
  {
    return lhs;
  }

  public BaseExpression getRHS()
  {
    return rhs;
  }
}
