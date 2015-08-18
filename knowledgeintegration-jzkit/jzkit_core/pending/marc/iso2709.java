// Title:       iso2709
// @version:    $Id: iso2709.java,v 1.4 2005/03/01 23:20:21 ibbo Exp $
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


package org.jzkit.search.util.RecordModel.marc;

import org.jzkit.search.util.RecordModel.InformationFragment;
import org.jzkit.search.util.RecordModel.ExplicitRecordFormatSpecification;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.Serializable;
import org.marc4j.MarcHandler;
import org.marc4j.MarcReader;
import org.marc4j.MarcReaderException;
import org.marc4j.marc.*;
import org.marc4j.marcxml.*;
import org.marc4j.util.*;
import java.io.StringWriter;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * Helpful links for this class:
 * http://lcweb.loc.gov/marc/bibliographic/ecbdldrd.html
 * http://lcweb.loc.gov/marc/specifications/speccharucs.html
 * 
 */
public class iso2709 implements InformationFragment, Serializable {

  protected String source_repository = null;
  protected String source_collection_name = null;
  protected Object handle = null;
  protected byte[] source_record = null;
  private ExplicitRecordFormatSpecification spec = null;
  private Map additional_info = new HashMap();
  private long hit_no = 0;

 //  org.marc4j.marc.Record record = null;

  public iso2709(byte[] marc_record) {
    this.source_record = marc_record;
    // parseRecord();
  }
    
  public iso2709(String source_repository,
                 String source_collection_name, 
                 ExplicitRecordFormatSpecification spec,
                 Object handle,
                 Object source) {
    this.source_record=(byte[])source;
    this.handle = handle;
    this.source_collection_name = source_collection_name;
    this.source_repository = source_repository;
    this.spec = spec;

   //  parseRecord();
  }

  public ExplicitRecordFormatSpecification getFormatSpecification() {
    return spec;
  }

  public void setFormatSpecification(ExplicitRecordFormatSpecification spec) {
    this.spec = spec;
  }

  // Get hold of the original (maybe blob) object
  public Object getOriginalObject() {
    return source_record;
  }

  // Name of class for above object
  public String getOriginalObjectClassName() {
    return "byte[]";
  }

  // We will use the root node to determine any applicable namespace

  // Get DOM Document for object
  public Document getDocument() {
    org.w3c.dom.Document retval = null;
                                                                                                                                          
    DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
    dfactory.setNamespaceAware(true);
                                                                                                                                          
    try {
      // Source
      MarcXmlReader producer = new MarcXmlReader();
      org.xml.sax.InputSource in = new org.xml.sax.InputSource( new ByteArrayInputStream(this.source_record));
      javax.xml.transform.Source source = new javax.xml.transform.sax.SAXSource(producer, in);

      // Result
      javax.xml.transform.dom.DOMResult result = new javax.xml.transform.dom.DOMResult();

      // Transform
      org.marc4j.marcxml.Converter converter = new org.marc4j.marcxml.Converter();
      converter.convert(source, result);

      retval = (Document)(result.getNode());
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }

    return retval;
  }

  public String getDocumentSchema()
  {
    return spec.getSchema().toString();
  }

  public String toString() {
    StringWriter sw = new StringWriter();
    try {
      MarcReader reader = new MarcReader();
      TaggedWriter handler = new TaggedWriter(sw);
      MarcSource source = new MarcSource(reader, new ByteArrayInputStream(this.source_record));
      MarcResult result = new MarcResult();
      result.setHandler(handler);
      Converter converter = new Converter();
      converter.convert(source, result);
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }

    return sw.toString();
  }

  public void setSourceRepositoryID(String source_repository) {
    this.source_repository = source_repository;
  }

  public String getSourceRepositoryID() {
    return source_repository;
  }

  public void setSourceCollectionName(String source_collection_name) {
    this.source_collection_name = source_collection_name;
  }

  public String getSourceCollectionName() {
    return source_collection_name;
  }
 
  public void setSourceFragmentID(Object handle) {
    this.handle = handle;
  }

  public Object getSourceFragmentID() {
    return handle;
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
