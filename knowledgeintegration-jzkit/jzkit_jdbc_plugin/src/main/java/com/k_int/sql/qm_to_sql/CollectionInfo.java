package com.k_int.sql.qm_to_sql;

import java.util.List;

/**
 *       Title: CollectionInfo
 *     @author: Ian Ibbotson (ian.ibbotson@k-int.com)
 *    @version: $Id: CollectionInfo.java,v 1.1 2004/10/31 12:22:31 ibbo Exp $
 */
public class CollectionInfo {

  public String base_entity_name;
  public List collection_identifiers;

  public CollectionInfo() {
  }

  public CollectionInfo(String base_entity_name, List collection_identifiers) {
    this.base_entity_name=base_entity_name;
    this.collection_identifiers=collection_identifiers;
  }

  public String getBaseEntityName() {
    return base_entity_name;
  }

  public List getCollectionIdentifiers() {
    return collection_identifiers;
  }
}
