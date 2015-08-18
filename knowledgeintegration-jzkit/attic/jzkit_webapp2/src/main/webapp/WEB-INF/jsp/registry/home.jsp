Registry home page

1. Get hold of jzkit config object

<%
  String base_dir = request.getContextPath();

  javax.naming.InitialContext context = new javax.naming.InitialContext();
  javax.naming.Context environment = (javax.naming.Context) context.lookup("java:comp/env");
  org.jzkit.service.JZKitService jzkit = (org.jzkit.service.JZKitService) environment.lookup("jir/IRService");
%>
Got global IR service

Search Services
<%
  org.jzkit.configuration.api.Configuration config = jzkit.getConfig();
  for (java.util.Iterator i = config.enumerateRepositories(); i.hasNext(); ) {
    org.jzkit.ServiceDirectory.SearchServiceDescriptionDBO ssd = (org.jzkit.ServiceDirectory.SearchServiceDescriptionDBO) i.next();
    %>Got search service<%
  }
%>
<a href="<%=base_dir%>/registry/service">New Service</a>
  
