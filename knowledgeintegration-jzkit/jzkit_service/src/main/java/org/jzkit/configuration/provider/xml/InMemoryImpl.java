package org.jzkit.configuration.provider.xml;

import org.jzkit.configuration.api.*;
import org.jzkit.ServiceDirectory.*;
import java.util.*;
import java.net.URL;
import org.jzkit.search.util.Profile.ProfileDBO;
import org.jzkit.search.util.Profile.CrosswalkDBO;
import java.beans.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.*;

public class InMemoryImpl implements Configuration, java.io.Serializable {

  private Hashtable collection_descriptions = new Hashtable();
  private Hashtable service_descriptions = new Hashtable();
  private Hashtable profiles = new Hashtable();
  private Hashtable crosswalks = new Hashtable();
  private Hashtable landscapes = new Hashtable();
  private List record_converter_types = new ArrayList();
  private List record_converter_mappings = new ArrayList();
  private Hashtable app_settings = new Hashtable();
  private Hashtable infotypes = new Hashtable();
  private Map backend_plugins = null;
  protected ApplicationContext ctx = null;

  protected transient Log log = LogFactory.getLog(InMemoryImpl.class);

  public InMemoryImpl() {
  }
  
  public void initialise() {
    log.debug(" -- initialising");
    log.debug(" -- Scanning classpath for jzkit backend plugins");
    backend_plugins = ctx.getBeansOfType(org.jzkit.search.provider.iface.JZKitPluginMetadata.class);
    if ( backend_plugins != null ) {
      for ( java.util.Iterator i = backend_plugins.entrySet().iterator(); i.hasNext(); ) {
        Map.Entry e = (Map.Entry) i.next();
        log.debug("Registered: "+e.getKey()+" = "+e.getValue());
      }
    }

    log.debug(" -- Scanning classpath for jzkit query builders");
    Map qb_plugins = ctx.getBeansOfType(org.jzkit.search.util.QueryBuilder.QueryBuilder.class);
    if ( qb_plugins != null ) {
      for ( java.util.Iterator i = qb_plugins.entrySet().iterator(); i.hasNext(); ) {
        Map.Entry e = (Map.Entry) i.next();
        log.debug("Registered QueryBuilder: "+e.getKey()+" = "+e.getValue());
      }
    }
  }

  public void close() {
  }

  public void registerCollectionDescription(CollectionDescriptionDBO collection_description) {
    collection_descriptions.put(collection_description.getCode(), collection_description);
  }

  public void registerServiceDescription(SearchServiceDescriptionDBO service_description) {
    if ( service_description != null ) {
      service_descriptions.put(service_description.getCode(), service_description);

      log.debug("Registering any collections from service "+service_description.getCode());
      // Make sure any collections described by this service are added to the global collection map
      if ( service_description.getCollections() != null ) {
        for ( java.util.Iterator i = service_description.getCollections().iterator(); i.hasNext(); ) {
          org.jzkit.ServiceDirectory.CollectionDescriptionDBO coll = (org.jzkit.ServiceDirectory.CollectionDescriptionDBO) i.next();
          coll.setSearchServiceDescription(service_description);
          registerCollectionDescription(coll);
          log.debug(" - "+coll.getCode());
        }
      }
      else {
        log.error("get collections returned null");
      }
    }
    else {
      log.error("register collection passed null collection description");
    }
  }

  public String toString() {  
    return "IRDirectoryServiceInMemoryImpl:"+this.hashCode();
  }

  public CollectionDescriptionDBO lookupCollectionDescription(String collection_code) {
    return (CollectionDescriptionDBO) collection_descriptions.get(collection_code);
  }

  public SearchServiceDescriptionDBO lookupSearchService(String service_id) {
    return (SearchServiceDescriptionDBO) service_descriptions.get(service_id);
  }

  public InformationLandscapeDBO lookupLandscape(String landscape_code) {
    return (InformationLandscapeDBO) landscapes.get(landscape_code);
  }

  public Iterator enumerateVisibleCollections() {
    return collection_descriptions.values().iterator();
  }

  public Iterator enumerateRepositories() {
    return service_descriptions.values().iterator();
  }

  public void registerProfile(ProfileDBO p) {
    log.debug("Register profile : "+p);
    profiles.put(p.getCode(), p);
  }
                                                                                                                                        
  public ProfileDBO lookupProfile(String profile_code) {
    return (ProfileDBO) profiles.get(profile_code);
  }

  public Iterator enumerateProfiles()  throws ConfigurationException {
    return profiles.values().iterator();
  }

  public void registerSearchService(SearchServiceDescriptionDBO ssd) {
    service_descriptions.put(ssd.getCode(), ssd);
  }

  public void registerCrosswalk(CrosswalkDBO cw) {
    crosswalks.put(cw.getSourceNamespace(), cw);
  }

  public CrosswalkDBO lookupCrosswalk(String source_namespace) {
    return (CrosswalkDBO) crosswalks.get(source_namespace);
  }

  public Iterator enumerateCrosswalks() {
    return crosswalks.values().iterator();
  }

  public void registerRecordModelConverterType(RecordTransformerTypeInformationDBO info) {
    record_converter_types.add(info);
  }

  public Iterator getRegisteredConverterTypes() {
    return record_converter_types.iterator();
  }
                                                                                                                                        
  public void registerRecordModelMapping(RecordMappingInformationDBO info) {
    record_converter_mappings.add(info);
  }

  public Iterator getRegisteredRecordMappings() {
    return record_converter_mappings.iterator();
  }

  public void registerLandscape(InformationLandscapeDBO landscape) {
    landscapes.put(landscape.getCode(),landscape);
  }

  public Iterator enumerateLandscapes() {
    return landscapes.values().iterator();
  }

  public String getAppProperty(String name) {
    return (String) app_settings.get(name);
  }

  public void setAppProperty(String name, String value) {
    app_settings.put(name,value);
  }

  public Iterator getAppPropertyNames() {
    return app_settings.keySet().iterator();
  }

  public CollectionInfoTypeDBO lookupOrCreateCollectionInfoType(String namespace, String code) {
    CollectionInfoTypeDBO result = (CollectionInfoTypeDBO) infotypes.get(namespace+":"+code);
    if ( result == null ) {
      result = new CollectionInfoTypeDBO(namespace,code);
      infotypes.put(namespace+":"+code,result);
    }
    return result;
  }

  public Iterator enumerateInfoTypes() {
    return infotypes.values().iterator();
  }

  public void setApplicationContext(ApplicationContext ctx) {
    log.debug("setApplicationContext...");
    this.ctx = ctx;
  }

  public void tagCollection(String collection_id, String posting_namespace, String tag) {
    log.debug("tagCollection("+collection_id+","+posting_namespace+","+tag+")");
  }

  public Map getBackendPlugins() {
    return backend_plugins;
  }

  public void addOrUpdate(SearchServiceDTO svc) throws ConfigurationException {
    throw new RuntimeException("Not impleemented");
  }

}
