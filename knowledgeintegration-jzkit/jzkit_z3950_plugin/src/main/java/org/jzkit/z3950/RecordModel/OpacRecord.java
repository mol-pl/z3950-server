// Title:       GRS1 Information Fragment... Holder for a GRS record
// @version:    $Id: OpacRecord.java,v 1.4 2005/10/22 14:01:11 ibbo Exp $
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


package org.jzkit.z3950.RecordModel;

// import org.jzkit.SearchProvider.iface.*;
import org.w3c.dom.Document;
import org.w3c.dom.*;
// import org.apache.xml.serialize.OutputFormat;
// import org.apache.xml.serialize.XMLSerializer;
import java.io.*;

// for OID Register
import org.jzkit.a2j.codec.util.*;

import org.jzkit.z3950.gen.v3.RecordSyntax_opac.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.a2j.gen.AsnUseful.EXTERNAL_type;
import javax.xml.parsers.*;

import java.util.Map;
import java.util.HashMap;

public class OpacRecord implements InformationFragment, Serializable, Z3950RetrievalRecord
{
  private String source_repository = null;
  private String source_collection_name = null;
  private String orig_schema = null;
  private Object handle = null;
  private OPACRecord_type root_node = null;
  private Document record_as_dom = null;
  private ExplicitRecordFormatSpecification spec = null;
  private transient static DocumentBuilderFactory dfactory = null;
  private Map additional_info = new HashMap();
  private long hit_no;

  static {
    dfactory = DocumentBuilderFactory.newInstance();
    dfactory.setNamespaceAware(true);
    dfactory.setValidating(false);
    dfactory.setAttribute("http://xml.org/sax/features/validation",Boolean.FALSE);
    dfactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd",Boolean.FALSE);
  }



  public OpacRecord(Object root_node) {
    this.root_node=(OPACRecord_type)root_node;
  }

  public OpacRecord(String source_repository,
		    String source_collection_name, 
                    Object handle,
                    Object root_node,
		    ExplicitRecordFormatSpecification spec) {
    this.source_repository = source_repository;
    this.source_collection_name = source_collection_name;
    this.handle = null;
    this.root_node=(OPACRecord_type)root_node;
    this.spec = spec;
  } 

  // Get hold of the original (maybe blob) object
  public Object getOriginalObject() {
    return root_node;
  }

  // Name of class for above object
  public String getOriginalObjectClassName() {
    return "org.jzkit.gen.RecordSyntax_opac.OPACRecord_type";
  }

  // We will use the root node to determine any applicable namespace

  // Get DOM Document for object
  public Document getDocument() {
   if ( record_as_dom == null ) {
      try {
        DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
        record_as_dom = docBuilder.newDocument();
        Element root = record_as_dom.createElement("OpacRecord");
        record_as_dom.appendChild( root );
        convert(record_as_dom, root, root_node);
      }
      catch ( ParserConfigurationException pce ) {
        pce.printStackTrace();
      }
    }

    return record_as_dom;
  }

  public String getDocumentSchema() {
    if ( record_as_dom == null )
      getDocument();

    // Default to plain GRS-1 as a schema
    if ( orig_schema == null )
      orig_schema = "grs-1";

    return orig_schema;
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

  private void convert(Document doc, Element parent, OPACRecord_type record) {
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

  public EXTERNAL_type getRecordEncoding() {
    return null;
  }

  /**
   *  Any extended record info such as hit highligting points, OAI Header record info etc that
   *  the search provider may return.
   */
  public Map getExtendedInfo() {
    return additional_info;
  }

  public long getHitNo() {
    return hit_no;
  }

  public void setHitNo(long hit_no) {
    this.hit_no = hit_no;
  }

}
