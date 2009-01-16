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
package org.fireflow.model;



public class DataField extends AbstractWFElement { 
	public static final String STRING = "STRING";
	public static final String FLOAT = "FLOAT";
        public static final String DOUBLE = "DOUBLE";
	public static final String INTEGER = "INTEGER";
        public static final String LONG = "LONG";
	public static final String DATETIME = "DATETIME";
	public static final String BOOLEAN = "BOOLEAN";
	
    private String dataType;//取值必须是BasicDataTypes中的基本类型
    private String initialValue;
//    private int length;

    public DataField(){
        this.setDataType(STRING);
    }
    public DataField(WorkflowProcess workflowProcess, String name, String dataType){
        super(workflowProcess, name);
        setDataType(dataType);
    }

    
    public String getDataType(){
        return dataType;
    }


    public void setDataType(String dataType){
        if(dataType == null){
            throw new IllegalArgumentException("Data type cannot be null");
        }
        this.dataType = dataType;
    }

    public String getInitialValue(){
        return initialValue;
    }


    public void setInitialValue(String initialValue){
        this.initialValue = initialValue;
    }

//    public int getLength(){
//        return length;
//    }
//
//
//    public void setLength(int length){
//        this.length = length;
//    }
}
