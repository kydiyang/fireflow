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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.invocation.TimerOperationName;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.binding.impl.AssignmentImpl;
import org.fireflow.model.binding.impl.ResourceBindingImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.Expression;
import org.fireflow.model.data.Property;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.PropertyImpl;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.Util4Deserializer;
import org.fireflow.model.io.resource.ResourceDeserializer;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.pdl.fpdl20.diagram.ActivityShape;
import org.fireflow.pdl.fpdl20.diagram.AssociationShape;
import org.fireflow.pdl.fpdl20.diagram.CommentShape;
import org.fireflow.pdl.fpdl20.diagram.Diagram;
import org.fireflow.pdl.fpdl20.diagram.DiagramElement;
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
import org.fireflow.pdl.fpdl20.diagram.basic.impl.BoundsImpl;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.CircleImpl;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.LabelImpl;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.LineImpl;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.PointImpl;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.RectangleImpl;
import org.fireflow.pdl.fpdl20.diagram.impl.ActivityShapeImpl;
import org.fireflow.pdl.fpdl20.diagram.impl.AssociationShapeImpl;
import org.fireflow.pdl.fpdl20.diagram.impl.CommentShapeImpl;
import org.fireflow.pdl.fpdl20.diagram.impl.DiagramImpl;
import org.fireflow.pdl.fpdl20.diagram.impl.EndNodeShapeImpl;
import org.fireflow.pdl.fpdl20.diagram.impl.GroupShapeImpl;
import org.fireflow.pdl.fpdl20.diagram.impl.LaneShapeImpl;
import org.fireflow.pdl.fpdl20.diagram.impl.MessageFlowShapeImpl;
import org.fireflow.pdl.fpdl20.diagram.impl.PoolShapeImpl;
import org.fireflow.pdl.fpdl20.diagram.impl.RouterShapeImpl;
import org.fireflow.pdl.fpdl20.diagram.impl.StartNodeShapeImpl;
import org.fireflow.pdl.fpdl20.diagram.impl.TransitionShapeImpl;
import org.fireflow.pdl.fpdl20.diagram.style.BoundsStyle;
import org.fireflow.pdl.fpdl20.diagram.style.FulfilStyle;
import org.fireflow.pdl.fpdl20.diagram.style.LineStyle;
import org.fireflow.pdl.fpdl20.diagram.style.impl.BoundsStyleImpl;
import org.fireflow.pdl.fpdl20.diagram.style.impl.FulfilStyleImpl;
import org.fireflow.pdl.fpdl20.diagram.style.impl.LineStyleImpl;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.misc.LoopStrategy;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.EndNode;
import org.fireflow.pdl.fpdl20.process.Import;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.Router;
import org.fireflow.pdl.fpdl20.process.StartNode;
import org.fireflow.pdl.fpdl20.process.Subflow;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.event.EventListenerDef;
import org.fireflow.pdl.fpdl20.process.event.impl.EventListenerDefImpl;
import org.fireflow.pdl.fpdl20.process.features.Feature;
import org.fireflow.pdl.fpdl20.process.features.endnode.impl.ThrowCompensationFeatureImpl;
import org.fireflow.pdl.fpdl20.process.features.endnode.impl.ThrowFaultFeatureImpl;
import org.fireflow.pdl.fpdl20.process.features.endnode.impl.ThrowTerminationFeatureImpl;
import org.fireflow.pdl.fpdl20.process.features.router.impl.AndJoinAndSplitRouterFeature;
import org.fireflow.pdl.fpdl20.process.features.router.impl.CustomizedRouterFeature;
import org.fireflow.pdl.fpdl20.process.features.router.impl.DynamicRouterFeature;
import org.fireflow.pdl.fpdl20.process.features.router.impl.OrJoinOrSplitRouterFeature;
import org.fireflow.pdl.fpdl20.process.features.startnode.CatchCompensationFeature;
import org.fireflow.pdl.fpdl20.process.features.startnode.CatchFaultFeature;
import org.fireflow.pdl.fpdl20.process.features.startnode.TimerStartFeature;
import org.fireflow.pdl.fpdl20.process.features.startnode.impl.CatchCompensationFeatureImpl;
import org.fireflow.pdl.fpdl20.process.features.startnode.impl.CatchFaultFeatureImpl;
import org.fireflow.pdl.fpdl20.process.features.startnode.impl.TimerStartFeatureImpl;
import org.fireflow.pdl.fpdl20.process.features.startnode.impl.WebserviceStartFeatureImpl;
import org.fireflow.pdl.fpdl20.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl20.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.ImportImpl;
import org.fireflow.pdl.fpdl20.process.impl.RouterImpl;
import org.fireflow.pdl.fpdl20.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.SubflowImpl;
import org.fireflow.pdl.fpdl20.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl20.process.impl.WorkflowProcessImpl;
import org.firesoa.common.schema.NameSpaces;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



/**
 * @author 非也 nychen2000@163.com
 */
public class FPDLDeserializer implements FPDLNames{
	private static final Log log = LogFactory.getLog(FPDLDeserializer.class);
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	static {
		docBuilderFactory.setNamespaceAware(true);
	}
    private static ImportLoader defaultImportLoader = new ImportLoaderClasspathImpl();
    
    protected ImportLoader importLoader = defaultImportLoader;
    
	/**
	 * @return the importLoader
	 */
	public ImportLoader getImportLoader() {
		return importLoader;
	}

	/**
	 * @param importLoader the importLoader to set
	 */
	public void setImportLoader(ImportLoader importLoader) {
		this.importLoader = importLoader;
	}

	public WorkflowProcess deserialize(InputStream in) throws IOException,
			DeserializerException ,InvalidModelException{
		try {
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			
			Document document = docBuilder.parse(in);

			WorkflowProcess wp = deserialize(document);// 解析
			return wp;
		} catch (ParserConfigurationException e) {
			throw new DeserializerException("Error parsing document.", e);
		} catch (SAXException e) {
			throw new DeserializerException("Error parsing document.", e);
		} finally {
		}
		// return parse(new InputStreamReader(in));
	}
	
	public WorkflowProcess parse(InputStream in,RuntimeContext ctx){
		return null;
	}

	@SuppressWarnings("static-access")
	public WorkflowProcess deserialize(Document document)
			throws DeserializerException,InvalidModelException,IOException {
		Element workflowProcessElement = document.getDocumentElement();
		String processName = workflowProcessElement.getAttribute(NAME);
		String processDisplayName = workflowProcessElement.getAttribute(DISPLAY_NAME);
		
		// 流程ID
		WorkflowProcessImpl wp = new WorkflowProcessImpl(processName,processDisplayName);
		
		// 流程整体描述
		wp.setDescription(Util4Deserializer.elementAsString(workflowProcessElement,
				DESCRIPTION));

		// 流程业务类别
		wp.setBizCategory(workflowProcessElement.getAttribute(BIZ_CATEGORY));

		//解析Import
		this.loadProcessImport(wp,Util4Deserializer.children(workflowProcessElement, IMPORT));
		
		
		//解析Service
        QName qname = new QName(SERVICE_NS_URI,	SERVICES, SERVICE_NS_PREFIX);
        List<ServiceDef> serviceList = new ArrayList<ServiceDef>();
        ServiceParser.loadServices(serviceList,Util4Deserializer.child(workflowProcessElement, qname));
		if (serviceList!=null){
			for (ServiceDef serviceDef : serviceList){
				wp.addService(serviceDef);
			}
		}
        
		//解析Resource
        List<ResourceDef> resourcesList = new ArrayList<ResourceDef>();
        qname = new QName(RESOURCE_NS_URI,	RESOURCES, RESOURCE_NS_PREFIX);
        ResourceDeserializer.loadResources(resourcesList,Util4Deserializer.child(workflowProcessElement, qname));
        if (resourcesList!=null){
        	for (ResourceDef resourceDef : resourcesList){
        		wp.addResource(resourceDef);
        	}
        }
        
        //解析subflow
        Element subflowsElem = Util4Deserializer.child(workflowProcessElement, SUBFLOWS);
        List<Element> subflowElementList = Util4Deserializer.children(subflowsElem, SUBFLOW);

        if (subflowElementList!=null){
        	for (Element subflowElm : subflowElementList){
        		this.loadSubflow(wp, subflowElm);
        	}
        }
        
        //解析diagrams
        Element diagramsElm = Util4Deserializer.child(workflowProcessElement, DIAGRAMS);
        List<Element> diagramsElementList = Util4Deserializer.children(diagramsElm, DIAGRAM);
        
        if (diagramsElementList!=null){
        	for (Element diagramElm : diagramsElementList){
        		this.loadDiagram(wp,diagramElm);
        	}
        }
        
		return wp;

	}
	
