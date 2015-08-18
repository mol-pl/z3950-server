package org.jzkit.client.bsh.commands;

import bsh.*;
import org.jzkit.ServiceDirectory.*;



public class jzList {

  public jzList() {
    System.out.println("jzList constructor");
  }

  public static void invoke( Interpreter env, CallStack callstack ) {
    env.println("usage jzList([\"sessions\"])");
  }

  public static void invoke( Interpreter env, CallStack callstack, String list_type ) throws bsh.EvalError {
    org.jzkit.configuration.api.Configuration c = (org.jzkit.configuration.api.Configuration) env.get("jzcfg");
    org.springframework.context.ApplicationContext ctx = (org.springframework.context.ApplicationContext) env.get("jzctx");
    org.jzkit.search.SearchSessionFactory search_session_factory = (org.jzkit.search.SearchSessionFactory) ctx.getBean("SearchSessionFactory");

    if ( list_type.equalsIgnoreCase("sessions") ) {
      java.util.List sessions = search_session_factory.activeSessions();
      env.println("JZkit Instance currently holds "+sessions.size()+" active sessions");
      for ( java.util.Iterator i = sessions.iterator(); i.hasNext(); ) {
        org.jzkit.search.SearchSession sess = (org.jzkit.search.SearchSession) i.next();
        env.println(" -> "+sess);
      }
    }
  }

}

