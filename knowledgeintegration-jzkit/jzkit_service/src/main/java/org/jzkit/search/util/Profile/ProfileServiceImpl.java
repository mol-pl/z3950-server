package org.jzkit.search.util.Profile;

import java.beans.*;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.*;
import java.util.prefs.Preferences;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import org.apache.commons.digester.*;
import org.apache.commons.digester.xmlrules.*;
import org.jzkit.configuration.api.*;
import org.springframework.context.*;
import org.jzkit.ServiceDirectory.AttributeSetDBO;

                                                                                                                                        
public class ProfileServiceImpl implements ProfileService, ApplicationContextAware {

  // private Map m = new HashMap();
  private static Log log = LogFactory.getLog(ProfileServiceImpl.class);
  private ApplicationContext ctx = null;
  private Configuration configuration = null;

  /** If we can't map directly, abort */
  private static final int SEMANTIC_ACTION_STRICT = 1;
  /** If we can't map directly, strip the un-mappable component (Should feedback somehow) */
  private static final int SEMANTIC_ACTION_STRIP = 2; 
  /** If we can't map directly, do a best effort match */
  private static final int SEMANTIC_ACTION_FUZZY = 3;

  public ProfileServiceImpl() {
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  /* For backwards compatibility */
  // public InternalModelRootNode makeConformant(QueryModel qm, String profile_code) throws ProfileServiceException {
  //   return makeConformant(qm,profile_code,SEMANTIC_ACTION_STRICT);
  // }

  public InternalModelRootNode makeConformant(QueryModel qm, 
                                              Map<String,AttributeSetDBO> valid_attributes,
                                              Map<String,AttrValue> service_specific_rewrite_rules,
                                              String profile_code) throws ProfileServiceException {

    InternalModelRootNode result = null;

    // Walk the query tree.. validate each node.
    log.debug("makeConformant profile:"+profile_code+" query:"+qm.toString());

    try {
      ProfileDBO p = configuration.lookupProfile(profile_code);

      if ( ( p == null ) && ( valid_attributes == null ) ) {
        log.debug("No profile defined and no valid attributes list, unable to rewrite");
        result = qm.toInternalQueryModel(ctx);
      }
      else {
        log.debug("Rewriting");
        result = (InternalModelRootNode) visit(qm.toInternalQueryModel(ctx), null, valid_attributes, service_specific_rewrite_rules, p);
      }
    }
    catch ( org.jzkit.search.util.QueryModel.InvalidQueryException iqe ) {
      throw new ProfileServiceException(iqe.toString());
    }
    catch ( org.jzkit.configuration.api.ConfigurationException ce ) {
      throw new ProfileServiceException(ce.toString());
    }

    // log.debug("makeConformant result="+result);
    return result;
  }

  private QueryNode visit(QueryNode qn, 
                          String default_namespace, 
                          Map<String,AttributeSetDBO> valid_attributes,
                          Map<String,AttrValue> service_specific_rewrite_rules,
                          ProfileDBO p) throws org.jzkit.search.util.QueryModel.InvalidQueryException, ProfileServiceException {

    if ( qn == null ) 
      throw new org.jzkit.search.util.QueryModel.InvalidQueryException("Query node was null, unable to rewrite");

    log.debug("Rewrite: visit instance of "+qn.getClass().getName());

    if ( qn instanceof InternalModelRootNode ) {
      InternalModelRootNode imrn = (InternalModelRootNode)qn;
      return new InternalModelRootNode(visit(imrn.getChild(), default_namespace, valid_attributes, service_specific_rewrite_rules, p));
    }
    else if ( qn instanceof InternalModelNamespaceNode ) {
      InternalModelNamespaceNode imns = (InternalModelNamespaceNode)qn;
      log.debug("child default attrset will be "+imns.getAttrset());
      return new InternalModelNamespaceNode(imns.getAttrset(), visit(imns.getChild(),
                                                                     imns.getAttrset(), 
                                                                     valid_attributes,
                                                                     service_specific_rewrite_rules, 
                                                                     p));
    }
    else if ( qn instanceof ComplexNode ) {
      ComplexNode cn = (ComplexNode)qn;

      QueryNode lhs = null;
      QueryNode rhs = null;
      
      if ( ( cn.getLHS() != null ) && ( cn.getLHS().countChildrenWithTerms() > 0 ) )
        lhs = visit(cn.getLHS(), default_namespace, valid_attributes, service_specific_rewrite_rules, p);

      if ( ( cn.getRHS() != null ) && ( cn.getRHS().countChildrenWithTerms() > 0 ) )
        rhs = visit(cn.getRHS(), default_namespace, valid_attributes, service_specific_rewrite_rules, p);

      if ( ( lhs != null ) && ( rhs != null ) )
        return new ComplexNode(lhs,  rhs, cn.getOp());
      else if ( lhs != null )
        return lhs;
      else
        return rhs;
    }
    else if ( qn instanceof AttrPlusTermNode ) {
      AttrPlusTermNode aptn = null;

      if ( ( valid_attributes != null ) && 
           ( service_specific_rewrite_rules != null ) && 
           ( valid_attributes.size() > 0 ) ) {
        // Use explain mode - valid queries taken from service itself
        aptn = rewriteUntilValid((AttrPlusTermNode)qn, valid_attributes, service_specific_rewrite_rules,default_namespace);
      }
      else {
        // Use profile mode - valid queries determined from a pre-arranged profile
        aptn = rewriteUntilValid((AttrPlusTermNode)qn,p,default_namespace);
      }

      // If we are in strict mode, throw an exception
      if ( aptn == null )
        throw new ProfileServiceException("Unable to rewrite node. Semantic action was set to strict, and there appears to be no valid alternatives for node "+qn,3);

      return aptn;
    }
    else
      throw new ProfileServiceException("Should never be here");
  }

  private AttrPlusTermNode rewriteUntilValid(AttrPlusTermNode q,
                                             Map<String,AttributeSetDBO> valid_attributes,
                                             Map<String,AttrValue> service_specific_rewrite_rules,
                                             String default_namespace) 
                   throws org.jzkit.search.util.QueryModel.InvalidQueryException, ProfileServiceException {

    AttrPlusTermNode result = q;

    for ( java.util.Iterator i = q.getAttrIterator(); i.hasNext(); ) {
      // 1. extract and rewrite use attribute
      String attr_type = (String) i.next();
      AttrValue av = (AttrValue) q.getAttr(attr_type);
      log.debug("Rewriting "+attr_type+"="+av);
      AttributeSetDBO as = valid_attributes.get(attr_type);

      if ( as == null )
        throw new ProfileServiceException("No "+attr_type+" attr types allowed for target repository",4);

      AttrValue new_av = rewriteUntilValid(av,as.getAttrs(),service_specific_rewrite_rules,default_namespace);
      log.debug("Setting attr "+attr_type+" to "+new_av);
      q.setAttr(attr_type, new_av);

    }

    log.debug(q.getAttrs());

    return result;
  }

  private AttrValue rewriteUntilValid(AttrValue av,
                                      Set<AttrValue> explain_use_indexes,
                                      Map<String,AttrValue> service_specific_rewrite_rules,
                                      String default_namespace) throws ProfileServiceException {
    AttrValue result = av;

    if ( av != null ) {
      String av_str_val = av.getWithDefaultNamespace(default_namespace);
      if ( explain_use_indexes.contains(av) ) {
        log.debug("No need to rewrite, source index "+av+" is already allowed by target");
      }
      else {
        log.debug("Rewrite, source index "+av+" is disallowed, scanning server alternatives allowed="+explain_use_indexes);
        boolean found = false;
        for ( java.util.Iterator i = service_specific_rewrite_rules.entrySet().iterator(); ( ( i.hasNext() ) && ( !found ) ); ) {
          Map.Entry e = (Map.Entry) i.next();
          if ( e.getKey().equals(av_str_val) ) {
            AttrValue new_av = (AttrValue) e.getValue();
            log.debug("Possible rewrite: "+new_av);
            if ( explain_use_indexes.contains(new_av) ) {
              log.debug("Matched, replacing");
              result = new_av;
              found=true;
            }
          }
        }
        if ( !found ) {
          log.debug("Unable to rewrite query, exception");
          throw new ProfileServiceException("Unable to rewrite access point '"+av_str_val+"' to comply with service explain record",3);
        }
      }
    }
    else {
    }

    return result;
  }

  /**
   * Continue to rewrite the source query until one which validates agains the profile is found.
   * Returns null if there are no valid expansions.
   */
  private AttrPlusTermNode rewriteUntilValid(AttrPlusTermNode q, 
                                             ProfileDBO p,
                                             String default_namespace) throws ProfileServiceException {

    log.debug("rewriteUntilValid.... def ns = "+default_namespace);

    QueryVerifyResult qvr = p.validate(q, default_namespace);
    // if ( p.isValid(q, default_namespace) )
    AttrPlusTermNode result = null;

    if ( qvr.queryIsValid() ) {
      log.debug("Node is conformant to profile.... return it");
      result = q;
    }
    else {
      log.debug("Node does not conform to profile ("+q.getAccessPoint()+" not allowed by profile "+p.getCode()+")");
      // Get failing attr from QVR, generate expansions, rewriteUntilValid each expansion.
      // What if failing attr was an AND..?.. Still had to be a component that failed. The Rule that returned false.
      String failing_attr_type = qvr.getFailingAttr();
      AttrValue av = (AttrValue) q.getAttr(failing_attr_type);

      if ( av != null ) {
        Set<AttrValue> possible_alternatives = lookupKnownAlternatives(av,default_namespace);
        if ( possible_alternatives != null ) {
          log.debug("Check out alternatives for "+failing_attr_type+":"+possible_alternatives);
          for ( Iterator i = possible_alternatives.iterator(); ( ( i.hasNext() ) && ( result == null ) ); ) {
            AttrValue target_av = (AttrValue)i.next();
            AttrPlusTermNode new_variant = q.cloneForAttrs();
            new_variant.setAttr(failing_attr_type, target_av);

            result = rewriteUntilValid(new_variant, p, default_namespace);
          }
        }
        else {
          log.debug("No expansions available. Return null");
        }
      }
      else {
        log.debug("Hmm.. It appears that we failed because a rule required an attr type which is not present in the query tree("+failing_attr_type+"). Perhaps we should add missing attrs ;)");
      }
    }

    return result;
  }

  private Set<AttrValue> lookupKnownAlternatives(AttrValue av, String default_namespace) {
    Set<AttrValue> result = null;
    try {
      String namespace = av.getNamespaceIdentifier();
      if ( namespace == null )
        namespace = default_namespace;

      log.debug("Lookup mappings from namespace "+namespace+" attr value = "+av.getValue());

      CrosswalkDBO cw = configuration.lookupCrosswalk(namespace);

      if ( cw != null ) {
        org.jzkit.search.util.Profile.AttrMappingDBO am = cw.lookupMapping(av.getValue().toString());
        if ( am != null ) {
          result = am.getTargetAttrs();
        }
      }
      else {
        log.warn("No crosswalk available for source namespace "+namespace);
      }
    }
    catch ( ConfigurationException ce ) {
      log.warn("Problem looking up alternatives for "+av.getValue().toString(),ce);
    }

    return result;
  }

}
