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

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Title:       HumanReadableBundle
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
public class HumanReadableQueryBundle implements Serializable
{
	private Map term_map 			    = new HashMap();
	private StringBuilder query_builder = new StringBuilder();

	public void addTerm(String access_point, String term)
	{
		List terms = (List) term_map.get(access_point);
		if(terms==null)
		{
			terms= new ArrayList();
			term_map.put(access_point, terms);
		}
		terms.add(term);
	}
	
	public void addAll(String access_point, List term_list)
	{
		List terms = (List) term_map.get(access_point);
		if(terms==null)
		{
			terms= new ArrayList();
			term_map.put(access_point, terms);
		}
		terms.addAll(terms);
	}
	
	
	
	public void append(String string)
	{
		query_builder.append(string);
	}
	
	public Iterator getTermAccessPointIterator()
	{
		return term_map.keySet().iterator();
	}
	
	public List getValues(String access_point)
	{
		return (List) term_map.get(access_point);
	}
	
	public String getHumanReadableQuery()
	{
		return query_builder.toString();
	}
	
	public String toString()
	{
		return term_map.toString();
	}
	
	

}
