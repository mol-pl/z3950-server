<%
  String base_dir = request.getContextPath();
%>
<html><head>
<link rel="stylesheet" type="text/css" href="<%=base_dir%>/css/styles.css"/></head>
<body>
<div id="topheader">
<span id="version">Version 0.92.0</span>
<span id="title">BECTA/COL Web Services</span>
</div>
<div id="header">
<ul><li><a href="home">Home</a></li>
<li><a href="general">Intro</a></li>
<li><a href="docs">Docs</a></li>
<li><a href="searches">Test Searches</a></li>
<li id="current"><b>Sample Code</b></li>
<li><a href="soap">Soap</a></li>
<li><a href="troubleshooting">Troubleshooting</a></li>
</ul>
</div>
<div style="clear: both">&nbsp;</div>
<div id="main">
<h1>Sample Code</h1>
<ul>
<li>Source repository for sample .Net c# code can be browsed <a href="http://developer.k-int.com/svn/COLWS/trunk/toolkit/dotnet/">Here</a>
<li>Source repository for sample java code can be browsed <a href="http://developer.k-int.com/svn/COLWS/trunk/toolkit/java/">Here</a>
<li>Source repository for sample php code can be browsed <a href="http://developer.k-int.com/svn/COLWS/trunk/toolkit/php/">Here</a>
<li>Source repository for sample yahoo Widget code can be browsed <a href="http://developer.k-int.com/svn/COLWS/trunk/toolkit/YahooWidget/">Here</a>
</ul>
<p>
<h3>Contributing</h3>
We are happy to include (or link to) contributed samples and code fragments. Please contact the webmaster if you wish to share.
</p>
<p>
<h3>Java Samples</h3>
<div class="code">
<pre>
package uk.org.becta.ws.sample;

// For URL of web service
import java.net.URL;

// SRW Stubs etc
import org.jzkit.search.provider.zing.*;
import org.jzkit.search.provider.zing.interfaces.*;
import org.jzkit.search.provider.zing.srw.bindings.*;

// Logging
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ian Ibbotson (ian.ibbotson@k-int.com)
 * @version 1.0
 *
 * Sample Code to execute a soap based request against the curriculum online 
 * web services api (COLWS-API). This sample performs a search looking for suppliers
 * by suppler name. The example demonstrates the use of wildcards and free text searching
 * within the supplier name access point.
 *
 */
public class SupplierSearchByName {

  // Logging instance for this class.
  public static Log log = LogFactory.getLog(SupplierSearchByName.class);

  /**
   * Set up sample case and invoke if endpoint URL provided on command line.
   * @param args String array of args. In this case args[0] should be URL of soap endpoint
   * @return Nothing.
   */
  public static void main(String[] args) {

    // Check base URL
    if ( args.length == 0 ) {
      System.err.println("usage: java SupplierSearchByName <<base_url>> (eg http://developer.k-int.com:8080/webservices/soap/search/Suppliers");
      System.exit(1);
    }

    // New instance of sample class
    SupplierSearchByName test_instance = new SupplierSearchByName();

    // Execute sample
    test_instance.go(args[0]);
  }

