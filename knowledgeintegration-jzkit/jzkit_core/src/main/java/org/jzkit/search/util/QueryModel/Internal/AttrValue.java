package org.jzkit.search.util.QueryModel.Internal;

import org.jzkit.search.util.QueryModel.*;
import javax.persistence.*;

@Embeddable
public class AttrValue implements java.io.Serializable {

  String namespace_identifier = null;
  String value = null;

  public AttrValue() {
  }

  public AttrValue(String value) {
      this.value = value;
  }

  public AttrValue(String namespace_identifier, String value) {
      this.namespace_identifier = namespace_identifier;
      this.value = value;
  }

  public boolean equals(Object obj_to_comp) {
    if ( obj_to_comp instanceof AttrValue ) {
      AttrValue comp = (AttrValue)obj_to_comp;

      // True if value matches and both namespaces are null or both namespaces are not null and equal
      return ( value.equals(comp.getValue() ) &&
               ( ( ( namespace_identifier==null ) && ( comp.getNamespaceIdentifier() == null ) ) ||
                 ( ( ( namespace_identifier!=null ) && ( comp.getNamespaceIdentifier() != null ) &&
                     ( namespace_identifier.equals(comp.getNamespaceIdentifier() ) )))));
    }

    return false;
  }

  public int hashCode() {
    return toString().hashCode();
  }

  @Column(name="NAMESPACE_IDENTIFIER",length=64)
  public String getNamespaceIdentifier() {
    return namespace_identifier;
  }

  public void setNamespaceIdentifier(String namespace_identifier) {
    this.namespace_identifier = namespace_identifier;
  }

  @Column(name="ATTR_VALUE",length=64)
  public String getValue() {
      return value;
  }

  public void setValue(String value) {
      this.value = value;
  }

  public String toString() {
    if (namespace_identifier==null)
      return value.toString();
    else
      return namespace_identifier+":"+value.toString();
  }

  public String getWithDefaultNamespace(String default_namespace) {

    if (namespace_identifier==null)
      if ( default_namespace != null )
        return default_namespace+"."+value.toString();
      else
        return value.toString();
    else
      return namespace_identifier+"."+value.toString();
  }
}
