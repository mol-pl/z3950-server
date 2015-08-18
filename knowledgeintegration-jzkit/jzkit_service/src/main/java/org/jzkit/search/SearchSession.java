/*
 * $Log: SearchSession.java,v $
 * Revision 1.6  2004/10/03 16:31:54  ibbo
 * SimpleAggregatingResultSet now implements TransformingIRResultSet so you can ask it
 * to transform result records
 *
 * Revision 1.5  2004/09/24 16:46:21  ibbo
 * All final result set objects now implement IRResultSet directly instead of
 * inheriting the implementation from the Abstract base class.. This seems to
 * work better for trmi
 *
 * Revision 1.4  2004/09/24 12:23:15  ibbo
 * Migrated service API to standard IRResultSet interface... As it should be.
 *
 * Revision 1.3  2004/09/16 09:58:03  ibbo
 * Upgrade unit tests, work around older rmi classes, now using sprnig to provider
 * remote server capability
 *
 * Revision 1.2  2004/07/05 12:12:22  ibbo
 * Updated
 *
 * Revision 1.1.1.1  2004/06/18 06:38:51  ibbo
 * Initial
 *
 * Revision 1.1.1.1  2004/03/08 15:25:38  ibbo
 * Initial Import
 *
 * Revision 1.2  2004/02/20 09:35:57  ibbo
 * Updated for JAXR z server
 *
 * Revision 1.1.1.1  2003/12/05 16:30:43  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/11/16 15:10:42  ibbo
 * Initial import
 *
 *
 */

package org.jzkit.search;

import org.jzkit.search.util.ResultSet.TransformingIRResultSet;
import org.jzkit.search.provider.iface.SearchException;
import org.jzkit.search.util.QueryModel.*;
import org.jzkit.search.util.RecordModel.RecordFormatSpecification;
import java.util.Collection;
import java.rmi.RemoteException;

/**
 * Title:       SearchSession
 * @version:    $Id: SearchSession.java,v 1.6 2004/10/03 16:31:54 ibbo Exp $
 * Copyright:   Copyright 2003, Ian Ibbotson, Knowledge Integration Ltd
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Description:
 *              
 */

public interface SearchSession {
  public TransformingIRResultSet search(LandscapeSpecification landscape, 
                                        QueryModel query_model,
                                        DeduplicationModel deduplication_model,
                                        SortModel sort_model,
                                        RecordFormatSpecification rfs ) throws SearchException;

  public void setType(String session_type);
  public String getType();

  public void close();

  public String getSessionId();
}
