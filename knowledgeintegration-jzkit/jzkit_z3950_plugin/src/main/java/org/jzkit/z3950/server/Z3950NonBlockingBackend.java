// Title:       Z3950 Backend Interface
// @version:    $Id: Z3950NonBlockingBackend.java,v 1.3 2004/11/28 15:59:01 ibbo Exp $
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
import java.util.Collection;

public interface Z3950NonBlockingBackend {

  // public void init(BackendInitDTO bsr);
  public String getVersion();
  public String getImplName();

  /**
   * Implementor must call assoc.notifySearchResult(result);
   */
  public void search(BackendSearchDTO bsr);

  /**
   * Implementor must call assoc.notifyPresentResult(result);
   */
  public void present(BackendPresentDTO bpr);

  /**
   * Implementor must call assoc.notifyDeleteResultSetResult(result);
   */
  public void deleteResultSet(BackendDeleteDTO del_dto);
}
