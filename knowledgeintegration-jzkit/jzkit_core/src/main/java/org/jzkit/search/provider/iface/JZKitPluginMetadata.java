/**
 * Title:       Searchable
 * @version:    $Id: Searchable.java,v 1.4 2004/10/28 12:54:23 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: the Researcher interface is implemented by objects that are capable
 *              of evaluating an "IRQuery" object and communicating with the enquiry
 *              originator about it's progress whilst attempting to evaluate the enquiry
 *              
 */

package org.jzkit.search.provider.iface;

/**
 */ 
public interface JZKitPluginMetadata {
  public String getPluginCode();
  public String getPluginClassName();
  public String getPluginName();
  public String getPluginDescription();
  public PropDef[] getProps();
  public ExplainDTO explain(java.util.Map connection_properties);
}
