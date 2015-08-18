// Title:       Z3950Origin
// @version:    $Id: Z3950Origin.java,v 1.21 2005/10/27 16:51:52 ibbo Exp $
// Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ibbo@k-int.com)
// Company:     KI
// Description: 
//


//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2.1 of
// the license, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite
// 330, Boston, MA  02111-1307, USA.
// 

package  org.jzkit.search.provider.z3950;

import java.io.*;
import java.util.*;
import java.io.IOException;

import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;


// for OID Register
import org.jzkit.a2j.codec.util.*;

// Information Retrieval Interfaces
import org.jzkit.search.provider.iface.*;

import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.z3950.QueryModel.*;
import org.jzkit.z3950.RecordModel.*;
import org.jzkit.z3950.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// For weak reference to AbstractIRResultSet
import java.lang.ref.*;

// For copying properties from the config object to an association object
import org.apache.commons.beanutils.BeanUtils;

import org.springframework.context.*;

public class Z3950Origin implements APDUListener, Searchable, Scanable {

  private ZEndpoint assoc = null;
  private boolean assoc_is_accepting_searches   = false;
  protected OIDRegister reg = null;

  // Hashtable of **weak** references to active search objects
  private Hashtable active_searches     = new Hashtable();
  private Hashtable outstanding_requests   = new Hashtable();

  private int ref_counter;

  private static Log log = LogFactory.getLog(Z3950Origin.class);

  public static int dbg_count           = 0;
  private List outstanding_queries       = new ArrayList();
  private boolean supports_named_result_sets   = true;
  private boolean supports_scan         = false;

  // Should we attempt to work around broken REFID implementatations?

  // Should we use z39.50 reference id to control concurrent requests?

  // Are concurrent operations supported?
  private boolean target_supports_concurrent_operations = true;

  private String last_search_refid   = null;
  private String last_present_refid = null;

  private Object op_count_lock     = new Object();
  private int outstanding_operations= 0;

  // Helper to convert the stream of Observable/Observer events into notification calls agaist
  // the APDUListener interface of this object.
  private GenericEventToOriginListenerAdapter message_adapter = null;

  private String host; 
  private int port; 
  private String default_record_syntax="sutrs"; 
  private String default_record_schema; 
  private String default_element_set_name="F"; 
  private int pref_message_size = 1048576; 
  private int exceptional_message_size = 5242880; 
  private String charset_encoding="UTF-8"; 
  private boolean use_reference_id=true; 
  private String service_name;
  private String service_id;
  private int auth_type=0;
  private String service_user_principal=null;
  private String service_user_group=null;
  private String service_user_credentials=null;
  private Map archetypes=new HashMap();
  private boolean do_charset_neg = true;

  private ApplicationContext ctx = null;

  /**
   * Constructor
   */
  public Z3950Origin(OIDRegister reg) {
    dbg_count++;
    this.reg = reg;
    System.err.println("Z3950Origin::Z3950Origin() ("+dbg_count+" active) reg="+reg);
  }

  public Z3950Origin() {
    dbg_count++;
    System.err.println("Z3950Origin::Z3950Origin() ("+dbg_count+" active) reg="+reg);
  }
  /**
   * Finalizer
   */
  protected void finalize() {
    dbg_count--;
    log.debug("Z3950Origin::finalize() ("+dbg_count+" active)");
    assoc=null;
    active_searches=null;
  }

  /**
   * close : Clean up the object and stop any active tasks.
   * @return void;
   */
  public void close() { 
    log.debug("Z3950Origin::close()");


    if ( assoc != null ) {
      if ( message_adapter != null ) {
        assoc.getPDUAnnouncer().deleteObserver(message_adapter);
        message_adapter = null;
      }

      outstanding_requests.clear();
      active_searches.clear();

      try {
        assoc.shutdown();
        // log.debug("Waiting for assoc thread...");
        // assoc.join();
        // log.debug("Done waiting for assoc thread...");
      }
      catch ( Exception e ) {
        log.warn("Problem...",e);
      }
    }
  }

  public IRResultSet evaluate(IRQuery q) {
    log.debug("Z3950Origin::evaluate(...)");
    return evaluate(q,null,null);
  }

  /**
   * evaluate : Create a Z39.50 Search Task.
   *
   * @param  IRQuery : What to search for
   * @param  Object : User Data, a user defined object kept for context with this task.
   * @return IRResultSet.
   */
  // Create a task
  public IRResultSet evaluate(IRQuery q, Object user_data) {
    log.debug("Z3950Origin::evaluate(...)");

    return evaluate(q,user_data,null);
  }

