<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="urn:com:sun:jersey:api:view" prefix="rbt" %>
<%
  String base_dir = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div id="middle">
  <p>
    This is the setup page - it should only be shown if this is a fresh install
  </p>
</div>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />