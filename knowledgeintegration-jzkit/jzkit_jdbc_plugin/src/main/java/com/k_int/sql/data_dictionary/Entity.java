package com.k_int.sql.data_dictionary;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import com.k_int.sql.sql_syntax.*;
import java.sql.*;
import org.apache.commons.logging.*;
import java.lang.reflect.Constructor;

/**
 * Title:       Entity
 * @version:    $Id: Entity.java,v 1.6 2004/10/28 10:11:57 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description:
 */
public abstract class Entity
{
    private EntityTemplate entity_metadata=null;      // Pointer to entity metadata
    private Object attribute_values[];         // Actual attributes
    private boolean dirty_field_flags[];       // Has the value been modified
    private boolean fetched_from_db_flags[];   // So we can tell if something is null, or just not feteched from the DB yet
    private boolean is_null_flags[];           // Is the attribute null

    private boolean is_modified = false;

    public static final int PERSISTENT_DATA_NEW=0;
    public static final int PERSISTENT_DATA_CLEAN=1;
    public static final int PERSISTENT_DATA_DIRTY=2;
    public static final int PERSISTENT_DATA_DELETED=3;
    private int persistence_state=PERSISTENT_DATA_NEW;

    public static final int DATA_READONLY=0;
    public static final int DATA_WRITEABLE=1;
    private int update_state=0;

    private int num_attrs=0;

    private OID oid = null;

    protected static Log log = LogFactory.getLog(Entity.class);

    // This will replace conn in time...
    PersistenceContext ctx = null;

    //
    // Create a new empty entity
    //
    public Entity(EntityTemplate entity_metadata, PersistenceContext ctx) {
        this.entity_metadata = entity_metadata;
        this.ctx = ctx;

        // this.oid will remain null until values have been set for this entity....

        if ( null != ctx )
            ctx.register(this);

        num_attrs = entity_metadata.getNumAttributes();

        attribute_values = new Object[num_attrs];
        dirty_field_flags = new boolean[num_attrs];
        fetched_from_db_flags = new boolean[num_attrs];
        is_null_flags = new boolean[num_attrs];

        for ( int i = 0; i < num_attrs; i++ ) {
            dirty_field_flags[i]=false;
            fetched_from_db_flags[i]=false;
            is_null_flags[i]=true;
        }
    }

    // Create an entity based on a row in the database
    //
    public Entity(EntityTemplate entity_metadata, PersistenceContext ctx, ResultSet rs, OID oid) {

        this(entity_metadata, ctx);

        persistence_state=PERSISTENT_DATA_CLEAN;

        // Use data in rs to initially populate the entity...
        // Cycle through elements in the entity_metadata seeing if they are present in the result set
        for ( Iterator e = entity_metadata.getAttributeDefinitions(); e.hasNext(); ) {

            AttributeDefinition ad = (AttributeDefinition)e.next();

            if ( ad instanceof DatabaseColAttribute) {
                // Attribute represents a real database attribute
                DatabaseColAttribute dca = (DatabaseColAttribute)ad;

                // System.err.println("Trying to process "+dca.getAttributeName()+"-"+dca.getColName());

                try {
                    switch(dca.getSQLTypeCode()) {
                        case java.sql.Types.ARRAY:
                            break;

                        case java.sql.Types.BIGINT:
                            set(dca.getAttributeName(), new Integer(rs.getInt(dca.getColName())), false, true);
                            break;

                        case java.sql.Types.BINARY:
                            break;

                        case java.sql.Types.BIT:
                            set(dca.getAttributeName(), rs.getBytes(dca.getColName()), false, true);
                            break;
                            
                        case java.sql.Types.BLOB:
                            set(dca.getAttributeName(), rs.getBytes(dca.getColName()), false, true);
                            break;
                            
                        case java.sql.Types.BOOLEAN:
                            set(dca.getAttributeName(), new Boolean(rs.getBoolean(dca.getColName())), false, true);
                            break;
                            
                        case java.sql.Types.CHAR:
                            break;
                            
                        case java.sql.Types.DECIMAL:
                            set(dca.getAttributeName(), rs.getBigDecimal(dca.getColName()), false, true);
                            break;
                            
                        case java.sql.Types.DOUBLE:
                            break;
                            
                        case java.sql.Types.FLOAT:
                            break;

                        case java.sql.Types.VARCHAR:
                            set(dca.getAttributeName(), rs.getString(dca.getColName()), false, true);
                            break;

                        case java.sql.Types.INTEGER:
                            set(dca.getAttributeName(), new Integer(rs.getInt(dca.getColName())), false, true);
                            break;

                        case java.sql.Types.DATE:
                            set(dca.getAttributeName(), rs.getDate(dca.getColName()), false, true);
                            break;

                        case java.sql.Types.TIME:
                            break;

                        case java.sql.Types.NUMERIC:
                            break;

                        case java.sql.Types.SMALLINT:
                            break;

                        case java.sql.Types.TIMESTAMP:
                            set(dca.getAttributeName(), rs.getTimestamp(dca.getColName()), false, true);
                            break;

                        default:
                            set(dca.getAttributeName(), rs.getString(dca.getColName()), false, true);
                            break;
                    }
                } 
                catch ( java.sql.SQLException sqle ) {
                  log.error("SQL Exception",sqle);
                  sqle.printStackTrace();
                }
                catch ( UnknownAccessPointException uape ) {
                  log.error("Unknown Access Point",uape);
                  uape.printStackTrace();
                }
            }
        }

        if ( null != oid )
            this.oid = oid;
        else
            regenerateOID();
    }

