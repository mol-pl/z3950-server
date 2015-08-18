package com.k_int.sql.xml;

import java.util.Properties;
import org.w3c.dom.*;
import com.k_int.sql.data_dictionary.*;

/**
 *
 * @author Ian Ibbotson ( ian.ibbotson@k-int.com )
 * @version $Id: NodeAdapter.java,v 1.1 2004/10/26 11:38:36 ibbo Exp $
 */
public abstract class NodeAdapter implements org.w3c.dom.Node {

  private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(NodeAdapter.class.getName());

  /**
   */
  Node parent;
  
  /**
   */
  int ordinal;

  /**
   */
  public NodeAdapter(Node parent, int ordinal) {
    this.parent = parent;
    this.ordinal = ordinal;
  }

  /**
   * Unless otherwise defined, nodes do not have names.
   */
  public String getNodeName() {
    log.fine("getNodeName() returning null");
    return null;
  }

  /**
   * By default nodes do not have children.
   */
  public NodeList getChildNodes() {
    log.fine("getChildNodes()");

    return new NodeList() {
        public int getLength() { return 0; }
        public Node item(int index) { return null; }
      };
  }

  /**
   * By default nodes do not have attributes and should return null.
   */
  public NamedNodeMap getAttributes() {
    log.fine("getAttributes()");
    return null;
  }

  /**
   * Apparently null is acceptable here.
   */
  public String getNamespaceURI() {
    log.fine("getNamespaceURI()");
    return null;
  }

  /**
   * The docs say anything other than element and attribute should return null.
   */
  public String getLocalName() {
    log.fine("getLocalName() returning null");
    return null;
  }

  /**
   * Basic nodes have no children, spec says return null.
   */
  public Node getFirstChild() {
    log.fine("getFirstChild() returning null");
    return null;
  }

  /**
   * Basic nodes have no children, spec says return null.
   */
  public Node getLastChild() {
    log.fine("getLastChild()");
    return null;
  }

  /**
   * Whatever it is, the answer is no.
   */
  public boolean isSupported(String feature, String version) {
    log.fine("isSupported(\"" + feature + "\", \"" + version + "\") is false");
    return false;
  }

  /**
   */
  public Node getParentNode() {
    log.fine("getParentNode()");
    return this.parent;
  }

  /**
   * The node immediately preceding this node. If there is no such node,
   * this returns <code>null</code>.
   */
  public Node getPreviousSibling() {
    log.fine("getPreviousSibling()");
    if (this.ordinal <= 0)
      return null;
    else
      return this.parent.getChildNodes().item(this.ordinal - 1);
  }

  /**
   * The node immediately following this node. If there is no such node,
   * this returns <code>null</code>.
   */
  public Node getNextSibling() {
    log.fine("getNextSibling()");
    if (this.ordinal >= (this.parent.getChildNodes().getLength() - 1))
      return null;
    else
      return this.parent.getChildNodes().item(this.ordinal + 1);
  }

  /**
   * Always return null because we don't need no stinking owner document.
   */
  public Document getOwnerDocument() {
    log.fine("getOwnerDocument()");
    if(getNodeType() == DOCUMENT_NODE) { 
      return (Document) this; 
    } 
    else { 
      return getParentNode().getOwnerDocument(); 
    } 
  }
  
  /**
   * Returns whether this node has any children.
   * @return  <code>true</code> if this node has any children,
   *   <code>false</code> otherwise.
   */
  public boolean hasChildNodes() {
    log.fine("hasChildNodes() is false");
    return false;
  }

