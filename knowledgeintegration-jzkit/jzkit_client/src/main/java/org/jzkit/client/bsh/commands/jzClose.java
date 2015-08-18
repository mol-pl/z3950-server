package org.jzkit.client.bsh.commands;

import bsh.*;
import org.jzkit.ServiceDirectory.*;



public class jzClose {

  public jzClose() {
  }

  public static void invoke( Interpreter env, CallStack callstack ) {
    env.println("usage jzClose([\"session-id\"])");
  }

  public static void invoke( Interpreter env, CallStack callstack, String session_id ) throws bsh.EvalError {
    org.jzkit.configuration.api.Configuration c = (org.jzkit.configuration.api.Configuration) env.get("jzcfg");
    org.springframework.context.ApplicationContext ctx = (org.springframework.context.ApplicationContext) env.get("jzctx");
    org.jzkit.search.SearchSessionFactory search_session_factory = (org.jzkit.search.SearchSessionFactory) ctx.getBean("SearchSessionFactory");

    env.println("Closing Session With ID "+session_id);
     org.jzkit.search.SearchSession ss = search_session_factory.getSession(session_id);
    if ( ss != null ) {
      ss.close();
      env.println("Session "+session_id+" Closed");
    }
    else {
      env.println("Unable to locate session "+session_id);
    }
  }

}

