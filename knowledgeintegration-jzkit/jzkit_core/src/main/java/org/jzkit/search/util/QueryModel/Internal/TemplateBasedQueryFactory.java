package org.jzkit.search.util.QueryModel.Internal;

import java.beans.*;
import java.util.*;
import org.jzkit.search.util.QueryModel.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.*;

/**
 *  Creates a new query which only copies forward nodes that contain actual query
 *  values, extracting those values from the input map.
 */
public class TemplateBasedQueryFactory
{
  protected static Log log = LogFactory.getLog(TemplateBasedQueryFactory.class);

  private static final String START_VALUE="query.";
  /** 
   *  Creates a new query which only copies forward nodes that contain actual query
   *  values, extracting those values from the input map. The input map needs to consist
   *  of a set of values of the form query.NODENAME.Attr = XYZ, for example 
   *  query.Title.accessPoint=bib-1.1.4.
   *  ***INPUT QUERY WILL BE CHANGED***
   */
  public static InternalModelRootNode visit(InternalModelRootNode qn, Map values) {

    // Step 1 : Build an index into the query tree
    Map query_index = new HashMap();
    buildIndex(qn,query_index);

    // Step 2 : Assign any values set from input values clause
    for ( Iterator value_iterator = new TreeSet(values.keySet()).iterator(); value_iterator.hasNext(); ) {
      String key = (String) value_iterator.next();
      // See if key starts with query
      if ( key.startsWith(START_VALUE) ) {
        try {
          String[] key_parts = key.split("\\.");
          Object selected_node = query_index.get(key_parts[1]);
          if ( ( selected_node != null ) && ( values.get(key) != null ) ) {

            Object src_object = values.get(key);
            String source_value = null;
            if ( src_object instanceof String[] ) {
              // System.err.println(key+" = "+((String[])src_object).length+" = "+((String[])src_object)[0]);
              String[] src_array = (String[])src_object;
              source_value = src_array[0];
            }
            else {
              source_value = src_object.toString();
            }

            java.util.Vector values_list = new java.util.Vector();
            log.debug("Checking "+key+"=\""+source_value+"\" - "+source_value.getClass().getName());

            if ( ( selected_node instanceof AttrPlusTermNode ) && 
                 ( ((AttrPlusTermNode)selected_node).getDefaultMultiTermOperator() == AttrPlusTermNode.PASSTHRU_TERMS ) ) {
              // Passthrough mode for more complex free text searching. Entering 'Fred Jim "Quoted Phrase"' should pass
              // that exaxt string through. Because of the search mechanism, the entire string need to be wrapped in
              // quoted and any existing quotes escaped.
              // System.err.println("Passthrough Query : Result of replace: "+source_value.replaceAll("\"","\\\\*"));
              if ( ( source_value != null ) && ( source_value.length() > 0 ) )
                values_list.add(source_value.replaceAll("\"","\\\\\""));
                // values_list.add("\""+source_value.replaceAll("\"","\\\\\"")+"\"");
            }
            else {
              // Traditional boolean mode for the query, e.g. entering "Fred Jim" in the search box should produce
              // @and @attr x=y Fred @attr x=y Jim
              // System.err.println("traditional query processing...");
              java.io.StreamTokenizer st = new java.io.StreamTokenizer(new java.io.StringReader(source_value));
              st.wordChars('%','%');
              st.wordChars('*','*');
              st.wordChars('(','(');
              st.wordChars(')',')');
              st.wordChars('>','>');
              st.wordChars('<','<');
              st.wordChars('~','~');
              st.quoteChar('"');
              while ( st.nextToken() != java.io.StreamTokenizer.TT_EOF ) {
                switch(st.ttype) {
                  case java.io.StreamTokenizer.TT_WORD:
                    values_list.add(st.sval);
                    break;
                  case java.io.StreamTokenizer.TT_NUMBER:
                    values_list.add(""+st.nval);
                    break;
                  default:
                    values_list.add(st.sval);
                    break;
                }
              }
            }
           
            try {
              if ( values_list.size() == 0 ) {
                log.debug("No value for "+key_parts[1]+"."+key_parts[2]);
              }
              else if ( values_list.size() == 1 ) {
                log.debug("Set single "+key_parts[1]+"."+key_parts[2]+"="+values_list.get(0));
                BeanUtils.setProperty(selected_node,key_parts[2],values_list.get(0));
              }
              else {
                log.debug("Set list "+key_parts[1]+"."+key_parts[2]+"="+values_list);
                BeanUtils.setProperty(selected_node,key_parts[2],values_list);
              }
            }
            catch ( Exception e ) {
              e.printStackTrace();
            }
          }
          else {
            log.debug("Unable to locate query node for "+key);
          }
        }
        catch ( java.io.IOException ioe ) {
          ioe.printStackTrace();
        }
      }
    }

    // Step 3 : Trim any nodes without values.
    InternalModelRootNode imrn = (InternalModelRootNode)qn;
    return new InternalModelRootNode(visit(imrn.getChild()));
  }

  private static QueryNode visit(QueryNode qn)
  {
    if ( qn instanceof InternalModelNamespaceNode ) {
      InternalModelNamespaceNode imns = (InternalModelNamespaceNode)qn;
      return new InternalModelNamespaceNode(imns.getAttrset(), visit(imns.getChild()));
    }
    else if ( qn instanceof ComplexNode ) {
      ComplexNode cn = (ComplexNode)qn;

      QueryNode lhs = null;
      QueryNode rhs = null;
      
      lhs = visit(cn.getLHS());

      rhs = visit(cn.getRHS());

      if ( ( lhs != null ) && ( rhs != null ) ) {
        return new ComplexNode(lhs,  rhs, cn.getOp());
      }
      else if ( lhs != null ) {
        return lhs;
      }
      else {
        return rhs;
      }
    }
    else if ( qn instanceof AttrPlusTermNode ) {
      AttrPlusTermNode aptn = (AttrPlusTermNode)qn;
      AttrPlusTermNode result = null;

      if ( aptn.countChildrenWithTerms() > 0 ) {
        result = (AttrPlusTermNode) aptn.clone();
      }
      
      return result;
    }

    return null;
  }

  /**
   *   Recurse through the query building up an index that allows subsequent
   *   steps to easily look up nodes by their NodeName
   */
  private static void buildIndex(QueryNode qn, Map query_index) {

    if ( ( qn.getNodeName() != null ) && ( ! qn.getNodeName().equals("") ) ) {
      // System.err.println("adding "+qn.getNodeName());
      query_index.put(qn.getNodeName(), qn);
    }

    if ( qn instanceof InternalModelRootNode ) {
      InternalModelRootNode imrn = (InternalModelRootNode)qn;
      buildIndex(imrn.getChild(),query_index);
    }
    else if ( qn instanceof InternalModelNamespaceNode ) {
      InternalModelNamespaceNode imns = (InternalModelNamespaceNode)qn;
      buildIndex(imns.getChild(),query_index);
    }
    else if ( qn instanceof ComplexNode ) {
      ComplexNode cn = (ComplexNode)qn;

      if ( (cn.getLHS() != null ) ) {
        buildIndex(cn.getLHS(),query_index);
      }
      else {
        System.err.println("node with id "+qn.getNodeName()+"has null LHS");
      }

      if ( (cn.getRHS() != null ) ) {
        buildIndex(cn.getRHS(),query_index);
      } else {
        System.err.println("node with id "+qn.getNodeName()+"has null RHS");
      }

    }
    else if ( qn instanceof AttrPlusTermNode ) {
    }
  }

}
