/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jzkit.webapp.core;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import com.sun.jersey.spi.resource.Singleton;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.api.view.ImplicitProduces;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlTransient;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * REST Web Service
 *
 * @author ibbo
 */


@XmlRootElement
@ImplicitProduces("text/html;qs=5")
@XmlAccessorType(XmlAccessType.FIELD)
@Path("/logout")
public class Logout {

  // @Context SecurtyContext sc;
  private static Log log = LogFactory.getLog(Logout.class);

  @XmlTransient
  @Context
  private UriInfo context;

  @Context 
  HttpServletRequest r;

  public Logout() {
  }

  /**
   * Retrieves representation of an instance of info.made4u.made4uws.workorder.Made4UWorkOrderResource
   * @param id resource URI parameter
   * @return an instance of java.lang.String
   */
  @GET
  @Produces({MediaType.TEXT_HTML,MediaType.APPLICATION_XHTML_XML})
  public Response get() throws java.net.URISyntaxException {
    HttpSession http_session = r.getSession(false);
    if ( http_session != null ) {
      log.debug("Logout - invalidate session - Current id : "+http_session.getId());
      for ( java.util.Enumeration e =  r.getAttributeNames(); e.hasMoreElements(); ) {
        String attr=(String) e.nextElement();
        log.debug("attrs:"+attr+"="+r.getAttribute(attr));
      }
      log.debug("p pre inv="+r.getUserPrincipal());
      http_session.invalidate();
      log.debug("p after inv="+r.getUserPrincipal());
    }

    return Response.seeOther(new java.net.URI("/")).build(); 
  }
}
