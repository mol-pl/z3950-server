package org.jzkit.search.impl;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
*/
public class LRUCache extends Thread {

  private final float hashTableLoadFactor = 0.75f;
  private Map map;
  private int cacheSize=10;
  private long evict_timeout=0;
  private static Log log = LogFactory.getLog(LRUCache.class);
  private boolean running = true;

  /**
   * Creates a new LRU cache.
   * @param cacheSize the maximum number of entries that will be kept in this cache.
   */
  public LRUCache (int cacheSize) {
    this(cacheSize,0);
  }

  public LRUCache (int cacheSize, long evict_timeout) {

    log.debug("LRUCache::LRUCache("+cacheSize+","+evict_timeout+")");

    this.cacheSize = cacheSize;
    this.evict_timeout = evict_timeout;
    int hashTableCapacity = (int)Math.ceil(cacheSize / hashTableLoadFactor) + 1;

    map = Collections.synchronizedMap(new LinkedHashMap(hashTableCapacity, hashTableLoadFactor, true) {
       private static final long serialVersionUID = 1;
       protected boolean removeEldestEntry (Map.Entry eldest) {
         if ( ( size() > LRUCache.this.cacheSize ) && ( eldest.getValue() != null ) ) {
           log.info("** Cache size exceeds max, evict oldest **");
           // Close the session
           // SearchSession sess = (SearchSession)eldest... etc
           if ( eldest.getValue() instanceof CachedSearchSession ) {
             ((CachedSearchSession)eldest.getValue()).setActive(false);
             log.info("** EVICT: Closing eldest search session "+eldest+" **");
             ((CachedSearchSession)eldest.getValue()).close();
           }
           else {
             log.info("** EVICT: cant evice eldest instance of "+eldest.getValue().getClass().getName());
           }
           return true;
         }
         
         return false;
       }
    }); 

    if ( evict_timeout > 0 ) {
       log.debug("Starting evict thread");
       this.setDaemon(true);
       this.setName("Cached Search Session Expiry Thread");
       this.start();
    }
    else {
      log.info("No LRU evict thread");
    }
  }

  public int size() {
    if ( map != null ) 
      return map.size();
    return 0;
  }

  public void run() {
    log.debug("Starting LRU expunge thread");

    while(running) {
      try {
        this.sleep(60000);
        expunge();
      }
      catch ( java.lang.InterruptedException ie ) {
        ie.printStackTrace();
      }
      finally {
      }
    }
  }

  public synchronized void expunge() {
    log.debug("Running Expunge.  there are currently "+map.size()+" cached search sessions. Free Memory = "+Runtime.getRuntime().freeMemory());
    long now = System.currentTimeMillis();

    Object[] keys = map.keySet().toArray();

    for ( int i=0; i<keys.length; i++ ) {
      Object key = keys[i];
      Object o = map.get(key);
      if ( o instanceof CachedSearchSession ) {
        CachedSearchSession css = (CachedSearchSession) o;
        long inactive_for = (long)(now - css.getLastUsed());
        if ( inactive_for > evict_timeout ) {
          log.debug("Evict "+key+" - Inactive for "+inactive_for+" > "+evict_timeout);
          css.setActive(false);
          map.remove(key);
          css.close();
        }
      }
    }
    log.debug("After expunge there are "+map.size()+" cached search sessions, with "+Runtime.getRuntime().freeMemory()+" free memory");
    log.debug("CachedSearchSession instance count = "+CachedSearchSession.inst_count);
  }

  /**
   * Retrieves an entry from the cache.<br>
   * The retrieved entry becomes the MRU (most recently used) entry.
   * @param key the key whose associated value is to be returned.
   * @return    the value associated to this key, or null if no value with this key exists in the cache.
   */
  public synchronized Object get (Object key) {
    return map.get(key); 
  }

  /**
   * Adds an entry to this cache.
   * If the cache is full, the LRU (least recently used) entry is dropped.
   * @param key    the key with which the specified value is to be associated.
   * @param value  a value to be associated with the specified key.
   */
  public synchronized void put (Object key, Object value) {
    map.put (key,value); 
  }

  public synchronized void remove (Object key) {
    CachedSearchSession css = (CachedSearchSession) map.remove (key);
    if ( css != null ) {
      css.setActive(false);
      css.close();
    }
  }

  /**
   * Clears the cache.
   */
  public synchronized void clear() {
    map.clear(); 
  }

  /**
   * Returns the number of used entries in the cache.
   * @return the number of entries currently in the cache.
   */
  public synchronized int usedEntries() {
    return map.size(); 
  }

  /**
   * Returns a <code>Collection</code> that contains a copy of all cache entries.
   * @return a <code>Collection</code> with a copy of the cache content.
   */
  public synchronized Collection getAll() {
    return new ArrayList(map.entrySet()); 
  }
}
