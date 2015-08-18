<%
  response.setContentType("text/xml;charset=UTF-8");
  String xml = (String) request.getAttribute("xml");
%>
<%=xml%>
