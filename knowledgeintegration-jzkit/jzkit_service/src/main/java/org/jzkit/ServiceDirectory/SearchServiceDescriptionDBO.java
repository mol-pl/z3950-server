package org.jzkit.ServiceDirectory;

import java.util.*;
import org.apache.commons.beanutils.*;
import org.springframework.context.ApplicationContext;
import javax.persistence.*;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.IndexColumn;
import org.jzkit.search.provider.iface.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jzkit.search.util.QueryModel.Internal.AttrValue;

/**
 * An object which describes the properties of a search service. Different subclasses of this
 * interface will provide information about different kinds of resources, from Z39.50 servers to
 * SRW and SRU services.
 * These objects should be lightweight, for example,
 * Searchable s = new Z3950SearchServiceDescription("z3950://test.server:210").newSearchable();
 *
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * Company:     Knowledge Integration Ltd.
 * License:     A license.txt file should is distributed along with this software
 *
 * @author Ian Ibbotson
 * @version $Id: SearchServiceDescriptionDBO.java,v 1.17 2005/10/18 12:13:18 ibbo Exp $
 * @see org.jzkit.SearchProvider.iface.Searchable
 */ 
@Entity
@Table(name="JZ_SEARCH_SERVICE_DESCRIPTION")
public class SearchServiceDescriptionDBO {

  private Long id;
  private String code;
  private Map preferences = new HashMap();
  private Map record_syntax_archetypes = new HashMap();
  private String service_name;
  private String service_short_name;
  private String class_name;
  private Set collection_info = new HashSet();
  private Map<String,AttributeSetDBO> valid_attributes = new HashMap<String,AttributeSetDBO>();
  private Map<String,AttrValue> service_specific_translations = new HashMap<String,AttrValue>();
  private ApplicationContext ctx;

  protected transient Log log = LogFactory.getLog(SearchServiceDescriptionDBO.class);

  @Id
  @Column(name="ID")
  @GeneratedValue(strategy=GenerationType.AUTO)
  public Long getId() {
    return id;
  }
                                                                                                                                        
  public void setId(Long id) {
    this.id = id;
  }

  @Column(name="CODE",length=50)
  public String getCode() {
    return code;
  }
                                                                                                                                        
  public void setCode(String code) {
    this.code = code;
  }

  @CollectionOfElements(fetch=javax.persistence.FetchType.EAGER)
  @JoinTable(name="JZ_SSD_PROPS",joinColumns=@JoinColumn(name="SEARCH_SERVICE_ID"))
  @org.hibernate.annotations.MapKey(columns={@Column(name="PROP_NAME",length=128)})
  @Column(name="PROP_VALUE",length=128)
  public Map<String,String> getPreferences() {
    return preferences;
  }

  public void setPreferences(Map<String,String> preferences) {
    this.preferences = preferences;
  }

  public void setPreference(String name, Object value) {
    preferences.put(name,value);
  }

  public Object getPreference(String name, Object default_value) {
    Object result = preferences.get(name);
    if ( result == null )
      return default_value;
    return result;
  }

  @CollectionOfElements(fetch=javax.persistence.FetchType.EAGER)
  @JoinTable(name="JZ_SSD_RECORD_ARCHETYPES",joinColumns=@JoinColumn(name="SEARCH_SERVICE_ID"))
  @org.hibernate.annotations.MapKey(columns={@Column(name="ARCHETYPE",length=64)})
  @Column(name="SPEC",length=64)
  public Map<String,String> getRecordArchetypes() {
    return record_syntax_archetypes;
  }

  public void setRecordArchetypes(Map<String,String> record_syntax_archetypes) {
    this.record_syntax_archetypes = record_syntax_archetypes;
  }

  public void setRecordArchetype(String name, Object value) {
    record_syntax_archetypes.put(name,value);
  }

  public Object getRecordArchetype(String name, Object default_value) {
    Object result = record_syntax_archetypes.get(name);
    if ( result == null )
      return default_value;
    return result;
  }

  @Column(name="SERVICE_NAME",length=40)
  public String getServiceName() {
    return service_name;
  }

  public void setServiceName(String service_name) {
    this.service_name = service_name;
  }

  @Column(name="SERVICE_SHORT_NAME",length=40)
  public String getServiceShortName() {
    return service_short_name;
  }

  public void setServiceShortName(String service_short_name) {
    this.service_short_name = service_short_name;
  }