  /**
   * evaluate : Create a Z39.50 Search Task.
   *
   * @param  IRQuery : What to search for
   * @param  Object : User Data, a user defined object kept for context with this task.
   * @param  Observer[] observers : Any observers wanting to track this search task.
   * @return IRResultSet.
   */
  public IRResultSet evaluate(IRQuery q, Object user_info, Observer[] observers) {
    int timeout = -1;
    log.debug("Z3950Origin::evaluate(...,observers)");

    // Create a new search task ( A search ) 
    ExplicitRecordFormatSpecification default_spec = new ExplicitRecordFormatSpecification(default_record_syntax, null, default_element_set_name);
    Z3950SearchTask st = new Z3950SearchTask(this, observers, default_spec);
    st.setStatus(IRResultSetStatus.UNFILLED);
    st.setQuery(q);

    String task_id = st.getSetID();

    active_searches.put(task_id, new WeakReference(st));

    log.debug("evaluating the search task....");

    st.setStatus(IRResultSetStatus.SEARCHING );
    evaluateTask(st, timeout);

    return st;
  }

  /**
   * evaluateTask. Just a hack for now to cater for revised Searchable/AbstractIRResultSet refactoring of
   * createTask/evaluate. Should only be called by Z3950SearchTask.
   *
   * @param  AbstractIRResultSet : Task to evaluate
   * @param  int wait_for : Max timeout
   * @return void.
   */
  public void evaluateTask(AbstractIRResultSet st, int wait_for) {
    log.debug("Z3950Origin::evaluateTask(...,observers)");

    IRQuery q = st.getQuery();
    String task_id = st.getSetID();
    String refid = task_id+":srch";

    Z3950SearchTask zst = (Z3950SearchTask)st;

    log.debug("Send query");
    // if ( log.isDebugEnabled() )
    // {
    //   try
    //   {
    //     StringWriter sw = new StringWriter();
    //     org.jzkit.util.RPNQueryRep.PrefixQueryVisitor.visit(q.query.toRPN(),sw);
    //     log.debug("Query as RPN Is : "+sw.toString());
    //   }
    //   catch ( Exception iqe )
    //   {
    //     log.warn("Problem converting query",iqe);
    //   }
    // }

    if ( assoc_is_accepting_searches ) {
      // If the connection is already up, Just send the search.
      try {
        log.debug("Sending query to remote repository");
        zst.setPrivateStatusCode(Z3950SearchTask.ZSTATUS_SEARCHING);
        log.debug("Sending query directly to target");
        sendQuery(q,null,task_id,refid);
        // sendQuery(q,st.getUserData(),task_id,refid);
        ((Z3950SearchTask)st).z3950_status = 1;
      }
      catch ( java.io.IOException ioe ) {
        // Set failed status on tracker and add log reason for failure with tracker also.
        // May also need to invalidate association....???
        // zst.setDiagnosticStatus("diag.k-int.1", target_name, ioe.toString());
        zst.setDiagnosticStatus("diag.k-int.1", service_name, ioe.toString());
        zst.setPrivateStatusCode(Z3950SearchTask.ZSTATUS_ERROR);
        st.setStatus(IRResultSetStatus.FAILURE);
        log.error(ioe.toString(),ioe);
      }
      catch ( InvalidQueryException iqe )
      {
        log.error("Invalid query exception "+iqe.toString(),iqe);
        zst.setDiagnosticStatus("diag.k-int.5", service_name, iqe.toString());
        zst.setPrivateStatusCode(Z3950SearchTask.ZSTATUS_ERROR);
        st.setStatus(IRResultSetStatus.FAILURE);
      }
      catch ( SearchException se )
      {
        log.error("Search exception "+se.toString(),se);
        zst.setDiagnosticStatus("diag.k-int.3",service_name, se.toString() );
        zst.setPrivateStatusCode(Z3950SearchTask.ZSTATUS_ERROR);
        st.setStatus(IRResultSetStatus.FAILURE);
      }
    }
    else {
      log.debug("Association is not yet active, queue the search for later");
      // Add the query to the queue of waiting queries
      synchronized(outstanding_queries) {
        // outstanding_queries.add(new PendingSearch(q,st.getUserData(),task_id,refid,st));
        outstanding_queries.add(new PendingSearch(q,null,task_id,refid,st));
      }

      // And then check the connection
      checkConnection();
    }

    // One way or another, we have sent a query to the remote target. Either the assoc was
    // already accepting targets, or we have queued the search and asked the assoc to be 
    // brought up. Now we hang around and wait to see if any response comes along before
    // the timeout.
    try {
      if ( wait_for >= 0 ) {
        // Here we should be checking st.getTaskStatus for IRResultSetStatus.TASK_COMPLETE or IRResultSetStatus.TASK_FAILURE
        log.debug("evaluateQuery is waiting for up to "+wait_for+" ms task status complete or failure");
        st.waitForStatus(IRResultSetStatus.COMPLETE | IRResultSetStatus.FAILURE, wait_for);
      }
      else {
        log.debug("timeout < 0.");
      }
    }
    catch ( IRResultSetException tee ) {
      log.debug("Timeout waiting for search response");
      // Should probably re-throw adding search tracker to exception, since it's
      // OK for an operation to take longer than timeout millis to complete, just
      // nice if it does complete. Do we want to force users of the API to check
      // the task status after every little operation... I guess not!
      // throw new TimeoutExceededException();
    }
  }

 
  private void sortResultSet(List setnames_to_sort,
                             String target_set,
                             String refid,
                             String sort_specification) {
  }

