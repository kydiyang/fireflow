package org.fireflow.engine;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.definition.IDefinitionService;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.engine.taskinstance.ITaskInstanceManager;
import org.fireflow.kenel.INodeInstance;
import org.fireflow.kenel.INetInstance;
import org.fireflow.kenel.KenelException;
import org.fireflow.kenel.KenelManager;
import org.fireflow.kenel.condition.IConditionResolver;
import org.fireflow.kenel.impl.ActivityInstance;
import org.fireflow.kenel.impl.EndNodeInstance;
import org.fireflow.kenel.impl.StartNodeInstance;
import org.fireflow.kenel.impl.SynchronizerInstance;
import org.fireflow.kenel.impl.TransitionInstance;
import org.fireflow.kenel.impl.NetInstance;
import org.fireflow.kenel.plugin.IKenelExtension;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.Node;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

/**
 * RuntimeContext提供所有流程实例运行环境
 * @author chennieyun
 *
 */
public class RuntimeContext{
	private static RuntimeContext instance = null;
	private boolean isInitialized = false;
	//kenel扩展注册表

	private IConditionResolver  conditionResolver = null;
	private IPersistenceService persistenceService = null;
	private IDefinitionService definitionService = null;
	private KenelManager kenelManager = null;
	private String configFileName = null;
	private ITaskInstanceManager taskInstanceManager = null;
	
	private ThreadLocal<Object> currentDBSession = new ThreadLocal<Object>();
//	private ThreadLocal<IFireflowSession> currentFireflowSession = new ThreadLocal<IFireflowSession>();
	
	public void setCurrentDBSession(Object dbSession){
		currentDBSession.set(dbSession);
	}
	
	public Object getCurrentDBSession(){
		return currentDBSession.get();
	}

	
//	public IFireflowSession getCurrentFireflowSession() {
//		return currentFireflowSession.get();
//	}
//
//	public void setCurrentFireflowSession(IFireflowSession currentFireflowSession) {
//		this.currentFireflowSession.set(currentFireflowSession);
//	}

	//为了使kenel能够用于IDE环境，runtimecontext不要采用此模式，而是通过token区获取。
	public static RuntimeContext getInstance(){
		if (instance==null){
			instance = new RuntimeContext();
		}
		return instance;
	}
	
//	public RuntimeContext(String configFileName){
//
//	}
	
	private RuntimeContext(){
		
	}
	
	
//	public static INetInstance createNewProcessInstance(WorkflowProcess workflowProcess)throws KenelException{
//
//	}
	
	
	public IConditionResolver getConditionResolver(){
		return conditionResolver;
	}
	
	public void setConditionResolver(IConditionResolver resolver){
		this.conditionResolver = resolver;
	}
	
	public ITaskInstanceManager getTaskInstanceManager(){
		return taskInstanceManager;
	}
	
	public void setTaskInstanceManager(ITaskInstanceManager taskInstanceManager){
		this.taskInstanceManager = taskInstanceManager;
	}
	
	public IPersistenceService getPersistenceService(){
		return this.persistenceService;
	}
	
	public void setPersistenceService(IPersistenceService persistenceService){
		this.persistenceService = persistenceService;
	}
	
	public IDefinitionService getDefinitionService(){
		return definitionService;
	}
	
	public void setDefinitionService(IDefinitionService service){
		this.definitionService = service;
	}
	

	
	public Date getSysDate(){
		return new Date();
	}

	public KenelManager getKenelManager() {
		return kenelManager;
	}

	public void setKenelManager(KenelManager kenelManager) {
		this.kenelManager = kenelManager;
	}

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFile) {
		this.configFileName = configFile;		
	}
	
	public void initialize()throws EngineException,KenelException{
//		System.out.println("执行initialize(),the isInitialized="+this.isInitialized);
//		System.out.println("看看有没有 受理环节实例"+this.getKenelManager().getWFElementInstance("受理"));
		if (!isInitialized){
//			initKenelExtensions();
			initAllNetInstances();
			isInitialized = true;
		}
	}
	
        /*
	private void initKenelExtensions()throws EngineException{
		if (this.isInitialized)return;
		if (this.configFileName==null){
			throw new EngineException("Fileflow配置文件名为空，无法初始化RuntimeContext。");
		}
		kenelExtensions.clear();
		
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(configFileName);
		if (inStream==null){
			throw new EngineException("classpath中没有找到名称为"+configFileName+"的配置文件，无法初始化RuntimeContext。");
		}
		SAXReader reader = new SAXReader();
		try{
			Document configDoc = reader.read(inStream);
			
			List<org.dom4j.Node> nodeList = configDoc.selectNodes("//fireflow-config/kenel/extensions/extension");
			for(int i=0;nodeList!=null && i<nodeList.size();i++){
				org.dom4j.Node extensionNode = nodeList.get(i);
				String className = extensionNode.valueOf("@class");
				String targetName = extensionNode.valueOf("@target");
				Class extensionClass = Class.forName(className);
				Object obj = extensionClass.newInstance();
				
				List<IKenelExtension> extensionList = kenelExtensions.get(targetName);
				if (extensionList==null){
					extensionList = new ArrayList<IKenelExtension>();
					kenelExtensions.put(targetName, extensionList);
				}
				extensionList.add((IKenelExtension)obj);
			}
			
		}catch(Exception ex){
			throw new EngineException(ex.toString());
		}
	}
         * */
	public void initAllNetInstances()throws KenelException{
		List<WorkflowProcess> allWfProcess = definitionService.getAllWorkflowProcesses();
                this.getKenelManager().clearAllWFElementInstance();
		for(int i=0;allWfProcess!=null && i<allWfProcess.size();i++){
			WorkflowProcess process = allWfProcess.get(i);
			this.getKenelManager().createNetInstance( process);
		}
	}
	

}
