package com.k_int.sql.qm_to_sql;

import java.io.*;
import java.util.*;
import com.k_int.sql.data_dictionary.*;
import com.k_int.sql.sql_syntax.*;
import org.jzkit.search.util.QueryModel.Internal.*;
import org.springframework.context.*;
import java.io.StringWriter;
import org.apache.commons.logging.*;


/**
 * Title:           SQLQueryVisitor
 * Description:     Visits the nodes in an internal query model tree and builds an SQL statement to reflect it
 * Copyright:       
 * @author:         Ian Ibbotson (ian.ibbotson@sun.com)
 * @version:        $Id: SQLQueryVisitor.java,v 1.21 2005/10/12 10:20:14 ibbo Exp $
 *
 * SQL Clause Construction Descision Table
 *
 * $Log: SQLQueryVisitor.java,v $
 * Revision 1.21  2005/10/12 10:20:14  ibbo
 * Added sort bind variables seperate to where bv's
 *
 * Revision 1.20  2005/10/11 21:07:42  ibbo
 * updated
 *
 * Revision 1.19  2005/09/19 14:34:58  ibbo
 * Extra info in error case
 *
 * Revision 1.18  2005/09/15 16:22:05  ibbo
 * Fixed
 *
 * Revision 1.17  2005/08/15 18:39:16  ibbo
 * New capability to sort by relevance score in fulltext queries
 *
 * Revision 1.16  2005/08/04 08:02:58  ibbo
 * Added multiple fields for free text query
 *
 * Revision 1.15  2005/07/09 09:35:41  ibbo
 * Break out spatial expression and geom from text expressions
 *
 * Revision 1.14  2005/06/21 14:05:16  ibbo
 * Improved logging and handling of unmapped collections in SQLQueryVisitor
 *
 * Revision 1.13  2005/03/08 19:25:44  ibbo
 * Added option to use JOIN clause rather than joining through where clause
 *
 * Revision 1.12  2005/02/02 13:27:30  ibbo
 * Updated contains / within for spatial search
 *
 * Revision 1.11  2004/11/21 13:02:31  ibbo
 * Added a flag to support normalisation of search terms in access point config
 *
 * Revision 1.10  2004/11/05 17:34:49  ibbo
 * Updated
 *
 * Revision 1.9  2004/11/04 13:07:42  ibbo
 * Updated
 *
 * Revision 1.8  2004/11/04 12:50:01  ibbo
 * Added gazeteer interface
 *
 * Revision 1.7  2004/10/31 12:21:22  ibbo
 * Database criteria added
 *
 * Revision 1.6  2004/10/29 16:39:22  ibbo
 * Added MySQL Match Against Functionality for free text searching
 *
 * Revision 1.5  2004/10/28 15:13:46  ibbo
 * JDBC Query can now map a use attribute onto multiple database columns which
 * will be OR'd together
 *
 * Revision 1.4  2004/10/28 10:11:57  ibbo
 * Updated
 *
 * Revision 1.3  2004/10/28 09:54:25  ibbo
 * Got SQL Generation working a little faster / better :)
 *
 * Revision 1.2  2004/10/24 11:56:36  ibbo
 * Updated
 *
 * Revision 1.1  2004/10/22 09:24:28  ibbo
 * Added jdbc plugin
 *
 * Revision 1.8  2003/12/11 09:49:20  iibbotson
 * Handle "," and " " as delimiters in multi-term fields such as forename(s)
 *
 * Revision 1.7  2003/12/10 10:51:55  iibbotson
 * Added code to handle multi forename search
 *
 * Revision 1.6  2003/12/09 12:32:07  iibbotson
 * New code in preparation for multiple forename search
 *
 * Revision 1.5  2003/12/03 13:02:15  iibbotson
 * Get rid of last few remaining vectors in favour of ArrayList objects
 *
 * Revision 1.4  2003/11/26 15:55:46  iibbotson
 * Extra config for looking up person by identifier
 *
 * Revision 1.3  2003/11/26 10:55:24  iibbotson
 * Improved javadoc commenting and additional trim() of search strings.
 *
 *
 */

public class SQLQueryVisitor {

