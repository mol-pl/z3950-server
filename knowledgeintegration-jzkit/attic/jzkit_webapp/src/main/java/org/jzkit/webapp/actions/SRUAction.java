package org.jzkit.webapp.actions;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.tiles.actions.TilesAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.jzkit.search.*;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.provider.iface.SearchException;

/**
 * Implementation of <strong>Action</strong> that starts a search.
 */

public final class SRUAction extends Action {

  private Log log = LogFactory.getLog(SRUAction.class);

  private static RecordFormatSpecification request_spec = new ArchetypeRecordFormatSpecification("F");
  private static ExplicitRecordFormatSpecification full_format_xml = new ExplicitRecordFormatSpecification("xml:DC:F");
  private static ExplicitRecordFormatSpecification full_html = new ExplicitRecordFormatSpecification("text:html:F");
  private static ExplicitRecordFormatSpecification brief_html = new ExplicitRecordFormatSpecification("text:html:B");

  public ActionForward execute(ActionMapping mapping,
                               ActionForm form,
                               HttpServletRequest request,
                               HttpServletResponse response) {


    String result = "error";

    try {
      WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServlet().getServletContext());

      log.debug("SRUAction");

      String param = mapping.getParameter();
      String[] params = param.split(":");

      String operation = request.getParameter("operation");
      log.debug("operation="+operation);

      String target_type = params[0];
      String target_identifier = params[1];
      log.debug("target_identifier="+target_identifier);

      response.setContentType("text/xml; charset=UTF-8");

      String stylesheet = request.getParameter("stylesheet");

      // if ( ( stylesheet != null ) && ( stylesheet.length() > 0 ) ) {
      //   writer.println("<?xml-stylesheet type=\"text/xsl\" href=\""+stylesheet+"\"?>");
      // }

      if ( operation != null ) {
        if ( operation.toLowerCase().startsWith("info:srw/operation/1/" ) ) {
          if ( operation.toLowerCase().equals("info:srw/operation/1/update" ) ) {
            result="error";
          }
        }
        else if ( operation.toLowerCase().equalsIgnoreCase("searchRetrieve" ) ) {
          processSearchRetrieve(ctx, target_identifier, request, response);
          result="searchRetrieve";
        }
      }
      else {
        processExplain(ctx, target_identifier, request,response);
        result="explain";
      }
    }
    finally {
      log.debug("SRUAction complete");
    }

    return (mapping.findForward(result));
  }

  private void processExplain(WebApplicationContext ctx, 
                              String landscape_identifier, 
                              HttpServletRequest request, 
                              HttpServletResponse response) {
  }

  /**
   * Process search retrieve response.
   * @param ctx Spring application context file
   * @param landscape_identifier the landscape identifier used to select collections
   * @param request HTTP request
   * @param response HTTP response
   * @return void
   */
  private void processSearchRetrieve(WebApplicationContext ctx, 
                                     String landscape_identifier, 
                                     HttpServletRequest request, 
                                     HttpServletResponse response) {
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

      request.setAttribute("SearchResultDTO",result);

      log.debug("Call to getResultsPageFor completed : "+result);
    }
    catch ( org.jzkit.search.util.ResultSet.IRResultSetException rse ) {
      rse.printStackTrace();
    }
    catch ( java.rmi.RemoteException re ) {
      re.printStackTrace();
    }
    catch ( java.rmi.NotBoundException nbe ) {
      nbe.printStackTrace();
    }
    catch ( SearchException se ) {
      se.printStackTrace();
    }
    catch ( org.jzkit.search.util.QueryModel.InvalidQueryException iqe ) {
      iqe.printStackTrace();
    }
    catch ( java.net.MalformedURLException murle ) {
      murle.printStackTrace();
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }
  }
}

