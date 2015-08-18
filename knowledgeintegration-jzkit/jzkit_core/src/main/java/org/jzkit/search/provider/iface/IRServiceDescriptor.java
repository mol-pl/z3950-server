package org.jzkit.search.provider.iface;

import java.util.*;
import javax.persistence.*;
import org.hibernate.annotations.CollectionOfElements;

/**
 * Describes a connection to an IR Service. Should be storable in a database, and represent an extensible set
 * of properties that can be extended on a per-protocol basis, but with a common set of base attributes.
 * This is different to a service description in the sense that it is intended to only represent base connection
 * information, not a full metadata profile of the target. Having a service descriptor should allow other
 * services such as explain to obtain a full metadata description of the target.
 *
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * Company:     Knowledge Integration Ltd.
 * License:     A license.txt file should is distributed along with this software
 *
 * @author Ian Ibbotson
 * @version $Id: SearchServiceFactory.java,v 1.5 2004/11/27 09:18:57 ibbo Exp $
 * @see org.jzkit.SearchProvider.iface.Searchable
 */ 
@Entity
@Table(name="JZ_IR_SERVICE_DESCRIPTOR")
public class IRServiceDescriptor {
 
  private Long id;
  private String protocol;
  private String code;

  private Map preferences = new HashMap();
  private List auth_styles = new ArrayList();

  public IRServiceDescriptor() {
  }

  /**
   * Process a comma separated list of bean_property=value pairs
   */
  public IRServiceDescriptor(String simple_string_descriptor) {
    if ( simple_string_descriptor != null ) {
      String[] assignments = tokenize(simple_string_descriptor);
      System.err.println("Processing "+assignments.length+" assignments");
      for ( int i=0; i<assignments.length; i++ ) {
        String[] components = assignments[i].split("=");
        System.err.println(components[0]+"="+components[1]);
        if ( components.length==2 ) {
          if ( components[0].equals("proto") ) {
            setProtocol(components[1]);
          }
          else if ( components[0].equals("code") ) {
            setCode(components[1]);
          }
          else {
            if ( components[1].startsWith("'") ) {
              preferences.put(components[0],components[1].substring(1,components[1].length()-1));
            }
            else
              preferences.put(components[0],components[1]);
          }
        }
      }
      if ( code == null ) {
        code=java.util.UUID.randomUUID().toString();
      }
    }
  }

  private static String[] tokenize(String descriptor) {
    // return simple_string_descriptor.split(",");
    return descriptor.split(",(?=([^']*'[^']*')*(?![^']*'))");
  }

  @Id
  @Column(name="ID")
  @GeneratedValue(strategy=GenerationType.AUTO)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name="CODE",length=50)
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Column(name="PROTO",length=50)
  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  @CollectionOfElements(fetch=javax.persistence.FetchType.EAGER)
  @JoinTable(name="JZ_IR_DESCRIPTOR_PROPS",joinColumns=@JoinColumn(name="DESCRIPTOR_ID"))
  @org.hibernate.annotations.MapKey(columns={@Column(name="PROP_NAME",length=128)})
  @Column(name="PROP_VALUE",length=128)
  public Map<String,String> getPreferences() {
    return preferences;
  }

  public void setPreferences(Map<String,String> preferences) {
    this.preferences = preferences;
  }

  public void setPreference(String name, Object value) {
    preferences.put(name,value);
  }

  public Object getPreference(String name, Object default_value) {
    Object result = preferences.get(name);
    if ( result == null )
      return default_value;
    return result;
  }

  public String toString() {
    return code+" "+protocol+" "+preferences;
  }
}