    //
    // Pull an entity out of the persistent store based on it's OID
    //
    // private Entity(PersistenceContext ctx, OID oid)
    // {
        // Related to ctx.deserialise
    // }

    protected void finalize() throws Throwable {
    }

    // Put the record into update mode (update_state -> WRITEABLE)
    public boolean Update() {
        return true;
    }

    // This function is called to ensure that any database col attributes
    // which are foreign keys for a related record are in sync with the
    // pseudo reference attribute
    private void setLocalAttrsForRelatedEntity(DatabaseLinkAttribute dla, Entity related_entity) {
      try {
        for ( Iterator e = dla.getCorrespondingKeyPairs(); e.hasNext(); ) {
          CorrespondingKeyPair ckp = (CorrespondingKeyPair)e.next();
          set(ckp.getLocalAttrName(), related_entity.get(ckp.getRelatedAttrName()));
        }
      }
      catch ( UnknownAccessPointException uape )
      {
        // If this happens, things are really screwed
        uape.printStackTrace();
      }
    }

    public void set(String path, Object value) throws UnknownAccessPointException
    {
      set(path,value,true,false);
    }

    // Mark_dirty is available so that internal functions (E.G. whilst serialising from DB) can
    // set values without changing the dirty status of the attribute/record
    public void set(String path, 
                    Object value, 
                    boolean mark_dirty, 
                    boolean from_database) 
                    throws UnknownAccessPointException
    {
        String first_attr_name;
        StringTokenizer st = new StringTokenizer(path,".[",true);

        try
        {
            if ( st.hasMoreTokens() )
            {
                first_attr_name = st.nextToken();
                if ( st.hasMoreTokens() )
                {
                    // Must be dealing with related entity or a collection
                    String delim = st.nextToken();
                    // if ( delim.equals(".") )
                    // {
                    //     System.err.println("Accessing a related entity");
                    // }
                    // else if ( delim.equals("[") )
                    // {
                    //     System.err.println("Accessing a collection element");
                    // }
                    // else
                    // {
                    //     System.err.println("Unable to parse string....");
                    // }
                }
                else
                {
                    // System.err.println("Looking up position for attribute "+first_attr_name);

                    int pos = entity_metadata.lookupPosition(first_attr_name);
                    AttributeDefinition ad = entity_metadata.getAttributeDefinition(first_attr_name);

                    // System.err.println("Simple base attribute "+first_attr_name);
                    // Nice and easy standard attribute.. Lookup the definition in the entity_metadata
                    // Need to compare base type and object type
                    // System.err.println("Array Position for that attribute is : "+pos);


                    // Somthing has changed
                    if ( null != ctx )
                        ctx.setClean(false);

                    switch ( ad.getType() )
                    {     
                        case AttributeDefinition.LINK_ATTR_TYPECODE:
                            // N.B. We do not set the dirty attribute on reference links. When we
                            // set the value for this attribute, the appropriate primary key cols
                            // from the related entity will overwrite the fk cols in this entity.
                            // Those attributes will be marked as dirty. We have a convention that
                            // marking the link dirty indicates changed reference data in the
                            // related entity, which directs the context to look for changes in 
                            // the related record.. Don't set dirty on ref attributes
                            // System.err.println("Set reference attribute: Hope we have an OID");
                            if ( value instanceof OID )
                            {
                                Entity e = ctx.deserialise((OID)value);

                                // Set the actual pointer to the related record
                                attribute_values[pos] = e;

                                if ( from_database )
                                  fetched_from_db_flags[pos] = true;

                                // As well as setting the reference itself, we must also
                                // update any foreign keys in this entity that point to the
                                // related record
                                setLocalAttrsForRelatedEntity((DatabaseLinkAttribute)ad, e);

                                is_null_flags[pos]=false;
                            }
                            else
                            {
                                log.error("Warning... Set reference attribute was not supplied an OID:"+value);
                            }
                            break;
                        case AttributeDefinition.COLLECTION_ATTR_TYPECODE:
                            log.error("ERROR: We should never set a collection attr directly");
                            break;
                        case AttributeDefinition.DB_COLUMN_ATTR_TYPECODE:
                            if ( mark_dirty == true )
                            {
                                dirty_field_flags[pos]=true;
                                is_modified = true;
                            }

                            if ( from_database )
                              fetched_from_db_flags[pos] = true;

                            is_null_flags[pos]=false;
                            attribute_values[pos]=value;


                            break;
                    }
                }
            }
        }
        catch ( UnknownCollectionException uce )
        {
            uce.printStackTrace();
        }
    }

