package org.jzkit.search.util.QueryModel.PrefixString;

import java.io.Reader;
import java.util.Vector;
import java.util.Properties;
import java.net.URL;
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import java.math.BigInteger;

public class PrefixQueryParser {

  private int token=0;
  private PrefixQueryLexer p;
  // private OIDRegister reg = OIDRegister.getRegister();
  private String default_attrset = "bib-1";

  private static Properties default_conversion_rules = null;

  public static Properties getDefaultConversionRules() {
    if ( default_conversion_rules == null ) {
      default_conversion_rules = new Properties();
      // URL config = PrefixQueryParser.class.getResource("org/jzkit/search/util/QueryModel/PrefixString/ConversionRules.properties") ;
      URL config = PrefixQueryParser.class.getResource("ConversionRules.properties") ;

      if ( config == null )
        throw new RuntimeException("Unable to locate default conversion rules");

      try {
        default_conversion_rules.load(config.openStream());
      }
      catch ( java.io.IOException ioe ) {
        throw new RuntimeException(ioe.toString());
      }
    }

    return default_conversion_rules;
  }


  public PrefixQueryParser(Reader r) {
    p = new PrefixQueryLexer(r);
  }

  // public InternalModelNamespaceNode parse() throws PrefixQueryException
  public InternalModelRootNode parse() throws PrefixQueryException {
    InternalModelRootNode result = new InternalModelRootNode();
    token = p.nextToken();

    if ( token==PrefixQueryLexer.ATTRSET) {
        // Consume the Attrset token
        token = p.nextToken();
        default_attrset = p.getString();
        // System.err.println("Setting attrset "+default_attrset);

        // Consume the namespace value token
        token = p.nextToken();
        InternalModelNamespaceNode ns_node = new InternalModelNamespaceNode();
        result.setChild(ns_node);
        ns_node.setAttrset(default_attrset);
        ns_node.setChild(visitPrefixQuery(default_attrset));
    }
    else {
        result.setChild(visitPrefixQuery(null));
    }
    
    // token = p.nextToken();
    if ( token != PrefixQueryLexer.EOF )
      throw new  PrefixQueryException("Unparsed text at end of PQF expression. Starts with"+p.getString());

    return result;   
  }

  public QueryNode visitPrefixQuery(String current_default_ns) throws PrefixQueryException {
    // System.err.println("visitPrefixQuery");

    QueryNode qn = null;
    
    switch ( token ) {
      case PrefixQueryLexer.AND:
        // Consume the and token
        token = p.nextToken();
        qn = new ComplexNode(visitPrefixQuery(current_default_ns), 
                             visitPrefixQuery(current_default_ns), 
                             ComplexNode.COMPLEX_AND);
        break;

      case PrefixQueryLexer.OR:
        // Consume the or token
        token = p.nextToken();
        qn = new ComplexNode(visitPrefixQuery(current_default_ns), 
                             visitPrefixQuery(current_default_ns), 
                             ComplexNode.COMPLEX_OR);
        break;

      case PrefixQueryLexer.NOT:
        // Consume the NOT token
        token = p.nextToken();
        qn = new ComplexNode(visitPrefixQuery(current_default_ns), 
                             visitPrefixQuery(current_default_ns), 
                             ComplexNode.COMPLEX_ANDNOT);
        break;

      case PrefixQueryLexer.TERM:
        // Must be a term with no attributes
        qn = visitQueryNode(current_default_ns);
        break;

      case PrefixQueryLexer.ATTR:
        qn = visitQueryNode(current_default_ns);
        break;
    }
    return qn;
  }

  public AttrPlusTermNode visitQueryNode(String current_default_ns) throws PrefixQueryException {
    // System.err.println("visitQueryNode");

    AttrPlusTermNode apt = new AttrPlusTermNode();
    Vector terms = new Vector();

    while(token==PrefixQueryLexer.ATTR) {
      // System.err.println("Consume @attr");

      int attr_type=0;
      Object attr_val=null;
      String local_attrset=null;

      // Consume the @attr and see what's next
      token = p.nextToken();

      // See if there is an attrset, as in "@attr gils 1=2016"
      if ( token == PrefixQueryLexer.TERM ) {
        // It must be an attribute set identifier, since attr types are always numeric
        local_attrset=p.getString();
        token = p.nextToken();
      }

      // Process the attribute
      if ( token == PrefixQueryLexer.NUMBER ) {
        attr_type=p.getInt();
        // Consume the attribute token
        token = p.nextToken();
      }
      else
        throw new PrefixQueryException("Unexpected error processing RPN query, expected attribute type");

      // Ensure that there is an equals
      if ( token == PrefixQueryLexer.EQUALS ) {
        // Consume it
        token = p.nextToken();
      }
      else
        throw new PrefixQueryException("Unexpected error processing RPN query, expected =");

      // Ensure there is a value
      if ( token == PrefixQueryLexer.NUMBER ) {
        attr_val=java.math.BigInteger.valueOf(p.getInt());
        // Consume It
        token = p.nextToken();
      }
      else if ( token == PrefixQueryLexer.TERM ) {
        // With the new attribute set architecture we will start to get string values... Deal here
        attr_val=p.getString();
        // Consume It
        token = p.nextToken();
      }
      else
        throw new PrefixQueryException("Unexpected error processing RPN query, expected str or num attribute");

      // Use the config to figure out which one of
      // ACCESS_POINT_ATTR = "AccessPoint"; "Relation"; "Position"; "Structure"; "Truncation"; "Completeness";
      // the selected attr relates to

      String attr_type_str = ""+attr_type;

      String lookup_str = null;
      if ( local_attrset != null )
        lookup_str = local_attrset+"."+attr_type;
      else
        lookup_str = current_default_ns+"."+attr_type;

      String internal_attr_type = getDefaultConversionRules().getProperty(lookup_str);

      if ( internal_attr_type == null )
        throw new PrefixQueryException("Unable to convert prefix query attribute type "+lookup_str);

      apt.setAttr(internal_attr_type, new AttrValue(local_attrset,attr_type_str+"."+attr_val));
    }

    // See if we have an element name
    if ( token == PrefixQueryLexer.ELEMENTNAME ) {
      // Consume the element name token and move on to the actual element name
      token = p.nextToken();

      apt.setNodeName(p.getString());

      // Consume the actual element name
      token = p.nextToken();
    }
  
    // Process any terms following the attrs

    // System.err.println("Expecting terms . Next token type = "+token);
    while ( ( token==PrefixQueryLexer.TERM ) || ( token==PrefixQueryLexer.NUMBER ) ) {

      // Handle the term
      if ( token==PrefixQueryLexer.TERM )
        terms.addElement(p.getString());
      else
        terms.addElement(""+p.getNumber());

        // terms.addElement(new java.lang.Double(p.getNumber()));

      // System.err.println("Processing Term(s)"+p.getString());
      
      token = p.nextToken();
    }

    // System.err.println("terms.size="+terms.size());

    if ( terms.size() > 1 )
      apt.setTerm(terms);
    else if ( terms.size() == 1 )
      apt.setTerm(terms.get(0));
    else
      throw new  PrefixQueryException("No Terms");
    
    // System.err.println("All done");

    return apt;
  }
}
