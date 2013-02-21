/**
 * Copyright 2007-2010 非也
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
package org.fireflow.client.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.misc.EntityPropertyXmlAdapter;
import org.fireflow.misc.ObjectXmlAdapter;
import org.firesoa.common.util.JavaDataTypeConvertor;

/**
 * @author 非也
 * @version 2.0
 */
@XmlRootElement(name="between")
@XmlType(name="betweenExpressionType",propOrder={"property","lo","hi"})
@XmlAccessorType(XmlAccessType.FIELD)
public class BetweenExpression extends AbsCriterion implements Criterion {
	@XmlAttribute(name="property")
	@XmlJavaTypeAdapter(EntityPropertyXmlAdapter.class)
	private EntityProperty property;
	
	@XmlElement(name="lo")
	@XmlJavaTypeAdapter(ObjectXmlAdapter.class)
	private Object lo;
	
	@XmlElement(name="hi")
	@XmlJavaTypeAdapter(ObjectXmlAdapter.class)
	private Object hi;
	
	public BetweenExpression(){
		
	}

	public BetweenExpression(EntityProperty property, Object lo, Object hi) {
		if (!JavaDataTypeConvertor.isPrimaryObject(lo)
				||
				!JavaDataTypeConvertor.isPrimaryObject(hi)){
			throw new IllegalArgumentException("Between表达式只接受基本数据类型（包含String ,java.util.Date,不含byte）");
		}
		if (lo==null || hi==null){
			throw new IllegalArgumentException("Between表达式不接受空值。");
		}
		this.property = property;
		this.lo = lo;
		this.hi = hi;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.Criterion#toSqlString()
	 */
	public String toSqlString() {
		return property.getColumnName() + " "+getOperation()+" ? and ? ";
	}
	
	/**
	 * TODO 对于Date类型，返回的表达式不能正确执行
	 */
	public String toString() {
		return property.getColumnName() + " "+getOperation()+valueToSQLString(lo) + " and "+ valueToSQLString(hi);
	}

	public String getOperation(){
		return Criterion.OPERATION_BETWEEN;
	}
	public EntityProperty getEntityProperty(){
		return property;
	}
	public Object[] getValues(){
		return new Object[]{lo,hi};
	}
}
