// Title:       JDBCRecord
// @version:    $Id: JDBCRecord.java,v 1.3 2004/10/26 16:42:23 ibbo Exp $
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
import java.util.logging.*;
import java.util.Map;
import java.util.HashMap;

import com.k_int.sql.data_dictionary.*;

/**
 * JDBCRecord
 */
public class JDBCRecord implements InformationFragment, java.io.Serializable
{
  private OID oid = null;
  private ExplicitRecordFormatSpecification spec = null;
  private Map additional_info = new HashMap();

  public JDBCRecord(OID oid, ExplicitRecordFormatSpecification spec) {
    this.oid=oid;
    if ( ( spec == null ) || ( spec.getSchema() == null ) ||  ( spec.getSchema().equals("") ) ) {
      // Need to create a new spec.. It's XML, we will use the root element and namespace as a schema
      // and there will be no element set name
      this.spec = new ExplicitRecordFormatSpecification("jdbc",oid.getCollection(),null);
    }
    else {
      this.spec = spec;
    }
  }

  // Get hold of the original (maybe blob) object
  public Object getOriginalObject()
  {
    return oid;
  }

  // Name of class for above object
  public String getOriginalObjectClassName()
  {
    return "Entity";
  }

  // We will use the root node to determine any applicable namespace

  // Get DOM Document for object
  public Document getDocument() {
    return null;
  }

  public String getDocumentSchema() {
    return oid.getCollection();
  }

  public String toString() {
    return oid.toString();
  }

  public String getSourceRepositoryID() {
    return oid.getRepository();
  }

  public String getSourceCollectionName() {
    return oid.getCollection();
  }
 
  public Object getSourceFragmentID() {
      return oid;
  } 

  public ExplicitRecordFormatSpecification getFormatSpecification() {
    return spec;
  }

  public void setFormatSpecification(ExplicitRecordFormatSpecification spec)
  {
    this.spec = spec;
  }
                                                                                                                                        
  public void setSourceRepositoryID(String id) {
  }
                                                                                                                                        
  public void setSourceCollectionName(String collection_name) {
  }
                                                                                                                                        
  public void setSourceFragmentID(Object id) {
  }

  /**
   *  Any extended record info such as hit highligting points, OAI Header record info etc that
   *  the search provider may return.
   */
  public Map getExtendedInfo() {
    return additional_info;
  }

}
