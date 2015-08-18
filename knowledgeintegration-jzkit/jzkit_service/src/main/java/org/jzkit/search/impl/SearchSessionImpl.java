package org.jzkit.search.impl;

import java.util.*;
import org.jzkit.configuration.api.*;
import org.jzkit.ServiceDirectory.*;
import org.jzkit.search.util.Profile.*;
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.RecordBuilder.*;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.RecordConversion.*;
import org.jzkit.search.*;
import org.jzkit.search.provider.iface.SearchException;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Title:       SearchServiceImpl
 * @version:    $Id: SearchSessionImpl.java,v 1.23 2005/10/18 11:37:18 ibbo Exp $
 * Copyright:   Copyright 2003, Ian Ibbotson, Knowledge Integration Ltd
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Description:
 *              
 */

public class SearchSessionImpl implements SearchSession, ApplicationContextAware {

  private static String UNKNOWN = "UNKNOWN";
  private String session_type = UNKNOWN;
  private String principal = null;
  private String session_id;
  private static Log log = LogFactory.getLog(SearchSessionImpl.class);
  private Map all_active_searchable_objects = new HashMap();
  private static ProfileService profile_service = null;
  private FragmentTransformerService fts;
  private RecordBuilderService rbs = null;
  private ApplicationContext ctx;

  private static int inst_count = 0;

  private SearchSessionImpl() {
    session_id = new java.rmi.dgc.VMID().toString();
    log.info("new SearchSessionImpl count="+(++inst_count)+", hc="+this.hashCode()+", id="+session_id);
  }

  protected void finalize() {
    log.info("finalize SearchSessionImpl("+(--inst_count)+")");
  }

  /*
   * Use the factory to create SearchSesionInstances
   */
  protected SearchSessionImpl(ProfileService profile_service,
                              FragmentTransformerService fts,
                              RecordBuilderService rbs) throws SearchException {
    this();
    this.profile_service = profile_service;
    this.fts = fts;
    this.rbs = rbs;
  }

  public void setType(String session_type) {
    this.session_type = session_type;
  }

  public String getType() {
    return session_type;
  }

  public void setPrincipal(String principal) {
    this.principal = principal;
  }

  public String getPrincipal() {
    return principal;
  }

  public void setSessionId(String session_id) {
    this.session_id = session_id;
  }

  public String getSessionId() {
    return session_id;
  }

  public String toString() {
    return "SearchService. id:"+session_id+" ("+session_type+")";
  }

  public void close() {
    log.info("close. Releasing all session resources");
    for ( Iterator i = all_active_searchable_objects.values().iterator(); i.hasNext(); ) {
      Searchable s = (Searchable) i.next();
      log.debug("Calling close on searchable");
      s.close();
      log.debug("Done");
    }
    all_active_searchable_objects.clear();
  }

