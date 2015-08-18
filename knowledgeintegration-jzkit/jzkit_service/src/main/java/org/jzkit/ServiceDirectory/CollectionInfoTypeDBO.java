package org.jzkit.ServiceDirectory;

import java.util.*;
import javax.persistence.*;

/** A business entity class representing an Collection Category
  *
  * @author Ian Ibbotson
  * @since 1.0
  */
@Entity
@Table(name="JZ_COLLECTION_INFO_TYPE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class CollectionInfoTypeDBO {

  private Long id;
  private String namespace;
  private String code;
  private Set collections = new HashSet();

  public CollectionInfoTypeDBO() {
  }

  public CollectionInfoTypeDBO(String namespace, String code) {
    this.namespace = namespace;
    this.code = code;
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

  @Column(name="NAMESPACE",length=64)
  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  @Column(name="CODE",length=64,nullable=false)
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @ManyToMany(
    targetEntity=org.jzkit.ServiceDirectory.CollectionDescriptionDBO.class,
    cascade={CascadeType.PERSIST, CascadeType.MERGE}
  )
  @JoinTable(
        name="JZ_COLLECTION_INFO_TYPE_POSTING",
        joinColumns={@JoinColumn(name="INFO_TYPE_ID_FK")},
        inverseJoinColumns={@JoinColumn(name="COLLECTION_ID_FK")}
  )
  public Set getCollections() {
    return collections;
  }
                                                                                                                                                             
  public void setCollections(Set collections) {
    this.collections = collections;
  }
                                                                                                                                                             
  public String toString() {
    return id+" - "+namespace+":"+code;
  }
}
