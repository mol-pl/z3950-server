package org.jzkit.search.util.RecordBuilder;

import org.springframework.context.ApplicationContext;

import org.jzkit.search.util.RecordModel.*;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import javax.xml.parsers.*;
import  java.io.*;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

@Service("org.jzkit.recordbuilder.string")
public class StringRecordFactory implements RecordBuilder, ApplicationContextAware, org.springframework.context.ApplicationListener, java.io.Serializable {

  private static Log log = LogFactory.getLog(StringRecordFactory.class);
  private ApplicationContext ctx = null;

  public StringRecordFactory() {
  }

 /**
  * @param input_fragment Source Fragment
  * @param esn Optional Element Set Name (F,B, For XML, a schema to request);
  * @return native_object
  */
  public Object createFrom(Document input_dom, String esn) {
    String result = null;
    try {
      if ( input_dom.getDocumentElement().getTagName().equals("string") ) {
        result = input_dom.getDocumentElement().getFirstChild().getNodeValue();
      }
      else {
        OutputFormat format  = new OutputFormat( "xml","utf-8",false );
        format.setOmitXMLDeclaration(true);
        java.io.StringWriter  stringOut = new java.io.StringWriter();
        XMLSerializer serial = new XMLSerializer( stringOut,format );
        serial.setNamespaces(true);
        serial.asDOMSerializer();
        serial.serialize( input_dom.getDocumentElement() );
        result = stringOut.toString();
      }
    }
    catch (java.io.IOException ioe) {
      log.error("Problem",ioe);
    }
    return result;
  }

  /**
   * create the canonica XML for this object
   */
  public Document getCanonicalXML(Object native_object) {
    Document retval = null;
    try {
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      dfactory.setNamespaceAware(true);
      dfactory.setValidating(false);
      DocumentBuilder docBuilder = dfactory.newDocumentBuilder();

      retval = docBuilder.newDocument();
      org.w3c.dom.Element root = retval.createElement("text");
      root.appendChild( retval.createTextNode(native_object.toString()) );
      retval.appendChild( root );
    }
    catch ( javax.xml.parsers.ParserConfigurationException pce ) {
      pce.printStackTrace();
    }
    return retval;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public void onApplicationEvent(ApplicationEvent evt) {
  }
}
