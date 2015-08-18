package org.jzkit.search.util.Profile;

import java.util.*;
import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;

public class ProfileServiceException extends Exception {

  private int error_code = 1;

  public ProfileServiceException(String reason) {
    super(reason);
  }

  public ProfileServiceException(String reason, int error_code) {
    super(reason);
    this.error_code = error_code;
  }

  public int getErrorCode() {
    return error_code;
  }
  
  public void setErrorCode(int error_code) {
    this.error_code = error_code;
  }
}
