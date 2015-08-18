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
<ul>
<li><a href="home">Home</a></li>
<li id="current"><b>Intro</b></li>
<li><a href="docs">Docs</a></li>
<li><a href="searches">Test Searches</a></li>
<li><a href="sample">Sample Code</a></li>
<li><a href="soap">Soap</a></li>
<li><a href="troubleshooting">Troubleshooting</a></li>
</ul>
</div>
<div style="clear: both">&nbsp;</div>
<div id="main">
<h1>Generic Search REST Service documentation, parameters. etc</h1>
<p>
The COL REST Search service is based around the <a href="http://www.loc.gov/standards/sru/sru-spec.html">SRU</a> protocol. 
</p>
</div>
</body></html>
