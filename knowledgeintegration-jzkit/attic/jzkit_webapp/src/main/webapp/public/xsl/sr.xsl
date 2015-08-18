<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:srw="http://www.loc.gov/zing/srw/"
                xmlns:jzkit="http://www.k-int.com/jzkit/"
                xmlns:lom="http://www.imsglobal.org/xsd/imsmd_v1p2"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:owl="http://www.w3.org/2002/07/owl#"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://www.imsglobal.org/xsd/imsmd_v1p2 schema/colv1.1.xsd"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl srw jzkit lom dcterms owl rdf xsi"
>

<xsl:output method="html" />

<xsl:template match="/">

<xsl:text disable-output-escaping="yes">
<![CDATA[
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
]]>
</xsl:text>

  <html lang="en-gb">
    <head>
      <title>Curriculum Online</title>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
      <meta name="author" content="Department for Education and Skills "/>
      <meta name="description" content="Curriculum Online offers an online catalogue of digital learning resources, searchable by Key Stage, subject and topic. This portal - aimed at teachers and other school staff, and developed by the Department for Education and Skills - also offers independent evaluations and teacher reviews of products. "/>
      <meta name="keywords" content="Curriculum Online COL digital resources showcase catalogue teachers teaching assistants classroom ICT learning education national curriculum school management standards DfES performance threshold assessments England"/>
      <meta name="DC.Title" content="DfES - Curriculum Online"/>
      <meta name="DC.AlternativeTitle" content="none"/>
      <meta name="DC.Audience" content="teachers"/>
      <meta name="DC.Contributor" content="none"/>
      <meta name="DC.Coverage.Spatial" content="United Kingdom"/>
      <meta name="DC.Creator" content="Curriculum Online, ICT in Schools Division, Department for Education &amp; Skills"/>
      <meta name="DC.Date.Created" content="2006-10-05"/>
      <meta name="DC.Date.Modified" content="2006-10-05"/>
      <meta name="DC.Description" content="Curriculum Online is a one-stop online catalogue of digital learning resources.  Resources on this site include: lesson plans, CD-ROMs, interactive videos, simulation software, assessment materials, online services . organised by the National Curriculum programmes of study, QCA schemes of work and topics.  Curriculum Online provides a definitive catalogue of eLC eligible products.  Electronic learning credits (eLCs) are a new form of funding made available to maintained primary and secondary schools, non maintained special schools and pupil referral units in England."/>
      <meta name="DC.Subject" content="Electronic learning credits (eLCs), Digital learning resources for use in schools, Lesson plans, CD-ROMs, Interactive videos, Simulation software, Assessment materials, Online services"/>
      <meta name="DC.Subject.Category" content="none"/>
      <meta name="DC.Subject.Keyword" content="none"/>
      <meta name="DC.Disposal.Action" content="none"/>
      <meta name="DC.Format.Medium" content="text/html"/>
      <meta name="DC.Identifier" content="www.curriculumonline.gov.uk"/>
      <meta name="DC.Language" content="eng"/>
      <meta name="DC.Preservation" content="none"/>
      <meta name="DC.Publisher" content="Department for Education and Skills e-Communication and Internet Unit"/>
      <meta name="DC.Relation.IsPartOf" content="www.curriculumonline.gov.uk"/>
      <meta name="DC.Relation.IsVersionOf" content="none"/>
      <meta name="DC.Rights.BusinessGroupAccessPermission" content="public"/>
      <meta name="DC.Rights.Copyright" content="Department for Education and Skills www.curriculumonline.gov.uk/Curriculum+Online/Popup/termsandconditions.htm"/>
      <meta name="DC.Rights.SecurityClassification" content="unclassified"/>
      <meta name="DC.Type" content="Web Page"/>
<!--
      <link rel="shortcut icon" href="/webservices/images/favicon.ico" type="image/x-icon"/>
      <link rel="icon" href="/webservices/images/favicon.ico" type="image/x-icon"/>
