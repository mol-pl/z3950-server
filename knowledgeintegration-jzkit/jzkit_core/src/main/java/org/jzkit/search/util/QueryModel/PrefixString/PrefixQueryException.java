// Title:       PrefixQueryException
// @version:    $Id: PrefixQueryException.java,v 1.1.1.1 2004/06/18 06:38:17 ibbo Exp $
// Copyright:   Copyright (C) 2001, Knowledge Integration Ltd.
// @author:     Ian Ibbotson ( ian.ibbotson@k-int.com )
// Company:     KI
// Description: Your description
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

package org.jzkit.search.util.QueryModel.PrefixString;

/**
  PrefixQueryException: Report problems with the query
  * @author Ian Ibbotson
  * @version $Id: PrefixQueryException.java,v 1.1.1.1 2004/06/18 06:38:17 ibbo Exp $
  */
 

public class PrefixQueryException extends Exception
{
  public PrefixQueryException(String e)
  {
    super(e);
  }
}
