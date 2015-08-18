import junit.framework.*;
import junit.extensions.*;
import java.util.*;

import org.jzkit.search.provider.SRW.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.provider.iface.*;

public class SRWTest extends TestCase {

  public SRWTest(String name) {
    super (name);
  }
  
  public static void main(String[] args) {
  }

  public void testJSTOR() throws Exception {
    System.err.println("Setting up JSTOR SRW factory");
    // http://z3950.loc.gov:7090/voyager?operation=explain&version=1.1
    // SRWIRServiceFactory factory = new SRWIRServiceFactory("http://stage.jstor.org:17080/SRW/search/jstor","JSTOR");
    // http://z3950.loc.gov:7090/voyager?operation=searchRetrieve&version=1.1&query=Science&maximumRecords=10
    SRWIRServiceFactory factory = new SRWIRServiceFactory("http://z3950.loc.gov:7090/voyager","LCSRW");

    factory.getRecordArchetypes().put("Default","usmarc::F");
    factory.getRecordArchetypes().put("F","usmarc::F");
    factory.getRecordArchetypes().put("B","usmarc::B");
    factory.getRecordArchetypes().put("H","usmarc::F");

    System.err.println("Build IR Query");
    IRQuery query = new IRQuery();
    query.collections = new Vector();
    // query.collections.add("SRW/search/jstor");
    query.query = new org.jzkit.search.util.QueryModel.CQLString.CQLString("dc.title=\"Science\"");

    System.err.println("Obtain instance from factory");
    Searchable s = factory.newSearchable();

    System.err.println("Evaluate query...");
    IRResultSet result = s.evaluate(query);

    System.err.println("Waiting for result set to complete, current status = "+result.getStatus());
    // Wait without timeout until result set is complete or failure
    result.waitForStatus(IRResultSetStatus.COMPLETE|IRResultSetStatus.FAILURE,0);

    System.err.println("Iterate over results (status="+result.getStatus()+"), count="+result.getFragmentCount());

    // Enumeration e = new org.jzkit.search.util.ResultSet.ReadAheadEnumeration(result);
    Enumeration e = new org.jzkit.search.util.ResultSet.ReadAheadEnumeration(result, new ArchetypeRecordFormatSpecification("Default"));

    for ( int i=0; ( ( e.hasMoreElements() ) && ( i < 20 ) ); i++) {
      System.err.println("Processing result "+i);
      Object o = e.nextElement();
      System.err.println(o);
    }
  }
}
