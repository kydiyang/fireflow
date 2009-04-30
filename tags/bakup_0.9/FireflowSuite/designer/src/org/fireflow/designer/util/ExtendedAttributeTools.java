
package org.fireflow.designer.util;

import java.util.Map;

public class ExtendedAttributeTools{
	
	private ExtendedAttributeTools(){
		// no op
	}
	
	public static Integer getInteger(Map extendedAttributes, String key){
		String value = (String)extendedAttributes.get(key);
		if(value == null){
			return null;
		}
		
		return new Integer(value);
	}
	
}
