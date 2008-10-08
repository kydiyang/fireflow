package org.fireflow.designer.editpanel.graph.actions;

import cn.bestsolution.tools.resourcesmanager.actions.*;

import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.ImageIcon;
import cn.bestsolution.tools.resourcesmanager.*;

import javax.swing.AbstractAction;
import org.fireflow.designer.editpanel.graph.actions.ActivityWithFormMarqueeHandler;
import org.fireflow.designer.editpanel.graph.ProcessDesignMediator;

public class ActivityWithFormMarqueeHandlerAction extends AbstractAction {
	private ProcessDesignMediator mediator = null;
	private ActivityWithFormMarqueeHandler marqueeHandler = null;

	public ActivityWithFormMarqueeHandlerAction(String argTtitle,
			ProcessDesignMediator md) {
		super(argTtitle);
		mediator = md;
		init();
	}

	public ActivityWithFormMarqueeHandlerAction(String argTtitle, ImageIcon icon,
			ProcessDesignMediator md) {
		super(argTtitle, icon);
		mediator = md;
		init();
	}

	public void actionPerformed(ActionEvent evt) {
		mediator.getDesignPanel().setPortsVisible(false);
		mediator.getDesignPanel().setCursor(new Cursor(Cursor.HAND_CURSOR));
		mediator.getDesignPanel().setMarqueeHandler(marqueeHandler);
	}

	private void init() {
		marqueeHandler = new ActivityWithFormMarqueeHandler(mediator,
				mediator.getDesignPanel());
	}

}