    /**
     * Generate a set of name-value pairs representing the PK of a related record based
     * on a foreign key defined in this entity. For example, if we are processing a Book
     * entity and the Book entity has a "Status-Code" attribute which is a foreign key
     * linking to a related "StatusCode" via an ID attr table then this function will generate 
     * a Key expression of ID={Value of Status-Code} which can be used to retrieve the related
     * record.
     */
    public EntityKey getRelatedRecordKeys(DatabaseLinkAttribute dla) throws com.k_int.sql.data_dictionary.UnknownCollectionException {
        // Task is to cycle through all CorrespondingKeyPair(s) and add appopriate components
        EntityKey retval = new EntityKey();

        try {
            for ( Iterator e = dla.getCorrespondingKeyPairs(); e.hasNext(); ) {
                CorrespondingKeyPair ckp = (CorrespondingKeyPair)e.next();

                // System.err.println("Looking up local attr "+ckp.getLocalAttrName());
                // System.err.println("Looking up related attr "+ckp.getRelatedAttrName());

                EntityTemplate related_entity_metadata = ctx.getDictionary().lookup(dla.getRelatedEntityName());
                Object keyval = get(ckp.getLocalAttrName());

                if ( null == keyval ) {
                    // A component of the primary key is NULL... Therefor, there is
                    // no related record
                    return null;
                }
                else {
                    // We need to resolve the local attr name into a real database column name
                    retval.addKeyComponent(related_entity_metadata.getRealColumnName(ckp.getRelatedAttrName()), keyval);
                }
            }
        }
        catch ( UnknownAccessPointException uape )
        {
            uape.printStackTrace();
            return null;
        }

        // System.err.println("getRelatedRecordKeys returning "+retval);

        return retval;
    }

