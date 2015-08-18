package org.jzkit.search.util.Profile;

import org.jzkit.search.util.QueryModel.Internal.AttrValue;
import java.util.*;
import javax.persistence.*;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.IndexColumn;

/** A business entity class representing an Concept
  *
  * @author Ian Ibbotson
  * @since 1.0
  */
@Entity
@Table(name="JZ_CROSSWALK_ATTR_MAPPING")
public class AttrMappingDBO {

  private Long id = null;
  private Set target_attrs = new HashSet();
  private String source_attr_value = null;
  private CrosswalkDBO parent = null;

  public AttrMappingDBO() {
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

  public void addTarget(String namespace_identifier, Object value) {
    target_attrs.add(new AttrValue(namespace_identifier, value.toString()));
  }

  public String toString() {
    if ( target_attrs != null )
      return target_attrs.toString();
    return "Empty target attr list";
  }

  // List of org.jzkit.search.util.QueryModel.Internal.AttrValue 
  @CollectionOfElements(fetch=javax.persistence.FetchType.EAGER)
  @JoinTable(name="JZ_ATTR_MAPPING_TARGET",joinColumns=@JoinColumn(name="ATTR_MAPPING_FK"))
  public Set<AttrValue> getTargetAttrs() {
    return target_attrs;
  }

  public void setTargetAttrs(Set<AttrValue> target_attrs) {
    this.target_attrs = target_attrs;
  }

  @Column(name="SOURCE_ATTR_VALUE",length=64)
  public String getSourceAttrValue() {
    return source_attr_value;
  }

  public void setSourceAttrValue(String value) {
    this.source_attr_value = value;
  }

  @ManyToOne(cascade=CascadeType.ALL)
  @JoinColumn(name="CROSSWALK_FK")
  public CrosswalkDBO getParent() {
    return parent;
  }

  public void setParent(CrosswalkDBO parent) {
    this.parent = parent;
  }

} 
