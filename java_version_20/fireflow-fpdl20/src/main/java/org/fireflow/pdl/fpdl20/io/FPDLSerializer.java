/**
 * Copyright 2007-2011 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.pdl.fpdl20.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Expression;
import org.fireflow.model.data.Property;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.io.Util4Serializer;
import org.fireflow.model.io.resource.ResourceSerializer;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.pdl.fpdl20.diagram.ActivityShape;
import org.fireflow.pdl.fpdl20.diagram.AssociationShape;
import org.fireflow.pdl.fpdl20.diagram.CommentShape;
import org.fireflow.pdl.fpdl20.diagram.Diagram;
import org.fireflow.pdl.fpdl20.diagram.EndNodeShape;
import org.fireflow.pdl.fpdl20.diagram.GroupShape;
import org.fireflow.pdl.fpdl20.diagram.LaneShape;
import org.fireflow.pdl.fpdl20.diagram.MessageFlowShape;
import org.fireflow.pdl.fpdl20.diagram.PoolShape;
import org.fireflow.pdl.fpdl20.diagram.RouterShape;
import org.fireflow.pdl.fpdl20.diagram.StartNodeShape;
import org.fireflow.pdl.fpdl20.diagram.TransitionShape;
import org.fireflow.pdl.fpdl20.diagram.WorkflowNodeShape;
import org.fireflow.pdl.fpdl20.diagram.basic.Bounds;
import org.fireflow.pdl.fpdl20.diagram.basic.Circle;
import org.fireflow.pdl.fpdl20.diagram.basic.Label;
import org.fireflow.pdl.fpdl20.diagram.basic.Line;
import org.fireflow.pdl.fpdl20.diagram.basic.Point;
import org.fireflow.pdl.fpdl20.diagram.basic.Rectangle;
import org.fireflow.pdl.fpdl20.diagram.basic.Shape;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.PointImpl;
import org.fireflow.pdl.fpdl20.diagram.style.BoundsStyle;
import org.fireflow.pdl.fpdl20.diagram.style.FulfilStyle;
import org.fireflow.pdl.fpdl20.diagram.style.LineStyle;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.EndNode;
import org.fireflow.pdl.fpdl20.process.Import;
import org.fireflow.pdl.fpdl20.process.Router;
import org.fireflow.pdl.fpdl20.process.StartNode;
import org.fireflow.pdl.fpdl20.process.SubProcess;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.event.EventListenerDef;
import org.fireflow.pdl.fpdl20.process.features.Feature;
import org.fireflow.pdl.fpdl20.process.features.endnode.NormalEndFeature;
import org.fireflow.pdl.fpdl20.process.features.endnode.ThrowCompensationFeature;
import org.fireflow.pdl.fpdl20.process.features.endnode.ThrowFaultFeature;
import org.fireflow.pdl.fpdl20.process.features.endnode.ThrowTerminationFeature;
import org.fireflow.pdl.fpdl20.process.features.router.impl.AndJoinAndSplitRouterFeature;
import org.fireflow.pdl.fpdl20.process.features.router.impl.CustomizedRouterFeature;
import org.fireflow.pdl.fpdl20.process.features.router.impl.DynamicRouterFeature;
import org.fireflow.pdl.fpdl20.process.features.router.impl.OrJoinOrSplitRouterFeature;
import org.fireflow.pdl.fpdl20.process.features.startnode.CatchCompensationFeature;
import org.fireflow.pdl.fpdl20.process.features.startnode.CatchFaultFeature;
import org.fireflow.pdl.fpdl20.process.features.startnode.NormalStartFeature;
import org.fireflow.pdl.fpdl20.process.features.startnode.TimerStartFeature;
import org.fireflow.pdl.fpdl20.process.features.startnode.WebserviceStartFeature;
import org.firesoa.common.schema.NameSpaces;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author 非也 nychen2000@163.com
 * @version 2.0
 */
public class FPDLSerializer implements FPDLNames {

    public static final String DEFAULT_FPDL_VERSION = "2.0";
    public static final String DEFAULT_VENDOR = "www.firesoa.com";

    private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    static{
    	documentBuilderFactory.setNamespaceAware(true);
    }

	public void serialize(WorkflowProcess workflowProcess, OutputStream out)
			throws IOException, SerializerException ,InvalidModelException{
		try {
			Document document = serializeToDOM(workflowProcess);

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");

			transformer.transform(new DOMSource(document),
					new StreamResult(out));
			out.flush();
		} catch (TransformerConfigurationException e) {
			throw new SerializerException(e);
		} catch (TransformerException e) {
			throw new SerializerException(e);
		} finally {

		}

	}

    public Document serializeToDOM(WorkflowProcess workflowProcess)
            throws SerializerException,InvalidModelException{
    	DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new SerializerException(e);
		}
    	Document document = documentBuilder.newDocument();

        // serialize the Package
        Element workflowProcessElement = document.createElementNS(FPDL_NS_URI,
        		FPDL_NS_PREFIX+":"+WORKFLOW_PROCESS);
        
        document.appendChild(workflowProcessElement);
        
//        workflowProcessElement.addNamespace(FPDL_NS_PREFIX, FPDL_NS_URI);
//        workflowProcessElement.addNamespace(SERVICE_NS_PREFIX, SERVICE_NS_URI);
//        workflowProcessElement.addNamespace(RESOURCE_NS_PREFIX, RESOURCE_NS_URI);
//        workflowProcessElement.addNamespace(XSD_NS_PREFIX, XSD_URI);
//        workflowProcessElement.addNamespace(XSI_NS_PREFIX, XSI_URI);

//        QName qname = df.createQName(
//        	      "schemaLocation", "xsi", XSI_URI);
//        workflowProcessElement.setAttribute(qname, FPDL_SCHEMA_LOCATION+" "+SERVICE_SCHEMA_LOCATION+" "+RESOURCE_SCHEMA_LOCATION);
      
      
        workflowProcessElement.setAttribute(ID, workflowProcess.getId());
        workflowProcessElement.setAttribute(NAME, workflowProcess.getName());
        workflowProcessElement.setAttribute(DISPLAY_NAME, workflowProcess.getDisplayName());
        workflowProcessElement.setAttribute(BIZ_CATEGORY, workflowProcess.getBizCategory());

        Util4Serializer.addElement(workflowProcessElement, DESCRIPTION,
                workflowProcess.getDescription()); 
        
        //序列化import信息
        writeImports4Service(workflowProcess.getImportsForService(),workflowProcessElement);
        writeImports4Resource(workflowProcess.getImportsForResource(),workflowProcessElement);
        this.writeImport4Process(workflowProcess.getImportsForProcess(),workflowProcessElement);
        
        
        //序列化 service信息
        ServiceParser.writeServices(workflowProcess.getLocalServices(),workflowProcessElement);   
        
        
        ResourceSerializer.writeResources(workflowProcess.getLocalResources(),workflowProcessElement);

