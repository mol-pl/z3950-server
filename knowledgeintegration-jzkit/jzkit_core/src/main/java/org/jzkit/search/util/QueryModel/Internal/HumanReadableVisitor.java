package org.jzkit.search.util.QueryModel.Internal;

import java.util.Iterator;
import java.util.Vector;

public class HumanReadableVisitor 
{

    public static String toHumanReadableString(InternalModelRootNode rn) throws java.io.IOException 
    {
      HumanReadableQueryBundle bundle = new HumanReadableQueryBundle();
      visit(rn, bundle);
      return bundle.getHumanReadableQuery();
    }

    public static void toHumanReadableBundle(InternalModelRootNode rn, HumanReadableQueryBundle bundle) throws java.io.IOException 
    { 
        visit(rn, bundle);
    }
    
  
    public static void visit(QueryNode qn, HumanReadableQueryBundle bundle) throws java.io.IOException {
        if ( qn instanceof InternalModelRootNode )
            visit((InternalModelRootNode)qn, bundle);
        else if ( qn instanceof InternalModelNamespaceNode )
            visit((InternalModelNamespaceNode)qn, bundle);
        else if ( qn instanceof ComplexNode )
            visit((ComplexNode)qn, bundle);
        else if ( qn instanceof AttrPlusTermNode )
            visit((AttrPlusTermNode)qn, bundle);
    }

    public static void visit(InternalModelRootNode rn,HumanReadableQueryBundle bundle) throws java.io.IOException {
      visit(rn.getChild(),bundle);
    }

    public static void visit(InternalModelNamespaceNode rn, HumanReadableQueryBundle bundle) throws java.io.IOException {
        if ( rn.getAttrset() != null ) {
            // Do we want to write anything about the attrset ? Probably not,
            // Although the default attrset will be used when looking up use attrs
            // os.write("@attrset "+rn.getAttrset()+" ");
        }

        visit(rn.getChild(), bundle);
    }

    public static void visit(ComplexNode cn, HumanReadableQueryBundle bundle) throws java.io.IOException {
        int inumleft = 0;
        if ( cn.getLHS() != null ) 
          inumleft = cn.getLHS().countChildrenWithTerms();

        int inumright = 0;
        if ( cn.getRHS() != null ) 
          inumright = cn.getRHS().countChildrenWithTerms();

        if ( ( inumleft > 0 ) && ( inumright > 0 ) )
            bundle.append(" ( ");

        if ( inumleft > 0 ) {
            visit(cn.getLHS(),bundle);
        }

        if ( ( inumleft > 0 ) && ( inumright > 0 ) ) {
            switch( cn.getOp() )
            {
                case 1:
                    bundle.append(" and ");
                    break;
                case 2:
                    bundle.append(" or ");
                    break;
                case 3:
                    bundle.append(" andnot ");
                    break;
                case 4:
                    bundle.append(" Close to ");
                    break;
                default:
                    bundle.append(" ERROR ");
                    break;
            }
        }

        if ( inumright > 0 ) {
            visit(cn.getRHS(),bundle);
        }

        if ( ( inumleft > 0 ) && ( inumright > 0 ) )
            bundle.append(" ) ");

    }

    public static void visit(AttrPlusTermNode aptn, HumanReadableQueryBundle bundle) throws java.io.IOException {

      Object term = aptn.getTerm();

      TermValueBundle value_bundle = aptn.getTermValueBundle(true);
      
      if ( term instanceof String ) {
        bundle.append(" "+value_bundle.getStringValue()+" ");
      }
      else if ( ( term instanceof Vector ) || ( term instanceof Object ) ) {
        int multi_term_action = 0;
        Object multi_term_action_val = aptn.getAttr("MultiTermOp");

        if ( ( multi_term_action_val != null ) && ( multi_term_action_val.toString().equals("OR") ) )
          multi_term_action=1;

        if ( multi_term_action == 1 )
          bundle.append(" Matches one or more of ");
        else
          bundle.append(" Matches all of ");

        bundle.append("[");
        bundle.append(" "+value_bundle.getStringValue()+" ");
        bundle.append("]");
      }
      
      Iterator i = value_bundle.getValueIterator();
      while(i.hasNext()) {
        String value = (String) i.next();
        if(aptn.getAccessPoint() instanceof String)
          bundle.addTerm(aptn.getAccessPoint().toString(), value);
        else
        {
          if ( aptn.getAccessPoint() != null ) {
            String triple = aptn.getAccessPoint().toString();
            int spot     = triple.lastIndexOf(".");
            if(spot!=-1)            
              triple = triple.substring(spot+1, triple.length());
            bundle.addTerm(triple, value);
          }
          else {
            bundle.addTerm("Unqualified", value);
          }
        }
      }
    }
}
