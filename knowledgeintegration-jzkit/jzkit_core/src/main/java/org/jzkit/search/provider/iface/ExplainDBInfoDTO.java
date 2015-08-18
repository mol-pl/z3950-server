/**
 * Title:       ExplainDBInfoDTO
 * @version:    $Id: Searchable.java,v 1.4 2004/10/28 12:54:23 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 */

package org.jzkit.search.provider.iface;

/**
 */ 
public class ExplainDBInfoDTO {

  private String local_code;
  private String title;
  private String description;

  public ExplainDBInfoDTO() {
  }

  public String getLocalCode() {
    return local_code;
  }

  public void setLocalCode(String local_code) {
    this.local_code = local_code;
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

  public String toString() {
    return local_code+":"+title+":"+description;
  }
}
