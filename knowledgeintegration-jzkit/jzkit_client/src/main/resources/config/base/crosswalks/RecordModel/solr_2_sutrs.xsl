<?xml version="1.0"?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/doc">
<string><xsl:value-of select="./str[@name='title']"/>
<xsl:value-of select="./str[@name='description']"/>
http://www3.interscience.wiley.com/journal/<xsl:value-of select="./str[@name='doi']"/>/abstract
<xsl:apply-templates match="./arr[@name='creator']/str"/>
</string>
</xsl:template>

  <xsl:template match="arr[@name='creator']/str">
    <xsl:value-of select="."/>
  </xsl:template>

</xsl:stylesheet>
