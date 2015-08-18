//Title:       Names
//Version:     $Id: Z3950Constants.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
//Copyright:   Copyright (C) 2001, Knowledge Integration Ltd (See the file LICENSE for details.)
//Author:      Ian Ibbotson ( ian.ibbotson@k-int.com )
//Company:     KI
//Description: 
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

import java.math.BigInteger;

/**
 * @author Ian Ibbotson ( ian.ibbotson@k-int.com )
 * @version $Id: Z3950Constants.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
 *
 */
 

public class Z3950Constants
{  
  public static String RECSYN_SUTRS = "sutrs";
  public static String RECSYN_HTML = "html";
  public static String RECSYN_XML = "xml";
  public static String RECSYN_UKMARC = "ukmarc";
  public static String RECSYN_USMARC = "usmarc";
  public static String RECSYN_MARC21 = "marc21";
  public static String RECSYN_NORMARC = "normarc";

  public static BigInteger CLOSE_REASON_TIMEOUT = BigInteger.valueOf(100);

  public static String[] z3950_option_names = { "Search",
	                                "Present",
				        "Del Set",
					"Resource Report",
					"Trigger Resource Ctrl",
					"Resource Ctrl",
					"Access Ctrl",
					"Scan",
					"Sort",
					"Reserved",
					"Extended Services",
					"Level-1 Segmentation",
					"Level-2 Segmentation",
					"Concurrent Operations",
					"Named Result Sets",
					"Encapsulation",
					"Result Count",
					"Negotiation Model",
					"Duplicate Detection",
					"Query Type 104" };

}
