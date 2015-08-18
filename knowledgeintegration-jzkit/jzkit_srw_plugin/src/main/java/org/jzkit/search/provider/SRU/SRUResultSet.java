/**
 * Title:       ExampleSearchable
 * @version:    $Id: SRUResultSet.java,v 1.2 2005/10/30 17:27:38 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

/*
 * $Log: SRUResultSet.java,v $
 * Revision 1.2  2005/10/30 17:27:38  ibbo
 * updated
 *
 * Revision 1.1  2005/10/30 13:42:40  ibbo
 * updated - added new SRU classes to work around some CQL issues, etc
 *
 */

package org.jzkit.search.provider.SRU;

import java.util.Properties;
import java.util.Observer;
import java.net.*;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.provider.zing.*;
import org.jzkit.search.provider.zing.interfaces.*;
import org.jzkit.search.provider.zing.srw.bindings.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xpath.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ian Ibbotson
 * @version $Id: SRUResultSet.java,v 1.2 2005/10/30 17:27:38 ibbo Exp $
 */ 
public class SRUResultSet extends AbstractIRResultSet implements IRResultSet {

  private static Log log = LogFactory.getLog(SRUResultSet.class);
  private int num_hits = 0;
  private SRWPort srw_port = null;
  private String cql_string;
  private String repos_id = null;
  private String base_url;

  public SRUResultSet(String base_url, String cql_string, String repos_id) {
    this(null,base_url,cql_string,repos_id);
  }

  public SRUResultSet(Observer[] observers, String base_url, String cql_string, String repos_id) {

    super(observers);

    log.debug("New CQL String : "+cql_string);

    this.cql_string = cql_string;
    this.repos_id = repos_id;
    this.base_url = base_url;

    try {
      setup(base_url);
    }
    catch ( java.net.MalformedURLException murle ) {
      murle.printStackTrace();
    }
  }

  private void setup(String base_url) throws java.net.MalformedURLException
  {
    URL url = new URL(base_url);
    requestRecords(1,0);  // Do the search but don't request any records
    setFragmentCount(num_hits);
    log.debug("Result num hits : "+num_hits);
  }


  private InformationFragment[] requestRecords(int first_rec, int max_records) {
    InformationFragment[] result = null;

    try {
      URL url = new URL(base_url + "?version=1.1&operation=searchRetrieve&query="+java.net.URLEncoder.encode(cql_string));

      log.debug("Searching, URL:"+url);
      HttpURLConnection http = (HttpURLConnection) url.openConnection();

      // create a DocumentBuilderFactory and configure it
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

      docFactory.setNamespaceAware(true);
      docFactory.setValidating(false);

      // create a DocumentBuilder that satisfies the constraints
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

      try {
        log.debug("Parsing response");
        Document xml = docBuilder.parse(http.getInputStream());

        if ( xml != null ) {
          Element namespaceNode = xml.createElement("nsnode");
          namespaceNode.setAttribute("xmlns:SRW", "http://www.loc.gov/zing/srw/");
          namespaceNode.setAttribute("xmlns:DIAG", "http://www.loc.gov/zing/srw/v1.0/diagnostic/");
          Node num_hits_node = XPathAPI.selectSingleNode(xml,"/SRW:searchRetrieveResponse/SRW:numberOfRecords/text()",namespaceNode);
          num_hits = Integer.parseInt(num_hits_node.getNodeValue());

          // Process Records
          NodeList nl = XPathAPI.selectNodeList(xml,"/SRW:searchRetrieveResponse/SRW:records/SRW:record",namespaceNode);
          int num_recs = nl.getLength();
          result = new InformationFragment[num_recs];
          if ( nl != null ) {
            for (int i=0; i<num_recs; i++) {
              Node record_node = nl.item(i);
              Element the_real_record = (Element) XPathAPI.selectSingleNode(record_node,"SRW:recordXML/*[1]",namespaceNode);
              String rec_schema = XPathAPI.selectSingleNode(record_node,"SRW:recordSchema/text()",namespaceNode).getNodeValue();
              ExplicitRecordFormatSpecification record_spec = new ExplicitRecordFormatSpecification("xml:"+rec_schema+":F");
              Document new_result_document = docBuilder.newDocument();
              Node n = new_result_document.importNode(the_real_record,true);
              new_result_document.appendChild(n);
              result[i] = new org.jzkit.search.util.RecordModel.InformationFragmentImpl(first_rec+i,"SRW","SRW",null,new_result_document,record_spec);
            }
          }
          else {
          }
        }
      } catch (IllegalArgumentException iae) {
        iae.printStackTrace();
      } catch (org.xml.sax.SAXException se) {
        se.printStackTrace();
      }
    } catch (javax.xml.transform.TransformerException te ) {
        te.printStackTrace();
    } catch (MalformedURLException mue) {
        mue.printStackTrace();
    } catch (ParserConfigurationException pce) {
        pce.printStackTrace();
    } catch (java.io.IOException ie) {
        ie.printStackTrace();
    }

    return result;
  }

