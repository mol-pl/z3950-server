package org.jzkit.search.provider.jdbc;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;
import java.beans.*;
import java.net.URL;
import java.io.BufferedInputStream;

import java.util.*;

/**
 * Title:
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@k-int.com)
 * @version:        $Id$
 */
public interface RecordTemplatesConfig {
  public JDBCCollectionMappingInfo lookupEntityTemplateMappingInfo(String entity_name);
}