  private void sendQuery(IRQuery q,
                         Object user_info,
                         String task_id,
                         String refid) throws SearchException, IOException, InvalidQueryException {
    sendQuery(q,user_info,task_id,refid,charset_encoding);
  }
  /**
   * evaluate a query, assume that the connection is up and running.
   */
  private void sendQuery(IRQuery q,
                         Object user_info,
                         String task_id,
                         String refid,
                         String encoding) throws SearchException, IOException, InvalidQueryException {

    synchronized(op_count_lock) {
      outstanding_operations++;
    }

    String small_set_setname = null;
    String default_element_set_name = null;
    ArrayList v = new ArrayList(q.collections);

    log.debug("Collections: "+v);

    last_search_refid=refid;

    log.debug("-> Sending search request with ID: "+refid+" recsyn is "+default_record_syntax);

    Z3950QueryModel qry = null;
                                                                                                                                          
    // First thing we need to do is check that query is an instanceof Type1Query or to do a conversion...
    if ( q.query instanceof Z3950QueryModel )
      qry = (Z3950QueryModel) q.query;
    else
      qry = Type1QueryModelBuilder.buildFrom(ctx, q.query, encoding != null ? encoding : "utf-8" );

    log.debug("Result of transformation is of class "+qry.getClass()+" = "+qry);

    if ( default_record_syntax == null )
      throw new SearchException("default record syntax is null for this association.");

    // Call sendSearchRequest with appropriate parameters ( task_id is used as result set name )
    assoc.sendSearchRequest(v, 
                            qry.toASNType(),
                            refid, 
                            0,          // Small Set Upper Bound was 0
                            200,        // Large Set Lower Bound was 200
                            0,          // Medium Set Present Number was 0
                            true, 
                            ( supports_named_result_sets == true ? task_id : "default" ), 
                            small_set_setname != null ? small_set_setname : "F", 
                            default_element_set_name != null ? default_element_set_name : "B", 
                            reg.oidByName(default_record_syntax));
  }

  public PresentResponse_type fetchRecords(                           String task_id,
                                           ExplicitRecordFormatSpecification spec,
                                                                         int start, 
                                                                         int count, 
                                                                         int wait_for) throws IRResultSetException {

    log.debug("Z3950Origin::fetchRecords("+task_id+","+ spec.getSetname()+","+start+","+count+","+wait_for+")");
    if ( assoc == null )
      throw new IRResultSetException("Connection to "+service_name+" seems to have died. Cannot request records");

    synchronized(op_count_lock) {
      outstanding_operations++;
    }

    log.debug("Z3950Origin::fetchRecords() from "+service_name);
    PresentResponse_type retval = null;
    String schema = "";
    // Hmmm.. The III target only returns the first 15 characters of the refid.... How rude!
    // String refid = task_id+":"+(ref_counter++)+":"+start+":"+count+":"+setname+":"+setname+":present";

    String refid = null;  
    refid = task_id+":"+(ref_counter++);
    last_present_refid = refid;
  
    log.debug("Set refid to "+refid); 
  
    // Set up the semaphore to wait for a response message _before_ sending the
    // PDU, since we would hate for the response to arrive in-between sending the
    // request and us completing setup of the semaphore.
    ReferencedPDUAvaialableSemaphore s = new ReferencedPDUAvaialableSemaphore(refid, assoc.getPDUAnnouncer());
    log.debug("About to send present request, start="+start+", count="+count+",spec="+spec);
    try {
      // We name result sets according to task_id
      assoc.sendPresentRequest(refid, 
                               (supports_named_result_sets == true ? task_id : "default"), 
                               start, 
                               count,
                               spec);

      log.debug("Waiting for present response PDU with refid "+refid);
      s.waitForCondition(wait_for); // Wait up to wait_for seconds for a response
      retval = (PresentResponse_type) s.the_pdu.o;
    }
    catch ( TimeoutExceededException tee ) {
      throw new IRResultSetException("Timeout Exception waiting for records from remote source "+service_name);
    }
    catch ( java.io.IOException ioe ) {
      log.error("IO Exception waiting for present response"+ioe,ioe);
      throw new IRResultSetException("IO Exception waiting for records from remote source "+service_name);
    }
    finally {
      s.destroy();
    }

    log.debug("fetchRecords returning presentResponse");

    if ( retval == null ) {
      log.error("Present found no records");
      throw new IRResultSetException("Failed to fetch records from remote source "+service_name);
    }

    return retval;
  }

