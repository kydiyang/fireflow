package org.fireflow.designer.editpanel.graph.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.fireflow.designer.editpanel.graph.actions.EndNodeMarqueeHandler;
import org.fireflow.designer.editpanel.graph.ProcessDesignMediator;


public class EndNodeMarqueeHandlerAction extends AbstractAction {
	private ProcessDesignMediator mediator = null;
	private EndNodeMarqueeHandler marqueeHandler = null;

	public EndNodeMarqueeHandlerAction(String argTtitle,
			ProcessDesignMediator md) {
		super(argTtitle);
		mediator = md;
		init();
	}
	
	public EndNodeMarqueeHandlerAction(String argTtitle, ImageIcon icon,
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
		marqueeHandler = new EndNodeMarqueeHandler(mediator,
				mediator.getDesignPanel());
	}
}
