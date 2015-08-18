package org.jzkit.search.util.Profile;

import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;
import java.util.*;
import javax.persistence.*;

@javax.persistence.Entity
@DiscriminatorValue("3")
public class AttrAndRuleDBO extends BooleanRuleNodeDBO {

  public AttrAndRuleDBO() {
    super();
    System.err.println("new AttrAndRuleDBO");
  }

  public AttrAndRuleDBO(RuleNodeDBO[] children) {
    super(children);
  }

  public boolean isValid(String default_namespace, AttrPlusTermNode aptn, QueryVerifyResult qvr) {
    boolean result = true;
    for ( Iterator i = child_rules.iterator(); ( ( i.hasNext() ) && ( result ) ); )
    {
      RuleNodeDBO next = (RuleNodeDBO)i.next();
      result = result && next.isValid(default_namespace,aptn,qvr);
    }
    return result;
  }

  @Transient
  public String getDesc() {
    return "Query term must match all of";
  }
                                                                                                                                        
  @Transient
  public String getNodeType() {
    return "";
  }

  public String toString() {
    return "Match All";
  }

}
