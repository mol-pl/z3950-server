package org.jzkit.search.util.QueryModel.Internal;

import org.jzkit.search.util.QueryModel.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Enumeration;
import java.io.StringWriter;

public class AttrPlusTermNode extends QueryNode implements Cloneable {
  public static final String ACCESS_POINT_ATTR = "AccessPoint";
  public static final String RELATION_ATTR = "Relation";
  public static final String POSITION_ATTR = "Position";
  public static final String STRUCTURE_ATTR = "Structure";
  public static final String TRUNCATION_ATTR = "Truncation";
  public static final String COMPLETENESS_ATTR = "Completeness";
  public static final String MULTI_TERM_OP = "MultiTermOp";

  private Object term;

  public static final int AND_TERMS=0;
  public static final int OR_TERMS=1;
  public static final int PASSTHRU_TERMS=2;
  public static final int NONE=3;
  private List terms = new ArrayList();

  private int default_multi_term_operator = AND_TERMS;

  public AttrPlusTermNode() {
  }
  
  public AttrPlusTermNode(HashMap attrs, Object term, String name, int default_multi_term_operator) {
    this.attrs = attrs;
    this.term = term;
    this.default_multi_term_operator = default_multi_term_operator;
    if ( name != null )
      setNodeName(name);
  }

  public Object clone() {
    return new AttrPlusTermNode((HashMap)(attrs.clone()), term, getNodeName(),default_multi_term_operator);
  }

  public int countChildrenWithTerms()
  {
    // If no term has been set AND no term list is set... This node has no terms
    if ( ( term == null ) ||
       ( ( term instanceof String ) && ( ((String)term).length() == 0 ) ) ||
       ( ( term instanceof Object[] ) && ( ((Object[])term).length == 0 ) ) ||
       ( ( term instanceof List ) && ( ((List)term).size() == 0 ) ) )
      return ( 0 );
    else
      return ( 1 );
  }

  public int countChildren() {
      return 0;
  }

  public void setTerm(Object term) {
    this.term = term;
  }

  public Object getTerm() {
    return term;
  }

  public int getDefaultMultiTermOperator() {
    String val = (String) attrs.get("MultiTermOp");
    int result = default_multi_term_operator;

    if ( val != null ) {
      if ( val.equals("OR") )
        result = OR_TERMS;
      else if ( val.equals("AND") )
        result = AND_TERMS;
      else if ( val.equals("PASSTHRU") )
        result = PASSTHRU_TERMS;
      else if ( val.equals("NONE") )
        result = NONE;
    }

    return result;
  }

  public void clearAttrs() {
    attrs.clear();
  }

  public Iterator getAttrIterator() {
    return attrs.keySet().iterator();
  }

  public String toString() {
    String result = null;

    if ( term != null )
    {
      if ( term instanceof String )
        result = getNodeName()+" Single Term : "+term+" attrs : "+attrs;
      else if ( term instanceof List )
        result = getNodeName()+" Term List : "+((List)term)+" attrs : "+attrs;
      else if ( term instanceof Object[] )
        result = getNodeName()+" Term List : "+((Object[])term)+" attrs : "+attrs;
      else
        result = getNodeName()+" Unknown Term Type : "+(term.toString())+" attrs : "+attrs;
    }
    else
      result = getNodeName()+" No Term, attrs : "+attrs;

    return result;
  }
  
  
  public TermValueBundle getTermValueBundle(boolean quote_phrases)
  {
	  TermValueBundle bundle = new TermValueBundle();
	  
	  String human_string = getTermAsString(quote_phrases);		  
	  bundle.setStringValue(human_string);
	  
	  
	  Iterator i = terms.iterator();
	  while(i.hasNext())
	  {
		  bundle.addValue((String) i.next());
	  }	  
	  
	  return bundle;
  }
  

  public String getTermAsString(boolean quote_phrases) 
  {
	  terms.clear();
     String retval = null;

    if ( term != null )
    {
      if ( term instanceof List )
      {
        retval = "";
        List term_list = (List)term;
        int i = term_list.size();
        for( int j=0; j < i; j++ ) {
          if ( j > 0 )
            retval = retval + ", ";

          String this_term = (String) term_list.get(j);

          if ( quote_phrases )
            this_term =  "\""+ this_term + "\"";
         
          terms.add(this_term);
          retval = retval+this_term;
        }
      }
      else if ( term instanceof Object[] )
      {
        Object[] term_array = (Object[]) term;
        StringWriter sw = new StringWriter();
        for( int j=0; j < term_array.length; j++ )
        {
          if ( j > 0 )
            sw.write(", ");

          String this_term = term_array[j].toString();

          if ( quote_phrases )
        	  this_term = "\""+this_term+"\"";
           
          sw.write(this_term);
          terms.add(this_term);
        }
        retval = sw.toString();
      }
      else
      {
        String term_string = term.toString();

        if ( quote_phrases )
          term_string = "\""+term_string+"\"";
        
        terms.add(term_string);
       
        retval=term_string;
      }
    }
    else
    {
      retval = "";
    }

    return retval; 
  }

  public void setAccessPoint(Object access_point) {
    attrs.put(ACCESS_POINT_ATTR,access_point);
  }

  public Object getAccessPoint() {
    return attrs.get(ACCESS_POINT_ATTR);
  }

  public void setRelation(Object relation) {
    attrs.put(RELATION_ATTR,relation);
  }

  public Object getRelation() {
    return attrs.get(RELATION_ATTR);
  }

  public void setPosition(Object position) {
    attrs.put(POSITION_ATTR,position);
  }

  public Object getPosition() {
    return attrs.get(POSITION_ATTR);
  }

  public void setStructure(Object structure) {
    attrs.put(STRUCTURE_ATTR,structure);
  }

  public Object getStructure() {
    return attrs.get(STRUCTURE_ATTR);
  }

  public void setTruncation(Object truncation) {
    attrs.put(TRUNCATION_ATTR,truncation);
  }

  public Object getTruncation() {
    return attrs.get(TRUNCATION_ATTR);
  }

  public void setCompleteness(Object completeness) {
    attrs.put(COMPLETENESS_ATTR,completeness);
  }

  public Object getCompleteness() {
    return attrs.get(COMPLETENESS_ATTR);
  }

  public void setMultiTermOp(Object multi_term_op) {
    attrs.put(MULTI_TERM_OP,multi_term_op);
  }

  public Object getMultiTermOp() {
    return attrs.get(MULTI_TERM_OP);
  }

  public Object getAttr(String attr_name) {
    return attrs.get(attr_name);
  }

  public void clearAllTerms()
  {
  }

  public void dump(StringWriter sw)
  {
    sw.write(toString());
  }

  public AttrPlusTermNode cloneForAttrs() {
    return new AttrPlusTermNode((HashMap)(attrs.clone()), term, getNodeName(),default_multi_term_operator);
  }
}
