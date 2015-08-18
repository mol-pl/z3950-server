// Title:       EntityElement
// @version:    $Id: EntityElement.java,v 1.2 2004/10/26 15:30:52 ibbo Exp $
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
import java.util.*;

/**
 *
 * A Realisation of a DOM Element that acts as a delagate for an
 * Entity object. These elements should ***ONLY*** be accessed as
 * a part of XSLT transformations, since they hold within themselves
 * the possibility of enumerating the entire source database!
 *
 * @author Ian Ibbotson ( ian.ibbotson@k-int.com )
 * @version $Id: EntityElement.java,v 1.2 2004/10/26 15:30:52 ibbo Exp $
 */
public class EntityElement extends ElementAdapter {

  protected com.k_int.sql.data_dictionary.Entity wrapped_entity;

  /**
   * Parent entity
   */
  protected com.k_int.sql.data_dictionary.Entity parent_entity;

  /**
   * Attribute name of this attribute within the parent
   */
  protected String attrname;

  public EntityElement(Node parent_node, 
                       com.k_int.sql.data_dictionary.Entity parent_entity, 
                       String attrname, 
                       String element_name, 
                       int ordinal) {
    super(parent_node, element_name, ordinal);
    this.parent_entity = parent_entity;
    this.attrname = attrname;
  }

  public EntityElement(Node parent_node,
                       com.k_int.sql.data_dictionary.Entity wrapped_entity, 
                       String element_name, 
                       int ordinal) {
    super(parent_node, element_name, ordinal);
    this.wrapped_entity = wrapped_entity;
  }

  public EntityElement(com.k_int.sql.data_dictionary.Entity wrapped_entity, 
                       String element_name, 
                       int ordinal) {
    super(null, element_name, ordinal);
    this.wrapped_entity = wrapped_entity;
  }

  protected class EntityAttributeNodeList implements NodeList {

    protected List nodes = new ArrayList();

    public EntityAttributeNodeList() {

      if ( wrapped_entity == null ) {
        try {
          wrapped_entity = (com.k_int.sql.data_dictionary.Entity) parent_entity.get(attrname);
        }
        catch ( Exception e ) {
          e.printStackTrace();
        }
      }
      else {
        // cat.debug("Already got entity");
      }

      System.err.println("--   Owner oid : "+wrapped_entity.getOID());
      EntityTemplate et = wrapped_entity.getTemplate();

      System.err.println("---> Processing "+et.getNumAttributes());

      int ordinal_counter = 0;
      for ( int i = 0; i < et.getNumAttributes(); i++ ) {

        try {
          AttributeDefinition ad = et.getAttributeDefinition(i);
          System.err.println("Processing entity attr "+i+", "+ad.getAttributeName());
  
          switch ( ad.getType() ) {
  
            case AttributeDefinition.LINK_ATTR_TYPECODE:                  // A Link Attribute
              System.err.println("Adding link attribute "+ad.getAttributeName());
              com.k_int.sql.data_dictionary.Entity link_ent = 
                      (com.k_int.sql.data_dictionary.Entity) wrapped_entity.get(ad.getAttributeName());

              // Add an Entity element that will be evaluated later if needed
              if ( link_ent != null ) {
                nodes.add( new EntityElement(EntityElement.this, 
                                             wrapped_entity, 
                                             ad.getAttributeName(), 
                                             ad.getAttributeName(), 
                                             ordinal_counter++));
                // cat.debug("Completed construction of new EntityElement for "+ ad.getAttributeName());
              }
              break;
    
            // Here we have a node which represents a link to a foreign key and hence, a set
            // of related entities. We don't want to enumerate that set until we actually hit that element.
            case AttributeDefinition.COLLECTION_ATTR_TYPECODE:
              System.err.println("Adding Collection attribute "+ad.getAttributeName());
              // RecordCollection rrc = (RecordCollection) wrapped_entity.get(ad.getAttributeName());
              // cat.debug("Child collection contains "+rrc.size());
              // The "Collection" Element
              // Element new_set_child = getOwnerDocument().createElement(ad.getAttributeName());
              Element new_set_child = new DelayedCollectionElement(EntityElement.this, 
                                                                   ad.getAttributeName(), 
                                                                   wrapped_entity, 
                                                                   ad.getAttributeName(),
                                                                   ordinal_counter++);
              nodes.add( new_set_child );
              break;

            case AttributeDefinition.DB_COLUMN_ATTR_TYPECODE:               // DB COLUMN
              System.err.println("Adding DB Column attribute "+ad.getAttributeName());
              Object v = wrapped_entity.getValueAt(i,true);
              if ( v != null ) {
                nodes.add( new ValueElement(EntityElement.this,
                                            ad.getAttributeName(),
                                            v,
                                            ordinal_counter++));

              }
              break;
  
            case AttributeDefinition.PSEUDO_COLUMN:
              break;
          }
        }
        catch ( Exception e ) {
          e.printStackTrace();
        }
      }
    }

    public int getLength() {
      return nodes.size();
    }

    public Node item(int index) {
      return (Node)nodes.get(index);
    }
  }

  protected NodeList createNodeListAdapter() {
    return new EntityAttributeNodeList();
  }
}
