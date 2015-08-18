<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="urn:com:sun:jersey:api:view" prefix="rbt" %>

<% 
  String base_dir = request.getContextPath(); 
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
      
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>${it.URI}</title>
    <link rel="stylesheet" type="text/css" media="all" href="<%=base_dir%>/main.css"/>

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
  </head>

  <body class="yui-skin-sam"> 
    <div id="top1">Header : ${it.URI}</div>

    <div id="bottom1">  
      <div class="links" style="margin:20px; text-align:center">
      </div>
    </div>

    <div id="center1">
      <form action="/WebEdit/Editor/Main">
        This is the item edit page
      </form>
    </div>

<script>

var data_fetch_callbacks = {
    // Successful XHR response handler
    success : function (o) {
        var messages = [];
        alert("Callback "+o);

        // Use the JSON Utility to parse the data returned from the server
        try {
            messages = YAHOO.lang.JSON.parse(o.responseText);
            alert("Got json data"+messages);
        }
        catch (x) {
            alert("JSON Parse failed!");
            return;
        }
    },
    failure : function(o) {
      alert("Failure"+o);
    }
};


(function() {
    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', height: 70, body: 'top1', gutter: '5px', collapse: false, resize: true },
                { position: 'bottom', height: 100, resize: true, body: 'bottom1', gutter: '5px', collapse: false },
                { position: 'center', header: 'main', body: 'center1', gutter: '5px' }
            ]
        });
        layout.on('render', function() {
            layout.getUnitByPosition('left').on('close', function() {
                closeLeft();
            });
        });
        alert("Render");
        layout.render();

        alert("Fetch");

        // Make the call to the server for JSON data
        YAHOO.util.Connect.initHeader("Accept", "application/json");
        YAHOO.util.Connect.asyncRequest('GET',"/WebEdit/Editor/Data/aaa", data_fetch_callbacks);
        alert("Done");
    });
})();
</script>

    <jsp:include page="/footer.jsp" />
  </body>

</html>
