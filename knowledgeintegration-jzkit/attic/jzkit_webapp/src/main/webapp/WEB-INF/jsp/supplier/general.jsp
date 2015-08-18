<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="org.hibernate.*" %>
<%@ page import="org.hibernate.type.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.k_int.lom.datamodel.*" %>
<div id="datalist">
<ul>
<%
  String base_dir = request.getContextPath();
  SupplierHDO sup = (SupplierHDO) request.getAttribute("sup");
  Session sess = (Session) request.getAttribute("sess");

  Iterator i = sess.createQuery("Select lo.title, lo.identifier, lo.description from com.k_int.lom.datamodel.LearningObjectHDO lo where lo.supplier.id = ? and lo.isActive=1")
                        .setParameter(0,sup.getId(),Hibernate.LONG)
                        .iterate();

  int ctr = 0;
  for ( ; i.hasNext(); ) {
    Object[] data = (Object[]) i.next();
    ctr++;
    %>
      <li><a href="<%=base_dir%>/product/<%=data[1]%>"><%=data[0]%></a>
      <a href="<%=base_dir%>/product/<%=data[1]%>"><img title="Open this link in a new window" alt="Open this link in a new window" class="new" src="<%=base_dir%>/images/new.gif" height="10" width="10"></a>
        <p><%=data[2]%></p>
      </li>
    <%
  }

  if ( ctr == 0 ) {
    %><p>Sorry, the supplier does not have any live products currently in the system.</p><%
  }
%>
</ul>
