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
public class ExplainDTO {

  private String title;
  private String description;
  private ExplainDBInfoDTO[] databases;
  

  public ExplainDTO() {
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  public ExplainDBInfoDTO[] getDatabases() {
    return databases;
  }

  public void setDatabases(ExplainDBInfoDTO[] databases) {
    this.databases=databases;
  }

  public String toString() {
    return title+" "+description+" "+databases;
  }

}
