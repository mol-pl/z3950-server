package org.jzkit.search;

import java.util.List;
import java.util.ArrayList;

/**
 * Title:       ExplainInformationDTO
 * @version:    $Id$
 * Copyright:   Copyright 2003-2005 Ian Ibbotson, Knowledge Integration Ltd
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Description:
 */
public class ExplainInformationDTO implements java.io.Serializable {

  private List database_info = new ArrayList();

  public ExplainInformationDTO() {
  }

  public List getDatabaseInfo() {
    return database_info;
  }

  public void setDatabaseInfo(List database_info) {
    this.database_info = database_info;
  }
}
