/**
 * Title:       SOLR Standard Query Formatter
 * Copyright:   Copyright (C) 2001- Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Company:     Knowledge Integration Ltd.
 */

package org.jzkit.search.util.QueryFormatter;

import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import java.util.Properties;
import org.z3950.zing.cql.*;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import org.jzkit.search.util.QueryModel.CQLString.*;
import java.io.StringWriter;

@Service("QF-SOLR-STD")
public class SOLRQueryFormatter implements  QueryFormatter, ApplicationContextAware, org.springframework.context.ApplicationListener {

  private static Log log = LogFactory.getLog(SOLRQueryFormatter.class);
  private ApplicationContext ctx = null;

  public String format(InternalModelRootNode query) throws QueryFormatterException {
    log.debug("format");
    StringWriter sw = new StringWriter();
    visitNode(query, sw);
    return sw.toString();
    
  }

  public void visitNode(QueryNode node, StringWriter sw) throws QueryFormatterException {
    log.debug("visitNode");
    if ( node instanceof InternalModelRootNode ) {
      log.debug("Processing root");
      // No special "Root" Node in cql
      visitNode(((InternalModelRootNode)node).getChild(),sw);
    }
    else if ( node instanceof InternalModelNamespaceNode ) {
      log.debug("Processing namespace: "+node);
      InternalModelNamespaceNode ns_node = (InternalModelNamespaceNode)node;
      visitNode(ns_node.getChild(),sw);
    }
    else if ( node instanceof ComplexNode ) {
      log.debug("Processing complex");
      sw.write("(");
      // Query node operators are 0=none, 1=and, 2=or, 3=andnot, 4=prox
      visitNode ( ((ComplexNode)node).getLHS(),sw);
      switch ( ((ComplexNode)node).getOp() ) {
        case 1:
          sw.write(" AND ");
          break;
        case 2:
          sw.write(" OR ");
          break;
        case 3:
          sw.write(" NOT ");
          break;
        case 4:
          throw new QueryFormatterException("Proximity search not supported by this database");
        default:
          throw new QueryFormatterException("Unhandled query operator");
      }
      visitNode ( ((ComplexNode)node).getRHS(),sw);
      sw.write(")");
    }
    else if ( node instanceof AttrPlusTermNode ) {
      AttrPlusTermNode aptn = (AttrPlusTermNode)node;
      log.debug("Processing attrplustermnode:"+aptn);

      boolean right_truncation = false;
      Object trunc =  aptn.getTruncation();
      if ( ( trunc != null ) && ( trunc.toString().equalsIgnoreCase("right"))) {
        log.debug("Adding right truncation wildcards");
        right_truncation = true;
      }

      if ( aptn.getAccessPoint() != null ) {
        sw.write(aptn.getAccessPoint().toString());
        sw.write(":");
      }
      if ( aptn.getTerm() instanceof java.util.List ) {
        sw.write("(");
        for ( java.util.Iterator i = ((java.util.List)aptn.getTerm()).iterator(); i.hasNext(); ) {
          sw.write(i.next().toString());
          if ( right_truncation )
            sw.write("*");
        }
        sw.write(")");
      }
      else if ( aptn.getTerm() instanceof String[] ) {
        String[] arr = (String[]) aptn.getTerm();
        sw.write("(");
        for ( int i = 0; i<arr.length; i++ ) {
          sw.write(arr[i]);
          if ( right_truncation )
            sw.write("*");
        }
        sw.write(")");
      }
      else {
        sw.write(aptn.getTerm().toString());
        if ( right_truncation )
          sw.write("*");
      }
    }
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public void onApplicationEvent(ApplicationEvent evt) {
  }

}
