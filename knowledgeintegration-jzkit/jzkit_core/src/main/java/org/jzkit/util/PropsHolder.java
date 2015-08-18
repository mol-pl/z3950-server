package org.jzkit.util;

import java.util.Properties;
import java.io.InputStream;

public class PropsHolder extends java.util.Properties {

  public PropsHolder(String resource_name) {
    try {
      InputStream is = PropsHolder.class.getResourceAsStream(resource_name);
      this.load(is);
    }
    catch ( java.io.IOException ioe ) {
      ioe.printStackTrace();
      throw new RuntimeException(ioe.toString());
    }
  }
}
