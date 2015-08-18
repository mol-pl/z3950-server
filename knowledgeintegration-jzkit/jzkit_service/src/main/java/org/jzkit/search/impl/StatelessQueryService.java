/*
 * $Log: StatelessQueryService.java,v $
 * Revision 1.12  2005/10/18 11:37:18  ibbo
 * Updated
 *
 * Revision 1.11  2005/10/05 08:57:47  ibbo
 * Added props to transformations
 *
 * Revision 1.10  2005/09/20 18:21:38  ibbo
 * removed extra debug
 *
 * Revision 1.9  2005/09/20 18:12:18  ibbo
 * UPdates to stateless search api
 *
 * Revision 1.8  2005/09/20 10:10:22  ibbo
 * Updated
 *
 * Revision 1.7  2005/09/13 17:10:07  ibbo
 * All good
 *
 * Revision 1.6  2005/09/13 15:22:24  ibbo
 * Harmonise all tasks
 *
 * Revision 1.5  2005/09/13 09:51:30  ibbo
 * Updated
 *
 * Revision 1.4  2005/08/24 08:40:49  ibbo
 * Improved cache hit mechanism for stateless search
 *
 * Revision 1.3  2005/08/23 13:02:05  ibbo
 * Initial impl of stateless search api
 *
 * Revision 1.2  2005/08/22 08:48:44  ibbo
 * More work on stateless search session
 *
 * Revision 1.1  2005/08/19 15:09:57  ibbo
 * Checked in first parts of stateless search api for PNS support
 *
 */

package org.jzkit.search.impl;

import java.util.*;
import org.jzkit.configuration.api.*;
import org.jzkit.ServiceDirectory.*;
import org.jzkit.search.util.Profile.*;
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.RecordConversion.*;
import org.jzkit.search.*;
import org.jzkit.search.landscape.*;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.lang.ref.WeakReference;

/**
 * Title:       StatelessQueryService
 * @version:    $Id: StatelessQueryService.java,v 1.12 2005/10/18 11:37:18 ibbo Exp $
 * Copyright:   Copyright 2003, Ian Ibbotson, Knowledge Integration Ltd
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Description:
 *              
 */

public class StatelessQueryService implements ApplicationContextAware {

  private static Log log = LogFactory.getLog(StatelessQueryService.class);

  private ApplicationContext ctx;
  private LRUCache sessions_by_id = null;
  private Map sessions_by_query = Collections.synchronizedMap(new HashMap());
  private int cache_size = 100;
  private long session_timeout = 180000;

  private StatelessQueryService() {
    log.debug("new StatelessQueryService() - default cache size is 100");
    sessions_by_id = new LRUCache(100, session_timeout);  // 100 sessions, 3 min timeout
  }

  public StatelessQueryService(int cache_size) {
    this(cache_size,0);
  }

  public StatelessQueryService(int cache_size, long session_timeout) {
    log.debug("new StatelessQueryService("+cache_size+","+session_timeout+")");
    this.cache_size = cache_size;
    this.session_timeout = session_timeout;
    sessions_by_id = new LRUCache(cache_size, session_timeout);
  }

