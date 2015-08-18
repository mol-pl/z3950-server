/*
 * $Log: TransformingIRResultSet.java,v $
 * Revision 1.2  2005/10/04 16:32:44  ibbo
 * added maps
 *
 * Revision 1.1  2004/10/04 11:22:19  ibbo
 * Updated
 *
 */

package org.jzkit.search.util.ResultSet;

import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.provider.iface.Condition;
import java.util.Observer;
import java.util.Map;


/**
 * Title:       TransformingIRResultSet
 * @version:    $Id: TransformingIRResultSet.java,v 1.2 2005/10/04 16:32:44 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2001,2002 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: An extension of ResultSet that recognises the difference between what
 *              we can ask a search service to supply and the format that we need the 
 *              records in. Specifically, the request_spec will normally be a record
 *              archetype such as "FullDisplay" or "Brief" or "Holdings".. IE something that
 *              will vary with each target. transform_spec defines the format that we
 *              actually need returned from records, for example text/html/full.
 */
public interface TransformingIRResultSet extends IRResultSet {

  /** Position based range access to the result set. Implementation must
    * be 0 based: IE, First record in result set is 0 not 1.
    */
  public InformationFragment[] getFragment(int starting_fragment, 
		                           int count,
					   RecordFormatSpecification request_spec,
                                           ExplicitRecordFormatSpecification transform_spec,
                                           Map additional_properties) throws IRResultSetException;

  /** Position based range access to the result set. Implementation must
    * be 0 based: IE, First record in result set is 0 not 1.
    */
  public InformationFragment[] getFragment(int starting_fragment,
                                           int count,
                                           RecordFormatSpecification request_spec,
                                           ExplicitRecordFormatSpecification transform_spec) throws IRResultSetException;

  /**
   * Accept a request to fetch a record in the background and then notify the target when we have
   * that fragment. If we make this return a boolean, we can say if the call completed synchronously or not.
   */
  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification request,
                               ExplicitRecordFormatSpecification transformation_spec,
                               IFSNotificationTarget target,
                               Map additional_properties) throws IRResultSetException;

  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification request,
                               ExplicitRecordFormatSpecification transformation_spec,
                               IFSNotificationTarget target) throws IRResultSetException;

}
