
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

package  org.jzkit.search.provider.z3950;


import org.jzkit.search.provider.iface.*;
import java.util.*;
import org.jzkit.a2j.codec.util.*;
import org.springframework.context.*;

/**
 *
 * A Z39.50 Target Description
 *
 * @version:    $Id: Z3950ServiceFactory.java,v 1.12 2005/08/22 06:21:03 ibbo Exp $
 * Copyright:   Copyright (C) 1999-2002 Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 */
public class Z3950ServiceFactory implements SearchServiceFactory {

  private String host;
  private int port;
  private String default_record_syntax;
  private String default_record_schema;
  private String default_element_set_name;
  private int pref_message_size = 1048576;
  private int exceptional_message_size = 1048576;
  private String charset_encoding="UTF-8";
  private boolean use_reference_id=true;
  private ZAuthenticationMethod auth_method;
  private Map record_archetypes = new HashMap();
  private ApplicationContext ctx;
  private boolean do_charset_neg = true;
  private static final String[] prop_names = new String[] { "host","port" };

  public Z3950ServiceFactory() {
  }

  public Z3950ServiceFactory(String host, int port) {
    this();
    this.host = host;
    this.port = port;
  }

  public Properties toProps()
  {
    Properties result = new Properties();
    result.setProperty("ServiceHost",host);
    result.setProperty("ServicePort",""+port);

    if ( charset_encoding != null )
      result.setProperty("charset",charset_encoding);

    return result;
  }

  public Searchable newSearchable(org.jzkit.search.provider.iface.ServiceUserInformation user_info) throws SearchException {

    if ( ctx == null )
      throw new SearchException("ApplicationContext not set for this factory. appctx is required");

    Z3950Origin result = new Z3950Origin();
    result.setApplicationContext(ctx);
    // System.err.println("New searchable, enc="+charset_encoding);

    try {
      org.apache.commons.beanutils.BeanUtils.copyProperties(result,this);
      if ( auth_method != null ) {
        result.setAuthType(auth_method.getAuthType());
        result.setServiceUserPrincipal(auth_method.getUserId());
        result.setServiceUserGroup(auth_method.getGroupId());
        result.setServiceUserCredentials(auth_method.getCredentials());
      }
      result.setRecordArchetypes(record_archetypes);
    }
    catch ( java.lang.IllegalAccessException iae ) {   
      throw new SearchException("Problem creating Z3950 Origin",iae);
    }
    catch ( java.lang.reflect.InvocationTargetException ite ) {
      throw new SearchException("Problem creating Z3950 Origin",ite);
    }

    return result;
  }

  public Searchable newSearchable() throws SearchException {
    return newSearchable(null);
  }

  /**
   */
  public String getHost() { 
    return host; 
  }

  /**
   */
  public int getPort() { 
    return port; 
  }

  /**
   */
  public String getDefaultRecordSyntax() { 
    return default_record_syntax; 
  }

  public String getDefaultRecordSchema() {
    return default_record_schema;
  }
                                                                                                                                          
  public String getDefaultElementSetName() {
    return default_element_set_name;
  }
                                                                                                                                          
  /**
   */
  public int getPrefMessageSize() { 
    return pref_message_size; 
  }

  /**
   */
  public int getExceptionalMessageSize() { 
    return exceptional_message_size; 
  }

  /**
   */
  public String getCharsetEncoding() { 
    return charset_encoding; 
  }

  /**
   */
  public boolean getUseReferenceId() { 
    return use_reference_id; 
  }

  public void setHost(String host) {
     this.host = host; 
  }

  public void setPort(int port) {
     this.port = port; 
  }

  public void setDefaultRecordSyntax(String default_record_syntax) {
     this.default_record_syntax = default_record_syntax; 
  }

  public void setDefaultRecordSchema(String default_record_schema) {
    this.default_record_schema = default_record_schema;
  }
                                                                                                                                          
  public void setDefaultElementSetName(String default_element_set_name) {
    this.default_element_set_name = default_element_set_name;
  }

  public void setPrefMessageSize(int pref_message_size) {
     this.pref_message_size = pref_message_size; 
  }

  public void setExceptionalMessageSize(int exceptional_message_size) {
     this.exceptional_message_size = exceptional_message_size; 
  }

  public void setCharsetEncoding(String charset_encoding) {
     this.charset_encoding = charset_encoding; 
  }

  public void setUseReferenceId(boolean use_reference_id) {
     this.use_reference_id = use_reference_id; 
  }

  public ZAuthenticationMethod getAuthMethod() {
    return auth_method;
  }

  public void setAuthMethod(ZAuthenticationMethod auth_method) {
    this.auth_method = auth_method;
  }

  public Map getRecordArchetypes() {
    return record_archetypes;
  }

  public void setRecordArchetypes(Map record_archetypes) {
    this.record_archetypes = record_archetypes;
  }

  public void setDoCharsetNeg(boolean do_charset_neg) {
    this.do_charset_neg = do_charset_neg;
  }

  public boolean getDoCharsetNeg() {
    return do_charset_neg;
  }



  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public ApplicationContext getApplicationContext() {
    return ctx;
  }

  public String[] getPropertyNames() {
    return prop_names;
  }
}
