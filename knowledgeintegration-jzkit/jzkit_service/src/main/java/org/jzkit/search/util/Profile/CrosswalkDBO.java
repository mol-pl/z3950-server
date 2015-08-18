package org.jzkit.search.util.Profile;

import org.jzkit.search.util.QueryModel.Internal.AttrValue;
import java.util.*;
import javax.persistence.*;

/**
 * Crosswalks enable JZKit to rewrite attributes for a query given in one profile to
 * semantically equivalent attributes in another profile. For example, most Z39.50 targets
 * suppot the bib-1 use attribute 4 for "Title" search (bib-1:1.4) but most srw targets use dc:title
 * Crosswalks list the possible semantic equivalents independently of given targets. 
 * All targets do is to report what they are willing to accept. JZkit then tries all possible
 * rewrites of a query tree until it fails, or finds one that matches the target being queried.
 *
 * @author <a href="mailto:ian.ibbotson@">Ian Ibbotson</a>
 */
@Entity
@Table(name="JZ_CROSSWALK")
public class CrosswalkDBO {

  private Long id = null;
  private String scope = null;
  private String source_namespace = null;
  private Map mappings = new HashMap();

  public CrosswalkDBO() {
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

  @Column(name="SCOPE",length=50)
  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  @Column(name="SOURCE_NAMESPACE",length=50)
  public String getSourceNamespace() {
    return source_namespace;
  }

  public void setSourceNamespace(String source_namespace) {
    this.source_namespace = source_namespace;
  }

  @OneToMany(mappedBy="parent",cascade={CascadeType.ALL})
  @MapKey(name="sourceAttrValue")
  public Map<String,org.jzkit.search.util.Profile.AttrMappingDBO> getMappings() {
    return mappings;
  }

  public void setMappings(Map<String,org.jzkit.search.util.Profile.AttrMappingDBO> mappings) {
    this.mappings = mappings; 
  }

  public void registerMapping(AttrMappingDBO mapping) {
    getMappings().put(mapping.getSourceAttrValue(),mapping);
    mapping.setParent(this);
  }

  public AttrMappingDBO lookupMapping(Object source_value) {
    return (AttrMappingDBO) getMappings().get(source_value);
  }
} 
