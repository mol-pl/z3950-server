//Title:       GenericEventToTargetListenerAdapter
//Version:     $Id: GenericEventToTargetListenerAdapter.java,v 1.2 2004/08/17 15:30:35 ibbo Exp $
//Copyright:   Copyright (C) 2001, Knowledge Integration Ltd (See the file LICENSE for details.)
//Author:      Ian Ibbotson ( ian.ibbotson@k-int.com )
//Company:     KI
//Description: Your description
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

package org.jzkit.z3950.util;
import java.util.Observer;
import java.util.Observable;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;

/**
 * GenericEventToTargetListenerAdapter : Adapts the generic Observer/Observable pattern
 * to produce notification messages for a Z Target
 *
 * @author Ian Ibbotson ( ian.ibbotson@k-int.com )
 * @version $Id: GenericEventToTargetListenerAdapter.java,v 1.2 2004/08/17 15:30:35 ibbo Exp $
 *
 */
 

public class GenericEventToTargetListenerAdapter implements Observer
{
  private TargetAPDUListener zl = null;

  public GenericEventToTargetListenerAdapter(TargetAPDUListener zl)
  {
    this.zl = zl;
  }

  public void update(Observable o, Object arg) 
  {

    APDUEvent e  = (APDUEvent)arg;
    PDU_type pdu = e.getPDU();

    // Peek into the pdu and make the appropriate notification
    switch ( pdu.which )
    {
      case PDU_type.initrequest_CID:
        zl.incomingInitRequest(e);
        break;
      case PDU_type.searchrequest_CID:
        zl.incomingSearchRequest(e);
        break;
      case PDU_type.presentrequest_CID:
        zl.incomingPresentRequest(e);
        break;
      case PDU_type.deleteresultsetrequest_CID:
        zl.incomingDeleteResultSetRequest(e);
        break;
      case PDU_type.accesscontrolresponse_CID:
        zl.incomingAccessControlResponse(e);
        break;
      case PDU_type.resourcecontrolrequest_CID:
        zl.incomingResourceControlRequest(e);
        break;
      case PDU_type.triggerresourcecontrolrequest_CID:
        zl.incomingTriggerResourceControlRequest(e);
        break;
      case PDU_type.resourcereportrequest_CID:
        zl.incomingResourceReportRequest(e);
        break;
      case PDU_type.scanrequest_CID:
        zl.incomingScanRequest(e);
        break;
      case PDU_type.sortrequest_CID:
        zl.incomingSortRequest(e);
        break;
      case PDU_type.segmentrequest_CID:
        zl.incomingSegmentRequest(e);
        break;
      case PDU_type.extendedservicesrequest_CID:
        zl.incomingExtendedServicesRequest(e);
        break;
      case PDU_type.close_CID:
        zl.incomingClose(e);
        break;
      default:
        // Dunno... Just notify listener of incoming APDU
        // Should probably throw an exception?
   
    }
  }
}
