/**
 *
 *     return null;
 * Title:       Generic backend Z3950 association
 * @version:    $Id: ZServerAssociation.java,v 1.8 2004/11/28 16:11:45 ibbo Exp $
 * Copyright:   Copyright (C) 2001 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     KI
 * Description: 
 */

/*
 * $log$
 */

package org.jzkit.z3950.server;
                                                                                                                                          
import java.math.BigInteger;
import java.net.Socket;
import java.util.*;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

// Hoping to just use Session
// import org.jzkit.SearchProvider.iface.*;
import org.jzkit.z3950.gen.v3.NegotiationRecordDefinition_charSetandLanguageNegotiation_3.*;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import org.jzkit.z3950.util.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.a2j.codec.util.OIDRegister;
import org.jzkit.a2j.codec.util.OIDRegisterEntry;
import org.jzkit.a2j.gen.AsnUseful.EXTERNAL_type;
import org.jzkit.a2j.gen.AsnUseful.encoding_inline0_type;
import org.jzkit.z3950.util.TimeoutExceededException;
import org.jzkit.z3950.RecordModel.*;
import org.springframework.context.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.RecordBuilder.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ZServerAssociation implements TargetAPDUListener {

  public static Log log = LogFactory.getLog(ZServerAssociation.class);
  public static Log event_log = LogFactory.getLog(Z3950Main.class);
  private ZTargetEndpoint assoc = null;   
  private OIDRegister reg = null;
  private int dbg_counter=0;
  private GenericEventToTargetListenerAdapter event_adapter = null;
  private Z3950NonBlockingBackend backend;
  private ApplicationContext ctx = null;
  private boolean add_extra_status_info = true;
  private java.net.SocketAddress client_address = null;
 
  public ZServerAssociation(Socket s, 
                            Z3950NonBlockingBackend backend,
                            ApplicationContext ctx) {
    event_log.info("New ZServerAssociation "+(dbg_counter++));
    this.backend = backend;
    reg = (OIDRegister) ctx.getBean("OIDRegister");
    assoc = new ZTargetEndpoint(s, reg);
    event_adapter = new GenericEventToTargetListenerAdapter(this);
    assoc.getPDUAnnouncer().addObserver( event_adapter );

    client_address = s.getRemoteSocketAddress();

    if ( reg == null ) 
      throw new RuntimeException("Unable to locate OID Register in Application Context");

    assoc.start();
    log.debug("New thread should have started");
  }

  protected void finalize() throws Throwable {
    dbg_counter--;
    event_log.info("ZServerAssociation::finalize() - "+dbg_counter+" remaining");
  }

  public void incomingInitRequest(APDUEvent e) {

    log.debug("Incoming initRequest....");
    InitializeRequest_type init_request = (InitializeRequest_type) (e.getPDU().o); 

    for ( int i=0; i<Z3950Constants.z3950_option_names.length; i++ ) {
      if ( init_request.options.isSet(i) )
        log.info("Origin requested service: "+Z3950Constants.z3950_option_names[i]);
    }

    // Did the origin request scan?
    if ( init_request.options.isSet(7) )
    {
      // For now, just say we support scan... which we do. but it depends on the backend searchable
      // Does our backend support scan?
      // if ( ! ( this.search_service instanceof Scanable ) )
      // {
      //   init_request.options.clearBit(7);
      //   log.info("Origin requested scan, not supported by this backend realisation.");
      // }
      // else
      // {
      //   Scanable s = (Scanable)this.search_service;
      //   if ( ! s.isScanSupported() )
      //   {
      //     init_request.options.clearBit(7);
      //     log.info("Origin requested scan, not supported by this instance of the search backend");
      //   }
      // }
    }

    // If we are talking v2, userInformationField may contain a character Set Negotiation
    // field, if v3, otherInfo may contain a charset/language negotiation feature.
    if ( init_request.userInformationField != null ) {
      OIDRegisterEntry ent = reg.lookupByOID(init_request.userInformationField.direct_reference);
      if ( ent != null )
        log.debug("Init Request contains userInformationField oid="+ent.getName());
      else
        log.debug("Unkown external in userInformationField");
        // The OID for the external should be found in userInformationField.direct_reference
    }

    if ( init_request.otherInfo != null ) {
      log.debug("Init Request contains otherInfo entries");
      for ( Iterator other_info_enum = init_request.otherInfo.iterator(); other_info_enum.hasNext(); )
      {
        log.debug("Processing otherInfo entry...");
        // Process the external at other_info_enum.nextElement();
        OtherInformationItem43_type oit = (OtherInformationItem43_type)(other_info_enum.next());

        log.debug("Processing OtherInformationItem43_type");

        switch ( oit.information.which )
        {
          case information_inline44_type.externallydefinedinfo_CID:
            EXTERNAL_type et = (EXTERNAL_type)(oit.information.o);
            if ( et.direct_reference != null )
            {
              OIDRegisterEntry ent = reg.lookupByOID(et.direct_reference);
              log.debug("External with direct reference, oid="+ent.getName());

              // Are we dealing with character set negotiation.
              if ( ent.getName().equals("z_charset_neg_3") )
                handleNLSNegotiation((CharSetandLanguageNegotiation_type)(et.encoding.o));
            }
            break;
          default:
            log.debug("Currently unhandled OtherInformationType");
            break;
        }
      }
    }

    try
    {
      log.debug("sendInitResponse");
      event_log.info("New_Z3950_Session:"+client_address);
      assoc.sendInitResponse(init_request.referenceId,
                             init_request.protocolVersion,
                             init_request.options,
                             init_request.preferredMessageSize.longValue(),
                             init_request.exceptionalRecordSize.longValue(),
                             true,
                             "174",
                             "JZkit generic server / "+backend.getImplName(),
                             backend.getVersion(),
                             null,
                             null);
    }
    catch ( java.io.IOException ioe )
    {
      ioe.printStackTrace();
    }
  }

  private void handleNLSNegotiation(CharSetandLanguageNegotiation_type neg) {
    log.debug("Handle Character Set and Language Negotiation");

    if ( neg.which == CharSetandLanguageNegotiation_type.proposal_CID )
    {
      OriginProposal_type op = (OriginProposal_type)(neg.o);

      // Deal with any proposed character sets.
      if ( op.proposedCharSets != null )
      {
        for ( Iterator prop_charsets = op.proposedCharSets.iterator(); prop_charsets.hasNext();)
        {
          proposedCharSets_inline0_choice1_type c = (proposedCharSets_inline0_choice1_type)(prop_charsets.next());
          switch ( c.which )
          {
            case proposedCharSets_inline0_choice1_type.iso10646_CID:
              // The client proposes an iso 10646 id for a character set
              Iso10646_type iso_type = (Iso10646_type)(c.o);
              OIDRegisterEntry ent = reg.lookupByOID(iso_type.encodingLevel);
              log.debug("Client proposes iso10646 charset: "+ent.getName());
              break;
            default:
              log.error("Unhandled character set encoding");
              break;
          }
        }
      }
    }
  }

  public void incomingSearchRequest(APDUEvent e)
  {
    log.debug("Processing incomingSearchRequest");

    SearchRequest_type search_request = (SearchRequest_type) (e.getPDU().o);

    int ssub = search_request.smallSetUpperBound.intValue();
    int lslb = search_request.largeSetLowerBound.intValue();
    int mspn = search_request.mediumSetPresentNumber.intValue();

    String pref_recsyn = null;

    if ( search_request.preferredRecordSyntax != null )
    {
      OIDRegisterEntry ent = reg.lookupByOID(search_request.preferredRecordSyntax);
      pref_recsyn = ( ent != null ? ent.getName() : null );
    }

    // switch(search_request.query.which)
    backend.search(new BackendSearchDTO(this,
                                        search_request.query,
                                        search_request.databaseNames,
                                        pref_recsyn,
                                        extractSetname(search_request.smallSetElementSetNames),
                                        extractSetname(search_request.mediumSetElementSetNames),
                                        search_request.resultSetName != null ? search_request.resultSetName : "Default",
                                        search_request.replaceIndicator.booleanValue(),
                                        search_request.referenceId) );
  }

  public void notifySearchResult(BackendSearchDTO bsr) {
    log.debug("notifySearchResult");

    // Create a search response
    PDU_type pdu = new PDU_type();
    pdu.which=PDU_type.searchresponse_CID;
    SearchResponse_type response = new SearchResponse_type();
    pdu.o = response;

    // response.referenceId = search_request.referenceId; 

    // Assume failure unless something below sets to true
    response.searchStatus = Boolean.valueOf(bsr.search_status);
    response.resultCount = BigInteger.valueOf(bsr.result_count);
    if ( bsr.refid != null )
      response.referenceId = bsr.refid;

    if ( bsr.search_status ) {
      if ( bsr.piggyback_records != null ) {
        response.presentStatus = BigInteger.valueOf(0); // OK...
        response.numberOfRecordsReturned = BigInteger.valueOf(bsr.piggyback_records.length);
        response.nextResultSetPosition = BigInteger.valueOf(bsr.piggyback_records.length+1);
      }
      else {
        response.presentStatus = BigInteger.valueOf(0); // OK... (but no records presented)
        response.numberOfRecordsReturned = BigInteger.valueOf(0);
        response.nextResultSetPosition = BigInteger.valueOf(1);
      }
    }
    else {
      response.presentStatus = BigInteger.valueOf(5); // Failure
      response.numberOfRecordsReturned = BigInteger.valueOf(0);
      response.nextResultSetPosition = BigInteger.valueOf(0);
      response.resultSetStatus = BigInteger.valueOf(3); // No result set available
      if ( bsr.diagnostic_code == 0 ) {
        String addinfo = null;
        if ( bsr.status_report != null )
          addinfo = bsr.status_report.toString();

        response.records = createNSD("2", addinfo, bsr.diagnostic_data);
      }
      else 
        response.records = createNSD(""+bsr.diagnostic_code,bsr.diagnostic_addinfo,bsr.diagnostic_data);
    }

    // Extra info for meta-search
    // if ( add_extra_status_info && ( bsr.status_report != null ) ) {
      // Create a list in additionalSearchInfo if one doesn't exist
    //   if ( response.additionalSearchInfo == null ) 
    //     response.additionalSearchInfo = new ArrayList();
      // Create and add a report for our internal structure
    //   createAdditionalSearchInfo(response.additionalSearchInfo, bsr.status_report);
    // }

    log.debug("Send search response : ");

    try {
      event_log.info("SearchComplete");
      assoc.encodeAndSend(pdu);
    }
    catch ( java.io.IOException ioe )
    {
      ioe.printStackTrace();
    }  
  }

  private String extractSetname(ElementSetNames_type esn_type)
  {
    String result;

    if ( ( esn_type != null ) &&
         ( esn_type.which == ElementSetNames_type.genericelementsetname_CID ) )
      result = esn_type.o.toString();
    else
      result = "f";

    return result;
  }

  public void incomingPresentRequest(APDUEvent e) {
    log.debug("Incoming presentRequest");

    PresentRequest_type present_request = (PresentRequest_type) (e.getPDU().o);

    int start = present_request.resultSetStartPoint.intValue();
    int count = present_request.numberOfRecordsRequested.intValue();

    String element_set_name = null;
    String result_set_name = present_request.resultSetId;
    String record_syntax = null;

    OIDRegisterEntry ent = reg.lookupByOID(present_request.preferredRecordSyntax);
    if ( ent != null )
      record_syntax = ent.getName();

    if ( ( present_request.recordComposition != null ) &&
         ( present_request.recordComposition.which == recordComposition_inline9_type.simple_CID ) ) {
      element_set_name = extractSetname((ElementSetNames_type)present_request.recordComposition.o);
    }
    else { // Complex
      // CompSpec! We don't handle this yet. NB that there is nothing to stop the procesing
      // happening here, Just set schema and esn. We just need to write the code to pull data
      // out of the pdu.
      element_set_name = "f";
    }

    ArchetypeRecordFormatSpecification archetype = new ArchetypeRecordFormatSpecification(element_set_name);

    ExplicitRecordFormatSpecification explicit = getExplicitFormat(record_syntax,element_set_name);
    // ExplicitRecordFormatSpecification explicit = new ExplicitRecordFormatSpecification(record_syntax,schema,element_set_name);

    backend.present(new BackendPresentDTO(this,
                                          result_set_name != null ? result_set_name : "Default",
                                          start,
                                          count,
                                          record_syntax,
                                          element_set_name,
                                          present_request.recordComposition,
                                          present_request.referenceId,
                                          archetype,
                                          explicit));
  }

  public void notifyPresentResult(BackendPresentDTO bpr) {
    log.debug("notifyPresentResult start="+bpr.start+", count="+bpr.count+", syntax="+bpr.record_syntax);
    PDU_type pdu = new PDU_type();
    pdu.which=PDU_type.presentresponse_CID;
    PresentResponse_type response = new PresentResponse_type();
    pdu.o = response;
    response.referenceId = bpr.refid;
    response.otherInfo = null;

      // element_set_name = extractSetname(search_request.smallSetElementSetNames);

    if ( bpr.start <= 0 ) {
      log.debug("Present out of range");
      response.presentStatus = BigInteger.valueOf(5);
      response.numberOfRecordsReturned = BigInteger.valueOf(0);
      response.nextResultSetPosition = BigInteger.valueOf(bpr.next_result_set_position);
      response.records = createNonSurrogateDiagnostic(1,"Unknown internal error presenting result records");
    } else if ( bpr.start > bpr.total_hits ) {
      log.debug("Present out of range");
      response.presentStatus = BigInteger.valueOf(5);
      response.numberOfRecordsReturned = BigInteger.valueOf(0);
      response.nextResultSetPosition = BigInteger.valueOf(bpr.next_result_set_position);
      response.records = createNonSurrogateDiagnostic(13,"Requested start record "+bpr.start+", only 1 to "+bpr.total_hits+" available");
    }
    else {
      response.records = createRecordsFor(bpr.result_records, bpr.record_syntax);

      // response.records = createRecordsFor(bpr.result_records, bpr.explicit);
      response.nextResultSetPosition = BigInteger.valueOf(bpr.next_result_set_position);

      if ( ( response.records != null ) && ( response.records.which == Records_type.responserecords_CID ) ) {
        // Looks like we managed to present some records OK..
        log.debug("Got some records to present "+((List)(response.records.o)).size());
        response.numberOfRecordsReturned = BigInteger.valueOf(((List)(response.records.o)).size());
        response.presentStatus = BigInteger.valueOf(0);
      }
      else {  // Non surrogate diagnostics ( Single or Multiple )
        log.debug("response.records.which did not contain response records.. Non surrogate diagnostic returned");
        response.numberOfRecordsReturned = BigInteger.valueOf(0);
        // PresentStatus, 0=0K, 1,2,3,4 = Partial, 5=Failure, None surrogate diag
        response.presentStatus = BigInteger.valueOf(5);
      }
    }

    try {
      log.debug("Sending present response");
      assoc.encodeAndSend(pdu);
    }
    catch ( java.io.IOException ioe ) {
      ioe.printStackTrace();
    } 
  }

  public void incomingDeleteResultSetRequest(APDUEvent e) {
    log.debug("Incoming deleteResultSetRequest");

    DeleteResultSetRequest_type delete_request = (DeleteResultSetRequest_type) (e.getPDU().o);

    // Create a DeleteResultSetResponse
    PDU_type pdu = new PDU_type();
    pdu.which=PDU_type.deleteresultsetresponse_CID;
    DeleteResultSetResponse_type response = new DeleteResultSetResponse_type();
    pdu.o = response;
    response.referenceId = delete_request.referenceId;


    if ( delete_request.deleteFunction.intValue() == 0 )
    {
      // Delete the result sets identified by delete_request.resultSetList
      for ( Iterator task_list = delete_request.resultSetList.iterator(); task_list.hasNext(); )
      {
        String next_rs = (String) task_list.next();
        // AbstractIRResultSet st = (AbstractIRResultSet)(active_searches.get(next_rs));
        // active_searches.remove(next_rs);
        // search_service.deleteTask(st.getTaskIdentifier());
      }
    }
    else
    {
      // Function must be 1 : All sets in the association

      // active_searches.clear();
    }

    response.deleteOperationStatus=BigInteger.valueOf(0); // 0 = Success, 1=resultSetDidNotExist, 2=previouslyDeletedByTarget, 3=systemProblemAtTarget
                                                          // 4 = accessNotAllowed, 5=resourceControlAtOrigin
    try
    {
      assoc.encodeAndSend(pdu);
    }
    catch ( java.io.IOException ioe )
    {
      ioe.printStackTrace();
    }
  }

  public void notifyDeleteResult(BackendDeleteDTO bdr) {
    log.debug(bdr);
  }

  public void incomingAccessControlRequest(APDUEvent e)
  {
    log.info("Incoming accessControlRequest");
  }

  public void incomingAccessControlResponse(APDUEvent e)
  {
    log.info("Incoming AccessControlResponse");
  }

  public void incomingResourceControlRequest(APDUEvent e)
  {
    log.info("Incoming resourceControlRequest");
  }

  public void incomingTriggerResourceControlRequest(APDUEvent e)
  {
    log.info("Incoming triggetResourceControlRequest");
  }

  public void incomingResourceReportRequest(APDUEvent e)
  {
    log.info("Incoming resourceReportRequest");
  }

  public void incomingScanRequest(APDUEvent e)
  {
    ScanRequest_type scan_request = (ScanRequest_type) (e.getPDU().o);

    int step_size = 0;
    int scan_status = 0;
    int number_of_entries_returned = 0;
    int position_of_term = 0;

    // try
    // {
    //      if ( this.search_service instanceof Scanable )
    //      {
    //        Scanable s = (Scanable)this.search_service;
    //        if ( s.isScanSupported() )
    //        {
    //          String name = null;
    //          OIDRegisterEntry ent = reg.lookupByOID(scan_request.attributeSet);
    //          if ( ent != null )
    //            name=ent.getName();
    //
    //          // RootNode rn = new RootNode();
    //
    //          int i1 = ( scan_request.stepSize == null ? 0 : scan_request.stepSize.intValue() );
    //          int i2 = ( scan_request.numberOfTermsRequested == null ? 0 : 
    //                            scan_request.numberOfTermsRequested.intValue() );
    //          int i3 = ( scan_request.preferredPositionInResponse == null ? 0 : 
    //                            scan_request.preferredPositionInResponse.intValue() );
    //
    //          ScanRequestInfo sri = new ScanRequestInfo();
    //          sri.collections = scan_request.databaseNames;
    //          sri.attribute_set = name;
    //          // sri.term_list_and_start_point = org.jzkit.z3950.util.RPN2Internal.convertAPT(scan_request.termListAndStartPoint,rn);
    //          sri.step_size = i1;
    //          sri.number_of_terms_requested = i2;
    //          sri.position_in_response = i3;
    //
    //          ScanInformation scan_result = s.doScan(sri);
    //
    //          assoc.sendScanResponse(scan_request.referenceId,
    //                                 BigInteger.valueOf(i1),
    //                                 BigInteger.valueOf(scan_status),
    //                                 BigInteger.valueOf(scan_result.position),
    //                                 scan_result.results,
    //                                 scan_request.attributeSet,
    //                                 null);
    //        }
    //      }
    // }
    // catch ( java.io.IOException ioe )
    // {
    //   ioe.printStackTrace();
    // }
    // catch ( org.jzkit.SearchProvider.iface.ScanException se )
    // {
    //   se.printStackTrace();
    // }
  }

  public void incomingSortRequest(APDUEvent e) {
    log.info("Incoming sortRequest");
  }

  public void incomingSegmentRequest(APDUEvent e) {
    log.info("Incoming segmentRequest");
  }

  public void incomingExtendedServicesRequest(APDUEvent e) {
    log.info("Incoming extendedServicesRequest");
  }

  public void incomingClose(APDUEvent e) {
    log.debug("Close...");
    assoc.getPDUAnnouncer().deleteObserver(event_adapter);
    assoc.getPDUAnnouncer().deleteObservers();
    event_adapter = null;
    assoc.shutdown();

    try {
      assoc.join();
    }
    catch ( java.lang.InterruptedException ie ) {
    }
    log.debug("Done joining with assoc thread");

    log.debug("Deleting tasks...");
    assoc = null;
  }


  private Records_type createNonSurrogateDiagnostic(int condition, String addinfo) {
    Records_type retval = new Records_type();
    retval.which = Records_type.nonsurrogatediagnostic_CID;
    DefaultDiagFormat_type default_diag = new DefaultDiagFormat_type();
    retval.o = default_diag;
    default_diag.diagnosticSetId = reg.oidByName("diag-bib-1");
    default_diag.condition = BigInteger.valueOf(condition);
    if ( addinfo != null ) {
      default_diag.addinfo = new addinfo_inline14_type();
      default_diag.addinfo.which = addinfo_inline14_type.v2addinfo_CID;
      default_diag.addinfo.o = (Object)(addinfo);
    }
    return retval;
  }


  // Helper functions
  //
  //
  private Records_type createRecordsFor(InformationFragment[] raw_records, String spec) {

    log.debug("createRecordsFor... spec="+spec);
    RecordBuilder rb = null; // (RecordBuilder) ctx.getBean(spec.toLowerCase());

    Records_type retval = new Records_type();
    try {
      if ( rb != null ) {
        log.error("createRecordsFor called with unhandled record syntax");
        retval.which = Records_type.nonsurrogatediagnostic_CID;
        DefaultDiagFormat_type default_diag = new DefaultDiagFormat_type();
        retval.o = default_diag;
        default_diag.diagnosticSetId = reg.oidByName("diag-bib-1");
        default_diag.condition = BigInteger.valueOf(0);
        default_diag.addinfo = new addinfo_inline14_type();
        default_diag.addinfo.which = addinfo_inline14_type.v2addinfo_CID;
        default_diag.addinfo.o = (Object)("createRecordsFor called with unhandled record syntax");
      }
      else if ( raw_records != null ) {
        ArrayList v =  new ArrayList();
        retval.which = Records_type.responserecords_CID;
        retval.o = v;

        for ( int i=0; i<raw_records.length; i++ ) {
          log.debug("Adding record "+i+" to result");
  
          NamePlusRecord_type npr = new NamePlusRecord_type();
          npr.name = raw_records[i].getSourceCollectionName();
          npr.record = new record_inline13_type();

          try {
            // InformationFragment frag_in_req_format = rf.buildFrom(raw_records[i]);
            npr.record.which = record_inline13_type.retrievalrecord_CID;
            // npr.record.o = encodeRecordForZ3950(frag_in_req_format);
            npr.record.o = encodeRecordForZ3950(raw_records[i]);
          }
          catch ( Exception e ) {
            log.warn("Problem encoding respionse fragment",e);
            npr.record.which = record_inline13_type.surrogatediagnostic_CID;
            DiagRec_type diag_rec = new DiagRec_type();
            DefaultDiagFormat_type default_diag = new DefaultDiagFormat_type();
            default_diag.diagnosticSetId=reg.oidByName("diag-bib-1");
            default_diag.condition=BigInteger.valueOf(0);
            default_diag.addinfo = new addinfo_inline14_type();
            default_diag.addinfo.which = addinfo_inline14_type.v2addinfo_CID;
            default_diag.addinfo.o = (Object)(e.toString());
            diag_rec.which = DiagRec_type.defaultformat_CID;
            diag_rec.o = default_diag;
            npr.record.o = diag_rec;
          }
          v.add(npr);
        }
      }
      else {
        log.error("createRecordsFor called with no internal records");
        retval.which = Records_type.nonsurrogatediagnostic_CID;
        DefaultDiagFormat_type default_diag = new DefaultDiagFormat_type();
        retval.o = default_diag;
        default_diag.diagnosticSetId = reg.oidByName("diag-bib-1");
        default_diag.condition = BigInteger.valueOf(0);
        default_diag.addinfo = new addinfo_inline14_type();
        default_diag.addinfo.which = addinfo_inline14_type.v2addinfo_CID;
        default_diag.addinfo.o = (Object)("createRecordsFor called with no internal records");
      }
    }
    catch ( Exception pe ) {
      log.error("Error processing records in createRecordsFor.."+spec,pe);
      // Need to set up diagnostic in here
      retval.which = Records_type.nonsurrogatediagnostic_CID;
      DefaultDiagFormat_type default_diag = new DefaultDiagFormat_type();
      retval.o = default_diag;
      default_diag.diagnosticSetId = reg.oidByName("diag-bib-1");

      // if ( pe.additional != null )
      //   default_diag.condition = BigInteger.valueOf( Long.parseLong(pe.additional.toString()) );
      // else
        default_diag.condition = BigInteger.valueOf(0);

      default_diag.addinfo = new addinfo_inline14_type();
      default_diag.addinfo.which = addinfo_inline14_type.v2addinfo_CID;
      default_diag.addinfo.o = (Object)(pe.toString());
    } 

    return retval;
  }

  private EXTERNAL_type encodeRecordForZ3950(InformationFragment fragment) { 

    log.debug("encodeRecordForZ3950... encoding="+fragment.getFormatSpecification().getEncoding()+", schema="+fragment.getFormatSpecification().getSchema().toString());

    EXTERNAL_type rec = null;

    if ( fragment.getOriginalObject() instanceof Document ) {
      rec = new EXTERNAL_type();
      rec.direct_reference = reg.oidByName("xml");
      rec.encoding = new encoding_inline0_type();
      rec.encoding.which = encoding_inline0_type.octet_aligned_CID;
      try {
        Document d = (Document) fragment.getOriginalObject();
        log.debug("serialize "+d);
        OutputFormat format  = new OutputFormat( "xml","utf-8",false );
        format.setOmitXMLDeclaration(true);
        java.io.StringWriter  stringOut = new java.io.StringWriter();
        XMLSerializer serial = new XMLSerializer( stringOut,format );
        serial.setNamespaces(true);
        serial.asDOMSerializer();
        serial.serialize( d.getDocumentElement() );
        rec.encoding.o = stringOut.toString().getBytes("UTF-8");
      }
      catch( Exception e ) {
         e.printStackTrace();
         rec.encoding.o = new String(e.toString()).getBytes();
      }
    }
    else if ( fragment.getFormatSpecification().getEncoding().toString().equals("iso2709") ) {
      rec = new EXTERNAL_type();
      rec.direct_reference = reg.oidByName(fragment.getFormatSpecification().getSchema().toString());
      rec.encoding = new encoding_inline0_type();
      rec.encoding.which = encoding_inline0_type.octet_aligned_CID;
      rec.encoding.o = (byte[])(fragment.getOriginalObject());
    }
    else if ( fragment.getOriginalObject() instanceof String )
    {
      rec = new EXTERNAL_type();
      rec.direct_reference = reg.oidByName("sutrs");
      rec.encoding = new encoding_inline0_type();
      rec.encoding.which = encoding_inline0_type.single_asn1_type_CID;
      rec.encoding.o = fragment.toString();
    }
    else {
      throw new RuntimeException("unhandled object encoding: "+fragment.getFormatSpecification());
    }

    return rec;
  }

  private Records_type createNSD(String diag_code, String additional, Object[] params) {
    Records_type retval = new Records_type();
    retval.which = Records_type.nonsurrogatediagnostic_CID;
    DefaultDiagFormat_type default_diag = new DefaultDiagFormat_type();
    retval.o = default_diag;
 
    default_diag.diagnosticSetId = reg.oidByName("diag-bib-1");
 
    if ( diag_code != null )
      default_diag.condition = BigInteger.valueOf( Long.parseLong(diag_code) );
    else
      default_diag.condition = BigInteger.valueOf(0);
 
    if ( additional != null ) {
      default_diag.addinfo = new addinfo_inline14_type();
      default_diag.addinfo.which = addinfo_inline14_type.v2addinfo_CID;
      default_diag.addinfo.o = (Object)(additional);
    }

    return retval;
  }

  private ExplicitRecordFormatSpecification getExplicitFormat(String record_syntax,String element_set_name) {

    ExplicitRecordFormatSpecification result = null;

    if ( record_syntax.equalsIgnoreCase("usmarc") ) {
      result = new ExplicitRecordFormatSpecification("iso2709","usmarc",element_set_name);
    }
    else if ( record_syntax.equalsIgnoreCase("marc21") ) {
      result = new ExplicitRecordFormatSpecification("iso2709","marc21",element_set_name);
    }
    else if ( record_syntax.equalsIgnoreCase("ukmark") ) {
      result = new ExplicitRecordFormatSpecification("iso2709","ukmarc",element_set_name);
    }
    else if ( record_syntax.equalsIgnoreCase("sutrs") ) {
      result = new ExplicitRecordFormatSpecification("string","sutrs",element_set_name);
    }
    else if ( record_syntax.equalsIgnoreCase("xml") ) {
      result = new ExplicitRecordFormatSpecification("xml",element_set_name,null);
    }

    return result;
  }

  private void createAdditionalSearchInfo(List l, BackendStatusReportDTO backend_status_report) {
    String report = backend_status_report.toString();
    OtherInformationItem43_type result = new OtherInformationItem43_type();
    result.information = new information_inline44_type();
    result.information.which = information_inline44_type.characterinfo_CID;
    result.information.o = report;
    l.add(result);
  }
}
