package org.jzkit.search.util.QueryModel.Internal;

import org.jzkit.search.util.QueryModel.*;
import java.util.HashMap;
import java.io.StringWriter;

public abstract class QueryNode implements java.io.Serializable {

  protected String node_name = null;
  protected HashMap attrs = new HashMap();

  protected QueryNode() {
  }

  public abstract int countChildrenWithTerms();
  public abstract int countChildren();
  public abstract void clearAllTerms();
  public abstract void dump(StringWriter sw);

  public String getNodeName() {
    return node_name;
  }

  public void setNodeName(String name) {
    node_name = name;
  }

  public void setAttr(String name, Object value) {
    attrs.put(name, value);
  }

  public HashMap getAttrs() {
    return attrs;
  }

  public void setAttrs(HashMap attrs) {
    this.attrs = attrs;
  }

}
