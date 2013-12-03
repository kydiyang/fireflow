/**
 * Copyright 2007-2011 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.firesoa.common.schema;


/**
 * 
 * @author 非也 www.firesoa.com
 * 
 *
 */
public enum NameSpaces {
	JAVA("java","http://jcp.org/en/jsr/detail?id=270"),
	XSD("xsd","http://www.w3.org/2001/XMLSchema"),
	;
	private String prefix;
	private String url ;
	private NameSpaces(String prefix,String url){
		this.prefix =  prefix;
		this.url = url;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getUri() {
		return url;
	}
	public void setUri(String uri) {
		this.url = uri;
	}
}
