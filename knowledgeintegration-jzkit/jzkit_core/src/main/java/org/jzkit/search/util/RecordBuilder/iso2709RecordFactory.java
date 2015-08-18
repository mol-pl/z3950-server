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
import java.io.*;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.marc4j.*;
import org.marc4j.marc.*;
import org.marc4j.util.*;

import java.text.DecimalFormat;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.io.FileInputStream;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

@Service("org.jzkit.recordbuilder.iso2709")
public class iso2709RecordFactory implements RecordBuilder, ApplicationContextAware, org.springframework.context.ApplicationListener, java.io.Serializable {

  private static Log log = LogFactory.getLog(StringRecordFactory.class);
  private ApplicationContext ctx = null;

  public iso2709RecordFactory() {
  }

 /**
  * @param input_fragment Source Fragment
  * @param esn Optional Element Set Name (F,B, For XML, a schema to request);
  * @return native_object
  */
  public Object createFrom(Document input_dom, String esn) throws RecordBuilderException {
    byte[] result = null;
    log.debug("iso2709 from marcxml");
    try {
      log.debug("open pip output stream");
      PipedOutputStream pos = new PipedOutputStream();
      XMLSerializer serializer = new XMLSerializer();
      MarcXmlReader reader = new MarcXmlReader(new PipedInputStream(pos));
      serializer.setOutputByteStream(pos);
      log.debug("Serialize dom to output stream");
      serializer.serialize(input_dom);
      log.debug("flush");
      pos.flush();
      pos.close();
      log.debug("attempt to read marcxml from pipe");
      if (reader.hasNext()) {
        Record record = reader.next();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MarcWriter writer = new MarcStreamWriter(baos);
        writer.write(record);
        result = baos.toByteArray();
        log.debug("Result of transform to marc: "+new String(result));
      }
      else {
        log.warn("No marc record found in reader stream");
      }
    }
    catch ( java.io.IOException ioe ) {
      throw new RecordBuilderException("Problem converting MARCXML to iso2709",ioe);
    }
   
    log.debug("result="+result);
    return result;
  }

  /**
   * create the canonica XML for this object
   */
  public Document getCanonicalXML(Object native_object) throws RecordBuilderException {
    org.w3c.dom.Document retval = null;
    try {
      MarcReader reader = new MarcStreamReader(new ByteArrayInputStream((byte[])native_object));
      javax.xml.transform.dom.DOMResult result = new javax.xml.transform.dom.DOMResult();
      MarcXmlWriter writer = new MarcXmlWriter(result);
      writer.setConverter(new org.marc4j.converter.impl.AnselToUnicode());
      if (reader.hasNext()) {
        Record record = (Record) reader.next();
        writer.write(record);
      }
      writer.close();

      retval = (Document) result.getNode();
    }
    catch ( Exception e ) {
      throw new RecordBuilderException("Problem creating marcxml from iso2709",e);
    }
    return retval;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public void onApplicationEvent(ApplicationEvent evt) {
  }
}
