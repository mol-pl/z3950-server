// Title:       DelayedCollectionElement
// @version:    $Id: DelayedCollectionElement.java,v 1.2 2004/10/26 15:30:52 ibbo Exp $
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
 *
 * A Realisation of a DOM Element that acts as a delagate for an
 * Entity object. These elements should ***ONLY*** be accessed as
 * a part of XSLT transformations, since they hold within themselves
 * the possibility of enumerating the entire source database!
 *
 * @author Ian Ibbotson ( ian.ibbotson@k-int.com )
 * @version $Id: DelayedCollectionElement.java,v 1.2 2004/10/26 15:30:52 ibbo Exp $
 */
// public class DelayedCollectionElement  extends org.apache.xerces.dom.ElementImpl
public class DelayedCollectionElement extends ElementAdapter
{
  protected com.k_int.sql.data_dictionary.Entity source;
  private static java.util.logging.Logger log = java.util.logging.Logger.getLogger("com.k_int.util.conv");
  protected String attrname=null;

  protected class EntityCollectionNodeList implements NodeList {

    protected List nodes = new ArrayList();

    public EntityCollectionNodeList() {
      log.fine(" DelayedCollectionElement ----   Owner oid : "+source.getOID());
      try {
        RecordCollection rrc = (RecordCollection) source.get(attrname);
                                                                                                                                          
        // One child element for each member of the collection
        for ( int cc = 0; cc<rrc.size(); cc++ ) {
          // +1 because result sets start at 1
          com.k_int.sql.data_dictionary.Entity set_link_ent = (com.k_int.sql.data_dictionary.Entity) rrc.get(cc+1);
          if ( set_link_ent != null ) {
            log.fine("Adding collection member using element name "+set_link_ent.getTemplate().getEntityName()+"\n");
            nodes.add( new EntityElement(DelayedCollectionElement.this, 
                                         set_link_ent, 
                                         set_link_ent.getTemplate().getEntityName(),
                                         cc));
          }
          else {
              log.fine("Child entity was null :( ");
          }
        }
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }

    public int getLength() {
      return nodes.size();
    }
                                                                                                                                          
    public Node item(int index) {
      return (Node)nodes.get(index);
    }
  }

  public DelayedCollectionElement( Node parent_node, 
                                   java.lang.String name, 
                                   com.k_int.sql.data_dictionary.Entity e, 
                                   String attrname,
                                   int ordinal) {
    super(parent_node, name, ordinal);
    this.source = e;
    this.attrname = attrname;
    log.fine("New "+attrname+" collection node parent = "+source.getOID());
  }

  protected NodeList createNodeListAdapter() {
    return new EntityCollectionNodeList();
  }
}
