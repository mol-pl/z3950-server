<%
  String user_token = request.getParameter("user_token");
  String base_dir = request.getContextPath();
%>

<html><head>
<link rel="stylesheet" type="text/css" href="<%=base_dir%>/css/styles.css"/></head>
<SCRIPT LANGUAGE="JavaScript">

// ---------------------------------------------
// --- Name:    Easy DHTML Treeview           --
// --- Author:  D.D. de Kerf                  --
// --- Version: 0.2          Date: 13-6-2001  --
// ---------------------------------------------
function Toggle(node)
{
  // Unfold the branch if it isn't visible
  if (node.nextSibling.style.display == 'none')
  {
    // Change the image (if there is an image)
    if (node.childNodes.length > 0)
    {
      if (node.childNodes.item(0).nodeName == "IMG")
      {
        node.childNodes.item(0).src = "images/minus.gif";
      }
    }

    node.nextSibling.style.display = 'block';
  }
  // Collapse the branch if it IS visible
  else
  {
    // Change the image (if there is an image)
    if (node.childNodes.length > 0)
    {
      if (node.childNodes.item(0).nodeName == "IMG")
      {
        node.childNodes.item(0).src = "<%=base_dir%>/images/plus.gif";
      }
    }

    node.nextSibling.style.display = 'none';
  }

}
</SCRIPT>
<body>
<div id="topheader">
<span id="version">Version 0.92.0</span>
<span id="title">BECTA/COL Web Services</span>
</div>
<div id="header">
<ul>
<li><a href="home">Home</a></li>
<li><a href="general">Intro</a></li>
<li><a href="docs">Docs</a></li>
<li id="current"><b>Test Searches</b></li>
<li><a href="sample">Sample Code</a></li>
<li><a href="soap">Soap</a></li>
<li><a href="troubleshooting">Troubleshooting</a></li>
</ul>
</div>
<div>
In order to run the test searches, please provide the user token generated when you registered to use the service
and hit the reset button. This will regenerate valid search URL's containing your unique access key. Searches will not
work without a vaild user token. User tokens are free and can be obtained by registering at <a href="/COLWSReg">/COLWSReg</a><br/>
  <form>
    <table>
      <tr>
        <td>User token</td>
        <td><input name="user_token"/></td>
      </tr>
      <tr>
        <td colspan="2"><input type="submit" value="update"/></td>
      </tr>
    </table>
  </form>
</div>
<div style="clear: both">&nbsp;</div>
<div id="main">
<h1>Test Searches</h1>
<div class="help">Click the +/- to expand/close the tree</div>
<a class="tree" onClick="Toggle(this)"><img src="<%=base_dir%>/images/plus.gif" title="Click here to expand/close">Product Search</a><div style="display:none">
    <ul class="inner">
      <li class="yes"><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22ICT%20And%20Art%22&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST Title Search for "ICT and Art" returning max of ten records, starting at record 1</a></li>
      <li class="yes"><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22ICT%20And%20Art%22&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>&stylesheet=<%=base_dir%>/style1.xsl">REST Title Search for "ICT and Art" returning max of ten records, starting at record 1, using a stylesheet</a></li>
      <li class="yes"><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22K*%22&amp;startRecord=1&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST Title Search for "k*" returning max of ten records, starting at record 1  presentation schema = learningresource</a></li>
      <li class="no"><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22K*%22&amp;startRecord=11&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST Title Search for "k*" returning max of ten records, starting at record 11</a></li>
      <li class="yes"><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22K*%22&amp;startRecord=11&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST Title Search for "k*" returning max of ten records, starting at record 11, presentation schema = learningresource</a></li>
      <li class="no"><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=lom.subject%3d%22Gg%22&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST Subject search for products "Gg" (Geography)</a></li>
      <li class="yes"><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=lom.subject%3d%22Mfl%22&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST Subject search for products "Mfl" (Modern Foreign languages)</a></li>
      <li class="yes"><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=lom.keystage%3d%221%22&amp;startRecord=1&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products for keystage 1</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=lom.language%3d%222014%22&amp;startRecord=1&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products language = danish</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=lom.resourceType%3d%22Exploration%22&amp;startRecord=1&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products by resourceType "Exploration"</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.intendedEndUserRole%3d%22Learner%22&amp;startRecord=1&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products by intendedEndUserRole: Learner</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22K*%22&amp;startRecord=11&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products by SENOnly</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=lom.Priced%3d1&amp;startRecord=11&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products which are priced</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=lom.Priced%3d0&amp;startRecord=11&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products which are free</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22K*%22&amp;startRecord=11&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products by aggregationLevel</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22K*%22&amp;startRecord=11&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products by whiteboard</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22K*%22&amp;startRecord=11&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products by specifier</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22K*%22&amp;startRecord=11&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products by School Year</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22K*%22&amp;startRecord=11&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products by provider id</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22K*%22&amp;startRecord=11&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products by Supplier name</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22K*%22+and+lom.Priced%3d1&amp;startRecord=11&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products starting with K which are priced</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=dc.title%3d%22K*%22+and+lom.Priced%3d0&amp;startRecord=11&amp;maximumRecords=10&amp;recordSchema=learningresource&user_token=<%=user_token%>">REST products starting with K which are not priced</a></li>
    </ul>
  </div>
