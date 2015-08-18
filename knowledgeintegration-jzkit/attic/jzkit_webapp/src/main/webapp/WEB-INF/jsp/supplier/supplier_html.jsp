<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %> 
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %> 
<%@ page import="org.hibernate.*" %>
<%@ page import="org.hibernate.type.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.k_int.lom.datamodel.*" %>
<%
  String supplier_code = (String) request.getAttribute("SupplierCode");
  String section = (String) request.getAttribute("Section");

  String base_dir = request.getContextPath();
  String page_name = null;

  System.err.println("Looking up product "+supplier_code);

  if ( ( supplier_code != null ) && ( supplier_code.length() > 0 ) ) {

    org.hibernate.Session sess = null;

    try {
      org.springframework.web.context.WebApplicationContext ctx =
        org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext(application);

      org.hibernate.SessionFactory factory = (org.hibernate.SessionFactory) ctx.getBean("COLWSSessionFactory");
      sess = factory.openSession();

      SupplierHDO sup = SupplierHDO.lookupByIdentifier(sess, supplier_code);
      if ( sup != null ) {
        request.setAttribute("sup",sup);

      request.setAttribute("sess",sess);
%>

<%
  if ( section.equalsIgnoreCase("educational") ) {
    page_name = "educational";
  } else if ( section.equalsIgnoreCase("technical") ) {
    page_name = "technical";
  } else if ( section.equalsIgnoreCase("evaluations") ) {
    page_name = "evaluations";
  } else { 
    page_name = "general";
  }
%>
<div class="clearing">&nbsp;</div>
  <h2><%=sup.getName()%></h2>
  <div id="pagelist">
    <ul>
      <% if (page_name.equals("general")) { %> <li class="selected">  <% } else { %> <li> <% } %>
      <a href="<%=base_dir%>/product/<%=sup.getIdentifier()%>/html/general">Products</a></li>
    </ul>
  </div>
  <div id="datacontainer" class="clearleft">
    <div id="databox">
      <%
        if (page_name.equals("boingflip")) {
          %><tiles:insert definition="supplier.boingflip.panel"/><%
        }
        else {
          %><tiles:insert definition="supplier.general.panel"/><%
        }
      %>
    </div>
    <div class="clearing">&#160;</div>
  </div>
<%
      }
      else {
        System.err.println("No supplier for ID "+supplier_code);
      }

      sess.flush();
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
  }
  else {
  }
%>
