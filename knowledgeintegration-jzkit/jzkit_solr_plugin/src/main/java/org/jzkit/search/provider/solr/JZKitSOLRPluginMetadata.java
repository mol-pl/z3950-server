//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2.1 of
// the license, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite
// 330, Boston, MA  02111-1307, USA.
// 

package org.jzkit.search.provider.solr;

import org.jzkit.search.provider.iface.JZKitPluginMetadata;
import org.jzkit.search.provider.iface.PropDef;
import org.jzkit.search.provider.iface.IRServiceDescriptor;
import org.jzkit.search.provider.iface.ExplainDTO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class JZKitSOLRPluginMetadata implements JZKitPluginMetadata {
 
  private static Log log = LogFactory.getLog(JZKitSOLRPluginMetadata.class);
  public static final String CODE = "SOLR";
  public static final String CLASS_NAME = "org.jzkit.search.provider.solr.SOLRSearchable";
  public static final String NAME = "JZKit Standard SOLR Plugin";
  public static final PropDef[] PROPS = new PropDef[] { new PropDef("baseURL"), new PropDef("QueryType"), new PropDef("fieldList") };

  public String getPluginClassName() {
    return CLASS_NAME;
  }

  public String getPluginCode() {
    return CODE;
  }

  public String getPluginName() {
    return NAME;
  }

  public String getPluginDescription() {
    return NAME;
  }

  public PropDef[] getProps() {
    return PROPS;
  }

  public ExplainDTO explain(java.util.Map connection_properties) {
    log.debug("Explain");
    ExplainDTO result = null;

    String base_url = (String) connection_properties.get("baseURL");
    if ( base_url != null )
      result = null;
    else
      log.warn("Base URL was NULL. Properties:"+connection_properties);

    return result;
  }

}
