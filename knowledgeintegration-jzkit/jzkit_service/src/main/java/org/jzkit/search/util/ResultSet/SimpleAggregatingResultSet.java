package org.jzkit.search.util.ResultSet;

import org.jzkit.search.util.RecordBuilder.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.RecordConversion.*;
import java.util.*;

import java.sql.Connection;
import javax.sql.DataSource;
import jdbm.*;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import jdbm.helper.StringComparator;
import jdbm.btree.BTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

/**
 * Title:       IRResultSet
 * @version:    $Id: SimpleAggregatingResultSet.java,v 1.25 2005/10/27 16:18:03 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2001,2002 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */
public class SimpleAggregatingResultSet extends AbstractIRResultSet implements Observer, TransformingIRResultSet {

  private static Log log = LogFactory.getLog(SimpleAggregatingResultSet.class);

  private Map sources = new HashMap();

  private jdbm.RecordManager recman = null;
  private BTree result_set = null;

  private int total_sources = 0;
  private int unknown_status = 0;
  private int partial_status = 0;
  private int complete_status = 0;
  private int error_status = 0;
  private boolean strict_mode = true;
  private Object counter_sync = new Object();
  private Object new_records_lock = new Object();

  protected int target_record_hwm = -1;
  protected int record_hwm = -1;

  private int total_records_available = 0;
  private int num_records_held = 0;
  private int rec_counter = 0;
  private ResultSetThread rs_aggregator_thread = null;
  private Vector pending_async_gets = new Vector();
  private FragmentTransformerService transformation_service = null;
  private RecordFormatSpecification default_format = null;
  private String results_file_name = null;
  private RecordBuilderService record_builder_service = null;
                                                                                                                                        
  // Status can go from IDLE to PARTIAL to COMPLETE and back again depending upon
  // the addition of new sources. Complete means that all the source result sets
  // know their size.. not that the records are available. Partial means that not all
  // sources are complete (or FAIL)  states are IDLE, COMPLETE, PARTIAL and FAILURE
  private int status = IRResultSetStatus.IDLE;

  private static long instance_counter = 0;

  private class SourceInfo {
    public int status =  IRResultSetStatus.IDLE;
    public IRResultSet ir_result_set = null;
    public int result_count = -1;
    public ReadAheadEnumeration rae = null;
    public int source_identifier;

    public SourceInfo(IRResultSet ir_result_set, RecordFormatSpecification default_format,int source_identifier) {
      this.ir_result_set = ir_result_set;
      this.source_identifier = source_identifier;
      this.rae = new ReadAheadEnumeration(ir_result_set, default_format, SimpleAggregatingResultSet.this);
    }
  }

  private class ResultSetThread extends Thread {
    boolean running = true;

    public ResultSetThread() {
      log.debug("new ResultSetThread for "+(SimpleAggregatingResultSet.this.hashCode()));
    }

    public void run() {

      log.debug("Start rs loop.. target_hwm="+target_record_hwm+", current="+record_hwm);

      while(running) {

        if ( target_record_hwm > record_hwm ) {
          log.debug("Read-ahead fetch of records required.. call fetchAnyNewRecords");
          SimpleAggregatingResultSet.this.fetchAnyNewRecords();
        }

        if ( running ) {
          try {
            synchronized( SimpleAggregatingResultSet.this) {
              log.debug("Waiting for more records to become available. Current hwm="+record_hwm+" id="+(SimpleAggregatingResultSet.this.hashCode()));
              SimpleAggregatingResultSet.this.wait();
            }
          }
          catch ( InterruptedException ie ) {
          }
          log.debug("Aggregate thread woken");
        }
      }
    }

    public void close() {
      this.running=false;
      this.interrupt();
    }
  }

  public SimpleAggregatingResultSet(FragmentTransformerService transformation_service,
                                    RecordBuilderService record_builder_service,
                                    RecordFormatSpecification default_format) {
    this ( transformation_service,record_builder_service,default_format,false);
  }

