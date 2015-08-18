// Title:       TestClient
// @version:    $Id: OutstandingOperationInfo.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
// Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ibbo@k-int.com)
// Company:     KI
// Description: 
//


package org.jzkit.search.provider.z3950;

public class OutstandingOperationInfo
{
  public String refid = null;
  public long create_time = 0;
  public String op_type = null;
  public ZCallbackTarget callback = null;

  private OutstandingOperationInfo()
  {
  }

  public OutstandingOperationInfo(String refid,
                                  String op_type,
                                  ZCallbackTarget callback)
  {
    this.refid = refid;
    this.create_time = System.currentTimeMillis();
    this.op_type = op_type;
    this.callback = callback;
  }

  public ZCallbackTarget getCallbackTarget()
  {
    return callback;
  }
}
