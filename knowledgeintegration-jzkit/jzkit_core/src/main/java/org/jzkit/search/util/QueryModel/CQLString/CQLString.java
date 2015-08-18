package org.jzkit.search.util.QueryModel.CQLString;

import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import java.io.StringReader;
import org.z3950.zing.cql.*;
import  org.springframework.context.ApplicationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CQLString implements QueryModel, java.io.Serializable {

  private Log log = LogFactory.getLog(CQLString.class);

  private static String default_qualifier="cql.serverChoice";

  private String the_cql_string;
  private InternalModelRootNode internal_model = null;
  private CQLNode cql_root;


  public CQLString(String the_cql_string) {

    try {
      this.the_cql_string = the_cql_string;
      CQLParser parser = new CQLParser();
      cql_root = parser.parse(the_cql_string);
      log.debug("Parsed CQL");
    }
    catch (  org.z3950.zing.cql.CQLParseException cqle ) {
      log.warn("Problem parsing CQL",cqle);
      // cqle.printStackTrace();
    }
    catch ( java.io.IOException ioe ) {
      log.warn("Problem parsing CQL",ioe);
      // ioe.printStackTrace();
    }
  }

  public CQLString( CQLNode cql_root ) {
    this.cql_root = cql_root;
  }

  public InternalModelRootNode toInternalQueryModel(ApplicationContext ctx) throws InvalidQueryException {
    if ( internal_model == null ) {
      internal_model = new InternalModelRootNode(translate(cql_root));
    }
    return internal_model;
  }

  private QueryNode translate(CQLNode cql_node) {
    QueryNode result = null;

    if ( cql_node instanceof CQLBooleanNode ) {
      CQLBooleanNode cbn = (CQLBooleanNode)cql_node;
      if ( cbn instanceof CQLAndNode ) {
        result = new ComplexNode(translate(cbn.left), translate(cbn.right),ComplexNode.COMPLEX_AND);
      }
      else if ( cbn instanceof CQLOrNode ) {
        result = new ComplexNode(translate(cbn.left),translate(cbn.right),ComplexNode.COMPLEX_OR);
      }
      else if ( cbn instanceof CQLNotNode ) {
        result = new ComplexNode(translate(cbn.left),translate(cbn.right),ComplexNode.COMPLEX_ANDNOT);
      }
      else if ( cbn instanceof CQLProxNode ) {
        result = new ComplexNode(translate(cbn.left),translate(cbn.right),ComplexNode.COMPLEX_PROX);
      }
    }
    else if ( cql_node instanceof CQLTermNode ) {
      log.debug("Warning: We should properly translate the CQLTermNode");
      CQLTermNode cql_term_node = (CQLTermNode) cql_node;
      AttrPlusTermNode aptn = new AttrPlusTermNode();

      aptn.setTerm(cql_term_node.getTerm());

      if ( ( cql_term_node.getQualifier() != null ) && ( cql_term_node.getQualifier().length() > 0 ) ) {
        log.debug("Using supplied qualifier : "+cql_term_node.getQualifier());
        aptn.setAttr(AttrPlusTermNode.ACCESS_POINT_ATTR,process(cql_term_node.getQualifier()));
      }
      else {
        log.debug("Using default qualifier");
        aptn.setAttr(AttrPlusTermNode.ACCESS_POINT_ATTR,process(default_qualifier));
      }

      // CQL Relation object:
      CQLRelation relation = cql_term_node.getRelation();

      if ( relation != null ) {
        if ( relation.getBase() != null ) {
          if ( relation.getBase().equalsIgnoreCase("src") ) {
            aptn.setAttr(AttrPlusTermNode.RELATION_ATTR,new AttrValue("="));
          }
          else if ( relation.getBase().equalsIgnoreCase("exact") ) {
            aptn.setAttr(AttrPlusTermNode.RELATION_ATTR,new AttrValue("="));
          }
          else if ( relation.getBase().equalsIgnoreCase("all") ) {
            aptn.setAttr(AttrPlusTermNode.RELATION_ATTR,new AttrValue("="));
          }
          else if ( relation.getBase().equalsIgnoreCase("any") ) {
            aptn.setAttr(AttrPlusTermNode.RELATION_ATTR,new AttrValue("="));
          }
          else {
            aptn.setAttr(AttrPlusTermNode.RELATION_ATTR,new AttrValue(relation.getBase()));
          }
        }
      }

      result = aptn;
    }
    else if ( cql_node instanceof CQLPrefixNode ) {
      CQLPrefixNode pn = (CQLPrefixNode)cql_node;
      result = new InternalModelNamespaceNode(pn.prefix.name, translate(((CQLPrefixNode)cql_node).subtree));
    }

    return result;
  }

  private AttrValue process(String s) {
    AttrValue result = null;
    if ( ( s != null ) && ( s.length() > 0 ) ) {
      String[] components = s.split("\\.");
      if ( components.length == 1 ) {
        result=new AttrValue(components[0]);
      }
      else if ( components.length == 2 ) { 
        result=new AttrValue(components[0],components[1]);
      }
      else {
        result = new AttrValue(s);
      }
    }
    return result;
  }

  public String toString() {
    if ( cql_root != null )
      return cql_root.toCQL();

    return null;
  }
}
