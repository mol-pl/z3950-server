/**
 * Title:       Condition
 * @version:    $Id: Condition.java,v 1.2 2004/09/24 12:23:15 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: An interface implemented by any condition obejct that can be evaluated to
 *              true or false. Objects implementing this interface can be passed to the 
 *              waitFor method of AbstractIRResultSet to provide flexible waiting for search conditions
 *              common examples include a condition which waits for at least 10 hits or
 *              the search to be "Complete"
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
//
/*
 * $Log: Condition.java,v $
 * Revision 1.2  2004/09/24 12:23:15  ibbo
 * Migrated service API to standard IRResultSet interface... As it should be.
 *
 * Revision 1.1.1.1  2004/06/18 06:38:18  ibbo
 * Initial
 *
 * Revision 1.1.1.1  2004/03/08 15:25:38  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/12/05 16:30:44  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/11/16 15:10:42  ibbo
 * Initial import
 *
 * Revision 1.1  2002/10/20 13:30:08  ianibbo
 * Added Condition interface to IR package.
 *
 *
 */

package org.jzkit.search.provider.iface;

/**
 * A condition which must evaluate to true or false.
 *
 * @author Ian Ibbotson
 * @version $Id: Condition.java,v 1.2 2004/09/24 12:23:15 ibbo Exp $
 * @see org.jzkit.SearchProvider.iface.IRQuery
 */ 
public interface Condition
{
  /**
   * Evaluate the condition.
   * @param Object The context in which this expression should be evaluated. For example, a result set
   * condition would be passed the specific result set here.
   * @return true or false.
   */
  public boolean evaluate(Object context);
}
