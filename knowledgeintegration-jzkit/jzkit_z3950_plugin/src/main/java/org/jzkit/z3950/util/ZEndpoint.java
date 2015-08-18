// Title:       ZEndpoint
// @version:    $Id: ZEndpoint.java,v 1.11 2005/10/27 16:51:52 ibbo Exp $
// Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
// @author:     Ian Ibbotson ( ibbo@k-int.com )
// Company:     Knowledge Integration Ltd.
// Description: Utility class representing the endpoint for a Z39.50 Association
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

package org.jzkit.z3950.util;


import org.jzkit.a2j.codec.runtime.*;
import org.jzkit.a2j.codec.util.*;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import org.jzkit.z3950.gen.v3.NegotiationRecordDefinition_charSetandLanguageNegotiation_3.*;
import org.jzkit.search.util.RecordModel.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
  ZEndpoint : Manages the socket endpoint for a z39.50 Origin

  * @author Ian Ibbotson
  * @version $Id: ZEndpoint.java,v 1.11 2005/10/27 16:51:52 ibbo Exp $
  *
  * This class manages the comminication with a Z origin. It deals with accepting
  * incoming PDU's and announcing them to any observers.
  *
  * The class also has helpers to construct outgoing PDU's. It *shoud not*
  * be used to store information about the Z-Association, instead that should be
  * dealt with by users of this class.
  * 
  * Normally, we will configure an instance of this class using Beanutils.copyProperties to 
  * set all the bean style properties on this object from. IE Beanutils.copyProperties(new ZEndpoint(), config);
  *
  */
 

// Basic imports
import java.io.*;
import java.net.*;
import java.util.*;
import java.math.BigInteger;

import org.jzkit.a2j.gen.AsnUseful.*;
import org.jzkit.a2j.codec.util.*;

import org.jzkit.util.*;

public class ZEndpoint extends Thread
{
  private Socket z_assoc = null;
  private InputStream incoming_data = null;
  private OutputStream outgoing_data = null;
  private PDU_codec codec = PDU_codec.getCodec();
  private OIDRegister reg = null;
  private boolean running = true;
  private boolean close_notified = false;

  // Properties
  private String target_hostname;
  private int target_port;
  private String charset_encoding = US_ASCII_ENCODING;
  private int auth_type;
  private int pref_message_size = 1048576; 
  private int exceptional_record_size = 5242880;
  private String service_user_principal;
  private String service_user_group;
  private String service_user_credentials;

  private boolean use_refid = true;
  private int refid_counter = 0;
  private int assoc_status = ASSOC_STATUS_IDLE;

  public APDUObservable pdu_announcer = new APDUObservable();

  private boolean supports_concurrent_operations = true;

  private Object op_counter_lock = new Object();
  private int op_counter;
  private List outbound_apdu_queue = new ArrayList();

  private static final int ASSOC_STATUS_IDLE=0;
  private static final int ASSOC_STATUS_CONNECTING=1;
  private static final int ASSOC_STATUS_CONNECTED=2;
  private static final int ASSOC_STATUS_PERM_FAILURE=3;
  private static final String EMPTY_STRING="";

  public static final String US_ASCII_ENCODING = "US-ASCII";
  public static final String UTF_8_ENCODING = "UTF-8";
  public static final String UTF_16_ENCODING = "UTF-16";

  private static Log log = LogFactory.getLog(ZEndpoint.class);

  private static int dbg_counter = 0;
  private static int active_thread_counter = 0;

  // private static Properties default_props = new Properties();

  /** The default buffer size for BER send and recieve streams */
  private static final int DEFAULT_BUFF_SIZE = 32768;

  private boolean do_charset_neg = true;

  /**
   * Z3950 Endpoint.
   */
  public ZEndpoint(OIDRegister reg) {                                                                   
    super("Z3950 Search Thread");
    this.reg = reg;
    dbg_counter++;
    // this.setIsDaemon(false);
  }

  protected void finalize() {
    dbg_counter--;
    log.debug("ZEndpoint::finalize() ("+dbg_counter+" active)");
  }

