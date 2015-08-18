Registry home page

Create new search service
<form>
  New Service Plugin: <select name="ssd_class">
    <option name="z3950">Z3950</option>
  </select><br/>
  New Service Code<input type="text" name="ssd_code">
</form>

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

