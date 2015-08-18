package com.k_int.colws.search;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.apache.commons.fileupload.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.context.ApplicationContext;

import org.jzkit.search.*;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.ResultSet.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

/**
 * @author Ian Ibbotson
 * @version
 */
public class ProcessSearchFormServlet extends HttpServlet {

  public static Log log = LogFactory.getLog(ProcessSearchFormServlet.class);

  static final int ERROR=-1;
  static final int AND=1;
  static final int OR=2;
  static final int NOT=3;
  static final int TERM=6;
  static final int EOL=9;
  static final int EOF=10;

  public ProcessSearchFormServlet() {
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

  /**
   * handle HTTP Post method.
   * @param request HTTP request
   * @param response HTTP response
   * @return void
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    log.debug("doPost");

    PrintWriter out = response.getWriter();
    String base_dir = request.getContextPath();

    String search_text = request.getParameter("searchText");

    String landscape = request.getParameter("landscape");
    if ( landscape == null ) 
      landscape = "Products";

    String schema = request.getParameter("schema");
    if ( schema == null ) 
      schema = "learningobject";

    String stylesheet = request.getParameter("stylesheet");
    if ( stylesheet == null ) 
      stylesheet = base_dir+"/public/xsl/sr.xsl";

    if ( search_text != null ) {
      Reader r = new StringReader(search_text);
      StreamTokenizer input = new StreamTokenizer(r);
      input.resetSyntax();
      input.eolIsSignificant(true);
      input.wordChars('a', 'z');
      input.wordChars('A', 'Z');
      input.wordChars('0', '9');
      input.wordChars('%', '%');
      input.wordChars('*', '*');
      input.wordChars('+', '+');
      input.wordChars('/', '/');
      input.wordChars('_', '_');
      input.wordChars('-', '-');
      input.wordChars('@', '@');
      input.wordChars('=', '=');
      input.wordChars('.', '.');
      input.ordinaryChar('(');
      input.ordinaryChar(')');
      input.quoteChar('"');
      input.whitespaceChars(' ',' ');
  
      StringWriter sw = new StringWriter();
  
      try {
        int token=0;
        int expect = 0; // 0=term, 1=Bool
        while ( expect != -1 ) {
          token = input.nextToken();
          switch (token) {
            case StreamTokenizer.TT_EOF:
              expect = -1;
              break;
            case StreamTokenizer.TT_EOL:
              expect = -1;
              break;
            case StreamTokenizer.TT_WORD:
            case '"':
              if ( expect == 1 ) {
                // See if we have a bool token, if not, add the bool, then this term, and expect a bool next
                if ( input.sval != null ) {
                  if ( input.sval.equalsIgnoreCase("and") ) {
                    expect = 0;
                    sw.write(" and ");
                  }
                  else if ( input.sval.equalsIgnoreCase("or") ) {
                    expect = 0;
                    sw.write(" or ");
                  }
                  else {
                    // Output and, followed by next token
                    if ( token == '"' )
                      sw.write("and \""+input.sval+"\"");
                    else
                      sw.write(" and "+input.sval);
                  }
                }
              }
              else if ( expect == 0 ) {
                // Add the token
                if ( token == '"' )
                  sw.write("\""+input.sval+"\"");
                else
                  sw.write(input.sval);
   
                expect = 1;
              }
              break;
            default:
              sw.write(input.sval);
              break;
          }
        }
  
        String qry = sw.toString();
  
        if ( ( request.getParameter("searchType") != null ) && 
             ( ( request.getParameter("searchType").equals("1") ) || ( request.getParameter("searchType").equals("0") ) ) ) {
          if ( ( qry != null ) && ( qry.length() > 0 ) ) {
            qry = qry + " and col.isPriced="+request.getParameter("searchType");
          }
        }
  
        if ( ( request.getParameter("TeachingSubject") != null ) && ( request.getParameter("TeachingSubject").length() > 0 ) ) {
          if ( ( qry != null ) && ( qry.length() > 0 ) ) {
            qry = qry + " and col.subject="+request.getParameter("TeachingSubject");
          }
        }
  
        if ( ( request.getParameter("Keystage") != null ) && ( request.getParameter("Keystage").length() > 0 ) ) {
          if ( ( qry != null ) && ( qry.length() > 0 ) ) {
            qry = qry + " and col.keystage="+request.getParameter("Keystage");
          }
        }
  
        if ( ( request.getParameter("SchoolYear") != null ) && ( request.getParameter("SchoolYear").length() > 0 ) ) {
          if ( ( qry != null ) && ( qry.length() > 0 ) ) {
            qry = qry + " and col.year="+request.getParameter("SchoolYear");
          }
        }
  
        if ( ( request.getParameter("SchoolYear") != null ) && ( request.getParameter("SchoolYear").length() > 0 ) ) {
          if ( ( qry != null ) && ( qry.length() > 0 ) ) {
            qry = qry + " and col.year="+request.getParameter("SchoolYear");
          }
        }
  
        if ( ( request.getParameter("AggregationLevel") != null ) && ( request.getParameter("AggregationLevel").length() > 0 ) ) {
          if ( ( qry != null ) && ( qry.length() > 0 ) ) {
            qry = qry + " and col.level="+request.getParameter("AggregationLevel");
          }
        }

        System.err.println("Rewritten query:" +qry);

        if ( ( qry == null ) || ( qry.trim().length() == 0 ) ) {
          response.sendRedirect(response.encodeRedirectURL(base_dir));
        }

        response.sendRedirect(response.encodeRedirectURL(base_dir+"/search?landscape="+landscape+"&operation=SearchRetrieve&query="+java.net.URLEncoder.encode(qry)+"&maximumRecords=10&recordSchema="+schema+"&stylesheet="+stylesheet));
      }
      catch(IOException e) {
        log.warn("Problem",e);
        response.sendRedirect(response.encodeRedirectURL(base_dir));
      }
    }
    else {
      log.error("No Search String!");
      response.sendRedirect(response.encodeRedirectURL(base_dir));
    }
  }

}
