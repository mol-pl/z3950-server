package org.jzkit.z3950.QueryModel;
                                                                                                                                          
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import java.net.URL;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import org.jzkit.a2j.codec.runtime.*;
import org.jzkit.a2j.codec.util.*;
import java.util.*;
import java.math.BigInteger;
import org.springframework.context.ApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * RPNHelper : Convert an internal RPN query tree into a Z39.50 RPNQuery.
 *
 * @version:    $Id: Type1QueryModelBuilder.java,v 1.5 2005/10/26 11:22:24 ibbo Exp $
 * @author:     Ian Ibbotson ( ian.ibbotson@k-int.com )
 *
 */
public class Type1QueryModelBuilder
{
  private static transient Log log = LogFactory.getLog(Type1QueryModelBuilder.class);

  public static Z3950QueryModel buildFrom(ApplicationContext ctx,
                                          QueryModel qm, 
                                          String encoding) throws InvalidQueryException
  {
    if ( qm == null )
      throw new InvalidQueryException("Null query model");

    // Generic internal representation to type 1 here
    OIDRegister reg = (OIDRegister) ctx.getBean("OIDRegister");
    InternalToType1ConversionRules rules = (InternalToType1ConversionRules) ctx.getBean("InternalToType1ConversionRules");
    return new Type1QueryModel(RootNodeToZRPNStructure(qm.toInternalQueryModel(ctx), encoding, rules, reg));
  }

  // public static final RPNQuery_type RootNodeToZRPNStructure(QueryNode query_tree,
  public static final RPNQuery_type RootNodeToZRPNStructure(InternalModelRootNode root,
	                                                    String encoding,
                                                            InternalToType1ConversionRules rules,
                                                            OIDRegister reg) throws InvalidQueryException
  {
    log.info("RootNodeToZRPNStructure");
    // System.err.println("\n\nRPNHelper::RootNodeToZRPNStructure");
    RPNQuery_type result = new RPNQuery_type();

    // String qry_attrset = query_tree.getAttrset();
    String qry_attrset = rules.getProperty("DefaultTargetAttrset");

    if ( null != qry_attrset )
    {
      // System.err.println("Setting attributeSet OID to "+qry_attrset);
      result.attributeSet = reg.oidByName(qry_attrset);

      // Extra check before we go as far as trying to BER Encode the mandatory attrset.
      if ( result.attributeSet == null )
        throw new InvalidQueryException("Unknown OID name : "+qry_attrset+". Please add to OIDRegister");
    }
    else
    {
      throw new InvalidQueryException("Query does not to have a valid default attrset");
    }

    result.rpn = visitNode(root, reg, encoding, null, rules);

    // System.err.println("Returning result, rpn = " + result.rpn);
    return result;
  }

