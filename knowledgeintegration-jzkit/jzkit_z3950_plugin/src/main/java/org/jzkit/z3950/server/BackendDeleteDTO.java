/**
 * Title:       BackendDeleteResult
 * @version:    $Id: BackendDeleteResult.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
 * Copyright:   Copyright (C) 2001 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     KI
 * Description: 
 */

/*
 * $log$
 */

package org.jzkit.z3950.server;
                                                                                                                                          
public class BackendDeleteDTO {
  public ZServerAssociation assoc;
  public String result_set_name;

  public BackendDeleteDTO(ZServerAssociation assoc,
                          String result_set_name) {
    this.assoc = assoc;
    this.result_set_name = result_set_name;
  }

}
