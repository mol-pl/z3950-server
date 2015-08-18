package org.jzkit.client.bsh.commands;

import bsh.*;
import org.jzkit.ServiceDirectory.*;

public class jzStatus {

  public jzStatus() {
    System.out.println("jzStatus constructor");
  }

  public static void invoke( Interpreter env, CallStack callstack ) throws bsh.EvalError {

    org.jzkit.configuration.api.Configuration c = (org.jzkit.configuration.api.Configuration) env.get("jzcfg");

    try {
      if ( c != null ) {
        env.println("JZKit Configuration Status");
        env.println("==========================");
        env.println("");

        env.println("Registered collections");
        env.println("======================");
        // Configured Searchable Objects
        for ( java.util.Iterator i = c.enumerateVisibleCollections(); i.hasNext(); ) {
          CollectionDescriptionDBO o = (CollectionDescriptionDBO) i.next();
          env.println(o.getCode()+" "+o.getCollectionName()+" service="+o.getSearchServiceDescription().getCode());
        }
        env.println("");

        env.println("Registered services");
        env.println("===================");
        // Configured Searchable Objects
        for ( java.util.Iterator i = c.enumerateRepositories(); i.hasNext(); ) {
          SearchServiceDescriptionDBO o = (SearchServiceDescriptionDBO) i.next();
          env.println(o.getCode()+" ("+o.getClassName()+")");
        }
        env.println("");

        env.println("JZKit Session Statistic");
        env.println("=======================");

    
        env.println("Current Search Landscape : ");

        env.println("Active queries");
        env.println("==============");
      }
      else {
        env.println("Fatal error. Unable to locate JZKit configuration");
      }
    }
    catch ( org.jzkit.configuration.api.ConfigurationException ce ) {
      env.println("Problem "+ce);
    }
  }

}

