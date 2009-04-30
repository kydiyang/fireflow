package org.fireflow.model.resource;

public class Form extends AbstractResource {
	private String uri = null;
	
	public Form(String name){
		this.setName(name);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	
}
