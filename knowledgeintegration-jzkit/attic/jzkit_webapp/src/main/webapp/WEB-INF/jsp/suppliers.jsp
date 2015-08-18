<%
  String base_dir = request.getContextPath(); 
  String[] links = new String[] {"0-9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z" };

%>


<h2>Supplier Search</h2>
<p>Please select a letter from the list below to view suppliers.</p> &nbsp;|&nbsp;

<%  
  for (int i=0; i<java.lang.reflect.Array.getLength(links); i++) {
     String srch_url = base_dir+"/search?landscape=Suppliers&operation=SearchRetrieve&query="+links[i]+"*&maximumRecords=10&recordSchema=supplier&stylesheet=/webservices/public/xsl/supplier.xsl";
     %> 
     <a href="<%=srch_url%>"><%=links[i]%></a>&nbsp;|&nbsp;
<% } %>