	protected void loadDiagram(WorkflowProcess wp,Element diagramElm)throws DeserializerException{
		if (diagramElm==null) return;
		
		String id = diagramElm.getAttribute(ID);
		String subflowId = diagramElm.getAttribute(REF);
		String direction = diagramElm.getAttribute(DIRECTION);
		
		Diagram diagram = new DiagramImpl(id,subflowId);
		if (!StringUtils.isEmpty(direction)){
			diagram.setDirection(direction);
		}
		wp.addDiagram(diagram);
		
		
		//1、首先load所有的节点
		List<Element> childElmList = Util4Deserializer.children(diagramElm,CHILD);
		if (childElmList!=null){
			for (Element childElm : childElmList){
				String type = childElm.getAttribute(TYPE);
				if (type==null) type="";

				if (ACTIVITY.equals(type)){
					ActivityShape activityShape =loadActivityShape(childElm);
					diagram.addWorkflowNodeShape(activityShape);
				}else if (START_NODE.equals(type)){
					StartNodeShape startNodeShape = loadStartNodeShape(childElm);
					diagram.addWorkflowNodeShape(startNodeShape);
				}else if (END_NODE.equals(type)){
					EndNodeShape endNodeShape = loadEndNodeShape(childElm);
					diagram.addWorkflowNodeShape(endNodeShape);
				}else if (ROUTER.equals(type)){
					RouterShape routerShape = loadRouterShape(childElm);
					diagram.addWorkflowNodeShape(routerShape);
				}
				else if (GROUP.equals(type)){
					GroupShape groupShape = loadGroupShapeWithoutConnector(childElm);
					diagram.addGroup(groupShape);
				}
				else if (type.equals(POOL)){
					PoolShape poolShape = loadPoolShapeWithoutConnector(diagram,childElm);
					diagram.addPool(poolShape);
				}
				else if (type.equals(COMMENT)){
					CommentShape commentShape = loadCommentShape(childElm);
					diagram.addComment(commentShape);
				}
			}
		}
		
		//2、然后load所有的线条
		/*
		if (childElmList!=null){
			for (Element childElm : childElmList){
				String type = childElm.getAttribute(TYPE);
				String elmId = childElm.getAttribute(ID);
				if (GROUP.equals(type)){
					GroupShape groupShape = (GroupShape)diagram.findChild(elmId);
					this.loadConnectorsInGroupShape(diagram, childElm, groupShape);
				}
				else if (type.equals(POOL)){
					PoolShape poolShape = (PoolShape)diagram.findChild(elmId);
					loadConnectorsInPoolShape(diagram,childElm,poolShape);
				}
			}
		}
		*/
		
		childElmList = Util4Deserializer.children(diagramElm, CONNECTOR);
		if (childElmList!=null){
			for (Element childElm : childElmList){
				String type = childElm.getAttribute(TYPE);
				if (type==null) type="";
				if (TRANSITION.equals(type)){
					TransitionShape transitionShape = loadTransitionShape(diagram,childElm);
					diagram.addTransition(transitionShape);
				}
				else if (type.equals(MESSAGEFLOW)){
					loadMessageFlowShape(diagram,childElm);
				}
				else if (type.equals(ASSOCIATION)){
					AssociationShape association = loadAssociationShape(diagram,childElm);
					diagram.addAssociation(association);
				}
			}
		}
	}
	
	protected GroupShape loadGroupShapeWithoutConnector(Element groupElement){
		String id = groupElement.getAttribute(ID);
		GroupShape groupShape = new GroupShapeImpl(id);
		
		Element rectElm = Util4Deserializer.child(groupElement, RECTANGLE);
		this.loadRectangle(groupShape, rectElm);
		
		List<Element> childElmList = Util4Deserializer.children(groupElement,CHILD);
		if (childElmList!=null){
			for (Element childElm : childElmList){
				String type = childElm.getAttribute(TYPE);
				if (type==null) type="";
				if (ACTIVITY.equals(type)){
					ActivityShape activityShape =loadActivityShape(childElm);
					groupShape.addWorkflowNodeShape(activityShape);
				}else if (START_NODE.equals(type)){
					StartNodeShape startNodeShape = loadStartNodeShape(childElm);
					groupShape.addWorkflowNodeShape(startNodeShape);
				}else if (END_NODE.equals(type)){
					EndNodeShape endNodeShape = loadEndNodeShape(childElm);
					groupShape.addWorkflowNodeShape(endNodeShape);
				}else if (ROUTER.equals(type)){
					RouterShape routerShape = loadRouterShape(childElm);
					groupShape.addWorkflowNodeShape(routerShape);
				}
				else if (COMMENT.equals(type)){
					CommentShape commentShape = this.loadCommentShape( childElm);
					groupShape.addComment(commentShape);
				}

			}
		}
		

		return groupShape;
	}
	
	/* 所有的链接放在diagram中
	protected void loadConnectorsInGroupShape(Diagram diagram ,Element groupElement,GroupShape groupShape){
		List<Element> childElmList = Util4Deserializer.children(groupElement, CONNECTOR);
		if (childElmList!=null){
			for (Element childElm : childElmList){
				String type = childElm.getAttribute(TYPE);
				if (type==null) type="";
				if (TRANSITION.equals(type)){
					TransitionShape transitionShape = loadTransitionShape(diagram,childElm);
					groupShape.addTransition(transitionShape);	
				}
				else if (ASSOCIATION.equals(type)){
					AssociationShape associationShape = this.loadAssociationShape(diagram, childElm);
					groupShape.addAssociation(associationShape);
				}
			}
		}
	}
	*/
	protected PoolShape loadPoolShapeWithoutConnector(Diagram diagram,Element poolElm){
		if (poolElm==null){
			return null;
		}
		
		String id = poolElm.getAttribute(ID);		
		String wfElementRef = poolElm.getAttribute(REF);
		
		PoolShape pool = new PoolShapeImpl(id);
		if (!StringUtils.isEmpty(wfElementRef)){
			pool.setWorkflowElementRef(wfElementRef);
		}
		
		Element planeElm = Util4Deserializer.child(poolElm, RECTANGLE);
		this.loadRectangle(pool, planeElm);
		
		//下级节点
		List<Element> children = Util4Deserializer.children(poolElm, CHILD);
		if (children!=null){
			for (Element child : children){
				String type = child.getAttribute(TYPE);
				if (type==null) type="";
				
				if (LANE.equals(type)){
					LaneShape lane = loadLaneShape(child);
					pool.addLane(lane);
				}
			}
		}

		return pool;
	}
	
	/* 所有的链接放在diagram层里面
	protected void loadConnectorsInPoolShape(Diagram diagram ,Element poolElement,PoolShape poolShape){
		List<Element> childElmList = Util4Deserializer.children(poolElement, CONNECTOR);
		if (childElmList!=null){
			for (Element childElm : childElmList){
				String type = childElm.getAttribute(TYPE);
				if (type==null) type="";
				if (TRANSITION.equals(type)){
					TransitionShape transitionShape = loadTransitionShape(diagram,childElm);
					poolShape.addTransition(transitionShape);	
				}
				else if (ASSOCIATION.equals(type)){
					AssociationShape associationShape = this.loadAssociationShape(diagram, childElm);
					poolShape.addAssociation(associationShape);
				}
			}
		}
		List<Element> children = Util4Deserializer.children(poolElement, CHILD);
		if (children!=null){
			for (Element child : children){
				String type = child.getAttribute(TYPE);
				String elmId = child.getAttribute(ID);
				if (GROUP.equals(type)){
					GroupShape groupShape = (GroupShape)diagram.findChild(elmId);
					this.loadConnectorsInGroupShape(diagram, child, groupShape);
				}
			}
		}
	}
	*/
	
	protected TransitionShape loadTransitionShape(Diagram diagram,Element transitionElm){
		if (transitionElm==null)return null;
		
		String id = transitionElm.getAttribute(ID);
		String ref = transitionElm.getAttribute(REF);
		String from = transitionElm.getAttribute(FROM);
		String to = transitionElm.getAttribute(TO);
		
		DiagramElement fromElm = diagram.findChild(from);
		DiagramElement toElm = diagram.findChild(to);
		
		
		TransitionShape transitionShape = new TransitionShapeImpl(id);
		transitionShape.setWorkflowElementRef(ref);
		transitionShape.setFromWorkflowNodeShape((WorkflowNodeShape)fromElm);
		transitionShape.setToWorkflowNodeShape((WorkflowNodeShape)toElm);
		
		
		Element lineElm = Util4Deserializer.child(transitionElm, LINE);
		this.loadLine(transitionShape, lineElm);
		
		return transitionShape;
	}
	
