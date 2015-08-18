/**
 * Title:       PendingGet
 * @version:    $Id: PendingGet.java,v 1.2 2004/10/03 16:31:54 ibbo Exp $
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

/**
 * PendingGet.
 */
public class PendingGet
{
  private int starting_fragment = 0;
  private int fragment_count = 0;
  private RecordFormatSpecification spec = null;
  private IFSNotificationTarget target = null;
  private ExplicitRecordFormatSpecification transform_spec = null;

  private PendingGet()
  {
  }
  
  public PendingGet(int starting_fragment,
                    int fragment_count,
                    RecordFormatSpecification spec,
                    IFSNotificationTarget target,
                    ExplicitRecordFormatSpecification transform_spec)
  {
    this.starting_fragment = starting_fragment;
    this.fragment_count = fragment_count;
    this.spec = spec;
    this.target = target;
    this.transform_spec = transform_spec;
  }

  public int getStartingFragment()
  {
    return starting_fragment;
  }

  public int getFragmentCount()
  {
    return fragment_count;
  }

  public RecordFormatSpecification getRecordFormatSpecification()
  {
    return spec;
  }

  public IFSNotificationTarget getNotificationTarget()
  {
    return target;
  }

  public int getTopFragmentPosition()
  {
    return starting_fragment+fragment_count-1;
  }

  public ExplicitRecordFormatSpecification getTransformSpec() {
    return transform_spec;
  }
}
