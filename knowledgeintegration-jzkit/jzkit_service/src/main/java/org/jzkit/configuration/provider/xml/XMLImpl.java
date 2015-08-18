package org.jzkit.configuration.provider.xml;

import org.jzkit.configuration.api.*;
import org.jzkit.ServiceDirectory.*;
import org.jzkit.search.util.Profile.ProfileDBO;
import org.jzkit.search.util.Profile.CrosswalkDBO;
import java.util.*;
import java.net.URL;
import org.apache.commons.digester.*;
import org.apache.commons.digester.xmlrules.*;
import org.springframework.context.*;

public class XMLImpl extends InMemoryImpl implements ApplicationContextAware {

  private Digester main_digester = null;
  private Digester profile_digester = null;
  private Digester query_crosswalk_digester = null;
  private String config_file = null;

  private XMLImpl() {
  }

  public XMLImpl(String config_file) {
    super();
    this.config_file = config_file;
  }

  public void init() {
    log.debug("Load config: "+config_file);
    try {
      load(config_file);
    }
    catch ( java.io.IOException ioe ) {
      log.error("Problem loading config",ioe);
    }
    catch ( org.xml.sax.SAXException saxe ) {
      log.error("Problem loading config",saxe);
    }
    initialise();
  }

  public XMLImpl(ApplicationContext ctx, String config_file) {

    if ( ctx == null )
      throw new RuntimeException("XMLIMpl ctx is null");

    this.ctx = ctx;

    log.debug("Load config: "+config_file);
    try {
      load(config_file);
    }
    catch ( java.io.IOException ioe ) {
      log.error("Problem loading config",ioe);
    }
    catch ( org.xml.sax.SAXException saxe ) {
      log.error("Problem loading config",saxe);
    }
  }

  private void load(String config_resource) throws java.io.IOException, org.xml.sax.SAXException {
    URL config_source = XMLImpl.class.getResource(config_resource);
    URL rules = XMLImpl.class.getResource("DigesterRules.xml");
    URL profile_rules = XMLImpl.class.getResource("ApplicationProfileDigesterRules.xml");
    URL query_crosswalk_rules = XMLImpl.class.getResource("QueryCrosswalkDigesterRules.xml");
    log.debug("Loading rules : "+rules);
    main_digester = DigesterLoader.createDigester(rules);
    profile_digester = DigesterLoader.createDigester(profile_rules);
    query_crosswalk_digester = DigesterLoader.createDigester(query_crosswalk_rules);
    main_digester.push(this); // Push the config object onto stack so we can add objects to it :)
    log.debug("Parsing config");
    main_digester.parse( config_source.openStream() );
    log.debug("Done Parsing config");
  }

  public void addCollection(String collection_code, String repository_code) throws ConfigurationException {

    log.debug("Add collection "+collection_code+" to repos "+repository_code);
    CollectionDescriptionDBO cd = lookupCollectionDescription(collection_code);
    log.debug("CollectionDescription="+cd);
    SearchServiceDescriptionDBO ssd = lookupSearchService(repository_code);
    log.debug("SearchServiceDescription="+ssd);

    if ( ( cd == null ) || ( ssd == null ) )
      throw new ConfigurationException("Unable to locate collection or repository information for collection instance");

    cd.setSearchServiceDescription(ssd);

    log.debug("addCollection completed");
  }

  public void addLandscape(String landscape_code, String coll_code) throws ConfigurationException {
    log.debug("Add collection "+coll_code+" to landscape "+landscape_code);
    // InformationLandscapeDBO landscape = lookupLandscape(landscape_code);
    // CollectionDescriptionDBO collection = lookupCollectionDescription(coll_code);
    // if ( ( collection != null ) && ( landscape != null ) )
    //   landscape.getInstances().add(collection);
    // else
    //   throw new ConfigurationException("Unknown landscape or collection");
  }

  public void registerClasspathProfile(String config_resource) throws java.io.IOException, org.xml.sax.SAXException {
    log.debug("registerClasspathProfile : "+config_resource+" (ctx="+ctx+")");
    profile_digester.clear();
    profile_digester.push(this);
    org.springframework.core.io.Resource r = ctx.getResource(config_resource);
    if ( r.exists() ) {
      URL config_source = r.getURL();
      log.debug("processing config "+config_source);
      profile_digester.parse( config_source.openStream() );
    }
    else {
      log.warn("Unable to locate resource "+config_resource);
    }
  }

  public void registerClasspathQueryCrosswalk(String query_crosswalk_resource_path) throws java.io.IOException, org.xml.sax.SAXException {
    log.debug("registerClasspathQueryCrosswalk : "+query_crosswalk_resource_path);
    query_crosswalk_digester.clear();
    query_crosswalk_digester.push(this);
    org.springframework.core.io.Resource r = ctx.getResource(query_crosswalk_resource_path);
    if ( r.exists() ) {
      URL config_source = r.getURL();
      // URL config_source = XMLImpl.class.getResource(query_crosswalk_resource_path);
      log.debug("processing crosswalk "+config_source);
      query_crosswalk_digester.parse( config_source.openStream() );
    }
    else {
      log.warn("Unable to locate resource "+query_crosswalk_resource_path);
    }
  }

  public void addPosting(String code, String name, String value) {
    log.debug("addPosting "+code+","+name+","+value);

    CollectionInfoTypeDBO it = lookupOrCreateCollectionInfoType(name, value);
    // Now look up collection
    CollectionDescriptionDBO coll = lookupCollectionDescription(code);

    if ( ( it != null ) && ( coll != null ) ) {
      coll.getPostings().add(it);
      it.getCollections().add(coll);
    }
    else {
      log.error("Error loading config - coll or infotype null - coll="+coll+", it="+it);
    }
  }
}