<a class="tree" onClick="Toggle(this)"><img src="<%=base_dir%>/images/plus.gif" title="Click here to expand/close">Taxon Search</a><div style="display:none">
    <ul class="inner">
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=col.vocabId%3dxxxx&amp;startRecord=1&amp;maximumRecords=10&user_token=<%=user_token%>&landscape=Vocabs">TBA - REST Subject Vocab</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=col.vocabId%3d253&amp;startRecord=1&amp;maximumRecords=10&user_token=<%=user_token%>&landscape=Vocabs">REST MFLanguage Vocab</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=col.vocabId%3dxxxx&amp;startRecord=1&amp;maximumRecords=10&user_token=<%=user_token%>&landscape=Vocabs">TBA - REST Taxon Hierarchy Vocab</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=col.vocabId%3dxxxx&amp;startRecord=1&amp;maximumRecords=10&user_token=<%=user_token%>&landscape=Vocabs">TBA - REST Hierarchy Name Vocab</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=col.vocabId%3d3094&amp;startRecord=1&amp;maximumRecords=10&user_token=<%=user_token%>&landscape=Vocabs">REST Key Stage Vocab</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=col.vocabId%3d3007&amp;startRecord=1&amp;maximumRecords=10&user_token=<%=user_token%>&landscape=Vocabs">REST School Year Vocab</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=col.vocabId%3d2877&amp;startRecord=1&amp;maximumRecords=10&user_token=<%=user_token%>&landscape=Vocabs">REST Learning Resource Type Vocab</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=col.vocabId%3d2861&amp;startRecord=1&amp;maximumRecords=10&user_token=<%=user_token%>&landscape=Vocabs">REST Intended End user Role Vocab</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=col.vocabId%3dxxxx&amp;startRecord=1&amp;maximumRecords=10&user_token=<%=user_token%>&landscape=Vocabs">TBA - REST Aggregation Level Vocab</a></li>
    </ul>
  </div>
  <a class="tree" onClick="Toggle(this)"><img src="<%=base_dir%>/images/plus.gif" title="Click here to expand/close">Supplier Search</a><div style="display:none">
    <ul class="inner">
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=col.supplierName%3d%22Knowledge%22&amp;startRecord=1&amp;maximumRecords=10&user_token=<%=user_token%>&landscape=Suppliers">REST Supplier name Search for "knowledge" returning max of ten records, starting at record 1</a></li>
      <li><a href="<%=base_dir%>/webservices/search?operation=SearchRetrieve&amp;query=col.supplierId%3d%22SUP_KINT%22&amp;startRecord=1&amp;maximumRecords=10&user_token=<%=user_token%>&landscape=Suppliers">REST Supplier id Search for "SUP_KINT" returning max of ten records, starting at record 1</a></li>
    </ul>
  </div>
</div>
</body></html>
