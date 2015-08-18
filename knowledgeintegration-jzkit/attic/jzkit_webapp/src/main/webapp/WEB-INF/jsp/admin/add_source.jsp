<%@ page import="org.hibernate.*" %>
<%@ page import="org.hibernate.type.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%
String base_dir = request.getContextPath();
WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(application);
org.jzkit.search.provider.iface.JZKitPluginMetadata plugin = (org.jzkit.search.provider.iface.JZKitPluginMetadata) request.getAttribute("plugin");

if ( plugin != null ) {
  String source_type = plugin.getPluginCode();
%>
<div id="MainContent">
  <div id="ORPanel">
    <div id="Weblet">
      <h1>Add Source: <%=plugin.getPluginName()%> (<%=source_type%>)</h1>
      <p class="type1">
        <form action="<%=base_dir%>/secure/admin/processNewSource">
          <input type="hidden" name="plugin_code" value="<%=plugin.getPluginCode()%>"/>
          <input type="hidden" name="class_name" value="<%=plugin.getPluginClassName()%>"/>
          <table>
            <tr><td align="right">Protocol:</td><td><%=plugin.getPluginCode()%></td><td></td></tr>
            <tr><td align="right">Class:</td><td><%=plugin.getPluginClassName()%></td><td></td></tr>
            <tr><td align="right">Code:</td><td><input type="text" name="source_code"/></td><td>Well known code for this source, if blank a GUID will be created.</td></tr>
            <tr><td align="right">Service Name:</td><td><input type="text" name="service_name"/></td><td>Full Display Name.</td></tr>
            <tr><td align="right">Service Short Name:</td><td><input type="text" name="service_short_name"/></td><td>Short Display Name.</td></tr>
<%
          for ( int i=0; i<plugin.getProps().length; i++ ) {
            org.jzkit.search.provider.iface.PropDef prop_def = plugin.getProps()[i];
            %><tr><td><%=prop_def.getPropertyDisplayString()%></td><td><input type="text" name="prop_<%=prop_def.getPropertyDisplayString()%>"/></td></tr><%
          }
%>
            <tr><td colspan="3" align="right"><input type="Submit" value="Add ->"/></td></tr>
          </table>
        </form><br/>
      </p>
    </div>
  </div>
</div>

<%
}
else {
  %>Unknown plugin code, unable to add<%
}
%>
