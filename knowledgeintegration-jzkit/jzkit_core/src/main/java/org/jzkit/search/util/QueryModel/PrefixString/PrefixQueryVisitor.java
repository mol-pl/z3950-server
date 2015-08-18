// Title:           QueryNodeVisitor
// Description:     Test visitor for structured query tree
// Copyright:       Copyright (C) 1999-2000, Knowledge Integration Ltd
// Company:         Knowledge Integration Ltd
// @author:         Ian Ibbotson ( ibbo@k-int.com )
// @version:        $Id: PrefixQueryVisitor.java,v 1.3 2005/10/12 11:13:54 ibbo Exp $
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2.1 of
// the license, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite
// 330, Boston, MA  02111-1307, USA.
// 


package org.jzkit.search.util.QueryModel.PrefixString;

import java.io.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import java.util.*;

public class PrefixQueryVisitor
{
    public static String toPQF(QueryNode qn) throws java.io.IOException {
      StringWriter sw = new StringWriter();
      visit(qn,sw,null);
      return sw.toString();
    }

    public static final void visit(QueryNode qn, Writer os, String attrset) throws java.io.IOException {
        if ( qn instanceof InternalModelRootNode )
            visit((InternalModelRootNode)qn, os, attrset);
        else if ( qn instanceof InternalModelNamespaceNode )
            visit((InternalModelNamespaceNode)qn, os, attrset);
        else if ( qn instanceof ComplexNode )
            visit((ComplexNode)qn, os, attrset);
        else if ( qn instanceof AttrPlusTermNode )
            visit((AttrPlusTermNode)qn, os, attrset);
    }

    public static final void visit(InternalModelRootNode rn, Writer os, String attrset) throws java.io.IOException {
        visit(rn.getChild(), os, attrset);
    }

    public static final void visit(InternalModelNamespaceNode nsn, Writer os, String attrset) throws java.io.IOException {
        if ( nsn.getAttrset() != null ) {
            os.write("@attrset "+nsn.getAttrset()+" ");
        }
        visit(nsn.getChild(), os, nsn.getAttrset());
    }

    public static final void visit(ComplexNode cn, Writer os, String attrset) throws java.io.IOException {
        int inumleft = cn.getLHS().countChildrenWithTerms();
        int inumright = cn.getRHS().countChildrenWithTerms();

        if ( ( inumleft > 0 ) &&
             ( inumright > 0 ) ) {
            switch( cn.getOp() ) {
                case 1:
                    os.write("@and ");
                    break;
                case 2:
                    os.write("@or ");
                    break;
                case 3:
                    os.write("@andnot ");
                    break;
                case 4:
                    os.write("@PROX ");
                    break;
                default:
                    os.write("@ERROR ");
                    break;
            }
        }


        if ( inumleft > 0 ) {
            visit(cn.getLHS(),os, attrset);
        }

        if ( inumright > 0 ) {
            visit(cn.getRHS(),os, attrset);
        }
    }

    public static final void visit(AttrPlusTermNode aptn, Writer os, String attrset) throws java.io.IOException {
        Object term = aptn.getTerm();

        if ( ( term != null ) && ( term instanceof Vector ) ) {
            Vector terms = (Vector) term;
            int j = terms.size() - 1;

            for(int i = 0; i<=j; i++) {
                switch ( aptn.getDefaultMultiTermOperator() ) {
                    case AttrPlusTermNode.OR_TERMS:
                        os.write("@or ");
                        break;
                    case AttrPlusTermNode.AND_TERMS:
                        os.write("@and ");
                        break;
                    default:
                        // if we aren't defaulting multi term operators
                        break;
                }
            }

            if ( aptn.getDefaultMultiTermOperator() == AttrPlusTermNode.NONE ) {
              addAttrs(aptn,os,attrset);
              for(int i = 0; i<=j; i++) {
                os.write("\"");
                os.write((String)(terms.elementAt(i)));
                os.write("\" ");
              }
            }
            else {
              for(int i = 0; i<=j; i++) {
                addAttrs(aptn,os,attrset);
                os.write("\"");
                os.write((String)(terms.elementAt(i)));
                os.write("\" ");
              }
            }

            os.flush();
        }
        else if ( term != null ) {
            addAttrs(aptn,os,attrset);
            os.write("\""+aptn.getTerm()+"\" ");
            os.flush();
        }
    }

    public static final void addAttrs(AttrPlusTermNode aptn, Writer os, String attrset) throws java.io.IOException {
      add(aptn,os,attrset,"AccessPoint");
      add(aptn,os,attrset,"Relation");
      add(aptn,os,attrset,"Position");
      add(aptn,os,attrset,"Structure");
      add(aptn,os,attrset,"Truncation");
      add(aptn,os,attrset,"Completeness");
    }

    public static final void add(AttrPlusTermNode aptn, Writer os, String attrset, String name) throws java.io.IOException {
      Object att = aptn.getAttr(name);
      if ( att != null ) {
        if ( att instanceof AttrValue ) {
          AttrValue attr = (AttrValue)att;
          String components[] = attr.getValue().split("\\.");
          if ( components.length == 2 ) {
            if ( ( attr.getNamespaceIdentifier() == null ) || ( attr.getNamespaceIdentifier().equals(attrset) ) )
              os.write("@attr "+components[0]+"="+components[1]+" ");
            else
              os.write("@attr "+attr.getNamespaceIdentifier()+" "+components[0]+"="+components[1]+" ");
          }
        }
        else {
        }
      }
    }
}
