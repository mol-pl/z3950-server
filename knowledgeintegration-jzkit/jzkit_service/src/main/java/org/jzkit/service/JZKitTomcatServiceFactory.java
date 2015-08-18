package org.jzkit.service;


import org.apache.commons.collections.map.ReferenceMap;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;
import java.util.Map;

/**
 * Create a JZKitService inside a Tomcat Shared JNDI Namespace, and
 * assume that the JZKitHome is $TOMCAT_HOME/jzkit. This is a special
 * adapter class for JZKit in embedded tomcat applications.
 */
public class JZKitTomcatServiceFactory implements ObjectFactory {

  /**
   * cache using <code>java.naming.Reference</code> objects as keys and
   * storing soft references to <code>JZKitService</code> instances
   */
  private static Map cache = new ReferenceMap(ReferenceMap.HARD, ReferenceMap.SOFT);

  /**
   * empty default constructor
   */
  public JZKitTomcatServiceFactory() {
  }

  /**
   * Creates an initialized JZKitService instance using the given
   * configuration information and puts it in {@link #cache}.
   *
   * @param configFilePath repository configuration file path
   * @param repHomeDir     repository home directory path
   * @return initialized jzkit instance
   * @throws RepositoryException if the repository cannot be created
   */
  static JZKitService createInstance() throws Exception {

    String service_home_dir = java.lang.System.getProperty("org.jzkit.home");

    if ( ( service_home_dir == null ) || ( service_home_dir.length() == 0 ) )
      throw new RuntimeException("No catalina.base defined");

    JZKitService jz = JZKitService.create(service_home_dir);
    cache.put(jz.getReference(), jz);
    return jz;
  }


  /**
   * {@inheritDoc}
   */
  public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception {
    if (obj instanceof Reference) {
      Reference ref = (Reference) obj;
      synchronized (cache) {
        if (cache.containsKey(ref)) {
          return cache.get(ref);
        } else {
          return createInstance();
        }
      }
    }
    return null;
  }
}
