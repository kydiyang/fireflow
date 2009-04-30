package org.fireflow.designer.editpanel.graph.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.fireflow.designer.editpanel.graph.ProcessDesignMediator;
import org.fireflow.designer.editpanel.graph.actions.SynchronizerMarqueeHandler;


public class SynchronizerMarqueeHandlerAction extends AbstractAction {
	private ProcessDesignMediator mediator = null;
	private SynchronizerMarqueeHandler marqueeHandler = null;

	public SynchronizerMarqueeHandlerAction(String argTtitle,
			ProcessDesignMediator md) {
		super(argTtitle);
		mediator = md;
		init();
	}
	
	public SynchronizerMarqueeHandlerAction(String argTtitle, ImageIcon icon,
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
		marqueeHandler = new SynchronizerMarqueeHandler(mediator,
				mediator.getDesignPanel());
	}

}
