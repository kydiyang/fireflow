package org.fireflow.designer.editpanel.graph.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.fireflow.designer.editpanel.graph.actions.ActivityWithToolMarqueeHandler;
import org.fireflow.designer.editpanel.graph.ProcessDesignMediator;


public class ActivityWithToolMarqueeHandlerAction extends AbstractAction {
	private ProcessDesignMediator mediator = null;
	private ActivityWithToolMarqueeHandler marqueeHandler = null;

	public ActivityWithToolMarqueeHandlerAction(String argTtitle,
			ProcessDesignMediator md) {
		super(argTtitle);
		mediator = md;
		init();
	}

	public ActivityWithToolMarqueeHandlerAction(String argTtitle, ImageIcon icon,
			ProcessDesignMediator md) {
		super(argTtitle, icon);
		mediator = md;
		init();
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub
		mediator.getDesignPanel().setPortsVisible(false);
		mediator.getDesignPanel().setCursor(new Cursor(Cursor.HAND_CURSOR));
		mediator.getDesignPanel().setMarqueeHandler(marqueeHandler);

	}
	private void init() {
		marqueeHandler = new ActivityWithToolMarqueeHandler(mediator,
				mediator.getDesignPanel());
	}
}
