package org.fireflow.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.fireflow.security.persistence.Department;
import org.fireflow.security.persistence.DepartmentDAO;
import org.fireflow.security.persistence.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.context.SecurityContextHolder;

public class Utilities implements InitializingBean{
	DepartmentDAO departmentDAO = null;
	
	List departmentSelectItems = new ArrayList();

	public List getDepartmentSelectItems() {
		return departmentSelectItems;
	}

	public void setDepartmentSelectItems(List departmentSelectItems) {
		this.departmentSelectItems = departmentSelectItems;
	}

	
	public DepartmentDAO getDepartmentDAO() {
		return departmentDAO;
	}

	public void setDepartmentDAO(DepartmentDAO departmentDAO) {
		this.departmentDAO = departmentDAO;
	}

	public void afterPropertiesSet() throws Exception {
		List<Department> departments = departmentDAO.findAll();
		for (int i=0;i<departments.size();i++){
			Department dept = departments.get(i);
			SelectItem selectItem = new SelectItem();
			selectItem.setValue(dept.getCode());
			selectItem.setLabel(dept.getName());
			departmentSelectItems.add(selectItem);
		}
	}
	
	
	

}
