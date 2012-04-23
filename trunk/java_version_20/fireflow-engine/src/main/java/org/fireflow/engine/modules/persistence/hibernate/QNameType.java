package org.fireflow.engine.modules.persistence.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.xml.namespace.QName;

import org.fireflow.pvm.kernel.TokenState;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.hibernate.util.EqualsHelper;

public class QNameType implements UserType {
	private static final int[] TYPES = new int[] { Types.VARCHAR };  

	@Override
	public Object assemble(Serializable arg0, Object arg1)
			throws HibernateException {
		return arg0;
	}

	@Override
	public Object deepCopy(Object arg0) throws HibernateException {
		return arg0;
	}

	@Override
	public Serializable disassemble(Object arg0) throws HibernateException {
		return (Serializable)arg0;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return EqualsHelper.equals(x, y);
	}

	@Override
	public int hashCode(Object arg0) throws HibernateException {
		return arg0.hashCode();
	}

	@Override
	public boolean isMutable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		String s = (String) Hibernate.STRING.nullSafeGet(rs,names[0]);
		if (s==null || s.trim().equals(""))return null;
		return QName.valueOf(s);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException {
		if (value != null) {
			Hibernate.STRING.nullSafeSet(st,((QName)value).toString(), index);
		} else {
			Hibernate.STRING.nullSafeSet(st, null, index);
		}

	}

	@Override
	public Object replace(Object arg0, Object arg1, Object arg2)
			throws HibernateException {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public Class returnedClass() {
		// TODO Auto-generated method stub
		return QName.class;
	}

	@Override
	public int[] sqlTypes() {
		return TYPES;
	}

}