  /**
   * Execute a search for suppliers who's name contains any word starting with "k*".
   * @param base_service_url URL of the soap endpoint.
   * @return Nothing.
   */
  public void go(String base_service_url) {
    try {
      System.out.println("BECTA COL WS API - Sample #1 Search for supplier data by name");

      System.out.println("Contacting: "+base_service_url);

      // Set up SRW endpoint.
      URL url = new URL(base_service_url);
      SRWPort srw_port = new SRWSoapBindingStub(url, new org.apache.axis.client.Service());
  
      // Set up request message.
      SearchRetrieveRequestType req = new SearchRetrieveRequestType();
 
      // The query : look for any word in supplier name that starts with k.
      req.setQuery("col.supplierName=\"k*\"");

      // Mandatory version parameter.
      req.setVersion("1.1");

      // Default for the col server, but specified here anyway. Product search can ask for lom or learningresource.
      req.setRecordSchema("dc");

      // xml packing rather than string.
      // We want the respons record coded as <title> and not &lt;title>
      req.setRecordPacking("xml");

      // Start with the first record
      req.setStartRecord(new java.math.BigInteger("1"));

      // Get a maximum of 10 records for this page. (Will try and get 10 if they are available)
      req.setMaximumRecords(new java.math.BigInteger("10"));

      // Execute the request, placing the response message in resp.
      SearchRetrieveResponseType resp = srw_port.searchRetrieveOperation(req);
  
      // If everything went OK...
      if ( resp != null ) {
        // Array to hold result records after request.
        RecordType[] records = null;

        // Process number of records and output a bit of info for debugging.
        int num_hits = 0;
        System.out.println("response result set id "+resp.getResultSetId());
        System.out.println("response number of records "+resp.getNumberOfRecords());
  
        // Just check we got a response in number of records.
        if ( ( resp.getNumberOfRecords() != null ) && ( resp.getNumberOfRecords() != null ) )
          num_hits=resp.getNumberOfRecords().intValue();
        else
          System.err.println("getNumberOfRecords was null");
  
        // If there were really records returned, display them.
        if ( resp.getRecords() != null ) {
          records = resp.getRecords().getRecord();
          System.err.println("Processing "+records.length+" records");

          for ( int i=0; i<records.length;i++ ) {
            org.jzkit.search.provider.zing.StringOrXmlFragment sof = records[i].getRecordData();
            org.apache.axis.message.MessageElement[] elements = sof.get_any();
            if ( elements.length > 0 )
              System.err.println("record: "+i+": "+records[i].getRecordSchema()+", "+elements[0]);
            else
              System.err.println("No record data for record "+i);
          }
        }
        else {
          System.err.println("resp.getRecords null");
        }
      }
      else {
        // Oops.
        devSupportMsg("Fatal error, Call returned null");
      }
    } 
    catch ( java.net.MalformedURLException murle ) {
      murle.printStackTrace();
    }
    catch ( org.apache.axis.AxisFault af ) {
      af.printStackTrace();
    }
    catch ( java.rmi.RemoteException re ) {   
      re.printStackTrace();
    }

    log.debug("All done");
  }
   
  /**
   * Little helper to display a message for this sample.
   * @param error Error string to display.
   * @return Nothing.
   */
  private static void devSupportMsg(String error) {
    System.err.println(error);
    System.err.println("You can contact ian.ibbotson@k-int.com for support, or visit the col ws developer forums at http://xxx");
  }
}
</pre>
</div>
</p>
<p>
<h3>.Net c# samples</h3>
The following code fragment is extracted from the c# code example tree. It requires the SRW.cs file and the assembles, but gives a flavour of creating and submitting a search. The SRW.cs class is auto generated from the .Net wsdl tool with some custom additions to allow the constructor to specify different target URL's for Product, Taxon and Supplier Search.
<div class="code">
<pre>
using System;
using System.Collections.Generic;
using System.Windows.Forms;

namespace WindowsApplication1
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
            SRWSoapBinding srw_server = new SRWSoapBinding("http://developer.k-int.com:8080/webservices/soap/search/Products");
            searchRetrieveRequestType search_req = new searchRetrieveRequestType();
            search_req.query = "dc.title=\"ICT And Art\"";
            search_req.recordPacking="xml";
            search_req.recordSchema="learningobject";
            search_req.version="1.1";
            search_req.maximumRecords="10";
            search_req.startRecord="1";
            searchRetrieveResponseType search_resp = srw_server.SearchRetrieveOperation(search_req);
            System.Console.Out.WriteLine("Search response :" + search_resp.numberOfRecords);
        }
    }
}
</pre>
</div>
</p>

</div>
</body></html>
