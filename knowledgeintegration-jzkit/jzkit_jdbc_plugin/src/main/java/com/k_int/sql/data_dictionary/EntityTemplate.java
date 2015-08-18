package com.k_int.sql.data_dictionary;


import java.sql.*;
import java.util.*;
import java.lang.reflect.Constructor;
import org.apache.commons.logging.*;


/**
 * Title:       EntityTemplate
 * @version:    $Id: EntityTemplate.java,v 1.6 2005/03/08 19:25:44 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description:
 * @hibernate.class  table="JZ_JDBC_ENTITY_TEMPLATE" dynmic-update="true" dynamic-insert="true"
 */
public class EntityTemplate {

  /** The <code>Log</code> instance for this application.  */
  protected static Log log = LogFactory.getLog(EntityTemplate.class);

  private Long id; // Only used in database config mode
  private String entity_name;
  private String base_table_name;

  private Map attributes_by_name = new java.util.HashMap();

  private List attributes = new java.util.ArrayList();
  private List primary_key_attributes = new java.util.ArrayList();
  private List best_row_identifier = new ArrayList();

  private String date_added_attr_name;
  private String date_last_modified_attr_name;
  private String date_deleted_attr_name;
  private String record_status_attr_name;
  private String discriminator_attr_name;
  private Boolean oai_header_supported = Boolean.FALSE;

  private EntityTemplate() {
  }

