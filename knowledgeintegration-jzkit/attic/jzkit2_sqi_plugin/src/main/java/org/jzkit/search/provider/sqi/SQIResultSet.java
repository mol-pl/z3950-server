/*
 * $Log: SQIResultSet.java,v $
 * Revision 1.3  2005/06/21 10:53:08  ibbo
 * Updated
 *
 * Revision 1.2  2005/04/08 08:21:42  ibbo
 * Updated
 *
 * Revision 1.1  2005/04/08 07:56:00  ibbo
 * Added
 *
 */

package org.jzkit.search.provider.sqi;

import java.util.Properties;
import java.util.Observer;
import java.net.URL;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Title:       SQIResultSet
 * @version:    $Id: SQIResultSet.java,v 1.3 2005/06/21 10:53:08 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

public class SQIResultSet extends AbstractIRResultSet implements IRResultSet
{
  private String base_url = null;
  private int num_hits = 0;
  private static Log log = LogFactory.getLog(SQIResultSet.class);

  public SQIResultSet() {
  }

  public SQIResultSet(Observer[] observers, String base_url) {
    super(observers);
    this.base_url = base_url;
  }

  // Fragment Source methods
  public InformationFragment[] getFragment(int starting_fragment,
                                           int count,
                                           RecordFormatSpecification spec) throws IRResultSetException {
    log.debug("getFragment");

    InformationFragment[] result = null;

    try {
      result = new InformationFragment[0];
    }
    catch (  java.lang.Exception e ) {
      throw new IRResultSetException(e.toString());
    }

    log.debug("getFragment");
    return result;
  }

  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification spec,
                               IFSNotificationTarget target) {
    log.debug("asyncGetFragment");
    try {
      InformationFragment[] result = getFragment(starting_fragment,count,spec);
      target.notifyRecords(result);
    }
    catch ( IRResultSetException re ) {
      target.notifyError("SQI", new Integer(0), "No reason", re);
    }
    log.debug("asyncGetFragment");
  }

  /** Current number of fragments available */
  public int getFragmentCount() {
    return num_hits;
  }

  /** The size of the result set (Estimated or known) */
  public int getRecordAvailableHWM() {
    return num_hits;
  }

  /** Release all resources and shut down the object */
  public void close() {
  }

  public void setFragmentCount(int i) {
    log.debug("setFragmentCount");
    num_hits = i;
    IREvent e = new IREvent(IREvent.FRAGMENT_COUNT_CHANGE, new Integer(i));
    setChanged();
    notifyObservers(e);
    log.debug("setFragmentCount");
  }

  public IRResultSetInfo getResultSetInfo() {
    return new IRResultSetInfo(getResultSetName(),
                               getFragmentCount(),
                               getStatus());
  }

}
