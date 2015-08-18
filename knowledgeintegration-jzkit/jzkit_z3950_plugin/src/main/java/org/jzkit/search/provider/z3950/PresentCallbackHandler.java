// Title:       PresentCallbackHandler
// @version:    $Id: PresentCallbackHandler.java,v 1.2 2005/10/26 13:46:56 ibbo Exp $
// Copyright:   Copyright (C) 1999-2002 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
// Company:     KI
// Description: 
//


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

package  org.jzkit.search.provider.z3950;

import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


// Subclass for specific responses, such as incoming records, etc.
public class PresentCallbackHandler implements ZCallbackTarget
{
  private Z3950SearchTask the_task;
  private IFSNotificationTarget target;
  private ExplicitRecordFormatSpecification actual_spec;
  private int starting_fragment;

  private static Log log = LogFactory.getLog(PresentCallbackHandler.class);

  private PresentCallbackHandler() {
  }

  public PresentCallbackHandler(Z3950SearchTask task,
                                IFSNotificationTarget target,
                                ExplicitRecordFormatSpecification actual_spec, 
                                int starting_fragment)
  {
    the_task = task;
    this.target = target;
    this.actual_spec = actual_spec;
    this.starting_fragment = starting_fragment;
  }

  public void notifyClose(String reason) {
    log.debug("notify close: "+reason);
    target.notifyError("bib-1",null,reason,null);
  }

  public void notifyPresentResponse(PresentResponse_type resp)
  {
    InformationFragment[] records = the_task.processRecords(resp.records,actual_spec,starting_fragment);
    target.notifyRecords(records);
  }
}