    // Generate name value pairs for a foreign key based on this entities primary key
    //
    // Same as above, but used to generate values matching FK cols in a related table,
    // For eample, this entity is an ORDER and the ORDERLINE has FK table ttribute
    // OL_O_ID ( but PK OL_O_ID, OL_ROW ) then this function will generate the key
    // OL_O_OD = (Primary key for this entity ).
    // N.B. This key will return a set of records who's FK matches this PK, in this
    // case, all the order lines associated with this order.
    public EntityKey getForeignKey(DatabaseLinkAttribute dla) throws com.k_int.sql.data_dictionary.UnknownCollectionException {
        // Task is to cycle through all CorrespondingKeyPair(s) and add appopriate components
        EntityKey retval = new EntityKey();

        try {
            for ( Iterator e = dla.getCorrespondingKeyPairs(); e.hasNext(); ) {
                CorrespondingKeyPair ckp = (CorrespondingKeyPair)e.next();

                // log.debug("Looking up value for foreign key : "+ckp);

                Object keyval = get(ckp.getLocalAttrName());

                EntityTemplate related_entity_metadata = ctx.getDictionary().lookup(dla.getRelatedEntityName());
                if ( ( null == keyval ) || ( related_entity_metadata == null ) ) {
                    log.warn("A component of our own primary key "+keyval+" is NULL or unable to lookup the related entity ("+dla.getRelatedEntityName()+")... ERROR!");
                    return null;
                }
                else {
                    // We need to discover the column name of the related attribute, but in order to
                    // do that we need to get hold of the type information for the related entity
                    retval.addKeyComponent(related_entity_metadata.getRealColumnName(ckp.getRelatedAttrName()), keyval);
                }
            }
        }
        catch ( UnknownAccessPointException uape )
        {
            log.error("Unknown access point ",uape);
            return null;
        }

        // log.debug("getForeignKey returning "+retval);
        return retval;
    }

    public RelatedRecordCollection getCollectionAttr(String attrname) 
                        throws UnknownAccessPointException
    {
        int pos = entity_metadata.lookupPosition(attrname);
        RelatedRecordCollection retval = (RelatedRecordCollection)attribute_values[pos];

        try
        {

            if ( fetched_from_db_flags[pos] == false )
            {
                CollectionAttribute ca = (CollectionAttribute)entity_metadata.getAttributeDefinition(pos);

                // Loop through Components of the fk/pk relationship
                // Need to pull related records into persistence context...

                if ( persistence_state==PERSISTENT_DATA_NEW )
                {
                    // Because this is a new record, there are no primary keys
                    // which can relate other entities to this one yet, so
                    // create an empty collection
                    retval = new RelatedRecordCollection(this, 
                                                         ca.getRelatedEntityName(),
                                                         ca);
                }
                else
                {
                    // Construct the collection, pulling in any related records
                    retval = new RelatedRecordCollection(this, 
                                                         ca.getRelatedEntityName(), 
                                                         getForeignKey((DatabaseLinkAttribute)ca),
                                                         ca);
                }

                fetched_from_db_flags[pos] = true;
                attribute_values[pos] = retval;
            }
        }
        catch( UnknownCollectionException uce)
        {
            uce.printStackTrace();
        }


        return retval;
    }

    public Entity getReferenceAttr(String attrname) throws UnknownAccessPointException {
        int pos = entity_metadata.lookupPosition(attrname);
        AttributeDefinition ad = entity_metadata.getAttributeDefinition(pos);
        // AttributeDefinition ad = entity_metadata.getAttributeDefinition(attrname);
        Entity retval = null;

        try {
            switch ( ad.getType() ) {
                case AttributeDefinition.LINK_ATTR_TYPECODE:
                    // It's a single reference attribute
                    // First thing to do is to see if it's allready been deserialised
                    if ( fetched_from_db_flags[pos] == true ) {
                        retval = (Entity)attribute_values[pos];
                    }
                    else {
                        EntityKey ek = getRelatedRecordKeys((DatabaseLinkAttribute)ad);
    
                        // If there are no foreign key values, don't try and de-serialise
                        if ( null != ek ) {
                            // We are going to need to deserialise this related data, so
                            // build the OID
                            OID related_record = new OID(ctx.getRepositoryId(), 
                                                         ((DatabaseLinkAttribute)ad).getRelatedEntityName(),
                                                         ek);

                            retval = ctx.deserialise(related_record);
                            attribute_values[pos] = retval;
                        }
                        fetched_from_db_flags[pos] = true;
                    }
                    break;
                case AttributeDefinition.COLLECTION_ATTR_TYPECODE:
                    break;
                case AttributeDefinition.DB_COLUMN_ATTR_TYPECODE:
                    // A bad error, 'cus right now, there is no reason
                    // for a . to follow a column name attribute
                    break;
            }
        }
        catch( UnknownCollectionException uce)
        {
            uce.printStackTrace();
        }

        return retval;
    }

