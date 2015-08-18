package com.k_int.sql.data_dictionary;

import java.util.Hashtable;

/**
 * Constants.
 *
 * @author Ian Ibbotson
 * @version $Id: Constants.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 */
public class Constants
{
  private static Hashtable type_codes_by_name = null;
  private static Hashtable type_names_by_code = null;

  public static final Object[][] sql_type_data = { { "BIT", new Integer(-7) },
	                                           { "TINYINT", new Integer(-6) },
					           { "SMALLINT", new Integer(5) },
						   { "INTEGER", new Integer(4) },
						   { "BIGINT", new Integer(-5) },
						   { "FLOAT", new Integer(6) },
						   { "REAL", new Integer(7) },
						   { "DOUBLE", new Integer(8) },
						   { "NUMERIC", new Integer(2) },
						   { "DECIMAL", new Integer(3) },
						   { "CHAR", new Integer(1) },
						   { "VARCHAR", new Integer(12) },
						   { "LONGVARCHAR", new Integer(-1) },
						   { "DATE", new Integer(91) },
						   { "TIME", new Integer(92) },
						   { "TIMESTAMP", new Integer(93) },
						   { "BINARY", new Integer(-2) },
						   { "VARBINARY", new Integer(-3) },
						   { "LONGVARBINARY", new Integer(-4) },
						   { "NULL", new Integer(0) },
						   { "OTHER", new Integer(1111) },
						   { "JAVA_OBJECT", new Integer(2000) },
						   { "DISTINCT", new Integer(2001) },
						   { "STRUCT", new Integer(2002) },
						   { "ARRAY", new Integer(2003) },
						   { "BLOB", new Integer(2004) },
						   { "CLOB", new Integer(2005) },
						   { "REF", new Integer(2006) } };

  public static void init()
  {
    if ( type_codes_by_name == null )
    {
      type_codes_by_name = new Hashtable();
      type_names_by_code = new Hashtable();
      for ( int i = 0; i<sql_type_data.length; i++ )
      {
         type_codes_by_name.put((String)sql_type_data[i][0], (Integer)sql_type_data[i][1]);
         type_names_by_code.put((Integer)sql_type_data[i][1], (String)sql_type_data[i][0]);
      }
    }
  }

  public static Hashtable getTypeCodesByName()
  {
    if ( type_codes_by_name == null )
      init();

    return type_codes_by_name;
  }

  public static Hashtable getTypeNamesByCode()
  {
    if ( type_names_by_code == null )
      init();

    return type_names_by_code;
  }
}