    private java.util.Stack branch_stack = new java.util.Stack();
    private EntityTemplate base_et;
    private TableScope base_table_scope = null;
    private Collection where_bind_vars = null;
    private Collection sort_bind_vars = null;
    private AttrMap am = null;
    private String default_attrset = null;
    protected static Log log = LogFactory.getLog(SQLQueryVisitor.class);
    private int full_text_counter = 1;
    private SelectStatement ss = null;
    private com.k_int.sql.data_dictionary.Dictionary d = null;
    private BaseWhereCondition join_clauses = null; 
    private int unique_counter = 0;
    private SQLDialect dialect = null;
    private static final String EQUALS = "=";
    private QMToSQLConfig config = null;
    private ApplicationContext ctx = null;
    /**
     * Constructor
     * @param d com.k_int.sql.data_dictionary.Dictionary object that defines the database schema
     * @param collection_id The root entity type that we are searching for (Person,Case,etc)
     * @param config A Mapping table between abstract access points and database access paths
     * @param s An input blank select statement.
     * @param where_bind_vars Collection that will be filled with bind variables that should be used as input to the prepared statement
     * @param sort_bind_vars Collection that will be filled with bind variables that should be used as input to the prepared statement
     * @param hint Hint about what column names we would like in the result
     * @throws UnknownCollectionException if asked to search an unknown root entity type
     */
    public SQLQueryVisitor(com.k_int.sql.data_dictionary.Dictionary d, // Information about the schema we are dealing with
                           SQLDialect dialect,
                           List collection_ids,
                           QMToSQLConfig config,
                           SelectStatement s,      // The base select statement we want to populate
                           Collection where_bind_vars,       // Bind Variables for the query
                           Collection sort_bind_vars,       // Bind Variables for the query
                           Collection hint,            // Hint as to which cols we really want back
                           ApplicationContext ctx)
                  throws UnknownCollectionException {
        this.where_bind_vars = where_bind_vars;
        this.sort_bind_vars = sort_bind_vars;
	this.ss = s;
        this.d = d;
        this.dialect = dialect;
        this.config = config;
        this.ctx = ctx;

        CollectionInfo ci = processCollectionList(collection_ids);
        String collection_id = ci.getBaseEntityName();

        // Lookup an AttrMap using collection id
        this.am = config.lookupAttrMap(collection_id);

        if ( am == null )
          throw new UnknownCollectionException("SQLQueryVisitor unable to attrmap: "+collection_id);

        // we will use the database name to determine a base table, and hence, base select for 
        // this query.
        String base_table = am.getBaseEntityName();
        base_et = d.lookup(base_table);

        if ( base_et == null )
          throw new UnknownCollectionException("SQLQueryVisitor unable to locate table: "+base_table);

        String base_table_name = base_et.getBaseTableName();
        base_table_scope = ss.addTable(base_table_name);

        // log.debug("Added "+base_table_name+", alias="+base_table_scope);

        // Decide which cols we will select... Select everything right now
        // Cycle through... Let's try everything...
        if ( ( hint != null ) && ( hint.size() > 0 ) ) {
          for( Iterator e = hint.iterator(); e.hasNext(); ) {
            Object col = e.next();
            // We are going to see if the element is an expression or just a string
            if ( col instanceof BaseExpression ) {
              // WooHoo...
              // log.debug("Adding a BaseExpression to the select list");
              s.addField((BaseExpression)col);
	    }
	    else {
              // String select_attr_name = e.nextElement().toString();
              // For now, just assume they are cols from the base table....
              // log.debug("Adding a simple field to the select list");
              s.addField(new ScopedColumnExpression(base_table_scope, col.toString()));
	    }
          }
        }
        else {
          log.debug("No select hint supplied, adding all cols from the base table");
          // for( Enumeration e = base_et.getAttributeDefinitions(); e.hasMoreElements(); )
          // Iterate key attr fields and add them to the select list
          for( Iterator e = base_et.getKeyAttrs(); e.hasNext(); ) {
            try {
              // AttributeDefinition ad = (AttributeDefinition) e.nextElement();
              AttributeDefinition ad = base_et.getAttributeDefinition((String)(e.next()));

              switch( ad.getType() ) {
                // A real col in a sql set
                case AttributeDefinition.DB_COLUMN_ATTR_TYPECODE:
                  // System.err.println("Adding qry attribute "+ad.getAttributeName());
                  s.addField(new ScopedColumnExpression(base_table_scope, ((DatabaseColAttribute)ad).getColName()));
                  break;
                // A Pseudo attr representing a 1:1 relationship
                case AttributeDefinition.LINK_ATTR_TYPECODE:
                  // May do something with these in the near future... Probably not though
                  break;
              }
            }
            catch ( Exception excep ) {
              // Should never happen really. Configuration error if it does.
              excep.printStackTrace();
            }
          }
        }

        // s.add primary key attributes, classifying attributes, maybe base attributes...
        // Adding attributes may mean adding join clauses, but that's dealt with by the SQL builder.

        // SelectStatement implements restrictable, so we can add conditions to it
        // N.B. We are really pushing an object that implements restrictable onto the branch_stack here.
        // In the first instance, the root SQL statement itself is restrictable by two basic types of
        // SQL : an atomic operation, e.g. WHERE X='Y' or a more complex op such as WHERE (X and Y)
        // in this case, X and Y represent furthur restrictable nodes (IE, they can themselves be
        // atomic or complex).

        BaseWhereCondition root_and = new ConditionCombination("AND");
        join_clauses = new ConditionCombination("AND");
        ((Restrictable)root_and).addCondition(join_clauses);
        s.addCondition( root_and );

        try {
          // If we have database name sub-mappings, add in additional criteria
          addCollectionRestrictionOR((Restrictable)root_and,ci.collection_identifiers);
          addFilterConditions((Restrictable)root_and, this.am.getFilterConditions());
        }
        catch ( com.k_int.sql.data_dictionary.UnknownAccessPointException uape ) {
          uape.printStackTrace();
        }
        catch ( com.k_int.sql.data_dictionary.UnknownCollectionException uce ) {
          uce.printStackTrace();
        }

        branch_stack.push(root_and);
    }

