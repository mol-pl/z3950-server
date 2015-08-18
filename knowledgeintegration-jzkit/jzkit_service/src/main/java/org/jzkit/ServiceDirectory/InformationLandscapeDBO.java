package org.jzkit.ServiceDirectory;

import java.util.*;
import javax.persistence.*;
import org.jzkit.ServiceDirectory.CollectionSelection.*;

/** A business entity class representing an Collection Description
  *
  * @author Ian Ibbotson
  * @since 1.0
  */
@Entity
@Table(name="JZ_LANDSCAPE")
@org.hibernate.annotations.Entity(dynamicUpdate=true, dynamicInsert=true)
public class InformationLandscapeDBO {

  private Long id;
  private String code;
  private String landscape_name;
  private CollectionSelectionRuleDBO collection_selection_rule;

  public InformationLandscapeDBO() {
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

  @Column(name="CODE",length=64)
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Column(name="LANDSCAPE_NAME",length=64)
  public String getLandscapeName() {
    return landscape_name;
  }

  public void setLandscapeName(String landscape_name) {
    this.landscape_name = landscape_name;
  }

  @ManyToOne(cascade=CascadeType.ALL)
  @JoinColumn(name="SELECTION_RULE_FK")
  public CollectionSelectionRuleDBO getCollectionSelectionRule() {
    return collection_selection_rule;
  }

  public void setCollectionSelectionRule(CollectionSelectionRuleDBO collection_selection_rule) {
    this.collection_selection_rule = collection_selection_rule;
  }

  public String toString() {
    return code+":"+landscape_name;
  }
}
