package org.fireflow.model.resource;

import java.util.List;

public interface IResourceManager {
	public List<Application> getApplications();
	
	public List<Participant> getParticipants();
	
	public List<Form> getForms();
}
