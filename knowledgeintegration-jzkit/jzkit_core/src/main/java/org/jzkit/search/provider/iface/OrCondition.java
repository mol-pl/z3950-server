package org.jzkit.search.provider.iface;

public class OrCondition extends ComplexCondition {

  private Condition[] child_conditions;
                                                                                                                                        
  public OrCondition(Condition[] child_conditions) {
    this.child_conditions = child_conditions;
  }

  public boolean evaluate(Object context) {
    boolean result = false;
    for ( int i=0; ((i<child_conditions.length)&&(!result));i++ )
      result = child_conditions[i].evaluate(context);
    return result;
  }

}
