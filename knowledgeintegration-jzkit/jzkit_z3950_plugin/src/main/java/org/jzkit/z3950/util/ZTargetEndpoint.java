/**
 * Title:       ZTargetEndpoint
 * @version:    $Id: ZTargetEndpoint.java,v 1.4 2004/11/19 16:37:42 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson ( ibbo@k-int.com )
 * Company:     Knowledge Integration Ltd.
 * Description: Utility class representing the endpoint for a Z39.50 Target
 */


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

/*
 * $Log: ZTargetEndpoint.java,v $
 * Revision 1.4  2004/11/19 16:37:42  ibbo
 * Changed the way oid register works. Now taken from app context
 *
 * Revision 1.3  2004/08/17 15:30:35  ibbo
 * Updated
 *
 * Revision 1.2  2004/08/17 15:13:04  ibbo
 * Updated
 *
 * Revision 1.1.1.1  2004/06/18 06:38:44  ibbo
 * Initial
 *
 * Revision 1.1.1.1  2004/03/08 15:25:39  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/12/05 16:30:44  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/11/16 15:10:42  ibbo
 * Initial import
 *
 * Revision 1.26  2003/06/17 18:27:21  rob_tice
 * check for null scan result - ie exception occurred in doScan - rob
 *
 * Revision 1.25  2003/05/09 12:54:47  rob_tice
 * Updated diagnostics and started removal of unused imports
 *
 * Revision 1.24  2002/12/11 10:25:32  ianibbo
 * Removed TASK_EXECUTING_ASYNC and TASK_EXECUTING_SYNC with TASK_EXECUTING.
 * Made HSSSearchTask behave a little better WRT timeouts.
 *
 * Revision 1.23  2002/11/29 13:24:03  ianibbo
 * Fixed license text
 *
 * Revision 1.22  2002/07/01 14:28:14  ianibbo
 * Updated a2jruntime that understands new marc variants and added appropriate
 * lines into Z client.
 *
 * Revision 1.21  2002/06/19 12:50:14  ianibbo
 * Added log history
 *
 */

package org.jzkit.z3950.util;

// Basic imports
import java.io.*;
import java.net.*;
import java.util.*;

import org.jzkit.a2j.codec.runtime.*;
import org.jzkit.a2j.gen.AsnUseful.*;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import java.math.BigInteger;

// for OID Register
import org.jzkit.a2j.codec.util.*;

// For AbstractIRResultSet definitions
// import org.jzkit.jiri.ResultSet.*;
// import org.jzkit.jiri.SearchProvider.iface.*;

import java.util.logging.*;

public class ZTargetEndpoint extends Thread
{
  private Socket z_assoc = null;

  private InputStream incoming_data = null;
  private OutputStream outgoing_data = null;

  private PDU_codec codec = PDU_codec.getCodec();
  // private OIDRegister reg = OIDRegister.getRegister();
  private OIDRegister reg = null;

  private boolean running = true;

  public APDUObservable pdu_announcer = new APDUObservable();
  private String charset_encoding = US_ASCII_ENCODING;
  private Properties props = null;

  private Logger log = Logger.getLogger(ZTargetEndpoint.class.getName());

  public static final String US_ASCII_ENCODING = "US-ASCII";
  public static final String UTF_8_ENCODING = "UTF-8";
  public static final String UTF_16_ENCODING = "UTF-16";

  public static Properties default_props = new Properties();

  static
  {
    default_props.setProperty("ImplementationId","174");
    default_props.setProperty("ImplementationName","K-Int Generic Z Server");
    default_props.setProperty("ImplementationVersion","1.0");
  }

  public ZTargetEndpoint(Socket s, Properties p, OIDRegister reg)
  {
    this.props = p;
    this.reg = reg;
  }

  public ZTargetEndpoint(Socket s, OIDRegister reg)
  {
    // A Z3950 endpoint... An origin or a target... makes no difference
    // Allows PDU's to be sent and notifies interested listeners
    // when PDU's arrive...
    z_assoc = s;
    this.props = default_props;
    this.reg = reg;

    try
    {
      incoming_data = s.getInputStream();
      outgoing_data = s.getOutputStream();
    }
    catch( java.io.IOException e )
    {
      log.log(Level.SEVERE,"Error constructing TargetEndpoint",e);
    }

  }

  protected void finalize()
  {
  }

