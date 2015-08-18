package org.jzkit.ServiceDirectory.CollectionSelection;

import java.util.*;
import javax.persistence.*;

/** A business entity class representing an Collection Description
  *
  * @author Ian Ibbotson
  * @since 1.0
  * A set operator, UNION, INTERSECT or MINUS that allows a collection selection rule to be
  * composed of many different rules. E.G.
  * Collections with ContenType "Audio" INTERSECT Collections with Subject "Science" to look for collections 
  * supporting audio resources about science.
  */
@javax.persistence.Entity
@DiscriminatorValue("2")
public class SetOperatorDBO extends CollectionSelectionRuleDBO {

  /** Everything selected by all child rules */
  public static final int RELATION_UNION = 0;

  /** Everything in first set not in any subsequent set */
  public static final int RELATION_MINUS = 1;

  /** Everything in all sets */
  public static final int RELATION_INTERSECT = 2;

  private int relation = 0;
  private List<CollectionSelectionRuleDBO> child_rules = new ArrayList();

  public SetOperatorDBO(int relation,
                        CollectionSelectionRuleDBO[] child_rules) {
    this.relation = relation;
    for ( int i=0; i<child_rules.length; i++ ) {
      this.child_rules.add(child_rules[i]);
    }
  }

  public SetOperatorDBO() {
  }

  @Column(name="RELATION")
  public int getRelation() {
    return relation;
  }

  public void setRelation(int relation) {
    this.relation = relation;
  }

  @OneToMany(mappedBy="parent")
  public List<CollectionSelectionRuleDBO> getChildRules() {
    return child_rules;
  }

  public void setChildRules(List<CollectionSelectionRuleDBO> child_rules) {
    this.child_rules = child_rules;
  }
}