  // Slightly modify the pattern... notify looks into the PDU and sends an appropriate 
  // notification rather than just an "IncomingAPDU". Seems more efficient than writing
  // 25 notify<<XXXPDU>>Event functions...
  //
  protected void notifyAPDUEvent(PDU_type pdu) {

    log.debug("notifyAPDUEvent : "+pdu.which);

    byte[] refid = null;
    // Extract the refid from the PDU
    switch ( pdu.which )
    {
      case PDU_type.initresponse_CID:
        refid = ((InitializeResponse_type)pdu.o).referenceId;
        break;
      case PDU_type.searchresponse_CID:
        refid = ((SearchResponse_type)pdu.o).referenceId;
        break;
      case PDU_type.presentresponse_CID:
        refid = ((PresentResponse_type)pdu.o).referenceId;
        break;
      case PDU_type.deleteresultsetresponse_CID:
        refid = ((DeleteResultSetResponse_type)pdu.o).referenceId;
        break;
      case PDU_type.accesscontrolrequest_CID:
        refid = ((AccessControlRequest_type)pdu.o).referenceId;
        break;
      case PDU_type.resourcecontrolresponse_CID:
        refid = ((ResourceControlResponse_type)pdu.o).referenceId;
        break;
      case PDU_type.resourcereportresponse_CID:
        refid = ((ResourceReportResponse_type)pdu.o).referenceId;
        break;
      case PDU_type.scanresponse_CID:
        refid = ((ScanResponse_type)pdu.o).referenceId;
        break;
      case PDU_type.sortresponse_CID:
        refid = ((SortResponse_type)pdu.o).referenceId;
        break;
      case PDU_type.extendedservicesresponse_CID:
        refid = ((ExtendedServicesResponse_type)pdu.o).referenceId;
        break;
      case PDU_type.close_CID:
        refid = ((Close_type)pdu.o).referenceId;
        break;
    }

    log.debug("Incoming PDU refid: "+( refid != null ? new String(refid) : null));
    // Create the new event and add the PDU that was sent
    APDUEvent e = new APDUEvent(this,pdu, ( refid != null ? new String(refid) : null));

    pdu_announcer.setChanged();
    pdu_announcer.notifyObservers(e);
  }

  /**
   *  Send an init request to the target. This is the original function that
   *  accepts no authentication information. Now just calls the new sendInitRequest
   *  with default parameters asking for no authentication 
   */
  private void sendInitRequest(AsnBitString versionInfo,
                               AsnBitString ops,
                               String refId,
                               int auth_type,
                               String principal,
                               String group,
                               String credentials) throws java.io.IOException
  {
    log.debug("sendInitRequest, pref="+pref_message_size+" excep="+exceptional_record_size);

    PDU_type pdu = new PDU_type();
    InitializeRequest_type initialize_request = new InitializeRequest_type();
    pdu.o = initialize_request;
    pdu.which = PDU_type.initrequest_CID;

    if ( refId != null )
      initialize_request.referenceId=refId.getBytes();

    initialize_request.protocolVersion = versionInfo;
    initialize_request.options = ops;
    initialize_request.preferredMessageSize = BigInteger.valueOf(pref_message_size);
    initialize_request.exceptionalRecordSize = BigInteger.valueOf(exceptional_record_size);

    switch ( auth_type ) {
      case 1:
        log.debug("Using anonymous authentication");
        initialize_request.idAuthentication = new IdAuthentication_type();
        initialize_request.idAuthentication.which = IdAuthentication_type.anonymous_CID;
        break;
      case 2:
        log.debug("Using idopen authentication : \""+principal+"\"");
        initialize_request.idAuthentication = new IdAuthentication_type();
        initialize_request.idAuthentication.which = IdAuthentication_type.open_CID;
        initialize_request.idAuthentication.o = principal;
        break;
      case 3:
        log.debug("Using idpass authentication : user:\""+principal+"\""+", group:\""+group+"\""+", pass:\""+credentials+"\"");
        initialize_request.idAuthentication = new IdAuthentication_type();
        initialize_request.idAuthentication.which = IdAuthentication_type.idpass_CID;
        idPass_inline0_type idpass = new idPass_inline0_type();
        idpass.groupId=group;
        idpass.userId=principal;
        idpass.password=credentials;
        initialize_request.idAuthentication.o = idpass;
        break;
      case 0:
      default:
        log.debug("Not using z-authentication");
        initialize_request.idAuthentication = null;
        break;
    }
    initialize_request.implementationId = "174";
    initialize_request.implementationName = "JZKit2 Generic ZEndpoint";
    initialize_request.implementationVersion = "2.0";
    initialize_request.userInformationField = null;

    // Ask for utf-8 encoding of data.
    if ( do_charset_neg ) {
      log.debug("Sending character set negotiation in init request");
      if ( initialize_request.otherInfo == null )
        initialize_request.otherInfo = new ArrayList();

      initialize_request.otherInfo.add(createCharsetNeg());
    }

    encodeAndSend(pdu);
  }

