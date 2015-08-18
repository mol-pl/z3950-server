<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %><%
  String base_dir = request.getContextPath();
  response.setHeader("Cache-Control","no-store"); //HTTP 1.1
  response.setHeader("Pragma","no-cache"); //HTTP 1.0
  response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
  response.setContentType("text/html;charset=utf-8");
  String username = request.getRemoteUser();
%>
<html:html locale="en" xhtml="true">

  <head>
    <title>JZkit Search Portal</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="<%=base_dir%>/css/styles.css" rel="stylesheet" type="text/css" media="screen" />
  </head>

  <body>
    <div id="header">
      <div id="navigation">
        <ul>
          <li><a href="<%=base_dir%>/secure/home" title="Home">Home</a></li>

      <% 
         if ( request.getUserPrincipal() != null ) {
           // If the user is logged in, offer the site level actions available to the users role
           %> <li><a href="<%=base_dir%>/logout" title="Logout">Logout</a></li> <%
           if ( request.isUserInRole("GLOBAL.admin") ||
                request.isUserInRole("org.jzkit.sysadmin") ) {
             %> <li><a href="<%=base_dir%>/secure/admin" title="Admin">Admin</a></li> <%
           }
           // if (  request.isUserInRole("com.k_int.apps.colws.sysadmin") ) {
         } 
         else {
           %><li><a href="<%=base_dir%>/secure/home" title="Log In">Log In</a></li><%
         }
      %>
        </ul>
      </div>
      <div id="navlogo">
        JZkit 3.0 Search Portal
      </div>
      <div class="clearing">&nbsp;</div>
    </div>

    <div id="content">
      <tiles:insert attribute="active_content"/>
    </div>

  </body>
</html:html>
