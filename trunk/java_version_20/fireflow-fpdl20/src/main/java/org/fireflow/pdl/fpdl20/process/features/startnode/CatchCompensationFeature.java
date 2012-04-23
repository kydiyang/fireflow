package org.fireflow.pdl.fpdl20.process.features.startnode;

import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.features.Feature;

public interface CatchCompensationFeature  extends Feature{
	/**
	 * 被catch的Activity
	 * @return
	 */
	public Activity getAttachedToActivity();
	
	public void setAttachedToActivity(Activity act);

	public void setCompensationCode(String compensationCode);
	public String getCompensationCode();
}
