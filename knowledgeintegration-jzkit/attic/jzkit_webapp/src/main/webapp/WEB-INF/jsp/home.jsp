<%
  String base_dir = request.getContextPath();
%>
<div id="searchform" class="floatright">
      <form action="<%=base_dir%>/searchFormHandler" method="post">
          <h2>Search for educational resources</h2>
          <input type="radio" id="free" name="searchType" value="0" onclick="clearSearch();" onkeypress="clearSearch();"/><label for="free">Free resources</label>
          <input type="radio" id="priced" name="searchType" value="1" onclick="clearSearch();" onkeypress="clearSearch();"/><label for="priced">Priced resources</label>
          <input type="radio" id="both" name="searchType" value="both" onclick="clearSearch();" onkeypress="clearSearch();" checked="checked"/><label for="both">Both</label><br/>
          <input type="text" value="Enter search text here" name="searchText" onclick="clearSearch();" onkeypress="clearSearch();" id="searchText" size="50"/><label class="accesstext" for="searchText">Search Query</label><br/>
          <div id="searchlinks"><a class="floatleft" href="<%=base_dir%>/searchHelp">Search Help</a><span class="accesstext"> | </span><a href="<%=base_dir%>/advanced">Advanced Search</a><br/>
          <input class="button" type="image" alt="Search" src="<%=base_dir%>/images/search_button.gif"/></div>
      </form>
</div>
<img height="296" width="296" src="<%=base_dir%>/images/cubes.gif" title="Cubes image featuring the Curriculum Online colour scheme." alt="Cubes image featuring the Curriculum Online colour scheme."/>
<div class="clearing">&#160;</div>
