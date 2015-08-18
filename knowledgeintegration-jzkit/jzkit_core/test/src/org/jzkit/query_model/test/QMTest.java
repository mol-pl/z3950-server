package org.jzkit.query_model.test;

import junit.framework.*;
import junit.extensions.*;
import java.util.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.*;
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;

public class QMTest extends TestCase {

  private static ApplicationContext app_context = null;

  public QMTest(String name) {
    super (name);
  }
  
  public static void main(String[] args) {
    app_context = new ClassPathXmlApplicationContext( "TestApplicationContext.xml" );
  }

  public void testSimplePrefix() throws Exception {
    System.err.println("\n\ntestSimplePrefix\n\n");
    QueryModel qm = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString("@attrset bib-1 @attr 1=4 science");
    System.err.println("Result of parse = "+qm.toString());
    InternalModelRootNode imrn = qm.toInternalQueryModel(app_context);
    System.err.println("Internal Model = "+imrn);
    System.err.println("Internal Model using RPN Visitor = "+org.jzkit.search.util.QueryModel.PrefixString.PrefixQueryVisitor.toPQF(imrn));
  }

  public void testBooleanPrefix() throws Exception {
    System.err.println("\n\ntestBooleanPrefix\n\n");
    QueryModel qm = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString("@attrset bib-1 @and @attr 1=4 science @attr 1=2 beer");
    System.err.println("Result of parse = "+qm.toString());
    InternalModelRootNode imrn = qm.toInternalQueryModel(app_context);
    System.err.println("Internal Model = "+imrn);
    System.err.println("Internal Model using RPN Visitor = "+org.jzkit.search.util.QueryModel.PrefixString.PrefixQueryVisitor.toPQF(imrn));
  }

  public void testSimpleInternal() throws Exception {
    System.err.println("\n\ntestSimpleInternal\n\n");
    QueryModel qm = InternalModelRootNode.createInstanceFromClasspathFile("/DefaultQueryModel.xml");
    System.err.println("Result of parse = "+qm.toString());
    InternalModelRootNode imrn = qm.toInternalQueryModel(app_context);
    System.err.println("Internal Model = "+imrn);
    System.err.println("Internal Model using RPN Visitor = "+org.jzkit.search.util.QueryModel.PrefixString.PrefixQueryVisitor.toPQF(imrn));
  }

  public void testTemplateBasedQueryFactory() throws Exception {
    System.err.println("\n\ntestTemplateBasedQueryFactory\n\n");
    QueryModel qm = InternalModelRootNode.createInstanceFromClasspathFile("/DefaultQueryModel.xml");
    System.err.println("Result of parse = "+qm.toString()); System.err.println("Internal Model = "+qm.toInternalQueryModel(app_context).toString());
    Map values = new HashMap();
    values.put("query.SubjectCriteria.term","Sports");
    values.put("query.PlaceCriteria.term","Sheffield");
    InternalModelRootNode qm2 = TemplateBasedQueryFactory.visit((InternalModelRootNode)qm,values);
    System.err.println("New Internal Model = "+qm2.toString());
    System.err.println("Internal Model using RPN Visitor = "+org.jzkit.search.util.QueryModel.PrefixString.PrefixQueryVisitor.toPQF(qm2));
  }

  public void testAdvancedTemplateBasedQueryFactory() throws Exception {
    System.err.println("\n\nadvancedTestTemplateBasedQueryFactory\n\n");
    QueryModel qm = InternalModelRootNode.createInstanceFromClasspathFile("/DefaultQueryModel.xml");
    System.err.println("Result of parse = "+qm.toString());
    System.err.println("Internal Model = "+qm.toInternalQueryModel(app_context).toString());
    Map values = new HashMap();
    values.put("query.SubjectCriteria.term","Sports \"and a phrase\" Other Term \"And another phrase\"");
    values.put("query.SubjectCriteria.relation","Like");
    values.put("query.SubjectCriteria.truncation","RightAndLeft");
    values.put("query.PlaceCriteria.term","Sheffield");
    InternalModelRootNode qm2 = TemplateBasedQueryFactory.visit((InternalModelRootNode)qm,values);
    System.err.println("New Internal Model = "+qm2.toString());
    System.err.println("Internal Model using RPN Visitor = "+org.jzkit.search.util.QueryModel.PrefixString.PrefixQueryVisitor.toPQF(qm2));
  }

  public void testQuotedStrings() throws Exception {
    System.err.println("\n\ntestQuotedStrings\n\n");
    QueryModel qm = InternalModelRootNode.createInstanceFromClasspathFile("/DefaultQueryModel.xml");
    System.err.println("Result of parse = "+qm.toString());
    System.err.println("Internal Model = "+qm.toInternalQueryModel(app_context).toString());
    Map values = new HashMap();
    values.put("query.SubjectCriteria.term","Sports \\\"and a phrase\\\" Other Term \\\"And another phrase\\\"");
    InternalModelRootNode qm2 = TemplateBasedQueryFactory.visit((InternalModelRootNode)qm,values);
    System.err.println("New Internal Model = "+qm2.toString());
    String new_rpn = org.jzkit.search.util.QueryModel.PrefixString.PrefixQueryVisitor.toPQF(qm2);
    System.err.println("Internal Model using RPN Visitor = -"+new_rpn+"-");

    // if ( ! new_rpn.equals("@attrset bib-1 @attr 1=1099 Sports \"and a phrase\" Other Term \"And another phrase\" ") ) {
    //   throw new Exception("RPN Quoted Strings Test Failed");
    // }
    // else {
    //   System.err.println("Quoted Strings test ok");
    // }
  }

  public void testPassthrough() throws Exception {
    System.err.println("\n\ntestPassthrough\n\n");
    QueryModel qm = InternalModelRootNode.createInstanceFromClasspathFile("/DefaultQueryModel.xml");
    System.err.println("Result of parse = "+qm.toString());
    System.err.println("Internal Model = "+qm.toInternalQueryModel(app_context).toString());
    Map values = new HashMap();
    values.put("query.SubjectCriteria.multiTermOp","PASSTHRU");
    values.put("query.SubjectCriteria.term","Sports \"and a phrase\" Other Term \"And another phrase\"");
    InternalModelRootNode qm2 = TemplateBasedQueryFactory.visit((InternalModelRootNode)qm,values);
    System.err.println("New Internal Model = "+qm2.toString());
    String new_rpn = org.jzkit.search.util.QueryModel.PrefixString.PrefixQueryVisitor.toPQF(qm2);
    System.err.println("Internal Model using RPN Visitor = -"+new_rpn+"-");

    if ( ! new_rpn.equals("@attrset bib-1 @attr 1=1099 \"Sports \\\"and a phrase\\\" Other Term \\\"And another phrase\\\"\" ") ) {
      throw new Exception("RPN Passthrough Test Failed");
    }
    else {
      System.err.println("Passthru test ok");
    }
  }
}
