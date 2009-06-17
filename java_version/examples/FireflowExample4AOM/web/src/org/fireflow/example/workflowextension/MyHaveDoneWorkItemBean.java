package org.fireflow.example.workflowextension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.fireflow.BasicManagedBean;
import org.fireflow.engine.EngineException;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.IWorkflowSessionCallback;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.example.goods_deliver_process.workflowextension.GoodsDeliverTaskInstance;
import org.fireflow.kernel.KernelException;
import org.fireflow.model.FormTask;
import org.fireflow.model.Task;
import org.fireflow.security.persistence.User;
import org.fireflow.security.util.SecurityUtilities;
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.grid.impl.UIEditDataGrid;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

@ManagedBean(scope = ManagedBeanScope.REQUEST)
@SuppressWarnings( { "unused", "serial" })
public class MyHaveDoneWorkItemBean extends BasicManagedBean {

	@SuppressWarnings("unchecked")
	@Bind(id = "grid", attribute = "value")
	private List data = (List) this.doQueryMyHaveDoneWorkItems();

	@Bind(id = "grid")
	private UIEditDataGrid grid;

	private List doQueryMyHaveDoneWorkItems() {
		IWorkflowSession wflsession = workflowRuntimeContext
				.getWorkflowSession();
		User currentUser = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		final String actorId = currentUser.getId();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		try {
			List<IWorkItem> ws = (List<IWorkItem>) wflsession
					.execute(new IWorkflowSessionCallback() {

						public Object doInWorkflowSession(RuntimeContext arg0)
								throws EngineException, KernelException {
							// TODO Auto-generated method stub
							IPersistenceService persistenceService = arg0
									.getPersistenceService();
							return persistenceService
									.findHaveDoneWorkItems(actorId);
						}

					});
			for (IWorkItem w : ws) {
				Map<String, Object> map = new HashMap<String, Object>();

				map.put("id", w.getId());
				map.put("state", w.getState() == 7 ? "已处理" : "已取消");
				GoodsDeliverTaskInstance task = (GoodsDeliverTaskInstance) w
						.getTaskInstance();
				map.put("displayName", task.getDisplayName());
				map.put("goodsName", task.getGoodsName());
				map.put("sn", task.getSn());
				map.put("quantity", task.getQuantity());
				map.put("customerName", task.getCustomerName());
				map.put("endTime", w.getEndTime());
				map.put("actorId",w.getActorId() );
				datas.add(map);
			}
		} catch (EngineException e) {
			e.printStackTrace();
		} catch (KernelException e) {
			e.printStackTrace();
		}
		return datas;
	}

}