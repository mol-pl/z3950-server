/**
 *  Title:       IRStatusReport
 * @version:    $Id: IRStatusReport.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2001 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
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

import java.util.Vector;
import java.util.Enumeration;
import org.w3c.dom.*;

public class IRStatusReport
{
  private static final String KISR_NS = "http://www.k-int.com/schemas/StatusReport";
  public String source_short_name;
  public String source_long_name;
  public String source_id;
  public String status;

  public int num_hits;
  public int current_hwm;

  public Vector recent_messages;

  public IRStatusReport[] child_reports;

  public IRStatusReport(String sn, 
		        String ln, 
			String id, 
			String status,
			int nh, 
			int ch,
			IRStatusReport[] cr,
			Vector recent_messages)
  {
    this.source_short_name = sn;
    this.source_long_name = ln;
    this.source_id = id;
    this.num_hits = nh;
    this.current_hwm = ch;
    this.child_reports = cr;
    this.status=status;
    this.recent_messages=recent_messages;
  }

  public String getSourceShortName()
  {
    return source_short_name;
  }

  public String getSourceLongName()
  {
    return source_long_name;
  }

  public String getSourceId()
  {
    return source_id;
  }

  public int getEstTotalHits()
  {
    return num_hits;
  }

  public int getCurrentSize()
  {
    return current_hwm;
  }

  public String getStatus()
  {
    return status;
  }

  public IRStatusReport[] getChildReports()
  {
    return child_reports;
  }

  public String toString()
  {
    java.io.StringWriter sw = new java.io.StringWriter();

    sw.write("Status report for ");
    sw.write(source_long_name);
    sw.write("( ");
    sw.write(source_id);
    sw.write(" ).\t Source status is ");
    sw.write(""+status);
    sw.write(" and currently holds ");
    sw.write(""+current_hwm);
    sw.write("out of ");
    sw.write(""+num_hits);
    sw.write("records");
    if ( child_reports == null )
    {
      sw.write(".\n");
    }
    else
    {
      sw.write(" and is aggregating data from ");
      sw.write(""+(child_reports.length));
      sw.write(" services \n{");
      for ( int i=0; i<child_reports.length; i++ )
      {
        sw.write(child_reports[i].toString());
      }
      sw.write("}.\n");
    }

    return sw.toString();
  }

  public Element toXMLNode(Document parent_doc)
  {
    Element status_report_node = parent_doc.createElementNS(KISR_NS,"KISR:StatusReport");

    status_report_node.setAttribute("SourceLongName",source_long_name);
    status_report_node.setAttribute("SourceID",source_id);
    status_report_node.setAttribute("Status",""+status);
    status_report_node.setAttribute("Estimate",""+current_hwm);
    status_report_node.setAttribute("Available",""+num_hits);

    for ( Enumeration e = recent_messages.elements(); e.hasMoreElements(); )
    {
      Object o = e.nextElement();
      Element message = parent_doc.createElement("Message");
      message.appendChild(parent_doc.createTextNode(o.toString()));
      status_report_node.appendChild(message);
    }

    if ( child_reports != null )
    {
      Element child_reports_node = parent_doc.createElement("ChildReports");
      for ( int i=0; i<child_reports.length; i++ )
      {
        child_reports_node.appendChild(child_reports[i].toXMLNode(parent_doc));
      }
      status_report_node.appendChild(child_reports_node);
    }

    return status_report_node;
  }

  public Vector getRecentMessages()
  {
    return recent_messages;
  }
}
