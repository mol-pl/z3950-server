// Title:       ObservableDelagate
// @version:    $Id: ObservableDelagate.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
// Copyright:   Copyright (C) 2001 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ibbo@k-int.com)
// Company:     KI
// Description: We want to use the standard Observable object as a delagate
//              to notify interested parties about changes to a number of
//              key variables, but don't want to use Observable as a superclass.
//              Because setChanged is protected, the owning object cant call
//              setChanged to cause the notification messages to be sent.
//              This class simply provides public setChanged and clearChanged methods.
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

import java.util.Observable;

public class ObservableDelagate extends Observable
{
  public void setChanged()
  {
    super.setChanged();
  }

  public void clearChanged()
  {
    super.clearChanged();
  }
}
