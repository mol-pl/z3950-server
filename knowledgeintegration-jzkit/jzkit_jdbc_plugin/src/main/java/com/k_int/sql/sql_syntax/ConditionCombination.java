package com.k_int.sql.sql_syntax;

import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.StringWriter;

/**
 * Title:       ConditionCombination
 * Version:     $Id: ConditionCombination.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Author:      Ian Ibbotson
 * Description: Base class for building SQL Insert statements
 */
public class ConditionCombination extends BaseWhereCondition implements Restrictable
{
    Vector condlist = new Vector();
    String Op = null;

    public ConditionCombination(String Op)
    {
        // System.err.println("adding new "+Op+" condition");
        this.Op = Op;
    }

    public void addCondition(BaseWhereCondition c)
    {
        // System.err.println("Adding condition to combination...");
        condlist.add(c);
    }

    // public void outputSQL(StringWriter sw)
    // {
    //     sw.write(" ( ");
    //     for (Enumeration e = condlist.elements() ; e.hasMoreElements() ;) 
    //     {
    //         ((BaseWhereCondition)(e.nextElement())).outputSQL(sw);

    //         if ( e.hasMoreElements() )
    //         {
    //             sw.write(" ");
    //             sw.write(Op);
    //             sw.write(" ");
    //         }
    //     }
    //     sw.write(" ) ");
    //    
    // }

    public void outputSQL(StringWriter sw)
    {
        boolean needs_connector = false;
                                                                                                                      
        if ( countChildClauses() > 0 )
        {
          sw.write(" ( ");
          for (Enumeration e = condlist.elements() ; e.hasMoreElements() ;)
          {
              BaseWhereCondition bwc = (BaseWhereCondition)(e.nextElement());
                                                                                                                      
              if ( bwc.countChildClauses() > 0 )
              {
                if ( needs_connector )
                {
                    sw.write(" ");
                    sw.write(Op);
                    sw.write(" ");
                }
                else
                {
                  needs_connector = true;
                }
                                                                                                                      
                bwc.outputSQL(sw);
             }
          }
          sw.write(" ) ");
      }
    }
                                                                                                                      
    public int numComponents()
    {
        return condlist.size();
    }

    public int countChildClauses()
    {
      int retval = 0;
                                                                                                                      
      for ( Enumeration e = condlist.elements() ; e.hasMoreElements() ;)
      {
        retval+=((BaseWhereCondition)(e.nextElement())).countChildClauses();
      }
                                                                                                                      
      return retval;
    }

}
