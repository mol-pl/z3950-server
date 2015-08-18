/**
 * Title:       ExampleSearchable
 * @version:    $Id: SRWResultSet.java,v 1.7 2005/02/18 09:24:22 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

/*
 * $Log: SRWResultSet.java,v $
 * Revision 1.7  2005/02/18 09:24:22  ibbo
 * Added getResultSet info to all RS impls
 *
 * Revision 1.6  2004/10/05 18:37:43  ibbo
 * Updated
 *
 * Revision 1.4  2004/09/30 15:35:02  ibbo
 * Completed migration to new RecordFormatSpecification structure
 *
 * Revision 1.3  2004/09/24 16:46:21  ibbo
 * All final result set objects now implement IRResultSet directly instead of
 * inheriting the implementation from the Abstract base class.. This seems to
 * work better for trmi
 *
 * Revision 1.2  2004/08/24 20:23:42  ibbo
 * Updated to new wsdl
 *
 * Revision 1.1.1.1  2004/06/18 06:38:40  ibbo
 * Initial
 *
 * Revision 1.1.1.1  2004/03/08 15:25:38  ibbo
 * Initial Import
 *
 * Revision 1.2  2004/02/07 17:42:51  ibbo
 * Updated
 *
 * Revision 1.1.1.1  2003/12/05 16:30:44  ibbo
 * Initial Import
 *
 * Revision 1.5  2003/11/23 14:41:16  ibbo
 * Improved SRW hadling
 *
 * Revision 1.4  2003/11/23 12:29:49  ibbo
 * Updated
 *
 * Revision 1.3  2003/11/22 10:56:50  ibbo
 * Updated
 *
 * Revision 1.2  2003/11/16 16:02:36  ibbo
 * Imported SRW fragments
 *
 * Revision 1.1.1.1  2003/11/16 15:10:42  ibbo
 * Initial import
 *
 */

package org.jzkit.search.provider.SRW;

import java.util.Properties;
import java.util.Observer;
import java.net.URL;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
import java.util.logging.*;

import org.jzkit.search.provider.zing.*;
import org.jzkit.search.provider.zing.interfaces.*;
import org.jzkit.search.provider.zing.srw.bindings.*;

/**
 * @author Ian Ibbotson
 * @version $Id: SRWResultSet.java,v 1.7 2005/02/18 09:24:22 ibbo Exp $
 */ 
public class SRWResultSet extends AbstractIRResultSet implements IRResultSet
{
  private Logger log = Logger.getLogger(SRWResultSet.class.getName());
  private int num_hits = 0;
  private SRWPort srw_port = null;
  private String cql_string;
  private String repos_id = null;

  public SRWResultSet(String base_url, String cql_string, String repos_id) {
    this(null,base_url,cql_string,repos_id);
  }

