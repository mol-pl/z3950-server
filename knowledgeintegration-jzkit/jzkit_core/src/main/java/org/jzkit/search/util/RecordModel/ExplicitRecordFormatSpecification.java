/**
 * Title:       ExplicitRecordFormatSpecification
 * @version:    $Id: ExplicitRecordFormatSpecification.java,v 1.1 2004/09/30 14:45:18 ibbo Exp $
 * Copyright:   Copyright (C) 2002 Knowledge Integration Ltd (See the COPYING file for details.)
 * @author:     Ian Ibbotson ( ian.ibbotson@k-int.com )
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

package org.jzkit.search.util.RecordModel;

import org.jzkit.a2j.codec.util.*;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExplicitRecordFormatSpecification extends RecordFormatSpecification {

  private FormatProperty encoding = null;  // Format_name becomes encoding
  private FormatProperty schema = null;
  private FormatProperty setname = null;
  private String string_rep = null;

  private transient static final String full_setname = "f";
  private static Log log = LogFactory.getLog(ExplicitRecordFormatSpecification.class);

  public ExplicitRecordFormatSpecification(String rfs) {
    // Passed in a string in the format FORMAT:SCHEMA:SETNAME.. Parse it
    String[] components = rfs.split(":");

    if ( components[0] != null )
      this.encoding = new DirectRefFormatProperty(components[0]);    // eg XML
    if ( components[1] != null )
      this.schema = new DirectRefFormatProperty(components[1]);                  // eg EAD
    if ( components[2] != null )
      this.setname = new DirectRefFormatProperty(components[2]);                // eg TPGRS
    this.string_rep = rfs;

    // OIDRegisterEntry entry = reg.lookupByName(components[0]);
    
    // if ( entry == null )
    // {
    //   log.warning("Unkown record format: "+components[0]+" default to xml");
    //   entry = reg.lookupByName("xml");
    // }
  }

  public ExplicitRecordFormatSpecification(String encoding, String schema, String setname) {
    // OIDRegisterEntry entry = reg.lookupByName(format);
   //  
    // if ( entry == null )
    // {
    //   log.warning("Unkown record format: "+format+" default to xml");
    //   entry = reg.lookupByName("xml");
    // }

    // this.format = entry;
    // this.encoding = new DirectRefFormatProperty(entry.getName());    // eg XML
    if ( encoding != null )
      this.encoding = new DirectRefFormatProperty(encoding);                  // eg EAD
    if ( schema != null )
      this.schema = new DirectRefFormatProperty(schema);                  // eg EAD
    if ( setname != null )
      this.setname = new DirectRefFormatProperty(setname);                // eg TPGRS

    this.string_rep = ( encoding != null ? encoding : "" ) +":"+ ( schema != null ? schema : "" ) +":"+ (setname != null ? setname : "" );
  }

  public ExplicitRecordFormatSpecification(FormatProperty encoding,
		                   FormatProperty schema,
				   FormatProperty element_set_name) {
    this.encoding = encoding;
    this.schema = schema;
    this.setname = element_set_name;

    // if ( encoding instanceof DirectRefFormatProperty )
    //   this.format = reg.lookupByName(encoding.toString());

    this.string_rep = encoding+":"+schema+":"+element_set_name;
  }

  // public OIDRegisterEntry getFormat()
  // {
  //   return format;
  // }

  public FormatProperty getEncoding() {
    return encoding;
  }

  public FormatProperty getSchema() {
    return schema;
  }

  public FormatProperty getSetname() {
    return setname;
  }

  public String toString() {
    return string_rep;
  }

  public int hashCode() {
    return string_rep.hashCode();
  }

  public boolean equals(Object o) {
    if ( o instanceof ExplicitRecordFormatSpecification ) {
      ExplicitRecordFormatSpecification comp = ( ExplicitRecordFormatSpecification ) o;

      if ( compare(this.encoding,comp.getEncoding() ) &&   // EG, XML, String, GRS
           compare(this.schema,comp.getSchema() ) &&       // OAI_DC, GILS, ETC
           compare(this.setname,comp.getSetname() ) )      // F,B,etc.
      return true;
    }

    return false;
  }

  private boolean compare(FormatProperty f1, FormatProperty f2) {
    if ( ( ( f1 == null ) && ( f2 == null ) ) ||
         ( ( f1 != null ) && ( f2 != null ) && f1.equals(f2) ) )
      return true;

    return false;
  }
}
