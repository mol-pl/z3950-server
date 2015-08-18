// Title:       FragmentTransformerService
// @version:    $Id: FragmentTransformerService.java,v 1.6 2004/11/28 15:59:01 ibbo Exp $
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

import org.xml.sax.*;
import javax.xml.transform.stream.StreamResult;
import javax.naming.Reference;
import javax.naming.NamingException;

import java.util.prefs.Preferences;

import java.lang.reflect.Constructor;

import org.jzkit.search.util.RecordModel.*;
import org.jzkit.configuration.api.*;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Convert a fragment with a given specification to a target spec.
 */
public class FragmentTransformerService implements ApplicationContextAware {

  private String template_source;
  private static String file_sep = System.getProperty("file.separator");
  private Map context = new HashMap();
  private Map type_register = new HashMap();
  private static Log log = LogFactory.getLog(FragmentTransformerService.class);
  private List all_transformations = new ArrayList();
  private ApplicationContext ctx;

  /**
   * A Hashtable of Hashtables that points from a source schema
   * to schemas that we can get to by applying known transformations
   */
  private Map known_schemas = new HashMap();

  /**
   * Hashtable of stylesheet file names to Templates objects
   */
  private Map known_transformers = new HashMap();
  private TransformerFactory tFactory = javax.xml.transform.TransformerFactory.newInstance();
  private DocumentBuilder docBuilder;
  private Configuration configuration = null;

  public FragmentTransformerService(Configuration configuration) {
    log.debug("new FragmentTransformerService");
    this.configuration = configuration;
  }

  public void init() {
    log.debug("FragmentTransformerService::init");
    try {
      for ( Iterator i = configuration.getRegisteredConverterTypes(); i.hasNext(); ) {
        Object o = i.next();
        RecordTransformerTypeInformationDBO rtti = (RecordTransformerTypeInformationDBO)o;
        registerType(rtti.getType(), rtti.getClassname());
      }

      for ( Iterator i = configuration.getRegisteredRecordMappings(); i.hasNext(); ) {
        RecordMappingInformationDBO rmi = (RecordMappingInformationDBO) i.next();
        registerMapping(rmi.getFromSpec(),
                        rmi.getToSpec(), 
                        rmi.getType(), 
                        rmi.getResource());
      }
      
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      dfactory.setNamespaceAware(true);
      docBuilder = dfactory.newDocumentBuilder();
      context.put("DocBuilder", docBuilder);
      context.put("TransformerFactory", tFactory);
    }
    catch ( org.jzkit.configuration.api.ConfigurationException ce )
    {
      throw new RuntimeException(ce.toString());
    }
    catch ( javax.xml.parsers.ParserConfigurationException pce )
    {
      throw new RuntimeException(pce.toString());
    }
    finally {
      // configuration.close();
    }
  }

  public void setTemplateSource(String directory)
  {
    // cat.debug("setTemplateSource("+directory+")");
    this.template_source=directory;
  }

  public void registerType(String type, String classname) {
    log.debug("Register Type "+type+" class="+classname);

    try {
      Class c = Class.forName(classname);
      type_register.put(type, c);
    }
    catch ( Exception e ) {
      log.warn("Problem: ",e);
      throw new RuntimeException("Problem with transformer class",e);
    }
  }

  public void registerMapping(String from, String to, String type, String sheet) {
    log.debug("from "+from+" to "+to+" sheet="+sheet+" type="+type);

    try {
      Map props = new HashMap();
      props.put("Sheet", sheet);

      Class required_type = (Class) type_register.get(type);

      if ( required_type != null ) {

        Class[] param_types = new Class[] { String.class, String.class, Map.class, Map.class, ApplicationContext.class };
        Constructor c = required_type.getConstructor(param_types);
        if ( c != null ) {
          Object params[] = new Object[] { from, to, props, context, ctx };
          FragmentTransformer t = (FragmentTransformer) c.newInstance(params);
  
          all_transformations.add(t);
        }
        else {
          log.error("No constructor available for class "+required_type.getName());
        }
      }
      else {
        log.error("Unable to resolve a class for mapping "+type);
      }
    }
    catch ( Exception e ) {
      log.error("Problem registering mapping "+sheet+". Mapping will not be available",e);
    }
  }



