package org.jzkit.search.provider.iface;

public class AndCondition extends ComplexCondition {

  private Condition[] child_conditions;
                                                                                                                                        
  public AndCondition(Condition[] child_conditions) {
    this.child_conditions = child_conditions;
  }

  public boolean evaluate(Object context) {
    boolean result = true;
    for ( int i=0; ((i<child_conditions.length)&&(result));i++ )
      result = result && child_conditions[i].evaluate(context);
    return result;
  }
}
