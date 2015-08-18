package org.jzkit.client;

import java.util.logging.*;
import java.util.*;
import java.io.InputStream;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.*;

import org.jzkit.search.SearchSession;

/**
 *
 */
public class JZKitClient
{
  private static Logger log = Logger.getLogger(JZKitClient.class.getName());

  /**
   *
   */
  public static void main(String[] args)
  {
    if ( args.length != 1 ) {
      System.err.println("Usage: JZKitClient app_context_def.xml");
      System.exit(1);
    }

    try {
      String application_context_resource = args[0];
      ApplicationContext app_context = new ClassPathXmlApplicationContext( new String[] {application_context_resource} );

      SearchSession search_service = (SearchSession) app_context.getBean("SearchService",SearchSession.class);

      log.info("JZKit server startup completed");
    }
    catch ( Exception e ) {
      log.log(Level.SEVERE,"Problem",e);
    }
  }
}
