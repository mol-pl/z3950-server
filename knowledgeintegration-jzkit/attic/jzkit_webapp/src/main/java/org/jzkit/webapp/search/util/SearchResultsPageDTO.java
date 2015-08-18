package com.k_int.colws.search.util;

import java.util.Collection;
import org.jzkit.search.util.RecordModel.InformationFragment;

public final class SearchResultsPageDTO {

  private int search_status;
  private int record_count;
  private int starting_record;
  private int num_records_returned;
  private int pageno;
  private String status_message;
  private InformationFragment[] records;

  public SearchResultsPageDTO(int pageno,
                              int search_status,
                              int record_count,
                              int starting_record,
                              int num_records_returned,
                              String status_message,
                              InformationFragment[] records) {
    this.pageno = pageno;
    this.search_status=search_status;
    this.record_count=record_count;
    this.starting_record=starting_record;
    this.num_records_returned=num_records_returned;
    this.status_message=status_message;
    this.records=records;
  }

  public int getSearchStatus() {
    return search_status;
  }

  public int getRecordCount() {
    return record_count;
  }

  public int getStartingRecord() {
    return starting_record;
  }

  public int getNumRecordsReturned() {
    return num_records_returned;
  }

  public InformationFragment[] getRecords() {
    return records;
  }

  public String getStatusMessage() {
    return status_message;
  }

  public int getPageNo() {
    return pageno;
  }
}
