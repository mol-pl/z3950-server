// Title:       Transformer
// @version:    $Id: DOMProducerTransformer.java,v 1.3 2004/10/26 15:30:52 ibbo Exp $
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

import java.util.*;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jzkit.search.util.RecordModel.InformationFragment;
import org.jzkit.search.util.RecordModel.ExplicitRecordFormatSpecification;
import org.springframework.context.*;

                                                                                                                                          
/**
 * This transformer allows us to create a new DOM document based on an existing fragment
 * which is not itself a dom tree.
 * The input to the transformation is an empty dom tree, the source fragment is made
 * available to the XSL transformation as a java object property. From there, xsl
 * extensions must be used to create node-lists which can access the java object and
 * create a resultant document.
 */
public class DOMProducerTransformer extends XSLFragmentTransformer {

  public DOMProducerTransformer(String from, 
                                String to, 
                                Map props, 
                                Map context,
                                ApplicationContext ctx) throws javax.xml.transform.TransformerConfigurationException {
    super(from, to, props, context,ctx);
  }

  public Object transform(Document input, Map additional_properties) throws FragmentTransformationException {
    try {
      DocumentBuilder docBuilder = dbf.newDocumentBuilder();
      Document result_doc = docBuilder.newDocument();
      performTransformation(new DOMSource(input), new DOMResult(result_doc), additional_properties);
      return result_doc;
    }
    catch ( javax.xml.parsers.ParserConfigurationException pce ) {
      pce.printStackTrace();
      throw new FragmentTransformationException(pce.toString());
    }
  }
}
