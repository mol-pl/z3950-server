package org.jzkit.search.util.Profile;

import java.util.*;
import org.jzkit.search.util.QueryModel.Internal.InternalModelRootNode;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.QueryModel.Internal.AttrValue;
import org.jzkit.configuration.api.Configuration;
import org.jzkit.ServiceDirectory.AttributeSetDBO;

public interface ProfileService
{
  /**
   * Return a query model based on the input model but conformant to the specified profile.
   * @param qm The input query model
   * @param profile_id id of the required profile
   * @return InternalQueryModel conformant to the specified profile
   */
  public InternalModelRootNode makeConformant(QueryModel qm, 
                                              Map<String,AttributeSetDBO> valid_attrs,
                                              Map<String,AttrValue> service_specific_rewrite_rules,
                                              String profile_id) throws ProfileServiceException;


  /**
   * Translate a given query into the appropriate semantic and structural query
   * @param source_query the input query
   * @param required_query_model the required class that implements QueryModel
   * @param global_profile global rules to fall back on in the absence of target specific rules (For example bib-1) 
   * @param service_profile Rules specifically for this service about what access points can be used.
   * @param service_rewrite_rules Rewrite rules made specifically for this target.
   * @param profile_id id of the required profile
   * @return InternalQueryModel conformant to the specified profile
  public QueryModel translate(QueryModel source_query,
                              Class required_query_model,
                              List<QueryAccessPointPattern> service_profile,
                              String global_profile_name,
                              List<SemanticExpansion> service_rewrite_rules);
   */
                        
}
