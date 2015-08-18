<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="org.hibernate.*" %>
<%@ page import="org.hibernate.type.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.k_int.lom.datamodel.*" %>
<%
String base_dir = request.getContextPath();
LearningObjectHDO lo = (LearningObjectHDO) request.getAttribute("lom");
%>
<div id="datalist">
<ul>
<% if (lo.getDescription() != null) { %>
<li><div class="head">Description:</div><div class="body"><%=lo.getDescription()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (lo.getSupplier() != null ) { %>
<li><div class="head">Publisher:</div><div class="body"><%=lo.getSupplier().getName()%><br/>
<!--ii <a>See other resources by this publisher</a>ii--></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (lo.getAggregationLevel() != null ) { %>
<li><div class="head">Covers:</div><div class="body"><%=lo.getAggregationLevel().getDescription()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if ((lo.getSpecialEducationalNeeds() != null) && (lo.getSpecialEducationalNeeds().size() > 0)) { %>
  <li><div class="head">SEN:</div><div class="body">
  <%
    for ( java.util.Iterator i = lo.getSpecialEducationalNeeds().iterator(); i.hasNext(); ) {
      com.k_int.zthes.datamodel.TermRevisionHDO subject_term = (com.k_int.zthes.datamodel.TermRevisionHDO) i.next();
      if ( subject_term != null ) {
        if ( subject_term.getTermName() != null ) { %>(<%=subject_term.getTermName()%>)<% }
        if ( i.hasNext() ) {
          %>, <%
        }
      }
    }
  %>
  </div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if ( ( lo.getIsWhiteboard() != null ) && ( lo.getIsWhiteboard().equals(Boolean.TRUE) ) ) { %>
<li><div class="head">Whiteboard:</div><div class="body"><%=lo.getWhiteboardTXT()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (lo.getTeachingSubjects() != null) { %>
  <li><div class="head">Teaching Subject(s):</div><div class="body">
  <%
    for ( java.util.Iterator i = lo.getTeachingSubjects().iterator(); i.hasNext(); ) {
      com.k_int.zthes.datamodel.TermRevisionHDO subject_term = (com.k_int.zthes.datamodel.TermRevisionHDO) i.next();
      if ( subject_term != null ) {
        %> <%=subject_term.getTermName()%> <%
        if ( i.hasNext() ) {
          %>, <%
        }
      }
    }
  %>
  </div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if ((lo.getKeystages() != null) && (lo.getKeystages().size() > 0)) { %>
  <li><div class="head">Keystage:</div><div class="body">
  <%
    for ( java.util.Iterator i = lo.getKeystages().iterator(); i.hasNext(); ) {
      com.k_int.zthes.datamodel.TermRevisionHDO subject_term = (com.k_int.zthes.datamodel.TermRevisionHDO) i.next();
      if ( subject_term != null ) {
        %> <%=subject_term.getTermName()%> <%
        if ( i.hasNext() ) {
          %>, <%
        }
      }
    }
  %>
  </div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if ((lo.getSchoolYears() != null) && (lo.getSchoolYears().size() > 0)) { %>
  <li><div class="head">School Year:</div><div class="body">
  <%
    for ( java.util.Iterator i = lo.getSchoolYears().iterator(); i.hasNext(); ) {
      com.k_int.zthes.datamodel.TermRevisionHDO subject_term = (com.k_int.zthes.datamodel.TermRevisionHDO) i.next();
      if ( subject_term != null ) {
        %> <%=subject_term.getTermName()%> <%
        if ( i.hasNext() ) {
          %>, <%
        }
      }
      else {
        %>Missing<%
      }
    }
  %>
  </div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if ((lo.getTechnicalFormat() != null)&&(lo.getTechnicalFormat().size() > 0)) { %>
  <li><div class="head">Product Type:</div><div class="body">
  <%
    for ( java.util.Iterator i = lo.getTechnicalFormat().iterator(); i.hasNext(); ) {
      com.k_int.lom.datamodel.RefDataItemHDO item = (com.k_int.lom.datamodel.RefDataItemHDO) i.next();
      if ( item != null ) {
        %> <%=item%> <%
        if ( i.hasNext() ) {
          %>, <%
        }
      }
    }
    if ( lo.getProductTypes() != null ) {
      for ( java.util.Iterator i2 = lo.getProductTypes().iterator(); i2.hasNext(); ) {
        com.k_int.lom.datamodel.RefDataItemHDO item = (com.k_int.lom.datamodel.RefDataItemHDO) i2.next();
        if ( item != null ) {
          %><%=item%> <%
          if ( i2.hasNext() ) {
            %>, <br/><%
          }
        }
      }
    }
  %>
  </div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if ( ( lo.getSupplierIdentCatalog() != null ) && ( lo.getSupplierIdentifier() != null ) ) { %>
  <li><div class="head">Product Code:</div>
    <div class="body">
      <% if (lo.getSupplierIdentCatalog().equals("URI")) { 
        String ident_url = lo.getSupplierIdentifier();
        if (ident_url.substring(0,4).equalsIgnoreCase("http") == false) {
         ident_url = "http://"+ident_url;
        } 
      %>
      <%=lo.getSupplierIdentCatalog()%>:  <a href="<%=ident_url%>"><%=ident_url%></a>
      <% } else { %>
        <%=lo.getSupplierIdentCatalog()%>: <%=lo.getSupplierIdentifier()%>
      <% } %>
    </div>
    <div class="clearlefttiny">&#160;</div>
  </li>
<% } %>
</ul>
</div>
