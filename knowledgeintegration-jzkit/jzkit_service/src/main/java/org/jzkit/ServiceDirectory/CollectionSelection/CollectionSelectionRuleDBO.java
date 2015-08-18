package org.jzkit.ServiceDirectory.CollectionSelection;

import java.util.*;
import javax.persistence.*;

/** A business entity class representing an Collection Description
  *
  * @author Ian Ibbotson
  * @since 1.0
  */
@Entity
@Table(name="JZ_COLLECTION_SELECTION_RULE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="RULE_TYPE", discriminatorType=javax.persistence.DiscriminatorType.INTEGER)
@DiscriminatorValue("0")
@org.hibernate.annotations.Entity(dynamicUpdate=true, dynamicInsert=true)
public class CollectionSelectionRuleDBO {

  private Long id;
  private SetOperatorDBO parent = null;

  public CollectionSelectionRuleDBO() {
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

  @ManyToOne(cascade=CascadeType.ALL)
  @JoinColumn(name="PARENT_RULE_FK")
  public SetOperatorDBO getParent() {
    return parent;
  }
 
  public void setParent(SetOperatorDBO parent) {
    this.parent = parent;
  }
}
