<!DOCTYPE html PUBLIC 
	"-//W3C//DTD XHTML 1.1 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%
  String base_dir = request.getContextPath();
  response.setHeader("Cache-Control","no-store"); //HTTP 1.1
  response.setHeader("Pragma","no-cache"); //HTTP 1.0
  response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
  response.setContentType("text/html;charset=utf-8");
  String username = request.getRemoteUser();
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title><decorator:title default="JZKit3"/></title>
    <link href="<s:url value='/styles/main.css'/>" rel="stylesheet" type="text/css" media="all"/>
    <link href="<s:url value='/struts/niftycorners/niftyCorners.css'/>" rel="stylesheet" type="text/css"/>
    <link href="<s:url value='/struts/niftycorners/niftyPrint.css'/>" rel="stylesheet" type="text/css" media="print"/>
    <script language="JavaScript" type="text/javascript" src="<s:url value='/struts/niftycorners/nifty.js'/>"></script>
	<script language="JavaScript" type="text/javascript">
        window.onload = function(){
            if(!NiftyCheck()) {
                return;
            }
            // perform niftycorners rounding
            // eg.
            // Rounded("blockquote","tr bl","#ECF1F9","#CDFFAA","smooth border #88D84F");
        }
    </script>
    <decorator:head/>
</head>
<body id="page-home">
    <div id="page">
        <div id="header" class="clearfix">JZKit 3.0 Metasearch Application</div>
        
        <div id="content" class="clearfix">
            <div id="main">
            	<h3>Main Content</h3>
            	<decorator:body/>
                <hr />
            </div>
            
            <div id="nav">
                <div class="wrapper">
                <ul class="clearfix">
                     <li><a href="<%=base_dir%>/search">Search</a></li>
                     <li><a href="<%=base_dir%>/registry">Registry</a></li>
                     <% if ( request.getUserPrincipal() != null ) { %>
                       <li class="last"><a href="<%=base_dir%>/user/<%=username%>">Home</a></li>
                     <% } else { %>
                       <li><a href="<%=base_dir%>/home">Login</a></li>
                       <li class="last"><a href="<%=base_dir%>/signup">Signup</a></li>
                     <% } %>
                </ul>
                </div>
            </div>
        </div>
        
        <div id="footer" class="clearfix">
            Footer
        </div>
        
    </div>
</body>
</html>
