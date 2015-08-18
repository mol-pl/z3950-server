<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ page import="com.k_int.QueryDescriptor.*" %>
<%
  String username = request.getRemoteUser();
  String base_dir = request.getContextPath();
%>
<div id="MainContent">
  <div id="ORPanel">
    <div id="Weblet">
    <h1>Login</h1>
    <p class="type1">JZKit MetaSearch Web Application.</p>
    <h4 class="section-heading">Existing User Login</h4> Click <a href="<%=base_dir%>/register">Here</a> to register as a new user.
    <table style="border: 1px solid #000; margin:10px; background: #dfdfff;"><tr><td>
      <center>
      <strong><small>Login to your <span class="show-hide"><br /></span>account</small></strong>
      <form name="loginForm" method="post" action="<%=base_dir%>/j_security_check">
        <table>
          <tr>
            <td valign="top">&nbsp;</td>
          </tr>
          <tr> 
            <td class="tab-form" valign="top">
            <label style="font-weight:bold;" for="j_username">Username</label><br/><input class="login-inputbox"  style="background: #fff;" name="j_username" type="text" id="j_username" value="" /></td>
          </tr>
          <tr> 
            <td class="tab-form" valign="top">
              <label style="font-weight:bold;" for="j_password">Password</label><br/><input class="login-inputbox" style="background: #fff;" type="password" name="j_password" id="j_password" />
      	    </td>
          </tr>
          <tr>
            <td class="tab-form"><input class="submit-login" type="submit" name="submit" value="Login" /></td>
          </tr>
        </table>
      </form>
    </center>
    </td>
    </tr>
    </table>
    </div>
  </div>
</div>