  /** createCharsetNeg : Create and populate the type asking for character set
   * negotiation.
   */
  private OtherInformationItem43_type createCharsetNeg() {
    OtherInformationItem43_type oit = new OtherInformationItem43_type();

    oit.information = new information_inline44_type();
    oit.information.which = information_inline44_type.externallydefinedinfo_CID;
    EXTERNAL_type et = new EXTERNAL_type();
    oit.information.o = et;

    et.direct_reference = reg.oidByName("z_charset_neg_3");

    if ( et.direct_reference == null )
      log.warn("Unable to locate direct reference for oid");

    et.encoding = new encoding_inline0_type();
    et.encoding.which = encoding_inline0_type.single_asn1_type_CID;

    CharSetandLanguageNegotiation_type t = new CharSetandLanguageNegotiation_type();
    et.encoding.o = t;

    t.which = CharSetandLanguageNegotiation_type.proposal_CID;
    OriginProposal_type proposal = new OriginProposal_type();
    t.o = proposal;

    proposal.proposedCharSets = new ArrayList();

    // For now, we just ask for UTF-8 as a charcter set (No language neg.)
    proposedCharSets_inline0_choice1_type c1 = new proposedCharSets_inline0_choice1_type();
    c1.which = proposedCharSets_inline0_choice1_type.iso10646_CID;
    Iso10646_type utf_8_request = new Iso10646_type();
    c1.o = utf_8_request;
    utf_8_request.encodingLevel = reg.oidByName("charset_utf8");
    proposal.proposedCharSets.add(c1);

    return oit;
  }

  // Used to take a QueryModel, now accepts a Z39.50 Query_type.. Need to remove all
  // dependencies on JIRI.
  public void sendSearchRequest(ArrayList database_names,
                                Query_type z3950_query_model,
                                String reference_id,
                                int ssub,
                                int lslb,
                                int mspn,
                                boolean replace,
                                String setname,
                                String ssen,
                                String msen,
                                int[] prefRecSynOID) throws java.io.IOException
  {
    // In debug mode, dump the query we have been asked to send.
    log.debug("Sending search request with refid: "+reference_id);

    // Create a search request
    PDU_type pdu = new PDU_type();
    pdu.which = PDU_type.searchrequest_CID;
    SearchRequest_type srt = new SearchRequest_type();
    pdu.o = srt;

    if ( reference_id != null )
      srt.referenceId = reference_id.getBytes();
    srt.smallSetUpperBound = BigInteger.valueOf(ssub);
    srt.largeSetLowerBound = BigInteger.valueOf(lslb);
    srt.mediumSetPresentNumber = BigInteger.valueOf(mspn);
    srt.replaceIndicator = Boolean.TRUE;

    srt.resultSetName = setname;

    srt.databaseNames = database_names;

    if ( ssen != null )
    {
      srt.smallSetElementSetNames = new ElementSetNames_type();
      srt.smallSetElementSetNames.which = ElementSetNames_type.genericelementsetname_CID;
      srt.smallSetElementSetNames.o = ssen;
    }

    if ( msen != null )
    {
      srt.mediumSetElementSetNames =  new ElementSetNames_type();
      srt.mediumSetElementSetNames.which = ElementSetNames_type.genericelementsetname_CID;
      srt.mediumSetElementSetNames.o = msen;
    }

    srt.preferredRecordSyntax = prefRecSynOID;

    srt.query = z3950_query_model;
    // srt.query = z3950_query_model.toASNType();
    // Query_type query;
    // srt.query = new Query_type();
    // srt.query.which=Query_type.type_1_CID;
    // srt.query.o =  RPNHelper.RootNodeToZRPNStructure(query_tree,charset_encoding);
 
    encodeAndSend(pdu);
  }

