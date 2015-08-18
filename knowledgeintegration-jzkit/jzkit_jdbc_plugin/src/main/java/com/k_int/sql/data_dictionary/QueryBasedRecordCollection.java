package com.k_int.sql.data_dictionary;

import java.sql.*;
import com.k_int.sql.sql_syntax.*;
import java.util.*;

/**
 * Title:       QueryBasedRecordCollection
 * @version:    $Id: QueryBasedRecordCollection.java,v 1.4 2004/10/28 09:54:25 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description:
 */
public class QueryBasedRecordCollection extends RecordCollection
{
  // Rows of this result set must conform to the pattern : 'LocalHost':Repository:TableName:Cols....
  // Headings might be Scheme:Repository:Collection:Attrs
  // Also, the attributes must contain all the components of any key defined for that entity

  // This use of result set may be dangerous if conn is returned to pool
  protected PersistenceContext ctx;
  protected String base_table_name;
  protected List row_cache = new ArrayList();

  // This is useful 'cos it tells us about the definition of members of
  // this collection
  protected  EntityTemplate base_et = null;

  public QueryBasedRecordCollection(PersistenceContext ctx,
                                    String base_table_name) throws UnknownAccessPointException,
                                                                   UnknownCollectionException
  {
    super();
    this.ctx = ctx;
    this.base_table_name = base_table_name;
    base_et = ctx.getDictionary().lookup(base_table_name);
  }

  public QueryBasedRecordCollection(PersistenceContext ctx,
                                    String base_table_name,
                                    EntityKey criteria) throws UnknownAccessPointException,
                                                               UnknownCollectionException
  {
    super();
    this.ctx = ctx;
    this.base_table_name = base_table_name;
    base_et = ctx.getDictionary().lookup(base_table_name);
    deserialise(criteria);
  }

  public void deserialise(EntityKey criteria) throws UnknownAccessPointException,
                                                     UnknownCollectionException
  {
    ResultSet rows = null;
    // Don't to anything if we are given a null criteria
    if ( null == criteria )
        return;

    // Step one is to look up the base table and extract database col attributes

    SelectStatement stmnt = new SelectStatement();
    Vector bindvars = new Vector();

    String base_table = base_et.getBaseTableName();
    // String base_table_alias = stmnt.addTable(base_table);
    TableScope base_table_alias = stmnt.addTable(base_table);

    // Step two, build select list

    // Cycle through all database coll attibutes in this type
    for( Iterator e = base_et.getAttributeDefinitions(); e.hasNext(); ) {

      AttributeDefinition ad = (AttributeDefinition) e.next();

      switch( ad.getType() ) {
        // A real col in a sql set
        case AttributeDefinition.DB_COLUMN_ATTR_TYPECODE:
          // System.err.println("Adding select attr : "+base_table_alias+"."+ad.getAttributeName());
          stmnt.addField(new ScopedColumnExpression(base_table_alias, ((DatabaseColAttribute)ad).getColName()));
          break;
          // We don't care about anything else here
      }
    }

    // Step three, build where clause based on criteria

    // Will be either the top level sql statement or a logical connector such as AND
    Restrictable r = null;

    switch ( criteria.numComponents() )
    {
        case 0:
            // Fatal error
            // System.err.println("No criteria given for collection query");
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

    for( Iterator e2 = criteria.getAttrNames(); e2.hasNext(); ) {
        String keyattr = (String)e2.next();

        r.addCondition( new ComparisonCondition( new ScopedColumnExpression(base_table_alias, keyattr),
                                                 "=",
                                                 new BindVariableExpression() ) );

        bindvars.add( criteria.getAttrValue(keyattr) );
    }

    // System.err.println("Query based record collection has generated SQL :");
    // System.err.println(stmnt.toString());
    // System.err.println("Bind vars : "+bindvars);

    // Execute count SQL
    try
    {
      rows = ctx.executeQuery(stmnt.toString(), bindvars);
      rows.beforeFirst();

      // Construct entity representations of each row
      while( rows.next() ) {
        Entity e =  ctx.constructFromResultSet(base_table_name, rows);
        row_cache.add(e);
      }

      Statement s = rows.getStatement();
      rows.close();
      if ( s != null )
        s.close();
    }
    catch ( java.sql.SQLException sqle )
    {
        sqle.printStackTrace();
    }
  }

  public Entity get(int pos)
  {
    return (Entity) row_cache.get(pos);
  }

  public int add(Entity e)
  {
    row_cache.add(e);
    return row_cache.size();
  }

  public int size() {
    return row_cache.size();
  }

  public Iterator iterator() {
    return row_cache.iterator();
  }
}
