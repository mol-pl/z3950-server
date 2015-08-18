/**
 * Title:       Condition
 * @version:    $Id: ResultCountCondition.java,v 1.3 2004/09/24 16:46:21 ibbo Exp $
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
 * $Log: ResultCountCondition.java,v $
 * Revision 1.3  2004/09/24 16:46:21  ibbo
 * All final result set objects now implement IRResultSet directly instead of
 * inheriting the implementation from the Abstract base class.. This seems to
 * work better for trmi
 *
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

package org.jzkit.search.util.ResultSet;

import org.jzkit.search.provider.iface.Condition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A condition which must evaluate to true or false.
 *
 * @author Ian Ibbotson
 * @version $Id: ResultCountCondition.java,v 1.3 2004/09/24 16:46:21 ibbo Exp $
 * @see org.jzkit.SearchProvider.iface.IRQuery
 */ 
public class ResultCountCondition implements Condition, java.io.Serializable {

  private static Log log = LogFactory.getLog(ResultCountCondition.class);

  int target_result_count;
  int status = 0;

  public ResultCountCondition(int target_result_count) {
    this.target_result_count = target_result_count;
  }

  /**
   * Evaluate the condition.
   * @return true or false.
   */
  public boolean evaluate(Object context)
  {
    IRResultSet base_task = (IRResultSet) context;

    if ( log.isDebugEnabled() )
      log.debug("evaluate: status:"+base_task.getStatus()+" req="+target_result_count+" current="+base_task.getFragmentCount());

    // Return true if the number of hits available >= target_result_count OR
    // if the search has completed.
    return (((base_task.getStatus() & 
             (IRResultSetStatus.COMPLETE|IRResultSetStatus.FAILURE))!=0) ||
            (base_task.getFragmentCount() >= target_result_count ) )  ;
  }
}
