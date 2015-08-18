package org.jzkit.search.util.QueryConversion;

import org.jzkit.search.util.QueryModel.Internal.*;

public interface QueryConverter
{
  /**
   * Take the input query model and create a new query model which conforms to the target profile definition.
   * If the input model is already conformat, a copy will be returned.
   */
  public InternalModelRootNode convert(InternalModelRootNode source_query, String target_profile) throws QueryConverterException;

  /**
   * Test to see if the query model is conformant to the target profile.
   */
  public boolean isConformant(InternalModelRootNode source_query, String target_profile);

}
