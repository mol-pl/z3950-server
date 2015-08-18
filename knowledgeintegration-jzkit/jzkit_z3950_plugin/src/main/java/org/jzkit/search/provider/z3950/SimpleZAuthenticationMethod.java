package  org.jzkit.search.provider.z3950;

/**
 *
 * SimpleZAuthenticationMethod.
 *
 * @version:    $Id: SimpleZAuthenticationMethod.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
 * Copyright:   Copyright (C) 1999-2002 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 */

public class SimpleZAuthenticationMethod extends ZAuthenticationMethod
{
  private int auth_type;
  private String user;
  private String group;
  private String credentials;

  public SimpleZAuthenticationMethod()
  {
  }

  public SimpleZAuthenticationMethod(int auth_type, String user, String group, String credentials)
  {
    this.auth_type=auth_type;
    this.user=user;
    this.group=group;
    this.credentials=credentials;
  }

  public int getAuthType()
  {
    return auth_type;
  }

  public void setAuthType()
  {
    this.auth_type = auth_type;
  }

  public String getUserId()
  {
    return user;
  }

  public void setUserId(String user)
  {
    this.user = user;
  }

  public String getGroupId()
  {
    return group;
  }

  public void setGroupId(String group)
  {
    this.group = group;
  }

  public String getCredentials()
  {
    return credentials;
  }

  public void setCredentials(String credentials)
  {
    this.credentials = credentials;
  }
}
