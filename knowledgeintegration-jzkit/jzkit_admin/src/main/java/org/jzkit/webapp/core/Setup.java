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

import com.sun.jersey.spi.resource.Singleton;

import java.util.logging.Logger;


/**
 * REST Web Service
 *
 * @author ibbo
 */


@XmlRootElement
@ImplicitProduces("text/html;qs=5")
@XmlAccessorType(XmlAccessType.FIELD)
@Path("/setup")
@Singleton
public class Setup {

  // @Context SecurtyContext sc;
  private static final Logger logger = Logger.getLogger( Setup.class.getName() );

  @XmlTransient
  @Context
  private UriInfo context;

  private String uri = "This is the default URI";

  public Setup() {
  }

  public String getURI() {  
    return uri;
  }

  public void setURI(String uri) {
    this.uri = uri;
  }

  /**
   * Retrieves representation of an instance of info.made4u.made4uws.workorder.Made4UWorkOrderResource
   * @param id resource URI parameter
   * @return an instance of java.lang.String
   * Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
   */
  @GET
  @Produces({MediaType.TEXT_HTML,MediaType.APPLICATION_XHTML_XML})
  public Viewable getXml() {
    System.out.println("GetXML");
    Viewable result = null;
    if ( 1==1 ) {
      System.out.println("return settings...");
      result = new Viewable("settings", this);
    }
    else {
      System.out.println("return index...");
      result = new Viewable("index", this);
    }

    return result;
  }
}
