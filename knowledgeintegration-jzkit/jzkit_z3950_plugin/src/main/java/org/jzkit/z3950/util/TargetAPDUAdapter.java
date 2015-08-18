//Title:       SearchAgentComponent
//Version:     $Id: TargetAPDUAdapter.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
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
  * @version $Id: TargetAPDUAdapter.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
  */
 

public class TargetAPDUAdapter 
	implements TargetAPDUListener
{
  public void incomingAPDU(APDUEvent e){};
  public void incomingInitRequest(APDUEvent e){};
  public void incomingSearchRequest(APDUEvent e){};
  public void incomingPresentRequest(APDUEvent e){};
  public void incomingDeleteResultSetRequest(APDUEvent e){};
  public void incomingAccessControlResponse(APDUEvent e){};
  public void incomingResourceControlRequest(APDUEvent e){};
  public void incomingTriggerResourceControlRequest(APDUEvent e){};
  public void incomingResourceReportRequest(APDUEvent e){};
  public void incomingScanRequest(APDUEvent e){};
  public void incomingSortRequest(APDUEvent e){};
  public void incomingSegmentRequest(APDUEvent e){};
  public void incomingExtendedServicesRequest(APDUEvent e){};
  public void incomingClose(APDUEvent e){};
}
