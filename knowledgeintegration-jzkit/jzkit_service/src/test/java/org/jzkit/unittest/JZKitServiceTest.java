package org.jzkit.unittest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.*;
import junit.extensions.*;
import java.util.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jzkit.configuration.api.Configuration;
import org.jzkit.configuration.api.*;
import org.jzkit.ServiceDirectory.*;
import org.jzkit.search.util.Profile.*;
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.RecordConversion.*;
import org.jzkit.search.*;
import org.jzkit.search.landscape.*;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

    
/**
 * Unit test for simple App.
 */
public class JZKitServiceTest extends TestCase {
//
//  public static Log log = LogFactory.getLog(JZKitServiceTest.class);
//    
//  public static void main(String[] args) {
//  }
//    
//  public JZKitServiceTest(String name) {
//    super (name);
//  } 
//
//  protected void setUp() {
//    log.debug("Setup");
//  } 
//    
//  protected void tearDown() {
//    log.debug("Shutdown");
//  } 
//  
//
//  /**
//   * @return the suite of tests being tested
//   */
//  public static Test suite() {
//    return new TestSuite( JZKitServiceTest.class );
//  }
//
//  /**
//   *
//   */
//  public void testApp() throws SearchException, 
//                               org.jzkit.search.util.ResultSet.IRResultSetException, 
//                               org.jzkit.search.util.QueryModel.InvalidQueryException {
//
//    log.debug("testApp - testing JZKitService");
//
//    try {
//      String jzkit_home_prop = java.lang.System.getProperty("org.jzkit.home");
//      log.debug("*** JZkitHome: "+jzkit_home_prop);
//
//      // Get the service
//      org.jzkit.service.JZKitService svc = org.jzkit.service.JZKitService.create(jzkit_home_prop);
//      svc.init();
//
//      // Obtain configuration interface
//      org.jzkit.configuration.api.Configuration jzkit_config = svc.getConfig();
//      log.debug("Got config object: "+jzkit_config);
//      assert(jzkit_config != null);
//
//      // Import JZKitConfig
//      //
//
//
//      // Export config - should match imported config
//      org.w3c.dom.Document config_as_doc = org.jzkit.configuration.api.ConfigDumper.output(jzkit_config);
//
//
//      Vector collection_ids = new Vector();
//      collection_ids.add("Test");
//
//      // Do a search 
//      org.jzkit.search.impl.StatelessQueryService sqs = svc.getStatelessQueryService();
//
//      String result_set_id = null;
//      QueryModel model = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString("@attrset bib-1 @attr 1=4 Science");
//      LandscapeSpecification landscape = new org.jzkit.search.landscape.SimpleLandscapeSpecification(collection_ids);
//      int first_hit = 1;
//      int num_hits = 10;
//      RecordFormatSpecification rfs = null;
//      ExplicitRecordFormatSpecification display_spec = null;
//      Map additional_properties = null;
//
//      // StatelessSearchResultsPageDTO rp = sqs.getResultsPageFor(result_set_id,model,landscape,first_hit,num_hits,rfs,display_spec,additional_properties);
//    }
//    finally { 
//      log.debug("Done");
//    }
//  }
}
