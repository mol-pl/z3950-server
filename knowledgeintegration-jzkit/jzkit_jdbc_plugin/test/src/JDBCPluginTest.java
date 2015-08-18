import junit.framework.*;
import junit.extensions.*;
import java.util.*;
import org.jzkit.search.util.ResultSet.*;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.*;

import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.provider.jdbc.*;
import org.jzkit.search.provider.iface.*;

public class JDBCPluginTest extends TestCase {

  public JDBCPluginTest(String name) {
    super (name);
  }
  
  public static void main(String[] args) {
  }

  public void testJDBCSearch() throws Exception {

    ApplicationContext app_context = new ClassPathXmlApplicationContext( "TestApplicationContext.xml" );

    // JDBCSearchServiceFactory factory = new JDBCSearchServiceFactory(app_context,
    //                                                                 "TestPersistenceDictionary",
    //                                                                 "TestAccessPointConfig", 
    //                                                                 "TestDataSource",
    //                                                                 "TestRecordTemplates");
    // Searchable s = factory.newSearchable();

    JDBCSearchable s = new JDBCSearchable();
    s.setApplicationContext(app_context);
    s.setDictionaryName("TestPersistenceDictionary");
    s.setAccessPathsConfigName("TestAccessPointConfig");
    s.setDatasourceName("TestDataSource");
    s.setTemplatesConfigName("TestRecordTemplates");
    s.setSQLDialect("com.k_int.sql.sql_syntax.provider.MySQLDialect");

    s.getRecordArchetypes().put("Default","xml:Resource:F");
    s.getRecordArchetypes().put("FullDisplay","xml:Resource:F");
    s.getRecordArchetypes().put("BriefDisplay","xml:Resource:B");
    s.getRecordArchetypes().put("Holdings","xml:Resource:F");

    System.err.println("Build IR Query");
    IRQuery query = new IRQuery();
    query.collections = new Vector();
    // query.collections.add("Resource");
    query.collections.add("SHYS");
    // query.query = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString("@attrset bib-1 @attr 1=4 \"brain\"");
    // query.query = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString("@attrset bib-1 @attr 1=1099 \"ATHLETICS\"");
    // query.query = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString("@attrset bib-1 @attr 1=1099 \"HEALTH\"");
    // query.query = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString("@attrset bib-1 @attr 1=1016 \"HEALTH\"");
    query.query = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString("@attrset bib-1 @and @attr 1=2061 \"S11 7GD\" @attr 1=1016 \"fred\"");

    System.err.println("Obtain instance from factory");

    System.err.println("Evaluate query...");
    IRResultSet result = s.evaluate(query);

    if ( result != null ) {
      System.err.println("Waiting for result set to complete, current status = "+result.getStatus());
      // Wait without timeout until result set is complete or failure
      result.waitForStatus(IRResultSetStatus.COMPLETE|IRResultSetStatus.FAILURE,0);
  
      System.err.println("Iterate over results (status="+result.getStatus()+"), count="+result.getFragmentCount());

      // Enumeration e = new org.jzkit.search.util.ResultSet.ReadAheadEnumeration(result);
      Enumeration e = new org.jzkit.search.util.ResultSet.ReadAheadEnumeration(result, new ArchetypeRecordFormatSpecification("Default"));

      for ( int i=0; ( ( e.hasMoreElements() ) && ( i < 10 ) ); i++) {
        System.err.println("Processing result "+i);
        InformationFragment f = (InformationFragment) e.nextElement();
        // InformationFragment output_frag = trans.transform(f,new Hashtable());
        System.err.println("Result Fragment: "+ f);

        // org.apache.xml.serialize.XMLSerializer ser = new org.apache.xml.serialize.XMLSerializer();
        // ser.setOutputByteStream(System.out);
        // ser.serialize(f.getDocument());
      }
    }
    else {
      System.err.println("Null result set");
    }
  }
}
