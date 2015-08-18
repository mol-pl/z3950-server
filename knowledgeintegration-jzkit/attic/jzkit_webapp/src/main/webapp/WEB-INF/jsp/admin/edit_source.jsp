<%@ page import="org.hibernate.*" %>
<%@ page import="org.hibernate.type.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%
String base_dir = request.getContextPath();
WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(application);

%>
<div id="MainContent">
  <div id="ORPanel">
    <div id="Weblet">
      <h1>Edit Source</h1>
      <p class="type1">
        <form action="<%=base_dir%>/secure/admin/processEditSource">
        </form><br/>
      </p>
    </div>
  </div>
</div>
