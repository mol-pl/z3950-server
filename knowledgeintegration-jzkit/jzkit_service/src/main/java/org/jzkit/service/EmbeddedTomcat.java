/**
 * Title:       
 * Copyright:   Copyright (C) 2002 Knowledge Integration Ltd (See the COPYING file for details.)
 * @author:     Ian Ibbotson ( ian.ibbotson@k-int.com )
 * Company:     Knowledge Integration Ltd.
 * Description:
 */

package org.jzkit.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.net.URL;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.startup.Embedded;
import org.apache.catalina.Container;
import org.apache.catalina.Wrapper;
import org.springframework.context.*;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class EmbeddedTomcat implements ApplicationContextAware {

  public static Log log = LogFactory.getLog(EmbeddedTomcat.class);


  private static final String DEFAULT_ENGINE = "default";
  private static final String DEFAULT_HOST = "localhost";
  private static final String WEB_APPS_NAME = "webapps";
  private static final String DOC_BASE = "ROOT";
  private ApplicationContext ctx = null;

  private Embedded embedded;
  private String catalinaHome;

  public EmbeddedTomcat() {
    // Register a shutdown hook to do a clean shutdown
    Runtime.getRuntime().addShutdownHook(
      new Thread() {
        public void run() {
        stopServer();
      }
    });
  }

  public void startServer() throws Exception {
    embedded.start();
  }

  public void stopServer() {
    if (embedded != null) {
      try {
        log.info("Shutting down embedded tomcat...");
        embedded.stop();
        log.info("Embedded tomcat shutdown.");
      } catch (Exception e) {
        //No need to do anything
      }
    }
  }

  private void init() throws Exception {
    File home = (new File("..")).getCanonicalFile();
    catalinaHome = home.getAbsolutePath();

    embedded = new Embedded();
    embedded.setCatalinaHome(catalinaHome);

    // Create an Engine
    Engine engine = embedded.createEngine();
    engine.setName(DEFAULT_ENGINE);
    engine.setDefaultHost(DEFAULT_HOST);
    embedded.addEngine(engine);

    // Create a Host
    File webAppsLocation = new File(home, WEB_APPS_NAME);
    Host host = embedded.createHost( DEFAULT_HOST, webAppsLocation.getAbsolutePath());
    engine.addChild(host);


    // Add the context
    File docBase = new File(webAppsLocation, DOC_BASE);
    Context context = this.createContext("",docBase.getAbsolutePath());
    host.addChild(context);

    String config_file_name = (String) ctx.getBean("configPropsFileName");

    log.debug("Attempting to load properties from "+config_file_name);
    Properties props = new Properties();
    try {
      InputStream is = this.getClass().getResourceAsStream(config_file_name);
      props.load(is);
    }
    catch(Exception e) {
    }
   
    log.debug("Web Context Props : "+props);

    // now add the lex app
    Context or_context = this.createContext("/jzkit", webAppsLocation.getAbsolutePath()+"/jzkit");

    for ( java.util.Iterator pi = props.keySet().iterator(); pi.hasNext(); ) {
      String prop_name = (String) pi.next();
      log.debug("Setting "+prop_name+" to "+props.getProperty(prop_name));
      setContextParam(or_context,prop_name,props.getProperty(prop_name));
    }

    String prop_driver = props.getProperty("org.jzkit.jdbc_driver");
    String prop_jdbc_url = props.getProperty("org.jzkit.url");
    String prop_jdbc_user = props.getProperty("org.jzkit.username");
    String prop_jdbc_pass = props.getProperty("org.jzkit.password");
  
    org.apache.catalina.realm.JDBCRealm jdbc_realm = new org.apache.catalina.realm.JDBCRealm();
    jdbc_realm.setDriverName(prop_driver);
    jdbc_realm.setConnectionURL(prop_jdbc_url);
    jdbc_realm.setConnectionName(prop_jdbc_user);
    jdbc_realm.setConnectionPassword(prop_jdbc_pass);
    jdbc_realm.setUserTable("IDENT_AUTH_DETAILS");
    jdbc_realm.setUserNameCol("USERNAME");
    jdbc_realm.setUserCredCol("PASSWORD");
    jdbc_realm.setUserRoleTable("TC_ROLES");
    jdbc_realm.setRoleNameCol("ROLE");

    or_context.setRealm(jdbc_realm);

    host.addChild(or_context);

    // Create a connector that listens on all addresses port 8080
    Connector connector = embedded.createConnector( (String)null, 8080, false);
        
    // Wire up the connector
    embedded.addConnector(connector);

    startServer();
  }

  private Context createContext(String path, String docBase){
    // Create a Context
    Context context = embedded.createContext(path, docBase);
    context.setParentClassLoader(this.getClass().getClassLoader());

    // Create a default servlet
    Wrapper servlet = context.createWrapper();
    servlet.setName("default");
    servlet.setServletClass( "org.apache.catalina.servlets.DefaultServlet");
    servlet.setLoadOnStartup(1);
    servlet.addInitParameter("debug", "0");
    servlet.addInitParameter("listings", "false");
    context.addChild(servlet);
    context.addServletMapping("/", "default");

    // Create a handler for jsps
    Wrapper jspServlet = context.createWrapper();
    jspServlet.setName("jsp");
    jspServlet.setServletClass( "org.apache.jasper.servlet.JspServlet");
    jspServlet.addInitParameter("fork", "false");
    jspServlet.addInitParameter("xpoweredBy", "false");
    jspServlet.setLoadOnStartup(2);
    context.addChild(jspServlet);
    context.addServletMapping("*.jsp", "jsp");
    context.addServletMapping("*.jspx", "jsp");

    // Set seme default welcome files
    context.addWelcomeFile("index.html");
    context.addWelcomeFile("index.htm");
    context.addWelcomeFile("index.jsp");
    context.setSessionTimeout(30);

    // Add some mime mappings
    context.addMimeMapping("html", "text/html");
    context.addMimeMapping("htm", "text/html");
    context.addMimeMapping("gif", "image/gif");
    context.addMimeMapping("jpg", "image/jpeg");
    context.addMimeMapping("png", "image/png");
    context.addMimeMapping("js", "text/javascript");
    context.addMimeMapping("css", "text/css");
    context.addMimeMapping("pdf", "application/pdf");

    return context;
  }

  private void setContextParam(Context ctx, String name, String value) {
    org.apache.catalina.deploy.ApplicationParameter param = new org.apache.catalina.deploy.ApplicationParameter();
    param.setName(name);
    param.setValue(value);
    ctx.addApplicationParameter(param);
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

}
