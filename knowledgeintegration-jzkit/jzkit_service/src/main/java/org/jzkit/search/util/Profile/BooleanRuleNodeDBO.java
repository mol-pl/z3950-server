package org.jzkit.search.util.Profile;

import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;

import java.util.Vector;
import javax.persistence.*;

@javax.persistence.Entity
@DiscriminatorValue("1")
public abstract class BooleanRuleNodeDBO extends RuleNodeDBO
{
  public BooleanRuleNodeDBO() {
    super();
    System.err.println("New boolean node");
  }

  public BooleanRuleNodeDBO(RuleNodeDBO[] children) {
    super();
    System.err.println("New boolean node[childArr]");
    for ( int i=0; i<children.length;i++ ) {
      child_rules.add(children[i]);
      children[i].setParent(this);
    }
  }

  public void add(RuleNodeDBO r) {
    child_rules.add(r);
    r.setParent(this);
  }
}
