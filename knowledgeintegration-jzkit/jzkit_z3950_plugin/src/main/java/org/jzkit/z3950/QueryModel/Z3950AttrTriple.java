package org.jzkit.z3950.QueryModel;

import java.math.BigInteger;

/**
 * @author Administrator
 */
public class Z3950AttrTriple
{
  private String attrset;
  private int type;
  private Object value;

  public Z3950AttrTriple(String str)
  {
    // Create a triple from the string attrset:type:value, e.g. bib-1.1.4
    String[] result = str.split("\\.");
    if ( result.length == 3 )
    {
      attrset = result[0];
      type = Integer.parseInt(result[1]);

      // Now the hard part.. We could have been given a string like 1.2.fred and fred is a valid
      // attr value. Yet because we are splitting up the string, we have no real way of knowing
      // if the user meant 1.2."3" or 1.2.3 (A string or an int for the last digit). So, we assume
      // So, if the string starts and ends with \" then we consider it a string, otherwise, its an int?
      if ( ( result[2].startsWith("\"") ) && ( result[2].endsWith("\"") && (result[2].length() > 1 )) )
        value = result[2].substring(1,result[2].length()-1);
      else
        value = new BigInteger( result[2] );
    }
    else
      throw new RuntimeException("unable to parse "+str+" using attrset.type.value. got"+result);
  }
	
  public Z3950AttrTriple(String attrset, int type, Object value)
  {
  	this.attrset = attrset;
  	this.type = type;
  	this.value = value;
  }
  
  public String getAttrset()
  {
  	return attrset;
  }
  
  public int getType()
  {
  	return type;
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public String toString()
  {
  	return attrset+"."+type+"."+value.toString();
  }
}
