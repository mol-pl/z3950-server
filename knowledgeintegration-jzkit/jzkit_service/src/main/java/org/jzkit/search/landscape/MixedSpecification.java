/*
 * $Log: InfoTypeSpecification.java,v $
 * Revision 1.3  2005/08/24 08:40:49  ibbo
 * Improved cache hit mechanism for stateless search
 *
 * Revision 1.2  2004/09/23 14:07:48  ibbo
 * Updated InfoType to contain a collection of CollectionDescriptionDTO objects
 *
 * Revision 1.1  2004/09/23 13:36:28  ibbo
 * Added InfoTypeSpecification for selecting a landscape to search
 *
 * Revision 1.1  2004/09/16 10:00:08  ibbo
 * Added landscape classes
 *
 *
 */

package org.jzkit.search.landscape;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * Title:       MixedSpecification
 * @version:    $Id: InfoTypeSpecification.java,v 1.3 2005/08/24 08:40:49 ibbo Exp $
 * Copyright:   Copyright 2003, Ian Ibbotson, Knowledge Integration Ltd
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Description:
 *              
 */

public class MixedSpecification implements org.jzkit.search.LandscapeSpecification, java.io.Serializable {

  private List spec_list = new ArrayList();

  public MixedSpecification() {
  }

  public void addSpecification(org.jzkit.search.LandscapeSpecification spec) {
    spec_list.add(spec);
  }

  public List getSpecList() {
    return spec_list;
  }

  public String toString() {
    return spec_list.toString();
  }
}
