<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="urn:com:sun:jersey:api:view" prefix="rbt" %>
<%
  String base_dir = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div id="middle">
  <p> 

Login
  <form action='j_security_check' method='post'>
    <table>
      <tr>
        <td>Name:</td>
        <td><input type='text' name='j_username'></td>
      </tr>
      <tr>
        <td>Password:</td>
        <td><input type='password' name='j_password' size='8'></td>
      </tr>
    </table>
    <br>
    <input type='submit' value='login'>
  </form>

  </p>
</div>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />
