/**
 * Title:       DirectRefFormatProperty
 * @version:    $Id: DirectRefFormatProperty.java,v 1.1.1.1 2004/06/18 06:38:17 ibbo Exp $
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

import java.io.Serializable;

public class DirectRefFormatProperty extends FormatProperty implements Serializable {
  String ref = null;

  public DirectRefFormatProperty(String direct_ref) {
    this.ref = direct_ref;
  }

  public String toString() {
    return ref;
  }

  public String getRef() {
    return ref;
  }

  public boolean equals(Object p) {
    if ( p instanceof DirectRefFormatProperty ) {
      DirectRefFormatProperty comp = (DirectRefFormatProperty)p;
      if ( ( ( ref == null ) && ( comp.getRef() == null ) ) || 
           ( ( ref != null ) && ( comp.getRef() != null ) && ( ref.equals(comp.getRef()) ) ) )
        return true;
    }

    return false;
  }
}
