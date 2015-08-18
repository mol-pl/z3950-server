//Title:       GenericEventToOriginListenerAdapter
//Version:     $Id: GenericEventToOriginListenerAdapter.java,v 1.3 2005/10/26 13:46:56 ibbo Exp $
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * GenericEventToOriginListenerAdapter : Adapts the generic Observer/Observable pattern
 * to produce notification messages for a Z
 *
 * @author Ian Ibbotson ( ian.ibbotson@k-int.com )
 * @version $Id: GenericEventToOriginListenerAdapter.java,v 1.3 2005/10/26 13:46:56 ibbo Exp $
 *
 */
 

public class GenericEventToOriginListenerAdapter implements Observer {
  private static Log log = LogFactory.getLog(GenericEventToOriginListenerAdapter.class);
  private APDUListener zl = null;

  public GenericEventToOriginListenerAdapter(APDUListener zl) {
    this.zl = zl;
  }

  public void update(Observable o, Object arg) {

    APDUEvent e = (APDUEvent)arg;
    PDU_type pdu = e.getPDU();

    log.debug("update:"+e.getPDU().which);

    // Peek into the pdu and make the appropriate notification
    switch ( pdu.which )
    {
      case PDU_type.initresponse_CID:
        zl.incomingInitResponse(e);
        break;
      case PDU_type.searchresponse_CID:
        zl.incomingSearchResponse(e);
        break;
      case PDU_type.presentresponse_CID:
        zl.incomingPresentResponse(e);
        break;
      case PDU_type.deleteresultsetresponse_CID:
        zl.incomingDeleteResultSetResponse(e);
        break;
      case PDU_type.accesscontrolrequest_CID:
        zl.incomingAccessControlRequest(e);
        break;
      case PDU_type.resourcecontrolresponse_CID:
        zl.incomingResourceControlResponse(e);
        break;
      case PDU_type.resourcereportresponse_CID:
        zl.incomingResourceReportResponse(e);
        break;
      case PDU_type.scanresponse_CID:
        zl.incomingScanResponse(e);
        break;
      case PDU_type.sortresponse_CID:
        zl.incomingSortResponse(e);
        break;
      case PDU_type.extendedservicesresponse_CID:
        zl.incomingExtendedServicesResponse(e);
        break;
      case PDU_type.close_CID:
        zl.incomingClose(e);
        break;
      default:
        // Dunno... Just notify listener of incoming APDU
        zl.incomingAPDU(e);
    }
  }
}
