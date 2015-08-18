// Title:       DelayedCollectionElement
// @version:    $Id: ValueElement.java,v 1.1 2004/10/26 11:38:36 ibbo Exp $
// Copyright:   Copyright (C) 1999-2001 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
// Company:     KI
// Description: 
//


//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation; either version 2.1 of
// the license, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite
// 330, Boston, MA  02111-1307, USA.
// 

package com.k_int.sql.xml;

import java.util.Properties;
import org.w3c.dom.*;
import com.k_int.sql.data_dictionary.*;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Ian Ibbotson ( ian.ibbotson@k-int.com )
 * @version $Id: ValueElement.java,v 1.1 2004/10/26 11:38:36 ibbo Exp $
 */
public class ValueElement extends ElementAdapter {

  private static java.util.logging.Logger log = java.util.logging.Logger.getLogger("com.k_int.util.conv");
  private Object value;

  protected class ValueNodeList implements NodeList {
    /** Cache the node */
    protected Node value_node;
                                                                                                                                                         
    /** */
    public ValueNodeList() {
    }

    /** */
    public int getLength() {
      return 1;
    }

    /** */
    public Node item(int index) {
      if (value_node == null)
        value_node = new TextAdapter(value.toString(), ValueElement.this, index);
      return value_node;
    }
  }

  public ValueElement( Node parent_node, 
                       java.lang.String name, 
                       Object value,
                       int ordinal) {
    super(parent_node, name, ordinal);
    this.value = value;
  }

  protected NodeList createNodeListAdapter() {
    return new ValueNodeList();
  }
}
