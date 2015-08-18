<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="org.hibernate.*" %>
<%@ page import="org.hibernate.type.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.k_int.lom.datamodel.*" %>
<%
 LearningObjectHDO lo = (LearningObjectHDO) request.getAttribute("lom");
%>
<div id="datalist">
<ul>

<% if (lo.getVersion() != null) { %>
  <li><div class="head">Version:</div><div class="body"><%=lo.getVersion()%></div></li>
<% } %>


<% if ((lo.getTechnicalFormat() != null) && ( lo.getTechnicalFormat().size() > 0 ) ) { %>
  <li><div class="head">File Types:</div><div class="body">
  <%
    for ( java.util.Iterator i = lo.getTechnicalFormat().iterator(); i.hasNext(); ) {
      com.k_int.lom.datamodel.RefDataItemHDO item = (com.k_int.lom.datamodel.RefDataItemHDO) i.next();
      %> <%=item.getTerm()%> <%
      if ( item.getDescription() != null ) { %>(<%=item.getDescription()%>)<% }
      if ( i.hasNext() ) {
        %>, <%
      }
    }
%></div></li>
<% } %>

<% if (lo.getSize() != null) { %>
  <li><div class="head">Size:</div><div class="body"><%=lo.getSize()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (lo.getSupplier().getName() != null) { %>
  <li><div class="head">Author:</div><div class="body"><%=lo.getSupplier().getName()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (lo.getRights() != null) { %>
  <li><div class="head">Copyright and other restrictions:</div><div class="body"><%=lo.getRights()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (lo.getInstallationRemarks() != null) { %>
  <li><div class="head">Installation Remarks:</div><div class="body"><%=lo.getInstallationRemarks()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (lo.getOtherPlatformRequirements() != null) { %>
  <li><div class="head">Other platform requirements:</div><div class="body"><%=lo.getOtherPlatformRequirements()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if ( (lo.getRequirements() != null) && ( lo.getRequirements().size() > 0 ) ) { %>
  <%
    for ( java.util.Iterator i = lo.getRequirements().iterator(); i.hasNext(); ) {
      com.k_int.lom.datamodel.RequirementDTO item = (com.k_int.lom.datamodel.RequirementDTO) i.next();
      %> <li><div class="head"><%=item.getReqType()%></div><div class="body"> <%=item.getReqName()%> <%
   
      if (item.getReqMin() != null) { %> <%=item.getReqMin()%> <% }
      if (item.getReqMax() != null) { %> <%=item.getReqMax()%> <% } %>
     </div><div class="clearlefttiny">&#160;</div></li><%
    }
%>
<% } %>

</ul>
</div>
