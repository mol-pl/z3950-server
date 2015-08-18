package org.jzkit.service.z3950server.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.*;
import org.jzkit.configuration.api.Configuration;
import org.jzkit.configuration.provider.db.DbConfigurationProvider;
import org.jzkit.search.util.RecordConversion.FragmentTransformerService;
import org.jzkit.configuration.provider.db.*;
import org.jzkit.configuration.api.Configuration;
import org.jzkit.search.*;
import org.jzkit.ServiceDirectory.*;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.QueryModel.Internal.*;
import org.jzkit.search.util.QueryModel.PrefixString.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordConversion.FragmentTransformerService;
import org.jzkit.search.provider.z3950.*;
import org.jzkit.search.provider.iface.*;
import java.util.logging.*;
import java.util.*;
import java.io.*;
import junit.framework.*;
import junit.extensions.*;
import org.jzkit.z3950.server.*;

public class TestZServer extends TestCase {

  public TestZServer(String name) {
    super (name);
  }
                                                                                                                                          
  public void testService() throws org.jzkit.configuration.api.ConfigurationException, 
                                       org.jzkit.search.provider.iface.SearchException,
                                       org.jzkit.search.util.ResultSet.IRResultSetException {

    System.err.println("Starting jzkit2 z3950 server...");
                                                                                                                                          
    ApplicationContext app_context = new ClassPathXmlApplicationContext( "TestApplicationContext.xml" );
    Z3950Listener listener = (Z3950Listener)app_context.getBean("Z3950Listener",Z3950Listener.class);
    listener.start();

    System.err.println("Listener up and running");

    // Use a stand-alone Z3950 client to 

    System.err.println("Setting up Z3950 factory");
    Z3950ServiceFactory factory = new Z3950ServiceFactory("localhost",9999);
    factory.setApplicationContext(app_context);
    factory.setDefaultRecordSyntax("xml");
    factory.setDefaultElementSetName("F");
    factory.getRecordArchetypes().put("Default","xml::F");
    factory.getRecordArchetypes().put("FullDisplay","xml::F");
    factory.getRecordArchetypes().put("BriefDisplay","xml::B");
    factory.getRecordArchetypes().put("Holdings","xml::F");

    System.err.println("Build IR Query");
    IRQuery query = new IRQuery();
    query.collections = new Vector();
    // query.collections.add("Default");
    query.collections.add("Test:one");
    query.query = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString("@attrset bib-1 @attr 1=4 \"brain\"");

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

    for ( int i=0; ( ( e.hasMoreElements() ) && ( i < 10 ) ); i++) {
      System.err.println("Processing z3950 server result "+i);
      Object o = e.nextElement();
      System.err.println(o);
    }

    System.err.println("All done - Z3950 Server Unit Test");

    result.close();
    s.close();
  }
}
