package org.jzkit.service;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 */
public class RMIService implements ApplicationContextAware, org.springframework.beans.factory.InitializingBean {

  private static Log log = LogFactory.getLog(RMIService.class);

  private String service_url=null;
  private String service_interface=null;
  private int service_port;
  private ApplicationContext ctx;

  public RMIService(ApplicationContext ctx,
                    String service_url) {
    this.ctx=ctx;
    this.service_url=service_url;
  }

  public RMIService() {
  }

  public void setServiceURL(String service_url) {
    this.service_url = service_url;
  }

  public String getServiceURL() {
    return service_url;
  }

  public void setServicePort(int service_port) {
    this.service_port = service_port;
  }

  public int getServicePort() {
    return service_port;
  }

  public void setServiceInterface(String service_interface) {
    this.service_interface = service_interface;
  }

  public String getServiceInterface() {
    return service_interface;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  /**
   * Locate or create the RMI registry for this exporter.
   * @param registryPort the registry port to use
   * @return the RMI registry
   * @throws RemoteException if the registry couldn't be located or created
   */
  protected Registry getRegistry(int registryPort) throws RemoteException {
    log.debug("Looking for RMI registry at port '" + registryPort + "'");
    Registry registry;
    try {
      // retrieve registry
      registry = LocateRegistry.getRegistry(registryPort);
      registry.list();
    }
    catch (RemoteException ex) {
      log.debug("Could not detect RMI registry - creating new one");
      // assume no registry found -> create new one
      registry = LocateRegistry.createRegistry(registryPort);
    }
    return registry;
  }

  public void afterPropertiesSet() throws Exception {
    log.debug("Rebind component "+service_url);

    SearchSessionFactory search_session_factory = (SearchSessionFactory) ctx.getBean("SearchSessionFactory");
    try {
      getRegistry(service_port);
      trmi.Naming.rebind(service_url, search_session_factory, new Class[] {Class.forName(service_interface)});
    }
    catch ( java.lang.ClassNotFoundException cnfe ) {
      cnfe.printStackTrace();
    }

  }
}
