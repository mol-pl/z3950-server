<?xml version="1.0"?>

<xsl:stylesheet version="2.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:marc="http://www.loc.gov/MARC21/slim"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-NS#">

  <xsl:template match="/">
<doc>
  A transformed SOLR record
  <arr name="cat"><str>electronics</str><str>monitor</str></arr>
  <arr name="features"><str>30" TFT active matrix LCD, 2560 x 1600, .25mm dot pitch, 700:1 contrast</str></arr>
  <str name="id">3007WFP</str>
  <bool name="inStock">true</bool>
  <str name="includes">USB cable</str>
  <str name="manu">Dell, Inc.</str>
  <str name="name">Dell Widescreen UltraSharp 3007WFP</str>
  <int name="popularity">6</int>
  <float name="price">2199.0</float>
  <str name="sku">3007WFP</str>
  <date name="timestamp">2008-03-11T09:38:15.925Z</date>
  <float name="weight">401.6</float>
</doc>
  </xsl:template>

</xsl:stylesheet>
