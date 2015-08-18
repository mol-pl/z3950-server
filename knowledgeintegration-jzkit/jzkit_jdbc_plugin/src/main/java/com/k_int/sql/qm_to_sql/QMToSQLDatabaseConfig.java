package com.k_int.sql.qm_to_sql;

import java.util.*;
import java.beans.*;
import java.net.URL;
import org.springframework.context.*;
import org.hibernate.*;

/**
 * QMToSQLInDatabaseConfig
 * Title:
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: QMToSQLInMemoryConfig.java,v 1.5 2004/10/31 12:21:22 ibbo Exp $
 */
public class QMToSQLDatabaseConfig implements QMToSQLConfig, org.springframework.context.ApplicationContextAware {

  private org.springframework.context.ApplicationContext ctx;
  private org.hibernate.SessionFactory factory = null;

  public DatabaseMapping lookupDatabaseMapping(String public_collection_id) {
    DatabaseMapping result = null;
    Session sess = null;
    try {
      sess = factory.openSession();
      Query q = sess.createQuery("Select x from com.k_int.sql.qm_to_sql.DatabaseMapping x where x.publicName = ?");
      q.setParameter(0,public_collection_id,Hibernate.STRING);
      result = (DatabaseMapping) q.uniqueResult();
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
    return result;
  }

  public AttrMap lookupAttrMap(String entity_name) {
    AttrMap result = null;
    Session sess = null;
    try {
      sess = factory.openSession();
      Query q = sess.createQuery("Select x from com.k_int.sql.qm_to_sql.AttrMap x where x.baseEntityName = ?");
      q.setParameter(0,entity_name,Hibernate.STRING);
      result = (AttrMap) q.uniqueResult();
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
    return result;
  }

  public void addDatabaseMapping(String public_collection_name,
                                 String base_entity_name,
                                 String collection_code) {
    Session sess = null;
    try {
      sess = factory.openSession();
      DatabaseMapping dm = new DatabaseMapping(public_collection_name,base_entity_name,collection_code);
      sess.save(dm);
      sess.flush();
      sess.connection().commit();
    }
    catch ( org.hibernate.HibernateException he ) {
      he.printStackTrace();
    }
    catch ( java.sql.SQLException sqle ) {
      sqle.printStackTrace();
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
