/**
 * Title:       BackendSearchResult
 * @version:    $Id: BackendSearchResult.java,v 1.2 2004/11/18 12:37:45 ibbo Exp $
 * Copyright:   Copyright (C) 2001 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     KI
 * Description: 
 */

/*
 * $log$
 */

package org.jzkit.z3950.server;
                                                                                                                                          
import org.jzkit.search.util.RecordModel.InformationFragment;
import java.util.*;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;

/**
 * The data package associated with a z39.50 search request and response. Follows the
 * lifecycle of a search request and response.
 */
public class BackendStatusReportDTO {
  public String source_id = null;
  public boolean search_status = true;
  public int result_set_status = 0;
  public int result_count = 0;
  public String status_string;
  public List<BackendStatusReportDTO> child_reports;

  public String toString() {
    java.io.StringWriter sw = new java.io.StringWriter();
    sw.write("{source_id:");
    sw.write(source_id);
    // sw.write(",");
    // sw.write("status:"+search_status);
    // sw.write(",");
    // sw.write(""+result_set_status);
    // sw.write(",count:");
    // sw.write(""+result_count);
    sw.write(",msg:");
    sw.write(status_string);
    if ( ( child_reports != null ) && ( child_reports.size() > 0 ) ) {
      sw.write(",");
      for ( java.util.Iterator i = child_reports.iterator(); i.hasNext(); ) {
        sw.write(i.next().toString());
      }
    }
    sw.write("}");

    return sw.toString();
  }
}
