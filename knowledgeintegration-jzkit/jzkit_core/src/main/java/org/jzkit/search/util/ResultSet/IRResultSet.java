/**
 * Title:       IRResultSet
 * @version:    $Id: IRResultSet.java,v 1.5 2005/02/18 08:07:30 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2001,2002 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

/*
 * $Log: IRResultSet.java,v $
 * Revision 1.5  2005/02/18 08:07:30  ibbo
 * Updated
 *
 * Revision 1.4  2004/09/30 14:45:18  ibbo
 * Made RecordFormatSpecification base class for new Explicit and Archetype variants.
 *
 * Revision 1.3  2004/09/24 14:11:45  ibbo
 * Updated
 *
 * Revision 1.2  2004/09/14 12:35:26  ibbo
 * Fixed z3950 unit tests
 *
 * Revision 1.1.1.1  2004/06/18 06:38:18  ibbo
 * Initial
 *
 * Revision 1.1.1.1  2004/03/08 15:25:38  ibbo
 * Initial Import
 *
 * Revision 1.2  2004/02/07 17:42:51  ibbo
 * Updated
 *
 * Revision 1.1.1.1  2003/12/05 16:30:43  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/11/16 15:10:43  ibbo
 * Initial import
 *
 */

package org.jzkit.search.util.ResultSet;

import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.provider.iface.Condition;
import java.util.Observer;


public interface IRResultSet {

  /*
   * It is *Essential* that the default_record_syntax and default_element_set_name properties
   * are set by the Searchable init method for this part of the framework to hang together properly!
  public static final ExplicitRecordFormatSpecification default_spec = 
              new ExplicitRecordFormatSpecification( new IndirectFormatProperty("defaultRecordSyntax"),
                                             null,
                                             new IndirectFormatProperty("DefaultElementSetName"));
   */

  /** Position based range access to the result set. Implementation must
    * be 0 based: IE, First record in result set is 0 not 1.
    */
  public InformationFragment[] getFragment(int starting_fragment, 
		                           int count,
					   RecordFormatSpecification spec) throws IRResultSetException;

  /**
   * Accept a request to fetch a record in the background and then notify the target when we have
   * that fragment. If we make this return a boolean, we can say if the call completed synchronously or not.
   */
  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification spec,
                               IFSNotificationTarget target) throws IRResultSetException;

  /** The size of the result set (Estimated or known) */
  public int getFragmentCount();

  /** Current number of fragments physically available */
  public int getRecordAvailableHWM();

  /** Get the result set status */
  public int getStatus();

  /** A shortcut method that simply does waitForCondition(StatusFlagCondition(status),timeout) */
  public boolean waitForStatus(int status, long timeout) throws IRResultSetException;

  /** Wait unti the specified condition is met, returning true if so, or false at timeout */
  public boolean waitForCondition(Condition condition, long timeout) throws IRResultSetException;
 
  /** Release all resources and shut down the object */
  public void close();

  public void addObserver(Observer o);

  public void requestStatusNotification();

  public String getSetID();

  public void setResultSetName(String result_set_name);

  public String getResultSetName();

  public IRResultSetInfo getResultSetInfo();

  public void postMessage(String message);
}