  // Fragment Source methods
  public InformationFragment[] getFragment(int starting_fragment,
                                           int count,
                                           RecordFormatSpecification spec) throws IRResultSetException
  {
    return requestRecords(starting_fragment, count);
  }

  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification spec,
                               IFSNotificationTarget target)
  {
    try
    {
      InformationFragment[] result = getFragment(starting_fragment,count,spec);
      target.notifyRecords(result);
    }
    catch ( IRResultSetException re )
    {
      target.notifyError("SRU", new Integer(0), "No reason", re);
    }
  }

  /** Current number of fragments available */
  public int getFragmentCount() {
    return num_hits;
  }

  /** The size of the result set (Estimated or known) */
  public int getRecordAvailableHWM() {
    return num_hits;
  }

  // public AsynchronousEnumeration elements()
  // {
  //   return null;
  // }

  /** Release all resources and shut down the object */
  public void close() {
  }

  public void setFragmentCount(int i) {
    num_hits = i;
    IREvent e = new IREvent(IREvent.FRAGMENT_COUNT_CHANGE, new Integer(i));
    setChanged();
    notifyObservers(e);
  }

  public IRResultSetInfo getResultSetInfo() {
    return new IRResultSetInfo(getResultSetName(),
                               "SRU",
                               null,
                               getFragmentCount(),
                               getStatus(),
                               null);
  }

  public static org.jzkit.search.provider.iface.ExplainDTO explain(String base_url) {
    log.debug("Explaining..... "+base_url);
    org.jzkit.search.provider.iface.ExplainDTO result = new org.jzkit.search.provider.iface.ExplainDTO();

    try {
      URL url = new URL(base_url + "?version=1.1&operation=explain");
      HttpURLConnection http = (HttpURLConnection) url.openConnection();

      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

      docFactory.setNamespaceAware(true);
      docFactory.setValidating(false);

      // create a DocumentBuilder that satisfies the constraints
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

      try {
        log.debug("Parsing response");
        Document xml = docBuilder.parse(http.getInputStream());

        if ( xml != null ) {
          log.debug("Processing Explain Response");
          Element namespaceNode = xml.createElement("nsnode");
          namespaceNode.setAttribute("xmlns:SRW", "http://www.loc.gov/zing/srw/");
          namespaceNode.setAttribute("xmlns:DIAG", "http://www.loc.gov/zing/srw/v1.0/diagnostic/");
          namespaceNode.setAttribute("xmlns:EXPLAIN", "http://explain.z3950.org/dtd/2.0/");

          Node server_info_node = XPathAPI.selectSingleNode(xml,"/SRW:explainResponse/SRW:record/SRW:recordData/EXPLAIN:explain/EXPLAIN:serverInfo",namespaceNode);
          Node database_info_node = XPathAPI.selectSingleNode(xml,"/SRW:explainResponse/SRW:record/SRW:recordData/EXPLAIN:explain/EXPLAIN:databaseInfo",namespaceNode);

          Node database_code_node = XPathAPI.selectSingleNode(server_info_node,"EXPLAIN:database/text()",namespaceNode);
          Node database_name_node = XPathAPI.selectSingleNode(xml,"EXPLAIN:title/text()",namespaceNode);
          Node database_desc_node = XPathAPI.selectSingleNode(xml,"EXPLAIN:description/text()",namespaceNode);

          String database_title = "Title Not Available";
          String database_desc = "Description Not Available";
          String database_code = null;

          if ( database_name_node != null ) 
            database_title = database_name_node.getNodeValue();

          if ( database_desc_node != null )
            database_desc = database_desc_node.getNodeValue();

          if ( database_code_node != null ) 
            database_code = database_code_node.getNodeValue();
          else  
            database_code = java.util.UUID.randomUUID().toString();

          result.setTitle(database_title);
          result.setDescription(database_desc);

          ExplainDBInfoDTO[] db_info = new ExplainDBInfoDTO[1];
          db_info[0] = new ExplainDBInfoDTO();
          db_info[0].setLocalCode(database_code);
          db_info[0].setTitle(database_title);
          db_info[0].setDescription(database_desc);
          result.setDatabases(db_info);
        }
        else {
          log.error("Error parsing explain response - no XML");
        }
      } catch (IllegalArgumentException iae) {
        iae.printStackTrace();
      } catch (org.xml.sax.SAXException se) {
        se.printStackTrace();
      }
    } catch (javax.xml.transform.TransformerException te ) {
        te.printStackTrace();
    } catch (MalformedURLException mue) {
        mue.printStackTrace();
    } catch (ParserConfigurationException pce) {
        pce.printStackTrace();
    } catch (java.io.IOException ie) {
        ie.printStackTrace();
    }

    log.debug("Result of explain: "+result);
    return result;
  }
}