	protected RouterShape loadRouterShape(Element routerShapeElm){
		if (routerShapeElm==null) return null;
		
		String id = routerShapeElm.getAttribute(ID);
		String ref = routerShapeElm.getAttribute(REF);
		
		RouterShape routerShape = new RouterShapeImpl(id);
		routerShape.setWorkflowElementRef(ref);
		
		Element rectElm = Util4Deserializer.child(routerShapeElm, RECTANGLE);
		this.loadRectangle(routerShape, rectElm);
		
		return routerShape;
	}
	
	protected EndNodeShape loadEndNodeShape(Element endNodeShapeElm){
		if (endNodeShapeElm==null) return null;
		String id = endNodeShapeElm.getAttribute(ID);
		String ref = endNodeShapeElm.getAttribute(REF);
		
		EndNodeShape endNodeShape = new EndNodeShapeImpl(id);
		endNodeShape.setWorkflowElementRef(ref);
		
		Element circleElm = Util4Deserializer.child(endNodeShapeElm,CIRCLE);
		loadCircle(endNodeShape,circleElm);
		
		return endNodeShape;
	}
	
	protected StartNodeShape loadStartNodeShape(Element startNodeShapeElm){
		if (startNodeShapeElm==null) return null;
		
		String id = startNodeShapeElm.getAttribute(ID);
		String ref = startNodeShapeElm.getAttribute(REF);
		
		StartNodeShape startNodeShape = new StartNodeShapeImpl(id);
		startNodeShape.setWorkflowElementRef(ref);
		
		Element circleElm = Util4Deserializer.child(startNodeShapeElm,CIRCLE);
		loadCircle(startNodeShape,circleElm);
		
		return startNodeShape;
	}
	
	protected void loadCircle(DiagramElement diagramElm,Element circleElm){
		if (circleElm==null) return;
		Circle circle = new CircleImpl();
		diagramElm.setShape(circle);
		
		String theCenter = circleElm.getAttribute(THE_CENTER);
		if (!StringUtils.isEmpty(theCenter)){
			circle.setTheCenter(PointImpl.fromString(theCenter));
		}
		
		String radius = circleElm.getAttribute(RADIUS);
		if (StringUtils.isNumeric(radius)){
			circle.setRadius(Integer.parseInt(radius));
		}
		
		Element labelElm = Util4Deserializer.child(circleElm, LABEL);
		Label lb = this.loadLabel(labelElm);
		circle.setLabel(lb);

		Element fulfilStyleElm = Util4Deserializer.child(circleElm,
				FULFIL_STYLE);
		FulfilStyle fulfilStyle = this.loadFulfilStyle(fulfilStyleElm);
		circle.setFulfilStyle(fulfilStyle);

		LineStyle lineStyle = new LineStyleImpl();
		circle.setLineStyle(lineStyle);

		String color = circleElm.getAttribute(COLOR);
		if (!StringUtils.isEmpty(color)) {
			lineStyle.setColor(color);
		}

		String thick = circleElm.getAttribute(THICK);
		if (!StringUtils.isEmpty(thick) && StringUtils.isNumeric(thick)) {
			lineStyle.setThick(Integer.parseInt(thick));
		}

		String space = circleElm.getAttribute(SPACE);
		if (!StringUtils.isEmpty(space) && StringUtils.isNumeric(space)) {
			lineStyle.setSpace(Integer.parseInt(space));
		}

		String lineType = circleElm.getAttribute(LINE_TYPE);
		if (!StringUtils.isEmpty(lineType)) {
			lineStyle.setLineType(lineType);
		}
	}
	
	protected ActivityShape loadActivityShape(Element activityShapeElm){
		if (activityShapeElm==null){
			return null;
		}
		
		String id = activityShapeElm.getAttribute(ID);
		String ref = activityShapeElm.getAttribute(REF);
		
		ActivityShape activityShape = new ActivityShapeImpl(id);
		activityShape.setWorkflowElementRef(ref);
		
		Element rectElm = Util4Deserializer.child(activityShapeElm, RECTANGLE);
		this.loadRectangle(activityShape, rectElm);
		
		return activityShape;
	}
	
	protected LaneShape loadLaneShape(Element laneElm){
		if (laneElm==null) return null;
		String id = laneElm.getAttribute(ID);
		LaneShape lane = new LaneShapeImpl(id);
		
		Element planeElm = Util4Deserializer.child(laneElm, RECTANGLE);
		this.loadRectangle(lane, planeElm);
		
		//下级节点
		List<Element> children = Util4Deserializer.children(laneElm, CHILD);
		if (children!=null){
			for (Element child : children){
				String type = child.getAttribute(TYPE);
				if (type==null) type="";
				
				if (ACTIVITY.equals(type)){
					ActivityShape activityShape =loadActivityShape(child);
					lane.addWorkflowNodeShape(activityShape);
				}else if (START_NODE.equals(type)){
					StartNodeShape startNodeShape = loadStartNodeShape(child);
					lane.addWorkflowNodeShape(startNodeShape);
				}else if (END_NODE.equals(type)){
					EndNodeShape endNodeShape = loadEndNodeShape(child);
					lane.addWorkflowNodeShape(endNodeShape);
				}else if (ROUTER.equals(type)){
					RouterShape routerShape = loadRouterShape(child);
					lane.addWorkflowNodeShape(routerShape);
				}
				else if (COMMENT.equals(type)){
					CommentShape commentShape = this.loadCommentShape(child);
					lane.addComment(commentShape);
				}
				else if (GROUP.equals(type)){
					GroupShape groupShape = this.loadGroupShapeWithoutConnector(child);
					lane.addGroup(groupShape);
				}
			}
		}
		
		return lane;
	}
	
	/*
	protected void loadPlane(DiagramElement diagramElm, Element planeElm){
		if (planeElm==null) return ;
		Plane plane = new PlaneImpl();
		diagramElm.setShape(plane);
		
		Element labelElm = Util4Deserializer.child(planeElm, LABEL);
		Label lb = this.loadLabel(labelElm);
		plane.setLabel(lb);
		
		Element boundsElm = Util4Deserializer.child(planeElm, BOUNDS);
		Bounds bounds = this.loadBounds(boundsElm);
		plane.setBounds(bounds);
		
		Element fulfilStyleElm = Util4Deserializer.child(planeElm, FULFIL_STYLE);
		FulfilStyle fulfilStyle = this.loadFulfilStyle(fulfilStyleElm);
		plane.setFulfilStyle(fulfilStyle);
	}
	*/
	protected CommentShape loadCommentShape( Element commentElm){
		if (commentElm==null) return null;
		String id = commentElm.getAttribute(ID);
		CommentShape comment = new CommentShapeImpl(id);
		Element rectElm = Util4Deserializer.child(commentElm,RECTANGLE);
		loadRectangle(comment, rectElm);
		
		return comment;
	}
	
	protected void loadRectangle(DiagramElement diagramElm , Element rectElm){
		if (rectElm==null)return;
		Rectangle rect = new RectangleImpl();
		diagramElm.setShape(rect);
		
		Element titleElm = Util4Deserializer.child(rectElm, TITLE);		
		if (titleElm!=null){
			Element labelElm = Util4Deserializer.child(titleElm, LABEL);
			Label lb = this.loadLabel(labelElm);
			rect.setTitleLabel(lb);
		}
		
		Element contentElm = Util4Deserializer.child(rectElm, CONTENT);
		if (contentElm!=null){
			Element labelElm = Util4Deserializer.child(contentElm, LABEL);
			Label lb = this.loadLabel(labelElm);
			rect.setContentLabel(lb);
		}
		
		Element boundsElm = Util4Deserializer.child(rectElm, BOUNDS);
		if (boundsElm!=null){
			Bounds bounds = loadBounds(boundsElm);
			rect.setBounds(bounds);
		}
		
		Element fulfilElm = Util4Deserializer.child(rectElm, FULFIL_STYLE);
		if (fulfilElm!=null){
			FulfilStyle fulfilStyle = loadFulfilStyle(fulfilElm);
			rect.setFulfilStyle(fulfilStyle);
		}
	}
	