  protected void notifyAPDUEvent(PDU_type pdu)
  {
    log.fine("notifyAPDUEvent(...)");
    byte[] refid = null;

    switch ( pdu.which )
    {
      case PDU_type.initrequest_CID:
        refid = ((InitializeRequest_type)pdu.o).referenceId;
        break;
      case PDU_type.searchrequest_CID:
        refid = ((SearchRequest_type)pdu.o).referenceId;
        break;
      case PDU_type.presentrequest_CID:
        refid = ((PresentRequest_type)pdu.o).referenceId;
        break;
      case PDU_type.deleteresultsetrequest_CID:
        refid = ((DeleteResultSetRequest_type)pdu.o).referenceId;
        break;
      case PDU_type.accesscontrolresponse_CID:
        refid = ((AccessControlRequest_type)pdu.o).referenceId;
        break;
      case PDU_type.resourcecontrolrequest_CID:
        refid = ((ResourceControlRequest_type)pdu.o).referenceId;
        break;
      case PDU_type.triggerresourcecontrolrequest_CID:
        refid = ((TriggerResourceControlRequest_type)pdu.o).referenceId;
        break;
      case PDU_type.resourcereportrequest_CID:
        refid = ((ResourceReportRequest_type)pdu.o).referenceId;
        break;
      case PDU_type.scanrequest_CID:
        refid = ((ScanRequest_type)pdu.o).referenceId;
        break;
      case PDU_type.sortrequest_CID:
        refid = ((SortRequest_type)pdu.o).referenceId;
        break;
      case PDU_type.segmentrequest_CID:
        refid = ((Segment_type)pdu.o).referenceId;
        break;
      case PDU_type.extendedservicesrequest_CID:
        refid = ((ExtendedServicesRequest_type)pdu.o).referenceId;
        break;
      case PDU_type.close_CID:
        refid = ((Close_type)pdu.o).referenceId;
        break;
      default:
        // Dunno... Just notify listener of incoming APDU
        // Should probably throw an exception?
    }

    // Create the new event and add the PDU that was sent
    APDUEvent e = new APDUEvent(this,pdu, ( refid != null ? new String(refid) : null ) );

    pdu_announcer.setChanged();
    pdu_announcer.notifyObservers(e);
  }



  //
  // Helper functions
  //

  public void sendInitResponse(byte[] refid, 
                               AsnBitString protocol_version, 
                               AsnBitString options,
                               long pref_message_size,
                               long exceptional_record_size,
                               boolean result,
                               String implementation_id,
                               String implementation_name,
                               String implementation_version,
                               EXTERNAL_type user_information,
                               ArrayList other_information) throws java.io.IOException
  {
    PDU_type pdu = new PDU_type();
    InitializeResponse_type initialize_response = new InitializeResponse_type();
    pdu.o = initialize_response;
    pdu.which = PDU_type.initresponse_CID;
 
    initialize_response.referenceId=refid;
    initialize_response.protocolVersion = protocol_version;
    initialize_response.options = options;
    initialize_response.preferredMessageSize = BigInteger.valueOf(pref_message_size);
    initialize_response.exceptionalRecordSize = BigInteger.valueOf(exceptional_record_size);
    initialize_response.result = new Boolean(result);
    initialize_response.implementationId = implementation_id;
    initialize_response.implementationName = implementation_name;
    initialize_response.implementationVersion = implementation_version;
    initialize_response.userInformationField = user_information;
    initialize_response.otherInfo = other_information;
 
    encodeAndSend(pdu);    
  }

// AbstractIRResultSet t,
  public void sendSearchResponse(Boolean search_status,
                                 int result_set_status,  // 2=interim, 3=none, only used if search_status == FALSE
                                 int result_set_size,
                                 int number_of_records_returned,
                                 int next_result_set_position) throws java.io.IOException
  {
    PDU_type pdu = new PDU_type();
    SearchResponse_type search_response = new SearchResponse_type();
    pdu.o = search_response;
    pdu.which = PDU_type.searchresponse_CID;

    // String refid = (String)t.getUserData();
    // search_response.referenceId = refid.getBytes();

    search_response.resultCount = BigInteger.valueOf(result_set_size);
    search_response.numberOfRecordsReturned = BigInteger.valueOf(number_of_records_returned);
    search_response.nextResultSetPosition = BigInteger.valueOf(next_result_set_position);

    search_response.searchStatus = search_status;
    
    if ( search_status.equals(Boolean.FALSE) )
    {
      // Only set if search result was not OK
      search_response.resultSetStatus = BigInteger.valueOf(result_set_status);
    }

    search_response.presentStatus = BigInteger.valueOf(0);
    search_response.records = new Records_type();
    search_response.records.which = Records_type.responserecords_CID;
    search_response.records.o = new ArrayList();

    // Now add n NamePlusRecord objects to the list
  
    encodeAndSend(pdu);    
  }