-->

      <link type="text/css" rel="stylesheet" href="/webservices/css/pub.css" media="screen"/>
      <xsl:text disable-output-escaping="yes"><![CDATA[
        <!--[if lt IE 7]> <link href="/webservices/css/ie_only.css" rel="stylesheet" type="text/css" media="screen"/> <![endif]-->
        <!--[if IE 7]> <link href="/webservices/css/ie7_only.css" rel="stylesheet" type="text/css" media="screen"/> <![endif]-->
      ]]>
      </xsl:text>
    </head>

    <body id="srbody">
<center>
      <div id="main" style="clear:both">
        <div class="corner" id="topleft">&#160;</div><div class="corner" id="topright">&#160;</div>
      <div class="panel">&#160;</div>
        <div id="head">
          <ul class="iconlinks">
            <li class="help"><a href="/webservices/help">Help</a></li>
            <li class="contact"><a href="/webservices/contact">Contact</a></li>
            <li class="wishlist"><a href="http://curriculumonline.gov.uk/WishList/WishList.htm">Wishlist</a></li>
          </ul>

          <img height="56" width="241" src="/webservices/images/logo_small.gif" title="Curriculum Online logo" alt="Curriculum Online logo"/>
          <div id="srlinks">
            | <a href="/webservices">Home</a> | <a href="/webservices/abouteLearningCredits">About eLearning Credits</a> | <a href="http://curriculumonline.gov.uk/NewsArchive/Archive.htm">News</a> | <a href="/webservices/about">About</a> | <a href="http://curriculumonline.gov.uk/LoginRegister/Login.htm">Login</a> | <a href="http://curriculumonline.gov.uk/LoginRegister/Register.htm">Register</a> | <a href="/webservices/supplier">Supplier Search</a> | <hr/>
          </div>
          <div class="accesstext">Curriculum Online. www.curriculumonline.gov.uk</div>
        <div class="clearing">&#160;</div>
        </div>

        <div id="content">
          <xsl:apply-templates select="/srw:diagnostics"/>
          <xsl:apply-templates select="/srw:searchRetrieveResponse"/>
        </div>

      <div id="links">
          | <a href="/webservices">Home</a> | <a href="/webservices/abouteLearningCredits">About eLearning Credits</a> | <!--<a href="/webservices/news">--><a href="http://curriculumonline.gov.uk/NewsArchive/Archive.htm">News</a> | <a href="/webservices/about">About Curriculum Online</a> | <a href="http://curriculumonline.gov.uk/LoginRegister/Login.htm">Login</a> | <a href="http://curriculumonline.gov.uk/LoginRegister/Register.htm">Register</a> | <a href="/webservices/supplier">Supplier Search</a> |
        <div class="clearing">&#160;</div>
      </div>
      <div id="foot">
        <img height="61" width="160" class="floatleft" src="/webservices/images/becta.gif" title="Curriculum Online is managed by Becta." alt="Curriculum Online is managed by Becta."/>
        <img height="56" width="256" class="floatleft" src="/webservices/images/dfes.gif" title="Curriculum Online is supported by the Department for Education and Skills. Creating oppertunity, releasing potential, achieving excellence." alt="Curriculum Online is supported by the Department for Education and Skills. Creating oppertunity, releasing potential, achieving excellence."/>
          <ul class="iconlinks">
            <li class="help"><a href="/webservices/help">Help</a></li>
            <li class="contact"><a href="/webservices/contact">Contact</a></li>
            <li class="wishlist"><a href="http://curriculumonline.gov.uk/WishList/WishList.htm">Wishlist</a></li>
          </ul>
        <div class="clearing">&#160;</div>
      </div>
     <div class="corner" id="bottomleft">&#160;</div><div class="corner" id="bottomright">&#160;</div><div class="panel">&#160;</div>
     </div>

</center>
    </body>
  </html>
</xsl:template>

