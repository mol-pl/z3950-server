package org.jzkit.search.provider.jdbc;

import org.springframework.context.*;
import org.hibernate.*;
import java.util.*;

/**
 * Title:
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id$
 */
public class RecordTemplatesDatabaseConfig implements RecordTemplatesConfig, org.springframework.context.ApplicationContextAware {

  private org.springframework.context.ApplicationContext ctx;
  private org.hibernate.SessionFactory factory = null;

  public RecordTemplatesDatabaseConfig() {
  }

  public JDBCCollectionMappingInfo lookupEntityTemplateMappingInfo(String entity_name) {
    JDBCCollectionMappingInfo result = null;
    Session sess = null;
    try {
      sess = factory.openSession();
      Query q = sess.createQuery("Select x from org.jzkit.search.provider.jdbc.JDBCCollectionMappingInfo x where x.entityName = ?");
      q.setParameter(0,entity_name,Hibernate.STRING);
      result = (JDBCCollectionMappingInfo) q.uniqueResult();
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
