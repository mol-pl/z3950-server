// Title:       Z3950SearchTracker
// @version:    $Id: Z3950SearchTask.java,v 1.15 2005/10/27 16:18:03 ibbo Exp $
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

import java.util.*;

// Information Retrieval Interfaces
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;

import org.jzkit.a2j.codec.util.*;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import org.jzkit.z3950.RecordModel.*;
import org.jzkit.z3950.QueryModel.*;
import org.jzkit.a2j.gen.AsnUseful.*;
 
import org.jzkit.util.*;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;

public class Z3950SearchTask extends AbstractIRResultSet implements IRResultSet
{
  public static final int ZSTATUS_NONE         = 0;
  public static final int ZSTATUS_IDLE         = 1;
  public static final int ZSTATUS_SEARCHING     = 2;
  public static final int ZSTATUS_SEARCH_COMPLETE   = 3;
  public static final int ZSTATUS_PRESENTING     = 4;
  public static final int ZSTATUS_ALL_PRESENTED   = 5;
  public static final int ZSTATUS_SORTING       = 6;
  public static final int ZSTATUS_SORT_COMPLETE   = 7;
  public static final int ZSTATUS_ERROR       = 8;

  private static final String[] private_status_types = { "Undefined",
                                                         "Idle", 
                                                         "Searching", 
                                                         "Search complete", 
                                                         "Requesting records", 
                                                         "All records returned",
                                                         "Sorting",
                                                         "Sort Complete",
                                                         "Error" };
  public int z3950_status = 0;
  private Z3950Origin protocol_endpoint = null;
  private int fragment_count = 0;
  private static Log log = LogFactory.getLog(Z3950SearchTask.class);
  public static int dbg_counter = 0;

  // Wait indefinitely for records to be presented.
  private int default_present_timeout = 60000;

  //private OIDRegisterEntry default_recsyn       = null;
  private RecordFormatSpecification default_spec = null;
  private String grs_record_profile = null;
  private Hashtable outstanding_requests = new Hashtable();
  private String charset = "UTF-8";

  private DocumentBuilder docBuilder = null;

  // This will hold result records. It may be that we start to throw out elements
  // on a LRU basis at some point in the future
  //

  public Z3950SearchTask(Z3950Origin protocol_endpoint, 
                         Observer[] observers, 
                         RecordFormatSpecification default_spec) {
    super(observers);
    dbg_counter++;
    this.protocol_endpoint = protocol_endpoint;
    this.default_spec = default_spec;
    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      docFactory.setNamespaceAware(true);
      docFactory.setValidating(false);
      docBuilder = docFactory.newDocumentBuilder();
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  protected void finalize()
  {
    dbg_counter--;
    log.debug("Z3950SearchTask::finalize() ("+dbg_counter+" active)");
  }

  protected void setPrivateStatusCode(int code)
  {
    z3950_status = code;
  }

  public int getPrivateTaskStatusCode()
  {
    return z3950_status;
  }

  public String lookupPrivateStatusCode(int code)
  {
    return private_status_types[code];
  }

  public InformationFragment[] getFragment(int starting_fragment, 
                                           int count,
                                           RecordFormatSpecification spec) throws IRResultSetException {
    if ( protocol_endpoint.connected() ) {
      ExplicitRecordFormatSpecification actual_spec = interpretSpec(spec);

      log.debug("Z3950SearchTask::getFragment - "+protocol_endpoint.getServiceId()+","+protocol_endpoint.getServiceName()+","+protocol_endpoint.getHost()+".");
      log.debug("Z3950SearchTask::getFragment("+starting_fragment+","+count+","+actual_spec+")");

      if ( starting_fragment > fragment_count )
        throw new IRResultSetException("Present out of range, only "+fragment_count+" records available");

      if ( starting_fragment+count-1 > fragment_count )
      {
        count=fragment_count-starting_fragment+1;
        log.debug("get asks for record past end of result set, trim to "+count);
      }

      InformationFragment[] result = null;
      PresentResponse_type pr = protocol_endpoint.fetchRecords(getSetID(),
                                                               actual_spec,
                                                               starting_fragment,
                                                               count,
                                                               default_present_timeout);
      return processRecords(pr.records,actual_spec,starting_fragment);
    }
    else {
      log.debug("GF Remote target has become disconnected, mark this result set as FAILURE");
      this.setStatus(IRResultSetStatus.FAILURE);
    }

    return null;
  }

  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification spec,
                               IFSNotificationTarget target) throws IRResultSetException {

    if ( protocol_endpoint.connected() ) {
      ExplicitRecordFormatSpecification actual_spec = interpretSpec(spec);

      log.debug("Z3950SearchTask::asyncgetFragment("+starting_fragment+","+count+","+actual_spec+")");

      if ( starting_fragment > fragment_count ) {
        throw new IRResultSetException("Present out of range, only "+fragment_count+" records available");
      }
      else {
        if ( starting_fragment+count-1 > fragment_count )
        {
          count=fragment_count-starting_fragment+1;
          log.debug("get asks for record past end of result set, trim to "+count);
        }

        try {
          protocol_endpoint.asyncFetchRecords(getSetID(),
                                              actual_spec,
                                              starting_fragment,
                                              count,
                                              new PresentCallbackHandler(this, target, actual_spec, starting_fragment));
        }
        catch ( org.jzkit.search.util.ResultSet.IRResultSetException rse ) {
          log.warn("Problem",rse);
          target.notifyError("bib1-diag",null,"Problem", new IRResultSetException(rse.toString()));
        }
      }
    }
    else {
      log.debug("Remote target has become disconnected, mark this result set as FAILURE");
      this.setStatus(IRResultSetStatus.FAILURE);
      target.notifyError("bib1-diag",null,"Problem", new IRResultSetException("Connection Closed"));
    }
  }