  public void sendScanResponse(byte[] refid,
                               BigInteger step_size,
                               BigInteger scan_status,
                               BigInteger position_of_term,
                               List scan_result,
                               int[] attrset,
                               Object additional)  throws java.io.IOException {
    PDU_type pdu = new PDU_type();
    ScanResponse_type scan_response = new ScanResponse_type();
    pdu.o = scan_response;
    pdu.which = PDU_type.scanresponse_CID;

    scan_response.referenceId = refid;
    scan_response.stepSize = step_size;
    scan_response.scanStatus = scan_status;
    
    if ( scan_result != null )
      scan_response.numberOfEntriesReturned = BigInteger.valueOf(scan_result.size());
    else
      scan_response.numberOfEntriesReturned = BigInteger.valueOf(0);
      
    scan_response.positionOfTerm = position_of_term;
    scan_response.attributeSet = attrset;
    scan_response.otherInfo = null;

    scan_response.entries = new ListEntries_type();
    scan_response.entries.nonsurrogateDiagnostics = null;
    scan_response.entries.entries = new ArrayList();
    
    if (scan_result != null) {
      for ( Iterator e = scan_result.iterator(); e.hasNext(); ) {
        TermInformation ir_term_info = (TermInformation)(e.next());
        Entry_type et = new Entry_type();
        if ( 1 == 1 ) {
          et.which = Entry_type.terminfo_CID;
          TermInfo_type term_info = new TermInfo_type();
          et.o = term_info;
          term_info.term = new Term_type();
          term_info.term.which = Term_type.general_CID;
          term_info.term.o = ir_term_info.the_term.getBytes();
          // term_info.displayTerm =
          // term_info.suggestedAttributes =
          // term_info.alternativeTerm =
          term_info.globalOccurrences = BigInteger.valueOf(ir_term_info.number_of_occurences);
          // term_info.byAttributes =
          // term_info.otherTermInfo =
        }
      	else {
          // We want to return a diagnostic
          et.which = Entry_type.surrogatediagnostic_CID;
      	}

        scan_response.entries.entries.add(et);
      }
    }	
    encodeAndSend(pdu);    
  }


  public void sendClose(int reason_code, String reason_text) throws java.io.IOException
  {
    PDU_type pdu = new PDU_type();
    pdu.which = PDU_type.close_CID;
    Close_type close = new Close_type();
    pdu.o = close;
    close.closeReason = BigInteger.valueOf(reason_code);
    close.diagnosticInformation = reason_text;
    encodeAndSend(pdu);
  }
                                 
  public void notifyClose()
  {
    PDU_type pdu = new PDU_type();
    pdu.which = PDU_type.close_CID;
    Close_type close = new Close_type();
    pdu.o = close;
    close.closeReason = Z3950Constants.CLOSE_REASON_TIMEOUT;
    close.diagnosticInformation = "Internal close notification";

    // This is a bit of a hack.. Notify ourselves that the assoc has closed.
    notifyAPDUEvent(pdu);
  }
  
  public void encodeAndSend(PDU_type the_pdu) throws java.io.IOException
  {
    log.fine("encodeAndSend(...)");
    synchronized(this)
    {
      BEROutputStream encoder = new BEROutputStream(charset_encoding, reg);
      codec.serialize(encoder, the_pdu, false, "PDU");
      encoder.flush();
      encoder.writeTo(outgoing_data);
      outgoing_data.flush();
    }
    yield();
  }

  public void shutdown()
  {
    log.fine("shutdown()");
    running=false;
    try
    {
      this.interrupt();
    }
    catch( java.lang.SecurityException se )
    {
      log.log(Level.SEVERE,"shutdown", se);
    }

  }

  public void run()
  {
    // System.out.println("Listening for incoming PDU's");

    while(running)
    {
      try
      {
        log.fine("Waiting for pdu on input stream");
        BERInputStream bds = new BERInputStream(incoming_data,charset_encoding,reg);
        PDU_type pdu = null;
        pdu = (PDU_type)codec.serialize(bds, pdu, false, "PDU");
        notifyAPDUEvent(pdu);
        yield();
      }
      catch ( java.io.InterruptedIOException iioe )
      {
        // Socket timeout, clean up and stop this thread. N.B. this exception
        // will only be caused if you have used setSoTimeout on the socket

        log.log(Level.SEVERE,"Processing java.io.InterruptedIOException, shut down association",iioe);

        running = false;

        try
        {
          sendClose(0, "Session Timeout");
        }
        catch ( java.io.IOException ioe )
        {
          // Don't worry, the peer might just have got the close PDU and
          // shut things down ahead of us...
        }

        notifyClose();

        // No need to close socket, but we should notify all listeners...
      }
      catch ( java.io.IOException ioe )
      {
        // Client snapped connection somehow...
        if(ioe.getMessage().equals("Connection Closed"))
          log.fine("Connection Closed");
        else
          log.log(Level.SEVERE,"Processing java.io.IOException, shut down association", ioe);
        running = false;
        notifyClose();
      }
      catch ( Exception e )
      {
        log.log(Level.SEVERE,"Processing exception : ",e);
        e.printStackTrace();
        running=false;
      }
    }

    log.fine("Assoc thread exiting (number of listeners="+pdu_announcer.countObservers()+")");

    try
    {
      incoming_data.close();
      outgoing_data.close();
      z_assoc.close();
    }
    catch ( Exception e )
    {
      log.log(Level.SEVERE,"Exception closing open sockets", e);
    }

    incoming_data = null;
    outgoing_data = null;
    z_assoc = null;
    pdu_announcer.deleteObservers();
    pdu_announcer = null;
  }

  public Observable getPDUAnnouncer()
  {
    return pdu_announcer;
  }

  public String getCharsetEncoding()
  {
    return charset_encoding;
  }

  public void setCharsetEncoding(String enc)
  {
    charset_encoding=enc;
  }
 
}
