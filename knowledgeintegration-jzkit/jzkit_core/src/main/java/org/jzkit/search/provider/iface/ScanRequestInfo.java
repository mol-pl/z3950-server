/**
 * Title:       ScanRequestInfo
 * @version:    $Id: ScanRequestInfo.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 * Copyright:   Copyright (C) 2001 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: Hide all the attributes of a scan request behind this class
 *              so we can add more without changing the Scanable interface.
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

import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;
import java.util.Vector;


/**
 * ScanRequestInfo:
 * Information about a term, for example number of occurences in the database, etc.
 * @see Scanable
 */
public class ScanRequestInfo
{
  public Vector collections = null;
  public String attribute_set = null;
  public AttrPlusTermNode term_list_and_start_point = null;
  public int step_size = 0;
  public int number_of_terms_requested = 0;
  public int position_in_response = 0;
}
