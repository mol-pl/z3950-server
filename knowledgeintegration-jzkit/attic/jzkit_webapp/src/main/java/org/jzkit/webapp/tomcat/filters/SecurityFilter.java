package com.k_int.colws.tomcat.filters;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.k_int.svc.identity.service.*;

public class SecurityFilter implements Filter {

  private FilterConfig config = null;
  private com.k_int.svc.identity.service.IdentityService identity_service = null;

  public void doFilter(ServletRequest request,
                       ServletResponse response,
                       FilterChain chain)
            throws IOException, ServletException {

    String user_token = request.getParameter("user_token");
    System.err.println("Validating user token "+user_token);

    HttpServletRequest hReq = (HttpServletRequest) request;
    HttpServletResponse hRes = (HttpServletResponse) response;

    try {
      if ( ( user_token != null ) && ( identity_service.authenticate(user_token) != null ) ) {
        System.err.println("User token "+user_token+" Checks out");
      }
      else {
        System.err.println("Sending forbidden response");
        hRes.sendError(HttpServletResponse.SC_FORBIDDEN);
      }
    }
    catch ( com.k_int.svc.identity.service.IdentityServiceException ise ) {
      System.err.println("Identity Service Exception "+ise);
      ise.printStackTrace();
      hRes.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    chain.doFilter(request, response);
  }

  public void init(FilterConfig config) throws ServletException {
    this.config = config;

    System.err.println("Configuring auth filter");
    try {
      // 1. Check that the supplied username is not already in use
      WebApplicationContext ctx =
        WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
      identity_service = (com.k_int.svc.identity.service.IdentityService) ctx.getBean("IdentityService");
    }
    catch ( org.springframework.beans.factory.NoSuchBeanDefinitionException nsbde ) {
      // Misconfiguratio
      System.err.println("Unable to configure the FilterConfig. Perhaps your configuration needs updating");
    }
    finally {
    }
    System.err.println("Configuring auth filter - all done, got IdentityService");
  }
     
  /**
   * Destroy the filter, releasing resources.
   */
  public void destroy() {
    System.err.println("Destroy auth filter");
  }

}
