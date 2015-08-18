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

public final class ShowEditSourceAction extends Action {

  public ActionForward execute(ActionMapping mapping,
                               ActionForm form,
                               HttpServletRequest request,
                               HttpServletResponse response) {

    String source_code = mapping.getParameter();

    try {
      WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServlet().getServletContext());
    }
    finally {
    }

    request.setAttribute("source_code",source_code);
    return (mapping.findForward("success"));
  }
}

