package org.jzkit.ServiceDirectory;

import java.util.prefs.Preferences;
import java.lang.reflect.Constructor;
import java.util.logging.*;

public class ServiceDirectoryFactory
{
  private static final String IMPL_PREFS = "Impl";
  private static final String DEFAULT_CONFIG = "Default";
  private static final String IMPL_PREF_NAME = "Service Directory Implementation Class Name";
  private static Logger log = Logger.getLogger(ServiceDirectoryFactory.class.getName());

  public static ServiceDirectory create() throws ServiceDirectoryException
  {
    // Create a new service directory depending upon the preferences
    // set out in the 
    log.info("create... with default config");
    return create(DEFAULT_CONFIG);
  }

  public static ServiceDirectory create(String config) throws ServiceDirectoryException
  {
    log.info("create..."+config);

    Preferences directory_service_root = Preferences.systemNodeForPackage(ServiceDirectoryFactory.class);
    Preferences impl_prefs =  directory_service_root.node(IMPL_PREFS);
    Preferences ds_prefs =  impl_prefs.node(config);

    String impl_class_name = ds_prefs.get(IMPL_PREF_NAME,"org.jzkit.ServiceDirectory.XMLProvider.XMLImpl");

    try
    {
      log.info("create new "+impl_class_name);

      Class retval_class = Class.forName(impl_class_name);
      Class[] param_types = new Class[] { Preferences.class };
      Constructor c = retval_class.getConstructor(param_types);
      Object params[] = new Object[] { ds_prefs };
      ServiceDirectory retval = (ServiceDirectory) c.newInstance(params);
      return retval;
    }
    catch(ClassNotFoundException err )
    {
      err.printStackTrace();
      log.throwing(ServiceDirectoryFactory.class.getName(),"create",err);
      throw new ServiceDirectoryException( err.toString() );
    }
    catch(InstantiationException err )
    {
      err.printStackTrace();
      log.throwing(ServiceDirectoryFactory.class.getName(),"create",err);
      throw new ServiceDirectoryException( err.toString() );
    }
    catch(IllegalAccessException err )
    {
      err.printStackTrace();
      log.throwing(ServiceDirectoryFactory.class.getName(),"create",err);
      throw new ServiceDirectoryException( err.toString() );
    }
    catch( Exception err )
    {
      err.printStackTrace();
      log.throwing(ServiceDirectoryFactory.class.getName(),"create",err);
      throw new ServiceDirectoryException( err.toString() );
    }
  }
}
