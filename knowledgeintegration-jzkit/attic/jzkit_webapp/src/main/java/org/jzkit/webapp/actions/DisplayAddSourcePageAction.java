package org.jzkit.webapp.actions;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.tiles.actions.TilesAction;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Implementation of <strong>Action</strong> that starts a search.
 */

public final class DisplayAddSourcePageAction extends Action {

  public ActionForward execute(ActionMapping mapping,
                               ActionForm form,
                               HttpServletRequest request,
                               HttpServletResponse response) {

    String mapping_param = mapping.getParameter();
    org.jzkit.search.provider.iface.JZKitPluginMetadata required_plugin = null;

    try {
      WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServlet().getServletContext());
      java.util.List<org.jzkit.search.provider.iface.JZKitPluginMetadata> jzkit_plugins = (java.util.List) ctx.getBean("JZKitPluginRegistry");

      for ( java.util.Iterator i = jzkit_plugins.iterator(); i.hasNext(); ) {
        org.jzkit.search.provider.iface.JZKitPluginMetadata p = (org.jzkit.search.provider.iface.JZKitPluginMetadata) i.next();
        if ( p.getPluginCode().equals(mapping_param) ) {
          required_plugin = p;
        }
      }
    }
    finally {
    }

    request.setAttribute("plugin",required_plugin);
    return (mapping.findForward("success"));
  }
}