    public Object get(String path) throws UnknownAccessPointException
    {
        StringTokenizer st = new StringTokenizer(path,".[]",true);
        return get(st);
    }

    public Object get(StringTokenizer path) throws UnknownAccessPointException {
        Object retval = null;
        String first_attr_name;

        if ( path.hasMoreTokens() ) {
            first_attr_name = path.nextToken();
            // System.err.println("Looking up position for attribute "+first_attr_name);

            int pos = entity_metadata.lookupPosition(first_attr_name);

            // Need to know if we are dealing with related entity or a collection
            AttributeDefinition ad = entity_metadata.getAttributeDefinition(pos);

            if ( path.hasMoreTokens() ) {
                String delim = path.nextToken();
                if ( delim.equals(".") ) {
                    if ( ad instanceof ImportedKeyAttribute) {
                        Entity ref = getReferenceAttr(first_attr_name);
                        if ( null != ref )
                            retval = ref.get(path);
                        else
                            return null;
                    }
                    else if ( ad instanceof CollectionAttribute ) {
                        // This should not normally happen, we would usually access a collection
                        // attributes members via the [] delimiter, may be asking for a psudo
                        // attribute like _size
                        RelatedRecordCollection ref = getCollectionAttr(first_attr_name);
                        return ref.getPseudoAttr(path.nextToken());
                    }
                    else {
                        log.error("FATAL Error, unknown attribute type, may be structured");
                    }
                }
                else if ( delim.equals("[") )
                {
                    // System.err.println("Accessing a collection element");
                    if ( ad instanceof CollectionAttribute )
                    {
                        String rownum = path.nextToken();
                        RelatedRecordCollection ref = getCollectionAttr(first_attr_name);
                        Entity e = ref.get(rownum);
                        path.nextToken(); // Consume ]

                        if ( path.hasMoreTokens() )
                        {
                          // There are field elements after the [index] entry
                          path.nextToken(); // Consume .
                       
                          if ( null != e )
                              return e.get(path);
                          else
                              return null;
                        }
                        else
                        {
                          // Hopefully, we are returning the entity at [index]
                          return e;
                        }
                    }
                }
                else
                {
                    log.error("Unable to parse access path string....");
                }
            }
            else
            {
                retval = attribute_values[pos];

                if ( null == retval )
                {
                  if ( ad instanceof CollectionAttribute )
                  {
                      // We are asked for a collection attribute and it's probably not yet
                      // been fetched from the database
                      retval=getCollectionAttr(first_attr_name);
                  }
                  else if ( ad instanceof DatabaseLinkAttribute )
                  {
                    if ( fetched_from_db_flags[pos] == false ) {
                      retval = getReferenceAttr(first_attr_name);
                    }
                  }
                }
            }
        }

        return retval;
    }

    public boolean writeChanges(Connection conn) throws SQLException
    {
        boolean retval = false;

        if ( is_modified )
        {
            SQLStatement stmnt = null;
    
            switch(persistence_state)
            {
                case PERSISTENT_DATA_NEW:
                    stmnt = new InsertStatement();
                    retval = performInsert(conn);
                    break;
                case PERSISTENT_DATA_DELETED:
                    stmnt = new DeleteStatement();
                    retval = performDelete(conn);
                    break;
                default:
                    stmnt = new UpdateStatement();
                    retval = performUpdate(conn);
                    break;
            }
        }
        else
        {
            // System.err.println("No changes to entity with OID : "+oid);
        }

        return retval;
    }

