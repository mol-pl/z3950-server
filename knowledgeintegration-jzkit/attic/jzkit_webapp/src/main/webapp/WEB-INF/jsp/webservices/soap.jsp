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
<li><a href="docs">Docs</a></li>
<li><a href="searches">Test Searches</a></li>
<li><a href="sample">Sample Code</a></li>
<li id="current"><b>Soap</b></li>
<li><a href="troubleshooting">Troubleshooting</a></li>
</ul>
</div>
<div style="clear: both">&nbsp;</div>
<div id="main">
<h1>Soap</h1>
<ul>
  <li>The generic WSDL can be found <a href="http://developer.k-int.com/srw1-1/srw-bindings.wsdl">Here</a></li>
  <li>The AXIS Generated WSDL for SOAP Supplier Search service is <a href="<%=base_dir%>/soap/search/Suppliers?wsdl">here</a></li>
  <li>The AXIS Generated WSDL for SOAP Product Search service is <a href="<%=base_dir%>/soap/search/Products?wsdl">here</a></li>
  <li>The AXIS Generated WSDL for SOAP Taxon Search service is <a href="<%=base_dir%>/soap/search/Vocabs?wsdl">here</a></li>
</ul>
</div>
</body></html>