  public TransformingIRResultSet search(LandscapeSpecification landscape, 
                                        QueryModel model,
                                        DeduplicationModel deduplication_model,
                                        SortModel sort_model,
                                        RecordFormatSpecification rfs) throws SearchException {

    log.info("SearchSessionImpl::search");
    TransformingIRResultSet result = null;

    Configuration jzkit_conf = (Configuration) ctx.getBean("JZKitConfig");

    log.info("search "+landscape.getClass().getName()+","+model.getClass().getName());
    Collection collection_ids = null;
    try {

      collection_ids = evaluateLandscape(landscape, jzkit_conf);

      if ( ( collection_ids == null ) && ( collection_ids.size() == 0 ) ) {
        throw new SearchException("Landscape Specification does not identify any collections");
      }

      SimpleAggregatingResultSet internal_result_set = new SimpleAggregatingResultSet(fts,rbs,rfs);
      result = internal_result_set;

      log.info("plan: "+model+" collections:"+collection_ids);

      if ( ( collection_ids == null ) || ( collection_ids.size() == 0 ) || ( model == null ) )
        throw new SearchException("Invalid parameters for search - No collections or no query model "+landscape);

      Map queries_by_repository = new HashMap();

      // We begin with an analysis of the collection_ids that we have been passed. 
      //
      // We run through the collections and try to resolve each one into
      // A Searchable resource (and possibly a collection name therin). For example, "LC/BOOKS"
      // Might resolve into a search that needs to be sent to a Z3950 Searchable that will query
      // the library of congress. The search will itself create a search task that has only one
      // member in the collections vector, the "BOOKS" database.
      for ( Iterator i = collection_ids.iterator(); i.hasNext(); ) {
        String collection_dn = (String) i.next();
        log.debug("Looking up collection "+collection_dn);

        CollectionDescriptionDBO ci = jzkit_conf.lookupCollectionDescription(collection_dn);

        if ( ci != null ) {
          String search_service = null;
          String profile = null;
          String service_id = null;
          SearchServiceDescriptionDBO ssd = ci.getSearchServiceDescription();

          if ( ssd == null )
            throw new RuntimeException("No SearchServiceDescription available for collection "+collection_dn);

          search_service = ssd.getCode();
          profile = ci.getProfile();
          service_id = search_service+":"+profile;
          
          // Figured out a repository to use?
          if ( search_service != null ) {
            log.debug("Lookup or Create searchable for "+service_id);

            // N.B. In the real world, things are going to be more complext that this, with
            // potentially many repositories supporting a single collection
            Searchable s = (Searchable) all_active_searchable_objects.get(service_id);
  
            // Look to see if we have an active connection to that repository
            if ( s == null ) {
              try {
                log.debug("new searchable required");
                // SearchServiceDescriptionDBO ssd = jzkit_conf.lookupSearchService(search_service);
                s = ssd.newSearchable(); // Alternate version takes a User object as a param...
                s.setApplicationContext(ctx);
                all_active_searchable_objects.put(service_id, s);
              }
              catch (org.jzkit.search.provider.iface.SearchException se) {
                se.printStackTrace();
              }
            }
            
            // Are we already sending a search to this repository (And later, is the combination allowed?)
            IRQuery subq = (IRQuery) queries_by_repository.get(service_id);

            try {

              // Is there a profile to try and apply for this repository?
              if ( subq == null ) {
                String profile_name = service_id.substring(service_id.lastIndexOf(':')+1,service_id.length());

                QueryModel adapted_query_model = null;

                if ( ( profile_name != null ) && ( !profile_name.equals("null") ) && ( profile_name.length() > 0 ) ) {
                  log.debug("Processing profile "+profile_name);
  
                  // This is where we call the query rewrite service to make a query conformant to the profile needed
                  // given the identified profile.
                  adapted_query_model = profile_service.makeConformant(model,
                                                                       ssd.getValidAttrs(),
                                                                       ssd.getServiceSpecificTranslations(),
                                                                       profile_name);
  
                  log.debug("Adapted query : "+adapted_query_model);
                }
                else {
                  log.debug("Not adapting query");
                  adapted_query_model = model;
                }

                log.info("Query for repository "+service_id+" will be "+adapted_query_model);
                // No, so create a new query that will be sent to that repository
                subq = new IRQuery();
                subq.query = adapted_query_model;
                queries_by_repository.put(service_id,subq);
              }
            }
            catch ( org.jzkit.search.util.Profile.ProfileServiceException pse ) {
              log.warn("Profile Service Exception",pse);
              throw new SearchException(pse.getMessage(),pse,pse.getErrorCode());
            }

            // We assume that a collection (with mirrors hosted by different repositories) might be known
            // by different local names at different repositories. Here, we take the local name for a 
            // collection at a specific repository to use as a parameter in a query to that repository.
            log.debug("Adding "+ci.getLocalId()+" to collection names to search at "+service_id);
            subq.collections.add(ci.getLocalId());
          }
          else {
            log.error("Unable to lookup repository for collection "+collection_dn+"... Skipping...");
            throw new SearchException("Internal Error "+collection_dn, SearchException.INTERNAL_ERROR);
          }
           
        }
        else {
          log.error("Unable to locate collection information for name "+collection_dn+"... skipping...");
          throw new SearchException("Unknown Collection "+collection_dn, SearchException.UNKOWN_COLLECTION);
        }
      }

      log.debug("HSS Task contains "+queries_by_repository.size()+" child tasks");

      // Phew, now cycle through each pair in queries_by_repository and send that query to the 
      // identified searchable object
      for ( Iterator rep_ids = queries_by_repository.keySet().iterator(); rep_ids.hasNext(); ) {
        // This will yield strings like "LC:bath"
        String repos = (String) rep_ids.next();
        IRQuery query_to_send_to_this_repository = (IRQuery) queries_by_repository.get(repos);
        Searchable s = (Searchable) all_active_searchable_objects.get(repos);

        // Create the subtask
        IRResultSet subtask = s.evaluate(query_to_send_to_this_repository, null, null);
        // So we get something sensible in status reports
        subtask.setResultSetName(repos);
        internal_result_set.addSource(subtask);
      }
    }
    catch ( ConfigurationException ce) {
      throw new SearchException(ce.toString());
    }
    finally {
      log.info("Removed call to jzkit_conf.close()");
      // jzkit_conf.close();
    }

    return result;
  }

  private Collection evaluateLandscape(LandscapeSpecification landscape, Configuration jzkit_conf) throws ConfigurationException {
    log.info("processing landscape: "+landscape.getClass().getName());
    Collection result=processEvaluateLandscape(landscape,jzkit_conf);
    log.debug("Result of evaluateLandscape = "+result);
    return result;
  }

  private Collection processEvaluateLandscape(LandscapeSpecification landscape, 
                                              Configuration jzkit_conf) throws ConfigurationException {

    Collection result = null;

    if ( landscape instanceof org.jzkit.search.landscape.SimpleLandscapeSpecification ) {
      result = ((org.jzkit.search.landscape.SimpleLandscapeSpecification)landscape).toCollectionList();
    }
    else if ( landscape instanceof org.jzkit.search.landscape.InfoTypeSpecification ) {
      // Lookup the landscape and then find all collections associated with it.
      String namespace = ((org.jzkit.search.landscape.InfoTypeSpecification)landscape).getNamespace();
      String code = ((org.jzkit.search.landscape.InfoTypeSpecification)landscape).getCode();
      log.debug("Looking for collections who's where the namespace ("+namespace+") matches "+code);
      CollectionInfoTypeDBO it = jzkit_conf.lookupOrCreateCollectionInfoType(namespace, code);
      result = new ArrayList();
      for ( Iterator i = it.getCollections().iterator(); i.hasNext(); ) {
        CollectionDescriptionDBO c = (CollectionDescriptionDBO) i.next();
        result.add(c.getCode());
      }
    }
    else if ( landscape instanceof org.jzkit.search.landscape.MixedSpecification ) {
      org.jzkit.search.landscape.MixedSpecification ms = (org.jzkit.search.landscape.MixedSpecification) landscape;
      result = new ArrayList();
      for ( java.util.Iterator i = ms.getSpecList().iterator(); i.hasNext(); ) { 
        LandscapeSpecification component = (LandscapeSpecification) i.next();
        Collection c2 = evaluateLandscape(component, jzkit_conf);
        result.addAll(c2);
      }
    }
    return result;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

}
