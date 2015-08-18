package org.jzkit.search.impl;

import org.jzkit.search.*;
import org.jzkit.configuration.api.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.ServiceDirectory.*;
import org.springframework.context.*;
import java.util.*;
import org.jzkit.search.provider.iface.SearchException;
import java.lang.ref.WeakReference;


/**
 * Title:       SearchSessionFactory
 * @version:    $Id: SearchSessionFactoryImpl.java,v 1.9 2005/10/05 08:57:47 ibbo Exp $
 * Copyright:   Copyright 2003,2004 Ian Ibbotson, Knowledge Integration Ltd
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Description:
 *              
 */

public class SearchSessionFactoryImpl implements SearchSessionFactory, ApplicationContextAware
{
  private ApplicationContext ctx;
  private StatelessQueryService stateless_query_service = null;
  private Map<String,WeakReference> sessions = new java.util.WeakHashMap<String,WeakReference>();

  public SearchSessionFactoryImpl() {
  }

  public SearchSession getSearchSession() {
    SearchSession result = (SearchSession) ctx.getBean("SearchSession");
    sessions.put(result.getSessionId(), new WeakReference(result));
    return result;
  }

  public SearchSession getSearchSession(String session_type) {
    SearchSession result = (SearchSession) ctx.getBean("SearchSession");
    result.setType(session_type);
    sessions.put(result.getSessionId(), new WeakReference(result));
    return result;
  }

  public StatelessSearchResultsPageDTO getResultsPageFor(String result_set_id,
                                                         QueryModel model,
                                                         LandscapeSpecification landscape,
                                                         int first_hit,
                                                         int num_hits,
                                                         RecordFormatSpecification request_spec,
                                                         ExplicitRecordFormatSpecification display_spec,
                                                         Map additional_properties) 
                                 throws SearchException,
                                        org.jzkit.search.util.ResultSet.IRResultSetException,
                                        org.jzkit.search.util.QueryModel.InvalidQueryException {

    return stateless_query_service.getResultsPageFor(result_set_id,
                                                     model,
                                                     landscape,
                                                     first_hit,
                                                     num_hits,
                                                     request_spec,
                                                     display_spec,
                                                     additional_properties);
  }

  public StatelessSearchResultsPageDTO getResultsPageFor(String result_set_id,
                                                         String model_type,
                                                         String model_str,
                                                         String landscape_str,
                                                         int first_hit,
                                                         int num_hits,
                                                         RecordFormatSpecification request_spec,
                                                         ExplicitRecordFormatSpecification display_spec,
                                                         Map additional_properties) 
                                  throws SearchException, 
                                         org.jzkit.search.util.ResultSet.IRResultSetException, 
                                         org.jzkit.search.util.QueryModel.InvalidQueryException {
    return stateless_query_service.getResultsPageFor(result_set_id,
                                                     model_type,
                                                     model_str,
                                                     landscape_str,
                                                     first_hit,
                                                     num_hits,
                                                     request_spec,
                                                     display_spec,
                                                     additional_properties);
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public void init() {
    stateless_query_service = (StatelessQueryService) ctx.getBean("StatelessQueryService");
  }

  public ExplainInformationDTO explain() {

    ExplainInformationDTO result = new ExplainInformationDTO();

    Configuration directory = (Configuration) ctx.getBean("JZKitConfig");
    try {
      // Populate explain information
      for ( java.util.Iterator i = directory.enumerateVisibleCollections(); i.hasNext(); ) {
        CollectionDescriptionDBO ci = (CollectionDescriptionDBO) i.next();
        RecordMappingInformationDBO rec_info = new RecordMappingInformationDBO();
        result.getDatabaseInfo().add(rec_info);
        // Add collection information
      }

      // CollectionInfoTypeDBO ci = (CollectionInfoTypeDBO) i.next();
    }
    catch ( ConfigurationException ce) {
      ce.printStackTrace();
    }
    finally {
      // if ( directory != null ) {
      //   directory.close();
      // }
    }

    return result;
  }

  public List<SearchSession> activeSessions() {
    List<SearchSession> result = new java.util.ArrayList<SearchSession>();

    for ( java.util.Iterator i = sessions.values().iterator(); i.hasNext(); ) {
      WeakReference wr = (WeakReference) i.next();
      SearchSession ss = (SearchSession) wr.get();
      if ( ss != null )
        result.add(ss);
    }

    return result;
  }

  public SearchSession getSession(String id) {
    SearchSession result = null;
    WeakReference wr = sessions.get(id);
    if ( wr != null )
      result = (SearchSession) wr.get();

    return result;
  }
}
