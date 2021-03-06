// Title:       FragmentTransformationException
// @version:    $Id: FragmentTransformationException.java,v 1.2 2004/10/26 15:30:52 ibbo Exp $
// Copyright:   Copyright (C) 1999-2003 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ibbo@k-int.com)
// Company:     Knowledge Integration Ltd.
// Description: 

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


package org.jzkit.search.util.RecordConversion;

import java.util.Hashtable;

/**
 *
 */
public class FragmentTransformationException extends Exception
{
  public FragmentTransformationException(String reason) {
    super(reason);
  }

  public FragmentTransformationException(String reason, Throwable cause) {
    super(reason,cause);
  }
}
