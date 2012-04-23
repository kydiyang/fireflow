package org.fireflow.engine.invocation;

import java.util.Map;

public interface Message<T> {
	public Map<String,String> getHeaders();
	public T getPayload();	
}
