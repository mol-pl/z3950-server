// Title:       APDUEvent
// @version:    $Id: APDUEvent.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
// Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
// @author:     Ian Ibbotson
// Company:     Knowledge Integration Ltd
// Description: Information given to interested parties when a PDU arrives
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
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import java.util.EventObject;

public class APDUEvent extends EventObject
{
  private PDU_type pdu;
  private String reference;

  public APDUEvent(Object source)
  {
    super(source);
  }

  public APDUEvent(Object source, 
                   PDU_type thepdu)
  {
    this(source, thepdu, null);
  }

  public APDUEvent(Object source, 
                   PDU_type thepdu,
                   String reference)
  {
    super(source);
    this.pdu=thepdu;
    this.reference=reference;
  }           

  public PDU_type getPDU()
  {
    return pdu;
  }

  public String getReference()
  {
    return reference;
  }
}
