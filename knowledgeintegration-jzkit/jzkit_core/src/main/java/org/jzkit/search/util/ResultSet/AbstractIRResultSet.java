package org.jzkit.search.util.ResultSet;

import org.jzkit.a2j.codec.util.*;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.RecordFormatSpecification;
import org.jzkit.search.provider.iface.Condition;
import org.jzkit.search.provider.iface.Diagnostic;
import org.jzkit.search.provider.iface.DiagnosticEvent;
import org.jzkit.search.provider.iface.IRQuery;
import org.jzkit.search.provider.iface.SearchTaskEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Title:       AbstractIRResultSet
 * @version:    $Id: AbstractIRResultSet.java,v 1.9 2005/10/27 16:18:03 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: A Search task manages the aggregation of a searchable object, a query
 *              and a result set. Taken together, these the state of these objects yields
 *              a search task.
 *              
 */
public abstract class AbstractIRResultSet extends Observable // implements IRResultSet
{
  public static final int TASK_MESSAGE_ERROR = 1;
  public static final int TASK_MESSAGE_WARNING     = 2;
  public static final int TASK_MESSAGE_INFO = 2;
  public static final int TASK_MESSAGE_DIAGNOSTIC = 2;

  protected int task_status_code = 0;
  protected String task_identifier = null;
  protected String result_set_name = null;

  // user_data, For example, the Z39.50 refid of a search request, or an Object containing app info
  protected Object user_data = null; 

  protected IRQuery query = null;
  protected long create_time = System.currentTimeMillis();
  protected OIDRegisterEntry requestedSyntax = null;
  protected String requestedSyntaxName = null;
  protected java.util.Queue messages = new java.util.LinkedList();
  protected String last_message = null;

  private static Log log = LogFactory.getLog(AbstractIRResultSet.class);

  public AbstractIRResultSet() {
    this.task_identifier=""+this.hashCode();
  }

  public AbstractIRResultSet(Observer[] observers) {
    this(null,observers);
  }

  public AbstractIRResultSet(String task_identifier) {
    this(task_identifier,null);
  }

  public AbstractIRResultSet(String task_identifier, Observer[] observers) {

    if ( task_identifier != null )
      this.task_identifier = task_identifier;
    else
      this.task_identifier=""+this.hashCode();

    if ( observers != null ) {
      for ( int i = 0; i<observers.length; i++ )
        addObserver(observers[i]);

      ResultSetEvent e = new ResultSetEvent(ResultSetEvent.SOURCE_RESET, null);
      setChanged();
      notifyObservers(e);
    }
  }

  /*
   * Evaluate the query, waiting at most timeout milliseconds, returning the
   * search status. IRResultSet object should be used to check
   * the final number of result records.
   * @return int - Task Status Code.
   */
  // public abstract int evaluate(int timeout) throws TimeoutExceededException, SearchException;

  
  public OIDRegisterEntry getRequestedSyntax() {
    return requestedSyntax;
  }

  public void setRequestedSyntax(OIDRegisterEntry rs) {
    requestedSyntax=rs;
  }

  public String getRequestedSyntaxName() {
    return requestedSyntaxName;
  }

  public void setRequestedSyntaxName(String rsn) {
    requestedSyntaxName=rsn;
  }


  public String getSetID() {
    return task_identifier;
  }

  // Overall task status
  public int getStatus() {
    return task_status_code;
  }


  public void setStatus(int task_status_code) {
    log.debug("Set result set("+hashCode()+", id="+getSetID()+") state to "+task_status_code);

    synchronized(this) {
      this.task_status_code=task_status_code;
      this.notifyAll();
    }

    ResultSetEvent e = new ResultSetEvent(SearchTaskEvent.ST_PUBLIC_STATUS_CODE_CHANGE, new Integer(task_status_code));
    setChanged();
    notifyObservers(e);
  } 
  
  
  /**
   * 
   *  Set the diagnostic status. This broadcasts a Daignostic event which a diagnostic observer
   *     can observe and react to
   * 
   * @param status_code             e.g. "diag.bib1.1";
   * @param target_name             the name of the target for the diagnostic
   * @param addinfo                 any additional information to include
   */
  public void setDiagnosticStatus(String status_code, String target_name, String addinfo ) {     
     Diagnostic the_diagnostic     = new Diagnostic(status_code, target_name, addinfo);
     DiagnosticEvent e             = new DiagnosticEvent(the_diagnostic);
     setChanged();
     notifyObservers(e);    
  }

  public boolean waitForStatus(int status, long timeout) throws IRResultSetException {
    return waitForCondition(new RSStatusMaskCondition(status), timeout);
  }

  /**
   * Wait for up to timeout seconds until expression condition to true.
   */
  public boolean waitForCondition(Condition condition, long timeout) throws IRResultSetException {

    long endtime = 0;
    long remain = timeout;
    boolean result = false;

    if ( timeout > 0 )
      endtime = System.currentTimeMillis() + timeout;

    while ( ( ! ( result = condition.evaluate(this) ) ) && 
            ( ( endtime == 0 ) || ( System.currentTimeMillis() < endtime ) ) ) {
      try {
        // The AbstractIRResultSet monitor will be notified when changes are made to it's status.
        synchronized(this) {
          this.wait(remain);
        }
      }
      catch( java.lang.InterruptedException ie ) {
        if ( timeout > 0 )
          remain = endtime - System.currentTimeMillis()+10;
      }
      finally {
        Thread.currentThread().yield();
      }
    }

    return result;
  }

  public int getPrivateTaskStatusCode() {
    return -1;
  }

  public String lookupPrivateStatusCode(int code) {
    return "Unknown";
  }

  // Provided so that applications can have a place to store app specific
  // information about this search. For example, the z3950 refid for the search
  // can be held here (Or as a member of an object held here) and used to
  // set the response PDU refid.
  // public void setUserData(Object o)
  // {
  //   this.user_data = o;
  // }
// 
  // public Object getUserData()
  // {
  //   return user_data;
  // }

  /** Cancel any active operation, but leave all the searchTask's data intact */
  public void cancelTask() {
  }

  public void setQuery(IRQuery query) {
    this.query = query;
  }

  public IRQuery getQuery() {
    return this.query;
  }

  public long getTaskCreationTime()
  {
    return create_time;
  }

  /** Shut down the task and release any resources, maybe notify our creating searchable. It
      is essential that this method be called on the object. */
  public void destroyTask() {
    deleteObservers();
  }

  public void requestStatusNotification() {
    // log.debug("requestStatusNotification()");
    ResultSetEvent e = new ResultSetEvent(SearchTaskEvent.STATUS_NOTIFICATION, new Integer(task_status_code));
    setChanged();
    notifyObservers(e);
  }

  public void setResultSetName(String result_set_name) {
    this.result_set_name = result_set_name;
  }

  public String getResultSetName() {
    return result_set_name;
  }

  /* Methods from IRResultSet */

  public abstract void asyncGetFragment(int starting_fragment,
                                        int count,
                                        RecordFormatSpecification spec,
                                        IFSNotificationTarget target) throws IRResultSetException;

  /** Current number of fragments physically available */
  public abstract int getFragmentCount();

  /** The size of the result set (Estimated or known) */
  public abstract int getRecordAvailableHWM();

  /** Release all resources and shut down the object */
  public abstract void close();

  /** Return a result set info object for this RS */
  public abstract IRResultSetInfo getResultSetInfo();

  public void postMessage(String message) {
    this.last_message = message;
    messages.offer(message);
  }

  public String getLastMessage() {
    return last_message;
  }

}
