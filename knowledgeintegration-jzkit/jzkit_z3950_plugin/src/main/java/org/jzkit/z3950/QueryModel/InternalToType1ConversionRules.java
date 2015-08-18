package org.jzkit.z3950.QueryModel;

import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import java.util.Properties;

public interface InternalToType1ConversionRules
{
  // Convert a generic internal Attr Value for example dc.title, k-int.truncation.right
  public Z3950AttrTriple convert(Object av, String default_source_tree_namespace);
  public String getProperty(String property);
}
