package org.jzkit.search.util.Profile;

import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;
import java.util.Set;
import java.util.HashSet;
import javax.persistence.*;

@Entity
@Table(name="JZ_RULE_NODE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="NODE_TYPE", discriminatorType=javax.persistence.DiscriminatorType.INTEGER)
public abstract class RuleNodeDBO {

  protected Long id = null;
  protected Set child_rules = new HashSet();
  protected RuleNodeDBO parent = null;

  public RuleNodeDBO() {
    System.err.println("new RuleNodeDBO");
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

  @OneToMany(mappedBy="parent",cascade={CascadeType.ALL})
  public Set<RuleNodeDBO> getChildren() {
    return child_rules;
  }

  public void setChildren(Set<RuleNodeDBO> child_rules) {
    this.child_rules=child_rules;
  }

  @ManyToOne(cascade=CascadeType.ALL)
  @JoinColumn(name="PARENT_NODE")
  public RuleNodeDBO getParent() {
    return parent;
  }

  public void setParent(RuleNodeDBO parent) {
    System.err.println("SetParent "+parent);
    this.parent = parent;
  }

  public abstract boolean isValid(String default_namespace, AttrPlusTermNode aptn, QueryVerifyResult qvr);

  @Transient
  public abstract String getDesc();

  @Transient
  public abstract String getNodeType();
}
