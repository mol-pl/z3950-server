/**
 * Title:       Scanable
 * @version:    $Id: Scanable.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: Interface that indicates an object implements scan
 *             
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

package org.jzkit.search.provider.iface;


/**
 * Scanable. The object implementing this interface is capable of providing a
 * service that allows browsing of terms in indexes.
 *
 * @author Ian Ibbotson (ian.ibbotson@k-int.com)
 * @version $Id: Scanable.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 *  $Log: Scanable.java,v $
 *  Revision 1.1.1.1  2004/06/18 06:38:18  ibbo
 *  Initial
 *
 *  Revision 1.1.1.1  2004/03/08 15:25:38  ibbo
 *  Initial Import
 *
 *  Revision 1.1.1.1  2003/12/05 16:30:44  ibbo
 *  Initial Import
 *
 *  Revision 1.1.1.1  2003/11/16 15:10:42  ibbo
 *  Initial import
 *
 *  Revision 1.9  2003/06/17 18:15:34  rob_tice
 *  doScan chaged to allow the possibility of an exception - rob
 *
 */ 
public interface Scanable
{
  public boolean isScanSupported();

  /** doScan MUST return a vector of TermInformation 
   * @see TermInformation
   */
  public ScanInformation doScan(ScanRequestInfo req) throws ScanException;
}
