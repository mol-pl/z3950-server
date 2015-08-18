package com.k_int.sql.qm_to_sql;
import java.util.Map;
/**
 * QMToSQLConfig
 * Title:
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: QMToSQLConfig.java,v 1.2 2004/10/31 12:21:22 ibbo Exp $
 */
public interface QMToSQLConfig
{
  public AttrMap lookupAttrMap(String entity_name);
  public DatabaseMapping lookupDatabaseMapping(String public_collection_id);

  public void addDatabaseMapping(String public_code, String base_entity, String discriminator_collection_code);
}
