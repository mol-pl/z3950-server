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
public class BackendSearchDTO {

  public ZServerAssociation assoc;
  public Query_type query;
  public List database_names;
  public String record_syntax;
  public String small_result_element_set_name;
  public String medium_result_element_set_name;
  public String result_set_name;
  public boolean replace;
  public byte[] refid;
  public boolean search_status = true;
  public int result_set_status = 0;
  public int result_count = 0;
  public long diagnostic_code = 0;
  public String diagnostic_addinfo = null;
  public Object[] diagnostic_data = null;
  public InformationFragment[] piggyback_records = null;
  public BackendStatusReportDTO status_report = null;

  public BackendSearchDTO(ZServerAssociation assoc,
                          Query_type query,
                          List database_names,
                          String record_syntax,
                          String small_result_element_set_name,
                          String medium_result_element_set_name,
                          String result_set_name,
                          boolean replace,
                          byte[] refid) {
    this.assoc = assoc;
    this.query = query;
    this.database_names = database_names;
    this.record_syntax = record_syntax;
    this.small_result_element_set_name = small_result_element_set_name;
    this.medium_result_element_set_name = medium_result_element_set_name;
    this.result_set_name = result_set_name;
    this.replace = replace;
    this.refid = refid;
  }
}
