/**
 * Title:       Z3950Exception
 * @version:    $Id: Z3950Exception.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     KI
 * Description: 
 *
 */

package org.jzkit.z3950;

public class Z3950Exception extends Exception
{
  private Object addinfo = null;
  private String diag_set = null;
  private int diag_code = -1;

  public Z3950Exception()
  {
    super();
  }

  public Z3950Exception(String text)
  {
    super(text);
  }

  public Z3950Exception(String text, String diag_set, int diag_code, Object addinfo)
  {
    super(text);
    this.diag_set = diag_set;
    this.diag_code = diag_code;
    this.addinfo = addinfo;
  }

  public Object getAddinfo()
  {
    return addinfo;
  }
}
