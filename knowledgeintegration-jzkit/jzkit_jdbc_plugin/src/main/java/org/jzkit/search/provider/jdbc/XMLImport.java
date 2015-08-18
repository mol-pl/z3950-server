package org.jzkit.search.provider.jdbc;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.logging.*;
import java.util.Iterator;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.*;
import org.hibernate.*;

/**
 * Title:
 * Description:     
 * Copyright:       
 *                  
 * Company:         
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: RecordTemplatesInMemoryConfig.java,v 1.2 2004/10/28 12:31:41 ibbo Exp $
 */
public class XMLImport {
  public static void main(String[] args) {
    if ( args.length < 1 ) {
      System.err.println("Usage : ImportXMLConfig app_context_def.xml...");
      System.exit(0);
    }
    else {

      String application_context_resource = args[0];
      ApplicationContext app_context = new ClassPathXmlApplicationContext( args );

      Session sess = null;
      SessionFactory factory = (SessionFactory) app_context.getBean("INodeSessionFactory");

      try {
        sess = factory.openSession();

        // ImportXMLConfig svc = new ImportXMLConfig();
        // svc.importConfig((Configuration)app_context.getBean("JZKitConfig"),args[1]);
        com.k_int.sql.data_dictionary.InMemoryConfig dictionary = (com.k_int.sql.data_dictionary.InMemoryConfig)
             com.k_int.sql.data_dictionary.InMemoryConfig.getConfig("/iNodeDataDictionary.xml");
        com.k_int.sql.qm_to_sql.QMToSQLInMemoryConfig qm_to_sql_config = (com.k_int.sql.qm_to_sql.QMToSQLInMemoryConfig)
             com.k_int.sql.qm_to_sql.QMToSQLInMemoryConfig.getConfig("/iNodeAccessPoints.xml");
        RecordTemplatesInMemoryConfig templates = (RecordTemplatesInMemoryConfig)
             RecordTemplatesInMemoryConfig.getConfig("/iNodeTemplates.xml");
  
        System.err.println("Loaded in memory config... saving to database");
        System.err.println("Processing dictionary");
        // Dictionary Entries
        for ( java.util.Iterator i = dictionary.getAllDefs(); i.hasNext(); ) {
          com.k_int.sql.data_dictionary.EntityTemplate et = (com.k_int.sql.data_dictionary.EntityTemplate)i.next();
          System.err.println("Processing entity template: "+et.getEntityName()+" base tab="+et.getBaseTableName());
          sess.save(et);
          sess.flush();
        }

        // Database Name Mappings
        for ( java.util.Iterator i3 = qm_to_sql_config.getDatabaseMappings(); i3.hasNext(); ) {
          com.k_int.sql.qm_to_sql.DatabaseMapping dm = (com.k_int.sql.qm_to_sql.DatabaseMapping) i3.next();
          System.err.println("Processing database mapping : "+dm.getPublicName());
          sess.save(dm);
          sess.flush();
        }

        // Entity Attr Maps
        for ( java.util.Iterator i2 = qm_to_sql_config.getEntityAttrMaps(); i2.hasNext(); ) {
          com.k_int.sql.qm_to_sql.AttrMap am = (com.k_int.sql.qm_to_sql.AttrMap)i2.next();
          System.err.println("Processing attr map for entity : "+am.getBaseEntityName());
          sess.save(am);
          sess.flush();
        }

        // Templates
        for ( java.util.Iterator i4 = templates.getTemplates(); i4.hasNext(); ) {
          JDBCCollectionMappingInfo temp = (JDBCCollectionMappingInfo) i4.next();
          System.err.println("Processing Entity mapping: "+temp.getEntityName());
          sess.save(temp);
          sess.flush();
        }

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
  }
}
