package org.jzkit.srw;

public interface WSAuthService {
  public boolean authenticateToken(String token);
  public boolean authenticateUser(String username, String credentials);
}
