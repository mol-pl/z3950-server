// Title:       Transformer
// @version:    $Id: FragmentTransformer.java,v 1.2 2004/10/26 15:30:52 ibbo Exp $
// Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ibbo@k-int.com)
// Company:     Knowledge Integration Ltd.
// Description: 

//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2.1 of
// the license, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite
// 330, Boston, MA  02111-1307, USA.
// 


package org.jzkit.search.util.RecordConversion;

import java.util.Map;
import org.jzkit.search.util.RecordModel.InformationFragment;
import java.util.regex.*;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

/**
 *
 */
public abstract class FragmentTransformer {
  protected String from;
  protected String to;
  protected Map props;
  protected Map context;
  protected Pattern from_pattern;
  protected ApplicationContext ctx;
 private static Log log = LogFactory.getLog(FragmentTransformer.class);

  private FragmentTransformer() {
  }

  public FragmentTransformer(String from, 
                             String to, 
                             Map properties,  // Properties for this transform alone
                             Map context,
                             ApplicationContext ctx) {
    this.from = from;
    this.to = to;
    this.context = context;
    this.props = properties;
    this.ctx = ctx;

    try {
      this.from_pattern = Pattern.compile(from);
    }
    catch ( IllegalArgumentException iae ) {
      log.error("Problem compiling from pattern:",iae);
      throw new RuntimeException(iae);
    }
  }

  public void setProperty(String prop_name, Object value) {
    props.put(prop_name, value);
  }

  public Object getProperty(String prop_name) {
    return props.get(prop_name);
  }

  public String getFromSchema() {
    return from;
  }

  public String getToSchema() {
    return to;
  }

  public boolean isApplicableTo(String input_spec) {
    return from.equalsIgnoreCase(input_spec);
  }

  public abstract Object transform(Document input, Map additional_properties) throws FragmentTransformationException;
}
