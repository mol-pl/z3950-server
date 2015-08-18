package com.k_int.sql.data_dictionary;

/**
 * PersistenceContext (Bit like a transaction in a relational db)
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.*;
import com.k_int.sql.sql_syntax.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.commons.logging.*;


/**
 * Title:       PersistenceContext
 * @version:    $Id: PersistenceContext.java,v 1.7 2004/11/17 15:31:39 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description:
 *
 */
public class PersistenceContext
{
    Dictionary dictionary;
    private boolean is_clean = true;
    private Map all_objects_by_hashcode = new HashMap();
    private Map object_by_oid = new HashMap();
    String repository_id = null;
    private boolean for_updating;
    private Connection conn=null;

    /** The <code>Log</code> instance for this application.  */
    protected static Log log = LogFactory.getLog(PersistenceContext.class);

    private static int instance_counter=0;

    private DataSource ds = null;
    private SQLDialect dialect;

    public PersistenceContext(Dictionary d, 
		              String repository_id,
                              SQLDialect dialect,
                              Connection conn) {
        this(d, false, repository_id, dialect, conn);
    }

    public PersistenceContext(Dictionary d, 
		              boolean for_updating, 
			      String repository_id,
                              SQLDialect dialect,
                              Connection conn) {
        dictionary = d;
        this.for_updating = for_updating;
        this.repository_id = repository_id;
        this.dialect = dialect;
        this.conn = conn;
    }

    public String getRepositoryId() {
        return repository_id;
    }

