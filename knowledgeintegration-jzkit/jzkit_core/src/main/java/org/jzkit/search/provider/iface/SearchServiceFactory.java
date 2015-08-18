package org.jzkit.search.provider.iface;

import java.util.*;

/**
 * An object which creates a search service. Different implementations of this
 * interface will assemble different searchablew objects
 * These objects should be lightweight, for example,
 * Searchable s = new Z3950SearchServiceDescription("z3950://test.server:210").newSearchable();
 *
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * Company:     Knowledge Integration Ltd.
 * License:     A license.txt file should is distributed along with this software
 *
 * @author Ian Ibbotson
 * @version $Id: SearchServiceFactory.java,v 1.5 2004/11/27 09:18:57 ibbo Exp $
 * @see org.jzkit.SearchProvider.iface.Searchable
 */ 
public interface SearchServiceFactory extends org.springframework.context.ApplicationContextAware {

  /**
   * @param user_info Information about the user requesting creation of this searchable object.
   * some search services require user authentication and some SearchServiceAuthenticationMethod
   * implementations *may* require the a userid (Or an athens_userid) in order to authenticate
   * themselves with the remote service. Subclasses *Must* be able to deal with a null value for
   * the user_info parameter. 
   * @return a new @see org.jzkit.SearchProvider.iface.Searchable object
   */
  public Searchable newSearchable(ServiceUserInformation user_info) throws SearchException;
  public Searchable newSearchable() throws SearchException;

  /** Return an array of the custom properties that can be set for a particular realisation of searchable */
  public String[] getPropertyNames();

}
