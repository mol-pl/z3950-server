package org.jzkit.configuration.api;

public class ConfigurationException extends Exception
{
  public ConfigurationException() {
    super();
  }

  public ConfigurationException(String reason) {
    super(reason);
  }

  public ConfigurationException(String reason, Throwable cause) {
    super(reason,cause);
  }


}
