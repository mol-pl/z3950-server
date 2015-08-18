/**
 * Title:       ExampleIRServiceDescription
 * @version:    $Id: ExampleIRServiceFactory.java,v 1.5 2004/11/27 09:18:57 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

/**
 * $Log: ExampleIRServiceFactory.java,v $
 * Revision 1.5  2004/11/27 09:18:57  ibbo
 * Updated to make app context available more widely
 *
 * Revision 1.4  2004/10/22 14:38:12  ibbo
 * updated
 *
 * Revision 1.3  2004/09/13 14:24:03  ibbo
 * Fixed some problems introduced by new SearchServiceDescriptionDBO.java changes
 *
 * Revision 1.2  2004/09/09 09:34:03  ibbo
 * Updated
 *
 * Revision 1.1  2004/08/26 13:41:55  ibbo
 * Updated
 *
 */

package org.jzkit.search.provider.SimpleExample;

import java.util.Properties;
import java.util.Observer;

import org.jzkit.search.provider.iface.*;
import org.springframework.context.*;

/**
 * @author Ian Ibbotson
 */ 
public class ExampleIRServiceFactory implements SearchServiceFactory {
                                                                                                                                          
  private static String[] prop_names = new String[] { "randomDelay" };
  private int random_delay = 10;
  private String behaviour = "normal";
  private ApplicationContext ctx;

  public Searchable newSearchable(ServiceUserInformation user_info) {
    Searchable s = new ExampleSearchable(random_delay, behaviour);
    s.setApplicationContext(ctx);
    return s;
  }

  public Searchable newSearchable() {
    Searchable s = new ExampleSearchable();
    s.setApplicationContext(ctx);
    return s;
  }

  public int getRandomDelay() {
    return random_delay;
  }

  public void setRandomDelay(int random_delay) {
    this.random_delay = random_delay;
  }

  public String getBehaviour() {
    return behaviour;
  }

  public void setBehaviour(String behaviour) {
    this.behaviour = behaviour;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public String[] getPropertyNames() {
    return prop_names;
  }
}