        Element subflowsElement = Util4Serializer.addElement(workflowProcessElement, SUBFLOWS);
        List<SubProcess> subflowList = workflowProcess.getLocalSubflows();
        if (subflowList!=null && subflowList.size()>0){
        	for (SubProcess subflow : subflowList){
        		writeSubflow(subflow, subflowsElement);
        	}
        	
        }
        
        Element diagramsElement = Util4Serializer.addElement(workflowProcessElement, DIAGRAMS);
        List<Diagram> diagramsList = workflowProcess.getDiagrams();
        if (diagramsList!=null && diagramsList.size()>0){
        	for (Diagram diagram : diagramsList){
        		writeDiagram(diagram,diagramsElement);
        	}
        }
        
        return document;
    }
    
    protected void writeDiagram(Diagram diagram,Element diagramsElement){
    	Element diagramElm = Util4Serializer.addElement(diagramsElement, DIAGRAM);
    	diagramElm.setAttribute(ID, diagram.getId());
    	diagramElm.setAttribute(REF, diagram.getWorkflowElementRef());
    	diagramElm.setAttribute(DIRECTION, diagram.getDirection());
    	
    	List<PoolShape> poolList = diagram.getPools();
    	if (poolList!=null && poolList.size()>0){
    		for (PoolShape pool : poolList){
    			writePoolShape(pool,diagramElm);
    		}    		
    	}
    	

    	List<GroupShape> groupList = diagram.getGroups();
    	if (groupList!=null){
    		for (GroupShape group : groupList){
    			this.writeGroupShape(group, diagramElm);
    		}
    	}
    	
    	
    	List<WorkflowNodeShape> workflowNodeShapeList = diagram.getWorkflowNodeShapes();
    	if(workflowNodeShapeList!=null && workflowNodeShapeList.size()>0){
    		for (WorkflowNodeShape node : workflowNodeShapeList){
    			writeNodeShape(node,diagramElm);
    		}
    	}
    	


    	
    	List<CommentShape> commentList = diagram.getComments();
    	if (commentList!=null && commentList.size()>0){
    		for (CommentShape comment : commentList){
    			writeCommentShape(comment,diagramElm);
    		}
    	}
    	
    	List<TransitionShape> transitionShapeList = diagram.getTransitions();
    	if (transitionShapeList!=null && transitionShapeList.size()>0){
    		for (TransitionShape transition : transitionShapeList){
    			writeTransitionShape(transition,diagramElm);
    		}
    	}
    	
    	
    	List<AssociationShape> associationList = diagram.getAssociations();
    	if (associationList!=null && associationList.size()>0){
    		for (AssociationShape association : associationList){
    			writeAssociationShape(association,diagramElm);
    		}
    	}
    	
    	
    	List<MessageFlowShape> messageFlowShapeList = diagram.getMessageFlows();
    	if (messageFlowShapeList!=null && messageFlowShapeList.size()>0){
    		for (MessageFlowShape messageFlowShape : messageFlowShapeList){
    			writeMessageFlowShape(messageFlowShape,diagramElm);
    		}
    	}
    }
    

    
    protected void writeMessageFlowShape(MessageFlowShape messageFlowShape,Element messageFlowShapesElm){
    	Element messageFlowElm = Util4Serializer.addElement(messageFlowShapesElm, CONNECTOR);
    	messageFlowElm.setAttribute(TYPE, MESSAGEFLOW);
    	messageFlowElm.setAttribute(ID, messageFlowShape.getId());
    	
    	if (messageFlowShape.getFromDiagramElement()!=null){
    		messageFlowElm.setAttribute(FROM, messageFlowShape.getFromDiagramElement().getId());
    	}
    	
    	if (messageFlowShape.getToDiagramElement()!=null){
    		messageFlowElm.setAttribute(TO,messageFlowShape.getToDiagramElement().getId());
    	}
    	
    	this.writeShape(messageFlowShape.getShape(), messageFlowElm);
    }
    
    protected void writeAssociationShape(AssociationShape association, Element associationShapesElm){
    	Element elem = Util4Serializer.addElement(associationShapesElm, CONNECTOR);
    	elem.setAttribute(TYPE, ASSOCIATION);
    	elem.setAttribute(ID, association.getId());
    	if (association.getFromDiagramElement()!=null){
    		elem.setAttribute(FROM, association.getFromDiagramElement().getId());
    	}
    	if (association.getToDiagramElement()!=null){
    		elem.setAttribute(TO, association.getToDiagramElement().getId());
    	}
    	this.writeShape(association.getShape(), elem);
    }
    
    protected void writeCommentShape(CommentShape commentshape,Element commentShapesElm){
    	Element commentShapeElm = Util4Serializer.addElement(commentShapesElm, CHILD);
    	commentShapeElm.setAttribute(TYPE, COMMENT);
    	commentShapeElm.setAttribute(ID, commentshape.getId());
    	this.writeShape(commentshape.getShape(), commentShapeElm);
    }
    
	protected void writePoolShape(PoolShape pool, Element poolshapesElm) {
		Element poolElm = Util4Serializer.addElement(poolshapesElm, CHILD);
		poolElm.setAttribute(TYPE, POOL);
		poolElm.setAttribute(ID, pool.getId());
		if (!StringUtils.isEmpty(pool.getWorkflowElementRef())) {
			poolElm.setAttribute(REF, pool.getWorkflowElementRef());
		}

		Shape shape = pool.getShape();
		writeShape(shape, poolElm);

		List<LaneShape> laneList = pool.getLanes();
		if (laneList != null && laneList.size() > 0) {
			for (LaneShape lane : laneList) {
				writeLaneShape(lane, poolElm);
			}
		}
	}
    
    protected void writeTransitionShape(TransitionShape transitionShape,Element transitionsElm){
    	Element transitionElm = Util4Serializer.addElement(transitionsElm, CONNECTOR);
    	transitionElm.setAttribute(TYPE, TRANSITION);
    	transitionElm.setAttribute(ID, transitionShape.getId());
    	transitionElm.setAttribute(REF,transitionShape.getWorkflowElementRef());
    	
    	if (transitionShape.getFromWorkflowNodeShape()!=null){
    		transitionElm.setAttribute(FROM, transitionShape.getFromWorkflowNodeShape().getId());
    	}
    	if (transitionShape.getToWorkflowNodeShape()!=null){
    		transitionElm.setAttribute(TO, transitionShape.getToWorkflowNodeShape().getId());
    	}    	
    	
    	writeShape(transitionShape.getShape(),transitionElm);
    }

	protected void writeGroupShape(GroupShape groupShape, Element parentElem) {
		Element groupElm = Util4Serializer.addElement(parentElem, CHILD);
		groupElm.setAttribute(TYPE, GROUP);
		groupElm.setAttribute(ID, groupShape.getId());
		writeShape(groupShape.getShape(), groupElm);
		// 序列化Group内部的元素

		List<WorkflowNodeShape> nodeShapeList = groupShape
				.getWorkflowNodeShapes();
		for (WorkflowNodeShape nodeShape : nodeShapeList) {
			this.writeNodeShape(nodeShape, groupElm);
		}


		List<CommentShape> commentShapeList = groupShape.getComments();
		for (CommentShape commentshape : commentShapeList) {
			this.writeCommentShape(commentshape, groupElm);
		}

	}
    protected void writeNodeShape(WorkflowNodeShape node,Element nodesElement){
    	Element nodeElm = null;
    	
    	if (node instanceof StartNodeShape){
    		nodeElm = Util4Serializer.addElement(nodesElement, CHILD);
    		nodeElm.setAttribute(TYPE, START_NODE);
    	}else if (node instanceof EndNodeShape){
    		nodeElm = Util4Serializer.addElement(nodesElement, CHILD);
    		nodeElm.setAttribute(TYPE, END_NODE);
    	}else if (node instanceof ActivityShape){
    		nodeElm = Util4Serializer.addElement(nodesElement, CHILD);
    		nodeElm.setAttribute(TYPE, ACTIVITY);
    	}else if (node instanceof RouterShape){
    		nodeElm = Util4Serializer.addElement(nodesElement, CHILD);
    		nodeElm.setAttribute(TYPE, ROUTER);
    	}
    	
    	if (nodeElm==null) return;
    	
    	nodeElm.setAttribute(ID, node.getId());
    	nodeElm.setAttribute(REF, node.getWorkflowElementRef());
    	writeShape(node.getShape(),nodeElm);
    	
    	if (node instanceof ActivityShape){
    		//write attached start node;
    		ActivityShape activityShape = (ActivityShape)node;
    		List<StartNodeShape> attachedStartNodeShapeList = activityShape.getAttachedStartNodeShapes();
    		
    		if (attachedStartNodeShapeList!=null && attachedStartNodeShapeList.size()>0){
    			for (StartNodeShape startNodeShape : attachedStartNodeShapeList){
    				this.writeNodeShape(startNodeShape, nodeElm);
    			}
    		}
    	}
    }
    
    protected void writeLaneShape(LaneShape lane,Element lanesElm){
    	Element laneElm = Util4Serializer.addElement(lanesElm, CHILD);
    	laneElm.setAttribute(TYPE, LANE);
    	laneElm.setAttribute(ID, lane.getId());
    	writeShape(lane.getShape(),laneElm);
    	
		List<WorkflowNodeShape> nodeShapeList = lane.getWorkflowNodeShapes();
		for (WorkflowNodeShape nodeShape : nodeShapeList) {
			this.writeNodeShape(nodeShape, laneElm);
		}


		List<CommentShape> commentShapeList = lane.getComments();
		for (CommentShape commentshape : commentShapeList) {
			this.writeCommentShape(commentshape, laneElm);
		}


		List<GroupShape> groupShapeList = lane.getGroups();
		for (GroupShape groupShape:groupShapeList){
			this.writeGroupShape(groupShape, laneElm);
		}
    }

    protected void writeShape(Shape shape,Element parentElement){
    	if (shape==null) return;

    	if (shape instanceof Rectangle){
    		writeRectangle((Rectangle)shape,parentElement);
    	}
    	else if (shape instanceof Circle){
    		writeCircle((Circle)shape,parentElement);
    	}
    	else if (shape instanceof Line){
    		writeLine((Line)shape,parentElement);
    	}
    }
    
    protected void writeLine(Line line , Element parentElement){
    	Element lineElm = Util4Serializer.addElement(parentElement, LINE);
    	if (line.getLabelPosition()!=null){
    		lineElm.setAttribute(LABEL_POSITION, line.getLabelPosition().toString());
    	}
    	
    	lineElm.setAttribute(LABEL_DIRECTION, line.getLabelDirection());
    	
    	this.writeLabel(line.getLabel(), lineElm);
    	
    	LineStyle lineStyle = line.getLineStyle();
    	if (lineStyle!=null){
    		if (!StringUtils.isEmpty(lineStyle.getLineType())){
    			lineElm.setAttribute(LINE_TYPE, lineStyle.getLineType());
    		}
    		
    		if (lineStyle.getThick()>0){
    			lineElm.setAttribute(THICK,
    					Integer.toString(lineStyle.getThick()));
    		}

    		if (lineStyle.getSpace()>0){
    			lineElm.setAttribute(SPACE,
    					Integer.toString(lineStyle.getSpace()));
    		}
    		
    		if (!StringUtils.isEmpty(lineStyle.getColor())){

    			lineElm.setAttribute(COLOR, lineStyle.getColor());
    		}  			
    	}

    	List<Point> pointList = line.getPoints();
    	if (pointList!=null && pointList.size()>0){
    		lineElm.setAttribute(POINT_LIST, pointList2String(pointList));
    	}
    }
    

    private String pointList2String(List<Point> pointList){
    	StringBuffer sbuf = new StringBuffer();
		for (int i=0;i<pointList.size();i++){
			Point p = pointList.get(i);
			sbuf.append(p.toString());
			if (i<pointList.size()-1){
				sbuf.append(";");
			}
		}
		return sbuf.toString();
    }
    
    protected void writeCircle(Circle circle,Element parentElement){
    	Element circleElm = Util4Serializer.addElement(parentElement, CIRCLE);

    	circleElm.setAttribute(RADIUS, Integer.toString(circle.getRadius()));
    	circleElm.setAttribute(THE_CENTER, circle.getTheCenter().toString());
    	
    	this.writeLabel(circle.getLabel(), circleElm);
    	
    	this.writeFulfilStyle(circle.getFulfilStyle(), circleElm);
    	
		LineStyle boundsStyle = circle.getLineStyle();
		if (boundsStyle != null) {
			if (!StringUtils.isEmpty(boundsStyle.getLineType())){
				circleElm.setAttribute(LINE_TYPE, boundsStyle.getLineType());
			}
			
			if (boundsStyle.getThick()>0){
				circleElm.setAttribute(THICK,
						Integer.toString(boundsStyle.getThick()));
			}
			
			if (boundsStyle.getSpace()>0){
				circleElm.setAttribute(SPACE,
						Integer.toString(boundsStyle.getSpace()));
			}
			if (!StringUtils.isEmpty(boundsStyle.getColor())){
				circleElm.setAttribute(COLOR, boundsStyle.getColor());
			}
			
		}
    	
    }
    
    protected void writeRectangle(Rectangle rect,Element parentElement){
    	Element rectElm = Util4Serializer.addElement(parentElement, RECTANGLE);
    	
    	if (rect.getTitleLabel()!=null && !StringUtils.isEmpty(rect.getTitleLabel().getText())){
        	Element titleElm = Util4Serializer.addElement(rectElm, TITLE);
        	this.writeLabel(rect.getTitleLabel(), titleElm);
    	}

    	if (rect.getContentLabel()!=null && !StringUtils.isEmpty(rect.getContentLabel().getText())){
    		Element contentElm = Util4Serializer.addElement(rectElm, CONTENT);
        	this.writeLabel(rect.getContentLabel(), contentElm);
    	}

    	
    	this.writeBounds(rect.getBounds(), rectElm);
    	
    	this.writeFulfilStyle(rect.getFulfilStyle(), rectElm);
    }
    