  public void sendPresentRequest(String refid,
                                 String rsname,
                                 long first,
                                 long count,
                                 ExplicitRecordFormatSpecification spec) throws java.io.IOException
  {
     sendPresentRequest(refid,
                        rsname,
                        first,
                        count,
                        spec.getSetname().getRef(),
                        FormatSpecOIDHelper.getOID(reg, spec));
  }

  public void sendPresentRequest(String refid,
                                 String rsname,
                                 long first,
                                 long count,
                                 String element_set_name,
                                 int[] preferred_record_syntax_oid) throws java.io.IOException
  {
    //if ( log.isDebugEnabled() )
    //{
      log.debug("sendPresentRequest, refid="+refid);
      log.debug("setname: "+rsname);
      log.debug("first="+first);
      log.debug("count="+count);
    //}
    PDU_type pdu = new PDU_type();
    pdu.which = PDU_type.presentrequest_CID;
    PresentRequest_type pr = new PresentRequest_type();
    pdu.o = pr;

    // System.err.println("Send present request, element set name is "+element_set_name);

    if ( refid != null )
      pr.referenceId = refid.getBytes();
      
    pr.resultSetId = rsname;
    pr.resultSetStartPoint = BigInteger.valueOf((long)first);
    pr.numberOfRecordsRequested = BigInteger.valueOf((long)count);

    // If element_set_name is non-null, set the recordComposition
    if ( element_set_name != null )
    {
      pr.recordComposition = new recordComposition_inline9_type();
      pr.recordComposition.which =  recordComposition_inline9_type.simple_CID;
      ElementSetNames_type esn = new ElementSetNames_type();
      pr.recordComposition.o = esn;
      esn.which = ElementSetNames_type.genericelementsetname_CID;
      esn.o = element_set_name;
    }

    // preferred_record_syntax
    // pr.preferredRecordSyntax = spec.getFormat().getValue();
    pr.preferredRecordSyntax = preferred_record_syntax_oid;

    encodeAndSend(pdu);
  }

  /**
   * sort_sequence is a list of type x
   */
  public void sendSortRequest(String refid,
                              ArrayList input_result_set_names,
                              String output_result_set_name,
                              ArrayList sort_sequence) throws java.io.IOException
  {
    PDU_type pdu = new PDU_type();
    pdu.which = PDU_type.sortrequest_CID;
    SortRequest_type sr = new SortRequest_type();
    pdu.o = sr;

    if ( refid != null )
      sr.referenceId = refid.getBytes();
    sr.inputResultSetNames = input_result_set_names;
    sr.sortedResultSetName = output_result_set_name;
    sr.sortSequence = sort_sequence;
    encodeAndSend(pdu);
  }