    private void addFilterConditions(Restrictable root_and, 
                                     List filter_conditions) throws com.k_int.sql.data_dictionary.UnknownAccessPointException, com.k_int.sql.data_dictionary.UnknownCollectionException {

      log.debug("addFilterConditions "+filter_conditions);

      for ( Iterator i = filter_conditions.iterator(); i.hasNext(); ) {
        Object filter = i.next();
        if ( filter instanceof FilterAttrMapping ) {
          FilterAttrMapping fam = (FilterAttrMapping) filter;
          ScopedColumnExpression db_field_to_search = getColFor(fam.getAccessPath(), root_and);
          root_and.addCondition(new ComparisonCondition(db_field_to_search,fam.getRelation(), new ValueExpression(fam.getValue())));
        }
        else if ( filter instanceof JoinFilter ) {
            JoinFilter jf = (JoinFilter) filter;
            ScopedColumnExpression db_field_to_search = getColFor(jf.getAccessPath(), root_and);
            ScopedColumnExpression target_field = getColFor(jf.getTargetPath(), root_and);
            root_and.addCondition(new ComparisonCondition(db_field_to_search,jf.getRelation(), target_field));
          }
        // Nasty, nasty hack to fix a bug in a MySQL query for bank2.  Don't use this, it's nasty!
        else if ( filter instanceof SubQueryFilter ) {
        	SubQueryFilter sqf = (SubQueryFilter) filter;
            ScopedColumnExpression db_field_to_search = getColFor(sqf.getAccessPath(), root_and);
            root_and.addCondition(new SubQueryCondition(db_field_to_search,sqf.getRelation(), sqf.getQuery()));
         }
      }
    }

    /**
     *  If the query had collection names which map onto values of a database column, then 
     *  here we tag on an In clause to the root of the generated SQL for that restriction,
     *  for example "AND c.collection_name in ( 'C1', 'C4', 'C8' )" N.B. That this clause is
     *  not added if the collection does not have a database name discriminator column or
     *  there are no values passed in for the actual collection names.
     */
    private void addCollectionRestriction(Restrictable root_and, List collection_names) throws com.k_int.sql.data_dictionary.UnknownAccessPointException, com.k_int.sql.data_dictionary.UnknownCollectionException {

      log.debug("addCollectionRestriction "+collection_names);

      int counter = 0;
      // Does this entity have a collection discriminator attribute
      if ( ( collection_names != null ) && 
           ( collection_names.size() > 0 ) &&
           ( this.am.getCollectionAttribute() != null ) ) {

        ExpressionList el = new ExpressionList();
                                                                                                                                          
        for ( Iterator i = collection_names.iterator(); i.hasNext(); ) {
          Object o = i.next();
          if ( o != null ) {
            counter++;
            el.addExpression( new BindVariableExpression() );
            where_bind_vars.add(o.toString());
          }
        }

        if ( counter > 0 ) {
          // We are working on a complete field
          log.debug("adding database name col "+this.am.getCollectionAttribute());
          ScopedColumnExpression db_field_to_search = getColFor(this.am.getCollectionAttribute(),root_and);
                                                                                                                                          
          root_and.addCondition(new MembershipCondition(db_field_to_search,false,el));
        }
      }
    }

    private void addCollectionRestrictionOR(Restrictable root_and, List collection_names) throws com.k_int.sql.data_dictionary.UnknownAccessPointException, com.k_int.sql.data_dictionary.UnknownCollectionException {
      log.debug("addCollectionRestrictionOR "+collection_names);

      ConditionCombination db_clauses = new ConditionCombination("OR");
      int counter = 0;
      // Does this entity have a collection discriminator attribute

      if ( ( collection_names != null ) && 
           ( collection_names.size() > 0 ) &&
           ( this.am.getCollectionAttribute() != null ) && 
           ( collection_names.get(0) != null ) ) {

        ScopedColumnExpression db_field_to_search = getColFor(this.am.getCollectionAttribute(),root_and);

        for ( Iterator i = collection_names.iterator(); i.hasNext(); ) {
          Object o = i.next();
          if ( o != null ) {
            counter++;
            db_clauses.addCondition(new ComparisonCondition(db_field_to_search,"=", new BindVariableExpression()));
            where_bind_vars.add(o.toString());
          }
        }

        if ( counter > 0 ) {
          root_and.addCondition(db_clauses);
        }
      }
    }

    public void visit(QueryNode qn) throws java.io.IOException, 
                                           UnknownAccessPointException,
                                           UnknownCollectionException {
      visit(qn,null);
    }

    public void visit(QueryNode qn, String default_ns) throws java.io.IOException, 
                                           UnknownAccessPointException,
                                           UnknownCollectionException {
        if ( qn instanceof InternalModelRootNode )
            visit((InternalModelRootNode)qn, default_ns);
        else if ( qn instanceof ComplexNode )
            visit((ComplexNode)qn, default_ns);
        else if ( qn instanceof AttrPlusTermNode )
            visit((AttrPlusTermNode)qn, default_ns);
        else if ( qn instanceof InternalModelNamespaceNode )
            visit((InternalModelNamespaceNode)qn, default_ns);
    }