  public SimpleAggregatingResultSet(FragmentTransformerService transformation_service,
                                    RecordBuilderService record_builder_service,
                                    RecordFormatSpecification default_format,
                                    boolean strict_mode) {
    this.transformation_service = transformation_service;
    this.default_format = default_format;
    this.record_builder_service = record_builder_service;
    this.strict_mode = strict_mode;
    log.debug("new SimpleAggregatingResultSet : "+(++instance_counter));

    try
    {
      java.io.File results_file = null;

      String dir = System.getProperty("com.k_int.inode.tmpdir");
      if ( dir != null )
        results_file = java.io.File.createTempFile("AFS","jdbm",new java.io.File(dir));
      else
        results_file = java.io.File.createTempFile("AFS","jdbm");
                                                                                                                                        
      results_file_name = results_file.toString();
      java.util.Properties props = new java.util.Properties();
      props.put( RecordManagerOptions.CACHE_SIZE, "500" );
      props.put( RecordManagerOptions.DISABLE_TRANSACTIONS, "true" );
      recman = RecordManagerFactory.createRecordManager(results_file_name,props);
      results_file.delete();
      result_set = BTree.createInstance(recman,new StringComparator());
    }
    catch ( Exception e )
    {
      log.warn("Problem creating results file name",e);
    }

    log.debug("Creating new ResultSetThread()");
    rs_aggregator_thread = new ResultSetThread();
    rs_aggregator_thread.start();
  }

  protected void finalize() {
    log.debug("SimpleAggregatingResultSet::finalize() : "+(--instance_counter));
  }

  public void addSource(IRResultSet ir_result_set) {
    log.debug("addSource "+ir_result_set.getSetID());

    synchronized(counter_sync) {
      sources.put(ir_result_set.getSetID(),new SourceInfo(ir_result_set,default_format,total_sources));
      total_sources++;
      unknown_status++;
    }

    ir_result_set.addObserver(this);
    ir_result_set.requestStatusNotification();
  }