  @Column(name="CLASS_NAME",length=256)
  public String getClassName() {
    return class_name;
  }

  public void setClassName(String class_name) {
    this.class_name = class_name;
  }

  @OneToMany(mappedBy="searchServiceDescription")
  public Set<org.jzkit.ServiceDirectory.CollectionDescriptionDBO> getCollections() {  
    return collection_info;
  }

  public void setCollections(Set<org.jzkit.ServiceDirectory.CollectionDescriptionDBO> collection_info) {
   this.collection_info = collection_info;
  }

  public void addCollection(org.jzkit.ServiceDirectory.CollectionDescriptionDBO coll) {
    log.debug("add collection "+coll.getCode());
    this.collection_info.add(coll);
  }

  /**
   * Return the list of allowable access points.
   * @return The set of AttrValue objects that are allowed to be used as access points. May return null, which means anything goes!
   */
  @CollectionOfElements(fetch=javax.persistence.FetchType.EAGER)
  @org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
  @JoinTable(name="JZ_SSD_VALID_ATTR_SET",joinColumns=@JoinColumn(name="DESCRIPTOR_ID"))
  public Map<String,AttributeSetDBO> getValidAttrs() {
    return valid_attributes;
  }

  public void setValidAttrs(Map<String,AttributeSetDBO> valid_attributes) {
    this.valid_attributes = valid_attributes;
  }

  @CollectionOfElements(fetch=javax.persistence.FetchType.EAGER)
  @JoinTable(name="JZ_SSD_TRANS",joinColumns=@JoinColumn(name="DESCRIPTOR_ID"))
  @org.hibernate.annotations.MapKey(columns={@Column(name="FROM_ATTR_STR",length=128)})
  public Map<String,AttrValue> getServiceSpecificTranslations() {
    return service_specific_translations;
  }

  public void setServiceSpecificTranslations(Map<String,AttrValue> service_specific_translations) {
    this.service_specific_translations = service_specific_translations;
  }

  public void registerValidIndex(String type, String namespace, String index_name) {
    log.debug("registerValidIndex "+type+", "+namespace+":"+index_name);
    AttributeSetDBO attr_set = valid_attributes.get(type);
    if ( attr_set == null ) {
      attr_set = new AttributeSetDBO();
      valid_attributes.put(type,attr_set);
    }

    attr_set.getAttrs().add(new AttrValue(namespace,index_name));
  }

  public void registerTranslation(String from, String target_namespace, String target_index_name) {
    log.debug("registerTranslation "+from+" -> "+target_namespace+":"+target_index_name);
    service_specific_translations.put(from, new AttrValue(target_namespace,target_index_name));
  }

  public String toString() {
    return code+" - "+service_name;
  }

  public synchronized org.jzkit.search.provider.iface.Searchable getInstance(ServiceUserInformation user_info) throws SearchException {

    Searchable result = null;
    try {
      Class searchable_class = Class.forName(class_name);

      result = (Searchable) searchable_class.newInstance();

      // Iterate through preferences, setting 
      for ( java.util.Iterator i = preferences.keySet().iterator(); i.hasNext(); ) {
        String property_name = (String)(i.next());
        Object property_value = preferences.get(property_name);
        BeanUtils.setProperty(result, property_name, property_value);
      }
 
      // Copy forward record syntax archetypes
      result.setRecordArchetypes(record_syntax_archetypes);
    }
    catch ( java.lang.ClassNotFoundException cnfe ) {
      throw new SearchException("Unable to locate searchable class: "+class_name, cnfe);
    }
    catch ( java.lang.InstantiationException ie ) {
      throw new SearchException("Probem creating new SearchServiceFactory - "+class_name+" ie",ie);
    }
    catch ( java.lang.IllegalAccessException iae ) {
      throw new SearchException("Probem creating new SearchServiceFactory",iae);
    }
    catch ( java.lang.reflect.InvocationTargetException ite ) {
      throw new SearchException("Probem creating new SearchServiceFactory",ite);
    }

    return result;
  }

  public Searchable newSearchable(ServiceUserInformation user_info) throws SearchException {
    return getInstance(user_info);
  }
                                                                                                                                          
  public Searchable newSearchable() throws SearchException {
    return getInstance(null);
  }

  public void setApplicationContext(org.springframework.context.ApplicationContext ctx) {
    this.ctx = ctx;
  }
}
