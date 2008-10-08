package org.fireflow.engine.ou;

import java.util.List;

public interface IAssignable {
	public void asignToActor(String id);
	public void asignToActors(List<String> ids);
}
