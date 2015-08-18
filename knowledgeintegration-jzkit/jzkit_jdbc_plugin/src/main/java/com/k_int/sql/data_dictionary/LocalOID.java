package com.k_int.sql.data_dictionary;

/**
 * Title:       OID
 * @version:    $Id: LocalOID.java,v 1.1 2004/10/24 15:18:31 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description:
 */

import java.util.Map;
import java.util.Iterator;
import java.util.StringTokenizer;

public class LocalOID {
  private String collection;
  private EntityKey keypairs = null;

  private final static int COLON=1;
  private final static int COMMA=2;
  private final static int EQUALS=3;
  private final static int COLLECTION_LITERAL=6;
  private final static int ATTR_LITERAL=7;
  private final static int VALUE_LITERAL=8;

  /** LocalOID Strings have the form collection:key1='val1',key2='val2',key3='val3'
  */

  public LocalOID(String oid) {
    keypairs = new EntityKey();

    int expect = COLLECTION_LITERAL;
    String temp = null;

    StringTokenizer st = new StringTokenizer(oid,":,='", true);
    while (st.hasMoreTokens()) {
      switch ( expect ) {
        case COLLECTION_LITERAL:
          collection = st.nextToken();
	  // System.err.println("collection="+collection);
	  // Consume :
	  st.nextToken();
          expect = ATTR_LITERAL;
          break;
        case ATTR_LITERAL:
          temp = st.nextToken("=");
	  // System.err.println("attrname="+temp);
	  // Consume =
	  st.nextToken("=");
          expect = VALUE_LITERAL;
          break;
        case VALUE_LITERAL:
	  String val = st.nextToken("',");
	  if ( val.equals("'") ) {
	    val=st.nextToken("'");
	    st.nextToken();
	  }
          // System.err.println("value="+val);
          keypairs.addKeyComponent(temp, val);
          expect = COMMA;
	  break;
        case COMMA:
	  // System.err.println("Get comma...");
	  st.nextToken(",");
	  // System.err.println("Expect attr...");
          expect = ATTR_LITERAL;
          break;
        default:
	  // System.err.println("Expect something????");
          st.nextToken(":,='");
          break;
        }
      }
    }

    public LocalOID ( String collection, EntityKey keypairs ) {
        this.collection=collection;
        this.keypairs = keypairs;
    }

    public String getCollection() {
        return collection;
    }

    public EntityKey getKeyPairs() {
        return keypairs;
    }

    public Map getKey() {
      return keypairs.getKeyMap();
    }

    public String toString() {
        StringBuffer retval = new StringBuffer();
        retval.append(collection);
        retval.append(":");
        retval.append(keypairs.toString());

        return retval.toString();
    }

    public boolean hasKeyAttributes() {
        if ( keypairs.numComponents() > 0 )
            return true;

        return false;
    }

    public boolean equals(Object obj) {
        if ( obj instanceof OID ) {
            OID o = (OID)obj;

            if ( ( keypairs.toString().equals(o.getKeyPairs().toString())) &&
                 ( collection.equals(o.getCollection()) ) )
                return true;
            else
                return false;
        }
        else {
                return false;
        }
    }

    public int hashCode()
    {
        return toString().hashCode();
    }
}
