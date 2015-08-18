// Title:       ZClient
// @version:    $Id: ZClient.java,v 1.3 2004/11/19 16:37:42 ibbo Exp $
// Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ibbo@k-int.com)
// Company:     KI
// Description: A Dummy Z39.50 Client to assist in testing the rest of the toolkit.
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

package org.jzkit.z3950.client;

import java.io.*;
import java.util.*;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import org.jzkit.z3950.util.Z3950Constants;

// for OID Register
import org.jzkit.a2j.codec.util.*;
import org.jzkit.a2j.gen.AsnUseful.*;

import org.jzkit.z3950.Z3950Exception;
import org.jzkit.search.util.QueryModel.InvalidQueryException;


/**
 * ZClient : A Simple Z3950 command line client to test the toolkit
 *
 * @version:    $Id: ZClient.java,v 1.3 2004/11/19 16:37:42 ibbo Exp $
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 *
 */
public class ZClient extends SynchronousOriginBean
{
  public static final char ISO2709_RS = 035;
  public static final char ISO2709_FS = 036;
  public static final char ISO2709_IDFS = 037;
  private static final String PREFIX_QUERY_TYPE = "PREFIX";
  private static final String CCL_QUERY_TYPE = "CCL";

  private int auth_type = 0; // 0=none, 1=anonymous, 2=open, 3=idpass
  private String principal = null;
  private String group = null;
  private String credentials = null;
  private String querytype = PREFIX_QUERY_TYPE;
  private String current_result_set_name = null;
  private int result_set_count=0;
  private String element_set_name = null; // Default to a null (no) element set name

  // private com.k_int.z3950.IRClient.Z3950Origin orig = null;

  public static void main(String args[])
  {
    ZClient newclient = new ZClient(new OIDRegister("/a2j.properties"));

    if ( args.length > 0 )
    {
      newclient.cmdOpen(args[0]);
    }

    newclient.commandShell();
  }

  public ZClient(OIDRegister reg)
  {
    super(reg);
  }

