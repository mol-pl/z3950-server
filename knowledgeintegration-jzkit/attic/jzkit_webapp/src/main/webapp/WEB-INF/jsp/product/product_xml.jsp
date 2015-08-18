<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %><%
  String base_dir = request.getContextPath();
  String search_url = base_dir+"/search?landscape=Products&operation=SearchRetrieve&query=Graphite&maximumRecords=1&recordSchema=learningobject";
%><tiles:insert page="<%=search_url%>"/>