  /** sendScanRequest: Send a scan request to the remote server.
   *
   */
  public void sendScanRequest(String refid,
                              ArrayList database_names,
			      Query_type z3950_query_model, // QueryModel qm,
                              long step_size,
			      long number_of_terms_requested,
			      long preferred_position_in_response) throws java.io.IOException
  {
    log.debug("Sending scan request....");

    // PDU_type pdu = new PDU_type();
    // pdu.which = PDU_type.scanrequest_CID;
    // ScanRequest_type sr = new ScanRequest_type();
    // pdu.o = sr;

    // if ( refid != null )
    //   sr.referenceId = refid.getBytes();

    // sr.databaseNames = database_names;

    // RootNode rn = qm.toRPN();

    // String attrset_name = rn.getAttrset();
    // sr.attributeSet = reg.oidByName(attrset_name);

    // if ( rn.getChild() instanceof AttrPlusTermNode)
    //   sr.termListAndStartPoint = RPNHelper.AttrPlusTermNode2apt_type((AttrPlusTermNode)(rn.getChild()),charset_encoding);
    // else
    //   log.warn("Unable to parse scan expression");

    // sr.stepSize = BigInteger.valueOf(step_size);
    // sr.numberOfTermsRequested = BigInteger.valueOf(number_of_terms_requested);
    // sr.preferredPositionInResponse = BigInteger.valueOf(preferred_position_in_response);
    // sr.otherInfo = null;
    
    // encodeAndSend(pdu);
  }

  public void sendCloseRequest(String refid,
                               long reason,
                               String diagnosticInfo) throws java.io.IOException {
    PDU_type pdu = new PDU_type();
    pdu.which = PDU_type.close_CID;
    Close_type cr = new Close_type();
    pdu.o = cr;

    if ( refid != null )
      cr.referenceId = refid.getBytes();
    cr.closeReason = BigInteger.valueOf((long)reason);
    cr.diagnosticInformation = diagnosticInfo;
    
    encodeAndSend(pdu);
  }

  /** setSerialOps: Called to indicate that the assoc cannot handle concurrent operations.
   * All requests to send APDU's <i>With the exception of close</i> 
   * will block until the previous operation has completed.
   */
  public void setSerialOps() {
    this.supports_concurrent_operations = false;
  }

  public void encodeAndSend(PDU_type the_pdu) throws java.io.IOException
  {
    log.debug("encodeAndSend...");

    if ( ( !supports_concurrent_operations ) && ( the_pdu.which != PDU_type.close_CID ) )
    {
      enqueueOutboundAPDU(the_pdu);
      sendPending();
    }
    else
    {
      internalEncodeAndSend(the_pdu);
    }
  }

  private void enqueueOutboundAPDU(PDU_type the_pdu)
  {
    log.debug("enqueue outbound apdu");

    synchronized ( outbound_apdu_queue )
    {
      outbound_apdu_queue.add(the_pdu);
    }
  }

  private void sendPending()
  {
    log.debug("sendPending()");

    // Here we check that there is currently no outstanding operation and then
    // send the next apdu on the queue.
    PDU_type pdu = null;

    synchronized(op_counter_lock) {
      // If there are no outstanding operations
      if ( op_counter == 0 ) {
        synchronized(outbound_apdu_queue) {
          if ( outbound_apdu_queue.size() > 0 ) {
            pdu = (PDU_type) outbound_apdu_queue.remove(0);
          }
        }
      }

      if ( pdu != null ) {
        try {
          log.debug("Sending queued apdu");
          internalEncodeAndSend(pdu);
        }
        catch ( java.io.IOException ioe ) {
          log.warn("Problem sending enqueued apdu",ioe);
        }
      }
    }
  }

  private synchronized void internalEncodeAndSend(PDU_type the_pdu) throws java.io.IOException
  {
    incOpCount();
    BEROutputStream encoder = new BEROutputStream(DEFAULT_BUFF_SIZE,charset_encoding,reg);
    codec.serialize(encoder, the_pdu, false, "PDU");
    encoder.flush();
    encoder.writeTo(outgoing_data);
    outgoing_data.flush();
    yield();
  }

  private void incOpCount() {
    synchronized( op_counter_lock )
    {
      op_counter++;
      op_counter_lock.notifyAll();
    }
  }

  private void decOpCount() {
    synchronized( op_counter_lock )
    {
      op_counter--;
      op_counter_lock.notifyAll();
    }
  }

