//package org.jzkit.unittest;
//
//import junit.framework.Test;
//import junit.framework.TestCase;
//import junit.framework.TestSuite;
//import junit.framework.*;
//import junit.extensions.*;
//import java.util.*;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//import org.springframework.context.*;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.jzkit.configuration.api.Configuration;
//    
///**
// * Unit test for simple App.
// */
//public class ServiceTest extends TestCase {
//
//  private static ApplicationContext app_context = null;
//  public static Log log = LogFactory.getLog(ServiceTest.class);
//    
//  public static void main(String[] args) {
//  }
//    
//  public ServiceTest(String name) {
//    super (name);
//    app_context = new ClassPathXmlApplicationContext( "DefaultApplicationContext.xml" );
//    if ( app_context == null )
//      throw new RuntimeException("Unable to locate DefaultApplicationContext.xml definition file");
//    log.debug("Got context");
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
//    return new TestSuite( ServiceTest.class );
//  }
//
//  /**
//   * Rigourous Test :-)
//   */
//  public void testApp() throws org.hibernate.HibernateException, java.sql.SQLException {
//  //   org.hibernate.SessionFactory sf = (org.hibernate.SessionFactory) app_context.getBean("JZKitSessionFactory");
//  //   org.hibernate.Session sess=null;
//  //   try {
//  //     sess=sf.openSession();
//
//  //     org.jzkit.configuration.provider.db.ImportXMLConfig jz_conf_svc = new org.jzkit.configuration.provider.db.ImportXMLConfig(app_context);
//  //     jz_conf_svc.importConfig((Configuration)app_context.getBean("JZKitConfig"),"/JZKitConfig.xml");
//  //     sess.flush(); 
//  //     sess.connection().commit();
//  //   }
//  //   finally { 
//  //     if ( sess != null ) {
//  //       try {
//  //         sess.close();
//  //       }
//  //       catch ( Exception e ) {
//  //         e.printStackTrace();
//  //       }
//  //     }
//  //   }
//  }
//}
