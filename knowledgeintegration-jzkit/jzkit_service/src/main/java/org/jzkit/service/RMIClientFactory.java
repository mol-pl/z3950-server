package org.jzkit.service;

import java.util.logging.*;
import java.util.*;
import java.io.InputStream;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.*;

import org.jzkit.configuration.api.Configuration;
import org.jzkit.configuration.provider.db.DbConfigurationProvider;
import org.jzkit.search.util.RecordConversion.FragmentTransformerService;
import org.jzkit.search.*;

import java.rmi.*;
import java.rmi.registry.*;

import trmi.*;

/**
 * Use RMI to obtain an instance of a remote object using the supplied service url.
 */
public class RMIClientFactory implements ApplicationContextAware {

  private static Logger log = Logger.getLogger(RMIService.class.getName());

  private String service_url=null;
  private ApplicationContext ctx;

  public RMIClientFactory(ApplicationContext ctx, String service_url) {
    this.ctx=ctx;
    this.service_url=service_url;
  }

  public RMIClientFactory() {
    System.err.println("New RMIClientFactory...");
  }

  public void setServiceURL(String service_url) {
    this.service_url = service_url;
  }

  public String getServiceURL() {
    return service_url;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public Object getInstance() throws NotBoundException,RemoteException,java.net.MalformedURLException {
    System.err.println("newInstance of service interface...");
    return trmi.Naming.lookup(service_url);
  }
}
