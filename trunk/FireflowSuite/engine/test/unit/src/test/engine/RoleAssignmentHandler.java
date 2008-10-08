package test.engine;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.ou.IAssignable;
import org.fireflow.engine.ou.IAssignmentHandler;

public class RoleAssignmentHandler implements IAssignmentHandler {

	public void assign(IAssignable asignable, String performerName) throws EngineException {
		// TODO Auto-generated method stub
		asignable.asignToActor(performerName);
	}

}
