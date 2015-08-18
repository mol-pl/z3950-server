package com.k_int.sql.data_dictionary;

import java.util.List;
import java.beans.*;
import java.net.URL;
import org.springframework.context.*;
import org.hibernate.*;
import org.apache.commons.logging.*;

public class DatabaseConfig implements Dictionary, org.springframework.context.ApplicationContextAware {

  protected static Log log = LogFactory.getLog(DatabaseConfig.class);

  private org.springframework.context.ApplicationContext ctx;
  private org.hibernate.SessionFactory factory = null;

  public DatabaseConfig() {
  }

  public EntityTemplate lookup(String entity_name) throws UnknownCollectionException {
    EntityTemplate result = null;
    Session sess = null;

    try {
      sess = factory.openSession();
      Query q = sess.createQuery("Select x from com.k_int.sql.data_dictionary.EntityTemplate x where x.entityName = ?");
      q.setParameter(0,entity_name,Hibernate.STRING);
      result = (EntityTemplate) q.uniqueResult();
    }
    catch ( org.hibernate.HibernateException he ) {
      he.printStackTrace();
    }
    finally {
      if ( sess != null ) {
        try {
          sess.close();
        }
        catch ( Exception e ) {
        }
      }
    }

    if ( result == null ) {
      log.warn("Failed to lookup EntityTemplate for "+entity_name);
    }

    return result;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public void setSessionFactory(org.hibernate.SessionFactory factory) {
    this.factory = factory;
  }

  public org.hibernate.SessionFactory getSessionFactory() {
    return factory;
  }

}
