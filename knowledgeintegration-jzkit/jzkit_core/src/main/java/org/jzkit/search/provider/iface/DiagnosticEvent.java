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
 * Title:       DiagnosticEvent
 * @version:    $Id: DiagnosticEvent.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Rob Tice (rob.tice@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description:
 * 
 * $Log: DiagnosticEvent.java,v $
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
 * Revision 1.1  2003/05/09 12:54:44  rob_tice
 * Updated diagnostics and started removal of unused imports
 *
 * Created 05-May-2003 10:53:55
 * 
 */
public class DiagnosticEvent extends IREvent
{	
	public String addinfo = null;
		
	/**
	 * 
	 * @param the_diagnostic The diagnostic object associated
	 * 							with this event
	 */
	public DiagnosticEvent(Diagnostic the_diagnostic)
	{
		super(IREvent.DIAGNOSTIC_EVENT, the_diagnostic);
	}
}
