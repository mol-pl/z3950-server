package org.jzkit.search.util.Profile;

import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import java.util.logging.*;
import org.springframework.context.*;
import javax.persistence.*;

/**
 * @author <a href="mailto:ian.ibbotson@">Ian Ibbotson</a>
 */
@Entity
@Table(name="JZ_PROFILE")
public class ProfileDBO {

  private java.sql.Timestamp last_updated;
  private Long id;
  private String code;
  private String profile_name;
  private RuleNodeDBO rule_node;
  private static Logger log = Logger.getLogger(ProfileDBO.class.getName());

  public ProfileDBO() {
  }

  @Id
  @Column(name="ID")
  @GeneratedValue(strategy=GenerationType.AUTO)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name="CODE",length=20)
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setProfileName(String profile_name) {
    this.profile_name = profile_name;
  }

  @Column(name="PROFILE_NAME",length=40)
  public String getProfileName() {
    return profile_name;
  }

  public void setValidAttrRule(RuleNodeDBO rule_node) {
    this.rule_node = rule_node;
  }

  @ManyToOne(cascade=CascadeType.ALL)
  @JoinColumn(name="RULE_NODE_ID")
  public RuleNodeDBO getValidAttrRule() {
    return rule_node;
  }

  public boolean isQueryConformant(QueryModel qm, ApplicationContext ctx) throws ProfileServiceException {
    // log.fine("isQueryConformant");
    // We need to visit each node in the model to check that it is conformant to the profile
    boolean result = true;

    try
    {
      result = visit(qm.toInternalQueryModel(ctx),null,new QueryVerifyResult());
    }
    catch (org.jzkit.search.util.QueryModel.InvalidQueryException iqe )
    {
      throw new ProfileServiceException(iqe.toString());
    }

    return result;
  }

  private boolean visit(QueryNode qn, 
                        String default_namespace,
                        QueryVerifyResult qvr) throws org.jzkit.search.util.QueryModel.InvalidQueryException {
    // log.fine("visit instance of "+qn.getClass().getName());

    if ( qn instanceof InternalModelRootNode )
    {
      InternalModelRootNode imrn = (InternalModelRootNode)qn;
      return visit(imrn.getChild(), default_namespace, qvr);
    }
    else if ( qn instanceof InternalModelNamespaceNode )
    {
      InternalModelNamespaceNode imns = (InternalModelNamespaceNode)qn;
      // log.fine("namespace set to "+imns.getAttrset());
      return visit(imns.getChild(), imns.getAttrset(), qvr);
    }
    else if ( qn instanceof ComplexNode )
    {
      ComplexNode cn = (ComplexNode)qn;
      return ( visit(cn.getLHS(), default_namespace, qvr) && visit(cn.getRHS(), default_namespace, qvr) );
    }
    else if ( qn instanceof AttrPlusTermNode )
    {
      return rule_node.isValid(default_namespace, (AttrPlusTermNode)qn, qvr);
    }

    // Shouldn't be here :)
    return false;
  }

  public boolean isValid(AttrPlusTermNode aptn, String default_namespace) throws ProfileServiceException {
    return validate(aptn,default_namespace).queryIsValid();
  }

  public QueryVerifyResult validate(AttrPlusTermNode aptn, String default_namespace) throws ProfileServiceException {
    QueryVerifyResult qvr = new QueryVerifyResult();
    // log.fine("validate");
                                                                                                                                        
    try {
      visit(aptn,default_namespace,qvr);
    }
    catch (org.jzkit.search.util.QueryModel.InvalidQueryException iqe ) {
      throw new ProfileServiceException(iqe.toString());
    }


    return qvr;
  }

  @Column(name="LAST_UPDATED")
  public java.sql.Timestamp getLastUpdated() {
    return last_updated;
  }

  public void setLastUpdated(java.sql.Timestamp last_updated) {
    this.last_updated = last_updated;
  }

}
