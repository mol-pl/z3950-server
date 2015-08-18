package org.jzkit.search;

/*
 * $Log: SearchSessionFactory.java,v $
 * Revision 1.10  2005/10/05 08:57:47  ibbo
 * Added props to transformations
 *
 * Revision 1.9  2005/09/13 09:51:30  ibbo
 * Updated
 *
 * Revision 1.8  2005/08/24 08:40:49  ibbo
 * Improved cache hit mechanism for stateless search
 *
 * Revision 1.7  2005/08/22 08:48:44  ibbo
 * More work on stateless search session
 *
 * Revision 1.6  2005/08/20 09:40:00  ibbo
 * updated
 *
 * Revision 1.5  2005/08/19 15:09:57  ibbo
 * Checked in first parts of stateless search api for PNS support
 *
 * Revision 1.4  2005/08/14 08:53:56  ibbo
 * Added result set id
 *
 * Revision 1.3  2005/08/14 08:44:07  ibbo
 * Added stateless search api
 *
 * Revision 1.2  2004/08/26 09:39:51  ibbo
 * Updated
 *
 * Revision 1.1  2004/07/08 14:39:06  ibbo
 * Updated
 *
 */

import org.jzkit.search.provider.iface.SearchException;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.QueryModel.*;
import java.util.Map;



/**
 * Title:       SearchSessionFactory
 * @version:    $Id: SearchSessionFactory.java,v 1.10 2005/10/05 08:57:47 ibbo Exp $
 * Copyright:   Copyright 2003,2004 Ian Ibbotson, Knowledge Integration Ltd
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Description:
 *              
 */
public interface SearchSessionFactory
{
  public SearchSession getSearchSession();
  public SearchSession getSearchSession(String session_type);

  public StatelessSearchResultsPageDTO getResultsPageFor(String result_set_id,
                                                         QueryModel model,
                                                         LandscapeSpecification landscape,
                                                         int first_hit,
                                                         int num_hits,
                                                         RecordFormatSpecification request_spec,
                                                         ExplicitRecordFormatSpecification display_spec,
                                                         Map additional_properties) throws SearchException, 
                                                                                           org.jzkit.search.util.ResultSet.IRResultSetException, 
                                                                                           org.jzkit.search.util.QueryModel.InvalidQueryException;

  public StatelessSearchResultsPageDTO getResultsPageFor(String result_set_id,
                                                         String model_type,
                                                         String model_str,
                                                         String landscape_str,
                                                         int first_hit,
                                                         int num_hits,
                                                         RecordFormatSpecification request_spec,
                                                         ExplicitRecordFormatSpecification display_spec,
                                                         Map additional_properties) throws SearchException, 
                                                                                           org.jzkit.search.util.ResultSet.IRResultSetException, 
                                                                                           org.jzkit.search.util.QueryModel.InvalidQueryException;

  public ExplainInformationDTO explain();

  public java.util.List<SearchSession> activeSessions();
  public SearchSession getSession(String id);
}


