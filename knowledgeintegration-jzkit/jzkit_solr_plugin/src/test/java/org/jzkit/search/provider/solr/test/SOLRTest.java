package org.jzkit.search.provider.solr.test;

import junit.framework.*;
import junit.extensions.*;
import java.util.*;
import org.jzkit.search.util.ResultSet.*;

import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.provider.solr.*;
import org.jzkit.search.provider.iface.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.*;
import org.jzkit.search.provider.iface.SearchableFactory;
import org.jzkit.search.provider.iface.Searchable;

public class SOLRTest extends TestCase {

  private static ApplicationContext app_context = null;

  public SOLRTest(String name) {
    super (name);

  }
  
  public static void main(String[] args) {
  }

  public void testSOLR() throws Exception {
    app_context = new ClassPathXmlApplicationContext( "TestApplicationContext.xml" );
    SearchableFactory sf = (org.jzkit.search.provider.iface.SearchableFactory) app_context.getBean("SearchableFactory");

    IRServiceDescriptor descriptor = new IRServiceDescriptor("proto=SOLR,code=k-int-solr,baseURL=http://dev.k-int.com:8080/solr/select,shortname=SOLR,longname=k-int SOLR Test,defaultRecordSyntax=usmarc,defaultElementSetName=F,recordArchetypes(Default)=solr::F,QueryType=SOLR-STD,fieldList(F)='id,name,popularity',fieldList(B)='id,name',fieldList(Default)='id,name,popularity'");

    Searchable s = sf.create(descriptor);

    if ( s != null ) {
      org.jzkit.search.util.QueryModel.CQLString.CQLString qm = new org.jzkit.search.util.QueryModel.CQLString.CQLString("name=dell");

      // Create a query
      IRQuery query = new IRQuery(qm,"Default");

      IRResultSet result = s.evaluate(query);
      result.waitForStatus(IRResultSetStatus.COMPLETE|IRResultSetStatus.FAILURE,0);
      Enumeration e = new org.jzkit.search.util.ResultSet.ReadAheadEnumeration(result, new ArchetypeRecordFormatSpecification("Default"));
      for ( int i=0; ( ( e.hasMoreElements() ) && ( i < 20 ) ); i++) {
        Object o = e.nextElement();
        System.err.println(o);
      }
    }
    else {
      System.err.println("No search created by factory");
    }
  }
}
