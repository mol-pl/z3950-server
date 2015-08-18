package org.jzkit.ServiceDirectory;

import java.util.*;
import javax.persistence.*;

/** A business entity class representing an Collection Description
  *
  * @author Ian Ibbotson
  * @since 1.0
  */
@Entity
@Table(name="JZ_COLLECTION_DESCRIPTION")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class CollectionDescriptionDBO {

  private Long id;
  private String code;
  private String local_id;
  private String profile;
  private String collection_name;
  private Set collection_postings = new HashSet();
  private String cd_metadata_record_id;
  private SearchServiceDescriptionDBO search_service_description;


  public CollectionDescriptionDBO() {
  }

  public CollectionDescriptionDBO(String code, String collection_name, String cd_metadata_record_id) {
    this.code = code;
    this.collection_name = collection_name;
    this.cd_metadata_record_id = cd_metadata_record_id;
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

  /**
   * @hibernate.column name="CODE" length="50" unique="true" unique-key="COLL_CODE_IDX" not-null="true"
   */
  @Column(name="CODE",length=50,nullable=false)
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Column(name="COLLECTION_NAME",length=128)
  public String getCollectionName() {
    return collection_name;
  }

  public void setCollectionName(String collection_name) {
    this.collection_name = collection_name;
  }

  @ManyToMany(
    targetEntity=org.jzkit.ServiceDirectory.CollectionInfoTypeDBO.class,
    cascade={CascadeType.ALL}
  )
  @JoinTable(
        name="JZ_COLLECTION_INFO_TYPE_POSTING",
        joinColumns={@JoinColumn(name="COLLECTION_ID_FK")},
        inverseJoinColumns={@JoinColumn(name="INFO_TYPE_ID_FK")}
  )
  public Set getPostings() {
    return collection_postings;
  }

  public void setPostings(Set collection_postings) {
    this.collection_postings = collection_postings;
  }

  /**
   * @hibernate.property column="CD_METADATA_REC_ID" length="255"
   */
  @Column(name="CD_METADATA_REC_ID",length=255)
  public String getCDMetadataRecordId() {
    return cd_metadata_record_id;
  }

  public void setCDMetadataRecordId(String cd_metadata_record_id) {
    this.cd_metadata_record_id = cd_metadata_record_id;
  }

  public String toString() {
    return code+":"+collection_name;
  }

  public void setLocalId(String local_id) {
    this.local_id = local_id;
  }

  @Column(name="LOCAL_ID",length=64)
  public String getLocalId() {
    return local_id;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  @Column(name="PROFILE",length=64)
  public String getProfile() {
    return profile;
  }

  @ManyToOne(cascade=CascadeType.ALL)
  @JoinColumn(name="REPOSITORY_ID")
  public SearchServiceDescriptionDBO getSearchServiceDescription() {
    return search_service_description;
  }

  public void setSearchServiceDescription(SearchServiceDescriptionDBO search_service_description) {
    this.search_service_description = search_service_description;
  }
}
