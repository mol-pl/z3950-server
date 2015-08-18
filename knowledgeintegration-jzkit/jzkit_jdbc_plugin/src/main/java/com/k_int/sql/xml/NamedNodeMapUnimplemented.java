/*
 * $Id: NamedNodeMapUnimplemented.java,v 1.1 2004/10/26 11:38:36 ibbo Exp $
 * $Source: /home/cvs/developer/jzkit2/jzkit2_jdbc_plugin/src/java/com/k_int/sql/xml/NamedNodeMapUnimplemented.java,v $
 */

package com.k_int.sql.xml;

import org.w3c.dom.*;

/**
 */
class NamedNodeMapUnimplemented implements NamedNodeMap {
  /** */
  public Node getNamedItem(String name) {
    throw new UnsupportedOperationException();
  }

  /** */
  public Node setNamedItem(Node arg) throws DOMException {
    throw new UnsupportedOperationException();
  }

  /** */
  public Node removeNamedItem(String name) throws DOMException {
    throw new UnsupportedOperationException();
  }

  /** */
  public Node item(int index) {
    throw new UnsupportedOperationException();
  }

  /** */
  public int getLength() {
    throw new UnsupportedOperationException();
  }

  /** */
  public Node getNamedItemNS(String namespaceURI, String localName) {
    throw new UnsupportedOperationException();
  }

  /** */
  public Node setNamedItemNS(Node arg) throws DOMException {
    throw new UnsupportedOperationException();
  }

  /** */
  public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
    throw new UnsupportedOperationException();
  }
}