  /** Send a present response with a registered callback target. The target must
   *  correctly support refids in order for this to work. We send a request off and
   *  then at some point, the caller will be notified when the work is complete. If
   *  the assoc is shut down before the call has been completed, the caller will be
   *  notified of the close event instead.
   */
  public void asyncFetchRecords(                           String task_id,
                                ExplicitRecordFormatSpecification spec,
                                                              int start, 
                                                              int count, 
                                                  ZCallbackTarget callback) throws IRResultSetException
  {
    log.debug("Z3950Origin::asyncfetchRecords("+task_id+","+spec.getSetname()+","+start+","+count+",notification_targets)");
    try {
      if ( assoc == null ) {
        log.warn("Connection died");
        throw new IRResultSetException("Connection to "+service_name+" seems to have died. Cannot request records");
      }

      synchronized(op_count_lock) {
        outstanding_operations++;
      }

      log.debug("Z3950Origin::asyncfetchRecords() from "+service_name);
      PresentResponse_type retval = null;
      String schema = "";
      // Hmmm.. The III target only returns the first 15 characters of the refid.... How rude!
      // String refid = task_id+":"+(ref_counter++)+":"+start+":"+count+":"+setname+":"+setname+":present";

      String refid = null;    
      refid = task_id+":"+(ref_counter++);  
      last_present_refid = refid;  
      //log.debug("Set refid to "+refid);

      // Set up the semaphore to wait for a response message _before_ sending the
      // PDU, since we would hate for the response to arrive in-between sending the
      // request and us completing setup of the semaphore.
      // We name result sets according to task_id
              
      outstanding_requests.put(refid, new OutstandingOperationInfo(refid,"Present",callback));

      log.debug("Set refid to "+refid);
      log.debug("About to send async present request, start="+start+", count="+count+",spec="+spec);

      assoc.sendPresentRequest(refid, 
                               (supports_named_result_sets == true ? task_id : "default"), 
                               start, 
                               count,
                               spec);

    }
    catch ( java.io.IOException ioe ) {
      log.error("IO Exception waiting for present response"+ioe,ioe);
    }
    finally {
    }
  }

  // Private functions
  // This function needs to throw an exception if there is something badly wrong
  // with the connect data...
  // private void checkConnection() throws SearchException
  // All new z39.50 associations will come through this function.
  private synchronized void checkConnection() {
    if ( assoc == null ) {
      log.debug("Assoc is null.... Create new association");
      try {
        log.debug("Create association and message adapter - reg="+reg);
        assoc = new ZEndpoint(reg);
        BeanUtils.copyProperties(assoc, this);

        message_adapter = new GenericEventToOriginListenerAdapter(this);
        assoc.getPDUAnnouncer().addObserver( message_adapter );

        log.debug("Calling ZEndpoint.start()");
        assoc.start();
      }
      catch( Exception e ) {
        if ( ( assoc != null ) && ( message_adapter != null ) ) {
          assoc.getPDUAnnouncer().deleteObserver( message_adapter );
        }
        assoc=null;
        message_adapter=null;

        log.error("Connect was not OK, not sending outstanding queries, and failing those queries in the queue",e);

        synchronized(outstanding_queries) {
          for ( Iterator oq = outstanding_queries.iterator(); oq.hasNext(); ) {
            PendingSearch p   = (PendingSearch) oq.next();
            Z3950SearchTask zst = (Z3950SearchTask)p.st;
            zst.setDiagnosticStatus("diag.k-int.4", service_name, "Remote target rejected connection");
            zst.setPrivateStatusCode(Z3950SearchTask.ZSTATUS_ERROR);
            zst.setStatus(IRResultSetStatus.FAILURE);
            zst.setFragmentCount(0);
          }
        }
        outstanding_queries.clear();
      }
    }
    else {
      log.debug("checkConnection: Association is present");
    }

    synchronized(this) {
      notifyAll();
    } 
  }

  // Notification Handlers

  public void incomingAPDU(APDUEvent e)
  {
    // We don't care about this
    log.info("Un-handled Generic Incoming APDU notification");
  }

