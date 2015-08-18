/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jzkit.webapp.core;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.POST;

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

import com.sun.jersey.spi.resource.Singleton;

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
@Path("/login")
@Singleton
public class Login {

  private static Log log = LogFactory.getLog(Login.class);

  @XmlTransient
  @Context
  private UriInfo context;

  @Context
  HttpServletRequest r;

  public Login() {
    log.debug("New Editor Instance");
  }

  /**
   * Retrieves representation of an instance of info.made4u.made4uws.workorder.Made4UWorkOrderResource
   * @param id resource URI parameter
   * @return an instance of java.lang.String
   */
  @GET
  @Produces({MediaType.TEXT_HTML,MediaType.APPLICATION_XHTML_XML})
  public Viewable getXml() {
    if ( r.getUserPrincipal() != null ) {
      log.debug("User principle present, use home viewable");
      return new Viewable("home",this);
    }

    log.debug("User principle not present, show login page");
    return new Viewable("index",this);
  }
}
