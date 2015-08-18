/**
 * Title:       SearchException
 * @version:    $Id: SearchException.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999-2001 Knowledge Integration Ltd (See the COPYING file for details.)
 * @author:     Ian Ibbotson ( ibbo@k-int.com )
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

package org.jzkit.search.provider.iface;

public class SearchException extends Exception {
  public int error_code;
  public Object[] diagnostic_data = null;

  public SearchException() {
    super();
  }

  public SearchException(String reason) {
    super(reason);
  }

  public SearchException(String reason, int error_code) {
    super(reason);
    this.error_code = error_code;
  }

  public SearchException(String reason, Throwable cause) {
    super(reason,cause);
  }

  public SearchException(String reason, Throwable cause, int error_code) {
    super(reason,cause);
    this.error_code = error_code;
  }

  public static final int INTERNAL_ERROR = 1;
  public static final int UNKOWN_COLLECTION = 2;
  public static final int UNSUPPORTED_ACCESS_POINT = 3;
  public static final int UNSUPPORTED_ATTR_NAMESPACE = 4;
}
