package com.k_int.sql.data_dictionary;

/** 
 * Title: PersistenceException
 * @version: $Id: PersistenceException.java,v 1.1 2004/10/26 11:38:36 ibbo Exp $
 * Copyright:   
 * @author: Ian Ibbotson
 * Company:     
 * Description:
 */
public class  PersistenceException extends java.lang.Exception
{
    public PersistenceException() {
    }

    public PersistenceException( String s ) {
        super( s );
    }

    public PersistenceException( String s, Throwable t ) {
        super( s, t );
    }
}