  public void incomingInitResponse(APDUEvent e)
  {
    log.debug("Processing init response from "+service_name);

    InitializeResponse_type init_response = (InitializeResponse_type) (e.getPDU().o);

    if ( log.isDebugEnabled() ) {
      if ( init_response.referenceId != null )
        log.debug("  Reference ID : "+new String(init_response.referenceId));
      else
        log.debug("  Incoming refid is NULL!");

      log.debug("  Implementation ID : "+init_response.implementationId);
      log.debug("  Implementation Name : "+init_response.implementationName);
      log.debug("  Implementation Version : "+init_response.implementationVersion);

      if ( init_response.protocolVersion.isSet(0))
        log.debug("v1 Support");

      if ( init_response.protocolVersion.isSet(1))
        log.debug("v2 Support");

      if ( init_response.protocolVersion.isSet(2))
        log.debug("v3 Support");
    }


    if ( init_response.protocolVersion.isSet(2)) {
      log.debug("Sending v3 optional elements");
    }

    // Preparation for synchronous Retrieval API

    // Send any queued searches

    if ( init_response.result.booleanValue() ) {
      assoc_is_accepting_searches=true;

      if ( init_response.options.isSet(14) ) {
        log.debug("Target supports named result sets");
      }
      else {
        log.debug("Target does not support named result sets");
        supports_named_result_sets=false;
      }

      if ( init_response.options.isSet(8) ) {
        log.debug("Target claims scan support");
        supports_scan=true;
      }

      if ( init_response.options.isSet(13) ) {
        log.debug("Target claims support for concurrent operations");
      }
      else {
        log.debug("Target does not support concurrent operations");
        target_supports_concurrent_operations=false;
      }

      // If for some reason, because of the config or init negotiation, we don't want to do
      // concurrent ops on this assoc, set the assoc to only do serial ops.
      if ( !target_supports_concurrent_operations )
        assoc.setSerialOps();

      synchronized(outstanding_queries) {
        // We are ready to accept searches

        for ( Iterator oq = outstanding_queries.iterator(); oq.hasNext(); ) {

            PendingSearch p   = (PendingSearch) oq.next();

            Z3950SearchTask zst = (Z3950SearchTask)p.st;
            zst.setPrivateStatusCode(Z3950SearchTask.ZSTATUS_SEARCHING);
            log.debug("Sending outstanding query, task="+p.task_id);

          try {
            sendQuery(p.q, p.user_info, p.task_id, p.refid);
          }
          catch ( java.io.IOException ioe ) {
            // Set failed status on tracker and add log reason for failure with tracker also.
            // May also need to invalidate association....???
            log.debug(ioe.toString());
            p.st.setDiagnosticStatus("diag.k-int.1",service_name, ioe.toString());
            p.st.setStatus(IRResultSetStatus.FAILURE);
          }
          catch ( InvalidQueryException iqe ) {
            log.debug("Invalid query exception "+iqe.toString());
            p.st.setDiagnosticStatus("diag.k-int.2",service_name, iqe.toString());
            p.st.setStatus(IRResultSetStatus.FAILURE);
          }
          catch ( SearchException se ) {
            log.debug("Search exception "+se.toString());
            p.st.setDiagnosticStatus("diag.k-int.3",service_name, se.toString());
            p.st.setStatus(IRResultSetStatus.FAILURE);
          }
        }
      }
    }
    else {
      log.error("Init was not OK, not sending outstanding queries, and failing those queries in the queue");
      synchronized(outstanding_queries) {
        for ( Iterator oq = outstanding_queries.iterator(); oq.hasNext(); ) {
          PendingSearch p   = (PendingSearch) oq.next();
          Z3950SearchTask zst = (Z3950SearchTask)p.st;
          zst.setDiagnosticStatus("diag.k-int.4", service_name, "Remote target rejected connection");        
          zst.setPrivateStatusCode(Z3950SearchTask.ZSTATUS_ERROR);
          zst.setStatus(IRResultSetStatus.FAILURE);
          zst.setFragmentCount(0);
        }
      }
    }
    outstanding_queries.clear();

    synchronized(this) {
      notifyAll();
    }
  }