	protected Bounds loadBounds(Element boundsElm){
		if (boundsElm==null)return null;
		Bounds bounds = new BoundsImpl();
		
		String upperleftPoint = boundsElm.getAttribute(UPPER_LEFT_CORNER);
		Point p = PointImpl.fromString(upperleftPoint);
		bounds.setUpperLeftCorner(p);
		
		String height = boundsElm.getAttribute(HEIGHT);
		if(StringUtils.isNumeric(height)){
			bounds.setHeight(Integer.parseInt(height));
		}
		
		String width = boundsElm.getAttribute(WIDTH);
		if (StringUtils.isNumeric(width)){
			bounds.setWidth(Integer.parseInt(width));
		}
		
		Element boundsStyleElm = Util4Deserializer.child(boundsElm, BOUNDS_STYLE);
		if(boundsStyleElm!=null){
			BoundsStyle style = new BoundsStyleImpl();
			
			String color = boundsStyleElm.getAttribute(COLOR);
			if (!StringUtils.isEmpty(color)){
				style.setColor(color);
			}
			
			String lineType = boundsStyleElm.getAttribute(LINE_TYPE);
			if (!StringUtils.isEmpty(lineType)){
				style.setLineType(lineType);
			}
			
			String thick = boundsStyleElm.getAttribute(THICK);
			if (StringUtils.isNumeric(thick)){
				style.setThick(Integer.parseInt(thick));
			}
			
			String space = boundsStyleElm.getAttribute(SPACE);
			if (!StringUtils.isEmpty(space) && StringUtils.isNumeric(space)){
				style.setSpace(Integer.parseInt(space));
			}
			
			String radius = boundsStyleElm.getAttribute(RADIUS);
			if (!StringUtils.isEmpty(radius) && StringUtils.isNumeric(radius)){
				style.setRadius(Integer.parseInt(radius));
			}
			
			String cornerType = boundsStyleElm.getAttribute(CORNER_TYPE);
			if (!StringUtils.isEmpty(cornerType)){
				style.setCornerType(cornerType);
			}
			
			bounds.setBoundsStyle(style);
		}
		return bounds;
	}
	
	protected FulfilStyle loadFulfilStyle(Element fulfilStyleElm){
		if (fulfilStyleElm==null) return null;
		
		FulfilStyle fulfilStyle = new FulfilStyleImpl();
		String color = fulfilStyleElm.getAttribute(COLOR);
		if (color!=null){
			fulfilStyle.setColor(color);
		}
		
		
		String gradientStyle = fulfilStyleElm.getAttribute(GRADIENT_STYLE);
		if (gradientStyle!=null){
			fulfilStyle.setGradientStyle(gradientStyle);
		}
		
		return fulfilStyle;
	}
	
	protected AssociationShape loadAssociationShape(Diagram diagram,Element associationElm){
		String id = associationElm.getAttribute(ID);
		String from = associationElm.getAttribute(FROM);
		String to = associationElm.getAttribute(TO);
		
		DiagramElement fromElm = diagram.findChild(from);
		DiagramElement toElm = diagram.findChild(to);
			
		AssociationShape associationShape = new AssociationShapeImpl(id);
		associationShape.setFromDiagramElement(fromElm);
		associationShape.setToDiagramElement(toElm);

		Element lineElm = Util4Deserializer.child(associationElm, LINE);
		loadLine(associationShape,lineElm);
		
		return associationShape;
	}
	
	protected void loadMessageFlowShape(Diagram diagram,Element messageFlowElm){
		String id = messageFlowElm.getAttribute(ID);
		String from = messageFlowElm.getAttribute(FROM);
		String to = messageFlowElm.getAttribute(TO);
		DiagramElement fromElm = diagram.findChild(from);
		DiagramElement toElm = diagram.findChild(to);
			
		MessageFlowShape messageFlow = new MessageFlowShapeImpl(id);
		messageFlow.setFromDiagramElement(fromElm);
		messageFlow.setToDiagramElement(toElm);

		Element lineElm = Util4Deserializer.child(messageFlowElm, LINE);
		loadLine(messageFlow,lineElm);
		
		diagram.addMessageFlow(messageFlow);
		
	}
	protected void loadLine(DiagramElement diagramElm , Element lineElm ){
		Line line = new LineImpl();
		diagramElm.setShape(line);
		
		String direction = lineElm.getAttribute(LABEL_DIRECTION);
		String position = lineElm.getAttribute(LABEL_POSITION);
		
		if (!StringUtils.isEmpty(direction)){
			line.setLabelDirection(direction);
		}
		
		if (!StringUtils.isEmpty(position)) {
			Point p = PointImpl.fromString(position);
			line.setLabelPosition(p);
		}
		
		String pointListStr = lineElm.getAttribute(POINT_LIST);
		if (!StringUtils.isEmpty(pointListStr)){
			List<Point> l = string2PointList(pointListStr);
			line.getPoints().addAll(l);
		}

		LineStyle lineStyle = new LineStyleImpl();
		String color = lineElm.getAttribute(COLOR);
		if (!StringUtils.isEmpty(color)) {
			lineStyle.setColor(color);
		}

		String lineType = lineElm.getAttribute(LINE_TYPE);
		if (!StringUtils.isEmpty(lineType)) {
			lineStyle.setLineType(lineType);
		}

		String spaceStr = lineElm.getAttribute(SPACE);
		if (!StringUtils.isEmpty(spaceStr)) {
			try {
				lineStyle.setSpace(Integer.parseInt(spaceStr));
			} catch (Exception e) {

			}

		}

		String thickStr = lineElm.getAttribute(THICK);
		if (!StringUtils.isEmpty(thickStr)) {
			try {
				lineStyle.setThick(Integer.parseInt(thickStr));
			} catch (Exception e) {

			}

		}

		line.setLineStyle(lineStyle);

		Element labelElm = Util4Deserializer.child(lineElm, LABEL);
		Label lb = loadLabel(labelElm);
		line.setLabel(lb);
	}
    private List<Point> string2PointList(String s){
    	StringTokenizer tokenizer = new StringTokenizer(s,";");
    	List<Point> l = new ArrayList<Point>();
    	while(tokenizer.hasMoreTokens()){
    		String tmp = tokenizer.nextToken();
    		Point p = PointImpl.fromString(tmp);
    		l.add(p);
    	}
    	return l;
    }
	
	protected Label loadLabel(Element labelElm){
		if (labelElm==null) return null;
		Label lb = new LabelImpl();		
		
		String color = labelElm.getAttribute(COLOR);
		if (!StringUtils.isEmpty(color)){
			lb.getFont().setColor(color);
		}
		
		String weight = labelElm.getAttribute(WEIGHT);
		if (!StringUtils.isEmpty(weight)){
			lb.getFont().setWeight(weight);
		}
		
		
		String size = labelElm.getAttribute(SIZE);
		if (!StringUtils.isEmpty(size)){
			try{
				lb.getFont().setSize(Integer.parseInt(size));
			}catch(Exception e){
				
			}
		}

		NodeList nodeList = labelElm.getChildNodes();
		if(nodeList!=null && nodeList.getLength()>0){
			int length = nodeList.getLength();
			for (int i=0;i<length;i++){
				org.w3c.dom.Node node = nodeList.item(i);
				if(node.getNodeType()==org.w3c.dom.Node.CDATA_SECTION_NODE){
					CDATASection cdataSection = (CDATASection)node;
					lb.setText(cdataSection.getData());
					break;
				}
			}
		}
		
		return lb;
	}
	
	protected void loadSubflow(WorkflowProcess wp,Element subflowElement) throws DeserializerException{
		String name = subflowElement.getAttribute(NAME);
		String displayName = subflowElement.getAttribute(DISPLAY_NAME);
		Subflow subflow = new SubflowImpl(wp,name,displayName);
		//
		// 解析datafields
		this.loadProperties(subflow, subflow.getProperties(), Util4Deserializer.child(
				subflowElement, this.PROPERTIES));
		
		// 解析duration
		subflow.setDuration(this.createDuration(subflowElement.getAttribute(DURATION)));


		// 所有业务节点,同时将这个节点的所有的属性都解析出来保存到节点信息中。
		loadActivities(subflow, subflow.getActivities(), Util4Deserializer.child(
				subflowElement, ACTIVITIES));
		
		// 工作流同步器节点
		loadRouters(subflow, subflow.getRouters(), Util4Deserializer.child(
				subflowElement, ROUTERS));
		// 结束节点
		loadEndNodes(subflow, subflow.getEndNodes(), Util4Deserializer.child(
				subflowElement, END_NODES));
		

		// 开始节点
		loadStartNodes(subflow, subflow.getStartNodes(), Util4Deserializer.child(
				subflowElement, START_NODES));
		
		
		// 转移线
		loadTransitions(subflow, subflow.getTransitions(),Util4Deserializer.child(subflowElement,
				TRANSITIONS));
		
		//设置entry
		String entryNodeId = subflowElement.getAttribute(ENTRY);
		WorkflowElement entryNode = subflow.findWFElementById(entryNodeId);
		if (entryNode==null){
			
			subflow.setEntry((Node)entryNode);
		}
		
		//设置activity的attached nodes
		List<StartNode> startNodes = subflow.getStartNodes();
		for (StartNode startNode : startNodes){
			Feature feature = startNode.getFeature();
			if (feature!=null){
				if (feature instanceof TimerStartFeature){
					Activity act = ((TimerStartFeature) feature).getAttachedToActivity();
					if (act!=null){
						act.getAttachedStartNodes().add(startNode);
					}
				}
				else if (feature instanceof CatchFaultFeature){
					Activity act = ((CatchFaultFeature) feature).getAttachedToActivity();
					if (act!=null){
						act.getAttachedStartNodes().add(startNode);
					}
				}
				
				else if (feature instanceof CatchCompensationFeature){
					Activity act = ((CatchCompensationFeature) feature).getAttachedToActivity();
					if (act!=null){
						act.getAttachedStartNodes().add(startNode);
					}
				}
			}
		}
		
		// 所有的监听器
		loadEventListeners(subflow.getEventListeners(), Util4Deserializer.child(
				subflowElement, EVENT_LISTENERS));
		// 加载扩展属性
		Map<String, String> extAttrs = subflow.getExtendedAttributes();
		loadExtendedAttributes(extAttrs, Util4Deserializer.child(
				subflowElement, EXTENDED_ATTRIBUTES));
		
		wp.addSubflow(subflow);

	}
	


