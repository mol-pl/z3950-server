/**
 * Title:       IREvent
 * @version:    $Id: IREvent.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 * Copyright:   Copyright (C) 2001 Knowledge Integration Ltd.
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

package org.jzkit.search.provider.iface;


public class IREvent
{
  public static final int SOURCE_RESET=1000;
  public static final int FRAGMENT_COUNT_CHANGE=1001;
  public static final int SOURCE_NO_LONGER_AVAILABLE=1002;
  public static final int DIAGNOSTIC_EVENT=1003;
  public static final int MESSAGE_EVENT=1004;
  public static final int STATUS_NOTIFICATION=1005;
  public static final int ST_PUBLIC_STATUS_CODE_CHANGE=1011;
  public static final int ST_PRIVATE_STATUS_CODE_CHANGE=1012;

  public int event_type = 0;
  public Object event_info = null;

  public IREvent(int event_type, Object event_info)
  {
    this.event_type = event_type;
    this.event_info = event_info;
  }

}
