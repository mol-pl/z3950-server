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

package  org.jzkit.search.provider.z3950;

import org.jzkit.search.provider.iface.JZKitPluginMetadata;
import org.jzkit.search.provider.iface.PropDef;
import org.jzkit.search.provider.iface.IRServiceDescriptor;
import org.jzkit.search.provider.iface.ExplainDTO;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

@Service("JZKitZ3950PluginMetadata")
public class JZKitZ3950PluginMetadata implements JZKitPluginMetadata, ApplicationContextAware, org.springframework.context.ApplicationListener {
 
  public static final String CODE = "Z3950";
  public static final String NAME = "JZKit Standard Z39.50 Plugin";
  public static final String CLASS_NAME = "org.jzkit.search.provider.z3950.Z3950Origin";

  public static final PropDef[] PROPS = new PropDef[] { new PropDef("host"), new PropDef("port") };

  private ApplicationContext ctx = null;

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public void onApplicationEvent(ApplicationEvent evt) {
  }


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
    return new ExplainDTO();
  }

}
