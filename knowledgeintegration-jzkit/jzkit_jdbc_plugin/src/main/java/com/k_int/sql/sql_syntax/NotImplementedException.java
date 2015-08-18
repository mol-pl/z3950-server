package com.k_int.sql.sql_syntax;

/**
 * Title:       NotImplementedException
 * Version:     $Id: NotImplementedException.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */

public class NotImplementedException extends RuntimeException
{
  public NotImplementedException(String reason)
  {
    super(reason);
  }
}
