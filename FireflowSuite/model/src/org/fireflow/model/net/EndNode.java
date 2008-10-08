package org.fireflow.model.net;

import java.util.List;

import org.fireflow.model.WorkflowProcess;

public class EndNode extends Synchronizer {

	public EndNode(WorkflowProcess workflowProcess, String name) {
		super(workflowProcess, name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 返回null表示无输出弧。
	 */
	@Override
	public List<Transition> getLeavingTransitions() {
		return null;
	}
}
