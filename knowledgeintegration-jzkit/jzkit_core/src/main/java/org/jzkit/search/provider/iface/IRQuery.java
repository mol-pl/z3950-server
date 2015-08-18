/**
 * Title:       IRQuery
 * @version:    $Id: IRQuery.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
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

package org.jzkit.search.provider.iface;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import org.jzkit.search.util.QueryModel.QueryModel;

/**
 * A Generic Information Retrieval Query
 */
public class IRQuery
{
  /** The string used to syntax used to indicate that the query object contains a PREFIX string */
  private static final String SORT_SYNTAX_PREFIX = "RPN_TREE";

  /** The collections to be searched */
  public List collections = new ArrayList();

  /** A model of what the search is actually trying to find. Whatever the class is,
      it must implement the QueryModel interface so other components can convert the
      syntax of the expression into an RPN Tree. Default class will be PrefixString. */
  public QueryModel query = null;

  /** What kind of object is the sorting instruction */
  public String sorting_syntax = SORT_SYNTAX_PREFIX;

  /** Requested sorting instruction */
  public Object sorting = null;

  /** Hints might contain instructions on where to look for an answer, or the
      general subject area of the enquiry so that we can try and decide on some
      resources to route this particular enquiry to. */
  public Hashtable hints = new Hashtable();

  public IRQuery() {
  }

  public QueryModel getQueryModel() {
    return query;
  }

  public void setQueryModel(QueryModel query) {
    this.query = query;
  }

  public List getCollections() {
    return collections;
  }

  public void setCollections(List collections) {
    this.collections = collections;
  }

  public IRQuery(QueryModel query, String[] colls) {
    this.query = query;
    for ( int i=0; i<colls.length; i++ )
      collections.add(colls[i]);
  }

  public IRQuery(QueryModel query, String coll) {
    this.query = query;
    if ( coll != null ) {
      String[] colls = coll.split(",");
      for ( int i=0; i<colls.length; i++ ) {
        collections.add(colls[i]);
      }
    }
  }

  /** 
   * Construct a new query.
   */
  public IRQuery(QueryModel query, List colls) {
    this.query = query;
    this.collections = colls;
  }
}
