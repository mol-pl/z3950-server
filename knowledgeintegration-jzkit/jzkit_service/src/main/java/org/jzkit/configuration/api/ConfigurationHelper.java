package org.jzkit.configuration.api;

import java.util.prefs.Preferences;
import java.lang.reflect.Constructor;
import java.util.logging.*;
import java.util.Hashtable;

public class ConfigurationHelper
{
  public static final int SYSTEM_PREF_TYPE = 1;
  public static final int USER_PREF_TYPE = 2;

  public static boolean configExists(int type, String config) throws ConfigurationException
  {
    try {
      Preferences directory_service_root = null;
      if ( type == SYSTEM_PREF_TYPE )
        directory_service_root = Preferences.systemNodeForPackage(Configuration.class);
      else
        directory_service_root = Preferences.userNodeForPackage(Configuration.class);
                                                                                                                                        
      return directory_service_root.nodeExists(config);
    }
    catch ( java.util.prefs.BackingStoreException bse ) {
      throw new ConfigurationException(bse.toString());
    }
  }

  public static Preferences getPrefsNodeFor(int type, String config)
  {
    Preferences result = null;
    Preferences directory_service_root = null;

    if ( type == SYSTEM_PREF_TYPE )
      directory_service_root = Preferences.systemNodeForPackage(Configuration.class);
    else
      directory_service_root = Preferences.userNodeForPackage(Configuration.class);
                                                                                                                                        
    result = directory_service_root.node(config);

    return result;
  }
}
