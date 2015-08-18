/**
 * Title:       QueryBuilder
 * Copyright:   Copyright (C) 2001- Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Company:     Knowledge Integration Ltd.
 */

package org.jzkit.search.util.QueryBuilder;

import org.jzkit.search.util.QueryModel.Internal.InternalModelRootNode;
import org.springframework.context.ApplicationContext;
import org.jzkit.search.util.QueryModel.QueryModel;

/**
 * @author Ian Ibbotson
 * @version $Id: QueryModel.java,v 1.2 2004/11/18 14:25:54 ibbo Exp $
 * A QueryBuilder knows how to construct a specific QueryModel from an internal model root node.
 * The internal model is intended to be in the correct profile.
 */ 
public interface QueryBuilder {
  public QueryModel build(InternalModelRootNode query);
}
