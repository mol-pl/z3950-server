package org.jzkit.search.util;


import java.util.*;

/**
 * Title:       OAIHeaderInformation for an InformationFragment
 * Description:     
 * Copyright:       
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id$
 */
public class OAIHeaderInfo implements java.io.Serializable {

  private Date date_added;
  private Date date_modified;
  private Date date_deleted;
  private boolean del_flag;
  private String identifier;
  private String set_names;

  public OAIHeaderInfo() {
  }

  public OAIHeaderInfo(Date date_added,
                       Date date_modified,
                       Date date_deleted,
                       boolean del_flag,
                       String identifier,
                       String set_names) {
    this.date_added = date_added;
    this.date_modified = date_modified;
    this.date_deleted = date_deleted;
    this.del_flag = del_flag;
    this.identifier = identifier;
    this.set_names = set_names;
  }

  public Date getDateAdded() {
    return date_added;
  }

  public void setDateAdded(Date date_added) {
    this.date_added = date_added;
  }

  public Date getDateModified() {
    return date_modified;
  }

  public void setDateModified(Date date_modified) {
    this.date_modified = date_modified;
  }

  public Date getDateDeleted() {
    return date_deleted;
  }

  public void setDateDeleted(Date date_deleted) {
    this.date_deleted = date_deleted;
  }

  public boolean getDelFlag() {
    return del_flag;
  }

  public void setDelFlag(boolean del_flag) {
    this.del_flag = del_flag;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public void setSetNames(String set_names) {
    this.set_names = set_names;
  }

  public String getSetNames() {
    return set_names;
  }
}