//    protected void writePlane(Plane plane,Element parentElement){
//    	Element planeElm = Util4Serializer.addElement(parentElement, PLANE);
//    	
//    	writeLabel(plane.getLabel(),planeElm);
//    	
//    	Bounds bounds = plane.getBounds();
//    	writeBounds(bounds,planeElm);
//    	
//    	FulfilStyle fulfilStyle = plane.getFulfilStyle();
//    	writeFulfilStyle(fulfilStyle,planeElm);
//    }
    
    protected void writeLabel(Label lb,Element parentElm){
    	if (lb!=null){
        	Element labelElm = Util4Serializer.addElement(parentElm, LABEL);
      	
        	
        	if (lb.getFont()!=null){
        		labelElm.setAttribute(SIZE,Integer.toString(lb.getFont().getSize()));
        		labelElm.setAttribute(COLOR, lb.getFont().getColor());
        		labelElm.setAttribute(WEIGHT, lb.getFont().getWeight());
        	}
        	
        	Document doc = parentElm.getOwnerDocument();
            CDATASection cdata = doc.createCDATASection(lb.getText());
            labelElm.appendChild(cdata);
    	}
    	
    }
    
	protected void writeBounds(Bounds bounds, Element parentElm) {
		if (bounds == null)
			return;

		Element boundsElm = Util4Serializer.addElement(parentElm, BOUNDS);
		if (bounds.getUpperLeftCorner() != null) {
			boundsElm.setAttribute(UPPER_LEFT_CORNER, bounds
					.getUpperLeftCorner().toString());
		}

		boundsElm.setAttribute(WIDTH, Integer.toString(bounds.getWidth()));
		boundsElm.setAttribute(HEIGHT, Integer.toString(bounds.getHeight()));

		BoundsStyle boundsStyle = bounds.getBoundsStyle();
		if (boundsStyle != null) {
			Element boundsStyleElm = Util4Serializer.addElement(boundsElm,
					BOUNDS_STYLE);

			boundsStyleElm.setAttribute(LINE_TYPE, boundsStyle.getLineType());
			boundsStyleElm.setAttribute(THICK,
					Integer.toString(boundsStyle.getThick()));
			boundsStyleElm.setAttribute(SPACE,
					Integer.toString(boundsStyle.getSpace()));
			boundsStyleElm.setAttribute(COLOR, boundsStyle.getColor());
			boundsStyleElm.setAttribute(CORNER_TYPE,
					boundsStyle.getCornerType());
			boundsStyleElm.setAttribute(RADIUS,
					Integer.toString(boundsStyle.getRadius()));
		}

	}
    
    protected void writeFulfilStyle(FulfilStyle fulfilStyle,Element parentElm){
    	if (fulfilStyle!=null){
    		Element fulfilStyleElm = Util4Serializer.addElement(parentElm, FULFIL_STYLE);
    		fulfilStyleElm.setAttribute(COLOR, fulfilStyle.getColor());
    		fulfilStyleElm.setAttribute(GRADIENT_STYLE, fulfilStyle.getGradientStyle());
    	}
    }
    
    public String serializeToXmlString(WorkflowProcess workflowProcess)
            throws IOException, SerializerException ,InvalidModelException{

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        serialize(workflowProcess, out);
        return out.toString();

    }
    
    protected void writeSubflow(SubProcess subflow ,Element subflowsElement)throws InvalidModelException,SerializerException{
        Element subflowElement = Util4Serializer.addElement(subflowsElement, SUBFLOW);
        subflowElement.setAttribute(ID, subflow.getId());
        subflowElement.setAttribute(NAME, subflow.getName());
        subflowElement.setAttribute(DISPLAY_NAME, subflow.getDisplayName());
        Util4Serializer.addElement(subflowElement, DESCRIPTION,
                subflow.getDescription());
        if (subflow.getEntry()!=null){        	
        	subflowElement.setAttribute(ENTRY, subflow.getEntry().getId());
        }
        
        if (subflow.getDuration()!=null){
        	subflowElement.setAttribute(DURATION, durationToString(subflow.getDuration()));
        }
        //TODO 待处理
        
    	
        writeProperties(subflow.getProperties(), subflowElement);
        
        
        writeStartNodes(subflow.getStartNodes(), subflowElement);


        writeActivities(subflow.getActivities(), subflowElement);
        writeRouters(subflow.getRouters(),
        		subflowElement);
        writeEndNodes(subflow.getEndNodes(), subflowElement);
        writeTransitions(subflow.getTransitions(),
        		subflowElement);


        writeEventListeners(subflow.getEventListeners(), subflowElement);

        writeExtendedAttributes(subflow.getExtendedAttributes(),
        		subflowElement);
    }
    
    protected void writeImports4Service(List<Import<ServiceDef>> imports4Service,Element parentElement){
    	if (imports4Service==null || imports4Service.size()==0){
    		return ;
    	}
    	for (Import<ServiceDef> processImport : imports4Service){
            Element processImportElem = Util4Serializer.addElement(
            		parentElement, IMPORT);

            processImportElem.setAttribute(IMPORT_TYPE, Import.SERVICES_IMPORT);
            processImportElem.setAttribute(LOCATION, processImport.getLocation());

            
    	}
    }
    
    protected void writeImport4Process(List<Import<WorkflowProcess>> imports4Process,Element parentElement){
    	if (imports4Process==null || imports4Process.size()==0){
    		return ;
    	}
    	for (Import<WorkflowProcess> processImport : imports4Process){
            Element processImportElem = Util4Serializer.addElement(
            		parentElement, IMPORT);
            
            processImportElem.setAttribute(IMPORT_TYPE, Import.RESOURCES_IMPORT);
            processImportElem.setAttribute(LOCATION, processImport.getLocation());
    	}
    }
    
    protected void writeImports4Resource(List<Import<ResourceDef>> processImports,Element parentElement){
    	if (processImports==null || processImports.size()==0){
    		return ;
    	}
    	for (Import<ResourceDef> processImport : processImports){
            Element processImportElem = Util4Serializer.addElement(
            		parentElement, IMPORT);
            
            processImportElem.setAttribute(IMPORT_TYPE, Import.RESOURCES_IMPORT);
            processImportElem.setAttribute(LOCATION, processImport.getLocation());
    	}
    }    

    protected void writeEventListeners(List<EventListenerDef> eventListeners, Element parentElement) {
        if (eventListeners == null || eventListeners.size() == 0) {
            return;
        }

        Element eventListenersElm =
                Util4Serializer.addElement(parentElement,
                EVENT_LISTENERS);
        for (int i = 0; i < eventListeners.size(); i++) {
        	EventListenerDef listener = (EventListenerDef) eventListeners.get(i);
            Element eventListenerElm = Util4Serializer.addElement(
                    eventListenersElm, EVENT_LISTENER);
            
            eventListenerElm.setAttribute(ID, listener.getId());
            if (listener.getName()!=null && !listener.getName().trim().equals("")){
            	eventListenerElm.setAttribute(NAME, listener.getName());
            }
            if (listener.getDisplayName()!=null && !listener.getDisplayName().trim().equals("")){
            	eventListenerElm.setAttribute(DISPLAY_NAME, listener.getDisplayName());
            }
            eventListenerElm.setAttribute(BEAN_NAME, listener.getBeanName());
        }
    }

    protected void writeProperties(List<Property> dataFields, Element parent)
            throws SerializerException {

        if (dataFields == null || dataFields.size() == 0) {
            return;
        }

        Element dataFieldsElement = Util4Serializer.addElement(parent,
                PROPERTIES);
        Iterator<Property> iter = dataFields.iterator();
        while (iter.hasNext()) {
            Property dataField = iter.next();
            Element dataFieldElement = Util4Serializer.addElement(
                    dataFieldsElement, PROPERTY);

            dataFieldElement.setAttribute(ID, dataField.getId());
            dataFieldElement.setAttribute(NAME, dataField.getName());
            dataFieldElement.setAttribute(DISPLAY_NAME, dataField.getDisplayName());
            QName dataType = dataField.getDataType();
            if (dataType==null){//取默认值，java.lang.String
            	dataType = new QName(NameSpaces.JAVA.getUri(),"java.lang.String");
            }
            
            dataFieldElement.setAttribute(DATA_TYPE,dataType.toString() );

            dataFieldElement.setAttribute(INIT_VALUE,
                    dataField.getInitialValueAsString());

            if (dataField.getDescription()!=null && !dataField.getDescription().trim().equals("")){
                Util4Serializer.addElement(dataFieldElement, DESCRIPTION, dataField.getDescription());
            }
        }
    }

    protected void writeEndNodes(List<EndNode> endNodes, Element parent) {
        Element endNodesElement = Util4Serializer.addElement(parent, END_NODES);
        Iterator<EndNode> iter = endNodes.iterator();

        while (iter.hasNext()) {
            writeEndNode( iter.next(), endNodesElement);
        }
    }

    protected void writeEndNode(EndNode endNode, Element parent) {
        Element endNodeElement = Util4Serializer.addElement(parent, END_NODE);
        endNodeElement.setAttribute(ID, endNode.getId());
        endNodeElement.setAttribute(NAME, endNode.getName());
        if (endNode.getDisplayName()!=null && !endNode.getDisplayName().trim().equals("")){
        	endNodeElement.setAttribute(DISPLAY_NAME, endNode.getDisplayName());
        }
        if (endNode.getDescription()!=null && !endNode.getDescription().trim().equals("")){
        	Util4Serializer.addElement(endNodeElement, DESCRIPTION, endNode.getDescription());
        }
        
        Feature feature = endNode.getFeature();
        
        if (feature!=null){      	
        	Element featuresElement = Util4Serializer.addElement(endNodeElement, FEATURES);
        	writeEndNodeFeature(feature,featuresElement);
        }

        writeExtendedAttributes(endNode.getExtendedAttributes(), endNodeElement);

    }

    protected void writeStartNodes(List<StartNode> startNodes, Element parent)
            throws SerializerException {
        if (startNodes == null || startNodes.size()==0) {
            return;
        }
        Element startNodesElement = Util4Serializer.addElement(parent, START_NODES);
        
        for (StartNode startNode : startNodes){
            Element startElement = Util4Serializer.addElement(startNodesElement, START_NODE);
            startElement.setAttribute(ID, startNode.getId());
            startElement.setAttribute(NAME, startNode.getName());

            if (startNode.getDisplayName()!=null && !startNode.getDisplayName().trim().equals("")){
            	startElement.setAttribute(DISPLAY_NAME, startNode.getDisplayName());	
            }
            
            if (startNode.getDescription()!=null && !startNode.getDescription().trim().equals("")){
            	Util4Serializer.addElement(startElement, DESCRIPTION, startNode.getDescription());
            }
            
            Feature dec = startNode.getFeature();
            
            if (dec!=null){
            	Element decElement = Util4Serializer.addElement(startElement, FEATURES);
            	writeStartNodeFeature(dec,decElement);           	
            	
            }
            
            writeExtendedAttributes(startNode.getExtendedAttributes(), startElement);
        }
    }
    
    protected void writeEndNodeFeature(Feature feature,Element parent){
    	if (feature instanceof ThrowCompensationFeature){
    		ThrowCompensationFeature compensationDec = (ThrowCompensationFeature)feature;
    		Element catchCompensationElem = Util4Serializer.addElement(parent, THROW_COMPENSATION_FEATURE);
    		List<String> compensationCodes = compensationDec.getCompensationCodes();
    		StringBuffer compensationCodesStr = new StringBuffer("");
    		if (compensationCodes!=null){
    			int i=0;
    			for (String code : compensationCodes){
    				
    				compensationCodesStr.append(code);
    				if (i<compensationCodes.size()-1){
    					compensationCodesStr.append(",");
    				}
    				i++;
    			}
    		}
    		catchCompensationElem.setAttribute(COMPENSATION_CODES, compensationCodesStr.toString());
    	}
    	
    	else if (feature instanceof ThrowFaultFeature){
    		ThrowFaultFeature catchFaultDec = (ThrowFaultFeature)feature;
    		Element catchFaultElem = Util4Serializer.addElement(parent, THROW_FAULT_FEATURE);
    		
        	catchFaultElem.setAttribute(ERROR_CODE, catchFaultDec.getErrorCode());

    	}
    	else if (feature instanceof ThrowTerminationFeature){
    		Util4Serializer.addElement(parent, THROW_TERMINATION_FEATURE);
    	}
    	else if (feature instanceof NormalEndFeature){
    		Util4Serializer.addElement(parent, NORMAL_END_FEATURE);
    	}
    }
    protected void writeStartNodeFeature(Feature dec,Element featuresElement){
    	if (dec instanceof TimerStartFeature){
        	TimerStartFeature timerFeature = (TimerStartFeature)dec;
        	Element timerDecElem = Util4Serializer.addElement(featuresElement, TIMER_START_FEATURE);
        	if (timerFeature.getAttachedToActivity()!=null){
        		timerDecElem.setAttribute(ATTACHED_TO_ACTIVITY, timerFeature.getAttachedToActivity().getId());
        		timerDecElem.setAttribute(IS_CANCEL_ATTACHED_TO_ACTIVITY, Boolean.toString(timerFeature.getCancelAttachedToActivity()));
        	}
        	if (timerFeature.getTimerOperationName()!=null){
        		timerDecElem.setAttribute(TIMER_OPERATION_NAME, timerFeature.getTimerOperationName().getValue());
        	}
        	Expression cronExp = timerFeature.getCronExpression();
        	if(cronExp!=null){
        		Element expElem =  Util4Serializer.addElement(timerDecElem, CRON_EXPRESSION);
                this.writeExpression(cronExp, expElem);
        	}
        	
        	Expression startTimeExp = timerFeature.getStartTimeExpression();
        	if (startTimeExp!=null){
        		Element expElem =  Util4Serializer.addElement(timerDecElem, START_TIME_EXPRESSION);
                this.writeExpression(startTimeExp, expElem);
        	}
        	
        	Expression endTimeExp = timerFeature.getEndTimeExpression();
        	if (endTimeExp!=null){
        		Element expElem =  Util4Serializer.addElement(timerDecElem, END_TIME_EXPRESSION);
                this.writeExpression(endTimeExp, expElem);
        	}
        	
        	Expression intervalExp = timerFeature.getRepeatIntervalExpression();
        	if (intervalExp!=null){
        		Element expElem =  Util4Serializer.addElement(timerDecElem, REPEAT_INTERVAL_EXPRESSION);
                this.writeExpression(intervalExp, expElem);
        	}
        	
        	Expression countExp = timerFeature.getRepeatCountExpression();
        	if (countExp!=null){
        		Element expElem =  Util4Serializer.addElement(timerDecElem, REPEAT_COUNT_EXPRESSION);
                this.writeExpression(countExp, expElem);
        	}
    	}
    	
    	else if (dec instanceof CatchCompensationFeature){
    		CatchCompensationFeature catchCompensationDec = (CatchCompensationFeature)dec;
    		Element catchCompensationElem = Util4Serializer.addElement(featuresElement, CATCH_COMPENSATION_FEATURE);
        	if (catchCompensationDec.getAttachedToActivity()!=null){
        		catchCompensationElem.setAttribute(ATTACHED_TO_ACTIVITY, catchCompensationDec.getAttachedToActivity().getId());
        	}
    		catchCompensationElem.setAttribute(COMPENSATION_CODE, catchCompensationDec.getCompensationCode());

    	}
    	
    	else if (dec instanceof CatchFaultFeature){
    		CatchFaultFeature catchFaultDec = (CatchFaultFeature)dec;
    		Element catchFaultElem = Util4Serializer.addElement(featuresElement, CATCH_FAULT_FEATURE);
    		
        	if (catchFaultDec.getAttachedToActivity()!=null){
        		catchFaultElem.setAttribute(ATTACHED_TO_ACTIVITY, catchFaultDec.getAttachedToActivity().getId());
        	}
        	catchFaultElem.setAttribute(ERROR_CODE, catchFaultDec.getErrorCode());

    	}
    	else if (dec instanceof NormalStartFeature){
    		NormalStartFeature normalDec = (NormalStartFeature)dec;
    		Util4Serializer.addElement(featuresElement, NORMAL_START_FEATURE);
    	}else if (dec instanceof WebserviceStartFeature){
    		WebserviceStartFeature wsStartFeature = (WebserviceStartFeature)dec;
    		Element wsStartFeatureElement = Util4Serializer.addElement(featuresElement, WEBSERVICE_START_FEATURE);
    		this.writeServiceBinding(wsStartFeature.getServiceBinding(), wsStartFeatureElement);
    	}
    	//TODO MessageStartDecorator需完善~~~
    }

    protected void writeRouters(List<Router> synchronizers, Element parent)
            throws SerializerException {
        if (synchronizers == null || synchronizers.size() == 0) {
            return;
        }
        Element synchronizersElement = Util4Serializer.addElement(parent,
                ROUTERS);

        Iterator<Router> iter = synchronizers.iterator();

        while (iter.hasNext()) {
            writeRouter( iter.next(), synchronizersElement);
        }
    }

    protected void writeRouter(Router router, Element parent)
            throws SerializerException {
        Element routerElement = Util4Serializer.addElement(parent,
                ROUTER);
        routerElement.setAttribute(ID, router.getId());
        routerElement.setAttribute(NAME, router.getName());
        
        if (router.getDisplayName()!=null && !router.getDisplayName().trim().equals("")){
            routerElement.setAttribute(DISPLAY_NAME, router.getDisplayName());
        }
        if (router.getDescription()!=null && !router.getDescription().equals("")){
            Util4Serializer.addElement(routerElement, DESCRIPTION,
            		router.getDescription());
        }
        Feature feature = router.getFeature();
        if (feature!=null){
        	Element decElement = Util4Serializer.addElement(routerElement, FEATURES);
        	if (feature instanceof AndJoinAndSplitRouterFeature){
        		Util4Serializer.addElement(decElement, ANDJOIN_ANDSPLIT_FEATURE);
        	}else if (feature instanceof OrJoinOrSplitRouterFeature){
        		Util4Serializer.addElement(decElement, ORJOIN_ORSPLIT_FEATURE);
        	}else if (feature instanceof DynamicRouterFeature){
        		Util4Serializer.addElement(decElement, DYNAMIC_JOIN_SPLIT_FEATURE);
        	}
        	else if (feature instanceof CustomizedRouterFeature){
        		Element fElm = Util4Serializer.addElement(decElement, CUSTOMIZED_JOIN_SPLIT_FEATURE);
        		CustomizedRouterFeature customizedFeature = (CustomizedRouterFeature)feature;
        		fElm.setAttribute(JOIN_EVALUATOR, customizedFeature.getJoinEvaluatorClass());
        		fElm.setAttribute(SPLIT_EVALUATOR, customizedFeature.getSplitEvalutorClass());
        	}
        }
        writeExtendedAttributes(router.getExtendedAttributes(),
                routerElement);
    }

    protected void writeActivities(List<Activity> activities, Element parent)
            throws SerializerException {

        if (activities == null || activities.size() == 0) {
            return;
        }

        Element activitiesElement = Util4Serializer.addElement(parent,
                ACTIVITIES);

        Iterator<Activity> iter = activities.iterator();
        while (iter.hasNext()) {
            writeActivity(  iter.next(), activitiesElement);
        }
    }

    protected void writeActivity(Activity activity, Element parent)
            throws SerializerException {

        Element activityElement = Util4Serializer.addElement(parent, ACTIVITY);

        activityElement.setAttribute(ID, activity.getId());
        activityElement.setAttribute(NAME, activity.getName());
        if (activity.getDisplayName()!=null && !activity.getDisplayName().trim().equals("")){
        	activityElement.setAttribute(DISPLAY_NAME, activity.getDisplayName());
        }
        
        if (activity.getPriority()!=null && !activity.getPriority().trim().equals("")){
        	activityElement.setAttribute(PRIORITY, activity.getPriority());
        }        
        if (activity.getDuration()!=null){
        	activityElement.setAttribute(DURATION, durationToString(activity.getDuration()));
        }
        if (activity.getLoopStrategy()!=null){
        	activityElement.setAttribute(LOOP_STRATEGY, activity.getLoopStrategy().getValue());
        }        
        if (activity.getDescription()!=null){
        	Util4Serializer.addElement(activityElement, DESCRIPTION, activity.getDescription());
        }
        
        Feature dec = activity.getFeature();
        if (dec!=null){
        	//TODO Activity是否需要decorator?
        }
        
        this.writeProperties(activity.getProperties(), activityElement);
        
        
        this.writeServiceBinding(activity.getServiceBinding(),activityElement);
        this.writeResourceBinding(activity.getResourceBinding(),activityElement);
        
        writeEventListeners(activity.getEventListeners(), activityElement);
        writeExtendedAttributes(activity.getExtendedAttributes(),
                activityElement);
    }
    
    protected void writeResourceBinding(ResourceBinding resourceBinding,Element parent){
    	if (resourceBinding==null)return;
    	Element resourceBindingElem = Util4Serializer.addElement(parent, RESOURCE_BINDING);
    	resourceBindingElem.setAttribute(DISPLAY_NAME, resourceBinding.getDisplayName());
    	resourceBindingElem.setAttribute(ASSIGNMENT_STRATEGY, resourceBinding.getAssignmentStrategy().getValue());
    	
    	List<ResourceDef> administrators = resourceBinding.getAdministrators();
    	if (administrators!=null && administrators.size()>0){
    		Element administratorsElem = Util4Serializer.addElement(resourceBindingElem, ADMINISTRATORS);
    		for (ResourceDef resourceRef : administrators){
    			Element resourceRefElem = Util4Serializer.addElement(administratorsElem, RESOURCE_REF);
    			resourceRefElem.setAttribute(RESOURCE_ID, resourceRef.getId());
    			
//    			List<ParameterAssignment> paramAssignments = resourceRef.getParameterAssignments();
//    			if (paramAssignments!=null && paramAssignments.size()>0){
//    				Element paramAssignmentsElem = Util4Serializer.addElement(resourceRefElem, PARAMETER_ASSIGNMENTS);
//    				for (ParameterAssignment paramAssignment : paramAssignments){
//    					Element paramAssignmentElm = Util4Serializer.addElement(paramAssignmentsElem, PARAMETER_ASSIGNMENT);
//    	    			Element fromElement = Util4Serializer.addElement(paramAssignmentElm, FROM);
//    	    			Expression fromExp = paramAssignment.getFrom();
//    	    			this.writeExpression(fromExp, fromElement);
//    	                
//    	                Util4Serializer.addElement(paramAssignmentElm, TO,paramAssignment.getTo());
//
//    				}
//    			}
    		}
    	}
    	
    	//潜在操作者
    	List<ResourceDef> potentialOwners = resourceBinding.getPotentialOwners();
    	if (potentialOwners!=null && potentialOwners.size()>0){
    		Element potentialOwnersElem = Util4Serializer.addElement(resourceBindingElem, POTENTIAL_OWNERS);
    		for (ResourceDef resourceRef : potentialOwners){
    			Element resourceRefElem = Util4Serializer.addElement(potentialOwnersElem, RESOURCE_REF);
    			resourceRefElem.setAttribute(RESOURCE_ID, resourceRef.getId());
    			
//    			List<ParameterAssignment> paramAssignments = resourceRef.getParameterAssignments();
//    			if (paramAssignments!=null && paramAssignments.size()>0){
//    				Element paramAssignmentsElem = Util4Serializer.addElement(resourceRefElem, PARAMETER_ASSIGNMENTS);
//    				for (ParameterAssignment paramAssignment : paramAssignments){
//    					Element paramAssignmentElm = Util4Serializer.addElement(paramAssignmentsElem, PARAMETER_ASSIGNMENT);
//    	    			Element fromElement = Util4Serializer.addElement(paramAssignmentElm, FROM);
//    	    			Expression fromExp = paramAssignment.getFrom();
//    	    			this.writeExpression(fromExp, fromElement);
//    	                
//    	                Util4Serializer.addElement(paramAssignmentElm, TO,paramAssignment.getTo());
//
//    				}
//    			}
    		}
    	}
    	
    	List<ResourceDef> readers = resourceBinding.getReaders();
    	if (readers!=null && readers.size()>0){
    		Element potentialOwnersElem = Util4Serializer.addElement(resourceBindingElem, READERS);
    		for (ResourceDef resourceRef : readers){
    			Element resourceRefElem = Util4Serializer.addElement(potentialOwnersElem, RESOURCE_REF);
    			resourceRefElem.setAttribute(RESOURCE_ID, resourceRef.getId());
    			
//    			List<ParameterAssignment> paramAssignments = resourceRef.getParameterAssignments();
//    			if (paramAssignments!=null && paramAssignments.size()>0){
//    				Element paramAssignmentsElem = Util4Serializer.addElement(resourceRefElem, PARAMETER_ASSIGNMENTS);
//    				for (ParameterAssignment paramAssignment : paramAssignments){
//    					Element paramAssignmentElm = Util4Serializer.addElement(paramAssignmentsElem, PARAMETER_ASSIGNMENT);
//    	    			Element fromElement = Util4Serializer.addElement(paramAssignmentElm, FROM);
//    	    			Expression fromExp = paramAssignment.getFrom();
//    	    			this.writeExpression(fromExp, fromElement);
//    	                
//    	                Util4Serializer.addElement(paramAssignmentElm, TO,paramAssignment.getTo());
//
//    				}
//    			}
    		}
    	}    	
    }
    
    protected void writeServiceBinding(ServiceBinding serviceBinding,Element parent){
    	if (serviceBinding==null)return;
    	Element serviceBindingElem = Util4Serializer.addElement(parent, SERVICE_BINDING);
    	
    	serviceBindingElem.setAttribute(SERVICE_ID, serviceBinding.getServiceId());
    	serviceBindingElem.setAttribute(OPERATION_NAME, serviceBinding.getOperationName());
    	
    	List<Assignment> inputAssignments = serviceBinding.getInputAssignments();
    	if (inputAssignments!=null && inputAssignments.size()>0){
    		Element inputAssignmentsElem = Util4Serializer.addElement(serviceBindingElem, INPUT_ASSIGNMENTS);
    		
    		for (Assignment inputAssignment : inputAssignments){
    			Element inputAssignmentElem = Util4Serializer.addElement(inputAssignmentsElem, INPUT_ASSIGNMENT);
    			Element fromElement = Util4Serializer.addElement(inputAssignmentElem, FROM);
    			Expression fromExp = inputAssignment.getFrom();
    			writeExpression(fromExp, fromElement);
                
                Element toElement = Util4Serializer.addElement(inputAssignmentElem, TO);
                Expression toExp = inputAssignment.getTo();
                writeExpression(toExp, toElement);
    		}
    	}
    	
    	List<Assignment> outputAssignments = serviceBinding.getOutputAssignments();
    	if (outputAssignments!=null && outputAssignments.size()>0){
    		Element outputAssignmentsElem = Util4Serializer.addElement(serviceBindingElem, OUTPUT_ASSIGNMENTS);
    		
    		for (Assignment outputAssignment : outputAssignments){
    			Element outputAssignmentElem = Util4Serializer.addElement(outputAssignmentsElem, OUTPUT_ASSIGNMENT);
    			Element fromElement = Util4Serializer.addElement(outputAssignmentElem, FROM);
    			Expression fromExp = outputAssignment.getFrom();
    			this.writeExpression(fromExp, fromElement);
                
                Element toElement = Util4Serializer.addElement(outputAssignmentElem, TO);
                Expression toExp = outputAssignment.getTo();
                writeExpression(toExp, toElement);
    		}
    	}
    	
//    	List<PropOverride> propOverrides = serviceBinding.getPropOverrides();
//    	if (propOverrides!=null && propOverrides.size()>0){
//    		Element propOverridesElem = Util4Serializer.addElement(serviceBindingElem, PROP_OVERRIDES);
//    		
//    		for (PropOverride propOverride : propOverrides){
//    			Element propOverrideElem = Util4Serializer.addElement(propOverridesElem, PROP_OVERRIDE);
//    			propOverrideElem.setAttribute(PROP_GROUP_NAME, propOverride.getPropGroupName());
//    			propOverrideElem.setAttribute(PROP_NAME, propOverride.getPropName());
//    			propOverrideElem.setAttribute(VALUE, propOverride.getValue());
//    		}
//    	}
    }






    

    


