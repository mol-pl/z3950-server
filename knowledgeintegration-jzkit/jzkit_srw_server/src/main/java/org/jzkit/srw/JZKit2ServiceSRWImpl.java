package org.jzkit.srw;

import org.jzkit.search.provider.zing.interfaces.*;
import org.jzkit.search.provider.zing.*;
  
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.apache.commons.fileupload.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
  
import org.jzkit.search.*;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.provider.iface.SearchException;
  
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
  
import org.apache.axis.transport.http.HTTPConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;

public class JZKit2ServiceSRWImpl implements SRWPort {
  
  public static Log log = LogFactory.getLog(JZKit2ServiceSRWImpl.class);
  
  private static RecordFormatSpecification request_spec = new ArchetypeRecordFormatSpecification("F");
  private static ExplicitRecordFormatSpecification full_format_xml = new ExplicitRecordFormatSpecification("xml:DC:F");
  private static ExplicitRecordFormatSpecification full_html = new ExplicitRecordFormatSpecification("text:html:F");
  private static ExplicitRecordFormatSpecification brief_html = new ExplicitRecordFormatSpecification("text:html:B");
  private static int AUTH_MODE_NONE = 0;
  private static int AUTH_MODE_OPTIONAL = 1;
  private static int AUTH_MODE_REQUIRED = 2;
  
  public JZKit2ServiceSRWImpl() {
    log.debug("JZKit2ServiceSRWImpl::JZKit2ServiceSRWImpl");
  }
  
