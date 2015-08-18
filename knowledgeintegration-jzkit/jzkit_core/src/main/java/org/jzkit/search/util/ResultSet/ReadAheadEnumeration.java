/**
 * Title:       ReadAheadEnumeration
 * @version:    $Id: ReadAheadEnumeration.java,v 1.9 2005/10/27 16:23:13 ibbo Exp $
 * Copyright:   Copyright (C) 2001 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2.1 of
// the license, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite
// 330, Boston, MA  02111-1307, USA.
// 

package org.jzkit.search.util.ResultSet;

import org.jzkit.search.util.RecordModel.*;
import java.lang.ref.*;
import org.jzkit.search.util.ResultSet.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Enumerate the result records from an IRResultSet.
 * As normal FragmentSourceEnumeration, but caches chunks of records
 * as presented by target. E.G. Fetch chunk_size rows, getNext returns 1,2,3,4...
 * on 11, calls fetch again. And don't I wish I called it iterator instead....
 *
 * @version:    $Id: ReadAheadEnumeration.java,v 1.9 2005/10/27 16:23:13 ibbo Exp $
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * @see:	org.jzkit.SearchProvider.iface.FragmentSourceEnumeration
 * @see:	org.jzkit.SearchProvider.iface.AbstractIRResultSet
 *
 */
public class ReadAheadEnumeration implements AsynchronousEnumeration
{
  private IRResultSet source = null;
  private int current_element = 0;  // 0 is before first element...
  private int min_cache_size = 5;
  private java.util.LinkedList fragment_cache = new java.util.LinkedList();
  private boolean fetching = false;
  private int chunk_size = -1;
  private RecordFormatSpecification spec = null;
  private int next_to_request = 1;
  private int next_record_timeout = 50000;
  private WeakReference notification_target = null;

  private static Log log = LogFactory.getLog(ReadAheadEnumeration.class);

  IFSNotificationTarget rae_present_callback_handler = new IFSNotificationTarget()
  {
    public void notifyRecords(InformationFragment[] records) {
      if ( records != null ) {
        log.debug("ReadAheadEnumeration::IFSNotificationTarget::records:"+records.length);

        next_to_request += records.length;

        synchronized ( fragment_cache ) {
          for ( int i=0; i<records.length; i++ ) {
            if ( records[i] != null ) {
              fragment_cache.addLast(records[i]);
            }
            else {
              log.warn("**source "+source.hashCode()+" returned a null information fragment**");
            }
          }
          fetching = false;
          fragment_cache.notifyAll();
        }

        checkCache();

        // Notify anyone waiting on this asynchronous enumeration that new records are available.
        if ( notification_target != null ) {
          Object o = notification_target.get();
          if ( o != null ) {
            synchronized(o) {
              o.notifyAll();
            }
          }
        }
      }
      else {
      }
    }

    public void notifyError(   String code_set,  // For z3950 read diagnostic set
                              Integer code,      // For z3950 read diagnostic
                               String reason,
                            Throwable source_exception) {
      log.warn("Error notification "+code_set+" "+code+" "+reason,source_exception);
      if ( source.getStatus() != IRResultSetStatus.FAILURE ) {
        fetching = false;
        checkCache();
        log.warn("source returned a null information fragment");
      }
      else {
        synchronized(fragment_cache) {
          fragment_cache.notifyAll();
        }
      }
    }
  };

  private ReadAheadEnumeration() {
  }

  public ReadAheadEnumeration(IRResultSet source) {
    this(source, 
         new ExplicitRecordFormatSpecification(new IndirectFormatProperty("defaultRecordSyntax"),
                                               new IndirectFormatProperty("defaultRecordSchema"),
                                               new IndirectFormatProperty("defaultElementSetName")), 
         10,
         100000,
         null);
  }

  public ReadAheadEnumeration(IRResultSet source, int chunk_size)
  {
    this(source,
         new ExplicitRecordFormatSpecification(new IndirectFormatProperty("defaultRecordSyntax"),
                                               new IndirectFormatProperty("defaultRecordSchema"),
                                               new IndirectFormatProperty("defaultElementSetName")), 
         chunk_size,
         100000,
         null);
  }

  public ReadAheadEnumeration(IRResultSet source,
		              RecordFormatSpecification spec,
                              Object notification_target) {
    this(source, spec, 10, 100000, notification_target);
  }

  public ReadAheadEnumeration(IRResultSet source,
		              RecordFormatSpecification spec) {
    this(source, spec, 10, 100000, null);
  }

