package org.jzkit.search.util.QueryModel.Internal;

import org.jzkit.search.util.QueryModel.*;
import java.io.StringWriter;

public class ComplexNode extends QueryNode
{
    QueryNode lhs = null;
    QueryNode rhs = null;

    public static final int COMPLEX_AND  = 1;
    public static final int COMPLEX_OR  = 2;
    public static final int COMPLEX_ANDNOT  = 3;
    public static final int COMPLEX_PROX  = 4;
    // 0=none, 1=And, 2=Or, 3=AndNot, 4=Prox
    int op = 0;

    // Will add attr map here for prox params later

    public ComplexNode(QueryNode lhs, QueryNode rhs, int op, String nodename)
    {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
        if ( nodename != null )
          this.setNodeName(nodename);

        if ( ( lhs == null ) || ( rhs == null ) )
          System.err.println("new ComplexNode "+nodename+" has lhs="+lhs+" rhs="+rhs);
    }

    public ComplexNode(QueryNode lhs, QueryNode rhs, int op) {
      this(lhs,rhs,op,null);
    }

    public QueryNode getLHS() {
        return lhs;
    }

    public QueryNode getRHS()
    {
        return rhs;
    }

    public int countChildrenWithTerms()
    {
        return ( lhs.countChildrenWithTerms() + rhs.countChildrenWithTerms() );
    }

    public int countChildren()
    {
        return ( lhs.countChildren() + rhs.countChildren() + 2 );
    }

    public void setOp(int opval)
    {
        op = opval;
    }

    public int getOp()
    {
        return op;
    }

    public String toString()
    {
        switch(op)
        {
            case 1:
                return(getNodeName()+" Boolean AND node");
            case 2:
                return(getNodeName()+" Boolean OR node");
            case 3:
                return(getNodeName()+" Boolean ANDNOT node");
            case 4:
                return(getNodeName()+" PROX node");
            case 0:
            default:
                return(getNodeName()+" Unknown complex op node");
        }
    }

    public void clearAllTerms()
    {
      if ( lhs != null )
        lhs.clearAllTerms();
      if ( rhs != null )
        rhs.clearAllTerms();
    }

    public void dump(StringWriter sw)
    {
      switch(op)
      {
        case 1:
          sw.write("AND");
          break;
        case 2:
          sw.write("OR");
          break;
        case 3:
          sw.write("ANDNOT");
          break;
        case 4:
          sw.write("PROX");
          break;
        case 0:
        default:
          sw.write("Unknown");
        }
      sw.write("(");
      lhs.dump(sw);
      sw.write(",");
      rhs.dump(sw);
      sw.write(")");
    }
}
