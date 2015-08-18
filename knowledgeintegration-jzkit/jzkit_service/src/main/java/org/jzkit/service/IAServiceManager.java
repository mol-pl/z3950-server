package org.jzkit.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.*;
                                                                                                                                          
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.tanukisoftware.wrapper.WrapperManager;
import org.tanukisoftware.wrapper.WrapperListener;
                    
public class IAServiceManager implements WrapperListener {

  public static Log log = LogFactory.getLog(IAServiceManager.class);

  /**
   *
   */
  public static void main(String[] args)
  {
    if ( args.length == 0 ) {
      log.error("Usage: IAServiceManager app_context_def.xml+++");
      System.exit(1);
    }

    WrapperManager.start( new IAServiceManager(), args );
  }

  private IAServiceManager() {
  }

  /**
   * The start method is called when the WrapperManager is signaled by the 
   *	native wrapper code that it can start its application.  This
   *	method call is expected to return, so a new thread should be launched
   *	if necessary.
   *
   * @param args List of arguments used to initialize the application.
   *
   * @return Any error code if the application should exit on completion
   *         of the start method.  If there were no problems then this
   *         method should return null.
   */
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

  /**
   * Called when the application is shutting down.  The Wrapper assumes that
   *  this method will return fairly quickly.  If the shutdown code code
   *  could potentially take a long time, then WrapperManager.signalStopping()
   *  should be called to extend the timeout period.  If for some reason,
   *  the stop method can not return, then it must call
   *  WrapperManager.stopped() to avoid warning messages from the Wrapper.
   *
   * @param exitCode The suggested exit code that will be returned to the OS
   *                 when the JVM exits.
   *
   * @return The exit code to actually return to the OS.  In most cases, this
   *         should just be the value of exitCode, however the user code has
   *         the option of changing the exit code if there are any problems
   *         during shutdown.
   */
  public int stop( int exitCode ) {
    log.debug("stop");
    return exitCode;
  }
    
  /**
   * Called whenever the native wrapper code traps a system control signal
   *  against the Java process.  It is up to the callback to take any actions
   *  necessary.  Possible values are: WrapperManager.WRAPPER_CTRL_C_EVENT, 
   *    WRAPPER_CTRL_CLOSE_EVENT, WRAPPER_CTRL_LOGOFF_EVENT, or 
   *    WRAPPER_CTRL_SHUTDOWN_EVENT
   *
   * @param event The system control signal.
   */
  public void controlEvent( int event ) {
    log.debug("controlEvent");
    if (WrapperManager.isControlledByNativeWrapper()) {
      // The Wrapper will take care of this event
    } else {
      // We are not being controlled by the Wrapper, so
      //  handle the event ourselves.
      if ((event == WrapperManager.WRAPPER_CTRL_C_EVENT) ||
        (event == WrapperManager.WRAPPER_CTRL_CLOSE_EVENT) ||
        (event == WrapperManager.WRAPPER_CTRL_SHUTDOWN_EVENT)){
        WrapperManager.stop(0);
      }
    }
  }
}
