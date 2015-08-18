/**
 * Title:       JDBCConfigException
 * @version:    $Id$
 * Copyright:   Copyright (C) 1999-2001 Knowledge Integration Ltd (See the COPYING file for details.)
 * @author:     Ian Ibbotson ( ibbo@k-int.com )
 * Company:     Knowledge Integration Ltd.
 * Description:
 *
 */

package org.jzkit.search.provider.jdbc;

public class JDBCConfigException extends Exception {

  public Object additional = null;

  public JDBCConfigException(String reason) {
    super(reason);
  }

  public JDBCConfigException(String reason, Object addinfo) {
    super(reason);
    this.additional = addinfo;
  }
}

