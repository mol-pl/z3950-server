package org.jzkit.search.util.Profile;

import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;
import java.util.*;
import javax.persistence.*;

@javax.persistence.Entity
@DiscriminatorValue("2")
public class AttrOrRuleDBO extends BooleanRuleNodeDBO {

  public AttrOrRuleDBO() {
    super();
    System.err.println("AttrOrRuleDBO");
  }

  public AttrOrRuleDBO(RuleNodeDBO[] children) {
    super(children);
    System.err.println("AttrOrRuleDBO[childArray]");
  }
                                                                                                                                        

  public boolean isValid(String default_namespace, AttrPlusTermNode aptn, QueryVerifyResult qvr) {
    boolean result = false;
    String first_failing_attr = null;
    for ( Iterator i = child_rules.iterator(); ( ( i.hasNext() ) && ( !result ) ); )
    {
      RuleNodeDBO next = (RuleNodeDBO)i.next();
      result = result || next.isValid(default_namespace,aptn,qvr);
      if ( first_failing_attr == null )
        first_failing_attr = qvr.getFailingAttr();
    }

    // Since this was an OR, we might should use the first failing attr. Perhaps we should use them all?
    // If one of the children matched, don't record any failing attr.
    if ( !result )
    {
      qvr.setFailingAttr(first_failing_attr);
      // Log maybe : "The query is not conformant because the profile demands that at least one of the following
      // are true: <List child nodes>.
    }
    else
    {
      qvr.setFailingAttr(null);
    }


    // Bacause this is an OR node, we only need one of the children to be true.
    qvr.setIsValid(result);

    return result;
  }

  @Transient
  public String getDesc()
  {
    return "Query term must match at least one of";
  }
                                                                                                                                        
  @Transient
  public String getNodeType()
  {
    return "";
  }

  public String toString()
  {
    return "Match at least one of";
  }
}
