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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of <strong>Action</strong> that starts a search.
 */

public final class ProcessAddSourcePageAction extends Action {

  private Log log = LogFactory.getLog(ProcessAddSourcePageAction.class);

  public ActionForward execute(ActionMapping mapping,
                               ActionForm form,
                               HttpServletRequest request,
                               HttpServletResponse response) {
    log.debug("Adding source...");
    org.hibernate.Session sess = null;

    try {
      WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServlet().getServletContext());
      java.util.List<org.jzkit.search.provider.iface.JZKitPluginMetadata> jzkit_plugins = (java.util.List) ctx.getBean("JZKitPluginRegistry");

      org.hibernate.SessionFactory factory = (org.hibernate.SessionFactory) ctx.getBean("JZKitSessionFactory");
      sess = factory.openSession();

      String plugin_code = request.getParameter("plugin_code");

      org.jzkit.search.provider.iface.JZKitPluginMetadata required_plugin = null;
      for ( java.util.Iterator i = jzkit_plugins.iterator(); i.hasNext(); ) {
        org.jzkit.search.provider.iface.JZKitPluginMetadata p = (org.jzkit.search.provider.iface.JZKitPluginMetadata) i.next();
        if ( p.getPluginCode().equals(plugin_code) ) {
          required_plugin = p;
        }
      }

      if ( required_plugin != null ) {
        org.jzkit.ServiceDirectory.SearchServiceDescriptionDBO ssd = new org.jzkit.ServiceDirectory.SearchServiceDescriptionDBO();
        ssd.setCode(request.getParameter("source_code"));
        ssd.setServiceName(request.getParameter("service_name"));
        ssd.setServiceShortName(request.getParameter("service_short_name"));
        ssd.setClassName(request.getParameter("class_name"));

        for ( Iterator i = request.getParameterMap().entrySet().iterator(); i.hasNext(); ) {
          java.util.Map.Entry e = (java.util.Map.Entry) i.next();
          String param_name = e.getKey().toString();
          String[] values = (String[]) e.getValue();
          String param_value = values[0];
          log.debug("testing "+param_name);
          if ( param_name.startsWith("prop_") ) {
            String prop_name = param_name.substring(5,param_name.length());
            log.debug("Setting "+prop_name+" "+param_value);
            ssd.getPreferences().put(prop_name,param_value);
          }
        }

        // Process any collection information... explain the service
        log.debug("Explaining.....");
        org.jzkit.search.provider.iface.ExplainDTO explain_rec = required_plugin.explain(ssd.getPreferences());
        if ( explain_rec != null ) {
          log.debug("Got explain record....");
        }
        else {
          log.debug("Result of explain: "+explain_rec);
        }

        sess.save(ssd);
        sess.flush();
        sess.connection().commit();
      }
      else {
        log.error("Unable to add source... cannot look up plugin for "+plugin_code);
      }
    }
    catch ( java.sql.SQLException sqle ) {
      sqle.printStackTrace();
    }
    finally {
      try {
        if ( sess != null )
          sess.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }

    log.debug("All done");

    return (mapping.findForward("success"));
  }
}