  /**
   * The value of this node, depending on its type; see the table above.
   * When it is defined to be <code>null</code>, setting it has no effect.
   * @exception DOMException
   *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
   * @exception DOMException
   *   DOMSTRING_SIZE_ERR: Raised when it would return more characters than
   *   fit in a <code>DOMString</code> variable on the implementation
   *   platform.
   */
  public void setNodeValue(String nodeValue) throws DOMException {
    System.out.println("NodeAdapter: UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }
  
  /**
   * Inserts the node <code>newChild</code> before the existing child node
   * <code>refChild</code>. If <code>refChild</code> is <code>null</code>,
   * insert <code>newChild</code> at the end of the list of children.
   * <br>If <code>newChild</code> is a <code>DocumentFragment</code> object,
   * all of its children are inserted, in the same order, before
   * <code>refChild</code>. If the <code>newChild</code> is already in the
   * tree, it is first removed.
   * @param newChildThe node to insert.
   * @param refChildThe reference node, i.e., the node before which the new
   *   node must be inserted.
   * @return The node being inserted.
   * @exception DOMException
   *   HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not
   *   allow children of the type of the <code>newChild</code> node, or if
   *   the node to insert is one of this node's ancestors.
   *   <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
   *   from a different document than the one that created this node.
   *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly or
   *   if the parent of the node being inserted is readonly.
   *   <br>NOT_FOUND_ERR: Raised if <code>refChild</code> is not a child of
   *   this node.
   */
  public Node insertBefore(Node newChild,
               Node refChild)
               throws DOMException {
    System.out.println("NodeAdapter: UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  /**
   * Replaces the child node <code>oldChild</code> with <code>newChild</code>
   *  in the list of children, and returns the <code>oldChild</code> node.
   * <br>If <code>newChild</code> is a <code>DocumentFragment</code> object,
   * <code>oldChild</code> is replaced by all of the
   * <code>DocumentFragment</code> children, which are inserted in the
   * same order. If the <code>newChild</code> is already in the tree, it
   * is first removed.
   * @param newChildThe new node to put in the child list.
   * @param oldChildThe node being replaced in the list.
   * @return The node replaced.
   * @exception DOMException
   *   HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not
   *   allow children of the type of the <code>newChild</code> node, or if
   *   the node to put in is one of this node's ancestors.
   *   <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
   *   from a different document than the one that created this node.
   *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node or the parent of
   *   the new node is readonly.
   *   <br>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child of
   *   this node.
   */
  public Node replaceChild(Node newChild,
               Node oldChild)
               throws DOMException {
    System.out.println("NodeAdapter: UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  /**
   * Removes the child node indicated by <code>oldChild</code> from the list
   * of children, and returns it.
   * @param oldChildThe node being removed.
   * @return The node removed.
   * @exception DOMException
   *   NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
   *   <br>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child of
   *   this node.
   */
  public Node removeChild(Node oldChild)
              throws DOMException {
    System.out.println("NodeAdapter: UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  /**
   * Adds the node <code>newChild</code> to the end of the list of children
   * of this node. If the <code>newChild</code> is already in the tree, it
   * is first removed.
   * @param newChildThe node to add.If it is a <code>DocumentFragment</code>
   *  object, the entire contents of the document fragment are moved
   *   into the child list of this node
   * @return The node added.
   * @exception DOMException
   *   HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not
   *   allow children of the type of the <code>newChild</code> node, or if
   *   the node to append is one of this node's ancestors.
   *   <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
   *   from a different document than the one that created this node.
   *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
   */
  public Node appendChild(Node newChild)
              throws DOMException {
    System.out.println("NodeAdapter: UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }



  /**
   * Returns a duplicate of this node, i.e., serves as a generic copy
   * constructor for nodes. The duplicate node has no parent; (
   * <code>parentNode</code> is <code>null</code>.).
   * <br>Cloning an <code>Element</code> copies all attributes and their
   * values, including those generated by the XML processor to represent
   * defaulted attributes, but this method does not copy any text it
   * contains unless it is a deep clone, since the text is contained in a
   * child <code>Text</code> node. Cloning an <code>Attribute</code>
   * directly, as opposed to be cloned as part of an <code>Element</code>
   * cloning operation, returns a specified attribute (
   * <code>specified</code> is <code>true</code>). Cloning any other type
   * of node simply returns a copy of this node.
   * <br>Note that cloning an immutable subtree results in a mutable copy,
   * but the children of an <code>EntityReference</code> clone are readonly
   * . In addition, clones of unspecified <code>Attr</code> nodes are
   * specified. And, cloning <code>Document</code>,
   * <code>DocumentType</code>, <code>Entity</code>, and
   * <code>Notation</code> nodes is implementation dependent.
   * @param deepIf <code>true</code>, recursively clone the subtree under
   *   the specified node; if <code>false</code>, clone only the node
   *   itself (and its attributes, if it is an <code>Element</code>).
   * @return The duplicate node.
   */
  public Node cloneNode(boolean deep)
  {
    System.out.println("NodeAdapter: UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  /**
   * Puts all <code>Text</code> nodes in the full depth of the sub-tree
   * underneath this <code>Node</code>, including attribute nodes, into a
   * "normal" form where only structure (e.g., elements, comments,
   * processing instructions, CDATA sections, and entity references)
   * separates <code>Text</code> nodes, i.e., there are neither adjacent
   * <code>Text</code> nodes nor empty <code>Text</code> nodes. This can
   * be used to ensure that the DOM view of a document is the same as if
   * it were saved and re-loaded, and is useful when operations (such as
   * XPointer  lookups) that depend on a particular document tree
   * structure are to be used.In cases where the document contains
   * <code>CDATASections</code>, the normalize operation alone may not be
   * sufficient, since XPointers do not differentiate between
   * <code>Text</code> nodes and <code>CDATASection</code> nodes.
   * @version DOM Level 2
   */
  public void normalize()
  {
    System.out.println("NodeAdapter: UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  /**
   * The namespace prefix of this node, or <code>null</code> if it is
   * unspecified.
   * <br>Note that setting this attribute, when permitted, changes the
   * <code>nodeName</code> attribute, which holds the qualified name, as
   * well as the <code>tagName</code> and <code>name</code> attributes of
   * the <code>Element</code> and <code>Attr</code> interfaces, when
   * applicable.
   * <br>Note also that changing the prefix of an attribute that is known to
   * have a default value, does not make a new attribute with the default
   * value and the original prefix appear, since the
   * <code>namespaceURI</code> and <code>localName</code> do not change.
   * <br>For nodes of any type other than <code>ELEMENT_NODE</code> and
   * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1
   * method, such as <code>createElement</code> from the
   * <code>Document</code> interface, this is always <code>null</code>.
   * @exception DOMException
   *   INVALID_CHARACTER_ERR: Raised if the specified prefix contains an
   *   illegal character.
   *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
   *   <br>NAMESPACE_ERR: Raised if the specified <code>prefix</code> is
   *   malformed, if the <code>namespaceURI</code> of this node is
   *   <code>null</code>, if the specified prefix is "xml" and the
   *   <code>namespaceURI</code> of this node is different from "
   *   http://www.w3.org/XML/1998/namespace", if this node is an attribute
   *   and the specified prefix is "xmlns" and the
   *   <code>namespaceURI</code> of this node is different from "
   *   http://www.w3.org/2000/xmlns/", or if this node is an attribute and
   *   the <code>qualifiedName</code> of this node is "xmlns" .
   * @since DOM Level 2
   */
  public String getPrefix()
  {
    System.out.println("NodeAdapter: UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public void setPrefix(String prefix) throws DOMException
  {
    System.out.println("NodeAdapter: UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  /**
   * Returns whether this node (if it is an element) has any attributes.
   * @return <code>true</code> if this node has any attributes,
   *   <code>false</code> otherwise.
   * @since DOM Level 2
   */
  public boolean hasAttributes()
  {
    System.out.println("NodeAdapter: UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public Object setUserData(String key, Object data, UserDataHandler handler) {
    return null;
  }

  public Object getUserData(String key) {
    return null;
  }

  public Object getFeature(String feature, String version) {
    return null;
  }

  public boolean isEqualNode(Node arg) {
    return isSameNode(arg);
  }

  public String lookupNamespaceURI(String prefix) {
    return null;
  }

  public boolean isDefaultNamespace(String namespace_uri) {
    return true;
  }

  public String lookupPrefix(String namespace_uri) {
    return null;
  }

  public boolean isSameNode(Node other) {
    return false;
  }

  public void setTextContent(String text_content) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"Unsupported");
  }

  public String getTextContent() throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"Unsupported");
  }
    
  public short compareDocumentPosition(Node other) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"Unsupported");
  }

  public String getBaseURI() {
    return null;
  }
}
