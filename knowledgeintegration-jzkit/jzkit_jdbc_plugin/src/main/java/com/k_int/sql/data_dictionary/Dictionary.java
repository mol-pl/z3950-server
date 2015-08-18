package com.k_int.sql.data_dictionary;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Title:       Dictionary
 * @version:    $Id: Dictionary.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * @author:     Ian Ibbotson
 * Description: this class holds information about the known types in a repository
 *              This implementation is focused upon describing entities in a relational database.
 */
public interface Dictionary {
  public EntityTemplate lookup(String entity_name) throws UnknownCollectionException;
}
