package org.jzkit.webapp;

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SessionListener implements HttpSessionListener {

  private static int activeSessions = 0;
  private static Log log = LogFactory.getLog(SessionListener.class);
  private static Runtime r = Runtime.getRuntime();

  public SessionListener() {
    log.debug("\n\n**new session listener**");
  }

  public void sessionCreated(HttpSessionEvent se) {
    activeSessions++;

    if ( se != null ) {
      HttpSession s = se.getSession();
      if ( s != null ) {
        log.debug("Session[SessionListener:1] "+s.getId()+" created for user at "+s.getCreationTime()+" Session count = "+activeSessions);
      }
      else {
        log.debug("[SessionListener:1]Session Created, Session was null");
      }
    }
    else {
      log.debug("[SessionListener:1]Session Created, Session Event was null");
    }

  }

  public void sessionDestroyed(HttpSessionEvent se) {
      activeSessions--;

    if ( se != null ) {
      HttpSession s = se.getSession();
      if ( s != null ) {
        log.debug("Session "+s.getId()+" destroyed at "+s.getCreationTime()+" Active count="+activeSessions+", elapsed="+(System.currentTimeMillis()-s.getCreationTime())+", deleteCount="+s.getAttribute("DeleteCount")+",close="+s.getAttribute("CloseMethod"));
      }
      else {
        log.debug("[SessionListener:1]Session Destroyed, Session was null");
      }
    }
    else {
      log.debug("[SessionListener:1]Session Destroyed, Session Event was null");
    }

    memoryReport();
  }

  public static int getActiveSessions() {
    return activeSessions;
  }

  public static void memoryReport() {
    float freeMemory = (float) r.freeMemory()/1024;
    Float freeMemoryF = new Float(freeMemory);
 
    float totalMemory = (float) r.totalMemory()/1024;
    Float totalMemoryF = new Float(totalMemory);
 
    float maxMemory = (float) r.maxMemory()/1024;
    Float maxMemoryF= new Float(maxMemory);
 
    log.info("Free Memory : " + freeMemoryF.intValue() + " K \n" + "TotalMemory : " + totalMemoryF.intValue() + " K \n" + "MaxMemory : " + maxMemoryF.intValue() + " K \n");
  }
}
