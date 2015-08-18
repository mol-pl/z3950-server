/**
 * Title:       InformationFragment
 * @version:    $Id: InformationFragment.java,v 1.2 2004/09/30 14:45:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: A general interface that can be implemented by any object that
 *              whishes to participate in the information retrieval framework.
 *              Ideally, objects implementing this class will make the job of
 *              dealing with heterogeneous result items ( XML, GRS & Marc
 *              records, SUTRS, etc) eaiser.
 */

/*
 * $Log: InformationFragment.java,v $
 * Revision 1.2  2004/09/30 14:45:18  ibbo
 * Made RecordFormatSpecification base class for new Explicit and Archetype variants.
 *
 * Revision 1.1.1.1  2004/06/18 06:38:17  ibbo
 * Initial
 *
 * Revision 1.1.1.1  2004/03/08 15:25:38  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/12/05 16:30:43  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/11/16 15:10:42  ibbo
 * Initial import
 *
 * Revision 1.9  2002/10/02 09:25:48  ianibbo
 * Updated commenting.
 *
 * Revision 1.8  2002/07/16 14:37:47  ianibbo
 * All fragments now return a RecordFormatSpecification
 *
 */

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

import org.w3c.dom.Document;
import java.util.Map;

/**
 * InformationFragment: An interface that describes an object able to participate in
 * a retrieval operation. Ideally, objects implementing this class will make the job of
 * dealing with heterogeneous result items ( XML, GRS & Marc records, SUTRS, etc) eaiser.
 */
public interface InformationFragment {

  /// Get hold of the original (maybe blob) object
  public Object getOriginalObject();

  /**
   * Get DOM representation for this object... Should take account of schemas when translating & 
   * use namespaces where possible (E.G. gils, meta, generic namespace in converted GRS records)
   */
  // public Document getDocument();
  // public String getDocumentSchema();
  //

  /**
   * Describe the format of this fragment.. 
   */
  public ExplicitRecordFormatSpecification getFormatSpecification();
  public void setFormatSpecification(ExplicitRecordFormatSpecification spec);

  /** getSourceRepositoryID : return a string that identifies the repository from which this
   * record came. For example, "LC" might be used to identify the Library of Congress Z server.
   */
  public String getSourceRepositoryID();
  public void setSourceRepositoryID(String id);

  /** getSourceCollectionName: The ID of the collection from which the fragment came.
   * For example, z3950 database name 
   */
  public String getSourceCollectionName();
  public void setSourceCollectionName(String collection_name);

  /// For intergration with global handle system
  public Object getSourceFragmentID();
  public void setSourceFragmentID(Object id);

  /**
   *  Any extended record info such as hit highligting points, OAI Header record info etc that
   *  the search provider may return.
   */
  public Map getExtendedInfo();

  public long getHitNo();
  public void setHitNo(long hit_no);
}
