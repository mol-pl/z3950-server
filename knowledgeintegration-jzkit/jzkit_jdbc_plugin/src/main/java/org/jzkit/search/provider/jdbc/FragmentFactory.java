package org.jzkit.search.provider.jdbc;

import com.k_int.sql.data_dictionary.Entity;
import org.w3c.dom.Document;

/**
 * Title:
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: FragmentFactory.java,v 1.1 2004/10/27 14:42:42 ibbo Exp $
 */
public interface FragmentFactory {
  public Document createFragment(Entity e);
}
