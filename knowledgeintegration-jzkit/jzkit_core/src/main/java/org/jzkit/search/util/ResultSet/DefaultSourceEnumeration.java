/**
 * Title:       DefaultSourceEnumeration
 * @version:    $Id: DefaultSourceEnumeration.java,v 1.2 2004/09/30 14:45:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2001 Knowledge Integration Ltd.
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

import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;

public class DefaultSourceEnumeration implements AsynchronousEnumeration
{
  private IRResultSet source = null;
  private int next_element = 1;
  private int timeout = 10000;

  /**
   *  The record format specification for records produced by this enumeration.
   */
  private RecordFormatSpecification record_spec = null;

  public DefaultSourceEnumeration(IRResultSet source)
  {
    this.source = source;
    record_spec = new ExplicitRecordFormatSpecification( new IndirectFormatProperty("default_record_syntax"),
                                                         null,
                                                         new IndirectFormatProperty("default_element_set_name"));
  }

  public DefaultSourceEnumeration(IRResultSet source, RecordFormatSpecification record_spec)
  {
    this.source = source;
    record_spec = record_spec;
  }

  public boolean hasMoreElements()
  {
    if ( next_element <= source.getFragmentCount() )
      return true;

    return false;
  }

  public Object nextElement()
  {
    Object retval = null;

    try
    {
      InformationFragment[] records = source.getFragment(next_element,
                                                         1,
                                                         record_spec);
      retval = records[0];
    }
    catch ( IRResultSetException pe )
    {
      // Do nothing (for now)
      pe.printStackTrace();
    }

    if ( retval != null )
      next_element++;

    return retval;
  }

  public boolean nextIsAvailable()
  {
    return true;
  }

  public void setTimeout(int timeout)
  {
    this.timeout = timeout;
  }

  public void registerNotificationTarget(Object o)
  {
  }
}
