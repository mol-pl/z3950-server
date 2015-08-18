  package org.jzkit.configuration.provider.db;

import org.jzkit.configuration.api.*;
import org.jzkit.ServiceDirectory.*;
import org.jzkit.search.util.Profile.ProfileDBO;
import org.jzkit.search.util.Profile.CrosswalkDBO;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.List;
import org.hibernate.*;
import org.hibernate.type.*;
import org.springframework.context.*;
import org.jzkit.search.util.QueryModel.Internal.AttrValue;

public class DbConfigurationProvider implements Configuration {
  private SessionFactory sf = null;
  private org.hibernate.cfg.Configuration c = null;
  private ApplicationContext ctx = null;
  public static Log log = LogFactory.getLog(DbConfigurationProvider.class);

  private static org.hibernate.type.Type HIBERNATE_LONG_TYPE = Hibernate.LONG;
  private static org.hibernate.type.Type HIBERNATE_STRING_TYPE = Hibernate.STRING;
  private static org.hibernate.type.Type HIBERNATE_BOOLEAN_TYPE = Hibernate.BOOLEAN;

  private static final String SESSION_ID_PREF_NAME = "ConfigurationSessionId";
  private static final String SESSION_CONNECTION_URL_PREF_NAME = "ConnectionURL";
  private static final String SESSION_USERNAME_PREF_NAME = "Username";
  private static final String SESSION_PASSWORD_PREF_NAME = "Password";
  private static final String SESSION_DRIVER_PREF_NAME = "Driver";
  private static final String SESSION_DIALECT_PREF_NAME = "Dialect";
  private static final String POOL_SIZE_PREF_NAME = "PoolSize";

  private static final String COLLECTION_LIST_QRY="from r in class org.jzkit.ServiceDirectory.CollectionDescriptionDBO";
  private static final String REPOSITORY_LIST_QRY="from r in class org.jzkit.ServiceDirectory.SearchServiceDescriptionDBO";
  private static final String PROFILE_LIST_QRY="from r in class org.jzkit.search.util.Profile.ProfileDBO";
  private static final String CROSSWALK_LIST_QRY="from r in class org.jzkit.search.util.Profile.CrosswalkDBO";
  private static final String TRANS_TYPE_LIST_QRY="from r in class org.jzkit.configuration.api.RecordTransformerTypeInformationDBO";
  private static final String RECORD_MAPPING_LIST_QRY="from r in class org.jzkit.configuration.api.RecordMappingInformationDBO";
  private static final String LANDSCAPE_QRY="from r in class org.jzkit.ServiceDirectory.InformationLandscapeDBO";
  private static final String APP_PROPERTY_NAMES_QRY="select r.name from org.jzkit.configuration.api.AppSettingDBO as r";

  private static final String PROFILE_DESC_QRY="from r in class org.jzkit.search.util.Profile.ProfileDBO where r.code = ?";
  private static final String COLLECTION_DESC_QRY="from r in class org.jzkit.ServiceDirectory.CollectionDescriptionDBO where r.code = ?";
  private static final String SERVICE_DESC_QRY="from r in class org.jzkit.ServiceDirectory.SearchServiceDescriptionDBO where r.code = ?";
  private static final String CROSSWALK_DESC_QRY="from r in class org.jzkit.search.util.Profile.CrosswalkDBO where r.scope='Global' and r.sourceNamespace = ?";
  private static final String LOOKUP_LANDSCAPE_OQL="from r in class org.jzkit.ServiceDirectory.InformationLandscapeDBO where r.code=?";
  private static final String LOOKUP_PROPERTY_QRY="from r in class org.jzkit.configuration.api.AppSettingDBO where r.name=?";
  private static final String INFO_TYPES_QUERY="from r in class org.jzkit.ServiceDirectory.CollectionInfoTypeDBO";
  private static final String LOOKUP_INFOTYPE_QRY="from r in class org.jzkit.ServiceDirectory.CollectionInfoTypeDBO where r.namespace=? and r.code=?";

  private static int inst_count = 0;
  private static int session_count = 0;

  private Map backend_plugins = null;

