package org.fireflow.engine.invocation.impl;

import java.util.HashMap;
import java.util.Map;

import org.fireflow.engine.invocation.Message;

public class MessageImpl<T> implements Message<T> {
	private T payload = null;
	private Map<String,String> headers = new HashMap<String,String>();
	
	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public T getPayload() {
		return payload;
	}
	
	public void setPayload(T pld){
		this.payload = pld;
	}

}
