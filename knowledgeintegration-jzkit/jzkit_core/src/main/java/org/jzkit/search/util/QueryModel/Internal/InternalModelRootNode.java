package org.jzkit.search.util.QueryModel.Internal;

import java.util.HashMap;

import org.jzkit.search.util.QueryModel.*;
import java.io.StringWriter;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.beans.XMLDecoder;

public class InternalModelRootNode extends QueryNode implements QueryModel
{
    private QueryNode the_child = null;

    public InternalModelRootNode() {
        super();
    }

    public InternalModelRootNode(QueryNode child) {
        super();
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
        return ( the_child.countChildrenWithTerms() );
    }

    public int countChildren() {
        return ( the_child.countChildren() + 1 );
    }

    public void clearAllTerms() {
        if ( the_child != null )
            the_child.clearAllTerms();
    }

    public ComplexNode createComplexNode(QueryNode lhs, QueryNode rhs, int op) {
      return createComplexNode(lhs,rhs,op,null);
    }

    public ComplexNode createComplexNode(QueryNode lhs, QueryNode rhs, int op, String name) {
      ComplexNode result = new ComplexNode(lhs, rhs, op, name);
      return result;
    }

    public AttrPlusTermNode createAttrPlusTermNode(HashMap attrs,
                                                   Object term,
                                                   String nodename) {
      AttrPlusTermNode result = new AttrPlusTermNode(attrs, term, nodename,AttrPlusTermNode.AND_TERMS);
      return result;
    }

    public InternalModelRootNode toInternalQueryModel(org.springframework.context.ApplicationContext ctx) throws InvalidQueryException {
      return this;
    }

    public String toString() {
      StringWriter sw = new StringWriter();
      dump(sw);
      return sw.toString();
    }

    public void dump(StringWriter sw) {
      sw.write("IMRoot ( ");
      the_child.dump(sw);
      sw.write(" ) ");
    }

  public static InternalModelRootNode createInstanceFromClasspathFile(String classpath_resource) throws InvalidQueryException {
    InternalModelRootNode result = null;
    try {
      URL res_url = InternalModelRootNode.class.getResource(classpath_resource);
      if ( res_url == null )
        throw new InvalidQueryException("Unable to locate query def "+classpath_resource);
      XMLDecoder d = new XMLDecoder( new BufferedInputStream( res_url.openStream() ) );
      result = (InternalModelRootNode) d.readObject();
      d.close();
    }
    catch ( java.io.IOException ioe ) {
      ioe.printStackTrace();
      throw new InvalidQueryException(ioe.toString(),ioe);
    }
    finally {
    }
    return result;
  }

  public static InternalModelRootNode createInstanceFromString(String serialised_block) throws InvalidQueryException {
    InternalModelRootNode result = null;
    try {
      XMLDecoder d = new XMLDecoder( new BufferedInputStream( new ByteArrayInputStream(serialised_block.getBytes())  ) );
      result = (InternalModelRootNode) d.readObject();
      d.close();
    }
    finally {
    }

    return result;
  }
}
