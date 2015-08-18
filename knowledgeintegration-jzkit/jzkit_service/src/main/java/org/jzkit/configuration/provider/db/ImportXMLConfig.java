package org.jzkit.configuration.provider.db;

import java.io.InputStream;
import java.io.FileInputStream;
import org.jzkit.configuration.api.*;
import java.util.prefs.*;
import java.util.Iterator;
import org.jzkit.util.*;
import org.jzkit.configuration.api.*;
import org.jzkit.ServiceDirectory.*;
import org.jzkit.search.util.Profile.ProfileDBO;
import org.jzkit.search.util.Profile.CrosswalkDBO;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides the actions that a JZKit management console will offer to the host system.
 * Functions are provided for creating, listing, changing and deleting configurations, starting servers, etc, etc.
 */
public class ImportXMLConfig {

  private static final int SYSTEM_PREF_TYPE = 1;
  private static final int USER_PREF_TYPE = 2;

  public static Log log = LogFactory.getLog(ImportXMLConfig.class);
  private ApplicationContext app_context = null;

  public static void main(String[] args)
  {
    if ( args.length != 2 ) {
      log.debug("Usage : ImportXMLConfig app_context_def.xml import_xml_file");
      System.exit(0);
    }
    else {
      String application_context_resource = args[0];
      ApplicationContext app_context = new ClassPathXmlApplicationContext( new String[] {application_context_resource} );
      ImportXMLConfig svc = new ImportXMLConfig(app_context);
      svc.importConfig((Configuration)app_context.getBean("JZKitConfig"),args[1]);
    }
  }

  private ImportXMLConfig() {
  }

  public ImportXMLConfig( ApplicationContext app_context ) {
    this.app_context = app_context;
  }

  public void importConfig(Configuration c, String config_file) {
    Configuration result = null;

    try {
    }
    catch ( Exception e ) {
      e.printStackTrace();
      throw new RuntimeException("Exception Loading JZKit Config", e);
    }
  }
}