//    protected void writeDuration(Duration duration, Element parent) {
//        if (duration == null) {
//            return;
//        }
//        Element durationElement = Util4Serializer.addElement(parent, DURATION);
//        durationElement.setAttribute(VALUE, Integer.toString(duration.getValue()));
//        durationElement.setAttribute(UNIT, duration.getUnit());
//        durationElement.setAttribute(IS_BUSINESS_TIME, Boolean.toString(duration.isBusinessTime()));
//    }
    
    protected String durationToString(Duration duration){
    	if (duration==null) return null;
    	if (duration.isBusinessTime()){
    		return duration.getValue()+" BUSINESS "+duration.getUnit();
    	}else{
    		return duration.getValue()+" "+duration.getUnit();
    	}
    }



    protected void writeTransitions(List<Transition> transitions, Element parent)
            throws SerializerException {

        if (transitions == null || transitions.size() == 0) {
            return;
        }

        Element transitionsElement = Util4Serializer.addElement(parent,
                TRANSITIONS);

        Iterator<Transition>  iter = transitions.iterator();
        while (iter.hasNext()) {
            writeTransition( iter.next(), transitionsElement);
        }
    }

    protected void writeTransition(Transition transition, Element parent)
            throws SerializerException {

        Element transitionElement = Util4Serializer.addElement(parent,
                TRANSITION);

        transitionElement.setAttribute(ID, transition.getId());
        transitionElement.setAttribute(FROM, transition.getFromNode().getId());
        transitionElement.setAttribute(TO, transition.getToNode().getId());
        transitionElement.setAttribute(NAME, transition.getName());
        if (transition.getDisplayName()!=null){
        	transitionElement.setAttribute(DISPLAY_NAME, transition.getDisplayName());
        }        
        
        transitionElement.setAttribute(IS_LOOP, Boolean.toString(transition.isLoop()));
        transitionElement.setAttribute(IS_DEFAULT, Boolean.toString(transition.isDefault()));

        if (transition.getDescription()!=null){
        	Util4Serializer.addElement(transitionElement, DESCRIPTION,transition.getDescription());
        }
        
        
        
        Expression condition = transition.getCondition();
        if(condition!=null){
            Element conditionElement = Util4Serializer.addElement(transitionElement, CONDITION);
            writeExpression(condition,conditionElement);
        }


        writeExtendedAttributes(transition.getExtendedAttributes(),
                transitionElement);
    }
    
    protected void writeExpression(Expression exp,Element parent){
    	if (exp==null)return;
    	Element expressionElem = Util4Serializer.addElement(parent, EXPRESSION);
        if (exp.getName()!=null && !exp.getName().trim().equals("")){
        	expressionElem.setAttribute(NAME, exp.getName());
        }
        if (exp.getDisplayName()!=null && !exp.getDisplayName().trim().equals("")){
        	expressionElem.setAttribute(DISPLAY_NAME, exp.getDisplayName());
        }
        if (exp.getDataType()!=null){
        	expressionElem.setAttribute(DATA_TYPE, exp.getDataType().toString());
        }
        expressionElem.setAttribute(LANGUAGE, exp.getLanguage());
        Document doc = parent.getOwnerDocument();
        
        Element bodyElem = Util4Serializer.addElement(expressionElem, BODY);
        if (exp.getBody()!=null){
            CDATASection cdata = doc.createCDATASection(exp.getBody());
            bodyElem.appendChild(cdata);
        }

        
        if(exp.getNamespaceMap()!=null && exp.getNamespaceMap().size()>0){
        	
        	Element namespaceMapElem = Util4Serializer.addElement(expressionElem, NAMESPACE_PREFIX_URI_MAP);
        	Iterator<Map.Entry<String,String>> entrys = exp.getNamespaceMap().entrySet().iterator();
        	while(entrys.hasNext()){
        		Map.Entry<String,String> entry = entrys.next();
        		Element entryElem = Util4Serializer.addElement(namespaceMapElem, ENTRY);
        		entryElem.setAttribute(NAME, entry.getKey());
        		entryElem.setAttribute(VALUE, entry.getValue());
        	}
        }
    }

    protected Element writeExtendedAttributes(Map<String,String> extendedAttributes,
            Element parent) {

        if (extendedAttributes == null || extendedAttributes.size() == 0) {
            return null;
        }

        Element extendedAttributesElement =
                Util4Serializer.addElement(parent,
                EXTENDED_ATTRIBUTES);
//                        parent
//				.addElement(EXTENDED_ATTRIBUTES);

        Iterator<String> keys = extendedAttributes.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = extendedAttributes.get(key);

            Element extendedAttributeElement = Util4Serializer.addElement(
                    extendedAttributesElement, EXTENDED_ATTRIBUTE);
            extendedAttributeElement.setAttribute(NAME, key.toString());
            if (value != null) {
                extendedAttributeElement.setAttribute(VALUE, value.toString());
            }

        }

        return extendedAttributesElement;

    }




}
