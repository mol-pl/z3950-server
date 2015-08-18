<srw:explainResponse xmlns:srw=\"http://www.loc.gov/zing/srw/\" xmlns:zr=\"http://explain.z3950.org/dtd/2.0/\">
  <srw:version>1.1</srw:version>
  <srw:record>
  <srw:recordPacking>XML</srw:recordPacking>
  <srw:recordSchema>http://explain.z3950.org/dtd/2.0/</srw:recordSchema>
  <srw:recordData>
      <zr:explain>
        <zr:serverInfo wsdl=\"http://myserver.com/db\" protocol=\"SRU\" version=\"1.1\">
          <host></host>
          <port>80</port>
          <database>sru</database>
        </zr:serverInfo>
        <zr:databaseInfo>
           <title lang="en" primary="true">SRU Test Database</title>
           <description lang="en" primary="true"> My server SRU Test Database</description>
        </zr:databaseInfo>
      </zr:explain>
    </srw:recordData>
  </srw:record>
</srw:explainResponse>
