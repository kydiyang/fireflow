/**
 * Copyright 2003-2008,Chen Nieyun
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.model;

import java.util.Map;

/**
 * @author chennieyun
 *
 */
public interface IWFElement {
        public String getSn();
        public void setSn(String s);
    
	public String getId() ;
	
	public String getName() ;
	public void setName(String name);
	
	public String getDisplayName();
	public void setDisplayName(String displayName);
	
	public String getDescription();
	public void setDescription(String description);
	
	public IWFElement getParent();

	public void setParent(IWFElement parent) ;
	
	public Map<String,String> getExtendedAttributes() ;
}
