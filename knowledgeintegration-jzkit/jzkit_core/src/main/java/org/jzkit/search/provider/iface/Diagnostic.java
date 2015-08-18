/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * published by the Free Software Foundation; either version 2.1 of
 * the license, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite
 * 330, Boston, MA  02111-1307, USA.
 * 
 */
 
package org.jzkit.search.provider.iface;

/*
 * Title:       Diagnostic
 * @version:    $Id: Diagnostic.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Rob Tice (rob.tice@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: Holds the diagnostic status inforamtion;
 * 
 * Created 07-May-2003 09:06:16
 * $Log: Diagnostic.java,v $
 * Revision 1.1.1.1  2004/06/18 06:38:18  ibbo
 * Initial
 *
 * Revision 1.1.1.1  2004/03/08 15:25:38  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/12/05 16:30:44  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/11/16 15:10:42  ibbo
 * Initial import
 *
 * Revision 1.2  2003/06/03 09:53:21  rob_tice
 * Rewritten to use daignostic string as status code
 *
 * Revision 1.1  2003/05/09 12:54:44  rob_tice
 * Updated diagnostics and started removal of unused imports
 *
 * 
 */
public class Diagnostic
{	
	public String 	status_code = null;
	public String 	addinfo 	= null;
	public String	target_name = null;
	public String 	message 	= null;
	
	/**
	 * 
	 * @param status_code	Diagnostic status code
	 * @param target_name	Target name
	 * @param addinfo		Any additional information
	 */
	//public Diagnostic(int type, int status_code, String target_name, String addinfo)
	public Diagnostic(String status_code, String target_name, String addinfo)
	{
		this.status_code 	= status_code;
		this.addinfo 		= addinfo;
		this.target_name	= target_name;
	}
	/**
	 * 
	 * @return returns the integer value of the diagnostic code e.g. 1 for diag.bib1.1
	 * 				or null if the code doesn't have an integer value
	 */
	public Integer getIntegerDiagnosticCode() 
	{
		int last_period_pos = status_code.lastIndexOf(".");	
		String int_code 	= status_code.substring(last_period_pos+1);	
		Integer retval 		= null;
		try
		{	
			retval = new Integer(int_code);
		}
		catch(NumberFormatException e)
		{;}
		return retval;
	}
}
