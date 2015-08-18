package com.k_int.sql.xml;
                                                                                                                                          
import org.w3c.dom.*;

/**
 * @author  Scott Hernandez
 */
public class DocumentAdapter extends NodeAdapter implements Document
{
  protected static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(DocumentAdapter.class.getName());

  /**
   */
  Element root;

  public DocumentAdapter(com.k_int.sql.data_dictionary.Entity root, String name ) {
    super(null, 0);
    System.err.println("Creating DocumentAdapter");
    this.root = new EntityElement(root, name, 0);
  }

  public short getNodeType() {
    System.err.println("getNodeType()");
    return DOCUMENT_NODE;
  }

  /**
   */
  public org.w3c.dom.Element getDocumentElement() {
    log.fine("getDocumentElement()");
    return this.root;
  }
  /**
   */
  public org.w3c.dom.Node getFirstChild() {
    log.fine("getFirstChild()");
    return this.root;
  }
  
  /**
   * hmmm....
   */
  public String getLocalName() {
    log.fine("getLocalName()");
    return root.getLocalName();
  }

  /**
   * Returns whether this node has any children.
   * @return  <code>true</code> if this node has any children,
   *   <code>false</code> otherwise.
   *
   * Document nodes always have children.
   */
  public boolean hasChildNodes() {
    System.err.println("documentElement hasChildNodes() is true");
    return true;
  }

  public org.w3c.dom.NodeList getElementsByTagNameNS(java.lang.String str, java.lang.String str1) {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.ProcessingInstruction createProcessingInstruction(java.lang.String str, java.lang.String str1) throws org.w3c.dom.DOMException {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.Element createElement(java.lang.String str) throws org.w3c.dom.DOMException {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.NodeList getElementsByTagName(java.lang.String str) {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.Attr createAttribute(java.lang.String str) throws org.w3c.dom.DOMException {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.Element createElementNS(java.lang.String str, java.lang.String str1) throws org.w3c.dom.DOMException {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.Element getElementById(java.lang.String str) {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.Text createTextNode(java.lang.String str) {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.CDATASection createCDATASection(java.lang.String str) throws org.w3c.dom.DOMException {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.EntityReference createEntityReference(java.lang.String str) throws org.w3c.dom.DOMException {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.Attr createAttributeNS(java.lang.String str, java.lang.String str1) throws org.w3c.dom.DOMException {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.DocumentType getDoctype() {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.DOMImplementation getImplementation() {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.Node importNode(org.w3c.dom.Node node, boolean param) throws org.w3c.dom.DOMException {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.DocumentFragment createDocumentFragment() {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public org.w3c.dom.Comment createComment(java.lang.String str) {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public String getNodeValue() throws org.w3c.dom.DOMException {
    System.out.println("UnsupportedOperationException Thrown");throw new UnsupportedOperationException();
  }

  public  Node renameNode(Node n, String namespace_uri, String qualified_name) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"Unsupported");
  }

  public void normalizeDocument() {
  }

  public DOMConfiguration getDomConfig() {
    return  null;
  }

  public Node adoptNode(Node source) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR,"Unsupported");
  }

  public void setDocumentURI(String document_uri) {
  }

  public String getDocumentURI() {
    return null;
  }

  public boolean getStrictErrorChecking() {
    return false;
  }

  public void setStrictErrorChecking(boolean strict_error_checking) {
  }

  public String getXmlVersion() {
    return null;
  }

  public void setXmlVersion(String xml_version) {
  }

  public boolean getXmlStandalone() {
    return true;
  }

  public void setXmlStandalone(boolean xml_standalone) {
  }

  public String getInputEncoding() {
    return null;
  }

  public String getXmlEncoding() {
    return null;
  }

}
