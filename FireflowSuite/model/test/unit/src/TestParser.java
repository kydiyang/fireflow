/**
 * Copyright 2007-2008 陈乜云（非也,Chen Nieyun）
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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.io.Dom4JFPDLParser;
import org.fireflow.model.io.Dom4JFPDLSerializer;
import org.xml.sax.InputSource;

/**
 * @author chennieyun
 * 
 */
public class TestParser extends TestCase{

	/**
	 * @param args
	 */
	public  void testParser() {
		// TODO Auto-generated method stub
		try {
			
			
			File f = new File("D:/MyProject/workflow/FireFlow/model/myworkflowDef.xml");
			FileInputStream in = new FileInputStream(f);

			InputSource insource = new InputSource(in);
			
			Dom4JFPDLParser parser = new Dom4JFPDLParser();
			WorkflowProcess workflowProcess = parser.parse(insource);
			in.close();

			File f2 = new File("D:/MyProject/workflow/FireFlow/model/myworkflowDef2.xml");
			if (f2.exists()) {
				f2.delete();
			}
			FileOutputStream out = new FileOutputStream(f2);
			Dom4JFPDLSerializer ser = new Dom4JFPDLSerializer();
			ser.serialize(workflowProcess, out);
			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("执行完毕");
	}

}
