package org.jzkit.search.provider.jdbc;

import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.util.RecordModel.*;
                                                                                                                                          
import com.k_int.sql.sql_syntax.*;
import com.k_int.sql.qm_to_sql.*;
import com.k_int.sql.data_dictionary.*;
import java.io.StringWriter;

import java.util.*;

import org.w3c.dom.*;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import javax.xml.parsers.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Title:
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: TemplateFragmentFactory.java,v 1.4 2004/10/28 10:11:57 ibbo Exp $
 *
 */
public class PrebuiltBlobFactory implements FragmentFactory {

  private transient static DocumentBuilderFactory dfactory = null;
  private static Log log = LogFactory.getLog(PrebuiltBlobFactory.class);

  private String access_path = null;
                                                                                                                                          
  static
  {
    dfactory = DocumentBuilderFactory.newInstance();
    dfactory.setNamespaceAware(true);
    dfactory.setValidating(false);
    dfactory.setAttribute("http://xml.org/sax/features/validation",Boolean.FALSE);
    dfactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd",Boolean.FALSE);
  }
                                                                                                                                          
  public PrebuiltBlobFactory() {
  }

  public PrebuiltBlobFactory(String access_path) {
    this.access_path = access_path;
  }

  public Document createFragment(com.k_int.sql.data_dictionary.Entity e) {
    Document doc = null;
    try {
      // log.fine("Parse record:" +orig);
      DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
      Object r = e.get(access_path);
      if ( r != null ) {
        String the_doc = r.toString();
        doc = docBuilder.parse(the_doc);
      }
      else {
        log.warn("Result of get XML Blob was NULL");
      }
    }
    catch ( org.xml.sax.SAXException saxe ) {
      saxe.printStackTrace();
    }
    catch ( java.io.IOException ioe ) {
      ioe.printStackTrace();
    }
    catch ( ParserConfigurationException pce ) {
      pce.printStackTrace();
    }
    catch ( com.k_int.sql.data_dictionary.UnknownAccessPointException uape ) {
      uape.printStackTrace();
    }

    return doc;
  }
}
