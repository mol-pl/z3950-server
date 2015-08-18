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
<record xmlns="http://www.loc.gov/MARC21/slim">
  <leader>00726nam a2200241   4500</leader>
  <controlfield tag="001">4073417</controlfield>
  <controlfield tag="005">19721212000000.0</controlfield>
  <controlfield tag="008">721207s1972    nyua     b    001 0 eng  </controlfield>
  <datafield tag="035" ind1=" " ind2=" ">
    <subfield code="9">(DLC)   70186783</subfield>
  </datafield>
  <datafield tag="906" ind1=" " ind2=" ">
    <subfield code="a">7</subfield>
    <subfield code="b">cbc</subfield>
    <subfield code="c">orignew</subfield>
    <subfield code="d">u</subfield>
    <subfield code="e">ncip</subfield>
    <subfield code="f">19</subfield>
    <subfield code="g">y-gencatlg</subfield>
  </datafield>
  <datafield tag="010" ind1=" " ind2=" ">
    <subfield code="a">   70186783 </subfield>
  </datafield>
  <datafield tag="040" ind1=" " ind2=" ">
    <subfield code="a">DLC</subfield>
    <subfield code="c">DLC</subfield>
    <subfield code="d">DLC</subfield>
  </datafield>
  <datafield tag="050" ind1="0" ind2="0">
    <subfield code="a">HD38</subfield>
    <subfield code="b">.B363 1972</subfield>
  </datafield>
  <datafield tag="082" ind1="0" ind2="0">
    <subfield code="a">658.4</subfield>
  </datafield>
  <datafield tag="100" ind1="1" ind2=" ">
    <subfield code="a">Beer, Stafford.</subfield>
  </datafield>
  <datafield tag="245" ind1="1" ind2="0">
    <subfield code="a">Brain of the firm;</subfield>
    <subfield code="b">a development in management cybernetics.</subfield>
  </datafield>
  <datafield tag="260" ind1=" " ind2=" ">
    <subfield code="a">[New York]</subfield>
    <subfield code="b">Herder and Herder</subfield>
    <subfield code="c">[1972]</subfield>
  </datafield>
  <datafield tag="300" ind1=" " ind2=" ">
    <subfield code="a">319 p.</subfield>
    <subfield code="b">illus.</subfield>
    <subfield code="c">22 cm.</subfield>
  </datafield>
  <datafield tag="350" ind1=" " ind2=" ">
    <subfield code="a">$9.50</subfield>
  </datafield>
  <datafield tag="504" ind1=" " ind2=" ">
    <subfield code="a">Bibliography: p. 308-[314]</subfield>
  </datafield>
  <datafield tag="650" ind1=" " ind2="0">
    <subfield code="a">Cybernetics.</subfield>
  </datafield>
  <datafield tag="650" ind1=" " ind2="0">
    <subfield code="a">Industrial management.</subfield>
  </datafield>
  <datafield tag="991" ind1=" " ind2=" ">
    <subfield code="b">c-GenColl</subfield>
    <subfield code="h">HD38</subfield>
    <subfield code="i">.B363 1972</subfield>
    <subfield code="t">Copy 1</subfield>
    <subfield code="w">BOOKS</subfield>
  </datafield>
</record>
  </xsl:template>

</xsl:stylesheet>
