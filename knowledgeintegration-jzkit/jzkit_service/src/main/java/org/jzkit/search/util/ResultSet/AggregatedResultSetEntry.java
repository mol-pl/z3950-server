package org.jzkit.search.util.ResultSet;

import java.util.*;

/**
 * Title:       AggregatedResultSetEntry
 * Copyright:   Copyright (C) 1999,2001,2002 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */
public class AggregatedResultSetEntry implements java.io.Serializable {

  private String source_id;
  private Long hitno_at_source;

  public AggregatedResultSetEntry(String source_id, Long hitno_at_source) {
    this.source_id = source_id;
    this.hitno_at_source = hitno_at_source;
  }

  public String getSourceId() {
    return source_id;
  }

  public Long getHitnoAtSource() {
    return hitno_at_source;
  }

}
