//Title:       PDUTypeAvaialableSemaphore
//Version:     $Id: PDUTypeAvailableSemaphore.java,v 1.2 2004/08/17 15:30:35 ibbo Exp $
//Copyright:   Copyright (C) 2001, Knowledge Integration Ltd (See the file LICENSE for details.)
//Author:      Ian Ibbotson ( ian.ibbotson@k-int.com )
//Company:     KI
//Description: Wait for a specific TYPE of PDU for example, an INIT response...
//             
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
 * @author Ian Ibbotson ( ian.ibbotson@k-int.com )
 * @version $Id: PDUTypeAvailableSemaphore.java,v 1.2 2004/08/17 15:30:35 ibbo Exp $
 *
 */
 

public class PDUTypeAvailableSemaphore extends BaseSemaphore
{
  public PDU_type the_pdu = null;
  public int sought_type = -1;
  private Observable pdu_source = null;
  private boolean got_pdu = false;

  // private transient static Log cat = LogFactory.getLog( PDUTypeAvailableSemaphore.class);
                                                                                                                                          



  Observer o =  new Observer()
                {
                  public void update(Observable o, Object arg)
                  {
                    APDUEvent e = (APDUEvent)arg;

                    // cat.debug("ReferencedPDUAvaialableSemaphore::update(...)");
                    // cat.debug("Sought type is : "+sought_type+", update type is "+e.getPDU().which);

                    if ( ( e != null ) && 
                         ( e.getPDU() != null ) && 
                         ( e.getPDU().which == sought_type ) )
                    {
                      the_pdu = e.getPDU();   
                      got_pdu = true;
                      doNotify();
                    }
                    else
                    {
                        // cat.debug("No match");
                    }
                  }
                };

  public PDUTypeAvailableSemaphore(int type, Observable pdu_source)
  {
    super();
    this.sought_type = type;
    this.pdu_source = pdu_source;
    pdu_source.addObserver(o);
  }

  public void destroy()
  {
    pdu_source.deleteObserver(o);
    o=null;
  }

  protected void finalize()
  {
    // cat.debug("PDUTypeAvaialableSemaphore::finalize()");
  }

  public boolean isConditionMet()
  {
    return got_pdu;
  }
}
