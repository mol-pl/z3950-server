package org.jzkit.search.util.QueryConversion;

import java.util.prefs.Preferences;
import java.lang.reflect.Constructor;
import java.util.logging.*;

import org.jzkit.search.util.QueryModel.Internal.*;

public class QueryConverterImpl implements QueryConverter
{
  private QueryConverterImpl() 
  {
  }

  public QueryConverterImpl(Preferences prefs)
  {
  }

  /**
   * Take the input query model and create a new query model which conforms to the target profile definition.
   * If the input model is already conformat, a copy will be returned.
   */
  public InternalModelRootNode convert(InternalModelRootNode source_query, String target_profile) throws QueryConverterException
  {
    return null;
  }

  /**
   * Test to see if the query model is conformant to the target profile.
   */
  public boolean isConformant(InternalModelRootNode source_query, String target_profile)
  {
    return false;
  }

}
