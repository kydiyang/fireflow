package org.fireflow.model.reference;

public class Form extends AbstractReference {
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
