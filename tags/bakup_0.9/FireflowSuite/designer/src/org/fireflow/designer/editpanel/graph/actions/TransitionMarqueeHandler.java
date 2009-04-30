/**
 * Copyright 2007-2008,Chen Nieyun
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation��
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.designer.editpanel.graph.actions;

import org.fireflow.designer.editpanel.graph.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;
import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.designer.datamodel.element.ActivityElement;
import org.fireflow.designer.datamodel.element.TransitionElement;
import org.fireflow.designer.datamodel.element.TransitionsElement;
import org.fireflow.designer.util.DesignerConstant;
import org.fireflow.model.ExtendedAttributeNames;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.PortView;

/**
 * @author chennieyun
 * 
 */
public class TransitionMarqueeHandler extends BasicMarqueeHandler {

    private ProcessDesignPanel designPanel = null;
    private ProcessDesignMediator mediator = null;
    private DefaultGraphCell startCell = null;
    private DefaultGraphCell endCell = null;
    // Holds the Start and the Current Point
    protected Point2D startPoint,  currentPoint;
    // Holds the First and the Current Port
    protected PortView firstPortView,  currentPortView;

    public TransitionMarqueeHandler(ProcessDesignMediator md,
            ProcessDesignPanel designPanel) {
        mediator = md;
        this.designPanel = designPanel;
    }

    public boolean isForceMarqueeEvent(MouseEvent event) {
        return true;
    }

    public void mousePressed(MouseEvent mouseEvent) {

        if (mouseEvent.getButton() == mouseEvent.BUTTON3) {
            mediator.refreshToolBar();
            return;
        }
        if (currentPortView != null && !mouseEvent.isConsumed()) {
            startPoint = designPanel.toScreen(currentPortView.getLocation(null));
            // Remember First Port
            firstPortView = currentPortView;

            startCell = (DefaultGraphCell) ((DefaultPort) firstPortView.getCell()).getParent();
            // Consume Event
            mouseEvent.consume();
        }
        super.mousePressed(mouseEvent);
    }

//	protected DefaultGraphCell getGraphVertexAt(Point point) {
//		// Find Cell at point (No scaling needed here)
//		Object cell = designPanel.getFirstCellForLocation(point.x, point.y);
//		if (cell != null && (cell instanceof DefaultGraphCell)) {
//			return (DefaultGraphCell) cell;
//		}
//		// Loop Children to find PortView
//		return null;
//	}
    public void mouseMoved(MouseEvent e) {
        // Check Mode and Find Port
        if (e != null && this.getSourcePortAt(e.getPoint()) != null && !e.isConsumed()) {
            // Set Cusor on Graph (Automatically Reset)
            designPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            // Consume Event
            this.currentPortView = this.getSourcePortAt(e.getPoint());
            e.consume();
        }
        // Call Superclass
        super.mouseMoved(e);
    }

    public void mouseDragged(MouseEvent e) {
        Point2D loc1 = designPanel.fromScreen((Point2D) e.getPoint());
        // Find Cell in Model Coordinates

        // If remembered Start Point is Valid
        if (startPoint != null && !e.isConsumed()) {
            // Fetch Graphics from Graph
            Graphics g = designPanel.getGraphics();
            // Xor-Paint the old Connector (Hide old Connector)
            paintConnector(Color.black, designPanel.getBackground(), g);
            // Reset Remembered Port
            currentPortView = getTargetPortAt(e.getPoint());
            // If Port was found then Point to Port Location
            if (currentPortView != null) {
                currentPoint = designPanel.toScreen(currentPortView.getLocation(null));
                endCell = (DefaultGraphCell) ((DefaultPort) currentPortView.getCell()).getParent();
            // Else If no Port was found then Point to Mouse Location
            } else {
                currentPoint = designPanel.snap(e.getPoint());
            // Xor-Paint the new Connector
            }
            paintConnector(designPanel.getBackground(), Color.black, g);
            // Consume Event
            e.consume();
        } else {

        }
    // Call Superclass
//		super.mouseDragged(e);
    }

    protected void paintConnector(Color fg, Color bg, Graphics g) {
        // Set Foreground
        g.setColor(fg);
        // Set Xor-Mode Color
        g.setXORMode(bg);
        // Highlight the Current Port
        // paintPort(designPanel.getGraphics());
        // If Valid First Port, Start and Current Point
        if (startPoint != null && currentPoint != null) {

            // Then Draw A Line From Start to Current Point
            g.drawLine((int) startPoint.getX(), (int) startPoint.getY(),
                    (int) currentPoint.getX(), (int) currentPoint.getY());
        }
    }

    public void mouseReleased(MouseEvent e) {
        // If Valid Event, Current and First Port
        if (e != null && !e.isConsumed() && currentPortView != null && firstPortView != null && firstPortView != currentPortView) { //

            // Then Establish Connection
            connect(startCell, endCell, firstPortView, currentPortView);
            // Consume Event
            e.consume();
        // Else Repaint the Graph
        } else {
            designPanel.repaint();
        // Reset Global Vars
        }

        firstPortView = currentPortView = null;
        startPoint = currentPoint = null;

        // Call Superclass

        super.mouseReleased(e);
    }

