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
<% 
  for ( java.util.Iterator i = lo.getEvaluations().iterator(); i.hasNext(); ) {
    EvaluationDTO eval = (EvaluationDTO) i.next();
  %>

<% if (eval.getEvaluatorID() != null) { %>
  <li><div class="head">Evaluator Code:</div><div class="body"><%=eval.getEvaluatorID()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (eval.getDateTimeEvaluated() != null) { %>
  <li><div class="head">Date Of Evaluation:</div><div class="body"><%=eval.getDateTimeEvaluated()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (eval.getVersionEvaluated() != null) { %>
  <li><div class="head">Version Evaluated:</div><div class="body"><%=eval.getVersionEvaluated()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (eval.getEvalDescription() != null) { %>
  <li><div class="head">Brief Evaluation:</div><div class="body"><%=eval.getEvalDescription()%></div><div class="clearlefttiny">&#160;</div></li>
<% } %>

<% if (eval.getEvaluationURL() != null) { 
    String eval_url = eval.getEvaluationURL();
    java.net.URLDecoder decoder = new java.net.URLDecoder();
    eval_url = decoder.decode(eval_url, "utf-8");
   %>
  <li><div class="head">Full Evaluation:</div><div class="body"><a href="<%=eval_url%>"><%=eval_url%>
</a></div><div class="clearlefttiny">&#160;</div></li>
  <% } %>
<% } %>
</ul>
</div>
