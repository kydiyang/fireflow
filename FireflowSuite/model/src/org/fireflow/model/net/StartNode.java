package org.fireflow.model.net;

import java.util.List;

import org.fireflow.model.WorkflowProcess;

public class StartNode extends Synchronizer{
    static final String name = "START_NODE";
    public StartNode(){
        
    }
	public StartNode(WorkflowProcess workflowProcess) {
		super(workflowProcess, name);
		// TODO Auto-generated constructor stub
	}
        

	/**
	 * 返回null值，表示无输入弧
	 */
	@Override
	public List<Transition> getEnteringTransitions(){
		return null;
	}
}