    public void visit(InternalModelRootNode rn, String default_ns) throws java.io.IOException, 
                                                       UnknownAccessPointException,
                                                       UnknownCollectionException {
        log.debug("Visit Root Node for: "+rn.toString());
        visit(rn.getChild(), default_ns);
    }

    public void visit(InternalModelNamespaceNode ns, String default_ns) throws java.io.IOException, 
                                                            UnknownAccessPointException,
                                                            UnknownCollectionException {
        // Hmm.. This is not good enough any more.. Namespace can change at different positions
        // in the query tree.
        default_attrset = ns.getAttrset();
        visit(ns.getChild(), default_attrset);
    }


    public void visit(ComplexNode cn, String default_ns) throws java.io.IOException, 
                                             UnknownAccessPointException,
                                          UnknownCollectionException {
        // System.err.println("SQLQueryVisitor::visit(ComplexNode)");

        BaseWhereCondition new_condition = null;
        Restrictable parent = (Restrictable) branch_stack.peek();

        int inumleft = cn.getLHS().countChildrenWithTerms();
        int inumright = cn.getRHS().countChildrenWithTerms();

        if ( ( inumleft > 0 ) &&
             ( inumright > 0 ) )
        {

            switch( cn.getOp() )
            {
                case 1:
                    new_condition = new ConditionCombination("AND");
                    parent.addCondition( new_condition );
                    branch_stack.push(new_condition);
                    break;
                case 2:
                    new_condition = new ConditionCombination("OR");
                    parent.addCondition( new_condition );
                    branch_stack.push(new_condition);
                    break;
                case 3:  // and not
                    break;
                case 4:
                    break;
                default:
                    break;
            }

        }

        if ( inumleft > 0 )
        {
            visit(cn.getLHS(), default_ns);
        }

        if ( inumright > 0 )
        {
            visit(cn.getRHS(), default_ns);
        }

        if ( ( inumleft > 0 ) && ( inumright > 0 ) ) {
          if ( branch_stack.size() > 0 ) {
            branch_stack.pop();
          }
          else {
            log.warn("SQL Generation : Expected branch stack to be > size 0, not == 0");
          }
        }
    }

    public void visit(AttrPlusTermNode aptn, String default_ns) throws java.io.IOException,
                                                    UnknownAccessPointException,
                                                    UnknownCollectionException
 
    {
      Object ap = aptn.getAccessPoint();
      String access_point = ( ( ap instanceof AttrValue ) ? ((AttrValue)ap).getWithDefaultNamespace(default_ns) : ap.toString() );
      AttrMapping[] mappings = am.lookupAttr(access_point);

      if ( mappings != null ) {

        Restrictable parent = (Restrictable) branch_stack.peek();

        Object term = aptn.getTerm();
        AttrValue completeness = (AttrValue) aptn.getCompleteness();
        AttrValue relation_attr = (AttrValue) aptn.getRelation();

        // If this use attribute maps on to more than one set of target access points, for example
        // "TitleOrAuthor" might map onto title or author attributes, then we need to or the resultant
        // conditional components.
        if ( mappings.length > 1 ) {
          BaseWhereCondition new_condition = new ConditionCombination("OR");
          parent.addCondition( new_condition );
          parent = (Restrictable) new_condition;
        }

        for ( int mi=0; mi<mappings.length; mi++ ) {

          // Create a new AND condition which can be used to and any extra filter critera, or will just be normalised out
          // of the structure
          Restrictable new_condition = new ConditionCombination("AND");
          parent.addCondition( (BaseWhereCondition) new_condition );
          branch_stack.push(new_condition);

          AttrMapping m = mappings[mi];

          String relation = EQUALS;
                                                                                                                                          
          if ( relation_attr != null )
            relation = relation_attr.toString();
  
          // In processing is a special case for now :(
          if ( relation.equalsIgnoreCase("IN") ) {
            processInRelation( new_condition, aptn, default_ns, m, term, completeness, relation_attr );
          }
          else {
            // If term is an array or collection type, make an and and repeatedly call processMapping for each
            // else just call the once.
            if ( term instanceof String[] ) {
              log.debug("Processing array :"+term.getClass().getName());
              String[] term_array = (String[]) term;
              for ( int i=0; i<term_array.length; i++ ) {
                log.debug("Processing array component "+term_array[i]);
                if ( ( term_array[i] != null ) && ( !term_array[i].equals("") ) )
                  processMapping( new_condition, aptn, default_ns, m, term_array[i], completeness, relation_attr );
              }
            }
            else {
              log.debug("Processing scalar component "+m);
              processMapping( new_condition, aptn, default_ns, m, term, completeness, relation_attr );
            }
          }

          branch_stack.pop();
        }
      }
      else {
        throw new UnknownAccessPointException("Unknown access point : "+access_point);
      }
    }

