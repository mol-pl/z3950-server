<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="urn:com:sun:jersey:api:view" prefix="rbt" %>

<% 
  String base_dir = request.getContextPath();
  java.security.Principal user = request.getUserPrincipal();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
      
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JDBC Datasource administration</title>
    <link rel="stylesheet" type="text/css" media="all" href="<%=base_dir%>/css/all.css"/>

    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/reset-fonts-grids/reset-fonts-grids.css"> 
    <!-- Skin CSS files resize.css must load before layout.css --> 
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/assets/skins/sam/resize.css"> 
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/assets/skins/sam/layout.css"> 
    <!-- Utility Dependencies --> 
    <script src="http://yui.yahooapis.com/2.8.0r4/build/yahoo-dom-event/yahoo-dom-event.js"></script>  
    <script src="http://yui.yahooapis.com/2.8.0r4/build/dragdrop/dragdrop-min.js"></script>  
    <script src="http://yui.yahooapis.com/2.8.0r4/build/element/element-min.js"></script>  
    <!-- Optional Animation Support--> 
    <script src="http://yui.yahooapis.com/2.8.0r4/build/animation/animation-min.js"></script>  
    <!-- Optional Resize Support --> 
    <script src="http://yui.yahooapis.com/2.8.0r4/build/resize/resize-min.js"></script> 
    <!-- Source file for the Layout Manager --> 
    <script src="http://yui.yahooapis.com/2.8.0r4/build/layout/layout-min.js"></script> 

    <!-- Dependencies -->
    <script src="http://yui.yahooapis.com/2.8.0r4/build/yahoo/yahoo-min.js"></script>

    <!-- Source file -->
    <script src="http://yui.yahooapis.com/2.8.0r4/build/json/json-min.js"></script>

    <script src="http://yui.yahooapis.com/2.8.0r4/build/event/event-min.js"></script> 
    <script src="http://yui.yahooapis.com/2.8.0r4/build/connection/connection-min.js"></script> 

    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/treeview/assets/skins/sam/treeview.css" />
    <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/treeview/treeview-min.js"></script>

  </head>

<body class="yui-skin-sam">

  <div class="page-wrapper">
    <div id="top1">
      JDBC Bridge Administration
      <div class="user-status">
        <% if ( user != null ) { %>
        Welcome back <%=user.getName()%> (No messages)<br/>
        My Account <a href="<%=base_dir%>/logout">Logout</a><br/>
        <% } else { %>
          <ul class="navlist">
            <li><a href="<%=base_dir%>/join">Join</a></li>
            <li><a href="<%=base_dir%>/login">LogIn</a></li>
          </ul>
        <% } %>
        <br/>
      </div>
    </div>
    <div>

    <div id="left1">
      <div id="treeDiv1"></div> 
    </div>

    <div id="center1">
      <div id="CreateDatasourceForm">
        Create a new datasource here
        <form method="post">
          <table>
            <tr>
              <td>
                Name
              </td>
              <td>
                <input type="text" name="datasource_name"/>
              </td>
            </tr>
            <tr>
              <td>
                JDBC Driver:
              </td>
              <td>
                <select name="jdbc_driver">
                  <option value="com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource">MySQL 5.1.12</option>
                  <option value="a.b.ora">Oracle 10g</option>
                </select>
              </td>
            </tr>
            <tr>
              <td>
                JDBC URL:
              </td>
              <td>
                <input type="text" name="jdbc_url" value="jdbc:mysql://localhost:3306/kiunittest"/>
              </td>
            </tr>
            <tr>
              <td>
                Database Username:
              </td>
              <td>
                <input type="text" name="jdbc_user"/>
              </td>
            </tr>
            <tr>
              <td>
                Database Password:
              </td>
              <td>
                <input type="password" name="jdbc_pass"/>
              </td>
            </tr>
            <tr>
              <td>
              </td>
              <td>
                <input type="submit"/>
              </td>
            </tr>
          </table>
        </form>
      </div>
    </div>

<script>

(function() {
    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'left', header: 'Explorer', width: 150, body: 'left1', gutter: '5px', collapse: false, resize: true },
                { position: 'top', height: 50, body: 'top1', gutter: '5px', collapse: false, resize: true },
                { position: 'bottom', height: 100, resize: true, body: 'bottom1', gutter: '5px', collapse: false },
                { position: 'center', header: 'Main', body: 'center1', gutter: '5px' }
            ]
        });

        layout.on('render', function() {
            layout.getUnitByPosition('left').on('close', function() {
                closeLeft();
            });
        });
 
        buildTree();

        layout.render();
    });
})();

</script>

<script>

function buildTree() {
   //create a new tree:
   tree = new YAHOO.widget.TreeView("treeDiv1");
   
   //turn dynamic loading on for entire tree:
   // tree.setDynamicLoad(loadNodeData, currentIconMode);
   
   //get root node for tree:
   var root = tree.getRoot();

   //add child nodes for tree; our top level nodes are
   //all the states in India:
   // var aStates = ["Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat","Haryana","Himachal Pradesh","Jammu and Kashmir","Jharkhand","Karnataka"];
   // for (var i=0, j=aStates.length; i<j; i++) {
   //      var tempNode = new YAHOO.widget.TextNode(aStates[i], root, false);
   // }
   
   // var new_datasource_node = new YAHOO.widget.TextNode("New Datasource", root, false);
   var new_datasource_node = new YAHOO.widget.HTMLNode('<a href="<%=base_dir%>/admin/JDBCDataSource">New Datasource</a>', root, false);
   new_datasource_node.isLeaf = true;

   // Use the isLeaf property to force the leaf node presentation for a given node.
   // This disables dynamic loading for the node.
   // var tempNode = new YAHOO.widget.TextNode('This is a leaf node', root, false);
   // tempNode.isLeaf = true;

   //render tree with these toplevel nodes; all descendants of these nodes
   //will be generated as needed by the dynamic loader.
   tree.draw();
}
</script>

  </body>

</html>