    public PortView getSourcePortAt(Point point) {
        // Scale from Screen to Model
        Point2D tmp = designPanel.fromScreen(new Point(point));
        // Find a Port View in Model Coordinates and Remember
        Object obj = designPanel.getPortViewAt(tmp.getX(), tmp.getY());
        return (PortView) obj;
    }

    protected PortView getTargetPortAt(Point point) {
        // Find Cell at point (No scaling needed here)
        Object cell = designPanel.getFirstCellForLocation(point.x, point.y);
        // Loop Children to find PortView
        for (int i = 0; i < designPanel.getModel().getChildCount(cell); i++) {
            // Get Child from Model
            Object tmp = designPanel.getModel().getChild(cell, i);
            // Get View for Child using the Graph's View as a Cell Mapper
            tmp = designPanel.getGraphLayoutCache().getMapping(tmp, false);
            // If Child View is a Port View and not equal to First Port
            if (tmp instanceof PortView && tmp != firstPortView) {

                // Return as PortView
                return (PortView) tmp;
            }
        }
        // No Port View found
        return getSourcePortAt(point);
    }

    protected void connect(DefaultGraphCell fromCell, DefaultGraphCell toCell, PortView fromPortView, PortView toPortView) {
        System.out.println("Inside TransitionMarqueeHandler.connect()");

        IFPDLElement fromElement = (IFPDLElement) fromCell.getUserObject();
        IFPDLElement toElement = (IFPDLElement) toCell.getUserObject();
        DefaultPort fromPort = (DefaultPort) fromCell.getChildAt(0);
        System.out.println("FromElement Type is "+fromElement.getElementType());  
        System.out.println("toElement Type is "+toElement.getElementType());
        if (fromElement.getElementType().equals(IFPDLElement.START_NODE)) {
            if (toElement.getElementType().equals(IFPDLElement.ACTIVITY)) {
                //如果没有输入边，则建立连接
                if (!hasEnteringEdge(toCell)) {
                    StartNode startNode = (StartNode) fromElement.getContent();

                    Activity toActivity = (Activity) toElement.getContent();

                    ProcessGraphModel model = (ProcessGraphModel) this.designPanel.getModel();
                    IFPDLElement transElem = createTransition(model, startNode.getId(),startNode.getSn(), toActivity.getId(),toActivity.getSn(), null);
                    startNode.getLeavingTransitions().add((Transition)transElem.getContent());
                    toActivity.setEnteringTransition((Transition)transElem.getContent());
                }
                else{
                    JOptionPane.showMessageDialog(designPanel, "每个Activity只能有一个输入Transition！","错误",JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (fromElement.getElementType().equals(IFPDLElement.SYNCHRONIZER)) {
            if (toElement.getElementType().equals(IFPDLElement.ACTIVITY)) {
                //如果activity 没有输入边，则建立连接
                System.out.println("Inside TransitionMarqueeHandler.connect(),connect synchronizer and activity, !hasEnteringEdge(toCell)=" + !hasEnteringEdge(toCell));
                if (!hasEnteringEdge(toCell)) {
                    Synchronizer synchronizer = (Synchronizer) fromElement.getContent();

                    Activity toActivity = (Activity) toElement.getContent();
                    ProcessGraphModel model = (ProcessGraphModel) this.designPanel.getModel();
                    IFPDLElement transElem = createTransition(model, synchronizer.getId(), synchronizer.getSn(),toActivity.getId(),toActivity.getSn(), null);
                    synchronizer.getLeavingTransitions().add((Transition)transElem.getContent());
                    toActivity.setEnteringTransition((Transition)transElem.getContent());
//					connect(transElement,(Port)fromCell.getChildAt(0),(Port)toCell.getChildAt(0));

                }else{
                    JOptionPane.showMessageDialog(designPanel, "每个Activity只能有一个输入Transition！","错误",JOptionPane.ERROR_MESSAGE);                    
                }
            }
        } else if (fromElement.getElementType().equals(IFPDLElement.ACTIVITY)) {
            if (toElement.getElementType().equals(IFPDLElement.SYNCHRONIZER) ||
                    toElement.getElementType().equals(IFPDLElement.END_NODE)) {
                //如果Activity没有输出边则建立连接
//                System.out.println("=====连接activity 到 endnode hasLeavingEdge(fromCell)="+hasLeavingEdge(fromCell));
                if (!hasLeavingEdge(fromCell)) {
                    Activity fromActivity = (Activity) fromElement.getContent();
                    Synchronizer synchronizer = (Synchronizer) toElement.getContent();
                    ProcessGraphModel model = (ProcessGraphModel) this.designPanel.getModel();

                    IFPDLElement transElem = createTransition(model, fromActivity.getId(),fromActivity.getSn(), synchronizer.getId(),synchronizer.getSn(), null);
                    fromActivity.setLeavingTransition((Transition)transElem.getContent());
                    synchronizer.getEnteringTransitions().add((Transition)transElem.getContent());
                }
                else{
                    JOptionPane.showMessageDialog(designPanel, "每个Activity只能有一个输出Transition！","错误",JOptionPane.ERROR_MESSAGE);                    
                }
                
            } else if (toElement.getElementType().equals(IFPDLElement.ACTIVITY)) {
                //自动增加一个synchronizer，并建立连接。
                System.out.println("Inside TransitionMarqueeHandler::connect from activity to activity");
                if (!hasEnteringEdge(toCell) && !hasLeavingEdge(fromCell)) {
                    //计算中点，自动增加一个synchronizer
                    Point2D location1 = fromPortView.getLocation();
                    Point2D location2 = toPortView.getLocation();
                    Point2D synchronizerLocation = new Point((int) ((location1.getX() + location2.getX()) / 2),
                            (int) ((location1.getY() + location2.getY()) / 2));
                    Map<String,String> attributeMap = new HashMap<String,String>();
                    int radius = Integer.parseInt(DesignerConstant.DEFAULT_SYNCHRONIZER_RADIUS);
                    double d =  radius/Math.sqrt(2d);
                    int x = (int)(synchronizerLocation.getX()-d);
                    int y = (int)(synchronizerLocation.getY()-d);
                    attributeMap.put(ExtendedAttributeNames.BOUNDS_X,Integer.toString(x));
                    attributeMap.put(ExtendedAttributeNames.BOUNDS_Y,Integer.toString(y));
                    attributeMap.put(ExtendedAttributeNames.BOUNDS_WIDTH, DesignerConstant.DEFAULT_SYNCHRONIZER_RADIUS);
                    attributeMap.put(ExtendedAttributeNames.BOUNDS_HEIGHT, DesignerConstant.DEFAULT_SYNCHRONIZER_RADIUS);

                    ProcessGraphModel model = (ProcessGraphModel) this.designPanel.getModel();
                    IFPDLElement synchronizer =
                            model.getWorkflowProcessElement().createChild(IFPDLElement.SYNCHRONIZER, attributeMap);


                    GraphCell vertex = model.getGraphCell4FPDLElement(synchronizer);

                    this.connect(fromCell, (DefaultGraphCell) vertex, null, null);
                    this.connect((DefaultGraphCell) vertex, toCell, null, null);
                }else{
                    JOptionPane.showMessageDialog(designPanel, "每个Activity只能有一个输入/输出Transition！","错误",JOptionPane.ERROR_MESSAGE);                    
                }
            }
        }
        designPanel.repaint();
    }

    protected boolean hasEnteringEdge(DefaultGraphCell toCell) {
        boolean hasEnteredEdge = false;
        ActivityElement toActivityElem = (ActivityElement) toCell.getUserObject();
        Activity toActivity = (Activity) toActivityElem.getContent();
        DefaultPort toPort = (DefaultPort) toCell.getChildAt(0);

        Iterator edgesIter = toPort.edges();
        while (edgesIter.hasNext()) {
            DefaultEdge edge = (DefaultEdge) edgesIter.next();
            TransitionElement transElem = (TransitionElement) edge.getUserObject();
            Transition trans = (Transition) transElem.getContent();
            if (trans.getToNode().getId().equals(toActivity.getId())) {
                hasEnteredEdge = true;
                break;
            }
        }
        return hasEnteredEdge;
    }

    protected boolean hasLeavingEdge(DefaultGraphCell fromCell) {
        boolean hasEnteredEdge = false;
        ActivityElement fromActivityElem = (ActivityElement) fromCell.getUserObject();
        Activity fromActivity = (Activity) fromActivityElem.getContent();
        DefaultPort fromPort = (DefaultPort) fromCell.getChildAt(0);

        Iterator edgesIter = fromPort.edges();
        while (edgesIter.hasNext()) {
            DefaultEdge edge = (DefaultEdge) edgesIter.next();
            TransitionElement transElem = (TransitionElement) edge.getUserObject();
            Transition trans = (Transition) transElem.getContent();
            if (trans.getFromNode().getId().equals(fromActivity.getId())) {
                hasEnteredEdge = true;
                break;
            }
        }
        return hasEnteredEdge;
    }

    public IFPDLElement createTransition(ProcessGraphModel model, String fromNodeId,String fromSn, String toNodeId,String toNodeSn, Map<String,String> extendAttributes) {
        if (extendAttributes == null) {
            extendAttributes = new HashMap<String,String>();
        }
        extendAttributes.put(TransitionsElement.FROM_NODE_ID, fromNodeId);
        extendAttributes.put(TransitionsElement.TO_NODE_ID, toNodeId);
//        extendAttributes.put(TransitionsElement.FROM_SN, fromSn);
//        extendAttributes.put(TransitionsElement.TO_SN, toSn);
//        extendAttributes.put("FROM_NODE", fromNode);
//        extendAttributes.put("TO_NODE", toNode);
        IFPDLElement transElem = model.getWorkflowProcessElement().createChild(IFPDLElement.TRANSITION, extendAttributes);

        return transElem;
    }
}
