package org.fireflow.designer.editpanel.graph.actions;

import cn.bestsolution.tools.resourcesmanager.actions.*;

import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.ImageIcon;
import cn.bestsolution.tools.resourcesmanager.*;

import javax.swing.AbstractAction;
import org.fireflow.designer.editpanel.graph.*;
import org.jgraph.graph.BasicMarqueeHandler;

public class SelectionAction extends AbstractAction {
	private ProcessDesignMediator mediator = null;
	private BasicMarqueeHandler marqueeHandler = null;

	public SelectionAction(String argTtitle, ProcessDesignMediator md) {
		super(argTtitle);
		mediator = md;
		init();
	}

	public SelectionAction(String argTtitle, ImageIcon icon,
			ProcessDesignMediator md) {
		super(argTtitle, icon);
		mediator = md;
		init();
	}

	public void actionPerformed(ActionEvent evt) {
		mediator.getDesignPanel().setPortsVisible(false);
		mediator.getDesignPanel().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		mediator.getDesignPanel().setMarqueeHandler(marqueeHandler);

	}

	private void init() {
//		marqueeHandler = mediator.getOriginalMarqueeHandler();
//                System.out.println("=====原marqueehandler is "+marqueeHandler.getClass().getName());
//            System.out.println("===SelectAction : 创建FireflowDefaultMarqueeHandler");
            marqueeHandler = new FireflowDefaultMarqueeHandler(mediator,mediator.getDesignPanel());
	}

}