package org.jzkit.sru;

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
public class SRU extends SRUBaseServlet {

  public SRU() {
    super();
  }

  public String resolveLandscape(HttpServletRequest request) {
    // 1. Establish the landscape name... this is everything after /sru/<<LandscapeSpecifierHere>>?Params
    String landscape_identifier = request.getPathInfo();
    log.debug("Processing landscape for path info: "+landscape_identifier);

    if ( ( landscape_identifier != null ) && ( landscape_identifier.startsWith("/") ) )  {
      landscape_identifier = landscape_identifier.substring(1,landscape_identifier.length());
    }
    else {
      landscape_identifier = "default";
    }

    return landscape_identifier;
  }
}
