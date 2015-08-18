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
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import org.jzkit.configuration.api.Configuration;
import org.jzkit.search.util.QueryModel.PrefixString.PrefixString;
import org.jzkit.search.util.QueryModel.CQLString.CQLString;
import org.jzkit.ServiceDirectory.AttributeSetDBO;

    
/**
 * Unit test for simple App.
 */
public class ProfileServiceTest extends TestCase {

  private static ApplicationContext app_context = null;
  public static Log log = LogFactory.getLog(ProfileServiceTest.class);
    
  public static void main(String[] args) {
  }
    
  public ProfileServiceTest(String name) {
    super (name);
    app_context = new ClassPathXmlApplicationContext( "DefaultApplicationContext.xml" );
    if ( app_context == null )
      throw new RuntimeException("Unable to locate DefaultApplicationContext.xml definition file");
    log.debug("Got context");
  } 

  protected void setUp() {
    log.debug("Setup");
  } 
    
  protected void tearDown() {
    log.debug("Shutdown");
    try {
      org.hibernate.SessionFactory sf = (org.hibernate.SessionFactory) app_context.getBean("JZKitSessionFactory");
      sf.close();
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }
  } 
  

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite( ProfileServiceTest.class );
  }

  /**
   */
  public void testOne() throws org.jzkit.search.util.Profile.ProfileServiceException, org.jzkit.search.util.QueryModel.InvalidQueryException {


    AttrValue bib_1title = new AttrValue("bib-1","1.4");
    AttrValue bib_1author = new AttrValue("bib-1","1.2");
    AttrValue unqual_title = new AttrValue("title");
    AttrValue unqual_title_2 = new AttrValue("title");
    java.util.Set attr_set = new java.util.HashSet();
    attr_set.add(bib_1title);
    attr_set.add(bib_1author);
    attr_set.add(unqual_title);

    assert(unqual_title_2.equals(unqual_title));

    assert(attr_set.contains(bib_1title));
    assert(attr_set.contains(bib_1author));
    assert(attr_set.contains(unqual_title));
    assert(attr_set.contains(unqual_title_2));

    Map<String,AttributeSetDBO> valid_attributes = new HashMap<String,AttributeSetDBO>();

    AttributeSetDBO valid_access_points = new AttributeSetDBO();
    valid_access_points.getAttrs().add(new AttrValue("title"));  // No namespace

    AttributeSetDBO valid_relations = new AttributeSetDBO();
    valid_relations.getAttrs().add(new AttrValue("="));  // No namespace

    valid_attributes.put("AccessPoint",valid_access_points);
    valid_attributes.put("Relation",valid_relations);

    Map<String,AttrValue> transforms = new HashMap<String,AttrValue>();
    transforms.put("bib-1.1.4",new AttrValue("title"));
    transforms.put("dc.title",new AttrValue("title"));

    testTransform(app_context,valid_attributes,transforms, new PrefixString("@attrset bib-1 @attr 1=4 \"brain\""),"title = brain");
    testTransform(app_context,valid_attributes,transforms,new CQLString("dc.title=\"brain\""),"title = brain");
  }

  private void testTransform(ApplicationContext ctx,
                             Map<String,AttributeSetDBO> valid_attributes,
                             Map<String,AttrValue> transforms,
                             QueryModel qm,
                             String target_cql) throws org.jzkit.search.util.Profile.ProfileServiceException, org.jzkit.search.util.QueryModel.InvalidQueryException {

    org.jzkit.search.util.Profile.ProfileService ps = (org.jzkit.search.util.Profile.ProfileService) ctx.getBean("ProfileService");
    QueryModel new_qm = ps.makeConformant(qm,valid_attributes,transforms,null);
    String new_cql = transform(new_qm,ctx);
    log.debug(" testing "+qm.toString()+" -> "+new_cql+" (want "+target_cql+")");
    if ( new_cql.equals(target_cql) ) {
      log.debug(" PASS ");
    }
    else {
      log.debug(" FAIL - wanted "+target_cql+" got "+new_cql);
    }
  }

  private String transform(QueryModel qm, ApplicationContext ctx) throws org.jzkit.search.util.QueryModel.InvalidQueryException {
    String result;
    org.jzkit.search.util.QueryModel.CQLString.CQLString cql_str = org.jzkit.search.util.QueryModel.CQLString.CQLBuilder.buildFrom(qm,ctx,false);
    result = cql_str.toString();
    return result;
  }

}