    /**
     * Call the appropriate function to generate the right kind of SQL depending upon the mapping
     * in use.
     */
    private void processMapping( Restrictable parent,
                                 AttrPlusTermNode aptn,
                                 String default_ns,
                                 AttrMapping m,
                                 Object term,
                                 AttrValue completeness,
                                 AttrValue relation_attr)  throws UnknownAccessPointException, UnknownCollectionException
    {
      // N.B. We should changed all the following calls to use term instead of aptn

      // This sucks ass, but it's a test for H3 where hibernate only gives us a proxy object
      // until the object is actually accessed. So we call a method to replace the thing with a 
      // concrete instance that we can do instanceof on. (I hope)
      // m.getAccessPath();

      if ( m instanceof SimpleTextMapping ) {
        processTextMapping(parent, term, completeness, default_ns, (SimpleTextMapping)m);
      }
      else if ( m instanceof DateMapping ) {
        processDateMapping(parent, aptn, default_ns, (DateMapping)m);
      }
      else if ( m instanceof FreeTextMapping ) {
        processFreeTextMapping(parent, aptn, default_ns, (FreeTextMapping)m);
      }
      else if ( m instanceof CaseInsensitiveTextMapping ) {
        // processCaseInsensitiveTextMapping(parent, aptn, default_ns, (CaseInsensitiveTextMapping)m);
        processCaseInsensitiveTextMapping(parent, term, default_ns, (CaseInsensitiveTextMapping)m);
      }
      else if ( m instanceof IntMapping ) {
        processIntMapping(parent, aptn, default_ns, (IntMapping)m);
      }
      else if ( m instanceof BoolMapping ) {
        processBoolMapping(parent, aptn, default_ns, (BoolMapping)m);
      }
      else if ( m instanceof SpatialMapping ) {
        processSpatialMapping(parent, aptn, default_ns, (SpatialMapping)m);
      }
      else 
        throw new RuntimeException("Unhandled mapping type : "+m.getClass().getName());
    }

    private void processInRelation( Restrictable parent,
                                    AttrPlusTermNode aptn,
                                    String default_ns,
                                    AttrMapping m,
                                    Object term,
                                    AttrValue completeness,
                                    AttrValue relation_attr)  throws UnknownAccessPointException, UnknownCollectionException
    {
      // We are working on a complete field
      ScopedColumnExpression db_field_to_search = getColFor(m.getAccessPath(), parent);
                                                                                                                                        
      ExpressionList el = new ExpressionList();
     
      if ( term instanceof Collection )
      {
        for ( Iterator i = ((Collection)term).iterator(); i.hasNext(); )
        {
          el.addExpression( new BindVariableExpression() );
          where_bind_vars.add(i.next().toString());
        }
      }
      else
      {
        el.addExpression( new BindVariableExpression() );
        where_bind_vars.add(term.toString());
      }
 
      parent.addCondition(new MembershipCondition(db_field_to_search,false,el));
    }


    public void processTextMapping(Restrictable parent, 
                                   // AttrPlusTermNode aptn, 
                                   Object term,
                                   AttrValue completeness,
                                   String default_ns, 
                                   SimpleTextMapping m) throws UnknownAccessPointException, UnknownCollectionException
    {
      // String term = aptn.getTerm().toString();
      // Next up, is this a complete field search (Or is completeness null )
      // AttrValue completeness = aptn.getCompleteness();

      if ( ( completeness == null ) || 
           ( completeness.getWithDefaultNamespace(default_ns).equals("CompleteField") ) ||
           ( completeness.getWithDefaultNamespace(default_ns).equals("bib1.3") ) ) {
        // We are working on a complete field
        ScopedColumnExpression db_field_to_search = getColFor(m.getAccessPath(), parent);

        // Create a clause for "Where field like 'value'"
        parent.addCondition(new ComparisonCondition(db_field_to_search,"like", new BindVariableExpression()));

        // here we could look at truncation attrs and add the appropriate wildcard characters
        String the_term = term.toString();

        // Is normalise bit set?
        if ( ( m.pre_search_action & 1 ) == 1 ) {
          the_term = translateWildCards(normaliseOne(term.toString()));
        }

        // Is auto right truncate bit set
        if ( ( m.pre_search_action & 2 ) == 2 ) { 
          the_term = term + "%";
        }

        where_bind_vars.add(the_term);
      }
      else
      {
        // We are working at the inverted index level on the components of a field. this
        // Will require some form of extended SQL, either via locally defined inverted indexes
        // or via vendor specific extensions such as OracleText.

        // This _should_ be done by calling "dialect.createFullTextExpression(field, value)"
        // but for now, we are assuming oracle.... Boo
        FunctionExpression fe = new FunctionExpression("Contains");
        fe.addParameter( getColFor(m.getAccessPath(), parent ) );
        fe.addParameter( new BindVariableExpression() );
        // Add a marker so we can rank by this statement
        fe.addParameter( new ValueExpression(new Integer(full_text_counter)) );
        parent.addCondition( new ComparisonCondition(fe, ">", new  ValueExpression(new Integer(0))));
        where_bind_vars.add(translateWildCards(term.toString()));
        // ss.sortBy(null,"SCORE("+(full_text_counter++)+")",false);
        full_text_counter++;
      }
      // else throw new unsupported completeness directive
    }

