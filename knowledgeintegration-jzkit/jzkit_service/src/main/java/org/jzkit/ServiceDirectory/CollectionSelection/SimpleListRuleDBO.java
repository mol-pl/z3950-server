package org.jzkit.ServiceDirectory.CollectionSelection;

import java.util.*;
import javax.persistence.*;

/** A business entity class representing an Collection Description
  *
  * @author Ian Ibbotson
  * @since 1.0
  * Select collections based on explict addition to a list, E.G. "My Favorite Collections"
  */
@javax.persistence.Entity
@DiscriminatorValue("1")
public class SimpleListRuleDBO extends CollectionSelectionRuleDBO
{
  private Set instances = new HashSet();

  @ManyToMany(
    targetEntity=org.jzkit.ServiceDirectory.CollectionDescriptionDBO.class,
    cascade={CascadeType.PERSIST, CascadeType.MERGE}
  )
  @JoinTable(
        name="JZ_SIMPLE_COLLECTION_LIST",
        joinColumns={@JoinColumn(name="COLL_FK")},
        inverseJoinColumns={@JoinColumn(name="SIMPLE_LIST_RULE_FK")}
  )
  public Set getInstances()
  {
    return instances;
  }
                                                                                                                                          
  public void setInstances(Set instances)
  {
    this.instances = instances;
  }
                                                                                                                                          

}
