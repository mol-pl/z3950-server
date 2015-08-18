package org.jzkit.search.util.QueryModel.Internal;

import org.jzkit.search.util.QueryModel.*;
import java.util.Map;

/**
 *  Import a set of values from a map
 */
public class HashtableValueImportVisitor extends QueryNodeVisitor
{
  Map values = null;

  public HashtableValueImportVisitor(Map values) {
    System.err.println("new HashtableValueImportVisitor");
    this.values = values;
  }

  public void onAttrPlusTermNode(AttrPlusTermNode aptn) {
    Object o = values.get(aptn.getNodeName());

    System.err.println("Checking node "+aptn.getNodeName()+", value="+o);

    if ( o != null )
      aptn.setTerm(o);
  }
}
