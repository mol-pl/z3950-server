package org.jzkit.service.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.*;
                                                                                                                                          
import org.jzkit.configuration.api.Configuration;
import org.jzkit.configuration.provider.db.DbConfigurationProvider;
import org.jzkit.search.util.RecordConversion.FragmentTransformerService;
                                                                                                                                          

import org.jzkit.configuration.provider.db.*;
import org.jzkit.configuration.api.Configuration;
import org.jzkit.search.*;
import org.jzkit.search.impl.*;
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

public class TestStatelessSearch extends TestCase {

  public TestStatelessSearch(String name) {
    super (name);
  }
                                                                                                                                          
  public void testStatelessSearch() throws org.jzkit.configuration.api.ConfigurationException, org.jzkit.search.SearchException, org.jzkit.search.util.ResultSet.IRResultSetException, org.jzkit.search.util.QueryModel.InvalidQueryException {

    Logger log = Logger.getLogger(TestService.class.getName());

    log.info("Starting jzkit2 server...");

    RecordFormatSpecification request_spec = new ArchetypeRecordFormatSpecification("F");
    ExplicitRecordFormatSpecification display_spec = new ExplicitRecordFormatSpecification("text:html:F");
                                                                                                                                          
    ApplicationContext app_context = new ClassPathXmlApplicationContext( "TestApplicationContext.xml" );
    log.info("JZKit server startup completed");

    Vector collection_ids = new Vector();
    collection_ids.add("LC/BOOKS");

    QueryModel qm = new PrefixString("@attrset bib-1 @attr 1=4 Science");

    System.err.println("Processing search......");

    try {
      Map additional_properties = new HashMap();
      additional_properties.put("base_dir","/a/b/c/d");
  
      StatelessQueryService stateless_query_service = (StatelessQueryService) app_context.getBean("StatelessQueryService");
  
      org.jzkit.search.landscape.SimpleLandscapeSpecification landscape = new org.jzkit.search.landscape.SimpleLandscapeSpecification(collection_ids);
      // Test 1 - Kick off a search
      StatelessSearchResultsPageDTO rp = stateless_query_service.getResultsPageFor(null,
                                                                                   qm,
                                                                                   landscape,
                                                                                   1,
                                                                                   5,
                                                                                   request_spec,
                                                                                   display_spec,
                                                                                   additional_properties);
  
      if ( rp != null ) {
        System.err.println("Result Set Size....."+rp.total_hit_count+" records - result contains "+rp.number_of_records+" records");
        System.err.println("Result Set ID : "+rp.result_set_id); 
      }
      else {
        System.err.println("Results page was null");
      }
  
      if ( rp.records != null ) {
        for ( int i=0; ( ( i<rp.records.length ) && ( i<25 )); i++ ) {
          System.err.println("Getting next record ("+i+" out of "+rp.number_of_records+").....");
          InformationFragment frag = rp.records[i];
          System.err.println(frag);
        }
      }
  
      // Test 2 - use the result set ID to get a page of requests
      rp = stateless_query_service.getResultsPageFor(rp.result_set_id,
                                                     qm,
                                                     landscape,
                                                     6,
                                                     5,
                                                     request_spec,
                                                     display_spec,
                                                     additional_properties);
  
      if ( rp.records != null ) {
        for ( int i=0; ( ( i<rp.records.length ) && ( i<25 )); i++ ) {
          System.err.println("Getting next record ("+i+" out of "+rp.number_of_records+").....");
          InformationFragment frag = rp.records[i];
          System.err.println(frag);
        }
      }
  
      // Test 3 - Use the query to get a cache hit
      rp = stateless_query_service.getResultsPageFor(null,
                                                     qm,
                                                     landscape,
                                                     6,
                                                     5,
                                                     request_spec,
                                                     display_spec,
                                                     additional_properties);
  
      if ( rp.records != null ) {
        for ( int i=0; ( ( i<rp.records.length ) && ( i<25 )); i++ ) {
          System.err.println("Getting next record ("+i+" out of "+rp.number_of_records+").....");
          InformationFragment frag = rp.records[i];
          System.err.println(frag);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
