package org.jzkit.configuration.api;

import javax.persistence.*;

/**
  * @author Ian Ibbotson
  * @since 1.0
  */
@Entity
@Table(name="JZ_RECORD_TRANSFORMER_INFO")
public class RecordTransformerTypeInformationDBO {

  private String type;
  private String classname;
  private Long id;

  public RecordTransformerTypeInformationDBO() {
  }

  public RecordTransformerTypeInformationDBO(String type,String classname) {
    this.type = type;
    this.classname = classname;
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

  public void setType(String type) {
    this.type = type;
  }
                                                                                                                                        
  @Column(name="TRANSFORMER_TYPE",length=32)
  public String getType() {
    return type;
  }

  public void setClassname(String classname) {
    this.classname = classname;
  }
                                                                                                                                        
  @Column(name="CLASS_NAME",length=256)
  public String getClassname() {
    return classname;
  }
}
