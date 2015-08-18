// Title:       Base Z3950 listener
// @version:    $Id: Z3950Listener.java,v 1.4 2004/09/16 17:06:31 ibbo Exp $
// Copyright:   Copyright (C) 2001 Knowledge Integration Ltd.
// @author:     Ian Ibbotson (ibbo@k-int.com)
// Company:     KI
// Description: 
//
 
 
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
     

/**
 * ZServer : Controller class for a Z39.50 Server
 *
 * @author Ian Ibbotson
 * @version $Id: Z3950Listener.java,v 1.4 2004/09/16 17:06:31 ibbo Exp $
 * 
 * This class listens on an identified socket. On new connections, a new instance
 * of ZServerAssociation is created with a parameter that identifies a concrete 
 * realisation of the IR Searchable class. The ZServerAssociation talks with the
 * abstract Searchable interface and uses that service to resolve Z39.50 services.
 */

package org.jzkit.z3950.server;

import java.net.*;
import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.*;
import org.springframework.context.ApplicationContextAware;

public class Z3950Listener extends Thread implements ApplicationContextAware {

  public static Log log = LogFactory.getLog(Z3950Listener.class);

  private int socket_timeout = 300000;  // 300 second default timeout
  private boolean running = true;
  private ServerSocket server_socket = null;
  private int port;
  private String bind_address;
  private String default_collections;
  private String backend_bean_name;
  private ApplicationContext ctx;

  public Z3950Listener() {
    log.debug("new Z3950Listener()");
  }

  public void run() {
    try {
      log.debug("Starting ZServer on port "+port+ " (timeout="+socket_timeout+")");
      server_socket = new ServerSocket(port);

      while ( running ) {

        log.debug("Waiting for connection");
        Socket socket = (Socket)server_socket.accept();
        socket.setSoTimeout(socket_timeout);

        // AppCtx config will determine if this object is a singleton or per assoc...
        log.debug("Obtaining handle to backend and creating association");
        Z3950NonBlockingBackend backend = (Z3950NonBlockingBackend) ctx.getBean(backend_bean_name);
        log.debug("new backend instance "+backend.hashCode());
        ZServerAssociation za = new ZServerAssociation(socket,backend,ctx);
      } 

      server_socket.close();
    }
    catch (java.io.IOException e) {
      log.error("Problem",e);
    }
  }

  public void shutdown(int shutdown_type) {
    this.running = false;

    switch ( shutdown_type ) {
      default:
        // Currently no special processing to join with or shutdown active associations.
	try
	{
	  server_socket.close();
	}
	catch ( java.io.IOException ioe )
	{
          // No special action
	}
	break;
    }

    try {
      this.join();
    }
    catch ( java.lang.InterruptedException ie )
    {
      // No action
    }
  }

  public int getPort()
  {
    return port;
  }

  public void setPort(int port)
  {
    this.port = port;
  }

  public String getBindAddress()
  {
    return bind_address;
  }

  public void setBindAddress(String bind_address)
  {
    this.bind_address = bind_address;
  }

  public String getDefault()
  {
    return default_collections;
  }

  public void setDefault(String default_collections)
  {
    this.default_collections = default_collections;
  }

  public String getBackendBeanName()
  {
    return backend_bean_name;
  }

  public void setBackendBeanName(String backend_bean_name)
  {
    this.backend_bean_name = backend_bean_name;
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }
}      

