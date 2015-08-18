<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="urn:com:sun:jersey:api:view" prefix="rbt" %>
<%
  String base_dir = request.getContextPath();
  java.security.Principal user = request.getUserPrincipal();
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>Admin</title>
  <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
  <style media="all" type="text/css">@import url(<c:url value="/css/all.css"/>);</style>

  <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/paginator/assets/skins/sam/paginator.css" />
  <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/container/assets/skins/sam/container.css" />
  <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/resize/assets/skins/sam/resize.css" />
  <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/datatable/assets/skins/sam/datatable.css" />
  <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/yahoo-dom-event/yahoo-dom-event.js"></script>
  <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/dragdrop/dragdrop-min.js"></script>
  <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/container/container-min.js"></script>
  <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/element/element-min.js"></script>
  <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/paginator/paginator-min.js"></script>
  <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/datasource/datasource-debug.js"></script>
  <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/datatable/datatable-min.js"></script>
  <script src="http://yui.yahooapis.com/2.8.0r4/build/resize/resize-min.js"></script>
  <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/treeview/assets/skins/sam/treeview.css" />
  <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/treeview/treeview-min.js"></script>
  <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/utilities/utilities.js"></script>
  <link rel="stylesheet" type="text/css" href="http://js.bubbling-library.com/2.1/build/accordion/assets/accordion.css" />
  <script type="text/javascript" src="http://js.bubbling-library.com/2.1/build/bubbling/bubbling.js"></script>
  <script type="text/javascript" src="http://js.bubbling-library.com/2.1/build/accordion/accordion.js"></script>
</head>
<body class="yui-skin-sam">
  <div class="page-wrapper">
    <div id="header">
      <a href="index.html" class="logo"><img src="<%=base_dir%>/img/logo.gif" width="101" height="29" alt="" /></a>
      <div class="user-status">
        <% if ( user != null ) { %>
        Welcome back <%=user.getName()%> (No messages)<br/>
        My Account <a href="<%=base_dir%>/logout">Logout</a><br/>
        Server Time: 99:99:99 DD/MM/YY<br/>
        Last login from 192.168.1.0<br/>
        Session: <%=request.getSession().getId()%>
        <% } else { %>
          <ul class="navlist">
            <li><a href="<%=base_dir%>/join">Join</a></li>
            <li><a href="<%=base_dir%>/login">LogIn</a></li>
          </ul>
        <% } %>
        <br/>
      </div>
    </div>
    <div>