  public Object convert(Document source, String from_schema, String to_schema, Map trans_properties) throws FragmentTransformationException {

    if ( source == null )
      throw new FragmentTransformationException("No source record for transformation");
    
    Object result = source;
    Document next_input = source;

    log.debug("Attempting to find transform from "+from_schema+" to "+to_schema);
    List templates = findTransformationSequence(from_schema, to_schema);

    // This supplies us with a vector of FragmentTransformation objects which propose different
    // kinds of transformation which should be applied of the source document in order to
    // generate the target schema

    if(templates==null) {
      throw new FragmentTransformationException("No transformation templates available (trying "+from_schema+" to "+to_schema+")");
    }

    log.debug("Located path : "+templates);

    // For each template
    for(Iterator templates_enum = templates.iterator(); templates_enum.hasNext(); ) {
      // String template_name = (String)templates.elementAt(i);
      FragmentTransformer transformation = (FragmentTransformer) templates_enum.next();

      // cat.debug("Transform...");
      result = transformation.transform(next_input, trans_properties);

      // Is there another step in the transformation?
      if( ( templates_enum.hasNext() ) && ( result instanceof Document ) ) {
        // Yes, so use the document resulting from the last transformation as the source document for the next one
        // cat.debug("Passing result of intermediate transformation into next transformer");
        next_input = (Document) result;
      }
    }

    return result;
  }

  public List lookupTransformersMatchingSource(List path_so_far, String source)
  {
    // Source should be a real stringified RecordFormatSpecification, possibly with nulls for
    // syntax and element set name. Each transformer object has a regexp match expression that
    // represents the incoming specs it is willing to accept. Each time this function is called
    // It must iterate over the set of available transforms, looking for ones whos regexp
    // matches source.
    List retval = new ArrayList();
    log.debug("lookupTransformersMatchingSource sz="+all_transformations.size()+" src="+source);

    for ( Iterator e = all_transformations.iterator(); e.hasNext(); ) {
      FragmentTransformer ft = (FragmentTransformer) e.next();

      if ( ( path_so_far != null ) && ( path_so_far.contains(ft) ) ) {
        log.warn("CYCLE DETECTED in transformation graph.. not re-adding target "+ft.getToSchema());
      }
      else if ( ft.isApplicableTo(source) ) {
        log.debug("Adding transform to "+ft.getToSchema());
        retval.add(ft);
      }
    }

    log.debug("lookupTransformersMatchingSource result="+retval.size());
    return retval;
  }

  public List findTransformationSequence(String source, String target) {
    // log.fine("findTransformationSequence("+source+","+target+")");

    List possible_transform_sequences = new ArrayList();
    List possible_transforms_from_source = lookupTransformersMatchingSource(null, source);

    for ( Iterator e = possible_transforms_from_source.iterator(); e.hasNext(); ) {
      List new_path = new ArrayList();
      new_path.add(e.next());
      possible_transform_sequences.add(new_path);
    }
    return breadthFirst(target, possible_transform_sequences);
  }

  /**
   * Perform a breadth first search of the available mappings, ending when we have
   * exhausted the search tree or the result of a transformation matches target.
   * @param target The sought target spec.
   * @param possible_transformation_set A List of List representing all current possible paths.
   */
  public List breadthFirst(String target, List possible_transformation_set) {
    List result = null;

    // Any possible transforrmations?
    if ( ( possible_transformation_set == null ) || ( possible_transformation_set.size() == 0 ) ) {
      // nope.. can't be done :(
      return null;
    }

    // We have potentials... See if any of the current set of possible transformations ends with target
    for ( Iterator e = possible_transformation_set.iterator(); e.hasNext(); ) {
      // Examine each path
      List current_path = (List) e.next();
     
      // Get the result spec
      FragmentTransformer ft = (FragmentTransformer) current_path.get(current_path.size()-1);

      // Does it "Match" the target. Target should really be a regexp, with some scoring for exact match over wildcards.
      if ( ft.getToSchema().equalsIgnoreCase(target) )
        result = current_path;
    }

    // Did we match?
    if ( result != null ) {
      // Yep  :)
      return result;
    }
    else {
      // No.. So expand possible_transformation_step with the next round of nodes.. Yep, it's expensive :(
      List new_transformation_set = new ArrayList();

      // for each potential path
      for ( Iterator e2 = possible_transformation_set.iterator(); e2.hasNext(); ) {
        // Get hold of the vector of transformations represented by that path
        List current_path_to_expand = (List) e2.next();

        // Look up the last transformation on the path
        FragmentTransformer ft = (FragmentTransformer) current_path_to_expand.get(current_path_to_expand.size()-1);

        // Generate a list of transformations applicable given the output of that transformation
        List possible_next_steps = lookupTransformersMatchingSource(current_path_to_expand, ft.getToSchema());

        // Add each of those potential transformations to the new set
        for ( Iterator e3 = possible_next_steps.iterator(); e3.hasNext(); ) {
          // Clone the current path
          List new_path = new java.util.ArrayList();
          new_path.addAll(current_path_to_expand);
          // And add on this new possible transformation
          new_path.add(e3.next());
          // Finally, add this new path to the new_transformation_set
          new_transformation_set.add(new_path);
        }
      }

      return breadthFirst(target, new_transformation_set);
    }
    
  }

  public Reference getReference() throws NamingException
  {
    return new Reference(FragmentTransformerService.class.getName());
  }

  public DOMResult createEmptyDOMResult()
  {
    return new DOMResult(docBuilder.newDocument());
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

}
