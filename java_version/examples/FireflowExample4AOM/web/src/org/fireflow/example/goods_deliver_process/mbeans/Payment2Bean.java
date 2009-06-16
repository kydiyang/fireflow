package org.fireflow.example.goods_deliver_process.mbeans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.fireflow.example.goods_deliver_process.persistence.TradeInfo;
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.Required;
import org.operamasks.faces.annotation.SelectItems;
import org.operamasks.faces.component.form.impl.UICombo;


@ManagedBean(name = "Payment2Bean", scope = ManagedBeanScope.REQUEST)
public class Payment2Bean {

	@Bind
	@SelectItems
	private static List goods = new ArrayList();
	
	@Bind
	TradeInfo paymentInfo = new TradeInfo();
	
	static {
		SelectItem selectItem = new SelectItem();

		selectItem.setValue("TCL 电视机");
		selectItem.setLabel("TCL 电视机");
		goods.add(selectItem);

		selectItem = new SelectItem();
		selectItem.setValue("长虹 电视机");
		selectItem.setLabel("长虹 电视机");
		goods.add(selectItem);

		selectItem = new SelectItem();
		selectItem.setValue("万和 热水器");
		selectItem.setLabel("万和 热水器");
		goods.add(selectItem);

		selectItem = new SelectItem();
		selectItem.setValue("方太 抽油烟机");
		selectItem.setLabel("方太 抽油烟机");
		goods.add(selectItem);

		selectItem = new SelectItem();
		selectItem.setValue("海尔 洗衣机");
		selectItem.setLabel("海尔 洗衣机");
		goods.add(selectItem);
	}
	

	@Bind
	private String sn;
	
	@Bind
	private String goodsName;

	@Bind
	private Integer unitPrice = 0;
	
	@Bind
	@Required(message = "数量不能为空")
	private Integer quantity = 0;
	
	@Bind
	private Integer amount = 0;
	
	@Bind
	@Required(message = "客户名称不能为空")
	private String customerName = "";

	@Bind
	private String customerMobile;

	@Bind
	private String customerPhoneFax;
	
	@Bind(id = "goodsName")
	private UICombo goods_comboBox;

	@Action(immediate = true)
	private void goodsName_onselect() {
		goodsName = goods_comboBox.getSubmittedValue().toString();
		if ("TCL 电视机".equals(goodsName)){
			unitPrice=1399;
		}else if ("长虹 电视机".equals(goodsName)){
			unitPrice=1378;
		}else if ("万和 热水器".equals(goodsName)){
			unitPrice=899;
		}else if ("方太 抽油烟机".equals(goodsName)){
			unitPrice=2350;
		}else{
			unitPrice=1620;
		}
	}

	@Bind
	private String response;

	@Action
	private void save() {

	}
}
