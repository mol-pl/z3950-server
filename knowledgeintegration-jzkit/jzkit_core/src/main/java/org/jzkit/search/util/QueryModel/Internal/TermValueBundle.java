/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * published by the Free Software Foundation; either version 2.1 of
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
 */
package org.jzkit.search.util.QueryModel.Internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Title:       TermValueBundle
 * @version     $Id: $
 * Copyright:   Copyright (C) 1999 - 2006,Knowledge Integration Ltd.
 * @author      Rob Tice (rob.tice@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 * 
 * Created 		Nov 26, 2006
 * 
 * History:		
 * 				$Log:  $
 * 			
 * 				
 *
 */
public class TermValueBundle
{
	private List values = new ArrayList();
	private String string_value;
	
	public void setValues(List  values)
	{
		this.values=values;
	}
	
	public void setStringValue(String value)
	{
		string_value=value;
	}
	
	public void addValue(String value)
	{
		values.add(value);
	}
	
	public Iterator getValueIterator()
	{
		return values.iterator();
	}
	
	public String getStringValue()
	{
		return string_value;
	}

}
