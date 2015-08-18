package org.jzkit.search.util.QueryModel.Internal;

import org.jzkit.search.util.QueryModel.*;

public abstract class QueryNodeVisitor
{
    private int depth = 0;

    public void visit(QueryNode qn)
    {
        // System.out.println("Abstract....");

        if ( qn instanceof InternalModelNamespaceNode )
            visit((InternalModelNamespaceNode)qn);
        else if ( qn instanceof ComplexNode )
            visit((ComplexNode)qn);
        else if ( qn instanceof AttrPlusTermNode )
            visit((AttrPlusTermNode)qn);
        else if ( qn instanceof InternalModelRootNode )
            visit((InternalModelRootNode)qn);
    }

    public void visit(InternalModelRootNode rn)
    {
        // System.out.println(depth+" InternalModelRootNode");
        depth++;
        visit(rn.getChild());
        depth--;
    }

    public void visit(InternalModelNamespaceNode rn)
    {
        // System.out.println(depth+" InternalModelNamespaceNode");
        depth++;
        visit(rn.getChild());
        depth--;
    }

    public void visit(ComplexNode cn)
    {
        // System.out.println(depth+" Complex");
        depth++;
        visit(cn.getLHS());
        visit(cn.getRHS());
        depth--;
    }

    public void visit(AttrPlusTermNode aptn)
    {
        // System.out.println(depth+" aptn");
        depth++;
        onAttrPlusTermNode(aptn);
        depth--;
    }

    // Override this method if needed.
    public abstract void onAttrPlusTermNode(AttrPlusTermNode aptn);
}