	protected void loadProcessImport(WorkflowProcess wp,List<Element> importElems)throws InvalidModelException,DeserializerException,IOException{
		if (importElems==null){
			return ;
		}
		
		for (Element importElm : importElems){
			String type = importElm.getAttribute(IMPORT_TYPE);
			if (Import.SERVICES_IMPORT.equals(type)){
				ImportImpl<ServiceDef> serviceImport = new ImportImpl<ServiceDef>(wp);
				serviceImport.setImportType(type);
				serviceImport.setLocation(importElm.getAttribute(LOCATION));
				
				List<ServiceDef> services = this.importLoader.loadServices(serviceImport.getLocation());
				serviceImport.setContents(services);
				
				wp.getImportsForService().add(serviceImport);
			}
			else if (Import.RESOURCES_IMPORT.equals(type)){
				ImportImpl<ResourceDef> resourceImport = new ImportImpl<ResourceDef>(wp);
				resourceImport.setImportType(type);
				resourceImport.setLocation(importElm.getAttribute(LOCATION));
				
				List<ResourceDef> resources = this.importLoader.loadResources(resourceImport.getLocation());
				resourceImport.setContents(resources);
				
				wp.getImportsForResource().add(resourceImport);
			}
			else if (Import.PROCESS_IMPORT.equals(type)){
				ImportImpl<WorkflowProcess> processImport = new ImportImpl<WorkflowProcess>(wp);
				processImport.setImportType(type);
				processImport.setLocation(importElm.getAttribute(LOCATION));
				wp.getImportsForProcess().add(processImport);
//				WorkflowProcess processes = this.importLoader.loadProcess(processImport.getLocation());
//				List<WorkflowProcess> content = new ArrayList<WorkflowProcess>();
//				content.add(processes);
//				
//				processImport.setContents(content);
			}
		}
	}
	
