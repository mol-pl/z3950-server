package org.jzkit.client.bsh.commands;

import bsh.*;
import org.jzkit.ServiceDirectory.*;

public class jzNewSession {

  public jzNewSession() {
  }

  public static org.jzkit.search.SearchSession invoke( Interpreter env, CallStack callstack ) throws bsh.EvalError {
    org.jzkit.configuration.api.Configuration c = (org.jzkit.configuration.api.Configuration) env.get("jzcfg");
    org.springframework.context.ApplicationContext ctx = (org.springframework.context.ApplicationContext) env.get("jzctx");
    org.jzkit.search.SearchSessionFactory search_session_factory = (org.jzkit.search.SearchSessionFactory) ctx.getBean("SearchSessionFactory");
    org.jzkit.search.SearchSession new_session = search_session_factory.getSearchSession();
    new_session.setType("JZKitClientSession");
    return new_session;
  }
}

