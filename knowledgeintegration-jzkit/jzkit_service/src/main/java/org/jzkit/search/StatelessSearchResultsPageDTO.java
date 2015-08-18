package org.jzkit.search;

import org.jzkit.search.util.QueryModel.Internal.HumanReadableQueryBundle;
import org.jzkit.search.util.RecordModel.InformationFragment;
import org.jzkit.search.util.ResultSet.IRResultSetInfo;

/*
 * $Log: StatelessSearchResultsPageDTO.java,v $
 * Revision 1.6  2005/09/20 10:10:22  ibbo
 * Updated
 *
 * Revision 1.5  2005/09/13 17:10:07  ibbo
 * All good
 *
 * Revision 1.4  2005/09/13 15:22:24  ibbo
 * Harmonise all tasks
 *
 * Revision 1.3  2005/08/23 13:02:05  ibbo
 * Initial impl of stateless search api
 *
 * Revision 1.2  2005/08/22 08:48:44  ibbo
 * More work on stateless search session
 *
 * Revision 1.1  2005/08/14 08:44:07  ibbo
 * Added stateless search api
 *
 */

/**
 * Title:       StatelessSearchResultsPageDTO
 * @version:    $Id: StatelessSearchResultsPageDTO.java,v 1.6 2005/09/20 10:10:22 ibbo Exp $
 * Copyright:   Copyright 2003-2005 Ian Ibbotson, Knowledge Integration Ltd
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Description:
 */
public class StatelessSearchResultsPageDTO implements java.io.Serializable {

  // Standard response elements
  public int first_record_position;
  public int number_of_records;
  public String result_set_id;
  public long result_set_idle_time;
  public String query_type;
  public String query_string;
  public InformationFragment[] records;
  public HumanReadableQueryBundle human_readable_bundle;
  public String human_readable_query;

  // JZkit Extensions
  public int total_hit_count;
  public int cached_hit_count;
  public int search_status;
  public String human_readable_string;

  public IRResultSetInfo result_set_info;

  public StatelessSearchResultsPageDTO(String result_set_id,
                                       int status,
                                       int fragment_count,
                                       int first_hit,
                                       int num_hits,
                                       String message,
                                       InformationFragment[] records,
                                       IRResultSetInfo result_set_info) {
    this.result_set_id = result_set_id;
    this.search_status = status;
    this.first_record_position = first_hit;
    this.number_of_records = fragment_count;
    this.total_hit_count = num_hits;
    this.records = records;
    this.result_set_info = result_set_info;
  }

  public int getStartingRecord() {
    return first_record_position;
  }

  public int getSearchStatus() {
    return search_status;
  }

  public int getRecordCount() {
    return total_hit_count;
  }

  public InformationFragment[] getRecords() {
    return records;
  }

  public int getNumRecordsReturned() {
    return number_of_records;
  }

  public void setHumanReadableQueryBundle(HumanReadableQueryBundle human_readable_bundle) {
    this.human_readable_bundle = human_readable_bundle;
    this.human_readable_query = human_readable_bundle.getHumanReadableQuery();
  }
  
  public void setHumanReadableQuery(String human_readable_query) 
  {
	    this.human_readable_query = human_readable_query;
	  }

  public String getHumanReadableQuery() {
    return human_readable_query;
  }
  
  public HumanReadableQueryBundle getHumanReadableQueryBundle()
  {
	  return human_readable_bundle;
  }

  public void setQueryType(String query_type) {
    this.query_type = query_type;
  }

  public String getQueryType() {
    return query_type;
  }

  public void setQueryString(String query_string) {
    this.query_string = query_string;
  }

  public String getQueryString() {
    return query_string;
  }
}
