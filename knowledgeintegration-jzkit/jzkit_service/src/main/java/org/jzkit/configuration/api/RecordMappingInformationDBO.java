package org.jzkit.configuration.api;

import javax.persistence.*;

/** A business entity class representing an Concept
  *
  * @author Ian Ibbotson
  * @since 1.0
  */
@Entity
@Table(name="JZ_RECORD_MAPPING_INFO")
public class RecordMappingInformationDBO {

  private Long id;
  private String from_spec;
  private String to_spec;
  private String type;
  private String resource;

  public RecordMappingInformationDBO() {
  }

  public RecordMappingInformationDBO(String from, String to, String type, String resource) {
    this.from_spec = from;
    this.to_spec = to;
    this.type = type;
    this.resource = resource;
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

  public void setFromSpec(String from_spec) {
    this.from_spec = from_spec;
  }

  @Column(name="FROM_SPEC",length=40)
  public String getFromSpec() {
    return from_spec;
  }

  public void setToSpec(String to_spec) {
    this.to_spec = to_spec;
  }

  @Column(name="TO_SPEC",length=40)
  public String getToSpec() {
    return to_spec;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Column(name="MAPPING_TYPE",length=40)
  public String getType() {
    return type;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  @Column(name="RESOURCE",length=255)
  public String getResource() {
    return resource;
  }

  public String toString() {
    return   from_spec+" -> "+to_spec+" ("+type+") = "+resource;
  }
}
