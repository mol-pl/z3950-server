<?xml version="1.0"?>

<!--
Sample search response
<srw:searchRetrieveResponse xmlns:srw="http://www.loc.gov/zing/srw/" >
  <srw:version>1.1</srw:version>
  <srw:numberOfRecords>2</srw:numberOfRecords>
  <srw:records>
  <srw:record><lom xmlns="http://www.imsglobal.org/xsd/imsmd_v1p2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:owl="http://www.w3.org/2002/07/owl#" xmlns:dcterms="http://purl.org/dc/terms/" xsi:schemaLocation="http://www.imsglobal.org/xsd/imsmd_v1p2 schema/colv1.1.xsd"><title xmlns=""><langstring xml:lang="en">ICT in Art and Design</langstring></title><title xmlns=""><langstring xml:lang="en">ICT in Art and Design</langstring></title><description xmlns=""><langstring xml:lang="en">An ideal starting point for any Art and Design department wanting to develop its ICT policy. It includes guidance on key issues for the teacher and a selection of handy forms and checklists.</langstring></description><ResourceType xmlns=""><langstring xml:lang="en">Guide</langstring></ResourceType><EndUser xmlns=""><langstring xml:lang="en">Learner, Teacher</langstring></EndUser><SupplierId xmlns=""><langstring xml:lang="en">SUP_PPUB</langstring></SupplierId><SupplierName xmlns=""><langstring xml:lang="en">Pearson Publishing Group</langstring></SupplierName><TypicalAgeRange xmlns=""><langstring xml:lang="en">11-15</langstring></TypicalAgeRange></lom></srw:record>

  <srw:record><lom xmlns="http://www.imsglobal.org/xsd/imsmd_v1p2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:owl="http://www.w3.org/2002/07/owl#" xmlns:dcterms="http://purl.org/dc/terms/" xsi:schemaLocation="http://www.imsglobal.org/xsd/imsmd_v1p2 schema/colv1.1.xsd"><title xmlns=""><langstring xml:lang="en">Applying ICT to Art</langstring></title><title xmlns=""><langstring xml:lang="en">Applying ICT to Art</langstring></title><description xmlns=""><langstring xml:lang="en">Non-ICT specialists deliver ICT in the context of Art, meeting the NC PoS guidelines in both Art and ICT through stand-alone  worksheets. There are 10 worksheets for KS3 and 10 for KS4, and the pack now includes extra worksheets on using the Internet...</langstring></description><ResourceType xmlns=""><langstring xml:lang="en">Open Activity</langstring></ResourceType><SupplierId xmlns=""><langstring xml:lang="en">SUP_CHAL</langstring></SupplierId><SupplierName xmlns=""><langstring xml:lang="en">Chalkface Project Ltd</langstring></SupplierName><TypicalAgeRange xmlns=""><langstring xml:lang="en">11-15</langstring></TypicalAgeRange></lom></srw:record>
  </srw:records>
</srw:searchRetrieveResponse>
-->
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:srw="http://www.loc.gov/zing/srw/"
                xmlns:lom="http://www.imsglobal.org/xsd/imsmd_v1p2"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:owl="http://www.w3.org/2002/07/owl#" 
                xmlns:dcterms="http://purl.org/dc/terms/">
<xsl:output method="html"/>

<xsl:template match="/">
  <html>
    <head>
      <link rel="stylesheet" type="text/css" href="styles.css"/>
    </head>
    <body>
      <div id="topheader">
        <span id="version">Version 0.92.0</span>
        <span id="title">BECTA/COL Web Services</span>
      </div>
      <p>
        Process root element, selecting searchRetrieveResponse
        <xsl:apply-templates select="/srw:searchRetrieveResponse"/>
      </p>
    </body>
  </html>
</xsl:template>

<xsl:template match="srw:searchRetrieveResponse">
  <p>
    Process searchRetrieveResponse, processing records
    <xsl:apply-templates select="./srw:records"/>
  </p>
</xsl:template>

<xsl:template match="srw:records">
  <p>
    Process records, processing record
    <xsl:apply-templates select="./srw:record"/>
  </p>
</xsl:template>

<xsl:template match="srw:record">
  <p>
    Process record, selecting all children
    <xsl:apply-templates select="./lom:lom"/>
  </p>
</xsl:template>

<xsl:template match="lom:lom">
  Processing lom
  Lom Title : <xsl:apply-templates select="title"/>
</xsl:template>

</xsl:stylesheet>

