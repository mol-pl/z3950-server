/**
 * Title:       BackendPresentResult
 * @version:    $Id: BackendPresentResult.java,v 1.2 2004/11/28 15:59:01 ibbo Exp $
 * Copyright:   Copyright (C) 2001 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     KI
 * Description: 
 */

/*
 * $log$
 */

package org.jzkit.z3950.server;
                                                                                                                                          
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;

public class BackendPresentDTO {

  public ZServerAssociation assoc;
  public String result_set_name;
  public int start;
  public int count;
  public int total_hits=0;
  public String record_syntax;
  public String element_set_name;
  public recordComposition_inline9_type record_composition;
  public byte[] refid;
  public ArchetypeRecordFormatSpecification archetype;
  public ExplicitRecordFormatSpecification explicit;
  public InformationFragment[] result_records;
  public int next_result_set_position;

  public BackendPresentDTO(ZServerAssociation assoc,
                           String result_set_name,
                           int start,
                           int count,
                           String record_syntax,
                           String element_set_name,
                           recordComposition_inline9_type record_composition,
                           byte[] refid,
                           ArchetypeRecordFormatSpecification archetype,
                           ExplicitRecordFormatSpecification explicit) {
    this.assoc=assoc;
    this.result_set_name = result_set_name;
    this.start = start;
    this.count = count;
    this.record_syntax = record_syntax;
    this.element_set_name = element_set_name;
    this.refid = refid;
    this.archetype = archetype;
    this.explicit = explicit;
  }
}