  protected void finalize() {
    log.debug("finalize()");
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public StatelessSearchResultsPageDTO getResultsPageFor(String result_set_id,
                                                         QueryModel model,
                                                         LandscapeSpecification landscape,
                                                         int first_hit,
                                                         int num_hits,
                                                         RecordFormatSpecification rfs,
                                                         ExplicitRecordFormatSpecification display_spec,
                                                         Map additional_properties) 
                                                 throws SearchException,
                                                        org.jzkit.search.util.ResultSet.IRResultSetException,
                                                        org.jzkit.search.util.QueryModel.InvalidQueryException {
    StatelessSearchResultsPageDTO result = null;
    CachedSearchSession cached_search_session = null;
    String query_string = null;
    try {
      query_string = model.toInternalQueryModel(ctx).toString();
    }
    catch ( Exception e ) {
      throw new org.jzkit.search.util.QueryModel.InvalidQueryException("Unable to convert query to internal query model:"+model,e);
    }

    String qryid = landscape+":"+query_string;

    log.debug("getResultsPageFor (Landscape object) - "+landscape+" "+model+" rs:"+rfs+" ds:"+display_spec);

     // If we have been given a result set ID
    if ( result_set_id != null ) {
      log.debug("got result set id "+result_set_id);
      cached_search_session = (CachedSearchSession) sessions_by_id.get(result_set_id);
      if ( cached_search_session != null )
        log.debug("Cache Hit on Session ID");
    }
    else {
      log.debug("Checking cache for the supplied query string \""+qryid+"\"");
      // log.debug("cache size: "+sessions_by_query.size()+" keys : "+sessions_by_query.keySet());
      // see if we can detect an open session for the query string
      WeakReference wr = (WeakReference) sessions_by_query.get(qryid);
      if ( wr != null ) {
        cached_search_session = (CachedSearchSession) wr.get();
        if ( ( cached_search_session != null ) && ( cached_search_session.isActive() ) ) {
          // Freshen the LRU cache of queries by ID by calling get on the result set ID. This
          // is a structural modification to the LRU map.
          if ( sessions_by_id.get(cached_search_session.getResultSetId() ) != null ) {
            log.debug("Cache Hit on Query/Landscape ID - cache size = "+sessions_by_query.size());
          }
          else {
            log.warn("Cache hit on Weak Reference for query, but get returns null. Cached object expired");
            sessions_by_query.remove(qryid);
            cached_search_session = null;
          }

        }
        else {
          log.debug("Cache Hit, but query expunged from LRU list or not active, removing from sessions_by_query");
          sessions_by_query.remove(qryid);
          cached_search_session = null;
        }
      }
    }

    if ( cached_search_session == null ) {
      log.info("Not located in cache... create new session");
      cached_search_session = newSession(landscape,model,rfs);
      if ( cache_size > 0 ) {
        log.info("Session cache enabled - count="+sessions_by_id.size());
        sessions_by_id.put(cached_search_session.getResultSetId(), cached_search_session);
        sessions_by_query.put(qryid, new WeakReference(cached_search_session));
      }
    }

    log.debug("Asking for page of results");
    if ( cached_search_session != null ) {
      result = cached_search_session.getResultsPageFor(first_hit,num_hits,rfs,display_spec,additional_properties);
    }
    else {
      log.warn("Cached search session was null!");
      throw new SearchException("Cached search session was null!");
    }

    // After obtaining the result, if there is no cache, close down the session, it's never used again
    if ( cache_size == 0 ) {
      cached_search_session.setActive(false);
      cached_search_session.close();
    }
    else {
      if ( result.search_status == IRResultSetStatus.FAILURE ) {
        // Cache is enabled but result was failure. We don't cache failed results, client must cause a retry.
        // Remove session from cache of sessions by query.
        log.debug("Removing failed search from session by query cache");
        sessions_by_query.remove(qryid);
      }
    }

    log.debug("getResultsPageFor - returning");
    return result;
  }

  public StatelessSearchResultsPageDTO getResultsPageFor(String result_set_id,
                                                         String model_type,
                                                         String query_string,
                                                         String landscape_str,
                                                         int first_hit,
                                                         int num_hits,
                                                         RecordFormatSpecification rfs,
                                                         ExplicitRecordFormatSpecification display_spec,
                                                         Map additional_properties) 
                                                 throws SearchException,
                                                        org.jzkit.search.util.ResultSet.IRResultSetException,
                                                        org.jzkit.search.util.QueryModel.InvalidQueryException {

    log.debug("getResultsPageFor (Landscape Str):"+landscape_str+" mt:"+model_type+" qs:"+query_string+" ds:"+display_spec+" rfs:"+rfs);

    StatelessSearchResultsPageDTO result = null;
    CachedSearchSession cached_search_session = null;
    QueryModel model = null;
    String qryid = landscape_str+":"+query_string;

     // If we have been given a result set ID
    if ( result_set_id != null ) {
      log.debug("got result set id "+result_set_id);
      cached_search_session = (CachedSearchSession) sessions_by_id.get(result_set_id);
      if ( cached_search_session != null )
        log.debug("Cache Hit on Result Set ID");
    }
    else {
      log.debug("Checking cache for the supplied query string \""+qryid+"\"");
      // log.debug("cache size: "+sessions_by_query.size()+" keys : "+sessions_by_query.keySet());

      // see if we can detect an open session for the query string
      // cached_search_session = (CachedSearchSession) sessions_by_query.get(qryid);
      WeakReference wr = (WeakReference) sessions_by_query.get(qryid);
      if ( wr != null ) {
        log.debug("Weak Reference Cache Hit.. confirming cache status");
        cached_search_session = (CachedSearchSession) wr.get();
        if ( cached_search_session != null ) {
          if ( sessions_by_id.get(cached_search_session.getResultSetId() ) != null ) {
            log.debug("Cache Hit on Query/Landscape ID - cache size = "+sessions_by_query.size());
          }
          else {
            log.warn("Cache hit by query ID, but session not available in lru map");
            sessions_by_query.remove(qryid);
            cached_search_session = null;
          }
        }
        else {
          log.debug("Cache Hit for weak reference, but cached session itself has been expunged, removing from sessions by query (cache size="+cache_size+")");
          sessions_by_query.remove(qryid);
        }
      }
      else {
        log.debug("No Weak Reference Cache Hit");
      }
    }

    if ( cached_search_session == null ) {
      if ( ( landscape_str != null ) &&
           ( query_string != null ) &&
           ( query_string.length() > 0 ) ) {

        log.debug("Not located in cache... create new session");

        if ( model_type.equalsIgnoreCase("cql") ) {
          model = new org.jzkit.search.util.QueryModel.CQLString.CQLString(query_string);
        }
        else if ( model_type.equalsIgnoreCase("pqf") ) {
          model = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString(query_string);
        }
        else {
          log.warn("Unsupported query");
          throw new org.jzkit.search.util.QueryModel.InvalidQueryException("Unsupported Query Type. Currently Supported types are pqf and cql");
        }

        log.debug("Storring new session using ID "+qryid);
        cached_search_session = newSession(landscapeStringToDef(landscape_str),model,rfs);
        sessions_by_id.put(cached_search_session.getResultSetId(), cached_search_session);
        sessions_by_query.put(qryid, new WeakReference(cached_search_session));
      }
      else {
        throw new SearchException("No Query or Cache ID Not Found, landscape="+landscape_str+",query_string="+query_string);
      }
    }

    if ( cached_search_session != null ) {
      log.debug("Asking for page of results");
      result = cached_search_session.getResultsPageFor(first_hit,num_hits,rfs,display_spec,additional_properties);
    }
    else {
      log.warn("Cached search session was null!");
      throw new SearchException("Cached search session was null!");
    }

    if (result != null) 
      result.result_set_idle_time = this.session_timeout;

    log.debug("Leaving getResultsPageFor, result="+result);
    return result;
  }

  private CachedSearchSession newSession(LandscapeSpecification landscape, 
                                         QueryModel model, 
                                         RecordFormatSpecification rfs) throws SearchException {
    return newSession(landscape,model,null,null,rfs);
  }

  private CachedSearchSession newSession(LandscapeSpecification landscape, 
                                         QueryModel model, 
                                         DeduplicationModel deduplication_model,
                                         SortModel sort_model,
                                         RecordFormatSpecification rfs) throws SearchException {
    log.debug("newSession");
    CachedSearchSession cached_search_session = new CachedSearchSession((SearchSession) ctx.getBean("SearchSession"), ctx);
    cached_search_session.search(landscape, model, deduplication_model, sort_model, rfs);
    return cached_search_session;
  }

  private LandscapeSpecification landscapeStringToDef(String landscape) {
    // return new InfoTypeSpecification("InfoType",landscape_str);
    MixedSpecification result = new MixedSpecification();

    String[] components=landscape.split(",");
    for ( int i=0; i<components.length; i++ ) {
      String[] def_parts = components[i].split(":");
      if ( def_parts.length == 1 ) {
        result.addSpecification(new InfoTypeSpecification("InfoType",def_parts[0]));
      }
      else {
        if ( def_parts[0].equalsIgnoreCase("infotype") ) {
          result.addSpecification(new InfoTypeSpecification("InfoType",def_parts[1]));
        }
        else if ( def_parts[0].equalsIgnoreCase("collection") ) {
          result.addSpecification(new SimpleLandscapeSpecification(def_parts[1]));
        }
        else {
          log.warn("Unhandled landscape specification component: "+def_parts[0]+":"+def_parts[1]);
        }
      }
    }
    return result;
  }

  public void init() {
    log.debug("init()");
  }
}
