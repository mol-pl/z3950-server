/**
 *
 * MarcRecord
 *
 * @author Ian Ibbotson ( ibbo@k-int.com )
 * @version $Id: MarcRecord_codec.java,v 1.1.1.1 2004/06/18 06:38:44 ibbo Exp $
 *
 * Copyright:   Copyright (C) 2000, Knowledge Integration Ltd
 *
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the license, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite
 * 330, Boston, MA  02111-1307, USA.
 *    
 *
 */ 

package org.jzkit.z3950.util;

import org.jzkit.a2j.codec.runtime.*;

// Needs more work... Right now, it just returns a byte array stuffed with the record

// public class MarcRecord_codec extends base_codec implements InformationFragment
// Hmm... This class needs to produce an object implementing InformationFragment, not
// implement it itself.

public class MarcRecord_codec extends base_codec
{
  private static MarcRecord_codec me = null;

  public static MarcRecord_codec getCodec()
  {
    if ( me == null )
      me = new MarcRecord_codec();

    return me;
  }

  public Object serialize(SerializationManager sm,
                          Object type_instance,
                          boolean is_optional,
                          String type_name) throws java.io.IOException
  {
    Object retval = type_instance;

    System.err.println("MarcRecord_codec");

    // OK, we want to call the MarcRecord codec (Which is basically the any codec. This will generate
    // An octet array for us. We then want to create a marc record based on that octet stream.
    if ( sm.getDirection() == SerializationManager.DIRECTION_DECODE )
    {
      retval = sm.any_codec(retval, false);
    }   
    else
    {
      // We are encoding a marc record....
    }

    if ( ( retval == null ) && ( ! is_optional ) )
      throw new java.io.IOException("Missing mandatory member: "+type_name);

    return (Object)retval;
  }
}
