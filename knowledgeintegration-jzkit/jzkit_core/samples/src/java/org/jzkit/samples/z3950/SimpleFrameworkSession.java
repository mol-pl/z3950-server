package org.jzkit.samples.z3950;

public class SimpleFrameworkSession
{
  /**
   *  Sample code for using the JZKit Framework to create a simple stand alone
   *  Z39.50 association. No use of the collection description service to retrieve
   *  the target description.
   */
  public SimpleFrameworkSession()
  {
    RecordFormatSpecification brief_usmarc = new RecordFormatSpecification( "usmarc", null, "b" );

    Z3950TargetDescription assoc_factory = new Z3950TargetDescription();
    assoc_factory.setHost("z3950.loc.gov");
    assoc_factory.setPort(7090);
    
    Searchable z3950_assoc = assoc_factory.newSearchable();

    IRResultSet result_set = z3950_assoc.evaluate(new IRQuery(new PrefixString("@attrset bib-1 @attr 1=4 science"),"Voyager");
    
    DefaultSourceEnumeration dse = new DefaultSourceEnumeration(result_set, brief_usmarc);

    for ( ; des.hasMoreElements(); )
    {
      InformationFragment next = (InformationFragment)des.next();
      System.err.println("Record...");
    }

    result_set.close();
    z3950_assoc.close();
  }
}
