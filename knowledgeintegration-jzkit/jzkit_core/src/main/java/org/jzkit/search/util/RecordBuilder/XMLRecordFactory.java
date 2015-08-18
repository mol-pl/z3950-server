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

@Service("org.jzkit.recordbuilder.xml")
public class XMLRecordFactory implements RecordBuilder, ApplicationContextAware, org.springframework.context.ApplicationListener, java.io.Serializable {

  private static Log log = LogFactory.getLog(StringRecordFactory.class);
  private ApplicationContext ctx = null;

  public XMLRecordFactory() {
  }

 /**
  * @param input_fragment Source Fragment
  * @param esn Optional Element Set Name (F,B, For XML, a schema to request);
  * @return native_object
  */
  public Object createFrom(Document input_dom, String esn) {
    return input_dom;
  }

  /**
   * create the canonica XML for this object
   */
  public Document getCanonicalXML(Object native_object) {
    return (Document) native_object;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public void onApplicationEvent(ApplicationEvent evt) {
  }
}
