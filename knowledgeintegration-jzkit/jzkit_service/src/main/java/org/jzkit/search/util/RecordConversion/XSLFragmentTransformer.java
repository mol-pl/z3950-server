// Title:       Transformer
// @version:    $Id: XSLFragmentTransformer.java,v 1.3 2004/10/26 15:30:52 ibbo Exp $
// Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ibbo@k-int.com)
// Company:     Knowledge Integration Ltd.
// Description: 

//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2.1 of
// the license, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite
// 330, Boston, MA  02111-1307, USA.
// 


package org.jzkit.search.util.RecordConversion;

import java.util.*;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jzkit.search.util.RecordModel.InformationFragment;
import java.net.URL;
import java.net.URLConnection;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
                                                                                                                                          
/**
 * A FragmentTransformer that uses XSL (In some way ;) )
 */
public abstract class XSLFragmentTransformer extends FragmentTransformer {

  /// The Templates object that we will use to produce transformer objects for this instance
  protected Templates t = null;
  protected static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
  protected static Log log = LogFactory.getLog(XSLFragmentTransformer.class);
  protected String the_path = null;
  protected long datestamp=0;

  public XSLFragmentTransformer(String from, 
                                String to, 
                                Map props, 
                                Map context,
                                ApplicationContext ctx) throws javax.xml.transform.TransformerConfigurationException {
    super(from, to, props, context, ctx);
    the_path = (String) props.get("Sheet");
    reloadStylesheet();
  }

  public abstract Object transform(Document input, Map additional_properties) throws FragmentTransformationException;

  public void performTransformation(javax.xml.transform.Source source,
                                    javax.xml.transform.Result result,
                                    Map trans_properties) {
    try {
      checkStylesheetDatestamp();

      Transformer trans = t.newTransformer();
                                                                                                                                          
      // Transformer objects are transient stateful objects that track the application
      // of a specific transformation to a given source and the resulting output.
      // Will be cleaned up when it goes out of scope. N.B. We use the newTemplates
      // method of the TemplateFactory since we expect multithreaded execution of and
      // repeated use of specific transformations.
        
      // We pass in any params to the transformation
      trans.clearParameters();
      if ( trans_properties != null ) {
        Set <java.util.Map.Entry> entries = trans_properties.entrySet();
        for ( java.util.Iterator i=entries.iterator(); i.hasNext(); ) {
          java.util.Map.Entry e = (java.util.Map.Entry) i.next();
          trans.setParameter(e.getKey().toString(), e.getValue().toString());
        }
      }

      trans.transform(source,result);
    }
    catch ( javax.xml.transform.TransformerConfigurationException tce ) {
      log.warn("TransformerConfigurationException exception finding template "+the_path,tce);
    }
    catch ( javax.xml.transform.TransformerException te ) {
      log.warn("TransformerException General exception finding template "+the_path,te);
    }
    catch ( Exception e ) {
      log.warn("General exception finding template "+the_path,e);
    }
  }

  private void reloadStylesheet() throws javax.xml.transform.TransformerConfigurationException {
    log.debug("XSLFragmentTransformer::reloadStylesheet()");
    try {
      TransformerFactory tFactory = TransformerFactory.newInstance();
      if ( ctx == null ) 
        throw new RuntimeException("Application Context Is Null. Cannot resolve resources");

      org.springframework.core.io.Resource r = ctx.getResource(the_path);
      if ( ( r != null ) && ( r.exists() ) ) {
        URL path_url = r.getURL();
        URLConnection conn = path_url.openConnection();
        datestamp = conn.getDate();
        log.debug("get template for "+the_path+" url:"+path_url+" datestamp="+datestamp);
        t = tFactory.newTemplates(new javax.xml.transform.stream.StreamSource(conn.getInputStream()));
      }
      else {
        log.error("Unable to resolve URL for "+the_path);
      }
    }
    catch ( java.io.IOException ioe ) {
      log.warn("Problem with XSL mapping",ioe);
      throw new RuntimeException("Unable to locate mapping: "+the_path);
    }
    catch ( javax.xml.transform.TransformerConfigurationException tce ) {
      log.warn("Problem with XSL mapping",tce);
      throw(tce);
    }

  }

  private void checkStylesheetDatestamp() {
    try {
      URL path_url = XSLFragmentTransformer.class.getResource(the_path);
      URLConnection conn = path_url.openConnection();
      long current_date = conn.getDate();
      if ( current_date != datestamp ) {
        log.debug("Detected change of xsl stylesheet, reloading");
        reloadStylesheet();
      }
    }
    catch ( java.io.IOException ioe ) {
      log.warn("Problem with XSL mapping - "+the_path,ioe);
    }
    catch ( javax.xml.transform.TransformerConfigurationException tce ) {
      log.warn("Problem with XSL mapping - "+the_path,tce);
    }
  }
}
