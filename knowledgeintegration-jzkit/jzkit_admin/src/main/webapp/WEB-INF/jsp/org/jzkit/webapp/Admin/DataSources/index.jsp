<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="urn:com:sun:jersey:api:view" prefix="rbt" %>
<%@taglib uri="http://www.k-int.com/taglibs/cb4-1.0.0" prefix="cb4" %>

<% 
  String base_dir = request.getContextPath();
  java.security.Principal user = request.getUserPrincipal();

  // This page can be used to edit a new datasource
  com.k_int.sql.config.JDBCDataSource new_data_source = new com.k_int.sql.config.JDBCDataSource();
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
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/treeview/assets/skins/sam/treeview.css" />

    <script src="http://yui.yahooapis.com/2.8.0r4/build/yuiloader/yuiloader-min.js"></script>

  </head>

<body class="yui-skin-sam">

  <script>
    // Instantiate and configure Loader:
    var loader = new YAHOO.util.YUILoader({
 
    // Identify the components you want to load.  Loader will automatically identify
    // any additional dependencies required for the specified components.
    // require: ["colorpicker", "treeview"],
    require: ["layout","treeview","event", "datatable" ],
 
    // Configure loader to pull in optional dependencies.  For example, animation
    // is an optional dependency for slider.
    loadOptional: true,
 
    // The function to call when all script/css resources have been loaded
    onSuccess: function() {
        //this is your callback function; you can use
        //this space to call all of your instantiation
        //logic for the components you just loaded.
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
        buildSourceList();

        layout.render();
    },
 
    // Configure the Get utility to timeout after 10 seconds for any given node insert
    timeout: 10000,
 
    // Combine YUI files into a single request (per file type) by using the Yahoo! CDN combo service.
    combine: true
    });
 
    // Load the files using the insert() method. The insert method takes an optional
    // configuration object, and in this case we have configured everything in
    // the constructor, so we don't need to pass anything to insert().
    loader.insert();
  </script>

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
        <cb4:cbform id="NewDataSourceForm" method="post" objectBeingEdited="<%=new_data_source%>">
          <input type="hidden" name="meta.root" value="create"/>
          <input type="hidden" name="meta.root.__type" value="root"/>

          <table>
            <tr>
              <td>
                Name
              </td>
              <td>
                <cb4:cbinput property="datasourceName"/>
              </td>
            </tr>
            <tr>
              <td>
                Identifier
              </td>
              <td>
                <cb4:cbinput property="datasourceIdentifier"/>
              </td>
            </tr>
            <tr>
              <td>
                JDBC Driver:
              </td>
              <td>
                <select name="data.root.driverClass">
                  <option value="com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource">MySQL 5.1.12</option>
                  <option value="a.b.ora">Oracle 10g</option>
                </select>
              </td>
            </tr>
            <tr>
              <td>
                Properties:
              </td>
              <td>
                <table border="1">
                  <tr> <th>Property name</th> <th>Property Value</th> </tr>
                  <tr> <td>JDBC Driver</td> <td><input type="text" name="properties[JDBC Driver]" value="org.mysql.Driver"/></td> </tr>
                </table>
                new property name: <input type="text" name="new_property_name"/> <input type="submit" name="" value="addProperty:properties"/>
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
        </cb4:cbform>
      </div>

      <div id="CurrentDataSources">
        <div id="paginated"></div>
      </div>


    </div>

<script>

function buildTree() {
   //create a new tree:
   tree = new YAHOO.widget.TreeView("treeDiv1");
   
   //turn dynamic loading on for entire tree:
   var root = tree.getRoot();
   var new_datasource_node = new YAHOO.widget.HTMLNode('<a href="<%=base_dir%>/admin/DataSources">Datasources</a>', root, false);
   new_datasource_node.isLeaf = true;
   tree.draw();
}

function formatSourceLink(el, oRecord, oColumn, oData) {
  var link = "<%=base_dir%>/admin/DataSources/"+oData;
  el.innerHTML = '<a href="'+link+'">'+oData+'</a>';
}

function buildSourceList() {

  var myColumnDefs = [ 
    {key:"id", sortable:true, resizeable:true}, 
    {key:"datasourceName", sortable:true, resizeable:true}, 
    {key:"datasourceIdentifier", sortable:true, resizeable:true, formatter:formatSourceLink}, 
    {key:"driverClass", sortable:true, resizeable:true}, 
    {key:"properties", sortable:false, resizeable:true} 
  ]; 
	 
  var myDataSource = new YAHOO.util.XHRDataSource("<%=base_dir%>/admin/DataSources.json");
  myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
  myDataSource.responseSchema = {
    resultsList: "sources",
    fields: ["id","datasourceName","datasourceIdentifier","driverClass","properties"]
  };

  var myDataTable = new YAHOO.widget.DataTable("paginated", myColumnDefs, myDataSource, {caption:"DataTable Caption"}); 
}

</script>

  </body>

</html>
