package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.*;
import java.io.StringWriter;

//
//
// What about insert into a(b,c,d) select e,f,g from h;
//
// = Insert into (fields) <<Subselect>> | <<Values List>>


/**
 * Title:       InsertDataClause
 * Version:     $Id: InsertValuesClause.java,v 1.2 2005/03/08 19:25:44 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class InsertValuesClause implements InsertDataClause
{
  private List value_list = new ArrayList();
  
  public void addValueAssignment(BaseExpression value) {
    value_list.add(value);
  }

  public Iterator enumerateValues() {
    return value_list.iterator();
  }
}
