package org.jzkit.sru;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.apache.commons.fileupload.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.jzkit.search.provider.iface.SearchException;
import org.jzkit.search.*;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.ResultSet.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SRU : The SRU Interface Servlet that maps SRU request strings into the backend search service.
 * This servlet maps requests of the form servlet/<<search_landscape>>?operation=x&etc=etc&etc=etc. in essence, a user
 * accesses the search url and appends slash landscape (for the purposes of selecting backend collection(s) to search.
 * Standard SRW parameters apply, as described at @see http://www.loc.gov/standards/sru/sru-spec.html. The servlet then arranges
 * the appropriate SRU response based on the request verb.
 *
 * @author Ian Ibbotson
 * @version $Id: APDUListener.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
 */
public abstract class SRUBaseServlet extends HttpServlet {

  public static Log log = LogFactory.getLog(SRUBaseServlet.class);

  private static RecordFormatSpecification request_spec = new ArchetypeRecordFormatSpecification("F");
  private static ExplicitRecordFormatSpecification full_format_xml = new ExplicitRecordFormatSpecification("xml:DC:F");
  private static ExplicitRecordFormatSpecification full_html = new ExplicitRecordFormatSpecification("text:html:F");
  private static ExplicitRecordFormatSpecification brief_html = new ExplicitRecordFormatSpecification("text:html:B");

  /**
   * Public constructor. No special construction action.
   */
  public SRUBaseServlet() {
    log.debug("SRUBaseServlet::SRUBaseServlet");
  }

  /**
   * Servlet init method, calls standard servlet init.
   * @return void
   */
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    log.debug("SRU Servlet init");
  }


  /**
   * handle HTTP Get method.
   * @param request HTTP request
   * @param response HTTP response
   * @return void
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    log.debug("doGet");
    doPost(request, response);
  }

  public abstract String resolveLandscape(HttpServletRequest request);

  /**
   * handle HTTP Post method.
   * @param request HTTP request
   * @param response HTTP response
   * @return void
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    log.debug("doPost");
    try {
      // 1. Check that the supplied username is not already in use
      WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

      log.debug("SRU::doPost");
      String operation = request.getParameter("operation");
      log.debug("operation="+operation);

      // 1. Establish the landscape name... this is everything after /sru/<<LandscapeSpecifierHere>>?Params
      String landscape_identifier = resolveLandscape(request);
      log.debug("landscape_identifier="+landscape_identifier);

      // 2. Set response type to text/xml
      response.setContentType("text/xml; charset=UTF-8");               

      // 3. If the request contains a stylesheet definition
      String stylesheet = request.getParameter("stylesheet");

      PrintWriter writer = response.getWriter();

      if ( ( stylesheet != null ) && ( stylesheet.length() > 0 ) ) {
        writer.println("<?xml-stylesheet type=\"text/xsl\" href=\""+stylesheet+"\"?>");
      }

      // 4. Operation specific switch here
      if ( operation != null ) {
        if ( operation.toLowerCase().startsWith("info:srw/operation/1/" ) ) {
          if ( operation.toLowerCase().equals("info:srw/operation/1/update" ) ) {
            log.debug("Record Update");
            processRecordUpdate(ctx, request,response, writer);
          }
        }
        else if ( operation.toLowerCase().equalsIgnoreCase("searchRetrieve" ) ) {
          processSearchRetrieve(ctx, landscape_identifier, request, response, writer);
        }
      }
      else {
        processExplain(ctx, landscape_identifier, request,response, writer);
      }
    }
    finally {
      log.debug("SRU::doPost complete");
    }
  }

  /**
   * Return an SRW Explain response.
   *
   * @param ctx Spring application context file
   * @param landscape_identifier the landscape identifier used to select collections
   * @param request HTTP request
   * @param response HTTP response
   * @return void
   */
  private void processExplain(WebApplicationContext ctx, 
                              String landscape_identifier, 
                              HttpServletRequest request, 
                              HttpServletResponse response, 
                              PrintWriter writer) throws ServletException, IOException {
    log.debug("processExplain");
    writer.println("<srw:explainResponse xmlns:srw=\"http://www.loc.gov/zing/srw/\" xmlns:zr=\"http://explain.z3950.org/dtd/2.0/\">");
    writer.println("  <srw:version>1.1</srw:version>");
    writer.println("  <srw:record>");
    writer.println("    <srw:recordPacking>XML</srw:recordPacking>");
    writer.println("    <srw:recordSchema>http://explain.z3950.org/dtd/2.0/</srw:recordSchema>");
    writer.println("    <srw:recordData>");
    writer.println("      <zr:explain>");
    /*
    writer.println("        <zr:serverInfo wsdl=\"http://myserver.com/db\" protocol=\"SRU\" version=\"1.1\">");
    writer.println("          <host></host>");
    writer.println("          <port>80</port>");
    writer.println("          <database>sru</database>");
    writer.println("        </zr:serverInfo>");
    */
    writer.println("        <zr:databaseInfo>");
    writer.println("           <title lang=\"en\" primary=\"true\">SRU Test Database</title>");
    writer.println("           <description lang=\"en\" primary=\"true\"> My server SRU Test Database </description>");
    writer.println("        </zr:databaseInfo>");
    /*
    dynamically generated, guess this isn't appropriate
    writer.println("        <zr:metaInfo>");
    writer.println("           <dateModified>27-11-2003</dateModified>");
    writer.println("        </zr:metaInfo>");
    */
    writer.println("      </zr:explain>");
    writer.println("    </srw:recordData>");
    writer.println("  </srw:record>");
    writer.println("</srw:explainResponse>");
  }

  /**
   * handler for record upload.
   * @param ctx Spring application context file
   * @param request HTTP request
   * @param response HTTP response
   * @return void
   */
  private void processRecordUpdate(WebApplicationContext ctx, HttpServletRequest request, HttpServletResponse response, PrintWriter writer) throws ServletException, IOException {
    log.debug("processExplain");
    String identifier = request.getParameter("identifier");
    String record_source = request.getParameter("record_source");
    String publisher_identity = request.getParameter("publisher_identity");
    String publisher_credentials = request.getParameter("publisher_credentials");
    String target_collection = request.getParameter("target_collection");

    try {
      boolean isMultipart = FileUpload.isMultipartContent(request);
      if ( isMultipart ) {
        DiskFileUpload upload = new DiskFileUpload();
        List /* FileItem */ items = upload.parseRequest(request);

        Iterator iter = items.iterator();
        while (iter.hasNext()) {
          FileItem item = (FileItem) iter.next();
          log.debug("field name:"+item.getFieldName());
          log.debug("file name:"+item.getName());
          log.debug("In mem:"+item.isInMemory());
          if (item.isFormField()) {
            String record = item.getString();
            // publish(identifier,record_source,publisher_identity,publisher_credentials,target_collection,record);
          } else {
            // processUploadedFile(item);
            String record = item.getString();
            // publish(identifier,record_source,publisher_identity,publisher_credentials,target_collection,record);
          }
        }
      }
      else {
        log.debug("Not multipart");
      }
    } catch ( org.apache.commons.fileupload.FileUploadException fue ) {
      fue.printStackTrace();
    }
  }

  /**
   * Process search retrieve response.
   * @param ctx Spring application context file
   * @param landscape_identifier the landscape identifier used to select collections
   * @param request HTTP request
   * @param response HTTP response
   * @return void
   */
  private void processSearchRetrieve(WebApplicationContext ctx, String landscape_identifier, HttpServletRequest request, HttpServletResponse response, PrintWriter writer) throws ServletException, IOException {
    log.debug("processSearchRetrieve");
    String version = request.getParameter("version");
    String query = request.getParameter("query");
    String start_record = request.getParameter("startRecord");
    String maximum_records = request.getParameter("maximumRecords");
    String record_packing = request.getParameter("recordPacking");
    String record_schema = request.getParameter("recordSchema");
    String record_xpath = request.getParameter("recordXPath");
    String result_set_ttl = request.getParameter("resultSetTTL");
    String sort_keys = request.getParameter("sortKeys");
    String extra_request_data = request.getParameter("extraRequestData");
    String operation = request.getParameter("operation");

    int num_hits_per_page = 10;
    int first_record = 1;

    writer.println("<srw:searchRetrieveResponse xmlns:srw=\"http://www.loc.gov/zing/srw/\" xmlns:jzkit=\"http://www.k-int.com/jzkit/\">");
    writer.println("  <srw:version>1.1</srw:version>");

    if ( maximum_records != null )
      num_hits_per_page = Integer.parseInt(maximum_records);

    if ( start_record != null ) 
      first_record = Integer.parseInt(start_record);

    if ( record_schema == null ) 
      record_schema = "dc";


    ExplicitRecordFormatSpecification display_spec = new ExplicitRecordFormatSpecification("xml:"+record_schema+":F");

    try {
      log.debug("Obtain handle to rmi client factory");
      org.jzkit.service.RMIClientFactory rmi_cf = (org.jzkit.service.RMIClientFactory) ctx.getBean("SearchService");

      log.debug("Obtain handle to search session factory");
      SearchSessionFactory search_session_factory = (SearchSessionFactory)rmi_cf.getInstance();

      log.debug("Prepare props hashmap. Processing query "+query);

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

      log.debug("Calling search_session_factory.getResultsPageFor");
      StatelessSearchResultsPageDTO result = search_session_factory.getResultsPageFor(null,
                                                                                      "cql",
                                                                                      query,
                                                                                      landscape_identifier,
                                                                                      first_record,
                                                                                      num_hits_per_page,
                                                                                      request_spec,
                                                                                      display_spec,
                                                                                      additional_properties);
      log.debug("Call to getResultsPageFor completed : "+result);

      writer.println("  <srw:numberOfRecords>"+result.getRecordCount()+"</srw:numberOfRecords>");

      int last_record = 0;

      if ( ( result.records != null ) && ( result.records.length > 0 ) ) {
        writer.println("  <srw:records>");
        for ( int i=0; i<result.records.length;i++ ) {
          writer.println("  <srw:record>"+result.records[i]+"</srw:record>");
        }
        writer.println("  </srw:records>");

        last_record = first_record+result.records.length-1;
      }


      if ( ( last_record < result.getRecordCount() ) && ( last_record > 0 ) )
        writer.println("  <srw:nextRecordPosition>"+(last_record+1)+"</srw:nextRecordPosition>");

      writer.println("  <srw:echoedSearchRetrieveRequest>");
      writer.println("    <srw:version>"+version+"</srw:version>");
      writer.println("    <srw:query>"+query.replaceAll("\\&","&amp;").replaceAll("<","&lt;")+"</srw:query>");
      writer.println("    <srw:startRecord>"+first_record+"</srw:startRecord>");
      writer.println("    <srw:recordSchema>"+record_schema+"</srw:recordSchema>");
      writer.println("  </srw:echoedSearchRetrieveRequest>");

      writer.println("  <srw:extraResponseData>");
      writer.println("    <jzkit:lastRecord>"+last_record+"</jzkit:lastRecord>");
      writer.println("  </srw:extraResponseData>");

    }
    catch ( org.jzkit.search.util.ResultSet.IRResultSetException rse ) {
      rse.printStackTrace();
      diagnostic(writer,rse);
    }
    catch ( java.rmi.RemoteException re ) {
      re.printStackTrace();
      diagnostic(writer,re);
    }
    catch ( java.rmi.NotBoundException nbe ) {
      nbe.printStackTrace();
      diagnostic(writer,nbe);
    }
    catch ( SearchException se ) {
      se.printStackTrace();
      diagnostic(writer,se);
    }
    catch ( org.jzkit.search.util.QueryModel.InvalidQueryException iqe ) {
      iqe.printStackTrace();
      diagnostic(writer,iqe);
    }
    catch ( java.net.MalformedURLException murle ) {
      murle.printStackTrace();
      diagnostic(writer,murle);
    }
    catch ( Exception e ) {
      e.printStackTrace();
      diagnostic(writer,e);
    }

    writer.println("</srw:searchRetrieveResponse>");

  }

  private void diagnostic(PrintWriter writer, String error_key, String message) {
    writer.println("<diagnostics>");
    writer.println("<diagnostic xmlns=\"http://www.loc.gov/zing/srw/diagnostic/\">");
    writer.println("<uri>"+error_key+"</uri>");
    // writer.println("<details>10</details>");
    writer.println("<message>");
    writer.println(message);
    writer.println("</message>");
    writer.println("</diagnostic>");
    writer.println("</diagnostics>");
  }

  private void diagnostic(PrintWriter writer, Throwable t) {
    writer.println("<diagnostics>");
    writer.println("<diagnostic xmlns=\"http://www.loc.gov/zing/srw/diagnostic/\">");
    writer.println("<uri>info:srw/diagnostic/1/1</uri>");
    // writer.println("<details>10</details>");
    writer.println("<message><![CDATA[");
    t.printStackTrace(writer);
    writer.println("]]></message>");
    writer.println("</diagnostic>");
    writer.println("</diagnostics>");
  }


  // private void publish(String identifier,
  //                      String record_source,
  //                      String publisher_identity,
  //                      String publisher_credentials,
  //                      String target_collection,
  //                      String record) {
}
