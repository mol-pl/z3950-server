package org.jzkit.configuration.provider.hybrid;

import org.jzkit.configuration.api.*;

public interface ConfigFileProcessor {
  public void process(java.io.File file, Configuration configuration);
}
