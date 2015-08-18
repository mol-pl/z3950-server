// Title:       
// @version:    $Id: Z3950Main.java,v 1.3 2004/09/16 17:06:31 ibbo Exp $
// Copyright:   Copyright (C) 2001 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ibbo@k-int.com)
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
     

/**
 * Z3950Main
 *
 * @author Ian Ibbotson
 * @version $Id: Z3950Main.java,v 1.3 2004/09/16 17:06:31 ibbo Exp $
 * 
 */

package org.jzkit.z3950.server;

public class Z3950Main
{
  public static void main(String[] args)
  {
    Z3950Listener l = new Z3950Listener();
    l.setPort(9999);
    l.setBackendBeanName("Z3950Listener");
    l.start();
  }
}      
