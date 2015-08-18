/**
 * Title:       IRResultSetInfo
 * @version:    $Id: IRResultSetInfo.java,v 1.2 2005/02/18 08:57:34 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
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


package org.jzkit.search.util.ResultSet;

import java.util.List;

public class IRResultSetInfo implements java.io.Serializable {
  public String name;
  public int total_fragment_count;
  public int status;
  public List record_sources;
  public String source_type;
  public String source_data;
  public String last_message;


  public IRResultSetInfo(String name,
                         String source_type,
                         String source_data,
                         int total_fragment_count,
                         int status,
                         List record_sources) {
    this(name,source_type,source_data,total_fragment_count,status,record_sources,null);
  }

  public IRResultSetInfo(String name,
                         String source_type,
                         String source_data,
                         int total_fragment_count,
                         int status,
                         List record_sources,
                         String last_message) {
    this.name = name;
    this.total_fragment_count = total_fragment_count;
    this.status = status;
    this.record_sources = record_sources;
    this.source_type = source_type;
    this.source_data = source_data;
    this.last_message = last_message;
  }

  public IRResultSetInfo(String name,
                         int total_fragment_count,
                         int status,
                         List record_sources) {
    this(name,"UNKNOWN", null, total_fragment_count,status,record_sources);
  }

  public IRResultSetInfo(String name,
                         int total_fragment_count,
                         int status) {
    this(name, "UNKNOWN", null, total_fragment_count, status, null);
  }

  /** The name of this result set */
  public String getResultSetName() {
    return name;
  }

  /** The number of entries in this result set */
  public int getFragmentCount() {
    return total_fragment_count;
  }

  /** The result set status */
  public int getStatus() {
    return status;
  }

  /** A list of IRResultSetInfo objects for each contributing source (May be empty or null) */
  public List getSourceInfo() {
    return record_sources;
  }

  public String toString() {
    java.io.StringWriter result = new java.io.StringWriter();
    result.write("RS:"+name+",Type:"+source_type+",SrcInfo:"+source_data+",Count:"+total_fragment_count+",Status:"+IRResultSetStatus.getCode(status));

    if ( record_sources != null ) {
       result.write(" {\n");
       for ( java.util.Iterator i = record_sources.iterator(); i.hasNext(); ) {
         IRResultSetInfo child_info = (IRResultSetInfo)i.next();
         result.write(child_info.toString());
         if ( i.hasNext() ) {
           result.write(",\n");
         }
       }
       result.write("}");
    }
    return result.toString();
  }
}
