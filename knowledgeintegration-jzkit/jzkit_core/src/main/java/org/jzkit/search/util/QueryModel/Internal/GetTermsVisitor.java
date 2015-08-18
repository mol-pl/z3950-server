package org.jzkit.search.util.QueryModel.Internal;

import org.jzkit.search.util.QueryModel.*;

import java.util.Vector;
import java.util.Enumeration;

public class GetTermsVisitor
{
    public static void visit(QueryNode qn, Vector result)
    {
        if ( qn instanceof InternalModelNamespaceNode )
            visit((InternalModelNamespaceNode)qn, result);
        else if ( qn instanceof ComplexNode )
            visit((ComplexNode)qn, result);
        else if ( qn instanceof AttrPlusTermNode )
            visit((AttrPlusTermNode)qn, result);
    }

    public static void visit(InternalModelNamespaceNode rn, Vector result)
    {
        visit(rn.getChild(), result);
    }

    public static void visit(ComplexNode cn, Vector result)
    {
        visit(cn.getRHS(), result);
        visit(cn.getLHS(), result);
    }

    public static void visit(AttrPlusTermNode aptn, Vector result)
    {
      Object term = aptn.getTerm();

      if ( term != null )
      {
	if ( ( term instanceof Vector ) && ( ((Vector)term).size() > 0 ) )
        {
          for ( Enumeration e = ((Vector)term).elements(); e.hasMoreElements(); )
          {
            term = (String)e.nextElement();
            if ( ( term != null ) && ( !term.equals("") ) )
              result.add(term);
          }
	}
        else
	{
          result.add(term.toString());
	}
      }
    }
}
