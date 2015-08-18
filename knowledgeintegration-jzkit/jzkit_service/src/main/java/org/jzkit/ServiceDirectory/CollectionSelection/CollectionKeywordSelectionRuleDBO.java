package org.jzkit.ServiceDirectory.CollectionSelection;

import java.util.*;
import javax.persistence.*;


/** A business entity class representing an Collection Description
  *
  * @author Ian Ibbotson
  * @since 1.0
  * Select collections based on a keyword in the collection description
  */
@javax.persistence.Entity
@DiscriminatorValue("3")
public class CollectionKeywordSelectionRuleDBO extends CollectionSelectionRuleDBO {

  private String keyword;

  @Column(name="SEL_KEYWORD",length=64)
  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword=keyword;
  }
}