<xsl:template match="/srw:searchRetrieveResponse">
  <xsl:variable name="num_results">
    <xsl:value-of select="/srw:searchRetrieveResponse/srw:numberOfRecords"/>
  </xsl:variable>
  <xsl:variable name="record_no">
    <xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:startRecord"/>
  </xsl:variable>
  <xsl:variable name="last_page_rec_no">
    <xsl:value-of select="$num_results - 10"/>
  </xsl:variable>
  <xsl:variable name="next_rec_pos">
    <xsl:value-of select="/srw:searchRetrieveResponse/srw:nextRecordPosition"/>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="$num_results &gt; 0">
    <div class="floatright">
      <xsl:if test="$record_no &gt; 10">
        <a><xsl:attribute name="href">/webservices/search?landscape=Products&amp;operation=SearchRetrieve&amp;query=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:query"/>&amp;startRecord=<xsl:value-of select="$record_no - 10"/>&amp;maximumRecords=10&amp;recordSchema=learningobject&amp;stylesheet=/webservices/public/xsl/sr.xsl</xsl:attribute>&lt;&lt;&#160;Prev&#160;</a>
      </xsl:if>
      Showing Learning Resources 
      <b><xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:startRecord"/> to 
      <xsl:value-of select="/srw:searchRetrieveResponse/srw:extraResponseData/jzkit:lastRecord"/></b>
      of <b><xsl:value-of select="/srw:searchRetrieveResponse/srw:numberOfRecords"/></b>
      <xsl:if test="$record_no &lt; $last_page_rec_no">
        <a><xsl:attribute name="href">/webservices/search?landscape=Products&amp;operation=SearchRetrieve&amp;query=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:query"/>&amp;startRecord=<xsl:value-of select="$next_rec_pos"/>&amp;maximumRecords=10&amp;recordSchema=learningobject&amp;stylesheet=/webservices/public/xsl/sr.xsl</xsl:attribute>&#160;Next&#160;&gt;&gt;</a>
      </xsl:if>
    </div>
<!-- this nbsp is to stop ie shrinking margins when its told not to, do not remove -->
    &#160;
    <div class="clearing">&#160;</div>
    <div id="smallsearch">

    <form action="/webservices/searchFormHandler" method="post">
      <label for="searchText"><span class="accesstext">Search</span></label>
      <input type="text" id="searchText" name="searchText" size="40">
        <xsl:attribute name="value">
           <xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:query/"/>
        </xsl:attribute>
      </input>
      <input class="button" type="image" alt="Search" src="/webservices/images/search_button_small.gif"/>
    </form>
    </div>
    <div class="clearing">&#160;</div>

    
    <ul id="results"><xsl:apply-templates select="./srw:records/srw:record"/></ul>
    <div class="floatright">
      <xsl:if test="$record_no &gt; 10">
        <a><xsl:attribute name="href">/webservices/search?landscape=Products&amp;operation=SearchRetrieve&amp;query=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:query"/>&amp;startRecord=<xsl:value-of select="$record_no - 10"/>&amp;maximumRecords=10&amp;recordSchema=learningobject&amp;stylesheet=/webservices/public/xsl/sr.xsl</xsl:attribute>&lt;&lt;&#160;Prev&#160;</a>
      </xsl:if>
      Showing Learning Resources
      <b><xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:startRecord"/> to
      <xsl:value-of select="/srw:searchRetrieveResponse/srw:extraResponseData/jzkit:lastRecord"/></b>
      of <b><xsl:value-of select="/srw:searchRetrieveResponse/srw:numberOfRecords"/></b>
      <xsl:if test="$record_no &lt; $last_page_rec_no">
        <a><xsl:attribute name="href">/webservices/search?landscape=Products&amp;operation=SearchRetrieve&amp;query=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:query"/>&amp;startRecord=<xsl:value-of select="$next_rec_pos"/>&amp;maximumRecords=10&amp;recordSchema=learningobject&amp;stylesheet=/webservices/public/xsl/sr.xsl</xsl:attribute>&#160;Next&#160;&gt;&gt;</a>
      </xsl:if>
    </div>

    <div class="clearing">&#160;</div>
  </xsl:when>
  <xsl:otherwise>
    <div id="smallsearch">
      <form action="/webservices/searchFormHandler" method="post">
        <label for="searchText"><span class="accesstext">Search</span></label>
        <input type="text" id="searchText" name="searchText" size="40">
          <xsl:attribute name="value">
            <xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:query/"/>
          </xsl:attribute>
        </input>
        <input class="button" type="image" alt="Search" src="/webservices/images/search_button_small.gif"/>
      </form>
      <h2>Your search returned no results.</h2>
      <p>Try altering your query and searching again with the form above.</p>
    </div>
    <div class="clearing">&#160;</div>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
      <xsl:call-template name="page_list">
        <xsl:with-param name="selected_page"><xsl:value-of select="$pageno"/></xsl:with-param>
        <xsl:with-param name="current_page">1</xsl:with-param>
        <xsl:with-param name="num_pages"><xsl:value-of select="$num_pages"/></xsl:with-param>
      </xsl:call-template>

    <xsl:if test="$num_pages = 0">
      Num pages is 0 - no search results
    </xsl:if>
