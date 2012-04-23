package org.fireflow.engine.modules.workitem.event;

import java.util.Locale;
import java.util.ResourceBundle;

import org.fireflow.engine.modules.event.EventTrigger;

public enum WorkItemEventTrigger implements EventTrigger{
	ON_WORKITEM_CREATED,
//	AFTER_WORKITEM_END//暂时保留，2011-02-06
	;
		
	public String getDisplayName(Locale locale){
		ResourceBundle resb = ResourceBundle.getBundle("EngineMessages", locale);
		return resb.getString(this.name());
	}
	
	public String getDisplayName(){
		return this.getDisplayName(Locale.getDefault());
	}	
}
