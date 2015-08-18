<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="org.hibernate.*" %>
<%@ page import="org.hibernate.type.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.k_int.lom.datamodel.*" %>
<%
 LearningObjectHDO lo = (LearningObjectHDO) request.getAttribute("lom");
%>
<div id="datalist">
<ul>

<% if (lo.getEducationalDescription() != null) { %>
  <li><div class="head">Notes for Teachers:</div><div class="body"><%=lo.getEducationalDescription()%></div><div class="clearlefttiny">&#160;</div></li>
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

<% if (lo.getClassificationDescription() != null) { %>
  <li><div class="head">SEN Description:</div><div class="body"><%=lo.getClassificationDescription()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if ((lo.getContext() != null) && (lo.getContext().size() > 0)) { %>
  <li><div class="head">Context:</div><div class="body">
  <%
    for ( java.util.Iterator i = lo.getContext().iterator(); i.hasNext(); ) {
      com.k_int.lom.datamodel.RefDataItemHDO subject_term = (com.k_int.lom.datamodel.RefDataItemHDO) i.next();
      if ( subject_term != null ) {
        %><%=subject_term%><%
        if ( i.hasNext() ) {
          %>, <%
        }
      }
    }
  %>
  </div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (lo.getDifficulty() != null) { %>
  <li><div class="head">Difficulty:</div><div class="body"><%=lo.getDifficulty()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (lo.getTypicalLearningTime() != null) { %>
  <li><div class="head">Typical Learning Time:</div><div class="body"><%=lo.getTypicalLearningTime()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>


<% if (lo.getDeliveryMethod() != null) { %>
  <li><div class="head">Method of delivery:</div><div class="body"><%=lo.getDeliveryMethod()%>
  <% if (lo.getDeliveryMethodDescription() != null) { %>
    <br/><%=lo.getDeliveryMethodDescription()%></div><div class="clearlefttiny">&#160;</div></li>
  <% } %>
<% } %>

<% if (lo.getInteractivityType() != null ) { %>
<li><div class="head">Interactivity Type:</div><div class="body"><%=lo.getInteractivityType()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (lo.getInteractivityLevel() != null ) { %>
<li><div class="head">Interactivity Level:</div><div class="body"><%=lo.getInteractivityLevel()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (lo.getIntendedEndUserRole() != null) { %>
  <li><div class="head">Who is the resource for:</div><div class="body">
  <%
    for ( java.util.Iterator i = lo.getIntendedEndUserRole().iterator(); i.hasNext(); ) {
      com.k_int.lom.datamodel.RefDataItemHDO item = (com.k_int.lom.datamodel.RefDataItemHDO) i.next();
      if ( item != null ) {
        %> <%=item.getTerm()%> <%
        if ( item.getDescription() != null ) { %>(<%=item.getDescription()%>)<% }
        if ( i.hasNext() ) {
          %>, <%
        }
      }
    }
  %>
  </div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if ((lo.getGeneralKeywords() != null)&&(lo.getGeneralKeywords().size() > 0)) { %>
<li><div class="head">General Keywords:</div>
<div class="body">
<%
    for ( java.util.Iterator i = lo.getGeneralKeywords().iterator(); i.hasNext(); ) {
      com.k_int.lom.datamodel.KeywordHDO item = (com.k_int.lom.datamodel.KeywordHDO) i.next();
      if ( item != null ) {
        %> <%=item%> <%
        if ( i.hasNext() ) {
          %>, <%
        }
      }
    }
%>
</div><div class="clearlefttiny">&#160;</div></li>
<% } %>


<% if ((lo.getXtags() != null)&&(lo.getXtags().size() > 0)) { %>
<li><div class="head">Cross Curricula Themes:</div><div class="body">
<%
  for ( java.util.Iterator i = lo.getXtags().iterator(); i.hasNext(); ) {
    com.k_int.zthes.datamodel.TermRevisionHDO subject_term = (com.k_int.zthes.datamodel.TermRevisionHDO) i.next();
    if ( subject_term != null ) {
      %><%=subject_term%><%
      if ( i.hasNext() ) { %>, <% }
    }
  }
%>
</div><div class="clearlefttiny">&#160;</div></li>
<% } %>
</ul>


</div>