  public void commandShell()
  {
    BufferedReader br = new BufferedReader ( new InputStreamReader(System.in) );
    
    String command_line = new String();
    String command = new String();

    while ( command.regionMatches(true, 0, "quit", 0, 4) == false ) {
      try {
        System.out.print("ZClient > ");
        command_line = br.readLine();
        StringTokenizer st = new StringTokenizer(command_line, " ", true);

        if ( st.hasMoreTokens() )
        {
          command = st.nextToken();

          if (  command.equals("open") )
            cmdOpen( st.nextToken("") );
          else if (  command.equals("show") )
            cmdShow( st.nextToken("") );
          else if (  command.equals("find") )
            cmdFind( st.nextToken("") );
          else if (  command.equals("base") )
            cmdBase( st.nextToken("") );
          else if (  command.equals("elements") )
            cmdElements( st.nextToken("") );
          else if (  command.equals("format") )
            cmdFormat( st.nextToken("") );
          else if (  command.equals("auth") )
            cmdAuth( st.nextToken("") );
          else if (  command.equals("querytype") )
            cmdQueryType( st.nextToken("") );
          else if (  command.equals("scan") )
            cmdScan( st.nextToken("") );
          else
          {
            System.out.println("Commands:");
            System.out.println("  open hostname[:portnum]     - Connect to z server on host[:port]");
            System.out.println("  show n[+i]                  - show i records starting at n");
            System.out.println("  find [rpn-string]           - Process the supplied rpn query");
            System.out.println("  base db1 [db2.....]         - Search the specified databases");
            System.out.println("  elements [b|f|custom]       - select the specified element set name");
            System.out.println("  format [ xml|sutrs|grs..]   - Ask the server for the specified kind of records");
            System.out.println("  auth anon                   - use anonymous authentication");
            System.out.println("  auth open idstring          - use open authentication");
            System.out.println("  auth idpass user,group,pass - Use idpass authentication (user,none,pass for no group)");
            System.out.println("  querytype [prefix|ccl]      - Set query parser");
            System.out.println("  scan [rpn-string]");
            System.out.println("");
            System.out.println("  rpn strings are composed as follows: ");
            System.out.println("  rpn-string = @attrset default-attrset expr");
            System.out.println("  expr = [ attr-plus-term | boolean ]");
            System.out.println("  attr-plus-term = attrdef [ attrdef...] { single-term | \"quoted string\" }");
            System.out.println("  attrdef = @attr [attrset] attrtype=attrval ");
            System.out.println("  boolean = { @and | @or | @not } expr expr");
            System.out.println("");
        
          }
        }
      }
      catch( java.io.IOException ioe)
      {
        // System.err.println(ioe);
        ioe.printStackTrace();
      }
      catch ( java.util.NoSuchElementException nse )
      {
        System.err.println("Expected parameters for that command");
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }

    // Maybe we should send a close and stop the socket thread ;-)
    System.exit(0);
  }

  // Commands

  public void cmdOpen(String args)
  {
    // Extract ip address and port number
    // System.out.println("open \""+args+"\"");

    try
    {
      java.util.StringTokenizer st = new java.util.StringTokenizer(args,": ");

      String hostname = null;
      int portnum = 210;
      
      if ( st.hasMoreTokens() )
          hostname = st.nextToken();
      if ( st.hasMoreTokens() )
          portnum = Integer.parseInt(st.nextToken());

      InitializeResponse_type resp = connect(hostname,portnum,auth_type,principal,group,credentials);

      if ( resp.result.booleanValue() == true )
      {
        System.out.println("\n  Init Response");
	if ( resp.referenceId != null )
          System.out.println("Reference ID : "+new String(resp.referenceId));
        System.out.println("  Implementation ID : "+resp.implementationId);
        System.out.println("  Implementation Name : "+resp.implementationName);
        System.out.println("  Implementation Version : "+resp.implementationVersion);
        System.out.print("  Target Services : ");

	for ( int i=0; i<Z3950Constants.z3950_option_names.length; i++ ) {
	  if ( resp.options.isSet(i) )
	    System.out.print(Z3950Constants.z3950_option_names[i]+" ");
	}

        if ( resp.protocolVersion.isSet(0))
          System.out.println("v1 Support");
     
        if ( resp.protocolVersion.isSet(1))
          System.out.println("v2 Support");
     
        if ( resp.protocolVersion.isSet(2)) {
          System.out.println("v3 Support");
        }
     
        System.out.println("");

      }
      else
      {
        System.out.println("  Failed to establish association");
      }
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  }

  public void cmdFind(String args)
  {
    SearchResponse_type resp = null;
    current_result_set_name = "RS"+(result_set_count++);
    
    org.jzkit.search.util.QueryModel.QueryModel qm = null;

    try
    {
      if ( querytype.equalsIgnoreCase("CCL") )
        qm = new org.jzkit.search.util.QueryModel.CQLString.CQLString(args);
      else
        qm = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString(args);

      if ( qm == null )
        throw new InvalidQueryException("Failed to parse "+querytype+" query");

      resp = sendSearch(qm, null, current_result_set_name, element_set_name);
    }
    catch ( Z3950Exception se )
    {
      // cat.warn("Problem processing query",se);
      System.err.println("find: "+querytype+":"+args);
      se.printStackTrace();
      // If possible, the search response information will be passed along with
      // the exception
      // if ( se.getAddinfo() != null )
      //   resp = (SearchResponse_type)(se.getAddinfo());
    }
    catch ( InvalidQueryException iqe )
    {
      // cat.warn("Problem parsing query",iqe);
      System.err.println("find: "+args);
      iqe.printStackTrace();
    }
    catch ( Exception e )
    {
      // cat.warn("Problem processing query",e);
      System.err.println("find: "+args);
      e.printStackTrace();
    }

    if ( resp != null )
    {
      System.out.println("\n  Search Response");
      if ( resp.referenceId != null )
        System.out.println("  Reference ID : "+new String(resp.referenceId));
      System.out.println("  Search Result : "+resp.searchStatus);
      System.out.println("  Result Count : "+resp.resultCount);
      System.out.println("  Num Records Returned : "+resp.numberOfRecordsReturned);
      System.out.println("  Next RS position : "+resp.nextResultSetPosition);    

      if ( null != resp.additionalSearchInfo )
      {
        System.out.println("  search response contains "+resp.additionalSearchInfo.size()+" additionalSearchInfo entries");
      }

      if ( null != resp.otherInfo )
      {
        System.out.println("  search response contains "+resp.otherInfo.size()+" otherInfo entries");
      }
 
      if ( ( resp.records != null ) && ( resp.numberOfRecordsReturned.intValue() > 0 ) )
      {
        System.out.println("  Search has piggyback records");
        displayRecords(resp.records);
      }

      System.out.println("");   
    }
  }

  public void cmdScan(String args)
  {
    ScanResponse_type resp = null;

    try
    {
      if ( querytype.equals("CCL") )
        resp = sendScan(new org.jzkit.search.util.QueryModel.CQLString.CQLString(args));
      else
        resp = sendScan(new  org.jzkit.search.util.QueryModel.PrefixString.PrefixString(args));

      System.out.println("Scan Response");
      System.out.println("-------------");
      if ( resp.referenceId != null )
        System.out.println("  Reference ID : "+new String(resp.referenceId));
      System.out.println("Step size : "+resp.stepSize);
      System.out.println("Scan Status : "+resp.scanStatus);
      System.out.println("Number of entries returned: "+resp.numberOfEntriesReturned);
      System.out.println("Position of term: "+resp.positionOfTerm);

      if ( resp.entries != null )
      {
        if ( resp.entries.nonsurrogateDiagnostics != null )
	{
          System.out.println("Non-surrogate diagnostics");
          for ( Iterator e = resp.entries.nonsurrogateDiagnostics.iterator(); e.hasNext(); )
          {
            DiagRec_type diag = (DiagRec_type)e.next();
	    switch(diag.which)
	    {
              case DiagRec_type.defaultformat_CID:
                DefaultDiagFormat_type ddf = (DefaultDiagFormat_type)diag.o;
                System.out.println("Default diagnostic: "+ddf.condition+" "+ddf.addinfo.o.toString());
                break;
              case DiagRec_type.externallydefined_CID:
                System.out.println("External diagnostic");
                break;
	    }
          }
	}

	if ( resp.entries.entries != null )
	{
          for ( Iterator e = resp.entries.entries.iterator(); e.hasNext(); )
          {
            Entry_type et = (Entry_type)e.next();
    	    switch(et.which)
	    {
              case Entry_type.terminfo_CID:
                TermInfo_type ti = (TermInfo_type)(et.o);
		Term_type tt = ti.term;
		if ( tt.o instanceof byte[] )
	            System.out.println(new String((byte[])(tt.o)));
		else
	            System.out.println(tt.o.toString());
                break;
              case Entry_type.surrogatediagnostic_CID:
	        DiagRec_type diag = (DiagRec_type)(et.o);
                break;
	    }
          }
	}
      }
    }
    catch ( Exception e )
    {
      System.out.println(e);
    }
  }

  public void cmdShow(String args)
  {
    // Format for present command is n [ + n ]
    System.err.println("Present "+args);
    
    try
    {
      java.util.StringTokenizer st = new java.util.StringTokenizer(args,"+ ");
      int from = 1;
      int count = 1;
      String setname = current_result_set_name;

      from = Integer.parseInt(st.nextToken());

      if ( st.hasMoreTokens() )
        count = Integer.parseInt(st.nextToken());

      if ( st.hasMoreTokens() )
        setname = st.nextToken();

      PresentResponse_type resp = sendPresent(from,count,element_set_name,setname);

      System.out.println("\n  Present Response");

      if ( resp.referenceId != null )
        System.out.println("  Reference ID : "+new String(resp.referenceId));
 
      System.out.println("  Number of records : "+resp.numberOfRecordsReturned);
      System.out.println("  Next RS Position : "+resp.nextResultSetPosition);
      System.out.println("  Present Status : "+resp.presentStatus);
      System.out.println("");
 
      Records_type r = resp.records;
 
      displayRecords(r);   
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      System.err.println("Exception processing show command "+e);
    }
  }

  public void cmdBase(String args)
  {
    db_names.clear();

    try
    {
      java.util.StringTokenizer st = new java.util.StringTokenizer(args," ");

      clearAllDatabases();

      while ( st.hasMoreTokens() )
        addDatatabse(st.nextToken());

      System.err.println("dbnames:"+db_names);
    }
    catch ( Exception e )
    {
      System.err.println("Exception processing base command "+e);
    }
  }

  //
  // Change the element set name in use (Normally "F" or "B")
  //
  public void cmdElements(String args)
  {
    // Right now, args is prefixed with a space... So junk it.
    try
    {
      java.util.StringTokenizer st = new java.util.StringTokenizer(args," ");
      if ( st.hasMoreTokens() )
        setElementSetName( st.nextToken() );
      else
        setElementSetName( null );
    }
    catch ( Exception e )
    {
      System.err.println("Exception processing base command "+e);
    }
  }

  //
  // Change the element set name in use (Normally "F" or "B")
  //
  public void cmdFormat(String args)
  {
    // Right now, args is prefixed with a space... So junk it.
    try
    {
      java.util.StringTokenizer st = new java.util.StringTokenizer(args," ");
      if ( st.hasMoreTokens() )
      {
        String requested_syntax = st.nextToken();
        if ( reg.oidByName(requested_syntax) != null )
          setRecordSyntax( requested_syntax );
	else
          System.out.println("Unknown Record Syntax");
      }
    }
    catch ( Exception e )
    {
      System.err.println("Exception processing format command "+e);
    }
  } 

  //
  // Change the element set name in use (Normally "F" or "B")
  //
  public void cmdAuth(String args)
  {
    try
    {
      java.util.StringTokenizer st = new java.util.StringTokenizer(args," ,");
 
      if ( st.hasMoreTokens() )
      {
        String type = st.nextToken();
        if ( type.equals("anon") )
        {
          System.out.println("Will use anonymous authentication");
          auth_type = 1;
        }
        else if ( type.equals("open") )
        {
	  System.out.println("Will use open authentication");
          if ( st.hasMoreTokens() )
          {
            auth_type = 2;
            principal = st.nextToken();
            System.out.println("Open auth string will be "+principal);
          }
          else
          {
            System.out.println("Asked for open authentication but no open string supplied, No auth will be used");
          }
        }
        else if ( type.equals("idpass") )
        {
	  System.out.println("Will use idpass authentication");
          auth_type = 3;
          if ( st.hasMoreTokens() )
            principal = st.nextToken();
          if ( st.hasMoreTokens() )
            group = st.nextToken();
          if ( st.hasMoreTokens() )
            credentials = st.nextToken();
        }
        else
        {
          System.out.println("Unrecognised auth type, no authentication will be used");
          auth_type = 0;
        }
      }
      else
      {
        System.out.println("No auth type, no authentication will be used");
      }
    }
    catch ( Exception e )
    {
      System.err.println("Exception processing base command "+e);
    }
  }

  public void cmdQueryType(String args)
  {
    // Right now, args is prefixed with a space... So junk it.
    try
    {
      java.util.StringTokenizer st = new java.util.StringTokenizer(args," ");
      if ( st.hasMoreTokens() )
      {
        String type = st.nextToken();
        System.out.println("Set query type to "+type);

        if ( type.equals("CCL") )
          querytype=CCL_QUERY_TYPE;
        else
          querytype=PREFIX_QUERY_TYPE;
      }
    }
    catch ( Exception e )
    {
      System.err.println("Exception processing base command "+e);
    }
  }

  public void dumpOID(int[] oid)
  {
    System.out.print("{");
    for ( int i = 0; i < oid.length; i++ )
    {
      System.out.print(oid[i]+" ");
    }
    System.out.println("}");
  }

  public void displayRecords(Records_type r)
  {
    if ( r != null )
    {
      switch ( r.which )
      {
        case Records_type.responserecords_CID:
            List v = (List)(r.o);
            int num_records = v.size();
            System.out.println("Response contains "+num_records+" Response Records");
            for ( Iterator recs = v.iterator(); recs.hasNext(); ) 
            {
                NamePlusRecord_type npr = (NamePlusRecord_type)(recs.next());

                if ( null != npr )
                {
                  System.out.print("["+npr.name+"] ");

                  switch ( npr.record.which )
                  {
                    case record_inline13_type.retrievalrecord_CID:
                      // RetrievalRecord is an external
                      EXTERNAL_type et = (EXTERNAL_type)npr.record.o;
                      // System.out.println("  Direct Reference="+et.direct_reference+"] ");
                      // dumpOID(et.direct_reference);
                      // Just rely on a toString method for now
                      if ( et.direct_reference.length == 6 )
                      {
                        switch(et.direct_reference[(et.direct_reference.length)-1])
                        {
                          case 1: // Unimarc
                            System.out.print("Unimarc ");
                            DisplayISO2709((byte[])et.encoding.o);
                            break;
                          case 3: // CCF
                            System.out.print("CCF ");
                            break;
                          case 10: // US Marc
                            System.out.print("USMarc: ");
                            DisplayISO2709((byte[])et.encoding.o);
                            break;
                          case 11: // UK Marc
                            System.out.print("UkMarc ");
                            DisplayISO2709((byte[])et.encoding.o);
                            break;
                          case 12: // Normarc
                            System.out.print("Normarc ");
                            DisplayISO2709((byte[])et.encoding.o);
                            break;
                          case 13: // Librismarc
                            System.out.print("Librismarc ");
                            DisplayISO2709((byte[])et.encoding.o);
                            break;
                          case 14: // Danmarc
                            System.out.print("Danmarc ");
                            DisplayISO2709((byte[])et.encoding.o);
                            break;
                          case 15: // Finmarc
                            System.out.print("Finmarc ");
                            DisplayISO2709((byte[])et.encoding.o);
                            break;
			  case 100: // Explain
			    // cat.debug("Explain record");
			    // Write display code....
			    break;
			  case 101: // SUTRS
			    System.out.print("SUTRS ");
			    System.out.println((String)et.encoding.o);
			    break;
			  case 102: // Opac
			    // cat.debug("Opac record");
			    // Write display code....
			    break;
			  case 105: // GRS1
			    System.out.print("GRS1 ");
			    displayGRS((java.util.List)et.encoding.o);
			    break;
                          default:
			    System.out.print("Unknown.... ");
                            System.out.println(et.encoding.o.toString());
                            break;
                        }
                      }
                      else if ( ( et.direct_reference.length == 7 ) &&
                                ( et.direct_reference[5] == 109 ) )
                      {
                        switch(et.direct_reference[6])
                        {
                          case 3: // HTML
                            System.out.print("HTML ");
							String html_rec = null;
							if ( et.encoding.o instanceof byte[] )
								html_rec = new String((byte[])et.encoding.o);
							else
								html_rec = et.encoding.o.toString();                             
                            System.out.println(html_rec.toString());
                            break;
                          case 9: // SGML
                            System.out.print("SGML ");
                            System.out.println(et.encoding.o.toString());
                            break;
                          case 10: // XML
                            System.out.print("XML ");
                            System.out.println(new String((byte[])(et.encoding.o)));
                            break;
                          default:
                            System.out.println(et.encoding.o.toString());
                            break;
                         }
                      }
                      else
                        System.out.println("Unknown direct reference OID: "+et.direct_reference);
                      break;
                    case record_inline13_type.surrogatediagnostic_CID:
                      System.out.println("SurrogateDiagnostic");
                      break;
                    case record_inline13_type.startingfragment_CID:
                      System.out.println("StartingFragment");
                      break;
                    case record_inline13_type.intermediatefragment_CID:
                      System.out.println("IntermediateFragment");
                      break;
                    case record_inline13_type.finalfragment_CID:
                      System.out.println("FinalFragment");
                      break;
                    default:
                      System.out.println("Unknown Record type for NamePlusRecord");
                      break;
                  }
                }
                else
                {
                  System.out.println("Error... record ptr is null");
                }
            }
            break;
        case Records_type.nonsurrogatediagnostic_CID:
	    DefaultDiagFormat_type diag = (DefaultDiagFormat_type)r.o;
            System.out.println("    Non surrogate diagnostics : "+diag.condition);
	    if ( diag.addinfo != null )
	    {
              // addinfo is VisibleString in v2, InternationalString in V3
              System.out.println("Additional Info: "+diag.addinfo.o.toString());
	    }
            break;
        case Records_type.multiplenonsurdiagnostics_CID:
            System.out.println("    Multiple non surrogate diagnostics");
            break;
        default:
            System.err.println("    Unknown choice for records response : "+r.which);
            break;
      }
    }

    // if ( null != e.getPDU().presentResponse.otherInfo )
    //   System.out.println("  Has other information");
    System.out.println("");

  }


  // private void DisplayISO2709(byte[] record)
  private void DisplayISO2709(byte[] octets)
  {
    String orig = new String(octets);

    int record_length = Integer.parseInt(orig.substring(0,5));   // Positions 0-4 are length of record
    char record_status = orig.charAt(5);
    char type_of_record = orig.charAt(6);
    int indicator_length = 2;                                    // Default indicator length
    int subfield_code_length = 2;                                // Default indicator length
    char character_coding_scheme = orig.charAt(9);
    int base_address = Integer.parseInt(orig.substring(12,17));  //
 
    int length_field_length = Character.digit(orig.charAt(20),10);
    int length_entry = Character.digit(orig.charAt(21),10);
 
    if ( Character.isDigit(orig.charAt(10)) )
      indicator_length = Character.digit(orig.charAt(10), 10);
 
    if ( Character.isDigit(orig.charAt(11)) )
      subfield_code_length = Character.digit(orig.charAt(11), 10);
 
    int offset = 24;

    while ( orig.charAt(offset) != ISO2709_FS )
    {
      // int tag = Integer.parseInt(orig.substring(offset, offset+3));
      String tag = orig.substring(offset, offset+3);
      System.out.print(tag+" ");
      offset += 3;
      int data_length = Integer.parseInt(orig.substring(offset, offset+length_field_length));
      offset += length_field_length;
      int data_offset = Integer.parseInt(orig.substring(offset, offset+length_entry));
      offset += length_entry;
 
      String this_tag = orig.substring(base_address+data_offset+2, base_address+data_offset+data_length);

 
      StringTokenizer st = new StringTokenizer(this_tag, ""+ISO2709_RS+ISO2709_FS+ISO2709_IDFS, true);
 
      String subfield = null;
 
      while ( st.hasMoreTokens() )
      {
        subfield = st.nextToken();
 
        if ( subfield.charAt(0) == ISO2709_RS )
        {
          // System.err.print("Record sep ");
        }
        else if ( subfield.charAt(0) == ISO2709_FS )
        {
          // System.err.print("Field sep ");
        }
        else if ( subfield.charAt(0) == ISO2709_IDFS )
        {
          subfield = st.nextToken();
          System.out.print("$"+subfield.charAt(0)+" "+subfield.substring(1, subfield.length()));
        }
        else
        {
          System.out.print(subfield);
        }
      }
      System.out.println("");
    }
  }

  private void displayGRS(List v)
  {
    org.jzkit.z3950.RecordModel.GRS1 grs_rec = new  org.jzkit.z3950.RecordModel.GRS1("Repository",
		                                                                     "Coll", 
             									     "", 
	             							     	     v,
		             							     null,
		             							     reg);
    System.out.println(grs_rec.toString());
  }

  public void setElementSetName(String element_set_name)
  {
    this.element_set_name = element_set_name;
  }

  public String getElementSetName()
  {
    return element_set_name;
  }
}