  public SearchRetrieveResponseType searchRetrieveOperation(SearchRetrieveRequestType body) throws java.rmi.RemoteException {
    SearchRetrieveResponseType result = null;

    int error_code = 0;
    int result_num_records = 0;
    String result_result_set_id = null;
    org.apache.axis.types.PositiveInteger result_result_set_idle_time = new org.apache.axis.types.PositiveInteger("180");
    org.apache.axis.types.PositiveInteger result_next_result_set_position = null;
    org.jzkit.search.provider.zing.DiagnosticsType diagnostics = null;
    DocumentBuilderFactory dbf=null;
    DocumentBuilder db = null;
    String auth_token = null;
    int auth_mode = 1;
    boolean auth_ok = true;

    org.jzkit.search.provider.zing.RecordType[] records = null;

    try {
      try {
        log.debug("processing searchRetrieveOperation");
    
        org.apache.axis.MessageContext message_context = org.apache.axis.MessageContext.getCurrentContext();
        ServletContext sc = ((HttpServlet)message_context.getProperty(HTTPConstants.MC_HTTP_SERVLET)).getServletContext();
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
  
        org.jzkit.service.RMIClientFactory rmi_cf = (org.jzkit.service.RMIClientFactory) ctx.getBean("SearchService");
        SearchSessionFactory search_session_factory = (SearchSessionFactory)rmi_cf.getInstance();
  
        for ( java.util.Iterator iter = message_context.getPropertyNames(); iter.hasNext(); ) {
          System.err.println(iter.next());
        }
  
        String auth_type = (String) message_context.getProperty("org.jzkit.soap.wsauthtype");
        if ( auth_type == null ) {
          auth_mode = AUTH_MODE_REQUIRED;
          auth_ok = false;
        }
        else if ( auth_type.equalsIgnoreCase("REQ") ) {
          // Required
          auth_mode = AUTH_MODE_REQUIRED;
          // Set auth ok to false, as it must be positively confirmed by auth subsystem
          auth_ok = false;
        }
        else if ( auth_type.equalsIgnoreCase("OPT") ) {
          // Optional
          auth_mode = AUTH_MODE_OPTIONAL;
        }
        else if ( auth_type.equalsIgnoreCase("NONE") ) {
          // None
          auth_mode = AUTH_MODE_NONE;
        }
  
        // Look to see if there is an authenticationToken in body.getExtraRequestData()
        org.jzkit.search.provider.zing.ExtraDataType erd = body.getExtraRequestData();
  
        if ( erd != null ) {
          org.apache.axis.message.MessageElement[] ed_message_element_arr = erd.get_any();
          for ( int erd_i = 0; erd_i < ed_message_element_arr.length; erd_i++ ) {
            org.apache.axis.message.MessageElement elem = ed_message_element_arr[erd_i];
            log.debug("processing extra request data "+elem);
            org.w3c.dom.NodeList nl = elem.getElementsByTagNameNS("auth","authenticationToken");
            if ( nl.getLength() == 1 ) {
              // Handle auth data
              org.w3c.dom.Node n = nl.item(0);
              log.debug("processing extra request data "+n);
              if ( auth_mode > 0 ) {
                auth_token = n.getFirstChild().getNodeValue();
                WSAuthService ws_auth_service = (WSAuthService) ctx.getBean("WSAuthService"); 
                auth_ok = ws_auth_service.authenticateToken(auth_token);
              }
            }
          }
        }
        else {
          log.warn("Message contained no extra request data, and hence no authentication information");
        }
  
        if ( ! auth_ok ) {
          // Auth failed, reject and send error
          log.error("Auth failed");
          error_code = 1;
          diagnostics = new org.jzkit.search.provider.zing.DiagnosticsType(
            new org.jzkit.search.provider.zing.diagnostic.DiagnosticType[] {
              new org.jzkit.search.provider.zing.diagnostic.DiagnosticType(new java.net.URI("info:srw/diagnostic/1/3"),
                                                                           "Authenticatin Failure",
                                                                           "Authentication Failed for User Token "+auth_token) } );
        }
        else {
          log.debug("Auth OK");
          String landscape_identifier = (String) message_context.getProperty("org.jzkit.soap.landscape");
      
          HttpServletRequest request = (HttpServletRequest)(message_context.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST));
      
          if ( ( landscape_identifier == null ) || ( landscape_identifier.length() == 0 ) )
            landscape_identifier = "default";
      
          log.debug("return SearchRetrieveResponseType - landscape = "+landscape_identifier);
      
          String query = body.getQuery();
          int num_hits_per_page = 10;
          int first_record = 1;
    
          if ( body.getMaximumRecords() != null )
            num_hits_per_page = body.getMaximumRecords().intValue();
    
          if ( body.getStartRecord() != null )
            first_record = body.getStartRecord().intValue();
    
          String record_schema = body.getRecordSchema();
    
          if ( record_schema == null ) 
            record_schema = "dc";
    
          ExplicitRecordFormatSpecification display_spec = new ExplicitRecordFormatSpecification("xml:"+record_schema+":F");
    
          Map additional_properties = new HashMap();
          additional_properties.put("base_dir",request.getContextPath());
          try {
            if ( ( query != null ) && ( query.length() > 0 ) ) {
              additional_properties.put("query",java.net.URLEncoder.encode(query,"UTF-8"));
            }
            else {
              log.warn("Invalid query");
            }
          }
          catch ( java.io.UnsupportedEncodingException uee ) {
            uee.printStackTrace();
          }
          additional_properties.put("query_type","cql");
          additional_properties.put("landscape",landscape_identifier);
    
          StatelessSearchResultsPageDTO ssr = search_session_factory.getResultsPageFor(null,
                                                                                       "cql",
                                                                                       query,
                                                                                       landscape_identifier,
                                                                                       first_record,
                                                                                       num_hits_per_page,
                                                                                       request_spec,
                                                                                       display_spec,
                                                                                       additional_properties);
      
          int next_result_set_position = 1;
    
          if ( ssr.getRecords() != null ) {
            next_result_set_position = first_record + ssr.getRecords().length;
    
            log.debug("Allocating new records array of size "+ssr.getRecords().length);
            records = new org.jzkit.search.provider.zing.RecordType[ssr.getRecords().length];
    
            if(body.getRecordPacking().equalsIgnoreCase("xml")) {
              dbf=DocumentBuilderFactory.newInstance();
              dbf.setNamespaceAware(true);
              try {
                db=dbf.newDocumentBuilder();
              }
              catch(ParserConfigurationException e) {
                log.error(e, e);
              }
            }
    
            for ( int i = 0; i<ssr.getRecords().length; i++ ) {
              log.debug("Adding record "+i);
    
              org.jzkit.search.provider.zing.StringOrXmlFragment the_record = null;
    
              if ( ( body.getRecordPacking() != null ) && ( body.getRecordPacking().equalsIgnoreCase("xml") ) ) {
                // XML Packing
                try {
                  org.w3c.dom.Document dom=db.parse(new InputSource(new StringReader(ssr.getRecords()[i].toString())));
                  org.w3c.dom.Element elem=dom.getDocumentElement();
                  the_record = new org.jzkit.search.provider.zing.StringOrXmlFragment(
                                 new org.apache.axis.message.MessageElement[] {
                                   new org.apache.axis.message.MessageElement (elem) } );
                }
                catch ( org.xml.sax.SAXException saxe ) {
                  saxe.printStackTrace();
                }
                catch ( java.io.IOException ioe ) {
                  ioe.printStackTrace();
                }
              }
              else {
                // String Packing
                the_record = new org.jzkit.search.provider.zing.StringOrXmlFragment(
                               new org.apache.axis.message.MessageElement[] {
                                 new org.apache.axis.message.MessageElement (
                                   new org.apache.axis.message.Text(ssr.getRecords()[i].toString())) } );
              }
    
              records[i] = new org.jzkit.search.provider.zing.RecordType(record_schema, 
                             body.getRecordPacking(),
                             the_record,
                             new java.math.BigInteger(""+(first_record+i)),
                             null);
            }
          }
    
          result_num_records = ssr.getRecordCount();
          result_result_set_id = ssr.result_set_id;
          result_next_result_set_position = new org.apache.axis.types.PositiveInteger(""+next_result_set_position);
  
          log.debug("SRW search complete");
        }
        log.debug("Completed search processing");
      }
      catch ( java.rmi.NotBoundException nbe ) {
        nbe.printStackTrace();
        error_code = 1;
        diagnostics = new org.jzkit.search.provider.zing.DiagnosticsType(
          new org.jzkit.search.provider.zing.diagnostic.DiagnosticType[] {
            new org.jzkit.search.provider.zing.diagnostic.DiagnosticType(new java.net.URI("info:srw/diagnostic/1/1"),
                                                                         nbe.toString(),
                                                                         "Problem contacting backend search service")
        });
      }
      catch ( SearchException se ) {
        error_code = 1;
        se.printStackTrace();
        diagnostics = new org.jzkit.search.provider.zing.DiagnosticsType(
          new org.jzkit.search.provider.zing.diagnostic.DiagnosticType[] {
            new org.jzkit.search.provider.zing.diagnostic.DiagnosticType(new java.net.URI("info:srw/diagnostic/1/1"),
                                                                         se.toString(),
                                                                         "Search exception")
        });
  
      }
      catch ( java.net.MalformedURLException murele ) {
        error_code = 1;
        murele.printStackTrace();
        diagnostics = new org.jzkit.search.provider.zing.DiagnosticsType(
          new org.jzkit.search.provider.zing.diagnostic.DiagnosticType[] {
            new org.jzkit.search.provider.zing.diagnostic.DiagnosticType(new java.net.URI("info:srw/diagnostic/1/1"),
                                                                         murele.toString(),
                                                                         "Malformed URL")
        });
  
      }
      catch ( org.jzkit.search.util.ResultSet.IRResultSetException ire ) {
        error_code = 1;
        ire.printStackTrace();
        diagnostics = new org.jzkit.search.provider.zing.DiagnosticsType(
          new org.jzkit.search.provider.zing.diagnostic.DiagnosticType[] {
            new org.jzkit.search.provider.zing.diagnostic.DiagnosticType(new java.net.URI("info:srw/diagnostic/1/1"),
                                                                         ire.toString(),
                                                                         "IR Result Set Exception")
        });
  
      }
      catch ( org.jzkit.search.util.QueryModel.InvalidQueryException iqe ) {
        error_code = 1;
        iqe.printStackTrace();
        diagnostics = new org.jzkit.search.provider.zing.DiagnosticsType(
          new org.jzkit.search.provider.zing.diagnostic.DiagnosticType[] {
            new org.jzkit.search.provider.zing.diagnostic.DiagnosticType(new java.net.URI("info:srw/diagnostic/1/1"),
                                                                         iqe.toString(),
                                                                         "Invalid Query Exception")
        });
      }
      catch ( Exception e ) {
        error_code = 1;
        e.printStackTrace();
        diagnostics = new org.jzkit.search.provider.zing.DiagnosticsType(
          new org.jzkit.search.provider.zing.diagnostic.DiagnosticType[] {
            new org.jzkit.search.provider.zing.diagnostic.DiagnosticType(new java.net.URI("info:srw/diagnostic/1/1"),
                                                                         e.toString(),
                                                                         "unhandled exception in code")
        });
      }
      finally {
        log.debug("All done");
      }
    }
    catch ( java.net.URISyntaxException urise ) {
      log.error(urise);
    }

    RecordsType recs_type = null;
    if ( records != null ) 
      recs_type = new RecordsType(records);

    result = new SearchRetrieveResponseType("1.1",
                                            new java.math.BigInteger(""+result_num_records),
                                            result_result_set_id,
                                            result_result_set_idle_time,
                                            recs_type,
                                            result_next_result_set_position,
                                            null,
                                            diagnostics,
                                            null);


    return result;
  }

  public ScanResponseType scanOperation(ScanRequestType body) throws java.rmi.RemoteException {
    return new ScanResponseType( "1.1",
           new org.jzkit.search.provider.zing.TermsType(), // org.jzkit.search.provider.zing.TermType[] terms,
           null, // org.jzkit.search.provider.zing.EchoedScanRequestType echoedScanRequest,
           null, // org.jzkit.search.provider.zing.diagnostic.DiagnosticType[] diagnostics,
           null);// org.jzkit.search.provider.zing.ExtraDataType extraResponseData);
  }

  public SearchRetrieveResponseType method(SearchRetrieveRequestType request) throws java.rmi.RemoteException {
    return searchRetrieveOperation(request);
  }

  public ScanResponseType method(ScanRequestType request) throws java.rmi.RemoteException {
    return scanOperation(request);
  }

}
