package org.jzkit.z3950.QueryModel;
                                                                                                                                          
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import org.springframework.context.ApplicationContext;

public abstract class Z3950QueryModel implements QueryModel
{
  public abstract Query_type toASNType();
  public abstract InternalModelRootNode toInternalQueryModel(ApplicationContext ctx) throws InvalidQueryException;
}
