package com.k_int.colws.search.util;

import org.springframework.context.*;
import com.k_int.svc.identity.service.IdentityService;

public final class WSAuthService implements org.jzkit.srw.WSAuthService, ApplicationContextAware {

  private ApplicationContext ctx;

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public boolean authenticateToken(String token) {
    boolean result = false;
    try {
      IdentityService identity_service = (IdentityService) ctx.getBean("IdentityService");
      result = ( identity_service.authenticate(token) != null );
    }
    catch ( com.k_int.svc.identity.service.IdentityServiceException ise ) {
      System.err.println("Identity Service Exception "+ise);
      ise.printStackTrace();
    }
    return result;
  }

  public boolean authenticateUser(String username, String credentials) {
    return false;
  }

}
