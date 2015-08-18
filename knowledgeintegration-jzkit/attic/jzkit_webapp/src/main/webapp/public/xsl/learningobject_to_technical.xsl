<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template match="/">
  <h3>technical</h3>
  <xsl:apply-templates select="LearningObject"/>
</xsl:template>

<xsl:template match="LearningObject">
  Title: <xsl:value-of select="./Title"/>
</xsl:template>

</xsl:stylesheet>