  public InformationFragment[] getFragment(int starting_fragment, 
		                           int count,
					   RecordFormatSpecification spec) throws IRResultSetException {

    log.debug("SimpleAggregatingResultSet::getFragment("+starting_fragment+","+count+","+spec+")");

    if ( starting_fragment < 1 )
      throw new IRResultSetException("Starting fragment out of bounds ("+starting_fragment+") must be >= 1");

    InformationFragment result[] = null;
    int top_record = starting_fragment+count-1;
    // What should we do when we ask for records beyond the end of the result set?

    int default_timeout = 20000;
    String default_timeout_str = java.lang.System.getProperty("org.jzkit.resultset.timeout");
    if ( default_timeout_str != null ) {
      int tmp_int = Integer.parseInt(default_timeout_str);
      if ( tmp_int > 0 )
        default_timeout = tmp_int;
    }
    
    ensureRecordAvailable(top_record);
    waitForResult(top_record, default_timeout);

    int num_to_present = num_records_held - starting_fragment + 1;
    if ( num_to_present > count )
      num_to_present = count;
    else if ( num_to_present < 0 ) 
      num_to_present = 0;

    log.debug("After wait, count="+getFragmentCount()+", records held="+num_records_held+" required top rec="+top_record+" num to present="+num_to_present);

    result = new InformationFragment[num_to_present];

    for ( int i = 0; i<num_to_present; i++ ) {
      AggregatedResultSetEntry are = getHeader(starting_fragment+i);
      result[i] = retrieve(are,spec);
    }

    log.debug("SimpleAggregatingResultSet::getFragment returning - result array size="+result.length);

    return result;
  }

  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification spec,
                               IFSNotificationTarget target) throws IRResultSetException {
    asyncGetFragment(starting_fragment,count,spec,null,target);
  }

  public int getFragmentCount() {
    return total_records_available;
  }

  public int getRecordAvailableHWM() {
    return 0;
  }

  public void close() {
    log.debug("close() SAR="+this.hashCode());

    // Grab hold of the source result set objects
    Collection c = sources.values();

    if ( rs_aggregator_thread != null ) {
      rs_aggregator_thread.close();
    }

    log.debug("Closing source result sets");
    for ( Iterator i = c.iterator(); i.hasNext(); ) {
      SourceInfo source = (SourceInfo)i.next();
      IRResultSet rs = source.ir_result_set;
      log.debug("Close child: "+rs);
      rs.close();
      source.ir_result_set = null;
      source.rae = null;
    }
    log.debug("Done closing source result sets");

    // Clear out all known result sets
    sources.clear();

    // Now dispose of temp collection.
    c.clear();

    // Close temp results file
    try {
      log.debug("Closing record manager");
      recman.close();

      log.debug("Deleting "+results_file_name+".db");

      java.io.File f = new java.io.File(results_file_name+".db");
      f.delete();
      f=null;
      f = new java.io.File(results_file_name+".lg");
      f.delete();
      f=null;
    }
    catch ( java.io.IOException ioe ) {
      ioe.printStackTrace();
    }

    // Should really delete file here
  }


  /**
   *  Called whenever a child query has an event to report, updates the status of this aggregated result set.
   */
  public synchronized void update(Observable o, Object arg) {

    log.debug("update.....");

    if ( arg != null ) {
      if ( arg instanceof ResultSetEvent ) {
  
        ResultSetEvent e = (ResultSetEvent)arg;
   
        if ( o instanceof IRResultSet ) {
  
          IRResultSet rs = (IRResultSet)o;
          SourceInfo si = (SourceInfo) sources.get(rs.getSetID());
  
          // If the status reported back by the result set is not the same as the one we store internally...
          if ( si.status != rs.getStatus() ) {
  
              log.debug(" Detected a state change in RS "+rs.getSetID()+" stored status is "+si.status+" new status is "+rs.getStatus());

              // Decrement the appropriate counter for the previous status
              switch ( si.status ) {
                case  IRResultSetStatus.IDLE:
                  unknown_status--;
                  break;
                case IRResultSetStatus.COMPLETE:
                  complete_status--;
                  break;
                case IRResultSetStatus.FAILURE:
                  complete_status--;
                  break;
                case IRResultSetStatus.SUBSET:
                  partial_status--;
                  break;
              }
  
              // Increment the appropriate counter for the new status
              switch ( rs.getStatus() ) {
                case IRResultSetStatus.COMPLETE:
                  complete_status++;
                  break;
                case IRResultSetStatus.FAILURE:
                  complete_status++;
                  error_status++;
                  break;
                case IRResultSetStatus.SUBSET:
                  partial_status++;
                  break;
              }
  
              // Store the new status in the local state tracker
              si.status = rs.getStatus();
  
            // Having updated the counters, if all our sources are complete,we are complete. If at least one is
            // complete, we are SUBSET, if none are complete we are still IDLE
            if ( total_sources == complete_status ) {
              // Did any fail? If so, are we in stric mode, in which case, all must FAIL
              if ( error_status > 0 ) {
                log.debug("At least 1 component searches failed");
                // If we're in strict mode *OR* all the searches failed
                if ( ( strict_mode ) || ( error_status == total_sources ) ) {
                  log.debug("In strict mode, fail entire search");
                  setStatus(IRResultSetStatus.FAILURE);
                }
                else {
                  log.debug("Not strict mode... continue");
                  setStatus(IRResultSetStatus.COMPLETE);
                }
              }
              else {
                log.debug("All component searches completed OK");
                setStatus(IRResultSetStatus.COMPLETE);
              }
            }
            else if ( complete_status > 1 ) {
              log.debug("Set status to SUBSET");
              setStatus(IRResultSetStatus.SUBSET);
            }
            else {
              log.debug("Set status to IDLE");
              setStatus(IRResultSetStatus.IDLE);
            }
          }
  
          log.debug("UPDATE: Record count, status="+si.status+", local count="+si.result_count+", rs count="+rs.getFragmentCount());
  
          // Process the number of records available from that source, if it's appropriate to do so
          if ( ( ( si.status == IRResultSetStatus.COMPLETE ) || ( si.status == IRResultSetStatus.SUBSET ) ) &&
               ( si.result_count !=  rs.getFragmentCount() ) ) {
            if ( si.result_count == -1 ) {
              si.result_count = rs.getFragmentCount();
              total_records_available += si.result_count;
            }
            else {
              int diff = rs.getFragmentCount() - si.result_count;
              total_records_available += diff;
            }
          }
          else {
            log.warn("Source Result set status is idle or failure");
          }
        }
        else {
          log.warn("Update to aggregating result set did not come from Result set, but from "+o.getClass().getName());
        }
      }
      else {
          log.warn("Update arg was not Result set event, but from "+arg.getClass().getName());
      }
    }
    else {
      log.warn("Update to aggregating result passed null arg");
    }

    log.debug("After update, "+total_records_available+" records available, Aggr-RS Status is "+getStatus());
    log.debug("total_sources="+total_sources+" complete_status="+complete_status);
  }

  protected void fetchAnyNewRecords() {
    int records_fetched = 1;
    boolean needs_notify = false;

    // We prevent any objects contributing records from owning the monitor whilst we are in
    // this loop.
    //synchronized(this) {
      while ( ( records_fetched > 0 ) && ( target_record_hwm > record_hwm ) ) {
        // log.debug("fetchAnyNewRecords inside outer while");
        records_fetched = 0;

        for ( Iterator i = sources.values().iterator(); i.hasNext(); ) {
          // log.debug("Checking for records from a source");
          SourceInfo si = (SourceInfo) i.next();

          // Is the next record available from this target?
          // if ( si.rae.hasMoreElements() ) {
          if ( si.rae.nextIsAvailable() ) {
            // log.debug("Waiting for record from enumeration....");
            InformationFragment rec = (InformationFragment) si.rae.nextElement();
            if ( rec != null ) {
              records_fetched++;
              record_hwm++;
              // log.debug("Adding a new result record");
              rec.setSourceRepositoryID(si.ir_result_set.getResultSetName());
              String source_rec_key = store(rec,si.ir_result_set.getSetID(),new Long(rec.getHitNo()),default_format);
              String header_key = "H:"+(++rec_counter);
              try {
                result_set.insert(header_key,new AggregatedResultSetEntry(si.ir_result_set.getSetID(), new Long(rec.getHitNo())),true);
                num_records_held++;
              } catch ( java.io.IOException ioe ) {
                log.error("Problem storing fragment header",ioe);
              }
            }
            else {
              log.error("**ERROR** Information Fragment was null");
            }
          }
          else {
            // log.debug("Boo.. no records available from that source");
          }
        }
  
        if ( records_fetched > 0 )
          needs_notify = true;
      }
    //}
   
    if ( needs_notify ) {
      synchronized(new_records_lock) {
        new_records_lock.notifyAll();
      }
    }
  }

  /**
   * Wait for one of the sub tasks to make a record available
   * that will be presented at position "index" in the result set.
   */
  private boolean waitForResult(int index, int wait_timeout) throws IRResultSetException {

    log.debug("Wait for result("+index+","+wait_timeout+")");

    long starttime = System.currentTimeMillis();
    boolean record_is_available = ( num_records_held >= index );
    long endtime = starttime+wait_timeout;
 
    while ( ( !record_is_available  ) && ( System.currentTimeMillis() < endtime ) ) {
      // log.debug("Enter wait loop, records_held="+num_records_held+" requested="+index);

      try {
        long time_left_to_wait = endtime - System.currentTimeMillis();

        synchronized(new_records_lock) {
          if ( num_records_held >= index ) {
            record_is_available = true;
  	  }
          else {
            // log.debug("Waiting on result set for index "+index+" current = "+getResultSetSize());
	    if ( time_left_to_wait > 0 ) {
                new_records_lock.wait(time_left_to_wait);
  	    }
            // log.debug("Done Waiting on result set for index "+index+" current = "+getResultSetSize());
          }
        }
      }
      catch( java.lang.InterruptedException ie ) {
        log.warn("someone notified the new_records_lock that records have become available");
      }

      // log.debug("Thread.yield");
      Thread.yield();
    }

    if ( num_records_held > index )
      record_is_available = true;
 
    if ( !record_is_available ) {
      log.debug("Record not avail.. required="+index+" rs size = "+getResultSetSize());
      // throw new IRResultSetException("Timeout waiting for record");
    }
 
    return true;
  }

  public int getResultSetSize() {
    return num_records_held;
  }

  /**
   * Set the hwm and notify anyone observing us of the new required target.
   */
  private void ensureRecordAvailable(int index) {
    log.debug("ensureRecordAvailable: "+index);
    target_record_hwm = index;
    synchronized(this) {
      this.notifyAll();
    }
  }

  private String store(InformationFragment f, String source_id, Long hitno, RecordFormatSpecification format) {

    String key="S:"+source_id+":"+hitno+":"+format.toString();
    log.debug("Store fragment "+key);
    try {
      if ( f != null ) {
        result_set.insert(key,f,true);
      }
    }
    catch ( java.io.IOException ioe ) {
      log.warn("Problem storing fragment",ioe);
    }
    log.debug("Store fragment complete "+key);
    return key;
  }
                                                                                                                                        
  private InformationFragment retrieve(AggregatedResultSetEntry are) {
    return retrieve(are,default_format);
  }

  private InformationFragment retrieve(AggregatedResultSetEntry are, RecordFormatSpecification format) {
    InformationFragment retval = null;
    try {
      String record_key = "S:"+are.getSourceId()+":"+are.getHitnoAtSource()+":"+format.toString();
      log.debug("trying to retrieve actual record..."+record_key);
      retval = (InformationFragment) result_set.find(record_key);

      if ( retval == null ) {
        log.debug("Attempt direct retrieval from source rs:"+are.getSourceId()+" hitno is "+are.getHitnoAtSource());
        SourceInfo si = (SourceInfo) sources.get(are.getSourceId());
        log.debug("Got SourceInfo:"+si);
        try {
          InformationFragment[] result = si.ir_result_set.getFragment(are.getHitnoAtSource().intValue(),1,format);
          if ( result.length == 1 ) {
            retval = result[0];
            result_set.insert(record_key,retval,true);
          }
        }
        catch ( org.jzkit.search.util.ResultSet.IRResultSetException irrse ) {
          log.warn("Problem fetching alternate syntax",irrse);
        }
      }
    }
    catch ( java.io.IOException ioe ) {
      log.warn("Problem fetching fragment",ioe);
    }
                                                                                                                                        
    log.debug("returnung "+retval);
    return retval;
  }

  private AggregatedResultSetEntry getHeader(int index) throws IRResultSetException {
    String header_key="H:"+index;
    AggregatedResultSetEntry are = null;
    try {
      are = (AggregatedResultSetEntry) result_set.find(header_key);
    }
    catch ( java.io.IOException ioe ) {
      throw new IRResultSetException("Problem looking up result header",ioe);
    }

    return are;
  }

  public InformationFragment[] getFragment(int starting_fragment,
                                           int count,
                                           RecordFormatSpecification request_spec,
                                           ExplicitRecordFormatSpecification transform_spec,
                                           Map additional_properties) throws IRResultSetException {

    InformationFragment[] records = getFragment(starting_fragment,count,request_spec);

    if ( transform_spec != null )
      return transformRecords(records, transform_spec, starting_fragment, additional_properties);

    return records;
  }

  public InformationFragment[] getFragment(int starting_fragment,
                                           int count,
                                           RecordFormatSpecification request_spec,
                                           ExplicitRecordFormatSpecification transform_spec) throws IRResultSetException {
    return getFragment(starting_fragment,count,request_spec,transform_spec,null);
  }
                                                                                                                                          
  /**
   * Accept a request to fetch a record in the background and then notify the target when we have
   * that fragment. If we make this return a boolean, we can say if the call completed synchronously or not.
   */
  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification request_spec,
                               ExplicitRecordFormatSpecification transformation_spec,
                               IFSNotificationTarget target,
                               Map additional_properties) throws IRResultSetException {
    // Add the request to the queue.
    // call checkOutstandingRequests (And call when events processed in loop above).
    log.debug("asyncGetFragment("+starting_fragment+","+count+","+request_spec+","+transformation_spec+","+target+")");
                                                                                                                                          
    int top_record = starting_fragment+count-1;
                                                                                                                                          
    if ( getResultSetSize() >= top_record )
    {
      log.debug("request can be met by records currently in rs, just send them");
      // The records are already available, just send them back
      InformationFragment[] retval = new InformationFragment[count];
      for ( int i=0; i<count; i++ ) {
        AggregatedResultSetEntry are = getHeader(starting_fragment+i);
        retval[i] = retrieve(are,request_spec);
      }
      target.notifyRecords(retval);
    }
    else
    {
      log.debug("Storring pending get - request will processes as soon as records are available");
      PendingGet pg = new PendingGet(starting_fragment, count, request_spec, target, transformation_spec);
      pending_async_gets.add(pg);

      // When top_record becomes available, should be caught in aggregate thread and sorted out.
      ensureRecordAvailable(top_record);
    }

  }

  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification request_spec,
                               ExplicitRecordFormatSpecification transformation_spec,
                               IFSNotificationTarget target) throws IRResultSetException {
    asyncGetFragment(starting_fragment,count,request_spec,transformation_spec,target,null);
  }

  private InformationFragment[] transformRecords(InformationFragment[] records,
                                                 ExplicitRecordFormatSpecification transform_spec,
                                                 int starting_fragment,
                                                 Map default_props) {
    InformationFragment[] result = new InformationFragment[records.length];

    for ( int i=0; i<records.length; i++ ) {
      try {
        if ( records[i] != null ) {

          Hashtable trans_props = null;

          if ( default_props != null ) 
            trans_props = new Hashtable(default_props);
          else
            trans_props = new Hashtable();

          // Step 1 - Do we need to transform, or can we just pass along anyway.
          ExplicitRecordFormatSpecification source_spec = records[i].getFormatSpecification();

          log.debug("Trying to get from "+source_spec+" to "+transform_spec);

          // Just return when - Encoding is the SAME, Schema is the SAME OR transform schema is null, Setname is same, ignore case
          if ( source_spec.getEncoding().equals(transform_spec.getEncoding()) && 
               ( ( transform_spec.getSchema() == null ) || ( source_spec.getSchema().equals(transform_spec.getSchema()) ) ) &&
               ( ( transform_spec.getSetname() == null ) || ( transform_spec.getSetname().toString().equalsIgnoreCase(source_spec.getSetname().toString() ) ) ) ) {
            // Records apparently match, just pass along
            log.debug("Request pattern matches held record, just returning");
            result[i] = records[i];
          }
          else {
            // Transform the input 
            log.debug("Determine record factory for record type "+records[i].getFormatSpecification());
            String factory_name = "org.jzkit.recordbuilder."+records[i].getFormatSpecification().getEncoding();
            log.debug("Obtain record model plugin for "+factory_name);

            log.debug("Get the xml for the input document type");
            Document d = record_builder_service.getCanonicalXML(records[i]);

            String from_schema = source_spec.getSchema().toString();
            String to_schema = transform_spec.getSchema().toString();
            Document raw_data = (Document) transformation_service.convert(d, from_schema, to_schema, trans_props);

            log.debug("Creating instance of record model");

            result[i] = record_builder_service.createFrom(starting_fragment+i,
                                                          "svc",
                                                          "coll",
                                                          null,
                                                          raw_data,
                                                          transform_spec);
          }
        }
        else {
          result[i] = new InformationFragmentImpl(starting_fragment+i,null,null,null,"No source record",new ExplicitRecordFormatSpecification("string:diag:F"));
        }
      }
      catch ( FragmentTransformationException fte ) {
        log.warn("transformRecords - Problem transforming record "+i+" into "+transform_spec,fte);
        result[i] = new InformationFragmentImpl(starting_fragment+i,null,null,null,fte.toString(),new ExplicitRecordFormatSpecification("string:diag:F"));
      }
      catch ( org.jzkit.search.util.RecordBuilder.RecordBuilderException rbe ) {
        log.warn("transformRecords - Problem transforming record "+i+" into "+transform_spec,rbe);
        result[i] = new InformationFragmentImpl(starting_fragment+i,null,null,null,rbe.toString(),new ExplicitRecordFormatSpecification("string:diag:F"));
      }
    }

    return result;
  }

  private void checkPendingFecthList() {
    log.debug("checkPendingFecthList()");
  }

  /** Override the base impl and return a result set info object for this RS, including info about child sets */
  public IRResultSetInfo getResultSetInfo() {
    List child_reports = new ArrayList();

    // Grab hold of the source result set objects
    Collection c = sources.values();

    for ( Iterator i = c.iterator(); i.hasNext(); ) {
      SourceInfo source = (SourceInfo)i.next();
      IRResultSet rs = source.ir_result_set;
      child_reports.add(rs.getResultSetInfo());
    }

    return new IRResultSetInfo(result_set_name,
                               "AGGREGATOR",
                               "SIMPLE",
                               getFragmentCount(),
                               getStatus(),
                               child_reports);
  }

}
