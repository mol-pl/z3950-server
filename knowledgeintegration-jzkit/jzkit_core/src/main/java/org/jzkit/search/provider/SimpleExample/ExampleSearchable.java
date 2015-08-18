/**
 * Title:       ExampleSearchable
 * @version:    $Id: ExampleSearchable.java,v 1.7 2004/11/18 13:44:37 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 *              
 */

/*
 * $Log: ExampleSearchable.java,v $
 * Revision 1.7  2004/11/18 13:44:37  ibbo
 * Updated example constructor
 *
 * Revision 1.6  2004/10/28 12:54:23  ibbo
 * Updated
 *
 * Revision 1.5  2004/09/30 17:05:19  ibbo
 * Continued adoption of new RecordFormatSpecification
 *
 * Revision 1.4  2004/09/24 16:46:21  ibbo
 * All final result set objects now implement IRResultSet directly instead of
 * inheriting the implementation from the Abstract base class.. This seems to
 * work better for trmi
 *
 * Revision 1.3  2004/09/09 09:34:03  ibbo
 * Updated
 *
 * Revision 1.2  2004/08/17 13:05:45  ibbo
 * Updated - removed commons logging and replaced with jdk logging
 *
 * Revision 1.1.1.1  2004/06/18 06:38:18  ibbo
 * Initial
 *
 * Revision 1.1.1.1  2004/03/08 15:25:38  ibbo
 * Initial Import
 *
 * Revision 1.2  2004/02/07 17:42:51  ibbo
 * Updated
 *
 * Revision 1.1.1.1  2003/12/05 16:30:44  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/11/16 15:10:42  ibbo
 * Initial import
 *
 */

package org.jzkit.search.provider.SimpleExample;

import java.util.Properties;
import java.util.*;

import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.provider.iface.*;
import org.springframework.context.*;


/**
 * @author Ian Ibbotson
 * @version $Id: ExampleSearchable.java,v 1.7 2004/11/18 13:44:37 ibbo Exp $
 */ 
public class ExampleSearchable implements Searchable
{
  private Map record_archetypes = new HashMap();
  private ApplicationContext ctx;

  public ExampleSearchable() {
  }

  public ExampleSearchable(int random_delay, String behaviour) {
  }

  public void setRandomDelay(int random_delay) {
  }

  public int getRandomDelay() {
    return 0;
  }

  public void setBehaviour(String behaviour) {
  }

  public String getBehaviour() {
    return null;
  }

  public void close() {
  }

  public IRResultSet evaluate(IRQuery q) {
    return evaluate(q,null,null);
  }

  public IRResultSet evaluate(IRQuery q, Object user_info) {
    return evaluate(q, user_info, null);
  }

  public IRResultSet evaluate(IRQuery q, Object user_info, Observer[] observers) {
    ExampleResultSet result = new ExampleResultSet();
    result.setStatus(IRResultSetStatus.COMPLETE);
    return result;
  }

  public void setRecordArchetypes(Map record_syntax_archetypes) {
    this.record_archetypes = record_archetypes;
  }
                                                                                                                                          
  public Map getRecordArchetypes() {
    return record_archetypes;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }
}
