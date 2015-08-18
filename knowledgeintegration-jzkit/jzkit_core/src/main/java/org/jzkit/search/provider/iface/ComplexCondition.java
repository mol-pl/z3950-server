package org.jzkit.search.provider.iface;

public abstract class ComplexCondition implements Condition, java.io.Serializable {
  public abstract boolean evaluate(Object context);
}
