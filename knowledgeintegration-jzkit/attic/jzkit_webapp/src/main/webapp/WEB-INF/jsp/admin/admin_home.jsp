<%@ page import="org.hibernate.*" %>
<%@ page import="org.hibernate.type.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.jzkit.ServiceDirectory.SearchServiceDescriptionDBO" %>
<%
String base_dir = request.getContextPath();
WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(application);
java.util.List<org.jzkit.search.provider.iface.JZKitPluginMetadata> jzkit_plugins = (java.util.List) ctx.getBean("JZKitPluginRegistry");
org.hibernate.Session sess = null;
try {
  org.hibernate.SessionFactory factory = (org.hibernate.SessionFactory) ctx.getBean("JZKitSessionFactory");
  sess = factory.openSession();
%>
<div id="MainContent">
  <div id="ORPanel">
    <div id="Weblet">
      <h1>Data Sources</h1>
      <p class="type1">
        Data Sources<br/>
        <table>
<%
  Iterator i = sess.createQuery("Select ssd from org.jzkit.ServiceDirectory.SearchServiceDescriptionDBO ssd").iterate();
  for ( ; i.hasNext(); ) {
    SearchServiceDescriptionDBO ssd = (SearchServiceDescriptionDBO) i.next();
      %><tr><td><%=ssd.getId()%></td>
            <td><a href="<%=base_dir%>/secure/admin/source/<%=ssd.getCode()%>"><%=ssd.getCode()%></a></td>
            <td><%=ssd.getServiceName()%></td></tr><%
  }
%>
        </table>
          Add new : 
          <%
            for ( java.util.Iterator i2 = jzkit_plugins.iterator(); i2.hasNext(); ) {
              org.jzkit.search.provider.iface.JZKitPluginMetadata plugin = (org.jzkit.search.provider.iface.JZKitPluginMetadata) i2.next();
              %><a href="<%=base_dir%>/secure/admin/addSource/<%=plugin.getPluginCode()%>"><%=plugin.getPluginName()%></a><%
              if ( i2.hasNext() ) { %>,&nbsp;<%}
            }
          %>
      </p>
      <h1>Record Crosswalks</h1>
      <p class="type1">
      </p>
      <h1>Query Crosswalks</h1>
      <p class="type1">
      </p>
      <h1>System Settings</h1>
      <p class="type1">
      </p>
    </div>
  </div>
</div>

<%
}
finally {
  try {
    if ( sess != null )
      sess.close();
  }
  catch ( Exception e ) {
    e.printStackTrace();
  }
}
%>