  public SRWResultSet(Observer[] observers, String base_url, String cql_string, String repos_id) {

    super(observers);

    System.err.println("New CQL String : "+cql_string);

    this.cql_string = cql_string;
    this.repos_id = repos_id;
    try
    {
      setup(base_url);
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  }

  private void setup(String base_url) throws java.net.MalformedURLException, 
                                             javax.xml.rpc.ServiceException, 
                                             java.rmi.RemoteException
  {
    log.entering(SRWResultSet.class.getName(),"setup",base_url);

    URL url = new URL(base_url);
    // URL url = new URL("http://localhost:8080/axis/services/SearchRetrieveService");
    // URL url = new URL("http://alcme.oclc.org/axis/SOAR/services/SearchRetrieveService");
    // URL url = new URL("http://132.174.1.113/axis/SOAR/services/SearchRetrieveService");
    log.fine("Created URL");
    // SRWSoapBindingStub stub = new SRWSoapBindingStub(url, new org.apache.axis.client.Service());
    // SRWPort stub = new SRWSoapBindingStub(url, new org.apache.axis.client.Service());
    // srw_port = new SRWServiceLocator().getSearchRetrieveService(url);
    // srw_port = new SRWServiceLocator().getSRWSRWPort(url);
    srw_port = new SRWSoapBindingStub(url, new org.apache.axis.client.Service());
    log.fine("Got Stub");

    requestRecords(1,0);  // Do the search but don't request any records
    setFragmentCount(num_hits);
    System.err.println("Result num hits : "+num_hits);
  }

  private RecordType[] requestRecords(int first_rec, int max_records) throws java.rmi.RemoteException
  {
    RecordType[] records = null;
    SearchRetrieveRequestType req = new SearchRetrieveRequestType();
    String packing = "xml";
    // req.setQuery("dc.title=science");
    System.err.println("requestRecords qry="+cql_string+", first="+first_rec+", max="+max_records+", packing="+packing);
    req.setQuery(cql_string);

    // If this parameter is left out. things go haywire.. we need to test this out...
    req.setVersion("1.1");

    req.setRecordSchema("dc"); // Leaving this out seems to cause problems
    req.setRecordPacking(packing);  // XML rather than string
    req.setStartRecord(new org.apache.axis.types.PositiveInteger(""+first_rec));
    req.setMaximumRecords(new org.apache.axis.types.NonNegativeInteger(""+max_records));
    log.fine("Calling search...");

    SearchRetrieveResponseType resp = srw_port.searchRetrieveOperation(req);

    if ( resp != null )
    {
      log.fine("response hash code "+resp.hashCode());
      log.fine("response result set id "+resp.getResultSetId());
      log.fine("response number of records "+resp.getNumberOfRecords());

      if ( ( resp.getNumberOfRecords() != null ) && ( resp.getNumberOfRecords() != null ) )
        num_hits=resp.getNumberOfRecords().intValue();
      else
        log.info("getNumberOfRecords was null");

      if ( resp.getRecords() != null )
      {
        records = resp.getRecords().getRecord();
        System.err.println("Processing "+records.length+" records");
        for ( int i=0; i<records.length;i++ ) {
          log.fine("record: "+i+": "+records[i].getRecordSchema()+", "+records[i].getRecordData());
        }
      }
      else {
        System.err.println("resp.getRecords null");
      }
    }
    else
    {
      System.err.println("Response was null");
      log.info("Response was null");
    }

    return records;
  }

  // Fragment Source methods
  public InformationFragment[] getFragment(int starting_fragment,
                                           int count,
                                           RecordFormatSpecification spec) throws IRResultSetException
  {
    log.entering(SRWResultSet.class.getName(),"getFragment");

    InformationFragment[] result = null;

    try
    {
      RecordType[] records = requestRecords(starting_fragment, count);

      result = new InformationFragment[records.length];

      System.err.println("getFragment processing "+records.length+" records");

      for ( int i = 0; i<records.length; i++ )
      {
        System.err.println("Creating a fragment for the returned record...");

        ExplicitRecordFormatSpecification returned_spec = null;
        if ( records[i].getRecordSchema() != null ) {
          System.err.println("Returned schema: "+records[i].getRecordSchema());

          returned_spec = new ExplicitRecordFormatSpecification("xml",records[i].getRecordSchema(),null);
        }
        else
        {
          System.err.println("returned record had null record schema");
          log.fine("Letting XMLRecord determine specification");
        }

        org.apache.axis.message.MessageElement[] record_content = records[i].getRecordData().get_any();

        if ( record_content.length == 1 )
        {
          System.err.println("Create new dom tree element...");
          // result[i] = new XMLRecord(repos_id,
          result[i] = new InformationFragmentImpl(starting_fragment+i,
                                  repos_id,
                                  "SRW",
                                  "Handle",
                                  record_content[0].getAsDocument(),
                                  returned_spec);
        }
        else if ( record_content.length > 1 )
        {
          System.err.println("Too many message elements returned");
          result[i] = new InformationFragmentImpl(starting_fragment+i,
                                  repos_id,
                                  "SRW",
                                  "Handle",
                                  record_content[0].getAsDocument(),
                                  returned_spec);
         
        }
        else
        {
          System.err.println("No message elements returned");
        }
      }
    }
    catch ( java.rmi.RemoteException re )
    {
      re.printStackTrace();
      throw new IRResultSetException(re.toString());
    }
    catch (  java.lang.Exception e )
    {
      e.printStackTrace();
      throw new IRResultSetException(e.toString());
    }

    log.exiting(SRWResultSet.class.getName(),"getFragment");
    return result;
  }

  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification spec,
                               IFSNotificationTarget target)
  {
    log.entering(SRWResultSet.class.getName(),"asyncGetFragment");
    try
    {
      InformationFragment[] result = getFragment(starting_fragment,count,spec);
      target.notifyRecords(result);
    }
    catch ( IRResultSetException re )
    {
      target.notifyError("SRW", new Integer(0), "No reason", re);
    }
    log.exiting(SRWResultSet.class.getName(),"asyncGetFragment");
  }

  /** Current number of fragments available */
  public int getFragmentCount() {
    return num_hits;
  }

  /** The size of the result set (Estimated or known) */
  public int getRecordAvailableHWM() {
    return num_hits;
  }

  // public AsynchronousEnumeration elements()
  // {
  //   return null;
  // }

  /** Release all resources and shut down the object */
  public void close() {
  }

  public void setFragmentCount(int i) {
    log.entering(SRWResultSet.class.getName(),"setFragmentCount",new Integer(i));
    num_hits = i;
    IREvent e = new IREvent(IREvent.FRAGMENT_COUNT_CHANGE, new Integer(i));
    setChanged();
    notifyObservers(e);
    log.exiting(SRWResultSet.class.getName(),"setFragmentCount");
  }

  public IRResultSetInfo getResultSetInfo() {
    return new IRResultSetInfo(getResultSetName(),
                               "SRW",
                               null,
                               getFragmentCount(),
                               getStatus(),
                               null);
  }

}