  protected InformationFragment[] processRecords(Records_type r,ExplicitRecordFormatSpecification actual_spec, int starting_fragment) {

    InformationFragment[] result = null;

    if (  r != null ) {
      switch ( r.which ) {
        case Records_type.responserecords_CID:
          ArrayList v = (ArrayList)(r.o);
          int num_records = v.size();
          int counter=0;
          result = new InformationFragment[num_records];

          log.debug("Response contains "+num_records+" Response Records");

          for ( Iterator recs = v.iterator(); recs.hasNext(); ) {
            int hitno = starting_fragment+counter;

            NamePlusRecord_type npr = (NamePlusRecord_type)(recs.next());

            if ( null != npr ) {
              String source_name = protocol_endpoint.getTargetDN();
              String source_collection = npr.name;
              if ( source_collection == null )
                source_collection = "No Collection Given";

              log.debug("Result record, source name:"+source_name+" source collection:"+source_collection+".");

              switch ( npr.record.which ) {
                case record_inline13_type.retrievalrecord_CID:
                  // RetrievalRecord is an external
                  EXTERNAL_type et = (EXTERNAL_type)npr.record.o;
                  int[] record_oid = et.direct_reference;
                  ExplicitRecordFormatSpecification spec = FormatSpecOIDHelper.getSpec(protocol_endpoint.reg,
                                                                                       record_oid,
                                                                                       (String)null,
                                                                                       null);
                  log.debug("Derived record spec : "+spec);

                  switch(et.direct_reference.length) {
                    case 6:
                      switch(et.direct_reference[5]) {
                            case 1: // Unimarc
                              result[counter++] = 
                                new org.jzkit.search.util.RecordModel.InformationFragmentImpl(hitno,
                                                                                              source_name,
                                                                                              source_collection,
                                                                                              null,
                                                                                              et.encoding.o,
                                                                                              new ExplicitRecordFormatSpecification("iso2709:unimarc:F"));
                              break;
                            case 3: // CCF
                              // System.out.println("CCF");
                              break;
                            case 10: // US Marc
                            case 11: // UK Marc
                            case 12: // Normarc
                            case 13: // Librismarc
                            case 14: // Danmarc
                            case 15: // Finmarc
                            case 21: // IberMarc
                            case 22: // CatMarc
            // In Thailand, USMarc records contain Cp874 8-bit characters.
            // We allow the charset property to specify how we should
            // construct string representations of marc records.
                              result[counter++] = 
                                new org.jzkit.search.util.RecordModel.InformationFragmentImpl(hitno,
                                                                                              source_name,
                                                                                              source_collection,
                                                                                              null,
                                                                                              et.encoding.o,
                                                                                              spec);
                              break;
                            case 100: // Explain
                              log.debug("Explain");
                              result[counter++] = 
                                new org.jzkit.search.util.RecordModel.InformationFragmentImpl(hitno,
                                                                                              source_name,
                                                                                              source_collection,
                                                                                              null,
                                                                                              et.encoding.o,
                                                                                              new ExplicitRecordFormatSpecification("java:"+et.encoding.o.getClass().getName()+":F"));
                              break;
                            case 101: // SUTRS
                              log.debug("SUTRS");
                              result[counter++] = 
                              new org.jzkit.search.util.RecordModel.InformationFragmentImpl(hitno,
                                                                                            source_name,
                                                                                            source_collection,
                                                                                            null,
                                                                                            et.encoding.o,
                                                                                            new ExplicitRecordFormatSpecification("string::F"));

                              break;
                            case 102: // Opac
                              log.debug("Opac record");
                              result[counter++] = 
                               new org.jzkit.search.util.RecordModel.InformationFragmentImpl(hitno,
                                                                                             source_name,
                                                                                             source_collection,
                                                                                             null,
                                                                                             et.encoding.o,
                                                                                             new ExplicitRecordFormatSpecification("asn1:opac:F"));
                            case 105: // GRS1
                              log.debug("GRS1");
                              // Special processing on spec here in case we have a profile specified for
                              // the specific GRS-1 record profile in use at this target.
                              // result[counter++] = new GRS1(source_name,
                              //                              source_collection,  
                              //                              null, 
                              //                              (java.util.ArrayList)(et.encoding.o),
                              //                              FormatSpecOIDHelper.getSpec(protocol_endpoint.reg,
                              //                                                          record_oid,
                              //                                                          (String)null,
                              //                                                          grs_record_profile),
                              //                              protocol_endpoint.reg);
                              break;
                            default:
                              log.info("unknow Syntax OID ending with "+et.direct_reference[4]);
                              result[counter++] =  new org.jzkit.search.util.RecordModel.InformationFragmentImpl(hitno,
                                                                                             source_name,
                                                                                             source_collection,
                                                                                             null,
                                                                                             et.encoding.o,
                                                                                             new ExplicitRecordFormatSpecification("bytes::F"));
                              break;
                          }
                          break;

                        case 7:
                          if ( et.direct_reference[5] == 109 )
                          {
                            // Various Brinary formats, PDF, Jpeg, etc... add later
                            switch( et.direct_reference[6] )
                            {
                              case 1:
                                log.debug("PDF Document...");
                                break;
                              case 3:
                                log.debug("HTML record...");
                                String html_rec = null;
                                if ( et.encoding.o instanceof byte[] )
                                  html_rec = new String((byte[])et.encoding.o);
                                else
                                  html_rec = et.encoding.o.toString();  
                                break;
                              case 9:
                                log.debug("SGML record...");
                                break;
                              case 10: // XML
                                // String rec = new String((byte[])(et.encoding.o));
                                // result[counter++] =  new org.jzkit.search.util.RecordModel.InformationFragmentImpl(0,
                                //                                                             source_name,
                                //                                                             source_collection,
                                //                                                             null,
                                //                                                             rec,
                                //                                                             new ExplicitRecordFormatSpecification("string::F"));
                                try {
                                  Document new_result_document = docBuilder.parse(new java.io.ByteArrayInputStream((byte[])(et.encoding.o)));
                                  result[counter++] = new InformationFragmentImpl(hitno,
                                                                                  source_name,
                                                                                  source_collection,
                                                                                  null,
                                                                                  new_result_document,
                                                                                  actual_spec);
                                }
                                catch ( Exception e ) {
                                  e.printStackTrace();
                                }

                                break;
                              default:
                                break;
                            }
                    }
                    else {
                      log.info("Unhandled 7-int OID for record type");
                    }
                    break;
                  }
                  break;

                case record_inline13_type.surrogatediagnostic_CID:
                  log.info("SurrogateDiagnostic");
                  DiagRec_type d = (DiagRec_type) npr.record.o;
                  if ( d.which == DiagRec_type.defaultformat_CID ) {
                    DefaultDiagFormat_type ddf   = (DefaultDiagFormat_type)d.o;
                    String reason = "Diagnostic "+(ddf.addinfo != null ? ddf.addinfo.toString() : "none" );
                    log.info("  code:"+ddf.condition+" reason:"+reason);
                    setDiagnosticStatus("diag.bib1."+ddf.condition,protocol_endpoint.getTargetName(), source_name+" "+source_collection);

                    result[counter++] =  new org.jzkit.search.util.RecordModel.InformationFragmentImpl(hitno,
                                                                                             source_name,
                                                                                             source_collection,
                                                                                             null,
                                                                                             ddf.condition,
                                                                                             new ExplicitRecordFormatSpecification("string::F"));
                  }
                  else // externallydefined_CID
                  {
                    log.info("Externally defined surrogate");
                    setDiagnosticStatus("diag.k-int.7", protocol_endpoint.getTargetName(),source_name+" "+source_collection);
                    result[counter++] =  new org.jzkit.search.util.RecordModel.InformationFragmentImpl(hitno,
                                                                                             source_name,
                                                                                             source_collection,
                                                                                             null,
                                                                                             "diag",
                                                                                             new ExplicitRecordFormatSpecification("string::F"));
                  }
                  break;

                case record_inline13_type.startingfragment_CID:
                  log.info("StartingFragment");
                  break;

                case record_inline13_type.intermediatefragment_CID:
                  log.info("IntermediateFragment");
                  break;

                case record_inline13_type.finalfragment_CID:
                  log.info("FinalFragment");
                  break;

                default:
                  log.info("Unhandled record type");
              }
            }
            else {
              log.debug("Error... record ptr is null");
            }
          }
          break;

        case Records_type.nonsurrogatediagnostic_CID:
          // Record contains defaultDiagFormat object
          DefaultDiagFormat_type d = (DefaultDiagFormat_type)r.o;
          if ( d.addinfo != null ) {
            log.info("Non surrogate diagnostics ["+d.condition+"] Additional Info : "+d.addinfo.o);
            setDiagnosticStatus("diag.bib1."+d.condition,protocol_endpoint.getTargetName(),"Additional Info : "+d.addinfo.o);
          }
          else {
            log.info("Non surrogate diagnostics ["+d.condition+"] no additional info");
            setDiagnosticStatus("diag.bib1."+d.condition,protocol_endpoint.getTargetName(),null);
          }
          break;

        case Records_type.multiplenonsurdiagnostics_CID:
            log.info("Multiple non surrogate diagnostics");
            break;

        default:
            log.info("Unknown choice for records response : "+r.which);
            break;
      }
    }
    else
    {
      log.debug("Records member of present response is null");
    }
    
    return result;
  }

