/**
 * Title:       QueryModel
 * @version:    $Id: QueryModel.java,v 1.2 2004/11/18 14:25:54 ibbo Exp $
 * Copyright:   Copyright (C) 2001 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: Interface implemented by anything that represents a users expresion
 *              of their search goal.
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

package org.jzkit.search.util.QueryModel;

import org.jzkit.search.util.QueryModel.Internal.InternalModelRootNode;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author Ian Ibbotson
 * @version $Id: QueryModel.java,v 1.2 2004/11/18 14:25:54 ibbo Exp $
 * @see org.jzkit.SearchProvider.iface.IRQuery
 */ 
public interface QueryModel
{
  public InternalModelRootNode toInternalQueryModel(ApplicationContext ctx) throws InvalidQueryException;
}
