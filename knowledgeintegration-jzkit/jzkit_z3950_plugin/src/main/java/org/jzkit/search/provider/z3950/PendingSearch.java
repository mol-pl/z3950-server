// Title:       PendingSearch
// @version:    $Id: PendingSearch.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
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

package org.jzkit.search.provider.z3950;

import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.provider.iface.*;

public class PendingSearch
{
  public IRQuery q;
  public Object user_info;
  public String task_id;
  public String refid;
  public AbstractIRResultSet st;

  public PendingSearch(IRQuery q,
                       Object user_info,
                       String task_id,
                       String refid,
                       AbstractIRResultSet st)
  {
    this.q=q;
    this.user_info=user_info;
    this.task_id=task_id;
    this.refid=refid;
    this.st=st;
  }
}
