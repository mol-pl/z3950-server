/**
 * Title:       DOMCreatable
 * @version:    $Id: StringCreateable.java,v 1.1.1.1 2004/06/18 06:38:17 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ibbo@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: Is an instance of this fragment creatable from a DOM tree
 */

/*
 * $Log: StringCreateable.java,v $
 * Revision 1.1.1.1  2004/06/18 06:38:17  ibbo
 * Initial
 *
 * Revision 1.1.1.1  2004/03/08 15:25:38  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/12/05 16:30:43  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/11/16 15:10:42  ibbo
 * Initial import
 *
 */

package org.jzkit.search.util.RecordModel;

import org.w3c.dom.Document;

/**
 * InformationFragment: An interface that describes an object able to participate in
 * a retrieval operation. Ideally, objects implementing this class will make the job of
 * dealing with heterogeneous result items ( XML, GRS & Marc records, SUTRS, etc) eaiser.
 */
public interface StringCreateable
{
  public void setSourceString(String s);
}
