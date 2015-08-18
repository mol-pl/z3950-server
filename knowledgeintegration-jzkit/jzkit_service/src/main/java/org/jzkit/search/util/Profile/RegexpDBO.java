package org.jzkit.search.util.Profile;

import org.jzkit.search.util.QueryModel.Internal.*;
import java.util.regex.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.persistence.*;

@javax.persistence.Entity
@DiscriminatorValue("5")
public class RegexpDBO extends RuleNodeDBO { 

  public static Log log = LogFactory.getLog(RegexpDBO.class);
  private Pattern pattern = null;
  private String pattern_string;
  private String type;
  private String namespace;

  public RegexpDBO() {
    super();
    System.err.println("new RegexpDBO");
  }

  public RegexpDBO(String type, String namespace, String pattern) {
    super();
    System.err.println("new RegexpDBO");
    this.type = type;
    this.namespace = namespace;
    this.setPattern(pattern);
  }

  public boolean isValid(String default_namespace, AttrPlusTermNode aptn, QueryVerifyResult qvr) {
    boolean result = false;
    Object a = aptn.getAttr(type);

    if ( a != null ) {
      if ( a instanceof AttrValue ) {
        AttrValue av = (AttrValue) a;
        String val = av.getWithDefaultNamespace(default_namespace);
        result = pattern.matcher(val).matches();

        if ( !result ) {
          qvr.setFailingAttr(type);
          qvr.setIsValid(false);
        }
      }
      else {
        log.debug("checking simple string");
        result = pattern.matcher(a.toString()).matches();
      }
    }
    else {
      log.debug("No mapping for attribute type "+type+" from aptn "+aptn);
    }
    
    return result;
  }

  @Column(name="MATCH_ATTR_TYPE", length=32)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Column(name="REGEXP_NAMESPACE", length=32)
  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  @Column(name="REGEXP_PATTERN", length=256)
  public String getPattern() {
    return pattern_string;
  }

  public void setPattern(String pattern) {
    // log.fine("setPattern "+pattern);
    pattern_string = pattern;

    try {
      this.pattern = Pattern.compile(pattern);
    }
    catch ( IllegalArgumentException iae ) {
      log.warn("Problem compiling exception "+iae);
      throw new RuntimeException(iae);
    }
  }

  @Transient
  public String getDesc() {
    return pattern_string;
  }

  @Transient
  public String getNodeType() {
    return "Regular Expression Rule";
  }

  public String toString() {
    return "Regular Expression for "+type+" : "+pattern_string;
  }
}
