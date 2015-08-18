/*
 * $Log: CachedSearchSession.java,v $
 * Revision 1.12  2005/10/20 15:39:53  ibbo
 * Temp files locations can now be influenced with the JVM flag com.k_int.inode.tmpdir
 *
 * Revision 1.11  2005/10/18 11:37:18  ibbo
 * Updated
 *
 * Revision 1.10  2005/10/05 08:57:47  ibbo
 * Added props to transformations
 *
 * Revision 1.9  2005/10/04 16:32:44  ibbo
 * added maps
 *
 * Revision 1.8  2005/09/20 18:12:18  ibbo
 * UPdates to stateless search api
 *
 * Revision 1.7  2005/09/20 10:10:22  ibbo
 * Updated
 *
 * Revision 1.6  2005/09/13 17:10:07  ibbo
 * All good
 *
 * Revision 1.5  2005/09/13 15:48:50  ibbo
 * Improvements to stateless search
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
import org.jzkit.search.util.QueryModel.Internal.HumanReadableQueryBundle;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.RecordConversion.*;
import org.jzkit.search.*;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.*;

/**
 * Title:       CachedSearchSession
 * @version:    $Id: CachedSearchSession.java,v 1.12 2005/10/20 15:39:53 ibbo Exp $
 * Copyright:   Copyright 2003, Ian Ibbotson, Knowledge Integration Ltd
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Description:
 *              
 */

public class CachedSearchSession {
  
  private SearchSession search_session = null;  
  private String result_set_id = null;
  private TransformingIRResultSet transforming_ir_result_set = null;
  private static Log log = LogFactory.getLog(CachedSearchSession.class);
  private long last_used = System.currentTimeMillis();
  private String query_type = null;
  private String query_string = null;
  private HumanReadableQueryBundle human_readable_bundle = null;
  private ApplicationContext ctx = null;
  protected boolean active = true;

  protected static int inst_count = 0;

  private CachedSearchSession() {
    log.info("new CachedSearchSession("+(++inst_count)+")");
  }

  public CachedSearchSession(SearchSession search_session, ApplicationContext ctx) {
    log.info("new CachedSearchSession("+(++inst_count)+")");
    this.search_session = search_session;
    this.result_set_id = new java.rmi.dgc.VMID().toString();
    this.ctx = ctx;
  }

  public void search(LandscapeSpecification landscape,
                     QueryModel model,
                     RecordFormatSpecification rfs) throws SearchException {
    search(landscape,model,null,null,rfs);
  }

  public void search(LandscapeSpecification landscape,
                     QueryModel model,
                     DeduplicationModel deduplication_model,
                     SortModel sort_model,
                     RecordFormatSpecification rfs) throws SearchException {
    transforming_ir_result_set = search_session.search(landscape, model, deduplication_model, sort_model, rfs);
    // Create a human readable version of the query
    try {
      human_readable_bundle = new HumanReadableQueryBundle();
      org.jzkit.search.util.QueryModel.Internal.HumanReadableVisitor.toHumanReadableBundle(model.toInternalQueryModel(ctx), human_readable_bundle);
    }
    catch ( java.io.IOException ioe ) {
      log.warn("Problem",ioe);
    }
    catch ( org.jzkit.search.util.QueryModel.InvalidQueryException iqe ) {
      log.warn("Problem",iqe);
    }
  }

  public String getResultSetId() {
    return result_set_id;
  }

  public StatelessSearchResultsPageDTO getResultsPageFor(int first_hit,
                                                         int num_hits,
                                                         RecordFormatSpecification rfs,
                                                         ExplicitRecordFormatSpecification display_spec,
                                                         Map additional_properties) 
                                            throws SearchException,
                                                   org.jzkit.search.util.ResultSet.IRResultSetException {
    log.debug("getResultsPageFor "+first_hit+", "+num_hits+", rfs:"+rfs+", ds:"+display_spec);

    StatelessSearchResultsPageDTO result = null;
    touch();
    // Request records

    Condition c = null;
    // If we have been asked for 0 hits, it's probably because the user wants to execute the search and get
    // back a hit count, in that case, just wait for COMPLETE or FAILURE, otherwise wait for hitno also
    if ( ( first_hit == 0 ) && ( num_hits==0 ) )
      c = new RSStatusMaskCondition(IRResultSetStatus.COMPLETE|IRResultSetStatus.FAILURE);
    else
      c = new OrCondition( new Condition[] { new ResultCountCondition(first_hit+num_hits-1),
                                              new RSStatusMaskCondition(IRResultSetStatus.COMPLETE|IRResultSetStatus.FAILURE) } );

    if ( transforming_ir_result_set.waitForCondition(c,20000) ) {
      // 1. Figure out sensible options... the user may have asked for records beyond the end of the result set
      int num_to_request = 0;
      log.debug("first hit "+first_hit+" num to request "+num_hits+" count "+transforming_ir_result_set.getFragmentCount());

      if ( first_hit  > transforming_ir_result_set.getFragmentCount() ) {
        log.debug("Present out of range");
      }
      else if ( ( first_hit+num_hits-1 ) <= transforming_ir_result_set.getFragmentCount() )
        num_to_request=num_hits;
      else
        num_to_request=transforming_ir_result_set.getFragmentCount()-first_hit+1;

      InformationFragment[] fragments = null;
      if ( num_to_request > 0 ) {
        log.debug("Calling get fragment... requesting "+num_to_request+" from "+first_hit+" spec="+display_spec);
        fragments = transforming_ir_result_set.getFragment(first_hit,
                                                           num_to_request,
                                                           rfs,
                                                           display_spec,
                                                           additional_properties);
      }
      else {
        log.debug("Not requesting any records");
      }

      result = new StatelessSearchResultsPageDTO(result_set_id, 
                                                 transforming_ir_result_set.getStatus(),
                                                 num_to_request,
                                                 first_hit,
                                                 transforming_ir_result_set.getFragmentCount(),
                                                 null,
                                                 fragments,
                                                 transforming_ir_result_set.getResultSetInfo());
    }
    else {
      // Search failure
      log.warn("Search failure in some way - most likely timeout waiting for result records");
      result = new StatelessSearchResultsPageDTO(result_set_id, 
                                                 transforming_ir_result_set.getStatus(),
                                                 transforming_ir_result_set.getFragmentCount(),
                                                 first_hit,
                                                 0,
                                                 null,
                                                 null,
                                                 transforming_ir_result_set.getResultSetInfo());
    }
    
    result.setHumanReadableQueryBundle(human_readable_bundle);

    log.debug("leaving getResultsPageFor..");
    return result;
  }

  public void close() {
    log.info("close - Closing RS and Session");
    active = false;
    transforming_ir_result_set.close();
    search_session.close();
  }

  protected void finalize() {
    log.info("finalize CachedSearchSession("+(--inst_count)+")"); 
  }

  public void touch() {
    last_used = System.currentTimeMillis();
  }

  public long getLastUsed() {
    return last_used;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    return active;
  }
}
