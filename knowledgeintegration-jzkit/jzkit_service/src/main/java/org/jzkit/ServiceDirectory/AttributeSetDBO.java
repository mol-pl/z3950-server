package org.jzkit.ServiceDirectory;

import java.util.*;
import org.apache.commons.beanutils.*;
import org.springframework.context.ApplicationContext;
import javax.persistence.*;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.IndexColumn;
import org.jzkit.search.provider.iface.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jzkit.search.util.QueryModel.Internal.AttrValue;

/**
 * An object which describes the properties of a search service. Different subclasses of this
 * interface will provide information about different kinds of resources, from Z39.50 servers to
 * SRW and SRU services.
 * These objects should be lightweight, for example,
 * Searchable s = new Z3950SearchServiceDescription("z3950://test.server:210").newSearchable();
 *
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * Company:     Knowledge Integration Ltd.
 * License:     A license.txt file should is distributed along with this software
 *
 * @author Ian Ibbotson
 * @version $Id: SearchServiceDescriptionDBO.java,v 1.17 2005/10/18 12:13:18 ibbo Exp $
 * @see org.jzkit.SearchProvider.iface.Searchable
 */ 
@Entity
@Table(name="JZ_ATTR_SET_HEADER")
public class AttributeSetDBO {

  private Long id;
  private Set<AttrValue> attrs = new HashSet();

  @Id
  @Column(name="ID")
  @GeneratedValue(strategy=GenerationType.AUTO)
  public Long getId() {
    return id;
  }
                                                                                                                                        
  public void setId(Long id) {
    this.id = id;
  }

  @CollectionOfElements(fetch=javax.persistence.FetchType.EAGER)
  @org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
  @JoinTable(name="JZ_ATTR_SET_MEMBER",joinColumns=@JoinColumn(name="ATTR_SET_HEADER_FK"))
  public Set<AttrValue> getAttrs() {
    return attrs;
  }

  public void setAttrs(Set<AttrValue> explain_use_indexes) {
    this.attrs = attrs;
  }
}
