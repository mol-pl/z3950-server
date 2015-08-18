package org.jzkit.configuration.api;

import javax.persistence.*;


/**
  * @author Ian Ibbotson
  * @since 1.0
  */
@Entity
@Table(name="JZ_APPLICATION_SETTING")
public class AppSettingDBO {

  private Long id;
  private String name;
  private String value;


  public AppSettingDBO() {
  }

  public AppSettingDBO(String name, String value) {
    this.name = name;
    this.value = value;
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

  public void setName(String name) {
    this.name = name;
  }
                                                                                                                                        
  @Column(name="NAME",length=256)
  public String getName() {
    return name;
  }

  public void setValue(String value) {
    this.value = value;
  }
                                                                                                                                        
  @Column(name="VALUE",length=256)
  public String getValue() {
    return value;
  }
}
