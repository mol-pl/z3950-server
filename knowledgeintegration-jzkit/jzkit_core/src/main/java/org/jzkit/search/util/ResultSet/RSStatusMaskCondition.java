/**
 * Title:       RSStatusMaskCondition
 * @version:    $Id: RSStatusMaskCondition.java,v 1.3 2004/09/24 16:46:21 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *             
 */

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


package org.jzkit.search.util.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RSStatusMaskCondition implements org.jzkit.search.provider.iface.Condition, java.io.Serializable {

  private static Log log = LogFactory.getLog(RSStatusMaskCondition.class);
  public int mask;

  public RSStatusMaskCondition(int mask) {
    this.mask = mask;
  }

  public boolean evaluate(Object context) {
    IRResultSet rs = (IRResultSet)context;

    if ( log.isDebugEnabled() )
      log.debug("evaluate: status:"+rs.getStatus()+" mask="+mask+" result="+ ((rs.getStatus() & mask)>0));

    // Return true if the result set status matches any of those required by the mask.
    return ( ( rs.getStatus() & mask ) > 0 );
  }
}