    public void processDateMapping(Restrictable parent,
                                   AttrPlusTermNode aptn,
                                   String default_ns,
                                   DateMapping m) throws UnknownAccessPointException, UnknownCollectionException {
      Object term = aptn.getTerm();
                                                                                                                                          
      // Next up, is this a complete field search (Or is completeness null )
      ScopedColumnExpression db_field_to_search = getColFor(m.getAccessPath(), parent);

      Object relation_attr = aptn.getRelation();
      String relation = EQUALS;

      if ( relation_attr != null )
        relation = relation_attr.toString();
                                                                                                                                          
      // Create a clause for "Where field like 'value'"
      parent.addCondition(new ComparisonCondition(db_field_to_search, relation, new BindVariableExpression()));
                                                                                                                                          
      // here we could look at truncation attrs and add the appropriate wildcard characters
      if ( term instanceof java.util.Calendar )
        where_bind_vars.add(new java.sql.Date(((java.util.Calendar)term).getTime().getTime())); // Add a date
      else if ( term instanceof java.util.Date )
        where_bind_vars.add(new java.sql.Date(((java.util.Date)term).getTime())); // Add a date
      else
        where_bind_vars.add(term);
    }

    public void processFreeTextMapping(Restrictable parent,
                                       AttrPlusTermNode aptn,
                                       String default_ns,
                                       FreeTextMapping m) throws UnknownAccessPointException, UnknownCollectionException {

      log.debug("processFreeTextMapping - Relation="+aptn.getRelation()+" access path:"+m.getAccessPath());
      Object term_obj = aptn.getTerm();

      String term = null;
      String relation = null;
      if ( aptn.getRelation() != null )
        relation = aptn.getRelation().toString();

      // Process the values we are searching for.....
      if ( term_obj instanceof List ) {
        term = dialect.processFreeTextTermList((List)term_obj,relation);
      }
      else {
        List l = new ArrayList();
        l.add(term_obj.toString());
        term = dialect.processFreeTextTermList(l, relation);
      }


      // We are assuming that the columns used are in the current context... hmm.
      String[] columns = m.getAccessPath().split(";");
      List l = new ArrayList();
      for ( int i=0; i<columns.length; i++ ) {
        l.add(getColFor(columns[i], parent));
      }

      parent.addCondition( dialect.freeTextExpression(l,new BindVariableExpression(),true));

      where_bind_vars.add(translateWildCards(quoteFreeTextTermIfNeeded(term)));

      Expression score = dialect.freeTextScore(l,new BindVariableExpression(),true);

      if ( ( score != null ) && ( sort_bind_vars != null ) ) {
        sort_bind_vars.add(translateWildCards(term));
        ss.sortBy(new OrderByExpression(score,false));
      }
    }

    private String quoteFreeTextTermIfNeeded(String term) {
      String result = term;
      if ( dialect.freeTextTermNeedsQuoting(term) ) {
        result = dialect.freeTextQuoteChar()+term+dialect.freeTextQuoteChar();
      }
      return result;
    }

    public void processIntMapping(Restrictable parent,
                                  AttrPlusTermNode aptn,
                                  String default_ns,
                                  IntMapping m) throws UnknownAccessPointException, UnknownCollectionException {
      Object term = aptn.getTerm();

      ScopedColumnExpression db_field_to_search = getColFor(m.getAccessPath(), parent);
      Object relation_attr = aptn.getRelation();
      String relation = EQUALS;
      if ( relation_attr != null )
        relation = relation_attr.toString();

      if ( relation.equals(EQUALS) ) {
        parent.addCondition(new ComparisonCondition(db_field_to_search, relation, new BindVariableExpression()));
      }
      else if ( relation.equals("in") ) { // log.finest("Relation is IN");
        parent.addCondition(new ComparisonCondition(db_field_to_search, relation, new BindVariableExpression()));
      }
      else {
        throw new RuntimeException("Unhandled class of value for int mapping");
      }

      where_bind_vars.add(term);
    }

    public void processBoolMapping(Restrictable parent,
                                   AttrPlusTermNode aptn,
                                   String default_ns,
                                   BoolMapping m) throws UnknownAccessPointException, UnknownCollectionException
    {
      Object term = aptn.getTerm();
                                                                                                                                        
      ScopedColumnExpression db_field_to_search = getColFor(m.getAccessPath(), parent);
      Object relation_attr = aptn.getRelation();
      String relation = EQUALS;
      if ( relation_attr != null )
        relation = relation_attr.toString();
      parent.addCondition(new ComparisonCondition(db_field_to_search, relation, new BindVariableExpression()));

      if ( term instanceof Boolean )
      {
        if ( ((Boolean)term).equals(Boolean.TRUE) )
          where_bind_vars.add( new Integer(1) );
        else
          where_bind_vars.add( new Integer(0) );
      }
      else
      {
        throw new RuntimeException("Invalid type for boolean match");
      }
    }

