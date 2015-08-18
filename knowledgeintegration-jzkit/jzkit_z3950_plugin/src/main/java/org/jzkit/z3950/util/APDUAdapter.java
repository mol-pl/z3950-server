//Title:       SearchAgentComponent
//Version:     $Id: APDUAdapter.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
//Copyright:   Copyright (C) 1999, Knowledge Integration Ltd (See the file LICENSE for details.)
//Author:      Mark Neale
//Company:     KI
//Description: Your description
//

package org.jzkit.z3950.util;


/**
  APDUAdapter : Blank implemenration of ADPUListener interface methods
  						allows easier construction of anonymous classes.

  * @author MAN
  * @version $Id: APDUAdapter.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
  */
 

public class APDUAdapter 
	implements APDUListener
{
  // Used when an unidentified PDU is recieved
  public void incomingAPDU(APDUEvent e){};
  // public void incomingInitRequest(APDUEvent e){};
  public void incomingInitResponse(APDUEvent e){};
  // public void incomingSearchRequest(APDUEvent e){};
  public void incomingSearchResponse(APDUEvent e){};
  // public void incomingPresentRequest(APDUEvent e){};
  public void incomingPresentResponse(APDUEvent e){};
  // public void incomingDeleteResultSetRequest(APDUEvent e){};
  public void incomingDeleteResultSetResponse(APDUEvent e){};
  public void incomingAccessControlRequest(APDUEvent e){};
  // public void incomingAccessControlResponse(APDUEvent e){};
  // public void incomingResourceControlRequest(APDUEvent e){};
  public void incomingResourceControlResponse(APDUEvent e){};
  // public void incomingTriggerResourceControlRequest(APDUEvent e){};
  // public void incomingResourceReportRequest(APDUEvent e){};
  public void incomingResourceReportResponse(APDUEvent e){};
  // public void incomingScanRequest(APDUEvent e){};
  public void incomingScanResponse(APDUEvent e){};
  // public void incomingSortRequest(APDUEvent e){};
  public void incomingSortResponse(APDUEvent e){};
  // public void incomingSegmentRequest(APDUEvent e){};
  // public void incomingExtendedServicesRequest(APDUEvent e){};
  public void incomingExtendedServicesResponse(APDUEvent e){};
  public void incomingClose(APDUEvent e){};
}