    protected void finalize() {
        if ( null != conn )
        {
  	  try
	  {
            conn.close();
          }
          catch ( java.sql.SQLException sqle )
          {
            sqle.printStackTrace();
          }
        }
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public boolean isClean()
    {
        return is_clean;
    }

    public void setClean(boolean flag)
    {
        is_clean = flag;
    }

    public void register(Entity e)
    {
        // System.err.println("Registering new entity "+e);
        all_objects_by_hashcode.put(new Integer(e.hashCode()), e);
    }

    // Commit everything in this context
    public void commit() {
      try {
        for ( Iterator e = all_objects_by_hashcode.values().iterator(); e.hasNext() ;) {
            Entity ent = (Entity) e.next();
            try {
                ent.writeChanges(conn);
            }
            catch ( java.sql.SQLException sqle ) {
                sqle.printStackTrace();
            }
        }

        conn.commit();

        // Get all new rows with non-dependent FK's
        is_clean=true;

        reset();
      }
      catch(Exception e)
      {
        log.warn( e.toString(), e );
      }
    }

    // Rollback everything in this context
    public void rollback() {
      try {
        // Connection conn_to_use = getConn();
        conn.rollback();
        reset();
        is_clean=true;
      }
      catch(Exception e) {
        log.warn( e.toString(), e );
      }
    }

    /**
     * Deprecated: If you use, you **MUST** release the statement yourself using
     * ResultSet.getStatement()....
     */
    public ResultSet executeQuery(String statement, List bindvars) throws java.sql.SQLException {
        ResultSet retval = null;


        // Connection conn_to_use = getConn();
        PreparedStatement pstmt = conn.prepareStatement(statement, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        if ( null != bindvars )
        {
          int counter=1;
          for ( Iterator e = bindvars.iterator(); e.hasNext(); )
            pstmt.setObject(counter++, e.next());
        }

        // System.err.println("Execute: "+statement);
        // N.B. We expect the caller to call close on the returned result set AND
        // the PreparedStatement via ResultSet.getStatement().close();
        retval = pstmt.executeQuery();

        return retval;
    }

    // Create an entity based on the current row in a result set, instead of constructing
    // the SQL to serialise the object from the database
    public Entity constructFromResultSet(String entity_name,
                                         ResultSet source) throws UnknownCollectionException {
        Entity retval = null;

        // System.err.println("PersistenceContext::constructFromResultSet deserialising Entity from result set row");

        // First, build an OID so we can see if this object is already in this context
        // We know what dictionary relates to this repository, and we know the repository id
       
        // Lookup the EntityTemplate for objects of the specified type
        EntityTemplate et = dictionary.lookup(entity_name);

        OID new_oid = null;

        // If Entity has no PK attributes, don't do any of the following guff....
        if ( ( et.getKeyAttrsCollection().size() > 0 ) && ( source != null ) ) {

          // Extract the primary key attributes to the keyattrs string
          EntityKey ek = new EntityKey(et, source);

          new_oid = new OID(repository_id, entity_name, ek);

          // System.err.println("Created oid : "+new_oid.toString()+"... See if object with that OID already available in this context");

          retval= (Entity)object_by_oid.get(new_oid);
        }

        // If retval was not obtained from cache
        if ( null == retval )
        {
            // System.err.println("Need to create the entity from the result set...");
            // retval = new Entity(et, this, source, new_oid);
            retval = Entity.create(et, this, source, new_oid);

            if ( new_oid != null ) {
              object_by_oid.put(new_oid, retval);
            }

            all_objects_by_hashcode.put(new Integer(retval.hashCode()), retval);
        }

        return retval;
    }

    public Entity deserialise(String oid) throws UnknownCollectionException {
        return deserialise(new OID(oid));
    }

    // Create an entity based on a row from a result set, instead of constructing
    // the SQL to serialise the object from the database
    public Entity deserialise(OID oid) throws UnknownCollectionException
    {
        Entity retval = null;

        retval =  (Entity)object_by_oid.get(oid);

        if ( null != retval ) {
            // Maybe freshen the existing entity from result set data? Maybe do some reference counting...
            // System.err.println("The specified item is allready available in this context");
        }
        else {
            // System.err.println("Deserialise entity from persistent storage based on OID:"+oid);

            // We are being asked to construct the SQL to extract the object from the database...
            // Phase one is to build a Select of all primary cols WHERE PK cols = xxx
            SelectStatement stmnt = new SelectStatement();

            EntityTemplate base_et = dictionary.lookup(oid.getCollection());

            String base_table = base_et.getBaseTableName();
            // String base_table_alias = stmnt.addTable(base_table);
            TableScope base_table_alias = stmnt.addTable(base_table);

            // Cycle through all database coll attibutes in this type
            for( Iterator e = base_et.getAttributeDefinitions(); e.hasNext(); ) {
                AttributeDefinition ad = (AttributeDefinition) e.next();

                switch( ad.getType() ) {
                    // A real col in a sql set
                    case AttributeDefinition.DB_COLUMN_ATTR_TYPECODE:
                        // System.err.println("Adding select attr : "+base_table_alias+"."+ad.getAttributeName());
                        stmnt.addField(new ScopedColumnExpression(base_table_alias, ((DatabaseColAttribute)ad).getColName()));
                        break;
                }
            }

            // Now add all the primary keys... REALLY, this should involve cycling through the key attributes
            // from the OID, and looking up
            Map keyattrs = oid.getKey();
            List bindvars = new ArrayList();

            // Will be either the top level sql statement or a logical connector such as AND
            Restrictable r = null;

            switch ( keyattrs.size() )
            {
                case 0:
                    // Fatal error
                    log.warn("Entity has no PK");
                    break;
                case 1:
                    // only one comparison (e.g. where id = 1 )
                    r = stmnt;
                    break;
                default:
                    // There are at lease 2 pk cols ( e.g. where a=1 AND b=2 AND c=3 )
                    ConditionCombination cc = new ConditionCombination("AND");
                    stmnt.addCondition(cc);
                    r=cc;
                    break;
            }

            for( Iterator e2 = keyattrs.keySet().iterator(); e2.hasNext(); ) {
                String keyattr = (String)e2.next();

                r.addCondition( new ComparisonCondition( new ScopedColumnExpression(base_table_alias, keyattr), 
                                                         "=", 
                                                         new BindVariableExpression() ) );

                bindvars.add( keyattrs.get(keyattr) );
            }

            String sql_string = stmnt.toString();
            // System.err.println("Generated Select SQL : "+sql_string);

            try {
              ResultSet rs = executeQuery(sql_string, bindvars);
              if ( rs.first() ) {
                retval = Entity.create(base_et, this, rs, oid);
              }

              Statement s = rs.getStatement();
              rs.close();
              if ( s != null )
                s.close();
            }
            catch(java.sql.SQLException sqle) {
                log.warn("Unable to locate record for supplied OID",sqle);
                sqle.printStackTrace();
            }
            if ( retval != null ) {
              object_by_oid.put(oid, retval);
              all_objects_by_hashcode.put(new Integer(retval.hashCode()), retval);
            }
        }

        return retval;
    }

    // Complete clear out of everything currently in this context...
    public void reset()
    {
        all_objects_by_hashcode.clear();
        object_by_oid.clear();
    }

    public Integer getSeqnoInContext(String seq_name)
    {
      Integer retval = null;

      try
      {
        // Need to add a switch based on jdbc implementation name... Oracle is different to pgsql, etc
        String stmnt = "Select "+seq_name+".nextval from dual";
        ResultSet rs = executeQuery(stmnt, null);
      
        if ( null != rs )
        {
          if ( rs.next() )
            retval = new Integer(rs.getInt(1));

          Statement s = rs.getStatement();
          rs.close();
          if ( s != null )
            s.close();
        }
      }
      catch( Exception e )
      {
        e.printStackTrace();
      }

      return retval;
    }

    public void close()
    {
      if ( !is_clean )
        rollback();

      if ( null != conn )
      {
        try
	{
          conn.close();
	  conn=null;
	}
	catch ( java.sql.SQLException sqle )
	{
          sqle.printStackTrace();
	}
      }

      all_objects_by_hashcode.clear();
      object_by_oid.clear();
    }

    public Entity create(String template_name) throws UnknownCollectionException
    {
      EntityTemplate et = dictionary.lookup(template_name);
      return Entity.create(et,this);
    }

    public SQLDialect getDialect()
    {
      return dialect;
    }

  public Entity deserialise(EntityTemplate base_et, Object keys) throws PersistenceException {
    try {
      return deserialise(new OID(repository_id, base_et.getEntityName(), new EntityKey(base_et, keys)));
    }
    catch ( com.k_int.sql.data_dictionary.UnknownCollectionException uce ) {
      throw new PersistenceException("Unable to get entity",uce);
    }
  }
}
