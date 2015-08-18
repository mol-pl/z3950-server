package com.k_int.colws.tomcat.filters;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/*
 * See : http://java.sun.com/products/servlet/Filters.html
 */
public class XSLTFilter implements Filter {

  private FilterConfig filterConfig = null;

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
   throws IOException, ServletException {

    // type is a parameter specifying the required output content type
    String type = request.getParameter("xslFltType");
    String styleSheet = request.getParameter("xslFltFile");
    String contentType;

    if (type == null || type.equals("")) {
       contentType = "text/html";
    } else {
      if (type.equals("xml")) {
        contentType = "text/plain";
      } else {
        contentType = "text/html";
      }
    }

    if ( ( type != null ) && ( styleSheet != null ) ) {
      System.err.println("Applying XSL Filter "+styleSheet+", type="+type);
      response.setContentType(contentType);

      String stylePath = filterConfig.getServletContext().getRealPath(styleSheet);
      System.err.println("Real path: "+stylePath);
      Source styleSource = new StreamSource(stylePath);
      PrintWriter out = response.getWriter();
      CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse)response);     
      chain.doFilter(request, wrapper);
      StringReader sr = new StringReader(wrapper.toString());
      Source xmlSource = new StreamSource((Reader)sr);

      try {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(styleSource);
        CharArrayWriter caw = new CharArrayWriter();
        StreamResult result  = new StreamResult(caw);
        transformer.transform(xmlSource, result);
        response.setContentLength(caw.toString().length());

        out.write(caw.toString());
      } catch(Exception ex) {
        out.println(ex.toString());
        out.write(wrapper.toString());
      }
    }
    else {
      System.err.println("No XSL Processing");
      chain.doFilter(request, response);
    }
  }

  public void init(FilterConfig filterConfig) {
   this.filterConfig = filterConfig;
  }

  public void destroy(){
   this.filterConfig = null;   
  }
}


