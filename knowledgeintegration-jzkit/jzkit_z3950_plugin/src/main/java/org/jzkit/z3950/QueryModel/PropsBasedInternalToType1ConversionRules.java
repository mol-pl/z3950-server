package org.jzkit.z3950.QueryModel;

import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import java.util.Properties;
import java.net.URL;
import java.io.InputStream;

public class PropsBasedInternalToType1ConversionRules implements InternalToType1ConversionRules {
  private Properties properties = new Properties();

  public PropsBasedInternalToType1ConversionRules(String props_file_resource) {
    try {
      InputStream is = PropsBasedInternalToType1ConversionRules.class.getResourceAsStream(props_file_resource);
      properties.load(is);
    }
    catch ( java.io.IOException ioe ) {
      ioe.printStackTrace();
      throw new RuntimeException(ioe.toString());
    }
  }

  // Convert a generic internal AttrValue for example dc.title, k-int.truncation.right
  public Z3950AttrTriple convert(Object av, String default_source_namespace) {
    String lookup_str = null;

    if ( av instanceof AttrValue ) {
      String ns_identifier = ((AttrValue)av).getNamespaceIdentifier();
      if ( ( ns_identifier == null ) || ( ns_identifier.length() == 0 ) )
        lookup_str = ns_identifier+"."+av.toString();
      else
        lookup_str = av.toString();
    }
    else
      lookup_str = av.toString();

    // If there is no conversion triple available, just try and do a pass-through
    if ( lookup_str != null ) {
      String target_triple = properties.getProperty(lookup_str);
      if ( target_triple != null ) {
        return new Z3950AttrTriple(target_triple);
      }
      else {
        return new Z3950AttrTriple(lookup_str);
      }
    }
    else
      throw new RuntimeException("Asked to convert null attr value");
  }

  public String getProperty(String property) {
    return properties.getProperty(property);
  }
}
