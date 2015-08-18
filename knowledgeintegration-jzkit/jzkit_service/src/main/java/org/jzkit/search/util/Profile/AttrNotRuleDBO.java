package org.jzkit.search.util.Profile;

import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;
import java.util.Iterator;
import javax.persistence.*;

@javax.persistence.Entity
@DiscriminatorValue("4")
public class AttrNotRuleDBO extends BooleanRuleNodeDBO {

  @Transient
  public RuleNodeDBO getChild() {
    return (RuleNodeDBO)(child_rules.iterator().next());
  }
                                                                                                                                        
  public boolean isValid(String default_namespace, AttrPlusTermNode aptn, QueryVerifyResult qvr) {
    Iterator i = child_rules.iterator();
    if ( i.hasNext() ) {
      RuleNodeDBO child = (RuleNodeDBO)i.next();
      return ! child.isValid(default_namespace,aptn, qvr);
    }
    else
      return true;
  }

  @Transient
  public String getDesc() {
    return "The Query must not match";
  }
                                                                                                                                        
  @Transient
  public String getNodeType() {
    return "";
  }

  public String toString() {
    return "Must not match";
  }

}
