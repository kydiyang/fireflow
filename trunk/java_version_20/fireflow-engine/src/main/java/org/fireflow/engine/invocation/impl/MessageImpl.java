package org.fireflow.engine.invocation.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.fireflow.engine.invocation.Message;

public class MessageImpl<T> implements Message<T> {
	private T payload = null;
	private Properties headers = new Properties();
	
	@Override
	public Properties getHeaders() {
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
