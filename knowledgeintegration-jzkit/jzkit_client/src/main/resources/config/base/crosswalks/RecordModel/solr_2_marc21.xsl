<?xml version="1.0"?>

<xsl:stylesheet version="2.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:marc="http://www.loc.gov/MARC21/slim"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-NS#">

  <xsl:template match="/doc">
    <record xmlns="http://www.loc.gov/MARC21/slim">
      <datafield tag="022" ind1=" " ind2=" ">
        <subfield code="a"><xsl:value-of select="./str[@name='issn']"/></subfield>
      </datafield>
      <datafield tag="245" ind1=" " ind2=" ">
        <subfield code="a"><xsl:value-of select="./str[@name='title']"/></subfield>
      </datafield>
      <datafield tag="260" ind1=" " ind2=" ">
        <subfield code="b"><xsl:value-of select="./str[@name='jtl']"/></subfield>
        <subfield code="c"><xsl:value-of select="./str[@name='idate']"/></subfield>
      </datafield>
      <datafield tag="362" ind1=" " ind2=" ">
        <subfield code="a">Volume: <xsl:value-of select="./str[@name='volume']"/>, Issue <xsl:value-of select="./str[@name='issue']"/></subfield>
      </datafield>
      <datafield tag="505" ind1=" " ind2=" ">
        <subfield code="a"><xsl:value-of select="./str[@name='pages']"/></subfield>
      </datafield>
      <datafield tag="520" ind1=" " ind2=" ">
        <subfield code="a"><xsl:value-of select="./str[@name='description']"/></subfield>
      </datafield>
      <datafield tag="856" ind1=" " ind2=" ">
        <subfield code="u">http://www3.interscience.wiley.com/journal/<xsl:value-of select="./str[@name='doi']"/>/abstract</subfield>
      </datafield>
      <xsl:apply-templates match="./arr[@name='creator']/str"/>
    </record>
  </xsl:template>

  <xsl:template match="arr[@name='creator']/str">
    <datafield tag="700" ind1=" " ind2=" ">
      <subfield code="a"><xsl:value-of select="."/></subfield>
    </datafield>
  </xsl:template>

</xsl:stylesheet>
