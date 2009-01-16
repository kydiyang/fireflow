package org.fireflow.engine.ou;

import java.util.List;
import org.fireflow.engine.EngineException;
import org.fireflow.kenel.KenelException;

public interface IAssignable {
	public void asignToActor(String id,boolean needSign) throws EngineException,KenelException;
        public void asignToActor(String id) throws EngineException,KenelException;
	public void asignToActors(List<String> ids) throws EngineException,KenelException;
}
