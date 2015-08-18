// Title:       EntityElement
// @version:    $Id: ElementAdapter.java,v 1.2 2004/10/26 15:30:52 ibbo Exp $
// Copyright:   Copyright (C) 1999-2001 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
// Company:     KI
// Description: 
//


//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation; either version 2.1 of
// the license, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite
// 330, Boston, MA  02111-1307, USA.
// 

package com.k_int.sql.xml;

import java.util.Properties;
import org.w3c.dom.*;

/**
 *
 * A Realisation of a DOM Element that acts as a delagate for an
 * Entity object. These elements should ***ONLY*** be accessed as
 * a part of XSLT transformations, since they hold within themselves
 * the possibility of enumerating the entire source database!
 *
 * @author Ian Ibbotson ( ian.ibbotson@k-int.com )
 * @version $Id: ElementAdapter.java,v 1.2 2004/10/26 15:30:52 ibbo Exp $
 */
public abstract class ElementAdapter extends NodeAdapter implements org.w3c.dom.Element
{
  /**
   *  List of child elements.. Combination of values, collections and link attributes
   */
  protected NodeList node_list = null;


  /**
   */
  String nodeName;

  public ElementAdapter(Node parent, String nodeName, int ordinal) {
    super(parent,ordinal);
    this.nodeName=nodeName;
    System.err.println("new element: "+nodeName);
  }

  /**
   */
  public short getNodeType()
  {
    return ELEMENT_NODE;
  }

  /**
   */
  public String getNodeName()
  {
    return this.nodeName;
  }

  /**
   * Element nodes do not have node values.
   */
  public String getNodeValue() throws DOMException
  {
    return null;
  }

  /**
   * Introspect our object and see what child nodes are appropriate.
   */
  public NodeList getChildNodes()
  {
    System.err.println("getChildNodes()");
    return this.getNodeListAdapter();
  }

  /**
   */
  public NamedNodeMap getAttributes()
  {
    return new NamedNodeMapUnimplemented() {
      public int getLength() { return 0; }
    };

  }

  public String getLocalName() {
    return this.getNodeName();
  }

  public Node getFirstChild() {
    NodeList list = this.getNodeListAdapter();

    if (list.getLength() > 0)
      return this.getNodeListAdapter().item(0);
    else
      return null;
  }

  public Node getLastChild() {
    NodeList list = this.getNodeListAdapter();

    if (list.getLength() > 0)
      return list.item(list.getLength()-1);
    else
      return null;
  }
  
  /**
   */
  public boolean hasChildNodes() {
    NodeList list = this.getNodeListAdapter();

    if (list.getLength() > 0)
         return true;
     else
         return false;
  }
  
  public String getTagName() {
    throw new UnsupportedOperationException();
  }

  public String getAttribute(String name) {
    throw new UnsupportedOperationException();
  }


  public void setAttribute(String name, String value) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public void removeAttribute(String name) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public Attr getAttributeNode(String name) {
    throw new UnsupportedOperationException();
  }

  public Attr setAttributeNode(Attr newAttr) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public NodeList getElementsByTagName(String name) {
    throw new UnsupportedOperationException();
  }

  public String getAttributeNS(String namespaceURI, String localName) {
    throw new UnsupportedOperationException();
  }

  public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public Attr getAttributeNodeNS(String namespaceURI, String localName) {
    throw new UnsupportedOperationException();
  }

  public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
    throw new UnsupportedOperationException();
  }

  public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
    throw new UnsupportedOperationException();
  }

  public boolean hasAttribute(String name) {
    throw new UnsupportedOperationException();
  }

  public boolean hasAttributeNS(String namespaceURI, String localName) {
    throw new UnsupportedOperationException();
  }

  public TypeInfo getSchemaTypeInfo() {
    return null;
  }

  public void setIdAttribute(String name, boolean is_id) throws DOMException {
  }

  public void setIdAttributeNS(String namespace_uri,String name, boolean is_id) throws DOMException {
  }

  public void setIdAttributeNode(Attr idAttr, boolean is_id) throws DOMException {
  }

  protected NodeList getNodeListAdapter() {
    if (this.node_list == null) {
      this.node_list = createNodeListAdapter();
    }
    return this.node_list;
  }
    
  protected abstract NodeList createNodeListAdapter();
}
