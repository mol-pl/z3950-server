// Title:       Z3950 Backend Interface
// @version:    $Id: AbstractSynchronousBackend.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
// Copyright:   Copyright (C) 2001 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ibbo@k-int.com)
// Company:     KI
// Description: 
//
 
 
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2.1 of
// the license, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite
// 330, Boston, MA  02111-1307, USA.
//
     

/**
 */

package org.jzkit.z3950.server;

import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import java.util.List;

public abstract class AbstractSynchronousBackend implements Z3950NonBlockingBackend {
  
  /*
   * 
   */
  public abstract BackendSearchDTO backendSearch(BackendSearchDTO search_req);

  /**
   */
  public abstract BackendPresentDTO backendPresent(BackendPresentDTO present_req);

  public abstract BackendDeleteDTO backendDeleteResultSet(BackendDeleteDTO delete_req);
 

  public void search(BackendSearchDTO search_req) {
    search_req.assoc.notifySearchResult(backendSearch(search_req));
  }

  /**
   * Implementor must call assoc.notifyPresentResult(result);
   */
  public void present(BackendPresentDTO request) {
    request.assoc.notifyPresentResult(backendPresent(request));
  }
                                                                                                                                          
  /**
   * Implementor must call assoc.notifyDeleteResultSetResult(result);
   */
  public void deleteResultSet(BackendDeleteDTO delete_request) {
    delete_request.assoc.notifyDeleteResult(backendDeleteResultSet(delete_request));
  }
}