  public void notifyClose() {
    PDU_type pdu = new PDU_type();
    pdu.which = PDU_type.close_CID;
    Close_type close = new Close_type();
    pdu.o = close;
    close.closeReason = BigInteger.valueOf(100);
    close.diagnosticInformation = "Internal close notification";
 
    // This is a bit of a hack.. Notify ourselves that the assoc has closed.
    notifyAPDUEvent(pdu);
  }

  public void shutdown() throws java.io.IOException {
    log.debug("ZEndpoint::shutdown() - host="+target_hostname+" status="+assoc_status+" running="+running);

    if ( z_assoc != null ) {
      if ( assoc_status == ASSOC_STATUS_CONNECTED )
        sendCloseRequest((String)null,0,"User requested shutdown");

      this.interrupt();

      running = false;
      z_assoc.close();
      z_assoc = null;
      assoc_status = ASSOC_STATUS_IDLE;
    }
  }

  public void run() {
    log.debug("Bringing assoc up........Active Z Thread counter = "+(++active_thread_counter));
    log.debug("My thread priority : "+this.getPriority());
    log.debug("My isDaemon: "+this.isDaemon());

    try {
      assoc_status = ASSOC_STATUS_CONNECTING;
      connect();
      assoc_status = ASSOC_STATUS_CONNECTED;
      log.debug("Connect completed OK, Listening for incoming PDUs");
    }
    catch ( java.net.ConnectException ce ) {
      log.info(ce.toString());
      assoc_status = ASSOC_STATUS_PERM_FAILURE;
      sendDummyFailInitResponse(ce.toString());
      running = false;
    }
    catch ( java.io.IOException ioe ) {
      log.warn("ZEndpoint thread encountered an exception",ioe);
      assoc_status = ASSOC_STATUS_PERM_FAILURE;
      sendDummyFailInitResponse(ioe.toString());
      running = false;
    }

    while(running) {
      try {
        log.debug("Waiting for data on input stream.....");
        BERInputStream bds = new BERInputStream(incoming_data, charset_encoding,DEFAULT_BUFF_SIZE, reg);
        PDU_type pdu = null;
        pdu = (PDU_type)codec.serialize(bds, pdu, false, "PDU");
        log.debug("Notifiy observers");

        if ( pdu.which == PDU_type.close_CID )
        {
          log.debug("Just got a close APDU");
          close_notified=true;
        }

        decOpCount();

        notifyAPDUEvent(pdu);

        // If the target does not support concurrent operations then it's possible that
        // outbound APDU's have stacked up whilst we wait for the response handled here.
        // Therefore, here we  check the outgoing apdu queue and send any messages that
        // have been queued
        if ( !close_notified )
          sendPending();

        log.debug("Yield to other threads....");
        yield();
      }
      catch ( java.io.InterruptedIOException iioe ) {
        log.debug("Processing java.io.InterruptedIOException, shut down association"+" - hostname="+target_hostname);
        log.info(iioe.toString());
        running=false;
      }
      catch ( java.net.SocketException se ) {
        // Most likely socket closed.
        log.info("SocketException");
        log.info(se.toString()+" - hostname="+target_hostname);
        running=false;
      }
      catch ( Exception e ) {
        log.warn("ZEndpoint Unknown error",e);
        log.info(e.toString()+" - hostname="+target_hostname);
        running=false;
      }
      finally {
      }
    }

    synchronized( op_counter_lock ) {
      op_counter_lock.notifyAll();
    }

    // We might need to notify any listening objects that the assoc has been
    // shut down if the target did not send a close before snapping the assoc
    // (Or in case there was a network problem etc)
    if ( !close_notified ) {
      notifyClose();
    }

    // End of association, clear out all listeners
    log.debug("End of ZEndpoint listening thread for host "+target_hostname+" active z thread counter="+(--active_thread_counter));
    pdu_announcer.deleteObservers();
    pdu_announcer=null;

    try {
      incoming_data=null;
      outgoing_data=null;
      if ( z_assoc != null )
        z_assoc.close();
    }
    catch( Exception e ) {
      // catches the socket close execption...
    }

    assoc_status = ASSOC_STATUS_IDLE;
    z_assoc=null;
  }

