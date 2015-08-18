package org.jzkit.search.util.QueryModel.Internal;

import java.util.Hashtable;

import org.jzkit.search.util.QueryModel.*;
import java.io.StringWriter;

public class InternalModelNamespaceNode extends QueryNode
{
    private QueryNode the_child = null;
    private String attrset = null;

    public InternalModelNamespaceNode() {
        super();
    }

    public InternalModelNamespaceNode(String attrset) {
        super();
        this.attrset = attrset;
    }

    public InternalModelNamespaceNode(String attrset, QueryNode child) {
        super();
        this.attrset = attrset;
        this.the_child = child;
    }

    public QueryNode getChild() {
        return the_child;
    }

    // If you are using this function, it's important that the child has been
    // created with the root node set = this node.
    public void setChild(QueryNode c) {
      the_child = c;
    }

    public int countChildrenWithTerms() {
      if ( the_child == null )
        return 0;

      return ( the_child.countChildrenWithTerms() );
    }

    public int countChildren() {
      if ( the_child == null )
        return 0;

      return ( the_child.countChildren() + 1 );
    }

    public void setAttrset(String _attrset) {
        attrset = _attrset;
    }

    public String getAttrset() {
        return attrset;
    }

    public void clearAllTerms() {
        if ( the_child != null )
            the_child.clearAllTerms();
    }

    public String toString() {
      return "Namespace ["+attrset+"] ( "+the_child.toString()+" )";
    }

    public void dump(StringWriter sw) {
      sw.write("Namespace [");
      sw.write(attrset);
      sw.write("] ( ");
      if ( the_child != null ) {
        the_child.dump(sw);
      }
      else {
        System.err.println("\n***WARNING*** child if internal model namespace node is null\n");
      }
      sw.write(" ) ");
    }
}
