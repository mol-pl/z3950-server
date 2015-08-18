/**
 * Title:       PropDef - A property definition
 */

package org.jzkit.search.provider.iface;

/**
 */ 
public class PropDef {
  private String property_display_string = null;

  public PropDef() {
  }

  public PropDef(String property_display_string) {
    this.property_display_string = property_display_string;
  }

  public String getPropertyDisplayString() {
    return property_display_string;
  }

  public void setPropertyDisplayString(String property_display_string) {
    this.property_display_string = property_display_string;
  }
}
