package org.jzkit.search.util.Profile;

public class QueryVerifyResult
{
  private boolean is_valid = true;
  private String fail_attr = null;

  public QueryVerifyResult()
  {
  }

  public void setIsValid(boolean is_valid)
  {
    this.is_valid = is_valid;
  }

  public boolean queryIsValid()
  {
    return is_valid;
  }

  public void setFailingAttr(String fail_attr)
  {
    this.fail_attr = fail_attr;
  }

  public String getFailingAttr()
  {
    return fail_attr;
  }
}