    public void processSpatialMapping(Restrictable parent,
                                   AttrPlusTermNode aptn,
                                   String default_ns,
                                   SpatialMapping m) throws UnknownAccessPointException, UnknownCollectionException
    {
      Object term = aptn.getTerm();
      Object relation_attr = aptn.getRelation();
      String relation = EQUALS;

      log.debug("aptn: "+aptn.toString());

      long near=150;

      if ( relation_attr != null )
        relation = relation_attr.toString();
                                                                                                                                          
      if ( m.resolvePlaceNames() && ( m.getGazetterName() != null ) ) {
        Gazeteer g = (Gazeteer) ctx.getBean(m.getGazetterName());
        String wkt_polygon = g.lookupWKTForNearPlaceName(term.toString(),near);

        if ( wkt_polygon != null ) {
          if ( ( m.getOperation() != null ) && ( m.getOperation().equalsIgnoreCase("within") ) ) {
            // COL Within BV  ( = BV Contains Col )
            log.debug("Processing spatial component as COL within BV");
            parent.addCondition(dialect.createSpatialExpression(dialect.WKTToGeometry(new BindVariableExpression()),getColFor(m.getAccessPath(), parent)));
            where_bind_vars.add(wkt_polygon);
          }
          else {
            // COL Contains BV
            log.debug("Processing spatial component as COL contains BV");
            parent.addCondition(dialect.createSpatialExpression(getColFor(m.getAccessPath(), parent), dialect.WKTToGeometry(new BindVariableExpression())));
            where_bind_vars.add(wkt_polygon);
          }
        }
        else {
          // Add some SQL to simulate a false condition.. Which is what this always relates to.
          log.warn("Gazeteer failed to resolve a WKT polygon for place name "+term.toString()+" with near value of "+near);
          parent.addCondition( new ComparisonCondition(new ValueExpression(1),"=", new ValueExpression(2)));
        }
      }
      else {
        parent.addCondition(dialect.createSpatialExpression(getColFor(m.getAccessPath(), parent), new BindVariableExpression()));
        where_bind_vars.add( term );
      }

    }
                                                                                                                                        

    public void processCaseInsensitiveTextMapping(Restrictable parent,
                                                  Object term,
                                                  String default_ns,
                                                  CaseInsensitiveTextMapping m) throws UnknownAccessPointException, 
                                                                                       UnknownCollectionException
    {
      // AttrPlusTermNode aptn,
      // Object term = aptn.getTerm();

      // As above.. creation of this statement should be delagated to dialect
      FunctionExpression fe = new FunctionExpression("lower");
      fe.addParameter( getColFor(m.getAccessPath(), parent) );

      // Create a clause for "Where lower(field) like 'value'"
      parent.addCondition(new ComparisonCondition(fe,"like", new BindVariableExpression()));

      where_bind_vars.add(translateWildCards(term.toString().toLowerCase()));
    }