  public ReadAheadEnumeration(IRResultSet source, 
		              RecordFormatSpecification spec,
			      int chunk_size,
                              int next_record_timeout,
                              Object notification_target) {
    this.chunk_size = chunk_size;
    this.source = source;
    this.spec = spec;
    this.next_record_timeout = next_record_timeout;

    if ( notification_target != null )
      this.notification_target = new WeakReference(notification_target);

    log.debug("ReadAheadEumeration::ReadAheadEumeration(source,"+chunk_size+","+spec+")");
  }

  public boolean hasMoreElements() {
    // log.fine("ReadAheadEumeration::hasMoreElements()");
    if ( (current_element) < source.getFragmentCount() )
      return true;
    return false;
  }

  public Object nextElement() {
    // log.fine("ReadAheadEumeration::nextElement()");
    Object retval = null;

    // log.fine("ReadAheadEumeration::nextElement() - check cache");
    checkCache();

    if ( fragment_cache.size() == 0 )
    {
      // log.fine("ReadAheadEumeration::nextElement() - cache empty.. waiting");
      waitForNextRecord(next_record_timeout);
    }

    // log.fine("ReadAheadEumeration::nextElement() - Return result");
    if ( fragment_cache.size() > 0 )
      retval = fragment_cache.removeFirst();

    if ( retval != null )
      current_element++;

    return retval;
  }

  private void checkCache() {
    if ( ( fragment_cache.size() < min_cache_size ) && ( source.getStatus() != IRResultSetStatus.FAILURE ) ) {
      if ( !fetching ) {
        // log.fine("Checking cache in ReadAheadEnumeration, current_element="+current_element+
        //             " cache.size()="+fragment_cache.size()+" total="+source.getFragmentCount() );

        // Firstly, check that the cache does not represent the last items in the
        // result set
        if ( current_element + fragment_cache.size() < source.getFragmentCount() )
        {
          // Is the cache as big as we would like?
          // Nope, we need to fetch the next chunk of records
          // It's possible that this should use a thread pool when it looks like
          // we might be approaching the cache limit... Need a semaphore to block
          // if we ask for the next record whilst the threaded fetch is still in process.
          // log.fine("There are still items outstanding....");
          try {
            requestRecords();
          }
          catch ( IRResultSetException irrse ) {
            log.warn("Problem with result set",irrse);
          }
        }
      }
      // else
      // {
      //   log.fine("Not currently fetching");
      // }
    }
    else {
      // Cache is fine... or source in error state
    }
  }

  private synchronized void requestRecords() throws IRResultSetException {

    if ( source.getStatus() != IRResultSetStatus.FAILURE ) {
      fetching = true;
      int records_available = source.getFragmentCount();

      int num_to_request = 0;

      if ( ( next_to_request + chunk_size ) > records_available )
        num_to_request = records_available - next_to_request + 1;
      else
        num_to_request =  chunk_size;

      log.debug("ReadAheadEumeration::fetchRecords()");
      log.debug("Requesting "+num_to_request+" records, current="+current_element+" max="+records_available+" next to request="+next_to_request);

      // Don't forget getFragment is 1-based!
      source.asyncGetFragment(next_to_request,num_to_request,spec,rae_present_callback_handler); 
    }
  }

  public boolean nextIsAvailable() {
    // log.debug("ReadAheadEumeration::nextIsAvailable() - cache-size="+fragment_cache.size());
    checkCache();
    if ( fragment_cache.size() > 0 )
      return true;
    return false;
  }

  public void waitForNextRecord(int timeout) {
    // log.fine("Waiting for up to "+timeout+" ms for next record");

    switch ( timeout ) {
      case -1:
      {
        while ( fragment_cache.size() == 0 ) {
          synchronized( fragment_cache ) {
            try {
              fragment_cache.wait();
            }
            catch ( InterruptedException ie ) {
            }
          }
        }
        break;
      }
      case 0: {
        break;
      }
      default: {
        long end_time = System.currentTimeMillis() + timeout;

        while ( ( System.currentTimeMillis() < end_time ) &&
                ( fragment_cache.size() == 0 ) && 
                (  source.getStatus() != IRResultSetStatus.FAILURE ) ) {
          long wait_time = end_time - System.currentTimeMillis();

          if ( wait_time > 0 ) {
            synchronized( fragment_cache ) {
              try {
                fragment_cache.wait(wait_time);
              }
              catch ( InterruptedException ie ) {
              }
            }
          }
        }
      }
    }
  }

  public void registerNotificationTarget(Object o) {
    notification_target = new WeakReference(o);
  }

  protected void finalize() {
  }
}
