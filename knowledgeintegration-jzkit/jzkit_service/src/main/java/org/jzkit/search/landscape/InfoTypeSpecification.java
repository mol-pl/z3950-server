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

/**
 * Title:       SimpleLandscapeSpecification
 * @version:    $Id: InfoTypeSpecification.java,v 1.3 2005/08/24 08:40:49 ibbo Exp $
 * Copyright:   Copyright 2003, Ian Ibbotson, Knowledge Integration Ltd
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Description:
 *              
 */

public class InfoTypeSpecification implements org.jzkit.search.LandscapeSpecification, java.io.Serializable {

  private String namespace;
  private String code;

  public InfoTypeSpecification() {
  }

  public InfoTypeSpecification(String namespace, String code) {
    this.namespace=namespace;
    this.code=code;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getCode() {
    return code;
  }

  public String toString() {
    return namespace+":"+code;
  }
}
