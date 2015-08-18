package org.jzkit.service;


import org.apache.commons.collections.map.ReferenceMap;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;
import java.util.Map;

/**
 * <code>JZKitServiceFactory</code> is an object factory that when given
 * a reference for a <code>JZKitService</code> object, will create an
 * instance of the corresponding  <code>JZKitService</code>.
 */
public class JZKitServiceFactory implements ObjectFactory {

  /**
   * cache using <code>java.naming.Reference</code> objects as keys and
   * storing soft references to <code>JZKitService</code> instances
   */
  private static Map cache = new ReferenceMap(ReferenceMap.HARD, ReferenceMap.SOFT);

  /**
   * empty default constructor
   */
  public JZKitServiceFactory() {
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
  static JZKitService createInstance(String service_home_dir) throws Exception {
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
          String service_home_dir = (String) ref.get("jzkit_home").getContent();
          return createInstance(service_home_dir);
        }
      }
    }
    return null;
  }
}