    public boolean performInsert(Connection conn) throws SQLException
    {
        InsertStatement stmnt = new InsertStatement();
        InsertValuesClause ivc = new InsertValuesClause();
        stmnt.setInsertDataClause(ivc);

        List bind_vars = new ArrayList();
        // entity_metadata.dumpAttributes();

        // String alias = stmnt.addTable(entity_metadata.getBaseTableName());
        TableScope alias = stmnt.addTable(entity_metadata.getBaseTableName());

        // We need to work out what changes to make....
        for ( int i = 0; i < num_attrs; i++ ) {
            AttributeDefinition ad = entity_metadata.getAttributeDefinition(i);

            if ( dirty_field_flags[i] )
            {
                switch ( ad.getType() )
                {
                    case AttributeDefinition.LINK_ATTR_TYPECODE:
                        // If this is set it means we have changed some reference data within the
                        // related attribute
                        break;
                    case AttributeDefinition.COLLECTION_ATTR_TYPECODE:
                        break;
                    case AttributeDefinition.DB_COLUMN_ATTR_TYPECODE:
                    {
                        DatabaseColAttribute dca = (DatabaseColAttribute)ad;

                        Object value = attribute_values[i];

			if ( value == null )
			{
                          // We might need to set a default attribute
			  String def_expr_type = dca.getAttrProps().getProperty("DefaultExpressionType");

			  if ( def_expr_type != null )
			  {
                            if ( def_expr_type.equals("SeqNoExpression") )
			    {
                              String seq_name = dca.getAttrProps().getProperty("SeqName");
                              value = ctx.getDialect().getNextSeqnoExpression(seq_name);
		    	    }
			  }
			}

                        // Insert an expression
			if ( value instanceof com.k_int.sql.sql_syntax.Expression )
			{
                          stmnt.addInsertColumnSpec(new ScopedColumnExpression(alias, entity_metadata.getBaseAttrColName(i)));
                          ivc.addValueAssignment((Expression)value);
			}
			else // Insert a value
			{
                            stmnt.addInsertColumnSpec(new ScopedColumnExpression(alias, entity_metadata.getBaseAttrColName(i)));
                            ivc.addValueAssignment(new BindVariableExpression());
                            bind_vars.add(value);
                        }
                        break;
		    }
                }
            }
	    else // The field has not been modified, but we may have a default...
	    {
                switch ( ad.getType() )
                {
                    case AttributeDefinition.DB_COLUMN_ATTR_TYPECODE:
                    {
                        DatabaseColAttribute dca = (DatabaseColAttribute)ad;

                        Object value = attribute_values[i];

			if ( value == null )
			{
                          // We might need to set a default attribute
			  String def_expr_type = dca.getAttrProps().getProperty("DefaultExpressionType");

			  if ( def_expr_type != null )
			  {
                            if ( def_expr_type.equals("SeqNoExpression") )
			    {
                              String seq_name = dca.getAttrProps().getProperty("SeqName");
                              // value = ctx.getStatementFactory().getNextSeqnoExpression(seq_name);
                              value = ctx.getDialect().getNextSeqnoExpression(seq_name);
                              stmnt.addInsertColumnSpec(new ScopedColumnExpression(alias, entity_metadata.getBaseAttrColName(i)));
                              ivc.addValueAssignment((Expression)value);
		    	    }
			  }
			}

		        break;
		    }
	        }
	    }
        }

        String sql_string = stmnt.toString();
        log.debug("Generated SQL : "+sql_string);

        PreparedStatement ps = conn.prepareStatement(sql_string);
       
        int i_count = 1; 
        for (Iterator e = bind_vars.iterator() ; e.hasNext() ; )
            ps.setObject(i_count++, e.next());
            
        ps.close();
        return true;
    }

