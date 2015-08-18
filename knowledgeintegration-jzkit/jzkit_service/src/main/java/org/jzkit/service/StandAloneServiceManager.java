package org.jzkit.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.*;
                                                                                                                                          
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JZkit Service Manager that does not use Tanuki Libraries
 */
public class StandAloneServiceManager {

  public static Log log = LogFactory.getLog(StandAloneServiceManager.class);

  /**
   *
   */
  public static void main(String[] args)
  {
    if ( args.length == 0 ) {
      log.error("Usage: StandAloneServiceManager app_context_def.xml+++");
      System.exit(1);
    }

    StandAloneServiceManager sasm = new StandAloneServiceManager();
    sasm.start(args);
  }

  private StandAloneServiceManager() {
  }

  public Integer start( String[] args ) {
    try {
      log.info("Starting jzkit2 service..."+args);
      ApplicationContext app_context = new ClassPathXmlApplicationContext( args );
      log.info("JZKit server startup completed");
    }
    catch ( Exception e ) {
      log.warn("Problem",e);
    }

    return null;
  }
}