    /**
     * This function is responsible for taking a database access path and ensuring that the correct
     * table scopes are set up in the created SQL statement. For example, if we start with a root 
     * scope of Book, and the access path is Book.authors.author.surname then this function needs to set
     * up joins from book to book-authors and from book-authors to person, for example, so that person.surname
     * can be added as criteria.
     */
    private ScopedColumnExpression getColFor(String access_path, 
                                             Restrictable parent) throws UnknownAccessPointException, UnknownCollectionException {
      TableScope parent_scope = base_table_scope;
      AttributeDefinition ad = null;
      Vector access_path_elements = base_et.resolveAccessPath(access_path,d);

      if ( access_path_elements.size() > 1 ) {
        String scope_name = "gen";

        // Run through the access path adding the required joins for each node
        for ( Enumeration path_enum = access_path_elements.elements(); path_enum.hasMoreElements(); ) {

          AccessPathComponent apc = (AccessPathComponent)path_enum.nextElement();
          AttributeDefinition nextad = apc.getAttributeMetadata();
          EntityTemplate local_et = apc.getEntityMetadata();

          // AttributeDefinition nextad = (AttributeDefinition)(path_enum.nextElement());
          if ( nextad instanceof DatabaseLinkAttribute ) {
            scope_name += "_";
            scope_name += nextad.getAttributeName();

            if ( !apc.getReuseContext() ) {
              // log.fine("Do not reuse any existing context for "+ nextad.getAttributeName());
              scope_name += (unique_counter++);
            }

            DatabaseLinkAttribute dla = (DatabaseLinkAttribute)nextad;
            // System.err.println("Add intermediate join to "+dla.getRelatedTable()+" looking up scope "+scope_name);

            // log.fine("looking up "+scope_name);

            // Do we already have this alias available for access?
            TableScope child_alias = ss.lookupScope(scope_name);

            if ( child_alias == null ) {
              // System.err.println("Not found.. create new scope called "+scope_name);
              // We don't already have that scope, so create it afresh
 
              // getRelatedTable actually returns us an internal entity name, convert into a real
              // table name.
              EntityTemplate linked_et = d.lookup(dla.getRelatedEntityName());

              int mode = 2; // Mode 1 = Join via where clause and root scopes, Mode 2 = JOIN clauses

              switch ( mode ) {
                case 2:   // Use JOIN clauses
                  child_alias = ss.addJoin(linked_et.getBaseTableName(), scope_name, parent_scope, apc.getJoinType() );
                  // Add all the corresponding key pairs to the JOIN ... ON clause
                  for ( Iterator keys = dla.getCorrespondingKeyPairs(); keys.hasNext(); ) {
                    CorrespondingKeyPair ckp = (CorrespondingKeyPair) keys.next();
                    BaseWhereCondition on_clause_components = new ConditionCombination("AND");
  
                    ((Restrictable)on_clause_components).addCondition( 
                         new ComparisonCondition( 
                           new ScopedColumnExpression(parent_scope,local_et.getRealColumnName(ckp.getLocalAttrName())),
                           EQUALS,
                           new ScopedColumnExpression(child_alias,linked_et.getRealColumnName(ckp.getRelatedAttrName()))));

                    ((JoinTableScope)child_alias).addOnCondition(on_clause_components);
                  }
                  break;
                case 1:
                default:
                  // Use the where clause to join tables
                  child_alias = ss.addTable(linked_et.getBaseTableName(), scope_name);

                  for ( Iterator keys = dla.getCorrespondingKeyPairs(); keys.hasNext(); ) {
                    CorrespondingKeyPair ckp = (CorrespondingKeyPair) keys.next();
  
                    ((Restrictable)join_clauses).addCondition( 
                         new ComparisonCondition( 
                           new ScopedColumnExpression(parent_scope,local_et.getRealColumnName(ckp.getLocalAttrName())),
                           EQUALS,
                           new ScopedColumnExpression(child_alias,linked_et.getRealColumnName(ckp.getRelatedAttrName()))));
                  }
                  break;
              }

              // Add any additional filter conditions for this portion of the access path
              for ( java.util.Iterator fi = apc.getConstraints().keySet().iterator(); fi.hasNext(); ) {
                String filter_attr = (String) fi.next();
                String filter_value = (String) apc.getConstraints().get(filter_attr);
                parent.addCondition(
                         new ComparisonCondition(
                           new ScopedColumnExpression(child_alias,linked_et.getRealColumnName(filter_attr)),
                           EQUALS,
                           new ValueExpression(filter_value)));
              }
            }

            parent_scope = child_alias;
          }
          else
          {
            ad=nextad;
          }
        }
      }
      else
      {
        AccessPathComponent apc = (AccessPathComponent)access_path_elements.get(0);
        ad = (AttributeDefinition)apc.getAttributeMetadata();
        // ad = (AttributeDefinition)(access_path_elements.get(0));
      }

      return new ScopedColumnExpression(parent_scope,((DatabaseColAttribute)ad).getColName());
    }

    /**
     * Replace any occurences of * with % and ? with _ Also trim any leading and trailing spaces.
     */
    private static String translateWildCards(String input) {
      // For now, just translate * and ? to % and _
      // return input.replace('*','%').replace('?','_').trim();
      return input;
    }

  /**
   * Simple normalisation (Lowercase, trim, etc);
   */
  private static String normaliseOne(String input) {
    return input.toLowerCase();
  }

  private CollectionInfo processCollectionList(List collection_ids) {

    log.debug("processCollectionList("+collection_ids+")");

    String target_entity_name = null;
    List collection_id_values = new ArrayList();

    if ( ( collection_ids != null ) && ( collection_ids.size() > 0 ) ) {
      for ( Iterator i = collection_ids.iterator(); i.hasNext(); ) {
        String external_collection_id = (String) i.next();

        log.debug("Looking up \""+external_collection_id+"\"");

        // Attempt to look-up that collection
        DatabaseMapping dm = config.lookupDatabaseMapping(external_collection_id);

        if ( dm != null ) {
          // We can only return one kind of object per query, so target entity name must be null
          // or must be the same as one already selected. If not, it's invalid.
          if ( ( target_entity_name == null ) || ( target_entity_name.equals(dm.entity_name) ) ) {
            target_entity_name = dm.entity_name;
            if ( dm.collection_name != null ) {
              collection_id_values.add(dm.collection_name);
            }
          }
          else {
            log.warn("Unsupported database name combination "+dm.entity_name+" vs "+target_entity_name);
          }
        }
        else {
          log.warn("No mapping for collection "+external_collection_id+" - Please check AccessPoints configuration");
        }
      }
    }

    log.debug("Target entity : "+target_entity_name);

    // If we didn't manage to resolve any collection values, send a warning to log, and use default.
    if ( ( collection_id_values.size() == 0 ) && ( target_entity_name == null ) ) {
      log.warn("Unable to resolve any collections from config, using default!");

      DatabaseMapping dm = config.lookupDatabaseMapping("Default");

      if ( dm != null ) {
        target_entity_name = dm.entity_name;
        if ( dm.collection_name != null ) {
          collection_id_values.add(dm.collection_name);
        }
      }
      else {
        log.debug("Unable to locate Database mapping for collection Default");
      }
    }

    return new CollectionInfo(target_entity_name,collection_id_values);
  }

  public EntityTemplate getBaseEntityTemplate() {
    return base_et;
  }
}
