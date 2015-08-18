// Title:       XMLRecord
// @version:    $Id: XMLRecord.java,v 1.2 2004/09/30 14:45:18 ibbo Exp $
// Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ibbo@k-int.com)
// Company:     Knowledge Integration Ltd.
// Description: 

//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2.1 of
// the license, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite
// 330, Boston, MA  02111-1307, USA.
// 


package org.jzkit.search.util.RecordModel;

// import org.jzkit.SearchProvider.iface.*;
import org.w3c.dom.*;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import javax.xml.parsers.*;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * XMLRecord. Use DOMTree if you want to create a fragment from a Document!
 */
public class XMLRecord implements InformationFragment, Serializable {

  private static Log log = LogFactory.getLog(XMLRecord.class);
  private transient static DocumentBuilderFactory dfactory = null;

  private String source_repository = null;
  private String source_collection_name = null;
  private Object handle = null;
  private String orig = null;
  private ExplicitRecordFormatSpecification spec = null;
  private Map additional_info = new HashMap();

  static {
    dfactory = DocumentBuilderFactory.newInstance();
    dfactory.setNamespaceAware(true);
    dfactory.setValidating(false);
    // Check http://xml.apache.org/xerces2-j/features.html for features
    dfactory.setAttribute("http://xml.org/sax/features/validation",Boolean.FALSE);
    // dfactory.setAttribute("http://apache.org/xml/features/validation/schema",Boolean.FALSE);
    dfactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd",Boolean.FALSE);
  }

  /** The source schema of the record */
  private Document doc = null;

  public XMLRecord(String source) {
    log.debug("New XMLRecord");

    this.orig=source;
    this.spec = new ExplicitRecordFormatSpecification("xml",deriveSchemaFromDoc(),null);
  }

  public XMLRecord(String source_repository,
		   String source_collection_name,
                   Object handle,
                   String source,
                   ExplicitRecordFormatSpecification  spec) {
    this.source_repository = source_repository;
    this.source_collection_name = source_collection_name;
    this.handle = handle;
    this.orig=source;
    this.spec=spec;

    if ( ( spec == null ) || ( spec.getSchema() == null ) ||  ( spec.getSchema().equals("") ) ) {
      // Need to create a new spec.. It's XML, we will use the root element and namespace as a schema
      // and there will be no element set name
      this.spec = new ExplicitRecordFormatSpecification("xml",deriveSchemaFromDoc(),null);
    }
  }

  private String deriveSchemaFromDoc() {
    String result = null;

    try {
      Document d = getDocument();
      Element root_element = d.getDocumentElement();
      String root_tag = root_element.getTagName();
      // String tag_prefix = root_element.getPrefix();
      String tag_namespace_uri = root_element.getNamespaceURI();
      // log.debug("root_tag: "+root_tag);
      // log.debug("tag_prefix: "+tag_prefix);
      // log.debug("tag_namespace_uri: "+tag_namespace_uri);

      // If tag_namespace_uri we need to resolve it into a local name and then look up a canonical single
      // name for the schema. For now, we will just default to the root tag, but that is a bit pants.
      // result = ( tag_namespace_uri != null ? tag_namespace_uri+":"+root_tag : root_tag );
      result = root_tag;
    }
    catch ( Exception e ) {
      log.debug("Problem: "+e);
    }

    return result;
  }

  // Get hold of the original (maybe blob) object
  public Object getOriginalObject() {
    return orig;
  }

  // Name of class for above object
  public String getOriginalObjectClassName() {
    return "Document";
  }

  // We will use the root node to determine any applicable namespace

  // Get DOM Document for object
  public Document getDocument() {
    if ( ( doc == null ) && ( orig != null ) ) {
      // DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      // dfactory.setNamespaceAware(true);
      // dfactory.setValidating(false);
      // Experimental for ANG Project
      // Don't expand entity references.
      // dfactory.setExpandEntityReferences(false);
      // Wonder if there is a way to get case-insensitive tag parsers?

      try {
        // log.debug("Parse record:" +orig);
        DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
        doc = docBuilder.parse(new ByteArrayInputStream(orig.getBytes()));
      }
      catch ( ParserConfigurationException pce ) {
        log.warn(pce.toString(),pce);
      }
      catch ( org.xml.sax.SAXException saxe ) {
        log.warn(saxe.toString(),saxe);
      }
      catch ( java.io.IOException ioe ) {
	log.warn(ioe.toString(),ioe);
      }
    }

    return doc;
  }

  public String getDocumentSchema() {
    // return getDocument().getDocumentElement().getNodeName();
    DocumentType doctype = getDocument().getDoctype();

    if ( doctype != null ) {
      return doctype.getSystemId();  // Or getPublicId()
    }

    return getDocument().getDocumentElement().getNodeName();
  }

  public String toString() {
    return orig;
  }

  public String getSourceRepositoryID() {
    return source_repository;
  }

  public String getSourceCollectionName() {
    return source_collection_name;
  }
 
  public Object getSourceFragmentID() {
    if ( handle != null )
      return handle;
    else
      return null;
  } 

  public ExplicitRecordFormatSpecification getFormatSpecification() {
    return spec;
  }

  public void setFormatSpecification(ExplicitRecordFormatSpecification spec) {
    this.spec = spec;
  }
                                                                                                                                        
  public void setSourceRepositoryID(String id) {
    this.source_repository = id;
  }
                                                                                                                                        
  public void setSourceCollectionName(String collection_name) {
    this.source_collection_name = collection_name;
  }
                                                                                                                                        
  public void setSourceFragmentID(Object id) {
    this.handle = id;
  }

  /**
   *  Any extended record info such as hit highligting points, OAI Header record info etc that
   *  the search provider may return.
   */
  public Map getExtendedInfo() {
    return additional_info;
  }

  long hit_no;

  public long getHitNo() {
    return hit_no;
  }

  public void setHitNo(long hit_no) {
    this.hit_no = hit_no;
  }

}
