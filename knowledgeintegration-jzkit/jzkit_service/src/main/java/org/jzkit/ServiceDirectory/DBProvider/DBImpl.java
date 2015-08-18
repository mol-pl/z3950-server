package org.jzkit.ServiceDirectory.DBProvider;

import org.jzkit.ServiceDirectory.*;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.Preferences;

public class DBImpl implements ServiceDirectory
{
  private String service_name;
  private Logger log = Logger.getLogger(this.getClass().getName());

  public DBImpl(Preferences prefs) throws java.io.IOException, org.xml.sax.SAXException
  {
    log.info("New DBImpl");
    // Use the preferences API to discover the source of the config file
    // String config_resource = prefs.get("ConfigResource","/ServiceDirectory.xml");
    // Location of db config props will be
    // Preferences root = Preferences.systemRoot();
    // Preferences core_db_prefs = root.node("org/jzkit/core/db");

    log.info("All done");
  }

  public void registerCollectionDescription(CollectionDescriptionDBO collection_description)
  {
  }

  public void registerServiceDescription(SearchServiceDescriptionDBO service_description)
  {
  }

  public CollectionDescriptionDBO lookupCollectionDescription(String collection_id)
  {
    return null;
  }

  public SearchServiceDescriptionDBO lookupSearchService(String service_id)
  {
    return null;
  }

  public Iterator enumerateVisibleCollections()
  {
    return null;
  }

}
