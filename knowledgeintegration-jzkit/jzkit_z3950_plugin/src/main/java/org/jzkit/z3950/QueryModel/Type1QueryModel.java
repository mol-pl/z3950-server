package org.jzkit.z3950.QueryModel;
                                                                                                                                          
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import java.util.Properties;
import java.net.URL;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import org.springframework.context.ApplicationContext;
import org.jzkit.a2j.codec.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Type1QueryModel extends Z3950QueryModel {

  private RPNQuery_type query;
  private static Log log = LogFactory.getLog(Type1QueryModel.class);

  public Type1QueryModel(RPNQuery_type query) {
    log.info("Type1QueryModel::Type1QueryModel("+query+")");
    this.query = query;
  }

  public Query_type toASNType() {

    log.info("toASNType()");

    Query_type qt = new Query_type();
    qt.which=Query_type.type_1_CID;
    qt.o =  query;
    return qt;
  }

  public InternalModelRootNode toInternalQueryModel(ApplicationContext ctx) throws InvalidQueryException {
    OIDRegister reg = (OIDRegister)ctx.getBean("OIDRegister");
    Properties attr_type_config = (Properties)ctx.getBean("RPNToInternalRules");
    return traverse(query, reg, attr_type_config);
  }

  private static InternalModelRootNode traverse(RPNQuery_type q, OIDRegister reg, Properties attr_type_config) {

    InternalModelRootNode result = null;

    if ( q.attributeSet != null ) {
      String attr_set = null;
      OIDRegisterEntry re = reg.lookupByOID(q.attributeSet);
      if ( null != re )
        attr_set = re.getName();

      return new InternalModelRootNode(new InternalModelNamespaceNode(attr_set,traverse(q.rpn, reg, attr_type_config, attr_set)));
    }
    else {
      return new InternalModelRootNode(traverse(q.rpn, reg, attr_type_config, null));
    }
  }

  private static QueryNode traverse(RPNStructure_type rpn, 
                                    OIDRegister reg, 
                                    Properties attr_type_config,
                                    String attrset) {
    switch ( rpn.which ) {
      case RPNStructure_type.op_CID:
        // This node is an operand... Process it
        Operand_type ot = (Operand_type)(rpn.o);

        // What kind of operand (Let's hope it's AttrPlusTerm for now)
        switch ( ot.which ) {
          case Operand_type.attrterm_CID:
            // Get hold of an easy handle to the Z3950 RPN node
            AttributesPlusTerm_type apt = (AttributesPlusTerm_type)(ot.o);
            return convertAPT(apt, reg, attr_type_config, attrset);

          case Operand_type.resultset_CID:
            break;

          case Operand_type.resultattr_CID:
            break;
        }
        break;

      case RPNStructure_type.rpnrpnop_CID:
        ComplexNode c = new ComplexNode(traverse(((rpnRpnOp_inline2_type)(rpn.o)).rpn1, reg, attr_type_config, attrset),
                                        traverse(((rpnRpnOp_inline2_type)(rpn.o)).rpn2, reg, attr_type_config, attrset),
                                        ((rpnRpnOp_inline2_type)(rpn.o)).op.which+1);
        return c;
    }

    return null;
  }

  public static final QueryNode convertAPT( AttributesPlusTerm_type apt, 
                                            OIDRegister reg, 
                                            Properties attr_type_config,
                                            String attrset) {

    // Create the new QueryNode
    AttrPlusTermNode aptn = new AttrPlusTermNode();

    // Rely on the fact that setTerm takes an Object and just pull the term out of
    // the Z3950 RPN structure... Yuck! No longer holds true now that the encoders
    // return byte arrays for octetstring objects
    // aptn.setTerm(apt.term.o.toString());
    if ( apt.term.o instanceof byte[] )
      aptn.setTerm(new String((byte[])apt.term.o));
    else
      aptn.setTerm(apt.term.o.toString());

    // Deal with any attributes that came along with this AttrPlusTerm node
    for ( int i=0; i<apt.attributes.size(); i++ ) {
      // Helper
      AttributeElement_type aet = (AttributeElement_type) apt.attributes.get(i);

      // Figure out our internal name for the OID that came along with this attribute element
      if ( null != aet.attributeSet ) {
        OIDRegisterEntry re = reg.lookupByOID(aet.attributeSet);
        if ( null != re )
          attrset = re.getName(); 
      }

      // Now work out the value itself, it could be a number or a complex
      Object attrval = null;

      switch ( aet.attributeValue.which ) {
        case attributeValue_inline3_type.numeric_CID:
          // It's just an integer.. Phew
          attrval = aet.attributeValue.o;
          break;
        case attributeValue_inline3_type.complex_CID:
          // It's complicated
          complex_inline4_type complex = (complex_inline4_type) aet.attributeValue.o;
          // complex.list.. Sequence of stringOrNumeric, complex.semanticAction ( sequence of intger );
          // We only take the first element off the list for now....
          attrval = complex.list.get(0);
          break;
      }

      String attr_type_string = attrset+"."+aet.attributeType;

      String internal_attr_type = (String) attr_type_config.get(attr_type_string);

      if ( internal_attr_type != null ) {
        AttrValue av = new AttrValue(attrset,aet.attributeType+"."+attrval);
        // System.err.println("Setting internal attr type "+internal_attr_type+" to "+av);
        aptn.setAttr(internal_attr_type,av);
      }
      else {
        log.warn("No mapping for "+attr_type_string+" amongst "+attr_type_config);
      }
    }
    return aptn;
  }
}
