Registry home page

<%
  String base_dir = request.getContextPath();
  javax.naming.InitialContext context = new javax.naming.InitialContext();
  javax.naming.Context environment = (javax.naming.Context) context.lookup("java:comp/env");
  org.jzkit.service.JZKitService jzkit = (org.jzkit.service.JZKitService) environment.lookup("jir/IRService");
  org.jzkit.configuration.api.Configuration jzkit_config = jzkit.getConfig();
  java.util.Map available_plugins = jzkit_config.getBackendPlugins();

  if ( available_plugins != null ) {
%>

Create new search service
<form action="">
  New Service Plugin: <select name="ssd_class">
  <%
    for ( java.util.Iterator i = available_plugins.entrySet().iterator(); i.hasNext(); ) {
      org.jzkit.search.provider.iface.JZKitPluginMetadata p = (org.jzkit.search.provider.iface.JZKitPluginMetadata) i.next();
      %><option name="<%=p.getPluginCode()%>"><%=p.getPluginName()%></option><%
    }
  %>
  </select><br/>
  New Service Code<input type="text" name="ssd_code">
  <input type="submit">
</form>

<%
} else {
%>
  Unable to obtain search plugin list.
<% } %>
Got global IR service

Search Services
<%
  for (java.util.Iterator i = jzkit_config.enumerateRepositories(); i.hasNext(); ) {
    org.jzkit.ServiceDirectory.SearchServiceDescriptionDBO ssd = (org.jzkit.ServiceDirectory.SearchServiceDescriptionDBO) i.next();
    %>Got search service<%
  }
%>

