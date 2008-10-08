

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.fireflow.model.DataField;
import org.fireflow.model.Duration;
import org.fireflow.model.Task;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.io.Dom4JFPDLSerializer;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;
import org.fireflow.model.reference.Application;
import org.fireflow.model.reference.Form;
import org.fireflow.model.reference.Participant;


/**
 * @author chennieyun
 * 
 */
public class TestSerializer {
	public WorkflowProcess createNewWorkflowProcess() {


		Application app1 = new Application("app1");
		app1.setDescription("这是Application1,调用一个后台java类，实现XXX业务");
		app1.setDisplayName( "Application1");
		app1.setHandler("cn.bestsolution.xxxx.AbcHandler");

		Application app2 = new Application("app2");
		app2.setDescription("这是Application2, 调用一个后台应用程序，实现YYY业务");
		app2.setDisplayName("Application2");
		app2.setHandler("cn.bestsolution.xxxx.AbcHandler2");


		Participant part1 = new Participant("part1");
		part1.setDisplayName("受理岗操作员");
		part1.setDescription("受理岗操作员角色");
		part1
				.setAssignmentHandler("cn.bestsolution.xxxx.RoleAssignmentHandler");


		Participant part2 = new Participant("part2");
		part2.setDescription("电子政务部所有员工");
		part2
				.setAssignmentHandler("cn.bestsolution.xxxx.DepartmentAssignmentHandler");
		part2.setDisplayName("电子政务部");


		WorkflowProcess wfProcess = new WorkflowProcess("process1");
		wfProcess.setDisplayName("单指标新办业务流程");
		wfProcess.getExtendedAttributes().put("extendAttribute1", "1231234");

		// 构造datafield
		DataField dataField = new DataField(wfProcess,"dataField1",DataField.STRING);
		dataField.setInitialValue("initValue4Test");
		wfProcess.getDataFields().add(dataField);

		List activities = wfProcess.getActivities();
		List transitions = wfProcess.getTransitions();
		List synchronizers = wfProcess.getSynchronizers();
		List endNodes = wfProcess.getEndNodes();

		StartNode startNode = new StartNode(wfProcess);
		startNode.setDisplayName("开始");
		wfProcess.setStartNode(startNode);

		Activity activity1 = new Activity(wfProcess,"ShouLi");
		activity1.setDisplayName( "受理");
		activity1.setDescription("受理单指标业务");
		activity1.setCompletionStrategy(Activity.ANY);
		activities.add(activity1);
		Task task11 = new Task(activity1,"task11");
		task11.setDisplayName("受理");
		task11.setDefaultView(Task.EDITFORM);
		task11.setType(Task.FORM);
		task11.setAssignmentStrategy(Task.ANY);
		task11.setStartMode(Task.AUTOMATIC);
		
//		task11.setDuration(new Duration("10m"));
		Form editForm = new Form("newDzb");
		editForm.setDisplayName("单指标受理录入界面");
		editForm.setUri("/cn/hnisi/gac/DzbxbShouli.faces");
		task11.setEditForm(editForm);
		
		Participant part = new Participant("ShouliGang");
		part.setDisplayName("受理岗操作员");
		part.setAssignmentHandler("cn.hnisi.gac.XXXAssignmentHandler");
		task11.setPerformer(part);

		task11.setPriority(1);
		activity1.getTasks().add(task11);

		Activity activity2 = new Activity(wfProcess, "HCXZ");//
		activity2.setDisplayName("核查险种");
		activity2.setDescription("核查险种，:)");
		activities.add(activity2);
		
		Task task21 = new Task(activity2,"task21");
		task21.setDisplayName("核查险种");
		task21.setType(Task.TOOL);
//		task21.setDuration(new Duration("3h"));
		task21.setApplication(app1);
		task21.setExecution(Task.ASYNCHR);
		task21.setPriority(2);
		activity2.getTasks().add(task21);

		Activity activity3 = new Activity(wfProcess,"HCSPYJ");
		activity3.setDisplayName("核查索赔依据");//, "核查索赔依据");
		activities.add(activity3);

		Activity activity4 = new Activity(wfProcess,"拒赔");
		activity4.setDisplayName("拒赔");
		activities.add(activity4);

		Activity activity5 = new Activity(wfProcess,"赔付");//
		activity5.setDisplayName("赔付");
		activities.add(activity5);

		Synchronizer sync1 = new Synchronizer(wfProcess, "分支");
		synchronizers.add(sync1);
		Synchronizer sync2 = new Synchronizer(wfProcess, "汇聚");
		synchronizers.add(sync2);

		EndNode endNode1 = new EndNode(wfProcess, "结束1");
		endNodes.add(endNode1);
		EndNode endNode2 = new EndNode(wfProcess, "结束2");
		endNodes.add(endNode2);

		Transition trans1 = new Transition(wfProcess, "1");
		transitions.add(trans1);
		trans1.setFromNode(startNode);
		trans1.setToNode(activity1);
		activity1.setEnteringTransition(trans1);
		startNode.getLeavingTransitions().add(trans1);

		Transition trans2 = new Transition(wfProcess, "2");
		transitions.add(trans2);
		trans2.setFromNode(activity1);
		trans2.setToNode(sync1);
		activity1.setLeavingTransition(trans2);
		sync1.getEnteringTransitions().add(trans2);

		Transition trans3 = new Transition(wfProcess, "3");
		transitions.add(trans3);
		trans3.setFromNode(sync1);
		trans3.setToNode(activity2);
		sync1.getLeavingTransitions().add(trans3);
		activity2.setEnteringTransition(trans3);

		Transition trans4 = new Transition(wfProcess, "4");
		transitions.add(trans4);
		trans4.setFromNode(sync1);
		trans4.setToNode(activity3);
		sync1.getLeavingTransitions().add(trans4);
		activity3.setEnteringTransition(trans4);

		Transition trans5 = new Transition(wfProcess, "5");
		transitions.add(trans5);
		trans5.setFromNode(activity2);
		trans5.setToNode(sync2);
		activity2.setLeavingTransition(trans5);
		sync2.getEnteringTransitions().add(trans5);

		Transition trans6 = new Transition(wfProcess, "6");
		transitions.add(trans6);
		trans6.setFromNode(activity3);
		trans6.setToNode(sync2);
		activity3.setLeavingTransition(trans6);
		sync2.getEnteringTransitions().add(trans6);

		Transition trans7 = new Transition(wfProcess, "6");
		transitions.add(trans7);
		trans7.setFromNode(sync2);
		trans7.setToNode(activity4);
		activity4.setEnteringTransition(trans7);
		sync2.getLeavingTransitions().add(trans7);

		Transition trans8 = new Transition(wfProcess, "8");
		transitions.add(trans8);
		trans8.setFromNode(sync2);
		trans8.setToNode(activity5);
		activity5.setEnteringTransition(trans8);
		sync2.getLeavingTransitions().add(trans8);

		Transition trans9 = new Transition(wfProcess, "9");
		transitions.add(trans9);
		trans9.setFromNode(activity4);
		trans9.setToNode(endNode1);
		activity4.setLeavingTransition(trans9);
		endNode1.getEnteringTransitions().add(trans9);

		Transition trans10 = new Transition(wfProcess, "10");
		transitions.add(trans10);
		trans10.setFromNode(activity5);
		trans10.setToNode(endNode2);
		activity5.setLeavingTransition(trans10);
		endNode2.getEnteringTransitions().add(trans10);

		return wfProcess;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			TestSerializer testser = new TestSerializer();

			File f = new File("myworkflowDef.xml");
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			Dom4JFPDLSerializer ser = new Dom4JFPDLSerializer();
			ser.serialize(testser.createNewWorkflowProcess(), out);
			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("执行完毕。");
	}

}
