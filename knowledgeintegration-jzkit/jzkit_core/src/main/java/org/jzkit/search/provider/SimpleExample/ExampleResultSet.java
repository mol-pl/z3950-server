/*
 * Title:       ExampleSearchable
 * @version:    $Id: ExampleResultSet.java,v 1.6 2005/02/18 09:24:22 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

package org.jzkit.search.provider.SimpleExample;

import java.util.Properties;
import java.util.Observer;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ian Ibbotson
 * @version $Id: ExampleResultSet.java,v 1.6 2005/02/18 09:24:22 ibbo Exp $
 */ 
public class ExampleResultSet extends AbstractIRResultSet implements IRResultSet {

  private static Log log = LogFactory.getLog(ExampleResultSet.class);
  int num_hits = 0;

  public ExampleResultSet()
  {
    java.util.Random r = new java.util.Random();
    num_hits = r.nextInt(1000);
    log.debug("num_hits="+num_hits);
  }

  // Fragment Source methods
  public InformationFragment[] getFragment(int starting_fragment,
                                           int count,
                                           RecordFormatSpecification spec) throws IRResultSetException
  {
    InformationFragment[] result = new InformationFragment[count];
    for ( int i=0; i<count; i++ ) {
      // result[i] = new org.jzkit.search.util.RecordModel.XMLRecord("<oai-dc><title>Hello</title></oai-dc>");
      result[i] = new org.jzkit.search.util.RecordModel.InformationFragmentImpl(starting_fragment+1,
                                                                                "REPO",
                                                                                "COLL",
                                                                                null,
                                                                                "<oai-dc><title>Hello</title></oai-dc>",
                                                                                new ExplicitRecordFormatSpecification("string::F"));
    }
    return result;
  }

  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification spec,
                               IFSNotificationTarget target)
  {
    try {
      InformationFragment[] result = getFragment(starting_fragment,count,spec);
      target.notifyRecords(result);
    }
    catch ( IRResultSetException irrse ) {
      target.notifyError("bib-1",new Integer(0),"Problem obtaining result records",irrse);
    }
  }

  /** Current number of fragments available */
  public int getFragmentCount()
  {
    return num_hits;
  }

  /** The size of the result set (Estimated or known) */
  public int getRecordAvailableHWM()
  {
    return num_hits;
  }

  // public AsynchronousEnumeration elements()
  // {
  //   return null;
  // }

  /** Release all resources and shut down the object */
  public void close()
  {
  }

  public IRResultSetInfo getResultSetInfo() {
    return new IRResultSetInfo(getResultSetName(),
                               getFragmentCount(),
                               getStatus());
  }
}