  public EntityTemplate(String name, String basetable) {
    entity_name = name;
    base_table_name = basetable;
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


  /**
   * @hibernate.list cascade="all" lazy="false"
   * @hibernate.collection-key column="JZ_DB_ET_ID"
   * @hibernate.collection-index column="IDX"
   * @hibernate.collection-one-to-many class="com.k_int.sql.data_dictionary.AttributeDefinition"
   */
  public List getAttributes() {
    return attributes;
  }

  public void setAttributes(List attributes) {
    this.attributes = attributes;
    resetAttrMap();
  }

  /**
   * @hibernate.list cascade="all" table="JZ_JDBC_ET_PK_ATTR" lazy="false"
   * @hibernate.collection-key column="JZ_DB_ET_ID"
   * @hibernate.collection-index column="IDX"
   * @hibernate.collection-element type="string" column="PK_ATTR_NAME"
   */
  public List getPKAttributes() {
    return primary_key_attributes;
  }

  public void setPKAttributes(List primary_key_attributes) {
    this.primary_key_attributes = primary_key_attributes;
  }


  /**
   * @hibernate.property
   * @hibernate.column name="TABLE_NAME" length="128"
   */
  public String getBaseTableName() {
    return base_table_name;
  }

  public void setBaseTableName(String base_table_name) {
    this.base_table_name = base_table_name;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="ENTITY_NAME" length="128"
   */
  public String getEntityName() {
    return entity_name;
  }

  public void setEntityName(String entity_name) {
    this.entity_name = entity_name;
  }

  public String getBaseAttrColName(int position) {
    return ((DatabaseColAttribute)attributes.get(position)).getColName();
  }

  public void setPrimaryKeyCols(Vector cols) {
    primary_key_attributes=cols;
  }

  public void addPrimaryKeyCol(String colname) {
    primary_key_attributes.add(colname);
  }

  public Collection getKeyAttrsCollection() {
    return primary_key_attributes;
  }

  public Iterator getKeyAttrs() {
    return primary_key_attributes.iterator();
  }

  public void registerBestRowIdentifierCol(String colname) {
    best_row_identifier.add(colname);
  }

  public String getBestRowIdentifier() {
    return (String)best_row_identifier.get(0);
  }

  /**
   * @hibernate.property
   * @hibernate.column name="DATE_ADDED_ATTR_NAME" length="128"
   */
  public String getDateAddedAttrName() {
    return date_added_attr_name;
  }

  public void setDateAddedAttrName(String date_added_attr_name) {
    this.date_added_attr_name = date_added_attr_name;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="DATE_MODIFIED_ATTR_NAME" length="128"
   */
  public String getDateLastModifiedAttrName() {
    return date_last_modified_attr_name;
  }

  public void setDateLastModifiedAttrName(String date_last_modified_attr_name) {
    this.date_last_modified_attr_name = date_last_modified_attr_name;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="DATE_DELETED_ATTR_NAME" length="128"
   */
  public String getDateDeletedAttrName() {
    return date_deleted_attr_name;
  }

  public void setDateDeletedAttrName(String date_deleted_attr_name) {
    this.date_deleted_attr_name = date_deleted_attr_name;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="RECORD_STATUS_ATTR_NAME" length="128"
   */
  public String getRecordStatusAttrName() {
    return record_status_attr_name;
  }

  public void setRecordStatusAttrName(String record_status_attr_name) {
    this.record_status_attr_name = record_status_attr_name;
  }

  /**
   * The entity discriminator is the column used in an object model mapping to store a specific
   * subclass identifier for a given item.
   * @hibernate.property
   * @hibernate.column name="DISCRIM_ATTR_NAME" length="128"
   */
  public String getDiscriminatorAttrName() {
    return discriminator_attr_name;
  }

  public void setDiscriminatorAttrName(String discriminator_attr_name) {
    this.discriminator_attr_name = discriminator_attr_name;
  }


  /**
   * @hibernate.property
   * @hibernate.column name="OAI_HEADER_SUPPORTED"
   */
  public Boolean getOAIHeaderSupported() {
    return oai_header_supported;
  }

  public void setOAIHeaderSupported(Boolean oai_header_supported) {
    this.oai_header_supported = oai_header_supported;
  }


  public void addAttribute(String attrname, AttributeDefinition ad) {
    int pos = attributes.size();
    attributes.add(ad);

    // Attributes by name gives is the position of the named attribute in the vector.
    attributes_by_name.put(attrname, new Integer(pos));
  }

  public void addAttribute(AttributeDefinition ad) {
    addAttribute(ad.getAttributeName(), ad);
  }

  public AttributeDefinition getAttributeDefinition(String attrname) throws UnknownAccessPointException {
    return (AttributeDefinition) ( attributes.get( lookupPosition(attrname) ) );
  }

  public AttributeDefinition getAttributeDefinition(int pos) {
      return (AttributeDefinition) ( attributes.get(pos) );
  }

  public Iterator getAttributeDefinitions() {
      return attributes.iterator();
  }

  public int getNumAttributes() {
      // System.err.println("getNumAttributes returns "+attributes.size());
      return attributes.size();
  }

  public int lookupPosition(String attr) throws UnknownAccessPointException {
      Integer val = (Integer)attributes_by_name.get(attr);
      if ( null == val )
        throw new UnknownAccessPointException("Unknown attribute : "+attr);
      return val.intValue();
  }

  public void dumpAttributes() {
      for ( int i=0; i<attributes.size(); i++ ) {
          log.debug("Attribute at "+i+" is "+((AttributeDefinition)attributes.get(i)).getAttributeName());
      }
  }

  public String toString() {
      return entity_name;
  }


  /**
   *  The method takes a database access path like TitleAuthorLink.Author.Name and resolves it into a vector
   *  of information objects which describe the various entities and attributes that need to be linked
   *  together in order to join from the source element to the target element.
   */
  public Vector resolveAccessPath(String path, Dictionary dict) throws UnknownAccessPointException, 
                                                                       UnknownCollectionException {
      Vector result = new Vector();
      String[] components = path.split("\\.");
      EntityTemplate current_parent=this;
      AttributeDefinition current_attr = null;

      // log.debug("resolveAccessPath processing path : "+path);

      for ( int i=0; i<components.length; i++ ) {
        boolean reuse_context = true;
        int join_type = 0;

        String[] next_attr = components[i].split(",");

        // Figure out if we were given any special flags for this attr. 0 is the name...
        current_attr = current_parent.getAttributeDefinition(next_attr[0]);

        if ( current_attr == null ) {
          log.warn("Failed to lookup attr definition "+next_attr[0]);
        }

        // Pre process any attributes controlling generation of the join clause
        for ( int c = 1; c < next_attr.length; c++ ) {
          if ( next_attr[c].equals("new") ) {
            reuse_context = false;
          }
          else if ( next_attr[c].equals("outer") ) {
            join_type = 2;
          }
        }

        // result.add(new AccessPathComponent(current_attr,....);
        AccessPathComponent apc = new AccessPathComponent(current_parent, current_attr, reuse_context, join_type);
        result.add(apc);

        if ( ( current_attr.getType() == AttributeDefinition.LINK_ATTR_TYPECODE ) ||
             ( current_attr.getType() == AttributeDefinition.COLLECTION_ATTR_TYPECODE ) ) {
          String new_parent_name = ((DatabaseLinkAttribute)current_attr).getRelatedEntityName();
          current_parent = dict.lookup(new_parent_name);
        }

        for ( int c = 1; c < next_attr.length; c++ ) {
          // Post process any filter conditions
          if ( next_attr[c].startsWith("filter:") ) {
            String[] filter_components = next_attr[c].split("[:=\"]");
            if ( filter_components.length==3 ) {
              String attribute=filter_components[1];
              String value=filter_components[2];
              // log.debug("add filter condition: "+attribute+" = "+value);
              apc.addConstraint(attribute,value);
            }
            else {
              log.warn("unable to process filter: \""+next_attr[c]+"\" len="+filter_components.length);
            }
          }
        }
      }
 
      // log.debug("resolveAccessPath "+result);
      return result;
  }

  public String getRealColumnName(String attr_name) throws UnknownAccessPointException {
    DatabaseColAttribute ad = (DatabaseColAttribute) getAttributeDefinition(attr_name);
    return ad.getColName();
  }


  private void resetAttrMap() {
    int it=0;
    for ( java.util.Iterator i = attributes.iterator(); i.hasNext(); ) {
      AttributeDefinition attrdef = (AttributeDefinition) i.next();
      attributes_by_name.put(attrdef.getAttributeName(), new Integer(it++));
      // attributes_by_name.put(attrdef.getAttributeName(), attrdef);
    }
  }
}