	/**
	 * duration string的格式为：5 HOUR,或者 5 BUSINESS DAY 等。
	 * @param durationStr
	 * @return
	 */
	protected Duration createDuration(String durationStr){
		if (StringUtils.isEmpty(durationStr))return null;
		StringTokenizer tokenizer = new StringTokenizer(durationStr," ");
		String valueStr = null;
		String unit = null;
		String business = null;
		if (tokenizer.countTokens()==2){
			valueStr = tokenizer.nextToken();
			unit = tokenizer.nextToken();
		}else if (tokenizer.countTokens()==3){
			valueStr = tokenizer.nextToken();
			business = tokenizer.nextToken();
			unit = tokenizer.nextToken();
		}
		if (!StringUtils.isEmpty(valueStr)){
			try{
				int value = Integer.parseInt(valueStr);
				Duration du = new Duration(value,unit);
				if (!StringUtils.isEmpty(business)){
					du.setBusinessTime(true);
				}
				return du;
			}catch(Exception e){
				log.error(e);
				return null;
			}
			
		}else{
			log.error("Error duration attribute:"+durationStr);
		}
		return null;
		
	}
	protected Duration createDuration_deprecated(Element durationElem){
		if (durationElem==null) return null;

		String _v = durationElem.getAttribute(VALUE);
		int intV = -1;
		if (_v!=null){
			try{
				intV = Integer.parseInt(_v);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		String unit = durationElem.getAttribute(UNIT);
		
		String _isBizTime = durationElem.getAttribute(IS_BUSINESS_TIME);
		boolean isBizTime = false;
		if (_isBizTime!=null){
			try{
				isBizTime = Boolean.parseBoolean(_isBizTime);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		Duration du = new Duration(intV,unit);
		du.setBusinessTime(isBizTime);
		return du;
	}

	/**
	 * @param listeners
	 * @param element
	 */
	protected void loadEventListeners(List<EventListenerDef> listeners,
			Element element) {
		listeners.clear();
		if (element == null) {
			return;
		}
		if (element == null) {
			return;
		}
		List<Element> listenerElms = Util4Deserializer.children(element,
				EVENT_LISTENER);
		Iterator<Element> iter = listenerElms.iterator();
		while (iter.hasNext()) {
			Element elm = iter.next();
			EventListenerDefImpl listener = new EventListenerDefImpl();
			
			listener.setName(elm.getAttribute(NAME));
			listener.setDisplayName(elm.getAttribute(DISPLAY_NAME));
			listener.setBizCategory(elm.getAttribute(BIZ_CATEGORY));
			listener.setDescription(Util4Deserializer.elementAsString(elm, DESCRIPTION));
			listener.setBeanName(elm.getAttribute(BEAN_NAME));

			listeners.add(listener);
		}
	}

	/**
	 * @param subflow
	 * @param element
	 * @throws DeserializerException
	 */
	protected void loadStartNodes(Subflow subflow,
			List<StartNode> startNodes, Element startNodesElem)
			throws DeserializerException {
		if (startNodesElem == null) {
			return;
		}
		List<Element> startNodeElements = Util4Deserializer.children(startNodesElem,
				START_NODE);
		startNodes.clear();
		Iterator<Element> iter = startNodeElements.iterator();
		while (iter.hasNext()) {
			Element startNodeElem = iter.next();

			StartNode startNode = new StartNodeImpl(subflow, startNodeElem
					.getAttribute(NAME));

			startNode.setDescription(Util4Deserializer.elementAsString(startNodeElem,
					DESCRIPTION));
			startNode.setDisplayName(startNodeElem.getAttribute(DISPLAY_NAME));
			
			Element decoratorElem = Util4Deserializer.child(startNodeElem, FEATURES);
			if (decoratorElem!=null){
				boolean find = false;
				Element normalStartFeatureElm = Util4Deserializer.child(decoratorElem, NORMAL_START_FEATURE);
				if (normalStartFeatureElm!=null){
					//默认值，无需更多设置
					find = true;
				}
				
				Element catchCompensationFeatureElm = Util4Deserializer.child(decoratorElem, CATCH_COMPENSATION_FEATURE);
				if (catchCompensationFeatureElm!=null && !find){
					CatchCompensationFeatureImpl catchCompensationFeature = new CatchCompensationFeatureImpl();
					catchCompensationFeature.setCompensationCode(catchCompensationFeatureElm.getAttribute(COMPENSATION_CODE));
					
					String attachedToActId = catchCompensationFeatureElm.getAttribute(ATTACHED_TO_ACTIVITY);
					if (attachedToActId!=null){
						catchCompensationFeature.setAttachedToActivity((Activity)subflow.findWFElementById(attachedToActId));
					}
					
					startNode.setFeature(catchCompensationFeature);
					find=true;
				}
				
				Element catchFaultFeatureElm = Util4Deserializer.child(decoratorElem, CATCH_FAULT_FEATURE);
				if (catchFaultFeatureElm!=null && !find){
					CatchFaultFeatureImpl catchFaultFeature = new CatchFaultFeatureImpl();
					catchFaultFeature.setErrorCode(catchFaultFeatureElm.getAttribute(ERROR_CODE));
					
					String attachedToActId = catchFaultFeatureElm.getAttribute(ATTACHED_TO_ACTIVITY);
					if (attachedToActId!=null){
						catchFaultFeature.setAttachedToActivity((Activity)subflow.findWFElementById(attachedToActId));
					}
					
					startNode.setFeature(catchFaultFeature);
					find = true;
				}
				
				Element timerStartFeatureElm = Util4Deserializer.child(decoratorElem, TIMER_START_FEATURE);
				if (timerStartFeatureElm!=null && !find){
					TimerStartFeatureImpl timerStartDec = new TimerStartFeatureImpl();
					
					String timerOperationName = timerStartFeatureElm.getAttribute(TIMER_OPERATION_NAME);
					timerStartDec.setTimerOperationName(TimerOperationName.fromValue(timerOperationName));
					
					String attachedToActId = timerStartFeatureElm.getAttribute(ATTACHED_TO_ACTIVITY);
					if (attachedToActId!=null){
						timerStartDec.setAttachedToActivity((Activity)subflow.findWFElementById(attachedToActId));
					}
					
					String cancelAttachedToAct = timerStartFeatureElm.getAttribute(IS_CANCEL_ATTACHED_TO_ACTIVITY);
					if (cancelAttachedToAct!=null){
						try{
							timerStartDec.setCancelAttachedToActivity(Boolean.parseBoolean(cancelAttachedToAct));
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
					Element startTimeElem = Util4Deserializer.child(timerStartFeatureElm, START_TIME_EXPRESSION);
					if (startTimeElem!=null){
						timerStartDec.setStartTimeExpression(this.createExpression(Util4Deserializer.child(startTimeElem, EXPRESSION)));
					}
					
					Element endTimeElem = Util4Deserializer.child(timerStartFeatureElm, END_TIME_EXPRESSION);
					if (endTimeElem!=null){
						timerStartDec.setEndTimeExpression(this.createExpression(Util4Deserializer.child(endTimeElem, EXPRESSION)));
					}
					
					Element intervalElem = Util4Deserializer.child(timerStartFeatureElm, REPEAT_INTERVAL_EXPRESSION);
					if (intervalElem!=null){
						timerStartDec.setRepeatIntervalExpression(this.createExpression(Util4Deserializer.child(intervalElem, EXPRESSION)));
					}
					
					Element repeatCountElem = Util4Deserializer.child(timerStartFeatureElm, REPEAT_COUNT_EXPRESSION);
					if (repeatCountElem!=null){
						timerStartDec.setRepeatCountExpression(this.createExpression(Util4Deserializer.child(repeatCountElem, EXPRESSION)));
					}
					
					Element cronElem = Util4Deserializer.child(timerStartFeatureElm, CRON_EXPRESSION);
					if (cronElem!=null){
						timerStartDec.setCronExpression(this.createExpression(Util4Deserializer.child(cronElem, EXPRESSION)));
					}
					startNode.setFeature(timerStartDec);
					find = true;
				}
				
				Element webserviceStartElement = Util4Deserializer.child(decoratorElem, WEBSERVICE_START_FEATURE);
				if (webserviceStartElement!=null && !find){
					WebserviceStartFeatureImpl wsStartFeatureImpl = new WebserviceStartFeatureImpl();
					ServiceBinding serviceBinding = this.loadServiceBinding(subflow, Util4Deserializer.child(webserviceStartElement, SERVICE_BINDING));
					wsStartFeatureImpl.setServiceBinding(serviceBinding);
					startNode.setFeature(wsStartFeatureImpl);
				}
			}
			
			loadExtendedAttributes(startNode.getExtendedAttributes(),
					Util4Deserializer.child(startNodeElem, EXTENDED_ATTRIBUTES));

			startNodes.add(startNode);
		}
	}

	/**
	 * @param subflow
	 * @param endNodes
	 * @param element
	 * @throws DeserializerException
	 */
	protected void loadEndNodes(Subflow subflow, List<EndNode> endNodes,
			Element endNodesElem) throws DeserializerException {

		if (endNodesElem == null) {
			return;
		}
		endNodes.clear();
		
		List<Element> endNodesElms = Util4Deserializer.children(endNodesElem, END_NODE);
		Iterator<Element> iter = endNodesElms.iterator();
		while (iter.hasNext()) {
			Element elm = iter.next();
			EndNode endNode = new EndNodeImpl(subflow, elm.getAttribute(NAME));

			endNode.setDescription(Util4Deserializer.elementAsString(elm,
					DESCRIPTION));
			endNode.setDisplayName(elm.getAttribute(DISPLAY_NAME));
			
			Element featuresElem = Util4Deserializer.child(elm, FEATURES);
			if (featuresElem!=null){
				boolean find = false;
				Element normalEndFeatureElm = Util4Deserializer.child(featuresElem, NORMAL_END_FEATURE);
				if (normalEndFeatureElm!=null){
					//默认值，无需更多设置
					find = true;
				}
				
				Element throwCompensationFeatureElm = Util4Deserializer.child(featuresElem, THROW_COMPENSATION_FEATURE);
				if (throwCompensationFeatureElm!=null && !find){
					ThrowCompensationFeatureImpl throwCompensationFeature = new ThrowCompensationFeatureImpl();
					String compensationCodes = throwCompensationFeatureElm.getAttribute(COMPENSATION_CODES);
					if (compensationCodes!=null){
						StringTokenizer tokenizer = new StringTokenizer(compensationCodes,",");
						while(tokenizer.hasMoreTokens()){
							throwCompensationFeature.addCompensationCode(tokenizer.nextToken());
						}
					}else{
						//添加缺省的compensation code
						throwCompensationFeature.addCompensationCode(FpdlConstants.DEFAULT_COMPENSATION_CODE);
					}
					endNode.setFeature(throwCompensationFeature);
					find=true;
				}
				
				Element throwFaultFeatureElm = Util4Deserializer.child(featuresElem, THROW_FAULT_FEATURE);
				if (throwFaultFeatureElm!=null && !find){
					ThrowFaultFeatureImpl throwFaultFeature = new ThrowFaultFeatureImpl();
					throwFaultFeature.setErrorCode(throwFaultFeatureElm.getAttribute(ERROR_CODE));
					
					endNode.setFeature(throwFaultFeature);
					find = true;
				}
				
				Element throwTerminationFeatureElm = Util4Deserializer.child(featuresElem, THROW_TERMINATION_FEATURE);
				if (throwTerminationFeatureElm!=null && !find){
					endNode.setFeature(new ThrowTerminationFeatureImpl());
					find = true;
				}
			}
			
			loadExtendedAttributes(endNode.getExtendedAttributes(), Util4Deserializer
					.child(elm, EXTENDED_ATTRIBUTES));
			endNodes.add(endNode);
		}
	}

	/**
	 * @param wp
	 * @param routers
	 * @param element
	 * @throws DeserializerException
	 */
	protected void loadRouters(Subflow wp,
			List<Router> routers, Element element)
			throws DeserializerException {

		if (element == null) {
			return;
		}
		routers.clear();
		
		List<Element> synchronizerElms = Util4Deserializer.children(element,
				ROUTER);
		Iterator<Element> iter = synchronizerElms.iterator();
		while (iter.hasNext()) {
			Element elm = iter.next();
			Router router = new RouterImpl(wp, elm
					.getAttribute(NAME));

			router.setDescription(Util4Deserializer.elementAsString(elm,
					DESCRIPTION));
			router.setDisplayName(elm.getAttribute(DISPLAY_NAME));

			loadExtendedAttributes(router.getExtendedAttributes(),
					Util4Deserializer.child(elm, EXTENDED_ATTRIBUTES));

			Element featuresElm = Util4Deserializer.child(elm, FEATURES);
			if (featuresElm!=null){				
				Element fElm = Util4Deserializer.child(featuresElm, ANDJOIN_ANDSPLIT_FEATURE);
				if (fElm!=null){
					router.setFeature(new AndJoinAndSplitRouterFeature());
				}
				fElm = Util4Deserializer.child(featuresElm, ORJOIN_ORSPLIT_FEATURE);
				if (fElm!=null){
					router.setFeature(new OrJoinOrSplitRouterFeature());
				}
				fElm = Util4Deserializer.child(featuresElm, DYNAMIC_JOIN_SPLIT_FEATURE);
				if (fElm!=null){
					router.setFeature(new DynamicRouterFeature());
				}
				fElm = Util4Deserializer.child(featuresElm, CUSTOMIZED_JOIN_SPLIT_FEATURE);
				if (fElm!=null){
					router.setFeature(new CustomizedRouterFeature());
				}
			}
			
			routers.add(router);
		}
	}

	/**
	 * @param subflow
	 * @param activities
	 * @param element
	 * @throws DeserializerException
	 */
	protected void loadActivities(Subflow subflow,
			List<Activity> activities, Element element)
			throws DeserializerException {

		if (element == null) {
			// log.debug("Activites element was null");
			return;
		}

		List<Element> activitElements = Util4Deserializer.children(element, ACTIVITY);
		activities.clear();
		Iterator<Element> iter = activitElements.iterator();
		while (iter.hasNext()) {
			Element activityElement = iter.next();

			ActivityImpl activity = new ActivityImpl(subflow, activityElement
					.getAttribute(NAME));

			activity.setDisplayName(activityElement
					.getAttribute(DISPLAY_NAME));
			activity.setDescription(Util4Deserializer.elementAsString(
					activityElement, DESCRIPTION));
			
			Duration du = this.createDuration(activityElement.getAttribute(DURATION));
			activity.setDuration(du);
			activity.setPriority(activityElement.getAttribute(PRIORITY));
			String loopStrategy = activityElement.getAttribute(LOOP_STRATEGY);
			activity.setLoopStrategy(LoopStrategy.fromValue(loopStrategy));
			
			
			//Load Service Binding
			activity.setServiceBinding(loadServiceBinding(subflow,Util4Deserializer.child(activityElement, SERVICE_BINDING)));
			
			//Load Resource Binding
			loadResourceBinding(subflow,activity,Util4Deserializer.child(activityElement, RESOURCE_BINDING));
	
			loadEventListeners(activity.getEventListeners(), Util4Deserializer.child(
					activityElement, EVENT_LISTENERS));
			loadExtendedAttributes(activity.getExtendedAttributes(),
					Util4Deserializer.child(activityElement, EXTENDED_ATTRIBUTES));

			//Load Porperties
			this.loadProperties(activity, activity.getProperties(), 
					Util4Deserializer.child(activityElement, PROPERTIES));

			activities.add(activity);
		}
	}
	
	protected void loadResourceBinding(Subflow subflow,Activity activity,Element resourceBindingElem)throws DeserializerException{
		if (resourceBindingElem==null) return;
		
		WorkflowProcess workflowProcess = (WorkflowProcess)subflow.getParent();
		
		ResourceBindingImpl resourceBinding = new ResourceBindingImpl();
		resourceBinding.setDisplayName(resourceBindingElem.getAttribute(DISPLAY_NAME));
		String assignmentStrategy = resourceBindingElem.getAttribute(ASSIGNMENT_STRATEGY);
		resourceBinding.setAssignmentStrategy(WorkItemAssignmentStrategy.fromValue(assignmentStrategy));
		
		Element administratorsElem = Util4Deserializer.child(resourceBindingElem, ADMINISTRATORS);
		if (administratorsElem!=null){
			List<Element> resourceRefElems = Util4Deserializer.children(administratorsElem, RESOURCE_REF);
			if (resourceRefElems!=null){
				for (Element elm : resourceRefElems){
					String resourceId = elm.getAttribute(RESOURCE_ID);
					
					ResourceDef resource = workflowProcess.getResource(resourceId);
					if (resource==null){
						throw new DeserializerException("Resource not found,resource id = "+resourceId);
					}
					
//					Element prameterAssignmentsElem = Util4Deserializer.child(elm, PARAMETER_ASSIGNMENTS);
//					if (prameterAssignmentsElem!=null){
//						List<Element> parameterAssignmentElems = Util4Deserializer.children(prameterAssignmentsElem, PARAMETER_ASSIGNMENT);
//						for (Element parameterAssignmentElm : parameterAssignmentElems){
//							AssignmentImpl assignment = new AssignmentImpl();
//							Element fromElm = Util4Deserializer.child(parameterAssignmentElm, FROM);
//							
//							assignment.setFrom(this.createExpression(Util4Deserializer.child(fromElm, EXPRESSION)));
//							
//							
//							assignment.setTo(Util4Deserializer.elementAsString(parameterAssignmentElm,TO));
//							
//							resourceRef.getParameterAssignments().add(assignment);
//						}
//					}
					
					resourceBinding.getAdministrators().add(resource);
				}//for (Element elm : resourceRefElems)
			}
		}
		
		Element potentialOwnersElem = Util4Deserializer.child(resourceBindingElem, POTENTIAL_OWNERS);
		if (potentialOwnersElem!=null){
			List<Element> resourceRefElems = Util4Deserializer.children(potentialOwnersElem, RESOURCE_REF);
			if (resourceRefElems!=null){
				for (Element elm : resourceRefElems){
					
					String resourceId = elm.getAttribute(RESOURCE_ID);
					
					ResourceDef resource = workflowProcess.getResource(resourceId);
					if (resource==null){
						throw new DeserializerException("Resource not found,resource id = "+resourceId);
					}
					
//					Element prameterAssignmentsElem = Util4Deserializer.child(elm, PARAMETER_ASSIGNMENTS);
//					if (prameterAssignmentsElem!=null){
//						List<Element> parameterAssignmentElems = Util4Deserializer.children(prameterAssignmentsElem, PARAMETER_ASSIGNMENT);
//						for (Element parameterAssignmentElm : parameterAssignmentElems){
//							ParameterAssignmentImpl assignment = new ParameterAssignmentImpl();
//							Element fromElm = Util4Deserializer.child(parameterAssignmentElm, FROM);
//							
//							assignment.setFrom(this.createExpression(Util4Deserializer.child(fromElm, EXPRESSION)));
//							assignment.setTo(Util4Deserializer.elementAsString(parameterAssignmentElm,TO));
//							
//							resourceRef.getParameterAssignments().add(assignment);
//						}
//					}
					
					resourceBinding.getPotentialOwners().add(resource);
				}//for (Element elm : resourceRefElems)
			}
		}
		
		Element readersElem = Util4Deserializer.child(resourceBindingElem, READERS);
		if (readersElem!=null){
			List<Element> resourceRefElems = Util4Deserializer.children(readersElem, RESOURCE_REF);
			if (resourceRefElems!=null){
				for (Element elm : resourceRefElems){
					String resourceId = elm.getAttribute(RESOURCE_ID);
					
					ResourceDef resource = workflowProcess.getResource(resourceId);
					if (resource==null){
						throw new DeserializerException("Resource not found,resource id = "+resourceId);
					}
					
//					Element prameterAssignmentsElem = Util4Deserializer.child(elm, PARAMETER_ASSIGNMENTS);
//					if (prameterAssignmentsElem!=null){
//						List<Element> parameterAssignmentElems = Util4Deserializer.children(prameterAssignmentsElem, PARAMETER_ASSIGNMENT);
//						for (Element parameterAssignmentElm : parameterAssignmentElems){
//							ParameterAssignmentImpl assignment = new ParameterAssignmentImpl();
//							Element fromElm = Util4Deserializer.child(parameterAssignmentElm, FROM);
//							
//							assignment.setFrom(this.createExpression(Util4Deserializer.child(fromElm, EXPRESSION)));
//							assignment.setTo(Util4Deserializer.elementAsString(parameterAssignmentElm,TO));
//							
//							resourceRef.getParameterAssignments().add(assignment);
//						}
//					}
					
					resourceBinding.getReaders().add(resource);
				}//for (Element elm : resourceRefElems)
			}
		}	
		
		activity.setResourceBinding(resourceBinding);
	}
	
	protected ServiceBinding loadServiceBinding(Subflow subflow,Element serviceBindingElem)throws DeserializerException{
		if (serviceBindingElem==null) return null;
		WorkflowProcess workflowProcess = (WorkflowProcess)subflow.getParent();
		
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
		serviceBinding.setServiceId(serviceBindingElem.getAttribute(SERVICE_ID));
		serviceBinding.setOperationName(serviceBindingElem.getAttribute(OPERATION_NAME));

		ServiceDef service = workflowProcess.getService(serviceBinding.getServiceId());
		if (service==null){
			throw new DeserializerException("Service not found ,id=["+serviceBinding.getServiceId()+"]");
		}
		serviceBinding.setService(service);
		
		if (service.getInterface()==null) return serviceBinding;//人工活动无需定义interface，2012-02-03 
		InterfaceDef interfaceDef = service.getInterface();
		OperationDef op = interfaceDef.getOperation(serviceBinding.getOperationName());
		if (op==null){
			throw new DeserializerException("Operation not found ,service id=["+serviceBinding.getServiceId()+"],opreation name=["+serviceBinding.getOperationName()+"]");
		}
		serviceBinding.setOperation(op);
		
		Element inputAssignmentsElem = Util4Deserializer.child(serviceBindingElem, INPUT_ASSIGNMENTS);
		if (inputAssignmentsElem!=null){
			List<Element> inputAssignmentElems = Util4Deserializer.children(inputAssignmentsElem, INPUT_ASSIGNMENT);
			if (inputAssignmentElems!=null){
				for (Element inputAssignmentElm : inputAssignmentElems){
					Assignment inputAssignment = new AssignmentImpl();
					Element fromElm = Util4Deserializer.child(inputAssignmentElm, FROM);
					Expression from = this.createExpression(Util4Deserializer.child(fromElm, EXPRESSION));
					inputAssignment.setFrom(from);
					Element toElm = Util4Deserializer.child(inputAssignmentElm, TO);
					Expression to = this.createExpression(Util4Deserializer.child(toElm, EXPRESSION));
					inputAssignment.setTo(to);
					
					serviceBinding.getInputAssignments().add(inputAssignment);
				}
			}
		}
		
		Element outputAssignmentsElem = Util4Deserializer.child(serviceBindingElem, OUTPUT_ASSIGNMENTS);
		if (outputAssignmentsElem!=null){
			List<Element> outputAssignmentElems = Util4Deserializer.children(outputAssignmentsElem, OUTPUT_ASSIGNMENT);
			if (outputAssignmentElems!=null){
				for (Element outputAssignmentElm : outputAssignmentElems){
					AssignmentImpl outputAssignment = new AssignmentImpl();
					Element fromElm = Util4Deserializer.child(outputAssignmentElm, FROM);
					Expression from = this.createExpression(Util4Deserializer.child(fromElm, EXPRESSION));
					
					outputAssignment.setFrom(from);
					
					Element toElm = Util4Deserializer.child(outputAssignmentElm, TO);
					Expression to = this.createExpression(Util4Deserializer.child(toElm, EXPRESSION));
					outputAssignment.setTo(to);
					serviceBinding.getOutputAssignments().add(outputAssignment);
				}
			}
		}
		return serviceBinding;
//		Element propOverridesElem = Util4Deserializer.child(serviceBindingElem, PROP_OVERRIDES);
//		if (propOverridesElem!=null){
//			List<Element> propOverrideElems = Util4Deserializer.children(propOverridesElem, PROP_OVERRIDE);
//			if (propOverrideElems!=null){
//				for (Element propOverrideElm : propOverrideElems){
//					PropOverrideImpl propOverride = new PropOverrideImpl();
//					propOverride.setPropGroupName(propOverrideElm.getAttribute(PROP_GROUP_NAME));
//					propOverride.setPropName(propOverrideElm.getAttribute(PROP_NAME));
//					propOverride.setValue(propOverrideElm.getAttribute(VALUE));
//					
//					serviceBinding.getPropOverrides().add(propOverride);
//				}
//			}
//		}
//		
		
	}




	/**
	 * @param subflow
	 * @param transitionsElement
	 * @throws DeserializerException
	 */
	protected void loadTransitions(Subflow subflow,List<Transition> transitions,
			Element transitionsElement) throws DeserializerException {

		if (transitionsElement == null) {
			return;
		}

		transitions.clear();
		
		List<Element> transitionElements = Util4Deserializer.children(transitionsElement, TRANSITION);

		Iterator<Element> iter = transitionElements.iterator();
		while (iter.hasNext()) {
			Element transitionElement = iter.next();
			Transition transition = createTransition(subflow, transitionElement);
			transitions.add(transition);
			Node fromNode = transition.getFromNode();
			Node toNode = transition.getToNode();
			if (fromNode != null ) {
				fromNode.getLeavingTransitions().add(
						transition);
			}
			if (toNode != null ) {
				toNode.getEnteringTransitions()
				.add(transition);
			}
		}
	}


	/**
	 * @param subflow
	 * @param element
	 * @return
	 * @throws DeserializerException
	 */
	protected Transition createTransition(Subflow subflow, Element element)
			throws DeserializerException {
		String fromNodeId = element.getAttribute(FROM);
		String toNodeId = element.getAttribute(TO);
		Node fromNode = (Node) subflow.findWFElementById(fromNodeId);
		Node toNode = (Node) subflow.findWFElementById(toNodeId);

		TransitionImpl transition = new TransitionImpl(subflow,
				element.getAttribute(NAME), fromNode, toNode);


		transition.setDisplayName(element.getAttribute(DISPLAY_NAME));
		transition.setDescription(Util4Deserializer.elementAsString(element,
				DESCRIPTION));
		
		String isLoop = element.getAttribute(IS_LOOP);
		if (isLoop!=null){
			try{
				transition.setIsLoop(Boolean.parseBoolean(isLoop));
			}catch(Exception e){
				
			}
		}
		
		String isDefault = element.getAttribute(IS_DEFAULT);
		if (isDefault!=null){
			try{
				transition.setDefault(Boolean.parseBoolean(isDefault));
			}catch(Exception e){
				
			}
		}
		
		
		Element conditionElement = Util4Deserializer.child(element, CONDITION);
		
		if (conditionElement!=null){
			Element expressionElem = Util4Deserializer.child(conditionElement, EXPRESSION);
			transition.setCondition(createExpression(expressionElem));
		}

		// load extended attributes
		Map<String, String> extAttrs = transition.getExtendedAttributes();
		loadExtendedAttributes(extAttrs, Util4Deserializer.child(element,
				EXTENDED_ATTRIBUTES));

		return transition;
	}
	
	private Expression createExpression(Element expressionElement){
		if (expressionElement!=null){
			ExpressionImpl exp = new ExpressionImpl();
			exp.setLanguage(expressionElement.getAttribute(LANGUAGE));
			exp.setName(expressionElement.getAttribute(NAME));
			exp.setDisplayName(expressionElement.getAttribute(DISPLAY_NAME));
			String dataTypeStr = expressionElement.getAttribute(DATA_TYPE);
			QName qname = QName.valueOf(dataTypeStr);
			exp.setDataType(qname);
			Element bodyElement = Util4Deserializer.child(expressionElement, BODY);
			NodeList nodeList = bodyElement.getChildNodes();
			if(nodeList!=null && nodeList.getLength()>0){
				int length = nodeList.getLength();
				for (int i=0;i<length;i++){
					org.w3c.dom.Node node = nodeList.item(i);
					if(node.getNodeType()==org.w3c.dom.Node.CDATA_SECTION_NODE){
						CDATASection cdataSection = (CDATASection)node;
						exp.setBody(cdataSection.getData());
						break;
					}
				}
			}
			
			Element namespacePrefixUriMapElem = Util4Deserializer.child(expressionElement, this.NAMESPACE_PREFIX_URI_MAP);
	        
			if (namespacePrefixUriMapElem!=null){
				List<Element> children = Util4Deserializer.children(namespacePrefixUriMapElem, ENTRY);
				if (children!=null && children.size()>0){
					for (Element elem : children){
						String prefix = elem.getAttribute(NAME);
						String uri = elem.getAttribute(VALUE);
						exp.getNamespaceMap().put(prefix, uri);
					}
				}
			}

			return exp;
		}
		return null;
	}

	/**
	 * @param parent
	 * @param dataFields
	 * @param propertiesElement
	 * @throws DeserializerException
	 */
	protected void loadProperties(WorkflowElement parent,
			List<Property> dataFields, Element propertiesElement)
			throws DeserializerException {

		if (propertiesElement == null) {
			return;
		}

		List<Element> datafieldsElement = Util4Deserializer.children(propertiesElement,
				PROPERTY);
		dataFields.clear();
		Iterator<Element> iter = datafieldsElement.iterator();
		while (iter.hasNext()) {
			Element dataFieldElement = iter.next();
			dataFields.add(createProperty(parent, dataFieldElement));
		}
	}

	/**
	 * @param parent
	 * @param element
	 * @return
	 * @throws DeserializerException
	 */
	protected Property createProperty(WorkflowElement parent, Element element)
			throws DeserializerException {
		if (element == null) {
			return null;
		}
		String dataTypeStr = element.getAttribute(DATA_TYPE);
		QName dataType = QName.valueOf(dataTypeStr);
		if (dataType == null) {
			dataType = new QName(NameSpaces.JAVA.getUri(),"java.lang.String");
		}

		Property dataField = new PropertyImpl(parent, element.getAttribute(NAME));

		dataField.setDataType(dataType);

		dataField.setDisplayName(element.getAttribute(DISPLAY_NAME));
		dataField.setInitialValueAsString(element.getAttribute(INIT_VALUE));
		dataField.setDescription(Util4Deserializer.elementAsString(element,
				DESCRIPTION));

		return dataField;
	}

	/**
	 * @param extendedAttributes
	 * @param element
	 * @throws DeserializerException
	 */
	protected void loadExtendedAttributes(
			Map<String, String> extendedAttributes, Element element)
			throws DeserializerException {

		if (element == null) {
			return;
		}
		extendedAttributes.clear();
		List<Element> extendAttributeElementsList = Util4Deserializer.children(
				element, EXTENDED_ATTRIBUTE);
		Iterator<Element> iter = extendAttributeElementsList.iterator();
		while (iter.hasNext()) {
			Element extAttrElement = iter.next();
			String name = extAttrElement.getAttribute(NAME);
			String value = extAttrElement.getAttribute(VALUE);

			extendedAttributes.put(name, value);

		}
	}
}