  public void initialise() throws ConfigurationException {
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

    log.debug("Performing bootstrap configuration - Is there an explain database?");

    log.debug("Is there a URL of default services to sync with?");
  }

  // public void close() {
  //   if ( session != null ) {
  //     try { 
  //       session.close(); 
  //       log.info("session count : "+(--session_count));
  //     } catch ( Exception e ) {}
  //   }
  // }
                                                                                                                                        
  public DbConfigurationProvider(SessionFactory sf) throws ConfigurationException { 
    log.info("new DbConfigurationProvider("+(++inst_count)+")");
    ++session_count;
    this.sf = sf;
    // session = sf.openSession();
  }

  protected void finalize() {
    log.info("finalize DbConfigurationProvider("+(--inst_count)+")");
  }

  /**
   * lookup the collection information corresponding to the given ID.
   * @param collection_id Id of collection to locate
   * @return The corresponding collection description
   */
  public CollectionDescriptionDBO lookupCollectionDescription(String collection_id) throws org.jzkit.configuration.api.ConfigurationException {
    CollectionDescriptionDBO result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(COLLECTION_DESC_QRY);
      q.setParameter(0,collection_id, Hibernate.STRING);
      result = (CollectionDescriptionDBO) q.uniqueResult();
    }
    catch ( Exception e )
    {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }

