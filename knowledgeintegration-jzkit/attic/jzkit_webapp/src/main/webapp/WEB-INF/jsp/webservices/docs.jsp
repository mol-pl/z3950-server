<%
  String base_dir = request.getContextPath();
%>
<html><head>
<link rel="stylesheet" type="text/css" href="<%=base_dir%>/css/styles.css"/></head>
<body>
<div id="topheader">
<span id="version">Version 0.92.0</span>
<span id="title">BECTA/COL Web Services</span>
</div>
<div id="header">
<ul><li><a href="home">Home</a></li>
<li><a href="general">Intro</a></li>
<li id="current"><b>Docs</b></li>
<li><a href="searches">Test Searches</a></li>
<li><a href="sample">Sample Code</a></li>
<li><a href="soap">Soap</a></li>
<li><a href="troubleshooting">Troubleshooting</a></li>
</ul>
</div>
<div style="clear: both">&nbsp;</div>
<div id="main">
<h1>Implementation Specific Javadoc and other information</h1>
<ul>
<li>Javadocs for the SRU/SRW adaper which exposes the generic JZKit search service as a web service can be found <a href="http://developer.k-int.com/jzkit2_srw_service/apidocs">here</a>.</li>
<li>Javadocs for the JZKit search service core can be found <a href="http://developer.k-int.com/jzkit2_core/apidocs">here</a>.</li>
<li>Javadocs for the JZKit relational database adapter, which is used
to map incoming queries onto the MSSQL Server backend database can be
found <a href="http://developer.k-int.com/jzkit2_jdbc_plugin/apidocs/">here</a>.</li>
</ul>
</div>
</body></html>
