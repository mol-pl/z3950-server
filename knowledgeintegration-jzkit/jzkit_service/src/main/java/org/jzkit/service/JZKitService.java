package org.jzkit.service;

import java.util.*;
import java.io.InputStream;

import org.springframework.context.support.*;
import org.springframework.context.*;

import org.jzkit.configuration.api.Configuration;
import org.jzkit.configuration.provider.db.DbConfigurationProvider;
import org.jzkit.search.util.RecordConversion.FragmentTransformerService;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;

/**
 *
 */
public class JZKitService implements Referenceable {
  public static Log log = LogFactory.getLog(JZKitService.class);

  private ApplicationContext ctx = null;
  private String jzkit_home = null;

  /**
   *
   */
  public static void main(String[] args) {
    try {
      log.info("Starting jzkit2 service..."+args);
      ApplicationContext app_context = new ClassPathXmlApplicationContext( args );

      // org.jzkit.configuration.provider.db.DbConfigSyncTool sync_tool = (org.jzkit.configuration.provider.db.DbConfigSyncTool) app_context.getBean("ConfigSyncTool");

      // if ( sync_tool != null ) {
      //   log.debug("Checking for config updates");
      //   sync_tool.sync();
      // }

      log.info("JZKit server startup completed");
    }
    catch ( Exception e ) {
      log.warn("Problem",e);
    }
  }

  private JZKitService() {
  }

  public JZKitService(String jzkit_home) {
    this.jzkit_home = jzkit_home;
  }

  public void init() {
    try {
      log.debug("init - jzkit_home="+jzkit_home);
      String app_ctx = "file:///"+jzkit_home+"/etc/JZKitServiceAppCtx.xml";
      log.debug("app_ctx=\""+app_ctx+"\", creating app ctx");
      ctx = new FileSystemXmlApplicationContext(app_ctx);
    }
    catch ( Exception e ) {
      log.error("Problem loading configuration",e);
    }
  }

  public Reference getReference() {
    Reference ref = new Reference(JZKitService.class.getName(), JZKitServiceFactory.class.getName(), null); // no classpath defined
    ref.add(new StringRefAddr("jzkit_home", jzkit_home));
    return ref;
  }

  public static JZKitService create(String jzkit_home) {
    JZKitService result = new JZKitService(jzkit_home);
    result.init();
    return result;
  }

  public org.jzkit.configuration.api.Configuration getConfig() { 
    org.jzkit.configuration.api.Configuration result = null;
    if ( ctx != null ) {
      result = (org.jzkit.configuration.api.Configuration) ctx.getBean("JZKitConfig");
    }
    return result;
  }

  public ApplicationContext getCtx() {
    return ctx;
  }

  public org.jzkit.search.impl.StatelessQueryService getStatelessQueryService() {
    return (org.jzkit.search.impl.StatelessQueryService) ctx.getBean("StatelessQueryService");
  }
}
