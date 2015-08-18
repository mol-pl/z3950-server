/**
 * Title:       IRResultSetStatus
 * @version:    $Id: IRResultSetStatus.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
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
import org.jzkit.search.util.RecordModel.*;

public class IRResultSetStatus
{
  public static final int UNDEFINED = 0;

  public static final String[] codes = new String[] { "Undefined", "Idle", "Subset", "Complete", "Failure", "Unfilled", "Searching" };

  /** The result set is not yet known. The result set size is undefined */
  public static final int IDLE = 1;

  /** There is a subset of results available. The result set size should offer a best
      guess of the number of records available */
  public static final int SUBSET = 2;

  /** The result set is complete, the size reflects the actual known final size of the
      result set. Complete and size=0 = no records. */
  public static final int COMPLETE = 4;

  /** Problems.... */
  public static final int FAILURE = 8;
 
  public static final int UNFILLED = 16;

  public static final int SEARCHING = 32;
 

  private int record_available_hwm = 0;
  private int size = 0;
  private int status = UNDEFINED;

  public IRResultSetStatus(int status, int record_available_hwm, int size)
  {
    this.status = status;
    this.record_available_hwm = record_available_hwm;
    this.size = size;
  }
  
  public int getStatus()
  {
    return status;
  }

  public int getSize()
  {
    return size;
  }

  public int getRecordAvailableHWM()
  {
    return record_available_hwm;
  }

  public static String getCode(int c) {
    String result = null;

    switch ( c ) {
      case UNDEFINED:
        result = codes[0];
        break;
      case IDLE:
        result = codes[1];
        break;
      case SUBSET:
        result = codes[2];
        break;
      case COMPLETE:
        result = codes[3];
        break;
      case FAILURE:
        result = codes[4];
        break;
      case UNFILLED:
        result = codes[5];
        break;
      case SEARCHING:
        result = codes[6];
        break;
      default:
        result = "Unknown "+c;
        break;
    }

    return result;
  }
}
