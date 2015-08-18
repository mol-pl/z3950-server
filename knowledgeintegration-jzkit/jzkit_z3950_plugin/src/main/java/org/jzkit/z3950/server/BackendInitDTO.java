/**
 * Title:       BackendSearchResult
 * @version:    $Id: BackendSearchResult.java,v 1.2 2004/11/18 12:37:45 ibbo Exp $
 * Copyright:   Copyright (C) 2001 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     KI
 * Description: 
 */

/*
 * $log$
 */

package org.jzkit.z3950.server;
                                                                                                                                          
import org.jzkit.search.util.RecordModel.InformationFragment;
import java.util.*;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;

/**
 * The data package associated with a z39.50 search request and response. Follows the
 * lifecycle of a search request and response.
 */
public class BackendInitDTO {

  public ZServerAssociation assoc;
  public String server_name = "";

  public BackendInitDTO(ZServerAssociation assoc) {
    this.assoc = assoc;
  }
}
