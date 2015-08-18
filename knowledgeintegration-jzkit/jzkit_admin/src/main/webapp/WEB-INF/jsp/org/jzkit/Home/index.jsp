<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="urn:com:sun:jersey:api:view" prefix="rbt" %>
<%
  String base_dir = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div id="middle">
  <p>
    Some blrub about your site and information that you want on the home page.
    <a href="localhost:8080/Template/admin/DataSources">Edit datasources</a>
  </p>
</div>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />
