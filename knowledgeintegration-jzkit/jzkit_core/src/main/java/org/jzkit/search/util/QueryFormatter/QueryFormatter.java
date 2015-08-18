/**
 * Title:       QueryFormatter
 * Copyright:   Copyright (C) 2001- Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Company:     Knowledge Integration Ltd.
 */

package org.jzkit.search.util.QueryFormatter;

import org.jzkit.search.util.QueryModel.Internal.InternalModelRootNode;
import org.springframework.context.ApplicationContext;
import org.jzkit.search.util.QueryModel.QueryModel;

/**
 * @author Ian Ibbotson
 * @version $Id: QueryModel.java,v 1.2 2004/11/18 14:25:54 ibbo Exp $
 * A QueryFormatter turns a query model into a string.
 */ 
public interface QueryFormatter {
  public String format(InternalModelRootNode query) throws QueryFormatterException;
}
