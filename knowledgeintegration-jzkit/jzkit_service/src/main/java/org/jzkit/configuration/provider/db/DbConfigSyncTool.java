package org.jzkit.configuration.provider.db;

import java.io.InputStream;
import java.io.FileInputStream;
import org.jzkit.configuration.api.*;
import java.util.prefs.*;
import java.util.Iterator;
import org.jzkit.util.*;
import org.jzkit.configuration.api.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.net.URL;
import org.apache.commons.digester.*;
import org.apache.commons.digester.xmlrules.*;
import org.w3c.dom.*;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Element;
import org.jzkit.search.util.QueryModel.Internal.AttrValue;

/**
 * Provides the actions that a JZKit management console will offer to the host system.
 * Functions are provided for creating, listing, changing and deleting configurations, starting servers, etc, etc.
 */
public class DbConfigSyncTool {

  public static Log log = LogFactory.getLog(DbConfigSyncTool.class);
  public static javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();

  public String p_config_file = null;
  public Configuration p_config = null;

  public void setConfigFile(String config_file) {
    log.debug("Set config file "+config_file);
    this.p_config_file = config_file;
  }

  public void setConfiguration(Configuration config) {
    log.debug("Set jzkit config");
    this.p_config = config;
  }

  /**
   *  Read the config file and make sure it's contents are reflected in the system configuration.
   */
  public void sync() {
    sync(p_config_file,p_config);
  }

  public static void sync(String config_file, Configuration config) {

    // 1. Parse the config 
    try {
      URL config_url = DbConfigSyncTool.class.getResource(config_file);
      log.debug("Attempting to load config from "+config_url+"("+config_file+")");
      if ( config_url != null ) {
        org.apache.xpath.CachedXPathAPI api = new org.apache.xpath.CachedXPathAPI();
        dbf.setValidating(false);
        javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder();
        Document document = parser.parse(config_url.openStream());

        NodeList services = api.selectNodeList(document, "/ServiceDirectory/SearchService");
        for ( int i=0; i<services.getLength(); i++ ) {
           Element service = (Element) services.item(i);
           SearchServiceDTO ss_dto = new SearchServiceDTO();
           ss_dto.svc_class = service.getAttribute("className");
           ss_dto.svc_code = service.getAttribute("code");
           ss_dto.svc_name = service.getAttribute("serviceName");

           log.debug("Adding service "+ss_dto.svc_code+" - "+ss_dto.svc_name+" - "+ss_dto.svc_class);

           NodeList prefs =  api.selectNodeList(service,"./Preferences/Preference");
           for ( int i2=0; i2<prefs.getLength(); i2++ ) {
             Element pref = (Element) prefs.item(i2);
             Node val_node = api.selectSingleNode(pref,"./text()");
             ss_dto.properties.put(pref.getAttribute("name"),val_node.getNodeValue());
             log.debug("Property "+pref.getAttribute("name")+" = "+val_node.getNodeValue());
           }

           NodeList archetypes =  api.selectNodeList(service,"./RecordArchetypes/Archetype");
           for ( int i3=0; i3<archetypes.getLength(); i3++ ) {
             Element arch = (Element) archetypes.item(i3);
             Node val_node = api.selectSingleNode(arch,"./text()");
             ss_dto.archetypes.put(arch.getAttribute("name"),val_node.getNodeValue());
             log.debug("Archetype "+arch.getAttribute("name")+" = "+val_node.getNodeValue());
           }

           NodeList collections =  api.selectNodeList(service,"./Collections/Collection");
           for ( int i4=0; i4<collections.getLength(); i4++ ) {
             Element coll = (Element) collections.item(i4);
             if ( coll != null ) {
               CollectionDefDTO coll_def = new CollectionDefDTO();
               coll_def.code=coll.getAttribute("code");
               coll_def.name=coll.getAttribute("name");
               coll_def.localid=coll.getAttribute("localId");
               coll_def.profile=coll.getAttribute("profile");
               ss_dto.collections.add(coll_def);
             }
             else {
               log.error("Coll element was null");
             }
           }

           NodeList indexes =  api.selectNodeList(service,"./ValidIndexes/Index");
           for ( int i5=0; i5<indexes.getLength(); i5++ ) {
             Element idx = (Element) indexes.item(i5);
             Node val_node = api.selectSingleNode(idx,"./text()");
             // ss_dto.valid_indexes is a map of sets where the key to the map is the attr type and the set is a list
             // of the valid attributes of that type, for example
             //    AccessPoint -> (title,author,name)
             //    Truncation -> (none,right)
             // So here we try to lookup the set for type and if none exists we create it.
             if ( idx != null ) {
               java.util.Set valid_attrs_of_type = ss_dto.valid_indexes.get(idx.getAttribute("type"));
               if ( valid_attrs_of_type == null ) {
                 valid_attrs_of_type = new java.util.HashSet();
                 ss_dto.valid_indexes.put(idx.getAttribute("type"),valid_attrs_of_type);
               }
               AttrValue valid_av = new AttrValue(idx.getAttribute("context"),val_node.getNodeValue());
               valid_attrs_of_type.add(valid_av);
               log.debug("Added valid attr of type "+idx.getAttribute("type")+" : "+valid_av);
             }
             else {
               log.error("Coll element was null");
             }
           }

           NodeList translations =  api.selectNodeList(service,"./TargetSpecificTranslations/Translate");
           for ( int i6=0; i6<translations.getLength(); i6++ ) {
             Element trans = (Element) translations.item(i6);
             ss_dto.translations.put(trans.getAttribute("qualIndex"),trans.getAttribute("toIndex"));
           }

           config.addOrUpdate(ss_dto);
        }
      }
      else {
        log.warn("Unable to load configuration from XML");
      }
    }
    catch ( javax.xml.parsers.ParserConfigurationException pce ) {
      pce.printStackTrace();
    }
    catch ( java.io.IOException ioe ) {
      ioe.printStackTrace();
    }
    catch ( org.xml.sax.SAXException saxe ) {
      saxe.printStackTrace();
    }
    catch ( javax.xml.transform.TransformerException te ) {
      te.printStackTrace();
    }
    catch ( org.jzkit.configuration.api.ConfigurationException ce ) {
      ce.printStackTrace();
    }
    finally {
    }
  }
}
