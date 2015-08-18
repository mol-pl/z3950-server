package org.jzkit.search.util.QueryModel.CQLString;

import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import java.util.Properties;
import org.z3950.zing.cql.*;
import org.springframework.context.ApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Construct a CQL tree from an internal tree
 */
public class CQLBuilder {

  private static Log log = LogFactory.getLog(CQLBuilder.class);

  public static CQLString buildFrom(QueryModel model, 
                                    ApplicationContext ctx) throws org.jzkit.search.util.QueryModel.InvalidQueryException {
    InternalModelRootNode root = model.toInternalQueryModel(ctx);
    return new CQLString( visitNode(root,null,null, false) );
  }

  public static CQLString buildFrom(QueryModel model, 
                                    ApplicationContext ctx,
                                    boolean complex_support) throws org.jzkit.search.util.QueryModel.InvalidQueryException {
    InternalModelRootNode root = model.toInternalQueryModel(ctx);
    return new CQLString( visitNode(root,null,null, complex_support) );
  }

  public static CQLNode visitNode(QueryNode node, String source_ns, String target_ns, boolean complex_support) {
    log.debug("visitNode");
    CQLNode result = null;

    if ( node instanceof InternalModelRootNode ) {
      log.debug("Processing root");
      // No special "Root" Node in cql
      result = visitNode(((InternalModelRootNode)node).getChild(), source_ns, target_ns, complex_support);
    }
    else if ( node instanceof InternalModelNamespaceNode ) {
      log.debug("Processing namespace: "+node);
      InternalModelNamespaceNode ns_node = (InternalModelNamespaceNode)node;
      if ( complex_support )
        result=new CQLPrefixNode(ns_node.getAttrset(),ns_node.getAttrset(),visitNode(ns_node.getChild(), ns_node.getAttrset(), target_ns, complex_support));
      else
        result=visitNode(ns_node.getChild(), ns_node.getAttrset(), target_ns, complex_support);
    }
    else if ( node instanceof ComplexNode ) {
      log.debug("Processing complex");
      // Query node operators are 0=none, 1=and, 2=or, 3=andnot, 4=prox
      switch ( ((ComplexNode)node).getOp() ) {
        case 1:
          result = new CQLAndNode(visitNode ( ((ComplexNode)node).getLHS(), source_ns, target_ns, complex_support ), 
                                  visitNode ( ((ComplexNode)node).getRHS(), source_ns, target_ns, complex_support ));
          break;
        case 2:
          result = new CQLOrNode(visitNode ( ((ComplexNode)node).getLHS(), source_ns, target_ns, complex_support ), 
                                 visitNode ( ((ComplexNode)node).getRHS(), source_ns, target_ns, complex_support ));
          break;
        case 3:
          result = new CQLNotNode(visitNode ( ((ComplexNode)node).getLHS(), source_ns, target_ns, complex_support ), 
                                  visitNode ( ((ComplexNode)node).getRHS(), source_ns, target_ns, complex_support ));
          break;
        default:
          log.warn("Prox not yet handled");
          break;
      }
    }
    else if ( node instanceof AttrPlusTermNode )
    {
      AttrPlusTermNode aptn = (AttrPlusTermNode)node;

      log.debug("Processing attrplustermnode:"+node);
      // Look up conversion information for source node
      CQLRelation relation = null;

      if ( aptn.getRelation() != null )
        relation = new CQLRelation(aptn.getRelation().toString());
      else
        relation = new CQLRelation("=");

      
      String qualifier = null;
      Object ap_node = aptn.getAccessPoint();
      if ( ap_node != null )
        qualifier = ap_node.toString();

      result = new CQLTermNode(qualifier, relation, aptn.getTerm().toString());
    }

    return result;
  }
}
