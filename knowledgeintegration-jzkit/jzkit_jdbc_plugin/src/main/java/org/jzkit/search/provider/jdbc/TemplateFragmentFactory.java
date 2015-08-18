package org.jzkit.search.provider.jdbc;

import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
                                                                                                                                          
import com.k_int.sql.sql_syntax.*;
import com.k_int.sql.qm_to_sql.*;
import com.k_int.sql.data_dictionary.*;
import java.io.StringWriter;

import java.util.*;

import org.w3c.dom.*;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import javax.xml.parsers.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Title:
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: TemplateFragmentFactory.java,v 1.4 2004/10/28 10:11:57 ibbo Exp $
 *
 * @hibernate.class  table="JZ_JDBC_FRAGMENT_FACTORY" dynamic-update="true" dynamic-insert="true" lazy="false"
 *
 */
public class TemplateFragmentFactory implements FragmentFactory {

  private Long id;
  public EntityMapping root_element;
  private transient static DocumentBuilderFactory dfactory = null;
                                                                                                                                          
  static
  {
    dfactory = DocumentBuilderFactory.newInstance();
    dfactory.setNamespaceAware(true);
    dfactory.setValidating(false);
    dfactory.setAttribute("http://xml.org/sax/features/validation",Boolean.FALSE);
    dfactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd",Boolean.FALSE);
  }
                                                                                                                                          
  public TemplateFragmentFactory() {
  }

  /**
   * @hibernate.id  generator-class="native" column="ID"
   */
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TemplateFragmentFactory(EntityMapping root_element) {
    this.root_element = root_element;
  }

  /**
   * @hibernate.many-to-one column="MAPPING_ID" class="org.jzkit.search.provider.jdbc.EntityMapping" cascade="all"
   */
  public EntityMapping getEntityMapping() {
    return root_element;
  }

  public void setEntityMapping(EntityMapping root_element) {
    this.root_element = root_element;
  }

  public Document createFragment(com.k_int.sql.data_dictionary.Entity e) {
    try {
      // log.fine("Parse record:" +orig);
      DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
      Document doc = docBuilder.newDocument();
      // Element root = retval.createElement("sutrs");
      // root.appendChild( retval.createTextNode(orig) );
      // retval.appendChild( root );
      mapEntity(doc, doc, root_element, e);

      // InformationFragment retval = new org.jzkit.search.util.RecordModel.DOMTree("repos","collection","handle",doc,spec);
      return doc;
    }
    catch ( ParserConfigurationException pce ) {
      pce.printStackTrace();
    }

    return null;
  }

  private static void mapEntity(Document d, Node parent, EntityMapping mapping, com.k_int.sql.data_dictionary.Entity e) {
    if ( e != null ) {
      // If a parent elemet name is set, Create an element in the document to represent this entity
      // Otherwise append any children directly to parent. care should be taken!
      Element elem = null;
      if ( mapping.getElementName() != null ) {
        elem = d.createElement(mapping.getElementName());
        parent.appendChild(elem);
      }
      else {
        elem=(Element) parent;
      }

      // Iterate over child mappings adding any mapped fields / collections / entities
      for ( Iterator i = mapping.getChildElements().iterator(); i.hasNext(); ) {
        AttrInfo attr_info = (AttrInfo)i.next();
        try {
          Object o = e.get(attr_info.attr_name);
          if ( o != null ) {

            // If it's an instance of entity.
            if ( o instanceof com.k_int.sql.data_dictionary.Entity ) {
              // System.err.println("Processing entity");
              if ( attr_info.child_mapping != null ) {
                Element entity_holder_elem = d.createElement(attr_info.element_name);
                elem.appendChild(entity_holder_elem);
                mapEntity(d,entity_holder_elem,attr_info.child_mapping,(com.k_int.sql.data_dictionary.Entity)o);
              }
            }
            else if ( o instanceof RelatedRecordCollection ) {
              RelatedRecordCollection rrc = (RelatedRecordCollection) o;
              // System.err.println("Processing RelatedRecordCollection of size"+rrc.size());
              if ( ( rrc.size() > 0 ) && ( attr_info.child_mapping != null ) ) {
                Element list_holder_elem = d.createElement(attr_info.element_name);
                elem.appendChild(list_holder_elem);
                for ( Iterator child_records = rrc.iterator(); child_records.hasNext(); ) {
                  com.k_int.sql.data_dictionary.Entity ce = (com.k_int.sql.data_dictionary.Entity)child_records.next();
                  mapEntity(d,list_holder_elem,attr_info.child_mapping,ce);
                }
                // value_elem.appendChild(d.createTextNode(o.toString()));
              }
            }
            else {
              // Must be a value
              if ( ( o != null ) && ( o.toString().length() > 0 ) ) {
                Element value_elem = d.createElement(attr_info.element_name);
                elem.appendChild(value_elem);
                if ( o instanceof String ) {
                  value_elem.appendChild(d.createCDATASection(o.toString()));
                }
                else {
                  value_elem.appendChild(d.createTextNode(o.toString()));
                }
              }
              // elem.setAttribute(attr_info.element_name,o.toString());
            }
          }
        }
        catch ( com.k_int.sql.data_dictionary.UnknownAccessPointException uape ) {
          uape.printStackTrace();
          Element value_elem = d.createElement(attr_info.element_name);
          elem.appendChild(value_elem);
          value_elem.appendChild(d.createTextNode(uape.toString()));
        }
      }
    }
  }

}
