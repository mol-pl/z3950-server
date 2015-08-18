// Title:       A Synchronous bean for implementing a Z3950 Origin
// @version:    $Id: SynchronousOriginBean.java,v 1.6 2004/11/19 16:37:42 ibbo Exp $
// Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ibbo@k-int.com)
// Company:     KI
// Description: 
//


package org.jzkit.z3950.client;

import java.util.*;

import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import org.jzkit.z3950.gen.v3.RecordSyntax_explain.*;
import org.jzkit.z3950.util.*;
import org.jzkit.z3950.QueryModel.*;
import org.jzkit.z3950.Z3950Exception;
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.a2j.gen.AsnUseful.*;
import org.jzkit.a2j.codec.util.*;
import java.util.logging.*;
import org.springframework.context.*;

/**
 * SynchronousOriginBean : A Z3950 session that trys to behave as a synchronous local resource. IE
 * Z39.50 Requests wait for their corresponding response before returning (Or time out)
 *
 * @version:    $Id: SynchronousOriginBean.java,v 1.6 2004/11/19 16:37:42 ibbo Exp $
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 *
 */
public class SynchronousOriginBean implements APDUListener {

  protected ZEndpoint assoc = null;
  protected OIDRegister reg = null;
  protected ArrayList db_names = new ArrayList();
  protected String record_syntax = "sutrs"; // Default is to ask for brief sutrs records

  public static final int NO_CONNECTION = 1;
  public static final int CONNECTING = 2;
  public static final int CONNECTED = 3;
  // public static final String NO_RS_NAME="default";
  public static final String NO_RS_NAME="default";

  int session_status = NO_CONNECTION;

  // private Hashtable responses = new Hashtable();
  private HashMap responses = new HashMap();
  private Hashtable dbinfo = new Hashtable();

  private boolean supports_named_result_sets = true;

  protected Logger log = Logger.getLogger(SynchronousOriginBean.class.getName());

  private ApplicationContext ctx = null;

  public SynchronousOriginBean(OIDRegister reg)
  {
    db_names.add("Default");
    this.reg = reg;
  }

  public int getSessionStatus()
  {
    return session_status;
  }

  public void setSessionStatus(int i)
  {
    session_status = i;
  }

  public void setRecordSyntax(String record_syntax)
  {
    this.record_syntax = record_syntax;
  }

  public String getRecordSyntax()
  {
    return record_syntax;
  }

  public void clearAllDatabases()
  {
    db_names.clear();
  }

  public void addDatatabse(String dbname)
  {
    db_names.add(dbname);
  }

  public void disconnect()
  {
    if ( null != assoc )
    {
      System.err.println("Closing existing listener");
      try
      {
        assoc.shutdown();
      }
      catch ( java.io.IOException ioe )
      {
        ioe.printStackTrace();
      }
    }
  }

  public InitializeResponse_type connect(String hostname, int portnum)
  {
    // Close down any outstanding assoc
    disconnect();

    return connect(hostname,portnum,0,null,null,null);
  }

