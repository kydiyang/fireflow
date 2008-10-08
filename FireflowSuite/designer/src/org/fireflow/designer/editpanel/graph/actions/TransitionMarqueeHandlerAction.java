package org.fireflow.designer.editpanel.graph.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.fireflow.designer.editpanel.graph.ProcessDesignMediator;
import org.fireflow.designer.editpanel.graph.actions.TransitionMarqueeHandler;


public class TransitionMarqueeHandlerAction extends AbstractAction {
	private ProcessDesignMediator mediator = null;
	private TransitionMarqueeHandler marqueeHandler = null;

	public TransitionMarqueeHandlerAction(String argTtitle,
			ProcessDesignMediator md) {
		super(argTtitle);
		mediator = md;
		init();
	}
	
	public TransitionMarqueeHandlerAction(String argTtitle, ImageIcon icon,
			ProcessDesignMediator md) {
		super(argTtitle, icon);
		mediator = md;
		init();
	}	
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub
//		mediator.getDesignPanel().setCursor(new Cursor(Cursor.HAND_CURSOR));
		mediator.getDesignPanel().setPortsVisible(true);
		mediator.getDesignPanel().setMarqueeHandler(marqueeHandler);
	}
	private void init() {
		marqueeHandler = new TransitionMarqueeHandler(mediator,
				mediator.getDesignPanel());
	}

}
