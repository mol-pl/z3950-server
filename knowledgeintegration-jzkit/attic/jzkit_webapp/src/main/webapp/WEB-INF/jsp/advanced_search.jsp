<%
  String base_dir = request.getContextPath();
%>
<h2>Advanced Search</h2>
<div>
    <form action="<%=base_dir%>/searchFormHandler" method="post" id="searchform">
<div class="head">Pricing Information:</div>
<div class="body">
      <input type="radio" id="free" name="searchType" value="0" onclick="clearSearch();" onkeypress="clearSearch();"/><label for="free">Free resources</label>
      <input type="radio" id="priced" name="searchType" value="1" onclick="clearSearch();" onkeypress="clearSearch();"/><label for="priced">Priced resources</label>
      <input type="radio" id="both" name="searchType" onclick="clearSearch();" onkeypress="clearSearch();" checked="checked" value="both"/><label for="both">Both</label><br/>
</div>


<div class="head">
          <label for="TeachingSubject">Teaching Subject: </label>
</div>

<div class="body">
<select name="TeachingSubject" id="TeachingSubject" onclick="clearSearch();" onkeypress="clearSearch();">
                <option selected="selected" value="">All</option>
                <option value="AD">Art and Design</option>
                <option value="BS">Business Studies</option>
                <option value="Ci">Citizenship</option>
                <option value="CPSHE">Citizenship and Personal, Social and Health Education</option>
                <option value="CS">Classical Studies (including Greek and Latin)</option>
                <option value="DT">Design and Technology</option>
                <option value="Ec">Economics</option>
                <option value="En">English</option>
                <option value="Eng">Engineering</option>
                <option value="FS">Foundation</option>
                <option value="Gg">Geography</option>
                <option value="Hi">History</option>
                <option value="HSC">Health and Social Care</option>
                <option value="ICT">Information and Communication Technology</option>
                <option value="LT">Leisure and Tourism</option>
                <option value="Lw">Law</option>
                <option value="Ma">Mathematics</option>
                <option value="Man">Manufacturing</option>
                <option value="MFL">Modern Foreign Languages</option>
                <option value="Mu">Music</option>
                <option value="PE">Physical Education</option>
                <option value="Ps">Psychology</option>
                <option value="PSHE">Personal, Social and Health Education</option>
                <option value="RE">Religious Education</option>
                <option value="Sc">Science</option>
                <option value="SS">Social Science</option>
          </select>
</div>
          <br/>

<div class="head">
          <label for="Keystage">Keystage: </label>
</div>
<div class="body">
<select name="Keystage" id="Keystage" onclick="clearSearch();" onkeypress="clearSearch();">
                <option selected="selected" value="">All</option>
                <option value="KS1">Key stage 1</option>
                <option value="KS2">Key stage 2</option>
                <option value="KS3">Key stage 3</option>
                <option value="KS4">Key stage 4</option>
                <option value="KS4+">Key stage 4+</option>
                <option value="KSFS">Foundation Stage</option>
          </select>
</div>
          <br/>
<div class="head">
          <label for="SchoolYear">School Year: </label>
</div>
<div class="body">
<select name="SchoolYear" id="SchoolYear" onclick="clearSearch();" onkeypress="clearSearch();">
                <option selected="selected" value="">All</option>
                <option value="YN">Nursery</option>
                <option value="YR">Reception</option>
                <option value="Y1">Year 1</option>
                <option value="Y2">Year 2</option>
                <option value="Y3">Year 3</option>
                <option value="Y4">Year 4</option>
                <option value="Y5">Year 5</option>
                <option value="Y6">Year 6</option>
                <option value="Y7">Year 7</option>
                <option value="Y8">Year 8</option>
                <option value="Y9">Year 9</option>
                <option value="Y10">Year 10</option>
                <option value="Y11">Year 11</option>
          </select>
</div>
          <br/>
<div class="head">
          <label for="AggregationLevel">Aggregation Level: </label>
</div>
<div class="body">
<select name="AggregationLevel" id="AggregationLevel" onclick="clearSearch();" onkeypress="clearSearch();">
                <option selected="selected" value="">All</option>
                <option value="1">Covers a single lesson or topic</option>
                <option value="2">Covers an entire unit or a small set of units</option>
                <option value="3">Covers one or more academic years</option>
                <option value="4">A curriculum wide resource covering multiple subjects and keystages</option>
          </select>
</div>
          <br/>
<div class="head">
          <label for="searchText">Search Text: </label>
</div>
<div class="body">
<input type="text" value="Enter search text here" onclick="clearSearch();" onkeypress="clearSearch();" name="searchText" id="searchText" size="40"/>
</div>
<br/>
<div class="head">
     <input class="button" type="image" alt="Search" src="<%=base_dir%>/images/search_button.gif"/>
</div>
<div class="body">
      <a href="<%=base_dir%>/searchHelp">Search Help</a>&nbsp;&nbsp;&nbsp;<span class="accesstext">&nbsp;|&nbsp;</span>&nbsp;&nbsp;&nbsp;<a href="<%=base_dir%>">Simple Search</a><br/>
</div>
      </form>
</div>
<div class="clearing">&#160;</div>