    public static final RPNStructure_type visitNode(QueryNode node, 
		                                    OIDRegister reg,
						    String encoding,
                                                    String default_namespace,
                                                    InternalToType1ConversionRules rules)
    {
      log.debug("Visit node type="+node.getClass());
        RPNStructure_type retval = null;

        if ( node instanceof InternalModelRootNode )
        {
            log.debug("Processing root");
            retval = visitNode(((InternalModelRootNode)node).getChild(), reg, encoding, default_namespace, rules);
        }
        else if ( node instanceof InternalModelNamespaceNode )
        {
            log.debug("Processing namespace");
            retval = visitNode(((InternalModelNamespaceNode)node).getChild(), 
                               reg, 
                               encoding, 
                               ((InternalModelNamespaceNode)node).getAttrset(), 
                               rules);
        }
        else if ( node instanceof ComplexNode )
        {
            log.debug("Processing boolean");
            retval = new RPNStructure_type();
            retval.which = RPNStructure_type.rpnrpnop_CID;

            rpnRpnOp_inline2_type t =  new rpnRpnOp_inline2_type();
            retval.o = t;
            
            t.rpn1 = visitNode ( ((ComplexNode)node).getLHS(), reg, encoding, default_namespace, rules );
            t.rpn2 = visitNode ( ((ComplexNode)node).getRHS(), reg, encoding, default_namespace, rules );
            t.op = new Operator_type ();
            // Query node operators are 0=none, 1=and, 2=or, 3=andnot, 4=prox
            switch ( ((ComplexNode)node).getOp() )
            {
                case 1:
                    t.op.which = Operator_type.and_CID;
                    t.op.o = new AsnNull();
                    break;
                case 2:
                    t.op.which = Operator_type.or_CID;
                    t.op.o = new AsnNull();
                    break;
                case 3:
                    t.op.which = Operator_type.and_not_CID;
                    t.op.o = new AsnNull();
                    break;
                default:
                    System.err.println("Prox not yet handled");
                    break;
            }
        }
        else if ( node instanceof AttrPlusTermNode )
        {
            log.debug("Processing attrplustermnode");
            retval = new RPNStructure_type();
            retval.which = RPNStructure_type.op_CID;
            Operand_type t = new Operand_type();
            retval.o = t;

            t.which = Operand_type.attrterm_CID;
	    t.o = AttrPlusTermNode2apt_type((AttrPlusTermNode)node, encoding, default_namespace, rules, reg);
        }

        log.debug(" visitNode returning "+retval);
        return retval;
    }

    public static final AttributesPlusTerm_type AttrPlusTermNode2apt_type(AttrPlusTermNode node,
		                                                          String encoding,
		                                                          String default_source_tree_namespace,
                                                                          InternalToType1ConversionRules rules,
                                                                          OIDRegister reg)
    {
      log.debug("Convert aptn");
        AttributesPlusTerm_type apt = new AttributesPlusTerm_type();
        apt.attributes = new ArrayList();

        // We get an enumeration of...
        Iterator e = (node).getAttrIterator();

        for( ; e.hasNext(); )
        {
            String attr_id = e.next().toString();
            log.debug("Lookup attribute "+attr_id);
            Object av = node.getAttr(attr_id);
            Z3950AttrTriple at = rules.convert(av, default_source_tree_namespace);
            AttributeElement_type ae = new AttributeElement_type();

            String element_attset = at.getAttrset();

            // Use OID registry to lookup attr set oid if one is present
            // Don't fill out element attrset if it's the same as default attrset
            if ( ( null != element_attset ) &&
                 ( ! element_attset.equals(default_source_tree_namespace) ) )
            {
                ae.attributeSet = reg.oidByName(element_attset);
            }

            ae.attributeType = BigInteger.valueOf(at.getType());;


            // Need to decide if the attribute value is a number or a complex
            Object attrval = at.getValue();
            ae.attributeValue = new attributeValue_inline3_type();

            if ( attrval instanceof BigInteger ) {
                ae.attributeValue.which = attributeValue_inline3_type.numeric_CID ;
                ae.attributeValue.o = BigInteger.valueOf(((BigInteger)attrval).intValue());
            }
            else if ( attrval instanceof String ) {
                String val = attrval.toString();

                ae.attributeValue.which = attributeValue_inline3_type.complex_CID;

                complex_inline4_type cit = new complex_inline4_type();
                cit.list = new ArrayList();

                ae.attributeValue.o = cit;

                StringOrNumeric_type son = new StringOrNumeric_type();
                son.which = StringOrNumeric_type.string_CID;
                son.o = val;

                cit.list.add(son);
            }
            else {
                log.warn("Unhandled type ("+attrval.getClass().getName()+") for attribute value");
            }

            apt.attributes.add(ae);
        }

        // Add new AttributeElement members for each attribute
        apt.term = new Term_type();
        apt.term.which = Term_type.general_CID;
	try {
          apt.term.o = node.getTerm().toString().getBytes(encoding);
	}
	catch ( java.io.UnsupportedEncodingException uee ) {
          log.warn("Problem converting search string to requested encoding",uee);
          apt.term.o = node.getTerm().toString().getBytes();
	}

	return apt;
    }
}
