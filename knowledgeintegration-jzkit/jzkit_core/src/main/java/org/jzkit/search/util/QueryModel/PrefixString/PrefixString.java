package org.jzkit.search.util.QueryModel.PrefixString;

import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.InternalModelRootNode;
import java.io.StringReader;
import org.springframework.context.ApplicationContext;

public class PrefixString implements QueryModel, java.io.Serializable 
{
  private String the_prefix_string;
  private InternalModelRootNode internal_model = null;

  public PrefixString(String the_prefix_string) {
    this.the_prefix_string = the_prefix_string;
  }

  public InternalModelRootNode toInternalQueryModel(ApplicationContext ctx) throws InvalidQueryException {
    if ( the_prefix_string == null ) 
      throw new InvalidQueryException("Null prefix string");

    try {
      if ( internal_model == null ) {
        PrefixQueryParser pqs = new PrefixQueryParser( new StringReader(the_prefix_string));
        internal_model = pqs.parse();
      }
    }
    catch ( org.jzkit.search.util.QueryModel.PrefixString.PrefixQueryException pqe ) {
      throw new InvalidQueryException(pqe.toString());
    }

    return internal_model;
  }
}
