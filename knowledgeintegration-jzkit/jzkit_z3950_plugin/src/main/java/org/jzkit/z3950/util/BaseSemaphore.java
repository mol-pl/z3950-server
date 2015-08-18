//Title:       BaseAvaialableSemaphore
//Version:     $Id: BaseSemaphore.java,v 1.3 2004/08/17 15:30:35 ibbo Exp $
//Copyright:   Copyright (C) 2001, Knowledge Integration Ltd (See the file LICENSE for details.)
//Author:      Ian Ibbotson ( ian.ibbotson@k-int.com )
//Company:     KI
//Description: 
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

package org.jzkit.z3950.util;

import org.jzkit.z3950.util.TimeoutExceededException;

/**
 * @author Ian Ibbotson ( ian.ibbotson@k-int.com )
 * @version $Id: BaseSemaphore.java,v 1.3 2004/08/17 15:30:35 ibbo Exp $
 *
 */
 

public abstract class BaseSemaphore
{
  public BaseSemaphore()
  {
    // cat.debug("New BaseSemaphore");
  }

  /**
   *  Wait until a condition is met or a timeout occours. A timeout of <= 0 specifies 
   *  wait indefinitely.
   *  @param timeout : seconds to wait
   */
  public void waitForCondition(int timeout) throws TimeoutExceededException
  {
    if ( timeout > 0 )
    {
      // cat.debug("Waiting for up to "+timeout+" before returning");
      // Wait until there is a response available with a refid that matches.. Need to add a timer here
      long endtime = System.currentTimeMillis() + timeout;

      // cat.debug("Endtime will be : "+endtime+" current time is "+System.currentTimeMillis());
 
      while ( System.currentTimeMillis() < endtime ) 
      {
        long waittime = endtime - System.currentTimeMillis();

        if ( isConditionMet() )
        {
          // cat.debug("Condition is met... break");
          break;
        }

        try
        {
          synchronized ( this )
          {
            wait ( waittime );
          }
        }
        catch( java.lang.InterruptedException ie )
        {
        }

        // cat.debug("Looping");
      }

      if ( !isConditionMet() )
      {
        throw new TimeoutExceededException();
      }
    }
    else if ( timeout < 0 ) // Timeout == -1 
    {
      // cat.debug("Wait until condition met");
      while ( !isConditionMet() )
      {
        try
	{
          synchronized ( this )
          {
            ((Object)this).wait( (long)10000 );
          }
	}
        catch( java.lang.InterruptedException ie )
        {
        }
      }
    }

  }

  public abstract boolean isConditionMet();

  public void destroy()
  {
  }

  protected void finalize()
  {
    // cat.debug("BaseSemaphore::finalize()");
  }

  protected void doNotify()
  {
    synchronized(this)
    {
      // cat.debug("BaseSemaphore::NotifyAll");
      this.notifyAll();
    }
  }
}