    return result;
  }

  /**
   * lookup the search service corresponding to the given ID.
   * @param service_id Id of service to locate
   * @return The corresponding service
   */
  public SearchServiceDescriptionDBO lookupSearchService(String service_id) throws ConfigurationException {
    log.debug("lookupSearchService "+service_id);
    SearchServiceDescriptionDBO result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(SERVICE_DESC_QRY);
      q.setParameter(0,service_id, Hibernate.STRING);
      result = (SearchServiceDescriptionDBO) q.uniqueResult();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
                                                                                                                                        
    return result;
  }

  /**
   * list all known collections.
   * @return an enumeration of all collections known.
   */
  public Iterator enumerateVisibleCollections() throws ConfigurationException {
    Iterator result = null;

    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(COLLECTION_LIST_QRY);
      result=q.iterate();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }

    return result;
  }

  /**
   * list all known repositories
   */
  public Iterator enumerateRepositories() throws ConfigurationException {
    Iterator result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(REPOSITORY_LIST_QRY);
      result=q.iterate();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }

    return result;
  }

  /**
   * Lookup the identified profile.
   * @param id of profile to look up
   * @return Profile
   */
  public ProfileDBO lookupProfile(String profile_id) throws ConfigurationException {
    ProfileDBO result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(PROFILE_DESC_QRY);
      q.setParameter(0,profile_id, Hibernate.STRING);
      result = (ProfileDBO) q.uniqueResult();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
                                                                                                                                        
    return result;
  }

  public Iterator enumerateProfiles() throws ConfigurationException {    
    Iterator result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(PROFILE_LIST_QRY);
      result = q.iterate();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
                                                                                                                                        
    return result;
  }
                                                                                                                                        
  public CrosswalkDBO lookupCrosswalk(String source_namespace) throws ConfigurationException {
    CrosswalkDBO result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(CROSSWALK_DESC_QRY);
      q.setParameter(0,source_namespace, Hibernate.STRING);
      result = (CrosswalkDBO) q.uniqueResult();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
                                                                                                                                        
    return result;
  }

  public Iterator enumerateCrosswalks() throws ConfigurationException {
    Iterator result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(CROSSWALK_LIST_QRY);
      result = q.iterate();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
                                                                                                                                        
    return result;
  }
                                                                                                                                        
  public Iterator getRegisteredConverterTypes() throws ConfigurationException {
    Iterator result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(TRANS_TYPE_LIST_QRY);
      result = q.iterate();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
                                                                                                                                        
    return result;
  }

  public Iterator getRegisteredRecordMappings() throws ConfigurationException {
    Iterator result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(RECORD_MAPPING_LIST_QRY);
      result = q.iterate();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
                                                                                                                                        
    return result;
  }

  public InformationLandscapeDBO lookupLandscape(String landscape_code) throws ConfigurationException {
    InformationLandscapeDBO result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(LOOKUP_LANDSCAPE_OQL);
      q.setParameter(0,landscape_code, Hibernate.STRING);
      result = (InformationLandscapeDBO) q.uniqueResult();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
    return result; 
  }

  public Iterator enumerateLandscapes() throws ConfigurationException {
    Iterator result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(LANDSCAPE_QRY);
      result = q.iterate();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
                                                                                                                                        
    return result;
  }

  private AppSettingDBO lookupSetting(String name) throws ConfigurationException {
    AppSettingDBO result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(LOOKUP_PROPERTY_QRY);
      q.setParameter(0,name, Hibernate.STRING);
      result = (AppSettingDBO) q.uniqueResult();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
    return result; 
  }

  public String getAppProperty(String name) throws ConfigurationException {
    AppSettingDBO setting = lookupSetting(name);
    if ( setting != null )
      return setting.getValue();

    return null;
  }

  public void setAppProperty(String name, String value) throws ConfigurationException {
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(LOOKUP_PROPERTY_QRY);
      q.setParameter(0,name, Hibernate.STRING);
      AppSettingDBO current_setting = (AppSettingDBO) q.uniqueResult();

      if ( current_setting != null ) {
        current_setting.setValue(value);
      }
      else {
        current_setting = new AppSettingDBO(name,value);
      }

      session.saveOrUpdate(current_setting);
      session.flush();
      session.connection().commit();
    }
    catch ( java.sql.SQLException sqle )
    {
      throw new ConfigurationException(sqle.toString());
    }
    catch(org.hibernate.HibernateException he)
    {
      throw new ConfigurationException(he.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }

  public Iterator getAppPropertyNames() throws ConfigurationException {
    Iterator result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(APP_PROPERTY_NAMES_QRY);
      result = q.iterate();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
    return result;
  }

  public Iterator enumerateInfoTypes() throws ConfigurationException {
    Iterator result = null;
    Session session = null;
    try {
      session = sf.openSession();
      Query q = session.createQuery(INFO_TYPES_QUERY);
      result = q.iterate();
    }
    catch ( Exception e ) {
      throw new ConfigurationException(e.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
    return result;
  }

  /**
   * Lookup or create a collection info type of the form namespace:code, for example "Medium:AudoVisual" or "MIME:Application/XML"
   * to assert that a collection holds content of that type.
   */
  public CollectionInfoTypeDBO lookupOrCreateCollectionInfoType(String namespace, String code) throws ConfigurationException {
    CollectionInfoTypeDBO infotype = null;
    Session session = null;
    try {
      session = sf.openSession();
      // session.clear();
      Query q = session.createQuery(LOOKUP_INFOTYPE_QRY);
      q.setParameter(0,namespace,Hibernate.STRING);
      q.setParameter(1,code,Hibernate.STRING);
      infotype = (CollectionInfoTypeDBO) q.uniqueResult();
      if ( infotype == null ) {
        infotype = new CollectionInfoTypeDBO(namespace, code);
        session.saveOrUpdate(infotype);
        session.flush();
        session.connection().commit();
      }
    }
    catch ( java.sql.SQLException sqle ) {
      throw new ConfigurationException(sqle.toString());
    }
    catch(org.hibernate.HibernateException he) {
      throw new ConfigurationException(he.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
    return infotype;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public void tagCollection(String collection_id, String posting_namespace, String tag) {
    log.debug("tagCollection("+collection_id+","+posting_namespace+","+tag+")");
  }

  public Map getBackendPlugins() {
    return backend_plugins;
  }

  public void addOrUpdate(SearchServiceDTO svc) throws org.jzkit.configuration.api.ConfigurationException {
    // If no service with the given code exists create one, otherwise update it.
    if ( ( svc != null ) && ( svc.svc_code != null ) ) {
      Session session = null;
      try {
        log.debug("addOrUpdate SearchServiceDTO");
        session = sf.openSession();
        Query q = session.createQuery(SERVICE_DESC_QRY);
        q.setParameter(0,svc.svc_code, Hibernate.STRING);
        SearchServiceDescriptionDBO ssd = (SearchServiceDescriptionDBO) q.uniqueResult();
        if ( ssd == null ) {
          ssd = new SearchServiceDescriptionDBO();
          ssd.setCode(svc.svc_code);
        }
        ssd.setServiceShortName(svc.svc_name);
        ssd.setServiceName(svc.svc_name);
        ssd.setClassName(svc.svc_class);

        if ( svc.properties != null ) {
          for ( java.util.Iterator i = svc.properties.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            if ( e != null ) {
              String prop_name = e.getKey().toString();
              String prop_value = e.getValue().toString();
              log.debug("store preference "+prop_name+"="+prop_value);
              ssd.getPreferences().put(prop_name,prop_value);
            }
            else {
              log.error("Map entry was null");
            }
          }
        }
        else {
          log.info("No preferences in SSD DTO");
        }

        if ( svc.archetypes != null ) {
         for ( java.util.Iterator i = svc.properties.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            if ( e != null ) {
              String prop_name = e.getKey().toString();
              String prop_value = e.getValue().toString();
              log.debug("store archetype "+prop_name+"="+prop_value);
              ssd.getRecordArchetypes().put(prop_name,prop_value);
            }
            else {
              log.error("Map entry was null");
            }
          }
        }
        else {
          log.info("No archetypes in SSD DTO");
        }

        if ( svc.collections != null ) {
         for ( java.util.Iterator i = svc.collections.iterator(); i.hasNext(); ) {
            CollectionDefDTO coll_info = (CollectionDefDTO) i.next();
            org.jzkit.ServiceDirectory.CollectionDescriptionDBO coll = new org.jzkit.ServiceDirectory.CollectionDescriptionDBO();
            coll.setCode(coll_info.code);
            coll.setCollectionName(coll_info.name);
            coll.setLocalId(coll_info.localid);
            coll.setProfile(coll_info.profile);
            ssd.getCollections().add(coll);
          }
        }
        else {
          log.info("No archetypes in SSD DTO");
        }

        if ( svc.valid_indexes != null ) {
          for ( java.util.Iterator i = svc.valid_indexes.entrySet().iterator(); i.hasNext(); ) { 
            Map.Entry e = (Map.Entry) i.next();
            if ( e != null ) {
              String prop_name = e.getKey().toString();
              java.util.Set prop_value = (java.util.Set) e.getValue();
              log.debug("store valid "+prop_name+" attrs");
              // ssd.getRecordArchetypes().put(prop_name,prop_value);
              AttributeSetDBO valid_attr_set = ssd.getValidAttrs().get(prop_name);
              if ( valid_attr_set == null ) {
                valid_attr_set = new AttributeSetDBO();
                ssd.getValidAttrs().put(prop_name,valid_attr_set);
              }

              for ( java.util.Iterator i2 = prop_value.iterator(); i2.hasNext(); ) {
                String valid_attr = (String) i2.next();
                valid_attr_set.getAttrs().add(new AttrValue(valid_attr));
              }
            }
          }
        }
        else {
          log.info("No archetypes in SSD DTO");
        }

        if ( svc.valid_indexes != null ) {
          for ( java.util.Iterator i = svc.valid_indexes.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            if ( e != null ) {
              String prop_name = e.getKey().toString();
              String prop_value = e.getValue().toString();
              ssd.getServiceSpecificTranslations().put(prop_name,new AttrValue(prop_value));
            }
          }
        }
        else {
          log.info("No archetypes in SSD DTO");
        }


        log.debug("Save or update SSD");
        session.saveOrUpdate(ssd);
        session.flush();
        session.connection().commit();
      }
      catch ( java.sql.SQLException sqle ) {
        throw new ConfigurationException(sqle.toString());
      }
      catch(org.hibernate.HibernateException he) {
        throw new ConfigurationException(he.toString());
      }
      finally {
        try {
          if ( session != null )
            session.close();
        }
        catch ( Exception e ) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Register a new collection description
   */
  public void registerCollectionDescription(CollectionDescriptionDBO cd) throws org.jzkit.configuration.api.ConfigurationException {
    Session session = null;
    try {
      session = sf.openSession();
      session.saveOrUpdate(cd);
      session.flush();
      session.connection().commit();
    }
    catch ( java.sql.SQLException sqle ) {
      throw new ConfigurationException(sqle.toString());
    }
    catch(org.hibernate.HibernateException he) {
      throw new ConfigurationException(he.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * register the supplied service description.
   */
  public void registerSearchService(SearchServiceDescriptionDBO ssd) throws org.jzkit.configuration.api.ConfigurationException {
    Session session = null;
    try {
      session = sf.openSession();

      log.debug("Checking no ssd with code "+ssd.getCode()+" is present");
      Query q = session.createQuery(SERVICE_DESC_QRY);
      q.setParameter(0,ssd.getCode(), Hibernate.STRING);
      SearchServiceDescriptionDBO result = (SearchServiceDescriptionDBO) q.uniqueResult();
      if ( result == null ) {
        log.debug("Verifying attributes");
        for ( Iterator valid_attr_types = ssd.getValidAttrs().values().iterator(); valid_attr_types.hasNext(); ) {
          AttributeSetDBO attr_set = (AttributeSetDBO) valid_attr_types.next();
          session.saveOrUpdate(attr_set);
        }
        log.debug("flushing attr sets");
        session.flush();
  
        log.debug("Creating SSD");
        session.saveOrUpdate(ssd);
        session.flush();
        session.connection().commit();
      }
      else {
        log.info("Search service with code "+ssd.getCode()+" already exists");
      }
    }
    catch ( java.sql.SQLException sqle ) {
      throw new ConfigurationException("Problem registering search service",sqle);
    }
    catch(org.hibernate.HibernateException he) {
      throw new ConfigurationException("Problem registering search service",he);
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Register the specified profile
   */
  public void registerProfile(ProfileDBO p) throws ConfigurationException {
    Session session = null;
    try {
      session = sf.openSession();
      session.saveOrUpdate(p);
      session.flush();
      session.connection().commit();
    }
    catch ( java.sql.SQLException sqle ) {
      throw new ConfigurationException(sqle.toString());
    }
    catch(org.hibernate.HibernateException he) {
      throw new ConfigurationException(he.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }
                                                                                                                                        
  public void registerCrosswalk(CrosswalkDBO crosswalk) throws ConfigurationException {
    Session session = null;
    try {
      session = sf.openSession();
      session.saveOrUpdate(crosswalk);
      session.flush();
      session.connection().commit();
    }
    catch ( java.sql.SQLException sqle ) {
      throw new ConfigurationException(sqle.toString());
    }
    catch(org.hibernate.HibernateException he) {
      throw new ConfigurationException(he.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }

  public void registerRecordModelConverterType(RecordTransformerTypeInformationDBO info) throws ConfigurationException {
    Session session = null;
    try {
      session = sf.openSession();
      session.saveOrUpdate(info);
      session.flush();
      session.connection().commit();
    }
    catch ( java.sql.SQLException sqle ) {
      throw new ConfigurationException(sqle.toString());
    }
    catch(org.hibernate.HibernateException he) {
      throw new ConfigurationException(he.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }

  public void registerRecordModelMapping(RecordMappingInformationDBO info) throws ConfigurationException {
    Session session = null;
    try {
      session = sf.openSession();
      session.saveOrUpdate(info);
      session.flush();
      session.connection().commit();
    }
    catch ( java.sql.SQLException sqle ) {
      throw new ConfigurationException(sqle.toString());
    }
    catch(org.hibernate.HibernateException he) {
      throw new ConfigurationException(he.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }

  public void registerLandscape(InformationLandscapeDBO landscape) throws ConfigurationException {
    Session session = null;
    try {
      session = sf.openSession();
      session.saveOrUpdate(landscape);
      session.flush();
      session.connection().commit();
    }
    catch ( java.sql.SQLException sqle ) {
      throw new ConfigurationException(sqle.toString());
    }
    catch(org.hibernate.HibernateException he) {
      throw new ConfigurationException(he.toString());
    }
    finally {
      try {
        if ( session != null )
          session.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }

}