  public void setFragmentCount(int i) {
    log.debug("Z3950SearchTask::setFragmentCount("+i+")");
    fragment_count = i;

    // AbstractIRResultSet now extends Observable so call these directly
    IREvent e = new IREvent(IREvent.FRAGMENT_COUNT_CHANGE, new Integer(i));
    log.debug("setChanged");
    setChanged();
    log.debug("notifyObservers");
    notifyObservers(e);
    log.debug("leave Z3950SearchTask::setFragmentCount");
  }

  public int getFragmentCount() {
    return fragment_count;
  } 

  /** The size of the result set (Estimated or known) */
  public int getRecordAvailableHWM()
  {
    return fragment_count;
  }

  /** Cancel any active operation, but leave all the searchTask's data intact */
  public void cancelTask() {
    log.debug("Z3950SearchTask::cancelTask()");
  }

  /** From IRResultSet interface */
  public void close() {
    log.debug("Z3950SearchTask::close()");
  }

  /** From AbstractIRResultSet base class */
  public void destroyTask()
  {
    super.destroyTask();
    log.debug("Z3950SearchTask::destroyTask()");
  }

  public AsynchronousEnumeration elements()
  {
    log.debug("Z3950SearchTask::elements()");
    int default_present_chunk_size = 10;
    // int default_present_chunk_size = Integer.parseInt(target_description.getPreference("default_present_chunk_size","10").toString());
    return new ReadAheadEnumeration(this,default_present_chunk_size);
  }

