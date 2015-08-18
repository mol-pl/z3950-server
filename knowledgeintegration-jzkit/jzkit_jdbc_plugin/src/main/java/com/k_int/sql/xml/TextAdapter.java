package com.k_int.sql.xml;

import org.w3c.dom.*;

/**
 */
public class TextAdapter extends NodeAdapter implements Text {

  protected static final char CR = 0x0d;
  protected static final char LF = 0x0a;

  protected String wrapped;

  protected TextAdapter(String wrapme, Node parent, int ordinal) {
    super(parent, ordinal);
    this.wrapped = wrapme;
  }

  public short getNodeType() {
    return TEXT_NODE;
  }

  public String getNodeName() {
    return "#text";
  }

  public String getNodeValue() throws DOMException {
    return this.wrapped;
  }

  public String getData() throws DOMException {
    return this.wrapped;
  }

  public Text splitText(int offset) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public void setData(String data) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public int getLength() {
    throw new UnsupportedOperationException();
  }

  public String substringData(int offset, int count) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public void appendData(String arg) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public void insertData(int offset, String arg) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public void deleteData(int offset, int count) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public void replaceData(int offset, int count, String arg) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public String getWholeText() {
    throw new UnsupportedOperationException();
  }

  public Text replaceWholeText(String text) {
    throw new UnsupportedOperationException();
  }

  public boolean isElementContentWhitespace() {
    throw new UnsupportedOperationException();
  }
}
