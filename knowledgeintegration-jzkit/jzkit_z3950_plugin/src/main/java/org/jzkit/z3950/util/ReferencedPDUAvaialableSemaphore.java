//Title:       ReferencedPDUAvaialableSemaphore
//Version:     $Id: ReferencedPDUAvaialableSemaphore.java,v 1.3 2005/10/26 11:22:24 ibbo Exp $
//Copyright:   Copyright (C) 2001, Knowledge Integration Ltd (See the file LICENSE for details.)
//Author:      Ian Ibbotson ( ian.ibbotson@k-int.com )
//Company:     KI
//Description: A Semaphore object that waits until a PDU with the specified reference
//             to become available or a timeout takes place.
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
 * @version $Id: ReferencedPDUAvaialableSemaphore.java,v 1.3 2005/10/26 11:22:24 ibbo Exp $
 *
 * Wait on the given observer until a PDU with the given refid is available 
 * or a timeout has passed.
 *
 */
public class ReferencedPDUAvaialableSemaphore extends BaseSemaphore {

  public PDU_type the_pdu = null;
  public String sought_refid = null;
  private Observable pdu_source = null;
  private boolean got_pdu = false;

  Observer o =  new Observer() {
    public void update(Observable o, Object arg) {
      APDUEvent e = (APDUEvent)arg;
      String ref = e.getReference();
      // cat.debug("ReferencedPDUAvaialableSemaphore::update(...) refid=\""+ref+"\" sought=\""+sought_refid+"\" source="+e.getSource().hashCode());
      if ( ( ( sought_refid==null ) && ( ref==null ) ) ||
           ( ( ( e != null ) && ( ref != null ) && ( ref.equals(sought_refid) ) ) ) ) {
        the_pdu = e.getPDU();   
        got_pdu = true;
        doNotify();
      }
      // else
      //   cat.debug("No match on refid "+ref+" compared with sought refid "+sought_refid);
    }
  };

  public ReferencedPDUAvaialableSemaphore(String refid, Observable pdu_source) {
    super();
    this.sought_refid = refid;
    this.pdu_source = pdu_source;
    pdu_source.addObserver(o);
  }

  public void destroy() {
    pdu_source.deleteObserver(o);
    o=null;
  }

  protected void finalize() {
  }

  public boolean isConditionMet() {
    return got_pdu;
  }
}