  public void incomingSearchResponse(APDUEvent e) {
    log.debug("incomingSearchResponse()");

    SearchResponse_type search_response = (SearchResponse_type) e.getPDU().o;

    synchronized(op_count_lock) {
      outstanding_operations--;
    }

    if ( !use_reference_id ) {
      log.debug("Broken REFID, working around by manually setting the last Search_refid used");
      search_response.referenceId = last_search_refid.getBytes();
    }

    if ( log.isDebugEnabled() ) {
      if ( search_response.referenceId != null )
        log.debug("Search Response - Reference ID : "+new String(search_response.referenceId));
      else
        log.error("The search response has NO REFID!");

      log.debug("  Search Result : "+search_response.searchStatus);
      log.debug("  Result Count : "+search_response.resultCount);
      log.debug("  Num Records Returned : "+search_response.numberOfRecordsReturned);
      log.debug("  Next RS position : "+search_response.nextResultSetPosition);
    }

    // We need to split refid down into task-id : Srch
    StringTokenizer st = new StringTokenizer(new String(search_response.referenceId),":");

    if ( st.hasMoreTokens() ) {
      String taskid = st.nextToken();
      WeakReference wr = (WeakReference)active_searches.get(taskid);

      if ( wr != null ) {
        Z3950SearchTask tsk = (Z3950SearchTask)wr.get();

        if ( null != tsk ) {
          // We set the fragment count before announcing that the search has completed
          tsk.setFragmentCount(search_response.resultCount.intValue());
          tsk.setPrivateStatusCode(Z3950SearchTask.ZSTATUS_SEARCH_COMPLETE);

          tsk.z3950_status = 2;
          if ( search_response.searchStatus.booleanValue() ) {
            // SearchRequest completed without problems... Check to see if there was a sort order
            // If there are no sort criteria
            IRQuery q = tsk.getQuery();
            if ( ( q.sorting != null ) && ( ! q.sorting.equals("") ) ) {
              tsk.setPrivateStatusCode(Z3950SearchTask.ZSTATUS_SORTING);
              List setnames_to_sort = new ArrayList();
              setnames_to_sort.add(taskid);
              log.debug("Search task contains sort critera: "+q.sorting);
              // We should leave the task status as working, and send a sort request
              // OnSortResponse should deal with finalizing the task.
              // try
              // {
              //   assoc.sendSortRequest(tsk.getSetID()+":sort",
              //                         setnames_to_sort,
              //                         taskid,
              //                         (String) q.sorting);
              // }
              // catch ( com.k_int.util.SortSpecLang.SortStringException sse )
              // {
              //   log.debug(sse.toString());
              //   tsk.setDiagnosticStatus("diag.k-int.6",service_name,sse.toString());
              //   tsk.setStatus(IRResultSetStatus.FAILURE);
              // }
              // catch ( java.io.IOException ioe )
              // {
              //   log.debug(ioe.toString());
              //   tsk.setDiagnosticStatus("diag.k-int.1", service_name,ioe.toString());
              //   tsk.setStatus(IRResultSetStatus.FAILURE);
              // }
            }
            else {
              log.debug("No sorting instructions in task. All complete!");

              // There are no sort critera, so we are all finished.
              tsk.setStatus(IRResultSetStatus.COMPLETE );

              // Handle any piggyback records
              if ( null != search_response.records ) {
                handleRecords(tsk, search_response.records);
              }
            }
          }
          else {
            // Watch out for any diagnostic records
            if ( null != search_response.records ) {
              handleRecords(tsk, search_response.records);
            }      
            log.info("Search failure.....");
            tsk.setDiagnosticStatus("diag.k-int.3",service_name,"Search failure");
            tsk.setStatus(IRResultSetStatus.FAILURE);
          }
        }
        else {
          // The task is no longer refereced and the object has been GC'd
          log.info("The AbstractIRResultSet associated with REFID "+ new String(search_response.referenceId)+
            " is no longer referenced and has been garbage collected.");
          // remove it from the active_searches map. The task should have
          // arranged for itself to be removed from the map... Never mind
          active_searches.remove(taskid);
        }
      } 
      else {
        log.error("Unable to locate a search for the REFID "+new String(search_response.referenceId)+". REFID processing at the target may be BROKEN!");
      }
    }
    else
    {
      log.info("Unable to parse refid for search response");
    }

    // Preparation for synchronous Retrieval API. If we have sent a sort request
    // any waiters will be updated, but the status will still not be complete.
    // onSortResponse will set status to complete and update as needed.
    synchronized(this) {
      log.debug("NotifyAll");
      notifyAll();
    } 
    log.debug("End of incoming search response");
  }

  private void handleRecords(Z3950SearchTask tsk, Records_type r) {
    switch ( r.which ) {
      case Records_type.responserecords_CID :
        List records = (List)r.o;
        if ( records.size() > 0 ) {
          log.debug("  Search has records (type="+r.which+", but Z3950Origin should use MSPN of 0?");
        }
        break;
      case Records_type.nonsurrogatediagnostic_CID :
        log.debug("NonSurrogate diagnostics");
        DefaultDiagFormat_type diag = (DefaultDiagFormat_type)r.o;
        if ( diag != null )  {
          String msg = null;
          if ( ( diag.addinfo != null ) && ( diag.addinfo.o != null ) ) 
            msg = diag.addinfo.o.toString();
          else 
            msg = "No message available";

          String message = "Diagnostic ("+service_name+"): "+diag.condition+" addinfo: "+msg;
          log.info(message);
          tsk.setDiagnosticStatus("diag.bib1."+diag.condition,service_name,message);
        }
        else {
          log.error("Diagnostic NULL");
        }
        break;
      case Records_type.multiplenonsurdiagnostics_CID:
        log.debug("Multiple NonSurrogate diagnostics");
        break;
      default:
        log.debug("Unknown choice type in Records");
        break;
    }
  }

