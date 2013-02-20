package org.fireflow.pdl.fpdl20.process.features.startnode;

import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.features.Feature;

public interface CatchFaultFeature  extends Feature{
	public static final String CATCH_ALL_FAULT = "org.fireflow.constants.CATCH_ALL_FAULT";
	/**
	 * 被catch的Activity
	 * @return
	 */
	public Activity getAttachedToActivity();
	
	public void setAttachedToActivity(Activity act);
	
	/**
	 * 被监听的异常类的名称
	 * @return
	 */
	public String getErrorCode();
	
	public void setErrorCode(String errorCode);
}
