/**
 * Title:       ExampleSearchable
 * @version:    $Id: SOLRResultSet.java,v 1.2 2005/10/30 17:27:38 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

/*
 * $Log: SOLRResultSet.java,v $
 * Revision 1.2  2005/10/30 17:27:38  ibbo
 * updated
 *
 * Revision 1.1  2005/10/30 13:42:40  ibbo
 * updated - added new SOLR classes to work around some CQL issues, etc
 *
 */

package org.jzkit.search.provider.solr;

import java.util.Properties;
import java.util.Observer;
import java.net.*;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xpath.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.*;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

/**
 * @author Ian Ibbotson
 * @version $Id: SOLRResultSet.java,v 1.2 2005/10/30 17:27:38 ibbo Exp $
 */ 
public class SOLRResultSet extends AbstractIRResultSet implements IRResultSet {

  private static Log log = LogFactory.getLog(SOLRResultSet.class);
  private int num_hits = 0;
  private String query;
  private String repos_id = null;
  private String coll_name = null;
  private String base_url;
  private IRQuery q = null;
  private Map<String,String>field_lists;

  public SOLRResultSet(String base_url, 
                       String query, 
                       String repos_id, 
                       Map<String,String>field_lists,
                       IRQuery q) {
    this(null,base_url,query,repos_id,field_lists,q);
  }

  public SOLRResultSet(Observer[] observers, 
                       String base_url, 
                       String query, 
                       String repos_id, 
                       Map<String,String>field_lists,
                       IRQuery q) {

    super(observers);

    log.debug("New query String : "+query);

    this.query = query;
    this.repos_id = repos_id;
    this.base_url = base_url;
    this.field_lists = field_lists;
    this.q = q;

    if ( ( q.collections != null ) && ( q.collections.size() > 0 ) ) {
      coll_name = q.collections.get(0).toString();
    }
  }

  public void countHits() throws SearchException {
    try {
      URL url = new URL(base_url);
      requestRecords(1,0,null);  // Do the search but don't request any records
      setFragmentCount(num_hits);
      log.debug("Result num hits : "+num_hits);
    }
    catch ( java.net.MalformedURLException murle ) {
      throw new SearchException("Problem with SOLR query",murle);
    }
  }


  private InformationFragment[] requestRecords(int first_rec, int max_records, RecordFormatSpecification spec) throws SearchException {
    InformationFragment[] result = null;

    // Solr indexes are 0 based
    int solr_first_rec = first_rec-1;

    URL url = null;
    try {
      String fl = "";
      log.debug("requestRecords "+first_rec+","+max_records+","+spec+","+field_lists);
      if ( ( field_lists != null ) && ( spec != null ) ) {
        String fields = field_lists.get(spec.toString());
        if ( fields != null ) {
          fl = "&fl="+fields;
        }
      } 

      url = new URL(base_url + "?q="+java.net.URLEncoder.encode(query)+"&start="+solr_first_rec+"&rows="+max_records+fl);

      log.debug("Searching, URL:"+url);
      HttpURLConnection http = (HttpURLConnection) url.openConnection();

      // create a DocumentBuilderFactory and configure it
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

      // docFactory.setNamespaceAware(true);
      docFactory.setValidating(false);

      // create a DocumentBuilder that satisfies the constraints
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

      try {
        log.debug("Parsing response");
        Document xml = docBuilder.parse(http.getInputStream());
        // dumpResponseRecord(xml);

        if ( xml != null ) {
          Element namespaceNode = xml.createElement("nsnode");
          Node num_hits_node = XPathAPI.selectSingleNode(xml,"/response/result/@numFound",namespaceNode);
          if ( num_hits_node != null ) {
            num_hits = Integer.parseInt(num_hits_node.getNodeValue());
            log.debug("num_hits="+num_hits);

            NodeList nl = XPathAPI.selectNodeList(xml,"/response/result/doc",namespaceNode);
            int num_recs = nl.getLength();
            result = new InformationFragment[num_recs];
            if ( nl != null ) {
              for (int i=0; i<num_recs; i++) {
                Node record_node = nl.item(i);
                ExplicitRecordFormatSpecification record_spec = new ExplicitRecordFormatSpecification("xml:solr:F");
                Document new_result_document = docBuilder.newDocument();
                Node n = new_result_document.importNode(record_node,true);
                new_result_document.appendChild(n);
                // result[i] = new org.jzkit.search.util.RecordModel.DOMTree("SOLR","SOLR",null,new_result_document,record_spec);
                result[i] = new InformationFragmentImpl(first_rec+i,
                                           repos_id,
                                           coll_name,
                                           null,
                                           new_result_document,
                                           record_spec);

                result[i].setHitNo(first_rec+i);
                dumpResponseRecord(new_result_document);
              }
            }
            else {
              log.info("No response records");
            }
          }
          else {
            log.error("No numfound element in solr resuly");
          }
        }
        else {
          log.error("No XML response document from SOLR server");
        }
      } catch (IllegalArgumentException iae) {
        log.error("IllegalArgumentException SOLR response ("+url+")",iae);
        this.setStatus(IRResultSetStatus.FAILURE);
        throw new SearchException(iae.getMessage(),iae);
      } catch (org.xml.sax.SAXException se) {
        log.error("Error parsing SOLR response ("+url+")",se);
        this.setStatus(IRResultSetStatus.FAILURE);
        throw new SearchException(se.getMessage(),se);
      }
    } catch (javax.xml.transform.TransformerException te ) {
      throw new SearchException(te.getMessage(),te);
    } catch (MalformedURLException mue) {
      log.error("Malformed URL exception for SOLR request ("+url+")",mue);
      throw new SearchException(mue.getMessage(),mue);
    } catch (ParserConfigurationException pce) {
      throw new SearchException(pce.getMessage(),pce);
    } catch (java.io.IOException ie) {
      log.error("Java IO exception for SOLR request ("+url+")",ie);
      throw new SearchException(ie.getMessage(),ie);
    }

    return result;
  }

  // Fragment Source methods
  public InformationFragment[] getFragment(int starting_fragment,
                                           int count,
                                           RecordFormatSpecification spec) throws IRResultSetException {

    InformationFragment[] result = null;
    try {
      result = requestRecords(starting_fragment, count, spec);
    }
    catch ( SearchException se ) {
      log.error("Exception requesting records",se);
      throw new IRResultSetException("Problem with SOLR search in call to get fragment, query was "+query,se);
    }
    return result;
  }

  public void asyncGetFragment(int starting_fragment,
                               int count,
                               RecordFormatSpecification spec,
                               IFSNotificationTarget target)
  {
    try {
      InformationFragment[] result = getFragment(starting_fragment,count,spec);
      target.notifyRecords(result);
    }
    catch ( IRResultSetException re )
    {
      target.notifyError("SOLR", new Integer(0), "No reason = calling getFragment", re);
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
                               "SOLR",
                               null,
                               getFragmentCount(),
                               getStatus(),
                               null,
                               getLastMessage());

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

  private void dumpResponseRecord(Document d) throws java.io.IOException {
    log.debug("dumpResponseRecord");
    OutputFormat format  = new OutputFormat( "xml","utf-8",false );
    format.setOmitXMLDeclaration(true);
    java.io.StringWriter  stringOut = new java.io.StringWriter();
    XMLSerializer serial = new XMLSerializer( stringOut,format );
    serial.setNamespaces(true);
    serial.asDOMSerializer();
    serial.serialize( d.getDocumentElement() );
    log.debug("Result: "+stringOut.toString());
  }
}
