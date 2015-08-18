package org.jzkit.configuration.api;

import org.jzkit.ServiceDirectory.*;
import java.util.Iterator;
import java.util.Map;
import org.jzkit.search.util.Profile.ProfileDBO;
import org.jzkit.search.util.Profile.CrosswalkDBO;
import org.springframework.context.*;


public interface Configuration extends ApplicationContextAware {

  /**
   * Initialise a new configuration
   */
  public void initialise() throws ConfigurationException;

  /**
   * Indicate that we have done with this configuration object
   */
  // public void close();

  /**
   * lookup the collection information corresponding to the given ID.
   * @param collection_id Id of collection to locate
   * @return The corresponding collection description
   */
  public CollectionDescriptionDBO lookupCollectionDescription(String collection_id) throws ConfigurationException;


  /**
   * lookup the search service corresponding to the given ID.
   * @param service_id Id of service to locate
   * @return The corresponding service
   */
  public SearchServiceDescriptionDBO lookupSearchService(String service_id) throws ConfigurationException;

  /**
   * list all known collections.
   * @return an enumeration of all collections known.
   */
  public Iterator enumerateVisibleCollections() throws ConfigurationException;

  /**
   * list all known repositories.
   * @return an enumeration of all repositories known.
   */
  public Iterator enumerateRepositories() throws ConfigurationException;
 
  /**
   * Lookup or create a collection info type of the form namespace:code, for example "Medium:AudoVisual" or "MIME:Application/XML"
   * to assert that a collection holds content of that type.
   */
  public CollectionInfoTypeDBO lookupOrCreateCollectionInfoType(String namespace, String code) throws ConfigurationException;
                                                                                                                                        
  /**
   * Add a tag to a collection, for example Collection-X add Tag Infotype=Monographs
   */
  public void tagCollection(String collection_id, String posting_namespace, String tag);

  /**
   * Enumerate all known InfoTypes
   */
  public Iterator enumerateInfoTypes() throws ConfigurationException;

  /**
   * Lookup the identified profile.
   * @param id of profile to look up
   * @return Profile
   */
  public ProfileDBO lookupProfile(String profile_id) throws ConfigurationException;
  public Iterator enumerateProfiles() throws ConfigurationException;

  public CrosswalkDBO lookupCrosswalk(String source_namespace) throws ConfigurationException;
  public Iterator enumerateCrosswalks() throws ConfigurationException;

  public Iterator getRegisteredConverterTypes() throws ConfigurationException;

  public Iterator getRegisteredRecordMappings() throws ConfigurationException;

  public InformationLandscapeDBO lookupLandscape(String landscape_code) throws ConfigurationException;
  public Iterator enumerateLandscapes() throws ConfigurationException;

  public String getAppProperty(String name) throws ConfigurationException;
  public void setAppProperty(String name, String value) throws ConfigurationException;
  public Iterator getAppPropertyNames() throws ConfigurationException;

  public Map getBackendPlugins();

  // Registration methods

  /**
   * Register a new collection description
   */
  // public void registerCollectionDescription(CollectionDescriptionDBO cd) throws ConfigurationException;
  // public void registerCrosswalk(CrosswalkDBO crosswalk) throws ConfigurationException;
  // public void registerRecordModelConverterType(RecordTransformerTypeInformationDBO info) throws ConfigurationException;
  // public void registerRecordModelMapping(RecordMappingInformationDBO info) throws ConfigurationException;
  // public void registerLandscape(InformationLandscapeDBO landscape) throws ConfigurationException;
  // public void registerProfile(ProfileDBO p) throws ConfigurationException;
  // public void registerSearchService(SearchServiceDescriptionDBO ssd) throws ConfigurationException;

  public void addOrUpdate(SearchServiceDTO svc) throws ConfigurationException;
  
}
