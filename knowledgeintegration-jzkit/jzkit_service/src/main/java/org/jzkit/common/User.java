package org.jzkit.common;

public class User
{

  private Long id;
  private String userid;
  private String password;
  private String name;
  private String user_dn;
  private String notes;

  public User()
  {
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public String getUserID()
  {
    return userid;
  }

  public void setUserID(String userid)
  {
    this.userid = userid;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getUserName()
  {
    return name;
  }

  public void setUserName(String name)
  {
    this.name = name;
  }

  public String getUserDN()
  {
    return user_dn;
  }

  public void setUserDN(String user_dn)
  {
    this.user_dn = user_dn;
  }

  public String getNotes()
  {
    return notes;
  }

  public void setNotes(String notes)
  {
    this.notes = notes;
  }
}
