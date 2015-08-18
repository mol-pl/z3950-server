<?xml version="1.0"?>
 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                              xmlns:idzebra="http://www.indexdata.dk/zebra/">

<xsl:param name="source">no</xsl:param>
<xsl:param name="image_html">no</xsl:param>

<xsl:output method="xml"/>

<xsl:template match="/Resource">
        <Resource>A Resource</Resource>
</xsl:template>

</xsl:stylesheet>
