// Title:       APDUListener
// @version:    $Id: APDUListener.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
// Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd (See the COPYING file for details.)
// @author:     Ian Ibbotson
// Company:     Knowledge Integration Ltd
// Description: Interface implemented by classes using a ZEndpoint if they want to be notified
//              on incoming messages.
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

/**
  APDUListener : Interface implemented by objects interested in 
                 knowing what PDU's are being received.

  * @author Ian Ibbotson
  * @version $Id: APDUListener.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
  */
 

public interface APDUListener extends java.util.EventListener
{
  // Used when an unidentified PDU is recieved
  public abstract void incomingAPDU(APDUEvent e);

  // public abstract void incomingInitRequest(APDUEvent e);
  public abstract void incomingInitResponse(APDUEvent e);

  // public abstract void incomingSearchRequest(APDUEvent e);
  public abstract void incomingSearchResponse(APDUEvent e);

  // public abstract void incomingPresentRequest(APDUEvent e);
  public abstract void incomingPresentResponse(APDUEvent e);

  // public abstract void incomingDeleteResultSetRequest(APDUEvent e);
  public abstract void incomingDeleteResultSetResponse(APDUEvent e);

  public abstract void incomingAccessControlRequest(APDUEvent e);
  // public abstract void incomingAccessControlResponse(APDUEvent e);

  // public abstract void incomingResourceControlRequest(APDUEvent e);
  public abstract void incomingResourceControlResponse(APDUEvent e);

  // public abstract void incomingTriggerResourceControlRequest(APDUEvent e);

  // public abstract void incomingResourceReportRequest(APDUEvent e);
  public abstract void incomingResourceReportResponse(APDUEvent e);

  // public abstract void incomingScanRequest(APDUEvent e);
  public abstract void incomingScanResponse(APDUEvent e);

  // public abstract void incomingSortRequest(APDUEvent e);
  public abstract void incomingSortResponse(APDUEvent e);

  // public abstract void incomingSegmentRequest(APDUEvent e);

  // public abstract void incomingExtendedServicesRequest(APDUEvent e);
  public abstract void incomingExtendedServicesResponse(APDUEvent e);

  public abstract void incomingClose(APDUEvent e);
}
