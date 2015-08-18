/**
 * Title:       QueryFormatter
 * Copyright:   Copyright (C) 2001- Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Company:     Knowledge Integration Ltd.
 */

package org.jzkit.search.util.RecordBuilder;

import org.jzkit.search.util.RecordModel.*;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;

/**
 * @author Ian Ibbotson
 * @version $Id: QueryModel.java,v 1.2 2004/11/18 14:25:54 ibbo Exp $
 * A RecordFactory creates an information fragment from some other fragment.
 * Essentially a translation unit.
 */ 
public interface RecordBuilder {

  /**
   * @param input_fragment Source Fragment
   * @param esn Optional Element Set Name (F,B, For XML, a schema to request);
   * @return native_object
   */
  public Object createFrom(Document input_dom, String esn) throws RecordBuilderException;

  /**
   * create the canonica XML for this object
   */
  public Document getCanonicalXML(Object native_object)  throws RecordBuilderException;
}