    public boolean performUpdate(Connection conn) throws SQLException
    {
        UpdateStatement stmnt = new UpdateStatement();

        List bind_vars = new ArrayList();
        // entity_metadata.dumpAttributes();

        // String alias = stmnt.addTable(entity_metadata.getBaseTableName());
        TableScope alias = stmnt.addTable(entity_metadata.getBaseTableName());

        // We need to work out what changes to make....
        for ( int i = 0; i < num_attrs; i++ )
        {
            AttributeDefinition ad = entity_metadata.getAttributeDefinition(i);

            if ( dirty_field_flags[i] )
            {
                switch ( ad.getType() )
                {
                    case AttributeDefinition.LINK_ATTR_TYPECODE:
                        // If this is set it means we have changed some reference data within the
                        // related attribute
                        break;
                    case AttributeDefinition.COLLECTION_ATTR_TYPECODE:
                        break;
                    case AttributeDefinition.DB_COLUMN_ATTR_TYPECODE:
                    {
                        DatabaseColAttribute dca = (DatabaseColAttribute)ad;

                        Object value = attribute_values[i];

			if ( value == null )
			{
                          // We might need to set a default attribute
			  String def_expr_type = dca.getAttrProps().getProperty("DefaultExpressionType");

			  if ( def_expr_type != null )
			  {
                            if ( def_expr_type.equals("SeqNoExpression") )
			    {
                              String seq_name = dca.getAttrProps().getProperty("SeqName");
                              value = ctx.getDialect().getNextSeqnoExpression(seq_name);
		    	    }
			  }
			}

			if ( value instanceof com.k_int.sql.sql_syntax.Expression )
			{
                          stmnt.set(new ScopedColumnExpression(alias, entity_metadata.getBaseAttrColName(i)), (Expression)value);
			}
			else
			{
                            stmnt.set(new ScopedColumnExpression(alias, entity_metadata.getBaseAttrColName(i)), new BindVariableExpression());
                            bind_vars.add(value);
                        }
                        break;
		    }
                }
            }
	    else // The field has not been modified, but we may have a default...
	    {
                switch ( ad.getType() )
                {
                    case AttributeDefinition.DB_COLUMN_ATTR_TYPECODE:
                    {
                        DatabaseColAttribute dca = (DatabaseColAttribute)ad;

                        Object value = attribute_values[i];

			if ( value == null )
			{
                          // We might need to set a default attribute
			  String def_expr_type = dca.getAttrProps().getProperty("DefaultExpressionType");

			  if ( def_expr_type != null )
			  {
                            if ( def_expr_type.equals("SeqNoExpression") )
			    {
                              String seq_name = dca.getAttrProps().getProperty("SeqName");
                              value = ctx.getDialect().getNextSeqnoExpression(seq_name);
                              stmnt.set(new ScopedColumnExpression(alias, entity_metadata.getBaseAttrColName(i)), (Expression)value);
		    	    }
			  }
			}

		        break;
		    }
	        }
	    }
        }

        // Updating existing row, so add the appropriate SQL to restrict the update to only this PK:
        Map keyattrs = oid.getKey();

        // Will be either the top level sql statement or a logical connector such as AND
        Restrictable r = null;

        switch ( keyattrs.size() ) {
          case 0:
            // Fatal error
            log.error("Entity has no PK");
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

          r.addCondition( new ComparisonCondition( new ScopedColumnExpression(alias, keyattr),
                                                   "=",
                                                   new BindVariableExpression() ) );

          bind_vars.add( keyattrs.get(keyattr) );
        }

        String sql_string = stmnt.toString();
        log.debug("Generated SQL : "+sql_string);

        PreparedStatement ps = conn.prepareStatement(sql_string);
       
        int i_count = 1; 
        for (Iterator e = bind_vars.iterator() ; e.hasNext() ; )
            ps.setObject(i_count++, e.next());
            
        ps.close();
        return true;
    }


    public boolean performDelete(Connection conn) throws SQLException
    {
        DeleteStatement stmnt = new DeleteStatement();

        List bind_vars = new ArrayList();
        // String alias = stmnt.addTable(entity_metadata.getBaseTableName());
        TableScope alias = stmnt.addTable(entity_metadata.getBaseTableName());

        switch (persistence_state)
        {
            case PERSISTENT_DATA_DELETED:
                // Updating existing row, so add the appropriate SQL to restrict the update to only this PK:
                Map keyattrs = oid.getKey();

                // Will be either the top level sql statement or a logical connector such as AND
                Restrictable r = null;

                switch ( keyattrs.size() ) {
                    case 0:
                        // Fatal error
                        log.error("Entity has no PK, delete statement will delete EVERYTHING in collection!");
                        r = stmnt;
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

                    r.addCondition( new ComparisonCondition( new ScopedColumnExpression(alias, keyattr),
                                                             "=",
                                                             new BindVariableExpression() ) );

                    bind_vars.add( keyattrs.get(keyattr) );
                }
                break;
        }

        String sql_string = stmnt.toString();
        log.debug("Generated SQL for delete operation : "+sql_string);

        PreparedStatement ps = conn.prepareStatement(sql_string);
       
        int i_count = 1; 
        for (Iterator e = bind_vars.iterator() ; e.hasNext() ; )
            ps.setObject(i_count++, e.next());
            
        ps.close();
        return true;
    }

