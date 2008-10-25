package org.fireflow.designer.editpanel.graph;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import org.fireflow.designer.datamodel.IFPDLElement;
//import org.fireflow.designer.datamodel.element.FPDLNodesElement;
//import org.fireflow.designer.datamodel.element.TaskElement;
import org.fireflow.designer.datamodel.element.TransitionElement;
import org.fireflow.designer.datamodel.element.WorkflowProcessElement;
//import org.fireflow.designer.outline.FPDLOutlineTree;
//import org.fireflow.designer.outline.FPDLOutlineTreeNode;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.ExtendedAttributeNames;
import org.fireflow.model.IWFElement;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;
import org.jgraph.JGraph;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;



//import cn.bestsolution.tools.resourcesmanager.MainMediator;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

public class ProcessGraphModel extends DefaultGraphModel implements NodeListener {

    private WorkflowProcessElement workflowProcessElement = null;
    private ExplorerManager explorerManager = null;
    Map<String, DefaultGraphCell> cellsMap = new Hashtable<String, DefaultGraphCell>();
    private JGraph designPanel = null;

    public ProcessGraphModel(ExplorerManager explorerManager) {
        this.explorerManager = explorerManager;
        this.revert();

        this.addGraphModelListener(new GraphModelListener() {

            public void graphChanged(GraphModelEvent arg0) {

                Object[] changed = arg0.getChange().getChanged();

                if (changed != null) {
                    for (int i = 0; i < changed.length; i++) {
                        Object obj = changed[i];
                        if (obj instanceof DefaultEdge) {
//                            System.out.println("====================edge changed==========");
                            updateWorkflowEdageLocation((DefaultEdge) obj);
                        } else if (obj instanceof DefaultPort) {
//                            System.out.println("====================port changed==========");
                            DefaultPort port = (DefaultPort) obj;

                            if (port != null) {
                                Set edges = port.getEdges();
                                Iterator iterator = edges.iterator();
                                while (iterator.hasNext()) {
                                    DefaultEdge edge = (DefaultEdge) iterator.next();
                                    updateWorkflowEdageLocation(edge);
                                }
                            }
                        } else if ((obj instanceof DefaultGraphCell) && !(obj instanceof DefaultEdge)) {
//                            System.out.println("====================cell changed==========");
                            DefaultGraphCell cell = (DefaultGraphCell) obj;
                            updateWorkflowNodeLocation(cell);
                        }
                    }
                }

                FPDLDataObject fpdlDataObject = workflowProcessElement.getLookup().lookup(FPDLDataObject.class);
                fpdlDataObject.modelUpdatedFromUI();
                fpdlDataObject.setModified(true);
            }
        });
    }

    public void updateWorkflowNodeLocation(DefaultGraphCell cell) {
//        System.out.println("changed cell is " + cell.getClass().getName());
        Rectangle2D rect = GraphConstants.getBounds(cell.getAttributes());

//        System.out.println("rect is " + rect);
        IFPDLElement fpdlElement = (IFPDLElement) cell.getUserObject();
        IWFElement wfElement = (IWFElement) fpdlElement.getContent();
//        System.out.println("fpdlElemtn is " + fpdlElement);
        Map<String, String> extendedAttributes = wfElement.getExtendedAttributes();


        extendedAttributes.put(ExtendedAttributeNames.BOUNDS_X, Integer.toString((int) rect.getX()));
        extendedAttributes.put(ExtendedAttributeNames.BOUNDS_Y, Integer.toString((int) rect.getY()));
        extendedAttributes.put(ExtendedAttributeNames.BOUNDS_WIDTH, Integer.toString((int) rect.getWidth()));
        extendedAttributes.put(ExtendedAttributeNames.BOUNDS_HEIGHT, Integer.toString((int) rect.getHeight()));
    }

