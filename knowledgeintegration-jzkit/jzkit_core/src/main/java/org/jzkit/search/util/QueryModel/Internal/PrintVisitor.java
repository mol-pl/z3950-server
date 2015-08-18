package org.jzkit.search.util.QueryModel.Internal;

import org.jzkit.search.util.QueryModel.*;

public class PrintVisitor
{
    public static void visit(QueryNode qn, int depth)
    {
        if ( qn instanceof InternalModelRootNode )
            visit((InternalModelRootNode)qn,depth);
        if ( qn instanceof InternalModelNamespaceNode )
            visit((InternalModelNamespaceNode)qn,depth);
        else if ( qn instanceof ComplexNode )
            visit((ComplexNode)qn,depth);
        else if ( qn instanceof AttrPlusTermNode )
            visit((AttrPlusTermNode)qn,depth);
    }

    public static void visit(InternalModelRootNode rn, int depth)
    {
        visit(rn.getChild(), depth+1);
    }

    public static void visit(InternalModelNamespaceNode rn, int depth)
    {
        visit(rn.getChild(), depth+1);
    }

    public static void visit(ComplexNode cn, int depth)
    {
        visit(cn.getLHS(), depth+1);

        String out="";
        for ( int i=0; i<depth; i++ )
            out=out+"    ";

        System.out.println(out+cn);
        
        visit(cn.getRHS(), depth+1);
    }

    public static void visit(AttrPlusTermNode aptn, int depth)
    {
        String out="";
        for ( int i=0; i<depth; i++ )
            out=out+"    ";

        System.out.println(out+aptn);
    }
}