    // For example:
    //    title          First object is instance of String
    //    _REL_MEDIATP   First object is instance of Entity (for the related mediatp)
    //    _COLL_SUBJECT  First object is a collection of related subjects
    //    _REL_MEDIATP.sd 
    //    _COLL_SUBJECT[3].subjectsd
    //
    Object getFirstAttrValueOnPath(String path)
    {
        return null;
    }

    public EntityTemplate getTemplate() {
        return entity_metadata;
    }

    public PersistenceContext getPersistenceContext() {
        return ctx;
    }

    public int getPersistenceState() {
        return persistence_state;
    }

    public OID getOID() {
        return oid;
    }

    private void regenerateOID() {
        try
        {
            oid = new OID(ctx.getRepositoryId(),entity_metadata.getBaseTableName(),new EntityKey(this));
            // System.err.println("regeneratedOID is "+oid);
        }
        catch ( UnknownAccessPointException uape )
        {
            uape.printStackTrace();
        }
    }

    // Used to propagate our primary key to a newly created (related) record
    public void pushPKToRelatedRecord(CollectionAttribute ca, Entity related_entity) {
        // System.err.println("pushPKToRelatedRecord");
        // We need to cycle through the key componets in the collection attribute
        // and get/set the appropruate local/remote attributes
        try {
            for ( Iterator e = ca.getCorrespondingKeyPairs(); e.hasNext(); ) {
                CorrespondingKeyPair ckp = (CorrespondingKeyPair)e.next();

                Object keyval = get(ckp.getLocalAttrName());

                if ( null != keyval )
                {
                    // System.err.println("Set remote attribute "+ckp.getFkCol()+" to "+keyval);
                    related_entity.set(ckp.getRelatedAttrName(),keyval);
                }
            }
        }
        catch ( UnknownAccessPointException uape )
        {
            uape.printStackTrace();
        }
    }

    /**
     *
     * @param int index index of item to get
     * @param boolean fetch Attempt to pull that attr back from the database if
     * not allready retrieved.
     */
    public Object getValueAt(int index, boolean fetch) {
      Object retval = null;

      if ( fetched_from_db_flags[index] == true )
      {
        retval = attribute_values[index];
      }
      else if ( fetch == true )
      {
        // Not yet implemented
      }

      return retval;
    }

    public AttributeDefinition getDefinitionFor(int index)
    {
      return entity_metadata.getAttributeDefinition(index);
    }

    public void delete(boolean cascade)
    {
      is_modified = true;
      persistence_state = PERSISTENT_DATA_DELETED;
    }

    public void markDirty()
    {
      is_modified = true;
    }

    public void dump() {
      log.debug("Entity: "+oid.toString());
      log.debug("# name value dirty fetched is_null");
      for ( int i = 0; i < num_attrs; i++ ) {
        AttributeDefinition ad = entity_metadata.getAttributeDefinition(i);
        log.debug(""+i+" "+ad.getAttributeName()+
	          " "+attribute_values[i]+
	          " "+dirty_field_flags[i]+
	          " "+fetched_from_db_flags[i]+
	          " "+is_null_flags[i]);
      }
    }

    /** Override this method if you want to do something special when a new record is
     * created. For example, use a sequence number to set a primary key.
     */
    public abstract void onNewRecord(PersistenceContext ctx);

    public static Entity create(EntityTemplate et, PersistenceContext ctx) {
      return new DefaultEntity(et, ctx);
    }

    public static Entity create(EntityTemplate et,
		                PersistenceContext ctx,
				ResultSet rs,
				OID oid) {
      return new DefaultEntity(et, ctx, rs, oid);
    }
}
