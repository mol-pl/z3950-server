/**
 * Title:       Searchable
 * @version:    $Id: Searchable.java,v 1.4 2004/10/28 12:54:23 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: the Researcher interface is implemented by objects that are capable
 *              of evaluating an "IRQuery" object and communicating with the enquiry
 *              originator about it's progress whilst attempting to evaluate the enquiry
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
//
/*
 * $Log: Searchable.java,v $
 * Revision 1.4  2004/10/28 12:54:23  ibbo
 * Updated
 *
 * Revision 1.3  2004/09/30 17:05:19  ibbo
 * Continued adoption of new RecordFormatSpecification
 *
 * Revision 1.2  2004/09/09 09:34:03  ibbo
 * Updated
 *
 * Revision 1.1.1.1  2004/06/18 06:38:18  ibbo
 * Initial
 *
 * Revision 1.1.1.1  2004/03/08 15:25:38  ibbo
 * Initial Import
 *
 * Revision 1.2  2004/02/07 17:42:51  ibbo
 * Updated
 *
 * Revision 1.1.1.1  2003/12/05 16:30:44  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/11/16 15:10:42  ibbo
 * Initial import
 *
 * Revision 1.19  2003/05/09 12:54:44  rob_tice
 * Updated diagnostics and started removal of unused imports
 *
 * Revision 1.18  2002/10/02 09:25:48  ianibbo
 * Updated commenting.
 *
 * Revision 1.17  2002/06/19 10:33:28  ianibbo
 * Backed out NamingContext changes
 *
 */

package org.jzkit.search.provider.iface;

import java.util.*;
import org.jzkit.search.util.ResultSet.IRResultSet;
import org.springframework.context.*;
import org.jzkit.search.util.QueryModel.Internal.AttrValue;

/**
 * Searchable is the core interface of the IRLayer in JZKit. Objects implementing this
 * interface can take part in search operations and potentially be managed as one of
 * a group of resources against which a query is being evaluated. Search results may
 * themselves implement the searchable interface.
 *
 * @author Ian Ibbotson
 * @version $Id: Searchable.java,v 1.4 2004/10/28 12:54:23 ibbo Exp $
 * @see org.jzkit.SearchProvider.iface.IRQuery
 */ 
public interface Searchable extends ApplicationContextAware
{
  /** destroy the searchable object. Shut down the searchable object entirely. Release all
   *  held resources, make the object ready for GC. Try to release in here instead of on finalize.
   */
  public void close();

  /** Create a AbstractIRResultSet. Evaluate the query with the Tasks evaluate method.  
      ** This method must not block **
   */
  public IRResultSet evaluate(IRQuery q);

  /** Create a AbstractIRResultSet. Evaluate the query with the Tasks evaluate method.  
      ** This method must not block **
   */
  public IRResultSet evaluate(IRQuery q, Object user_info);

  /** Create a AbstractIRResultSet. Evaluate the query with the Tasks evaluate method.
      Any observers will be passwd to the AbstractIRResultSet and will be notified of search events.
      ** This method must not block **
   */
  public IRResultSet evaluate(IRQuery q, Object user_info, Observer[] observers);

  /**
   *  Initialise record syntax archetype information.
   */
  public void setRecordArchetypes(Map record_syntax_archetypes);

  public Map getRecordArchetypes();
}
