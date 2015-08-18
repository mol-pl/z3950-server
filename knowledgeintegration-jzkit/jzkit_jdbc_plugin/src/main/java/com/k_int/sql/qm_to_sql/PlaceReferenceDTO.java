package com.k_int.sql.qm_to_sql;

/**
 * Title: PlaceReferencDTO
 * Description: An unambiguous place reference, fully qualified in some identifying hierarchy
 * Copyright:       
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@k-int.com)
 */
public class PlaceReferenceDTO {

  // Fully qualified paf entries are of the form postcode, thoroughfare, locality, dep local, doub_dep_local , postal area (the code like LW)
  private String naming_scheme;  // Normally paf?

  // Array for passing to backend
  private String[] qualified_place_name_components;
  
  public PlaceReferenceDTO(String naming_scheme, String[] qualified_place_name_components) {
    this.naming_scheme = naming_scheme;
    this.qualified_place_name_components = qualified_place_name_components;
  }

  public PlaceReferenceDTO() {
  }

  public String getNamingScheme() {
    return naming_scheme;
  }

  public void setNamingScheme(String naming_scheme) {
    this.naming_scheme = naming_scheme;
  }

  public String[] getQualifiedNames() {
    return qualified_place_name_components;
  }

  public void setQualifiedNames(String[] qualified_place_name_components) {
    this.qualified_place_name_components = qualified_place_name_components;
  }
}
