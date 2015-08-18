/** 
 * Title:       AsynchronousEnumeration
 * @version:    $Id: AsynchronousEnumeration.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: An Enumeration that can tell us if the nextElement will
 *              return immediately or block..
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

import java.util.Enumeration;

/**
 * AsynchronousEnumeration. An Enumeration that can tell us if the nextElement will
 * return immediately or block..
 *
 * @author Ian Ibbotson (ian.ibbotson@k-int.com)
 * @version $Id: AsynchronousEnumeration.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 */ 
public interface AsynchronousEnumeration extends Enumeration
{
  /** We know that there will be a next record in this set, is it available yet? */
  public boolean nextIsAvailable();

  /** Register an object to be notified when a next object is available */
  public void registerNotificationTarget(Object o);
}
