package org.jzkit.service.test;

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
import org.jzkit.search.util.ResultSet.*;

import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.RecordConversion.FragmentTransformerService;
                                                                                                                                        
import java.util.logging.*;
import java.util.*;

import java.io.*;

import junit.framework.*;
import junit.extensions.*;


public class TestService extends TestCase {

  public TestService(String name) {
    super (name);
  }
                                                                                                                                          
  public void testService() throws org.jzkit.configuration.api.ConfigurationException, org.jzkit.search.SearchException {

    Logger log = Logger.getLogger(TestService.class.getName());

    log.info("Starting jzkit2 server...");

    RecordFormatSpecification request_spec = new ArchetypeRecordFormatSpecification("F");
    ExplicitRecordFormatSpecification display_spec = new ExplicitRecordFormatSpecification("text:html:F");
                                                                                                                                          
    ApplicationContext app_context = new ClassPathXmlApplicationContext( "TestApplicationContext.xml" );
    log.info("JZKit server startup completed");

    SearchSession search_service = (SearchSession) app_context.getBean("SearchSession",SearchSession.class);

    Vector collection_ids = new Vector();
    collection_ids.add("LC/BOOKS");

    QueryModel qm = new PrefixString("@attrset bib-1 @attr 1=4 Science");

    System.err.println("Processing search......");

    TransformingIRResultSet result_set=
               search_service.search(new org.jzkit.search.landscape.SimpleLandscapeSpecification(collection_ids),qm,request_spec);

    System.err.println("\n\n\n\n\n* * * * * * * * All Done * * * * * * * * *\n\n\n\n\n");

    try {
      result_set.waitForStatus(org.jzkit.search.util.ResultSet.IRResultSetStatus.COMPLETE|
                               org.jzkit.search.util.ResultSet.IRResultSetStatus.FAILURE, 5000);
    }
    catch ( IRResultSetException rse ) {
      rse.printStackTrace();
    }

    int i=0;
    int hits = result_set.getFragmentCount();

    System.err.println("Processing....."+hits+" records");


    for ( i=1; ( ( i<hits ) && ( i<25 )); i++ ) {
      System.err.println("Getting next record ("+i+" out of "+hits+").....");
      try {
        InformationFragment[] page  = result_set.getFragment(i,1,request_spec,display_spec);
        if ( page.length == 1 ) 
          System.err.println(page[0]);
      }
      catch ( org.jzkit.search.util.ResultSet.IRResultSetException irrse ) {
        log.info("Problem"+irrse);
        irrse.printStackTrace();
      }
    }

    System.err.println(result_set.getResultSetInfo());

    result_set.close();

    search_service.close();
  }
}
