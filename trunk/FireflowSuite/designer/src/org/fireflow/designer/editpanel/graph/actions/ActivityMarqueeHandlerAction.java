package org.fireflow.designer.editpanel.graph.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.fireflow.designer.editpanel.graph.actions.ActivityMarqueeHandler;
import org.fireflow.designer.editpanel.graph.ProcessDesignMediator;

//import cn.bestsolution.tools.resourcesmanager.MainMediator;

public class ActivityMarqueeHandlerAction extends AbstractAction {
	private ProcessDesignMediator mediator = null;
	private ActivityMarqueeHandler marqueeHandler = null;

	public ActivityMarqueeHandlerAction(String argTtitle,
			ProcessDesignMediator md) {
		super(argTtitle);
		mediator = md;
		init();
	}
	
	public ActivityMarqueeHandlerAction(String argTtitle, ImageIcon icon,
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
		marqueeHandler = new ActivityMarqueeHandler(mediator,
				mediator.getDesignPanel());
	}
}