  public String toString()
  {
    return "Z3950SearchTask - "+getSetID();
  }

  // Hate this... will fix in next point release
  private ExplicitRecordFormatSpecification interpretSpec(RecordFormatSpecification in_spec) throws IRResultSetException
  {
    ExplicitRecordFormatSpecification retval = null;

    if ( in_spec instanceof ExplicitRecordFormatSpecification ) {

      ExplicitRecordFormatSpecification spec = (ExplicitRecordFormatSpecification) in_spec;
      retval = spec;
      boolean new_spec_needed = false;
  
      try {
        String new_format = strval(spec.getEncoding());
        String new_schema = strval(spec.getSchema());
        String new_esetname = strval(spec.getSetname());
  
        log.debug("InterpretSpec "+spec);
  
        if ( spec.getEncoding() instanceof IndirectFormatProperty )
        {
          log.debug("Convert format "+spec.getEncoding());
          new_format = (String) BeanUtils.getProperty(protocol_endpoint,new_format);
          new_spec_needed = true;
          log.debug("actual format will be "+new_format);
        }
  
        if ( spec.getSchema() instanceof IndirectFormatProperty )
        {
          new_schema = (String) BeanUtils.getProperty(protocol_endpoint,new_schema);
          new_spec_needed = true;
          log.debug("actual format will be "+new_schema);
        }
  
        if ( spec.getSetname() instanceof IndirectFormatProperty )
        {
          log.debug("Convert setname "+spec.getSchema());
          new_esetname = (String) BeanUtils.getProperty(protocol_endpoint,new_esetname);
          new_spec_needed = true;
          log.debug("actual format will be "+new_esetname);
        }
  
        if ( new_spec_needed )
        {
          retval = new ExplicitRecordFormatSpecification(new_format, new_schema, new_esetname);
          log.debug("Converted "+spec.toString()+" into "+retval);
        }
      }
      catch ( java.lang.IllegalAccessException iae ) {
        throw new IRResultSetException("Problem interpreting spec",iae);
      }
      catch ( java.lang.reflect.InvocationTargetException ite ) {
        throw new IRResultSetException("Problem interpreting spec",ite);
      }
      catch ( java.lang.NoSuchMethodException nspe ) {
        throw new IRResultSetException("Problem interpreting spec",nspe);
      }
    }
    else if ( in_spec instanceof ArchetypeRecordFormatSpecification ) {
      String actual_spec = (String) protocol_endpoint.getRecordArchetypes().get(in_spec.toString());

      if ( actual_spec == null )
        throw new IRResultSetException("Unable to locate format specification for archetype "+in_spec.toString());

      log.debug("Record Archetype "+in_spec+" converted to "+actual_spec);

      retval = new ExplicitRecordFormatSpecification(actual_spec);
    }
    else {
      throw new IRResultSetException("Unhandled RecordFormatSpecification of class "+in_spec.getClass().getName());
    }

    
    return retval;
  }
  
  private String strval(FormatProperty p) {
    if ( p != null )
      return p.toString();
  
    return null;
  }

  public IRResultSetInfo getResultSetInfo() {
    return new IRResultSetInfo(getResultSetName(),
                               "Z3950",
                               null,
                               getFragmentCount(),
                               getStatus(),
                               null);
  }

}