    public void updateWorkflowEdageLocation(DefaultEdge edge) {

        IFPDLElement fpdlElement = (IFPDLElement) edge.getUserObject();
        IWFElement wfElement = (IWFElement) fpdlElement.getContent();

//        CellView cellView = this.getGraphLayoutCache().getMapping(edge, false);
//        if (cellView == null) {
//            return;
//        }
//        System.out.println("edge.source is " + edge.getSource().getClass().getName());

        List l = GraphConstants.getPoints(edge.getAttributes());
//        List l = GraphConstants.getPoints(edge.getAttributes());

        if (l != null) {
//            System.out.println("======l.size() is " + l.size());
            StringBuffer sbuf = new StringBuffer();
            for (int i = 1; i < l.size() - 1; i++) {
//                System.out.println("point is "+l.get(i).getClass().getName());
                if (l.get(i) instanceof Point2D) {
                    Point2D p = (Point2D) l.get(i);
                    sbuf.append("(");
                    sbuf.append((int) p.getX());
                    sbuf.append(",");
                    sbuf.append((int) p.getY());
                    sbuf.append(")");
                }
//                else if (l.get(i) instanceof PortView) {
//                    PortView pv = (PortView) l.get(i);
//                    
//                    Point2D p = (Point2D) pv.getLocation((EdgeView)cellView);
//                    sbuf.append("(");
//                    sbuf.append((int) p.getX());
//                    sbuf.append(",");
//                    sbuf.append((int) p.getY());
//                    sbuf.append(")");
//                }

            }
//            System.out.println("edge_point_list is " + sbuf.toString());
            wfElement.getExtendedAttributes().
                    put(ExtendedAttributeNames.EDGE_POINT_LIST,
                    sbuf.toString());
        } else {
//            System.out.println("hahah l is null");
        }

        Point2D labelPositionPoint = GraphConstants.getLabelPosition(edge.getAttributes());
        if (labelPositionPoint != null) {
            String labelPosition = "(" +
                    (int) labelPositionPoint.getX() + "," +
                    (int) labelPositionPoint.getY() + ")";

            wfElement.getExtendedAttributes().
                    put(ExtendedAttributeNames.LABEL_POSITION, labelPosition);
        } else {
//            System.out.println("haha labelPositionPoint is null");
        }
    }

    public WorkflowProcessElement getWorkflowProcessElement() {
        return workflowProcessElement;
    }

    public GraphCell getGraphCell4FPDLElement(IFPDLElement fpdlElement) {

        IWFElement workflowElem = (IWFElement) fpdlElement.getContent();
        return (GraphCell) cellsMap.get(workflowElem.getSn());
    }