</div>
<xsl:template name="page_list">
  <xsl:param name="selected_page"/>
  <xsl:param name="current_page"/>
  <xsl:param name="num_pages"/>

  <xsl:choose>
    <xsl:when test="$current_page > $num_pages">

    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="$current_page = $selected_page">
          <b><xsl:value-of select="$current_page"/></b>
        </xsl:when>
        <xsl:otherwise>
          <a><xsl:value-of select="$current_page"/></a>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="page_list">
        <xsl:with-param name="selected_page"><xsl:value-of select="$selected_page"/></xsl:with-param>
        <xsl:with-param name="current_page"><xsl:value-of select="$current_page + 1"/></xsl:with-param>
        <xsl:with-param name="num_pages"><xsl:value-of select="$num_pages"/></xsl:with-param>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
-->


<xsl:template match="srw:record">
  <xsl:apply-templates select="./learningobject"/>
</xsl:template>

<xsl:template match="learningobject">
<li>
<div class="clearing">&#160;</div>
  <div class="icons">
    <xsl:if test="./isevaluated/text()=1">
      <img src="/webservices/images/icon_evaluated.gif" title="This resource has been evaluated." alt="This resource has been evaluated." height="14" width="14"/>
    </xsl:if>
    <xsl:if test="./isreviewed/text()=1">
      <img src="/webservices/images/icon_reviewed.gif" title="This resource has been reviewed." alt="This resource has been reviewed." height="14" width="14"/>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="./priced/text()=1">
        <img src="/webservices/images/icon_priced.gif" title="This resource has been priced." alt="This resource has been priced." height="14" width="14"/>
      </xsl:when>
      <xsl:otherwise>
        <img src="/webservices/images/icon_notpriced.gif" title="This resource has not been priced." alt="This resource has not been priced." height="14" width="14"/>
      </xsl:otherwise>
    </xsl:choose>
  </div>
  <a><xsl:attribute name="href">/webservices/product/<xsl:value-of select="./identifier/text()"/></xsl:attribute><xsl:value-of select="./title/text()"/></a>
  <a target="_new">
    <xsl:attribute name="href">/webservices/product/<xsl:value-of select="./identifier/text()"/></xsl:attribute>
    <img src="/webservices/images/new.gif" class="new" alt="Open this link in a new window"  title="Open this link in a new window" height="10" width="10"/>
  </a>
   <br/>
    <xsl:value-of disable-output-escaping="yes" select="./description/text()"/>
  <br/>

  <xsl:if test="./suppliername/text() != ''">
    Supplied By <a><xsl:attribute name="href"><xsl:value-of select="./website"/></xsl:attribute><xsl:value-of select="./suppliername"/></a>.
  </xsl:if>
  <div class="clearing">&#160;</div>
</li>

</xsl:template>

  <xsl:template match="/srw:diagnostics">
    <b>Diagnostics</b>
  </xsl:template>
</xsl:stylesheet>
