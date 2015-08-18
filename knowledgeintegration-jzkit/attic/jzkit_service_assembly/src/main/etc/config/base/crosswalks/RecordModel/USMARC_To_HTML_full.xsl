<?xml version="1.0"?>
 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:dc="http://purl.org/dc/elements/1.1/"
                              xmlns:marc="http://www.loc.gov/MARC21/slim"
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<xsl:output method="html"/>

<xsl:template match="/">
        RECORD
	<xsl:apply-templates select="marc:collection/marc:record"/>
</xsl:template>

<xsl:template match="marc:record">
	<table>
	<xsl:apply-templates select="marc:datafield[@tag=35]"/>
	<xsl:apply-templates select="marc:datafield[@tag=37]"/>
	<xsl:apply-templates select="marc:datafield[@tag=40]"/>
	<xsl:apply-templates select="marc:datafield[@tag=245]"/>
	<xsl:apply-templates select="marc:datafield[@tag=246]"/>
	<xsl:apply-templates select="marc:datafield[@tag=300]"/>
	<xsl:apply-templates select="marc:datafield[@tag=321]"/>
	<xsl:apply-templates select="marc:datafield[@tag=362]"/>
	<xsl:apply-templates select="marc:datafield[@tag=506]"/>
	<xsl:apply-templates select="marc:datafield[@tag=513]"/>
	<xsl:apply-templates select="marc:datafield[@tag=520]"/>
	<xsl:apply-templates select="marc:datafield[@tag=555]"/>
	<xsl:apply-templates select="marc:datafield[@tag=650]"/>
	<xsl:apply-templates select="marc:datafield[@tag=785]"/>
	</table>
</xsl:template>

<xsl:template match="marc:datafield[@tag=35]">
	<tr><td align="right" nowrap="true" valign="top">System control number : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="marc:datafield[@tag=37]">
	<tr><td align="right" nowrap="true" valign="top">Source of Acquisition : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="marc:datafield[@tag=40]">
	<tr><td align="right" nowrap="true" valign="top">Cataloging Source : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="marc:datafield[@tag=245]">
	<tr><td align="right" nowrap="true" valign="top">Title : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="marc:datafield[@tag=246]">
	<tr><td align="right" nowrap="true" valign="top">Subtitle : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="marc:datafield[@tag=300]">
	<tr>
		<td align="right" nowrap="true" valign="top">Pub info : </td> 
		<td>
			<xsl:value-of select="."/>
		</td>
	</tr>
</xsl:template>

<xsl:template match="marc:datafield[@tag=321]">
	<tr><td align="right" nowrap="true" valign="top">321 : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="marc:datafield[@tag=362]">
	<tr><td align="right" nowrap="true" valign="top">362 : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="marc:datafield[@tag=506]">
	<tr><td align="right" nowrap="true" valign="top">Restrictions on Access : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="marc:datafield[@tag=513]">
	<tr><td align="right" nowrap="true" valign="top">Type of report and Period Covered : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="marc:datafield[@tag=520]">
	<tr><td align="right" nowrap="true" valign="top">Summary : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="marc:datafield[@tag=555]">
	<tr><td align="right" nowrap="true" valign="top">Summary : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="marc:datafield[@tag=650]">
	<tr><td align="right" nowrap="true" valign="top">Subject : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="marc:datafield[@tag=785]">
	<tr><td align="right" nowrap="true" valign="top">Lookup 785 : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>
</xsl:stylesheet>