  protected void connect() throws java.net.ConnectException, java.io.IOException {
    // connect("INIT");  // Backward compat. but useless for Isite targets :-(
    connect(null);
  }

  protected void connect(String refid) throws java.net.ConnectException, java.io.IOException {
    if ( ( null != target_hostname ) && ( target_port > 0 ) ) {
      log.debug("Attempting to connect to "+target_hostname+":"+target_port);
      int timeout = 20000;
      z_assoc = new Socket(target_hostname, target_port);
      outgoing_data = z_assoc.getOutputStream();
      incoming_data = z_assoc.getInputStream();
    }
    else {
      throw new java.net.ConnectException("Properties do not contain ServiceHost and/or ServicePort");
    }

    log.debug("Connect completed OK, send init request (nodelay="+z_assoc.getTcpNoDelay()+", timeout="+z_assoc.getSoTimeout()+", linger="+z_assoc.getSoLinger()+")");

    // Will allow user access to init parameters via props soon...
    // We might need to sleep here for a little while to give the remote target
    // a chance to warm up (testing against moray.stir.ac.uk indicates this to be
    // the case).

    AsnBitString version_info = new AsnBitString();
    version_info.setBit(0);
    version_info.setBit(1);
    version_info.setBit(2);
 
    AsnBitString options = new AsnBitString();
    options.setBit(0);     // search
    options.setBit(1);     // present
    options.setBit(2);     // del Set
    // options.setBit(3);     // resourceReport
    // options.setBit(4);     // trigger Resource Control
    // options.setBit(5);     // ResourceControl
    // options.setBit(6);     // Access control
    options.setBit(7);     // scan
    options.setBit(8);     // Sort
                             // 9 is reserved
    options.setBit(10);    // Extended Services
    // options.setBit(11);    // Level 1 Segmentations
    // options.setBit(12);    // Level 2 Segmentations
    options.setBit(13);    // Concurent ops
    options.setBit(14);    // Named result sets

    String s = null;

    // Init request, 8k pref. message size, 64k exceptional
    sendInitRequest(version_info,
                    options,
                    refid,
                    auth_type,
                    service_user_principal,
                    service_user_group,
                    service_user_credentials);

    log.debug("Sent init request");
  }

  private void sendDummyFailInitResponse(String reason) {
    PDU_type pdu = new PDU_type();
    InitializeResponse_type initialize_response = new InitializeResponse_type();
    pdu.o = initialize_response;
    pdu.which = PDU_type.initresponse_CID;

    initialize_response.referenceId = new byte[0];
    initialize_response.protocolVersion = new AsnBitString();
    initialize_response.options = new AsnBitString();
    initialize_response.result = Boolean.FALSE;

    notifyAPDUEvent(pdu);
  }

  public int getAssocStatus() {
    return assoc_status;
  }

  public Observable getPDUAnnouncer() {
    return pdu_announcer;
  }

  public String genRefid(String base) {
    if ( use_refid )
      return base+":"+(refid_counter++);
   
    return null;
  }

  public String getHost() {
    return target_hostname;
  }

  public void setHost(String host) {
    this.target_hostname = host;
  }

  public int getPort() {
    return target_port;
  }

  public void setPort(int port) {
    this.target_port = port;
  }

  public String getCharsetEncoding() {
    return charset_encoding;
  }

  public void setCharsetEncoding(String enc) { 
    if ( enc != null )
      charset_encoding=enc;
    else
      throw new RuntimeException("Null charset encoding is not allowed");
  }

  public int getAuthType() {
    return auth_type;
  }

  public void setAuthType(int auth_type) {
    this.auth_type = auth_type;
  }

  public int getPrefMessageSize() {
    return pref_message_size;
  }

  public void setPrefMessageSize(int pref_message_size) {
    this.pref_message_size = pref_message_size;
  }

  public int getExceptionalMessageSize() {
    return exceptional_record_size;
  }

  public void setExceptionalMessageSize(int exceptional_record_size) {
    this.exceptional_record_size = exceptional_record_size;
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
}
