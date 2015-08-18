<%
  String base_dir = request.getContextPath();
  String redirectURL = base_dir+"/webservices/home";
  response.sendRedirect(redirectURL);
%>


