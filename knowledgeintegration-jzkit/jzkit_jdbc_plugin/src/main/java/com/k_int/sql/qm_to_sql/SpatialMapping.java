package com.k_int.sql.qm_to_sql;

/**
 * Title: SimpleTextMapping
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: SpatialMapping.java,v 1.2 2005/02/02 13:27:30 ibbo Exp $
 *
 * @hibernate.subclass dynamic-update="true" dynamic-insert="true" discriminator-value="106"
 */
public class SpatialMapping extends BaseMapping {

  public static final String CONTAINS_OP="contains";
  public static final String WITHIN_OP="within";

  public String gazeteer_bean_name;
  public boolean resolve_place_names = false;
  public String operation=CONTAINS_OP;

  private SpatialMapping() {
  }

  public SpatialMapping(String access_path, String gazeteer_bean_name, boolean resolve_place_names, String operation) {
    super(access_path);
    this.gazeteer_bean_name=gazeteer_bean_name;
    this.resolve_place_names=resolve_place_names;
    this.operation=operation;
  }

  public SpatialMapping(String access_path) {
    super(access_path);
  }

  /**
   * @hibernate.property
   * @hibernate.column name="SPATIAL_MAPPING_GAZ_NAME" length="128"
   */
  public String getGazetterName() {
    return gazeteer_bean_name;
  }

  public void setGazetterName(String gazeteer_bean_name) {
    this.gazeteer_bean_name = gazeteer_bean_name;
  }

  public boolean resolvePlaceNames() {
    return resolve_place_names;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="SPATIAL_MAPPING_RES_PLACE_NAMES"
   */
  public boolean getResolvePlaceNames() {
    return resolve_place_names;
  }

  public void setResolvePlaceNames(boolean resolve_place_names) {
    this.resolve_place_names = resolve_place_names;
  }

  /**
   * @hibernate.property
   * @hibernate.column name="SPATIAL_MAPPING_OP" length="32"
   */
  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }
}
