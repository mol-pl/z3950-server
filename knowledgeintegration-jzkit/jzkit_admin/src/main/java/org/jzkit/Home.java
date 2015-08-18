/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jzkit;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;

import com.k_int.commons.util.*;

/**
 * REST Web Service
 *
 * @author ibbo
 */

@XmlRootElement
@ImplicitProduces("text/html;qs=5")
@XmlAccessorType(XmlAccessType.FIELD)
@Path("/")
@Singleton
@Component
public class Home implements ApplicationContextAware {

  private boolean init_completed = false;

  private ApplicationContext ctx;
  @Autowired
  private SessionFactory factory = null;
  private static Log log = LogFactory.getLog(Home.class);

  @XmlTransient
  @Context
  private UriInfo context;

  private String uri = "This is the default URI";

  public Home() {
    log.debug("New Editor Instance "+this.hashCode());
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
    log.debug("Setting app ctx");
  }

  public void setSessionFactory(SessionFactory factory)     { 
    this.factory = factory;         
  }

  public String getURI() {  
    return uri;
  }

  public void setURI(String uri) {
    this.uri = uri;
  }
 
  public synchronized boolean firstrun() {
    boolean show_setup_page = false;
    log.debug("firstrun - ctx="+ctx+", factory="+factory);
    if ( !init_completed ) {
      log.debug("Processing init code");
      Session sess = null;
      try {
        sess = factory.openSession();
        PropSetHDO propset_hdo = PropSetHDO.lookupOrCreate(sess,"com.k_int.identwebapp.system.globals");
        log.debug("Got system properties: "+propset_hdo);
        sess.clear();
      } catch (HibernateException he ) {
        log.error("[firstrun:1] Problem with resource action",he);
      } catch (Exception e ) {
        log.error("[firstrun:1]Problem with resource action",e);
      } finally {
        if (sess !=null) try { sess.close(); } catch (Exception e) {  }
      }
      
      log.debug("Init completed");
      init_completed = true;
      show_setup_page = true;
    }
    return show_setup_page;
  }


  /**
   * Retrieves representation of an instance of info.made4u.made4uws.workorder.Made4UWorkOrderResource
   * @param id resource URI parameter
   * @return an instance of java.lang.String
   */
  @GET
  @Produces({MediaType.TEXT_HTML,MediaType.APPLICATION_XHTML_XML})
  public Viewable get() {
    log.debug("Home action get");
    Viewable result = null;
    if ( !init_completed && firstrun() ) {
      result = new Viewable("setup",this);
    }
    else {
      result = new Viewable("setup",this);
    }

    log.debug("Returning result");
    return result;
  }
}