  public InitializeResponse_type connect(String hostname, 
                                         int portnum,
                                         int auth_type,      // 0 = none, 1=Anonymous, 2=open, 3=idpass
                                         String principal,   // for open, the access string, for idpass, the id
                                         String group,       // group
                                         String credentials) // password
  {
    System.err.println("Connect");
    InitializeResponse_type retval = null;

    try
    {
      disconnect();

      System.out.println("Create listener & encoder");
      assoc = new ZEndpoint(reg);
      assoc.setHost(hostname);
      assoc.setPort(portnum);
      assoc.setAuthType(auth_type);

      if ( principal != null )
        assoc.setServiceUserPrincipal(principal);
      if ( group != null )
        assoc.setServiceUserGroup(group);
      if ( credentials != null )
        assoc.setServiceUserCredentials(credentials);

      // Convert incoming observer/observable notifications into PDU notifications.
      assoc.getPDUAnnouncer().addObserver( new GenericEventToOriginListenerAdapter(this) );

      // Look out for the init response
      PDUTypeAvailableSemaphore s = new PDUTypeAvailableSemaphore(PDU_type.initresponse_CID, assoc.getPDUAnnouncer() );

      assoc.start();

      try
      {
        s.waitForCondition(20000); // Wait up to 20 seconds for an init response
        retval = (InitializeResponse_type) s.the_pdu.o;
      }
      catch ( Exception e )
      {
      }
      finally
      {
        s.destroy();
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    return retval;
  }

  public SearchResponse_type sendSearch(QueryModel query, 
		                        String refid,
					String setname) throws Z3950Exception, InvalidQueryException
  {
    return sendSearch(query,refid,setname,"F");
  }

  public SearchResponse_type sendSearch(QueryModel query, 
		                        String refid,
					String setname,
					String elements) throws Z3950Exception, InvalidQueryException
  {
    System.err.println("Sending search request....");
    SearchResponse_type retval = null;

    if ( refid == null )
      refid = assoc.genRefid("Search");

    // ReferencedPDUAvaialableSemaphore s = new ReferencedPDUAvaialableSemaphore(refid, assoc.getPDUAnnouncer() );
    PDUTypeAvailableSemaphore s = new PDUTypeAvailableSemaphore(PDU_type.searchresponse_CID, assoc.getPDUAnnouncer() );

    try
    {
      Z3950QueryModel qry = null;

      log.fine("Sending Search request, setname="+setname+" syntax="+record_syntax);

      // First thing we need to do is check that query is an instanceof Type1Query or to do a conversion...
      if ( query instanceof Z3950QueryModel )
        qry = (Z3950QueryModel) query;
      else
        qry = Type1QueryModelBuilder.buildFrom(ctx, query, "utf-8");
      
      assoc.sendSearchRequest(db_names, 
                              qry.toASNType(),
                              refid, 
                              0, 1, 1, true,   // ssub, lslb, mspn was 3,5,10
                              ( supports_named_result_sets == true ? setname : NO_RS_NAME ),   // Result set name
                              elements, 
                              elements, 
                              reg.oidByName(record_syntax));

      s.waitForCondition(20000); // Wait up to 20 seconds for a message of type SearchResponse
      retval = (SearchResponse_type) s.the_pdu.o;

      if ( retval.records != null )
      {
        if ( retval.records.which == Records_type.nonsurrogatediagnostic_CID )
        {
          // Record contains defaultDiagFormat object
          DefaultDiagFormat_type d = (DefaultDiagFormat_type)retval.records.o;
          if ( d.addinfo != null )
            throw new Z3950Exception("Diagnostic ["+d.condition+"] Additional Info : "+d.addinfo.o);
          else
            throw new Z3950Exception("Diagnostic ["+d.condition+"] no additional info");
        }
        else if ( retval.records.which == Records_type.multiplenonsurdiagnostics_CID )
        {
          // Record contains DiagRec ( May have externally defined info )
          DiagRec_type d = (DiagRec_type)retval.records.o;
          if ( d.which == DiagRec_type.defaultformat_CID )
          {
            DefaultDiagFormat_type dd = (DefaultDiagFormat_type) d.o;
            throw new Z3950Exception("Diagnostic ["+dd.condition+"] Additional Info : "+dd.addinfo.o);
          }
          else
            throw new Z3950Exception("Externally defined diagnostic record");
        }
        else
        {
          System.err.println("Search has valid piggyback records");
        }
      }
    }
    catch ( java.io.IOException ioe )
    {
      // Problem with comms sending PDU...
      ioe.printStackTrace();
    }
    catch ( TimeoutExceededException tee )
    {
      tee.printStackTrace();
    }
    finally
    {
      s.destroy();
    }


    return retval;
  }

  /** 
   * Alternate sendSearch that simply passes along a search request PDU. Added for proxy server.
   */
  public SearchResponse_type sendSearch(PDU_type req) throws Z3950Exception, InvalidQueryException
  {
    SearchResponse_type retval = null;
    SearchRequest_type search_req = (SearchRequest_type)req.o;

    ReferencedPDUAvaialableSemaphore s = new ReferencedPDUAvaialableSemaphore(new String(search_req.referenceId), assoc.getPDUAnnouncer() );

    try
    {
      assoc.encodeAndSend(req);

      s.waitForCondition(20000); // Wait up to 20 seconds for a response
      retval = (SearchResponse_type) s.the_pdu.o;
    }
    catch ( java.io.IOException ioe )
    {
      ioe.printStackTrace();
    }
    catch ( TimeoutExceededException tee )
    {
      tee.printStackTrace();
    }
    finally
    {
      s.destroy();
    }

    return retval;
  }

  public ScanResponse_type sendScan(QueryModel qm)
  {
    ScanResponse_type retval= null;
    PDUTypeAvailableSemaphore s = new PDUTypeAvailableSemaphore(PDU_type.scanresponse_CID, assoc.getPDUAnnouncer() );

    try
    {
      Z3950QueryModel qry = null;
                                                                                                                                          
      // First thing we need to do is check that query is an instanceof Type1Query or to do a conversion...
      if ( qm instanceof Z3950QueryModel )
        qry = (Z3950QueryModel) qm;
      else
        qry = Type1QueryModelBuilder.buildFrom(ctx, qm, "utf-8");
                                                                                                                                          
      assoc.sendScanRequest("scan", db_names, qry.toASNType(), 0, 10, 5);
      s.waitForCondition(20000);
      retval = (ScanResponse_type) s.the_pdu.o;
    }
    catch ( TimeoutExceededException tee )
    {
      tee.printStackTrace();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    finally
    {
      s.destroy();
    }

    return retval;
  }

  public PresentResponse_type sendPresent(long start, long count, String element_set_name, String setname)
  {
    PresentResponse_type retval = null;
    String refid = assoc.genRefid("Present");

    PDUTypeAvailableSemaphore s = new PDUTypeAvailableSemaphore(PDU_type.presentresponse_CID, assoc.getPDUAnnouncer() );
    // ReferencedPDUAvaialableSemaphore s = new ReferencedPDUAvaialableSemaphore(refid, assoc.getPDUAnnouncer() );

    try
    {
      log.fine("Sending present request for "+count+" records starting at "+start+", setname="+setname+" syntax="+record_syntax);

      assoc.sendPresentRequest(refid, 
                               ( supports_named_result_sets == true ? setname : NO_RS_NAME ),
                               start, 
                               count, 
                               new ExplicitRecordFormatSpecification(record_syntax,null,element_set_name));

      s.waitForCondition(20000); // Wait up to 20 seconds for a response
      retval = (PresentResponse_type) s.the_pdu.o;
    }
    catch ( TimeoutExceededException tee )
    {
      tee.printStackTrace();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    finally
    {
      s.destroy();
    }

    System.err.println("Done present request");
    return retval;
  }

  // Notification Handlers
  public void incomingAPDU(APDUEvent e)
  {
    // We don't care about this
    // System.err.println("Incoming APDU notification");
  }

  public void incomingInitRequest(APDUEvent e)
  {
    log.fine("Incoming InitRequest");

    // Preparation for synchronous Retrieval API
    notifyAll();
  }

  public void incomingInitResponse(APDUEvent e)
  {

    InitializeResponse_type init_response = (InitializeResponse_type) (e.getPDU().o);
    responses.put(init_response.referenceId, init_response);
    session_status = CONNECTED;

    log.fine("Incoming init response "+init_response.referenceId);

    if ( init_response.result.booleanValue() == true )
    {
      if ( init_response.options.isSet(14) )
        log.fine("Target supports named result sets");
      else
      {
        log.fine("Target does not support named result sets");
        supports_named_result_sets = false;
      }
    }

    synchronized(this)
    {
      notifyAll();
    }
  }

  public void incomingSearchRequest(APDUEvent e)
  {
    log.fine("\nIncoming SearchRequest");

    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    }
  }

  public void incomingSearchResponse(APDUEvent e)
  {
    SearchResponse_type search_response = (SearchResponse_type) e.getPDU().o;
    responses.put(search_response.referenceId, search_response);
    log.fine("Incoming search response "+search_response.referenceId);

    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingPresentRequest(APDUEvent e)
  {
    log.fine("Incoming PresentResponse");

    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingPresentResponse(APDUEvent e)
  {
    PresentResponse_type present_response = (PresentResponse_type) e.getPDU().o;
    responses.put(present_response.referenceId, present_response);

    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingDeleteResultSetRequest(APDUEvent e)
  {
    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingDeleteResultSetResponse(APDUEvent e)
  {
    log.fine("Incoming DeleteResultSetResponse");

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
    System.err.println("Incoming AccessControlResponse");

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
    System.err.println("Incoming ResourceControlResponse");

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
    System.err.println("Incoming ResourceReportResponse");

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
    System.err.println("Incoming ScanResponse");

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
    System.err.println("Incoming SortResponse");

    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingSortResponse(APDUEvent e)
  {
    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingSegmentRequest(APDUEvent e)
  {
    System.err.println("Incoming SegmentResponse");

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
    System.err.println("Incoming ExtendedServicesResponse");

    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void incomingClose(APDUEvent e)
  {
    System.err.println("Incoming close event: ");
    Close_type close_apdu = (Close_type) e.getPDU().o;
    System.err.println("closeReason:"+close_apdu.closeReason);
    System.err.println("diagnosticInformation:"+close_apdu.diagnosticInformation);

    // Preparation for synchronous Retrieval API
    synchronized(this)
    {
      notifyAll();
    } 
  }

  public void doExplain() throws Z3950Exception, InvalidQueryException
  {
    clearAllDatabases();
    addDatatabse("IR-Explain-1");
    setRecordSyntax("explain");

    SearchResponse_type r = sendSearch(
           new org.jzkit.search.util.QueryModel.PrefixString.PrefixString("@attrset exp-1 @attr 1=1 categoryList"),"exp","expset");

    if ( r.searchStatus.booleanValue() )
    {
      // OK, Our search for a category list in the explain database seems to have worked...
      // Figure out what useful info we can from the available categories

      if ( r.resultCount.intValue() == 1 )
      {
	// That's good, only one category list
	CategoryList_type cl = null;

	if ( r.numberOfRecordsReturned.intValue() == 1 )
	{
	  // Even better, it looks like the category list has been piggybacked
	  if ( r.records.which == Records_type.responserecords_CID )
	  {
	    List response_records = (List)r.records.o;
	    NamePlusRecord_type npr = (NamePlusRecord_type) response_records.get(0);

	    System.err.println("Got record from "+npr.name);

	    switch ( npr.record.which )
	    {
	      case record_inline13_type.retrievalrecord_CID :
	      {
		EXTERNAL_type et = (EXTERNAL_type)npr.record.o;
		Object o = et.encoding.o;
  
		System.err.println("Got retrieval record");
  
		if ( o instanceof Explain_Record_type )
		{
		  cl = (CategoryList_type)(((Explain_Record_type)o).o);

		  for ( int i=0; i<cl.categories.size(); i++ )
		  {
		    CategoryInfo_type ci = (CategoryInfo_type)cl.categories.get(i);
		    System.err.println("Aviailable category : "+ci.category);
  
		    if ( null != ci.category )
		    {
		      if ( ci.category.equals("DatabaseInfo") )
			explainDatabaseInfo();
		    }
		  }
		}
		break;
	      }

	      default:
		System.err.println("Unhandled name plus record option : "+npr.record.which);
	    }
	  }
	  else
	  {
	    System.err.println("Might have a diagnostic here...");
	  }

	  // If not, it's probably a diagnostic....
	}

	// Let's hope we have managed to set cl in some of the above
      } 
    }
  }

  // Function called to query the explain database for databaseInfo records
  private void explainDatabaseInfo() throws Z3950Exception, InvalidQueryException
  {
    clearAllDatabases();
    addDatatabse("IR-Explain-1");
    dbinfo.clear();
    setRecordSyntax("explain");
 
    SearchResponse_type r = sendSearch(
        new org.jzkit.search.util.QueryModel.PrefixString.PrefixString("@attrset exp-1 @attr 1=1 databaseInfo"),"expdbinfo","dbinfoset");

    if ( r.searchStatus.booleanValue() )
    {
      // OK, Our search for database info in the explain database seems to have worked...
      // Figure out what useful info we can from the available categories

      if ( r.resultCount.intValue() > 0 )
      {
	if ( r.numberOfRecordsReturned.intValue() > 0 )
	{
	  // Even better, it looks like we have some piggyback records
	  if ( r.records.which == Records_type.responserecords_CID )
	  {
	    processDatabaseInfoRecords((List)(r.records.o));
	  }

	  // If not, it's probably a diagnostic....
	}

	// Use present to get the records (For now we will rely on number of databases
	// being less that ssub ).
      } 
    }
  }

  private void processDatabaseInfoRecords(List records)
  {
    for (int i=0; i<records.size(); i++)
    {
      NamePlusRecord_type npr = (NamePlusRecord_type) records.get(i);

      if ( npr.record.which == record_inline13_type.retrievalrecord_CID )
      {
	EXTERNAL_type et = (EXTERNAL_type)npr.record.o;
	Object o = et.encoding.o;

	if ( o instanceof Explain_Record_type )
	{
	  DatabaseInfo_type di = (DatabaseInfo_type)(((Explain_Record_type)o).o);
	  dbinfo.put(di.name, di);
	  System.err.println("Aviailable database: "+di.name);
	}
      }
    }
  }

  public Enumeration getDatabases()
  {
    return dbinfo.elements();
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

}