    public GraphCell getGraphCellByWFElementSn(String elementSn) {
//        System.out.println("====Inside ProcessGraphModel..cellsMap size is "+cellsMap.size());
        return (GraphCell) cellsMap.get(elementSn);
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public void revert() {
//        System.out.println("====___====explorerManager is "+explorerManager);
//        System.out.print("====____===explorerManager.getRootContext() is "+explorerManager.getRootContext());
//        System.out.println("====___====explorerManager.getRootContext().getChildren() is "+explorerManager.getRootContext().getChildren());
//        System.out.println("========== explorerManager.getRootContext().getChildren().getNodes() is "+ explorerManager.getRootContext().getChildren().getNodes());
        if (explorerManager == null || explorerManager.getRootContext() == null ||
                explorerManager.getRootContext().getChildren() == null ||
                explorerManager.getRootContext().getChildren().getNodes().length <= 0) {
            return;
        }

        workflowProcessElement = (WorkflowProcessElement) explorerManager.getRootContext().getChildren().getNodes()[0];
        workflowProcessElement.addNodeListener(this);
        WorkflowProcess workflowProcess = (WorkflowProcess) workflowProcessElement.getContent();

        Node activitiesNode = workflowProcessElement.getChildren().findChild(IFPDLElement.ACTIVITIES);
        activitiesNode.addNodeListener(this);

        Node synchronizersNode = workflowProcessElement.getChildren().findChild(IFPDLElement.SYNCHRONIZERS);
        synchronizersNode.addNodeListener(this);

        Node endNodesNode = workflowProcessElement.getChildren().findChild(IFPDLElement.END_NODES);
        endNodesNode.addNodeListener(this);

        Node transitionsNode = workflowProcessElement.getChildren().findChild(IFPDLElement.TRANSITIONS);
        transitionsNode.addNodeListener(this);

        Node[] nodes = new Node[]{activitiesNode, synchronizersNode, endNodesNode};

        ConnectionSet cs = new ConnectionSet();
        Map<DefaultGraphCell, Map> attributes = new Hashtable<DefaultGraphCell, Map>();


        try { 
            Map<String, String> cellAttribute = null;
            //1 开始节点
            IFPDLElement startElement = (IFPDLElement) workflowProcessElement.getChildren().findChild(IFPDLElement.START_NODE);
            if (startElement != null) {
                DefaultGraphCell startVertex = new DefaultGraphCell(startElement);
                startVertex.addPort();
                cellAttribute = startVertex.getAttributes();
                GraphConstants.setBounds(cellAttribute, this.parseRectangle(
                        ((IWFElement) startElement.getContent()).getExtendedAttributes(),
                        new Rectangle(0, 0, 20, 20)));
                attributes.put(startVertex, cellAttribute);

                cellsMap.put(((IWFElement) startElement.getContent()).getSn(), startVertex);
            }
            //2 其他节点
            for (int m = 0; m < nodes.length; m++) {
                Node node = nodes[m];
                Node[] children = node.getChildren().getNodes();
                for (int i = 0; children != null && i < children.length; i++) {
                    IFPDLElement fpdlElement = (IFPDLElement) children[i];
                    ((AbstractNode) fpdlElement).addNodeListener(this);
                    DefaultGraphCell vertex = new DefaultGraphCell(fpdlElement);
                    vertex.addPort();

                    cellAttribute = vertex.getAttributes();
                    IWFElement wfElement = ((IWFElement) fpdlElement.getContent());
                    if (wfElement instanceof Synchronizer) {
                        GraphConstants.setBounds(cellAttribute, this.parseRectangle(
                                wfElement.getExtendedAttributes(),
                                new Rectangle(0, 0, 20, 20)));
                    } else {
                        GraphConstants.setBounds(cellAttribute, this.parseRectangle(
                                wfElement.getExtendedAttributes(),
                                new Rectangle(0, 0, 100, 60)));
                    }

                    attributes.put(vertex, cellAttribute);

                    cellsMap.put(((IWFElement) fpdlElement.getContent()).getSn(),
                            vertex);
                }
            }

            //3 转移
            AttributeMap transitionStyle = new AttributeMap();
            Node[] children = transitionsNode.getChildren().getNodes();
            for (int i = 0; i < children.length; i++) {

                TransitionElement transElement = (TransitionElement) children[i];
//				transElement.addContentChangeListner(this);
                DefaultEdge edge = new DefaultEdge(transElement);
                Transition trans = (Transition) transElement.getContent();

                if (cellsMap.get(trans.getFromNode().getSn()) != null &&
                        cellsMap.get(trans.getToNode().getSn()) != null) {
                    transitionStyle = edge.getAttributes();
                    GraphConstants.setLineEnd(transitionStyle,
                            GraphConstants.ARROW_SIMPLE);
//                    System.out.println("=========开始解析labelPosition==========");
                    Point2D point = parseLabelPosition(trans.getExtendedAttributes());
                    if (point != null) {
//                        System.out.println("=====point is "+point.getX());
                        GraphConstants.setLabelPosition(transitionStyle, point);

                    }

                    List pointList = this.parsePointList(trans.getExtendedAttributes(),
                            cellsMap.get(trans.getFromNode().getSn()), cellsMap.get(trans.getToNode().getSn()));
                    if (pointList != null) {
                        GraphConstants.setPoints(transitionStyle, pointList);
                    }

                    Object port1 = (cellsMap.get(trans.getFromNode().getSn())).getChildAt(0);

                    Object port2 = (cellsMap.get(trans.getToNode().getSn())).getChildAt(0);

                    attributes.put(edge, transitionStyle);

                    cs.connect(edge, port1, port2);
                    cellsMap.put(trans.getSn(), edge);

                }
            }

            this.insert(cellsMap.values().toArray(), attributes, cs, null,
                    null);
        } catch (Exception ex) {
            ex.printStackTrace();

        }

    }

    public void childrenAdded(NodeMemberEvent arg0) {

        Node[] nodesAdded = arg0.getDelta();
        IFPDLElement parent = (IFPDLElement) arg0.getSource();
        IFPDLElement child = (IFPDLElement) nodesAdded[0];
        ((AbstractNode) child).addNodeListener(this);


        if (child.getElementType().equals(IFPDLElement.START_NODE) ||
                child.getElementType().equals(IFPDLElement.ACTIVITY) ||
                child.getElementType().equals(IFPDLElement.SYNCHRONIZER) ||
                child.getElementType().equals(IFPDLElement.END_NODE)) {

            DefaultGraphCell vertex = new DefaultGraphCell(child);
            vertex.addPort();
            cellsMap.put(((IWFElement) child.getContent()).getSn(), vertex);
            Map extendAttributes = ((IWFElement) child.getContent()).getExtendedAttributes();
            Rectangle bounds = parseRectangle(extendAttributes);
            Map<DefaultGraphCell, Map> attributeMap = new HashMap<DefaultGraphCell, Map>();
            Map cellAttribute = vertex.getAttributes();
            GraphConstants.setBounds(cellAttribute, bounds);
            attributeMap.put(vertex, cellAttribute);
            Object[] insert = {vertex};
            this.insert(insert, attributeMap, null,
                    null, null);

            designPanel.setSelectionCells(insert);
        } else if (child.getElementType().equals(IFPDLElement.TRANSITION)) {
            Transition trans = (Transition) child.getContent();
            DefaultGraphCell fromCell = cellsMap.get(trans.getFromNode().getSn());
            DefaultGraphCell toCell = cellsMap.get(trans.getToNode().getSn());
            DefaultEdge edge = new DefaultEdge(child);
            cellsMap.put(trans.getSn(), edge);

            ConnectionSet cs = new ConnectionSet();

            Map<DefaultGraphCell, Map> attributes = new Hashtable<DefaultGraphCell, Map>();
            AttributeMap transitionStyle = new AttributeMap();
            GraphConstants.setLineEnd(transitionStyle,
                    GraphConstants.ARROW_SIMPLE);
//            System.out.println("==from cell is " + fromCell);
//            System.out.println("from.getUserObject is " + fromCell.getUserObject());
//            System.out.println("==Inside ProcessGraphModel:: childrenAdded(): edge  " + ((AbstractNode) edge.getUserObject()).getName());
//            System.out.println("==Inside ProcessGraphModel:: childrenAdded(): fromcell  " + ((AbstractNode) fromCell.getUserObject()).getName());
//            System.out.println("==Inside ProcessGraphModel:: childrenAdded(): toCell  " + ((AbstractNode) toCell.getUserObject()).getName());
            cs.connect(edge, fromCell.getChildAt(0), toCell.getChildAt(0));

            attributes.put(edge, transitionStyle);
            Object[] cells = new Object[1];
            cells[0] = edge;

            this.insert(cells, attributes, cs, null, null);
            designPanel.setSelectionCells(cells);
        }
//            else if (child.getElementName().equals(IFPDLElement.MANUAL_TASK) ||
//                    child.getElementName().equals(IFPDLElement.SUBFLOW_TASK) ||
//                    child.getElementName().equals(IFPDLElement.TOOL_TASK)) {
//                child.addContentChangeListner(this);
//            }
    }

    public void childrenRemoved(NodeMemberEvent arg0) {
        Node[] nodesDeleted = arg0.getDelta();
//        IFPDLElement parent = (IFPDLElement) arg0.getSource();
//        IFPDLElement child = (IFPDLElement) nodesDeleted[0];
        for (int i = 0; nodesDeleted != null && i < nodesDeleted.length; i++) {
            IFPDLElement child = (IFPDLElement) nodesDeleted[i];
            String id = ((IWFElement) child.getContent()).getId();
            String sn = ((IWFElement) child.getContent()).getSn();
            DefaultGraphCell cell = this.cellsMap.get(sn);
            List list = cell.getChildren();
            List objToDelete = new Vector();
            if (cell != null) {
                objToDelete.add(cell);
            }
            if (list != null) {
                objToDelete.addAll(list);
            }
            if (cell != null) {
                this.remove(objToDelete.toArray());
            }
        }
    }

    public void childrenReordered(NodeReorderEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void nodeDestroyed(NodeEvent arg0) {

    }

    public void propertyChange(PropertyChangeEvent evt) {

    }
    /*
    public void onContentChanged(ContentChangedEvent e) {
    if (e == null) {
    return;
    }
    if (e.getActionType() == -1) {
    return;
    }
    if (e.getActionType() == ContentChangedEvent.INSERT_ELEMENT) {
    } else if (e.getActionType() == e.DELETE_SELF) {
    IFPDLElement userObj = (IFPDLElement) e.getSource();
    //			System.out.println("Going to Delete "+userObj.getElementName()+"="+userObj);
    GraphCell cell = this.getGraphCell4FPDLElement(userObj);
    if (cell != null) {
    Object[] cells = new Object[1];
    cells[0] = cell;
    this.remove(cells);
    IWFElement wfElement = (IWFElement) userObj.getContent();
    cellsMap.remove(wfElement.getId());
    } else {
    if (userObj.getElementName().equals(IFPDLElement.MANUAL_TASK) ||
    userObj.getElementName().equals(IFPDLElement.SUBFLOW_TASK) ||
    userObj.getElementName().equals(IFPDLElement.TOOL_TASK)) {
    TaskElement taskElement = (TaskElement) userObj;
    GraphCell parentCell = this.getGraphCell4FPDLElement(taskElement.getParentElement());
    Object[] cells = new Object[1];
    cells[0] = parentCell;
    this.cellsChanged(cells);
    }
    }
    }
    }
     */

    private List parsePointList(Map extendAttributes, DefaultGraphCell sourceCell, DefaultGraphCell targetCell) {
        String pointListStr = (String) extendAttributes.get(ExtendedAttributeNames.EDGE_POINT_LIST);
        if (pointListStr == null) {
            return null;
        }
        List<Point2D> l = new ArrayList<Point2D>();
        Rectangle2D rect1 = GraphConstants.getBounds(sourceCell.getAttributes());
        Point point1 = new Point((int) (rect1.getX() + rect1.getWidth() / 2), (int) (rect1.getY() + rect1.getHeight() / 2));
        l.add(point1);

        StringTokenizer tokenizer = new StringTokenizer(pointListStr, ")");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = token + ")";
            Point2D p = this.parsePoint(token);
            l.add(p);
        }
        Rectangle2D rect2 = GraphConstants.getBounds(targetCell.getAttributes());
        Point point2 = new Point((int) (rect2.getX() + rect2.getWidth() / 2), (int) (rect2.getY() + rect2.getHeight() / 2));
        l.add(point2);

        return l;
    }

    private Point2D parseLabelPosition(Map extendAttributes) {
        String positionStr = (String) extendAttributes.get(ExtendedAttributeNames.LABEL_POSITION);

        System.out.println("=========positionStr is " + positionStr);

        if (positionStr == null) {
            return null;
        }

        return parsePoint(positionStr);
    }

    private Point2D parsePoint(String pointStr) {
        int index1 = pointStr.indexOf("(");
        int index2 = pointStr.indexOf(",");
        int index3 = pointStr.indexOf(")");
        if (index1 == -1 || index2 == -1 || index3 == -1) {
            return null;
        }
        String xStr = pointStr.substring(index1 + 1, index2);
        String yStr = pointStr.substring(index2 + 1, index3);
        try {
            Point point = new Point(Integer.parseInt(xStr), Integer.parseInt(yStr));
            return point;
        } catch (Exception e) {
            return null;
        }
    }

    private Rectangle parseRectangle(Map extendAttributes, Rectangle defaultRectangle) {
        Rectangle rect = parseRectangle(extendAttributes);
        if (rect == null) {
//            System.out.println("========rect is null");
            return defaultRectangle;
        }
        return rect;
    }

    private Rectangle parseRectangle(Map extendAttributes) {
//        System.out.println("==========extendAttributes size is "+extendAttributes.size());
        Iterator itr = extendAttributes.keySet().iterator();

        while (itr.hasNext()) {
            System.out.println(itr.next());
        }
        String xStr = (String) extendAttributes.get(ExtendedAttributeNames.BOUNDS_X);
//        System.out.println("====xStr="+xStr);
        int x = 0;

        try {
            x = Integer.parseInt(xStr);
        } catch (Exception ex) {
            return null;
        }

        String yStr = (String) extendAttributes.get(ExtendedAttributeNames.BOUNDS_Y);
        int y = 0;

        try {
            y = Integer.parseInt(yStr);
        } catch (Exception ex) {
            return null;
        }

        String widthStr = (String) extendAttributes.get(ExtendedAttributeNames.BOUNDS_WIDTH);
        int width = 20;

        try {
            width = Integer.parseInt(widthStr);
        } catch (Exception ex) {
            return null;
        }

        String heightStr = (String) extendAttributes.get(ExtendedAttributeNames.BOUNDS_HEIGHT);
        int height = 20;

        try {
            height = Integer.parseInt(heightStr);
        } catch (Exception ex) {
            return null;
        }


        Rectangle bounds = new Rectangle(x, y, width, height);
        return bounds;
    }

    public void setDesignPanel(JGraph designPanel) {
        this.designPanel = designPanel;
    }
}