  public void incomingPresentResponse(APDUEvent e) {
    PresentResponse_type present_response = (PresentResponse_type) e.getPDU().o;

    synchronized(op_count_lock) {
      outstanding_operations--;
    }

    log.debug("Incoming PresentResponse from "+e.getSource().hashCode());
    
    if (!use_reference_id) {
      log.debug("broken refid - manually setting refid using last present refid");
      present_response.referenceId = last_present_refid.getBytes();
    }

    if ( present_response.referenceId != null ) {
      String refid = new String(present_response.referenceId);
      log.debug("Present Response - Reference ID : \""+refid+"\" target="+service_name);
      OutstandingOperationInfo ooi = (OutstandingOperationInfo) outstanding_requests.remove(refid);
      if ( ooi != null ) {
        ooi.getCallbackTarget().notifyPresentResponse(present_response);
      }
      else {
        // log.error("Unable to locate callback target or outstanding operation info for refid "+refid);
        // This situation can happen legitimately if fetchRecords is being called instead of async fetch records
      }
    }
    else {
        log.info("incomingPresentResponse::Null refid");      
    }

    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingDeleteResultSetResponse(APDUEvent e)
  {
    log.debug("Incoming DeleteResultSetResponse");

    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingAccessControlRequest(APDUEvent e)
  {
    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingAccessControlResponse(APDUEvent e)
  {
    log.debug("Incoming AccessControlResponse");

    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingResourceControlRequest(APDUEvent e)
  {
    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingResourceControlResponse(APDUEvent e)
  {
    log.debug("Incoming ResourceControlResponse");

    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingTriggerResourceControlRequest(APDUEvent e)
  {
    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingResourceReportRequest(APDUEvent e)
  {
    log.debug("Incoming ResourceReportResponse");

    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingResourceReportResponse(APDUEvent e)
  {
    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingScanRequest(APDUEvent e)
  {
    log.debug("Incoming ScanResponse");

    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingScanResponse(APDUEvent e)
  {
    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingSortRequest(APDUEvent e)
  {
    log.debug("Incoming SortResponse");

    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingSortResponse(APDUEvent e)
  {
    SortResponse_type sort_response = (SortResponse_type) e.getPDU().o;
 
    if ( log.isDebugEnabled() ) {

      log.debug("Sort Response");
      if ( sort_response.referenceId != null )
        log.debug("  Reference ID : "+new String(sort_response.referenceId));
      log.debug("  Sort Status : "+sort_response.sortStatus);
      log.debug("  Result Set Status : "+sort_response.resultSetStatus);
    }
 
    // We need to split refid down into task-id : Srch
    StringTokenizer st = new StringTokenizer(new String(sort_response.referenceId),":");
    if ( st.hasMoreTokens() )
    {
      String taskid = st.nextToken();
      WeakReference wr = (WeakReference)active_searches.get(taskid);
 
      if ( wr != null )
      {
        Z3950SearchTask tsk = (Z3950SearchTask)wr.get();
        tsk.setPrivateStatusCode(Z3950SearchTask.ZSTATUS_SORT_COMPLETE);
 
        if ( null != tsk )
        {
          switch( sort_response.sortStatus.intValue() )
          {
            case 0: 
              // success - The sort was performed successfully.
              tsk.setStatus(IRResultSetStatus.COMPLETE );
              break;
            case 1: 
              // partial-1 - The sort was performed but target encountered missing values in one or more sort elements
              tsk.setStatus(IRResultSetStatus.COMPLETE );
              break;
            case 2: 
              // The sort was not performed... See Diagnostics. resultSetStatus set if sortStatus == failure.
              log.debug ("Sort Failure, Result set status is "+sort_response.resultSetStatus);
              tsk.setDiagnosticStatus("diag.k-int.6",service_name,"Sort Failure, Result set status is "+sort_response.resultSetStatus);
              tsk.setStatus(IRResultSetStatus.FAILURE );
              break;
          }
        }
        else
        {
          // The task is no longer refereced and the object has been GC'd, remove it from
          // the active_searches map. The task should have arranged for itself to be removed from the
          // map... Never mind...
          active_searches.remove(taskid);
        }
      }
    }

    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingSegmentRequest(APDUEvent e)
  {
    log.debug("Incoming SegmentResponse");

    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingExtendedServicesRequest(APDUEvent e)
  {
    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingExtendedServicesResponse(APDUEvent e)
  {
    log.debug("Incoming ExtendedServicesResponse");

    // Preparation for synchronous Retrieval API
    synchronized(this) {
      notifyAll();
    } 
  }

  public void incomingClose(APDUEvent e)
  {
    log.debug("Z3950Origin::incomingClose");

    // Let anyone waiting for an operation to complete know that it's not going to get
    // finished!
    for ( Enumeration req_enum = outstanding_requests.elements(); req_enum.hasMoreElements(); )
    {
      OutstandingOperationInfo ooi = ( OutstandingOperationInfo ) req_enum.nextElement();
      ooi.getCallbackTarget().notifyClose("closed");
    }

    log.debug("Connection closed, if there were outstanding queries, terminate them");

    synchronized(outstanding_queries) {
      for ( Iterator oq = outstanding_queries.iterator(); oq.hasNext(); ) {
        PendingSearch p   = (PendingSearch) oq.next();
        Z3950SearchTask zst = (Z3950SearchTask)p.st;
        zst.setDiagnosticStatus("diag.k-int.4", service_name, "Remote target rejected connection");
        zst.setPrivateStatusCode(Z3950SearchTask.ZSTATUS_ERROR);
        zst.setStatus(IRResultSetStatus.FAILURE);
        zst.setFragmentCount(0);
      }
    }

    // No more outstanding requests
    outstanding_queries.clear();

    // No more requests
    outstanding_requests.clear();

    // Preparation for synchronous Retrieval API
    synchronized(this) {
      notifyAll();
    } 

    log.debug("Clearing out old z-association");
    this.assoc = null;
    this.assoc_is_accepting_searches = false;

  }

  public String getTargetDN() {
    return service_id;
  }

  public String getTargetName() {
    return service_name;
  }

  public boolean isScanSupported() {
    return supports_scan;
  }

  public ScanInformation doScan(org.jzkit.search.provider.iface.ScanRequestInfo req) {
    return null;
  }

  public String toString() {
    return "Z3950Origin - "+service_name;
  }

  // Imported from factory

  /**
   */
  public String getHost() { 
    return host; 
  }

  /**
   */
  public int getPort() { 
    return port; 
  }

  /**
   */
  public String getDefaultRecordSyntax() { 
    return default_record_syntax; 
  }

  public String getDefaultRecordSchema() {
    return default_record_schema;
  }

  public String getDefaultElementSetName() {
    return default_element_set_name;
  }

  /**
   */
  public int getPrefMessageSize() { 
    return pref_message_size; 
  }

  /**
   */
  public int getExceptionalMessageSize() { 
    return exceptional_message_size; 
  }

  /**
   */
  public String getCharsetEncoding() { 
    return charset_encoding; 
  }

  /**
   */
  public boolean getUseReferenceId() { 
    return use_reference_id; 
  }

  public String getServiceName() {
    return service_name;
  }

  public String getServiceId() {
    return service_id;
  }

  public void setHost(String host) {
     this.host = host; 
  }

  public void setPort(int port) {
     this.port = port; 
  }

  public void setDefaultRecordSyntax(String default_record_syntax) {
     this.default_record_syntax = default_record_syntax; 
  }

  public void setDefaultRecordSchema(String default_record_schema) {
     this.default_record_schema = default_record_schema;
  }

  public void setDefaultElementSetName(String default_element_set_name) {
     this.default_element_set_name = default_element_set_name;
  }

  public void setPrefMessageSize(int pref_message_size) {
     this.pref_message_size = pref_message_size; 
  }

  public void setExceptionalMessageSize(int exceptional_message_size) {
     this.exceptional_message_size = exceptional_message_size; 
  }

  public void setCharsetEncoding(String charset_encoding) {
     this.charset_encoding = charset_encoding; 
  }

  public void setUseReferenceId(boolean use_reference_id) {
     this.use_reference_id = use_reference_id; 
  }

  public void setServiceName(String service_name) {
    this.service_name = service_name;
  }

  public void setServiceId(String service_id) {
    this.service_id = service_id;
  }

  public void setRecordArchetypes(Map archetypes) {
    this.archetypes = archetypes;
  }
                                                                                                                                          
  public Map getRecordArchetypes() {
    return archetypes;
  }

  public void setAuthType(int auth_type) {
    this.auth_type = auth_type;
  }
                                                                                                                                          
  public int getAuthType() {
    return auth_type;
  }

  public String getServiceUserPrincipal() {
    return service_user_principal;
  }

  public void setServiceUserPrincipal(String service_user_principal) {
    this.service_user_principal=service_user_principal;
  }

  public String getServiceUserGroup() {
    return service_user_group;
  }

  public void setServiceUserGroup(String service_user_group) {
    this.service_user_group=service_user_group;
  }

  public String getServiceUserCredentials() {
    return service_user_credentials;
  }

  public void setServiceUserCredentials(String service_user_credentials) {
    this.service_user_credentials=service_user_credentials;
  }

  public void setDoCharsetNeg(boolean do_charset_neg) {
    this.do_charset_neg = do_charset_neg;
  }

  public boolean getDoCharsetNeg() {
    return do_charset_neg;
  }


  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
    if ( reg == null ) {
      reg = (OIDRegister) ctx.getBean("OIDRegister");
    }
  }

  public boolean connected() {
    return assoc != null;
  }
}
