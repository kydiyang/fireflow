/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Assert;

import org.fireflow.engine.invocation.impl.DynamicAssignmentHandler;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;
import org.fireflow.server.support.MapConvertor;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class MapConvertorTest {
	/**
	 * 如果Map value中有JavaBean，则报错
	 * @throws JAXBException 
	 * @throws UnsupportedEncodingException 
	 */
	@Test()
	public void testUnexpectedMapValues() throws JAXBException, UnsupportedEncodingException{
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("id", "id-123");
		vars.put("age", 30);
		Date now = new Date();
		vars.put("birthday", now);
		vars.put("unexpedtedValue", new MapConvertorTest());

		SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = dformat.format(now);

		MapConvertor convertor = new MapConvertor();
		convertor.putAll(vars,MapConvertor.MAP_TYPE_VARIABLE);

		JAXBContext jc = JAXBContext.newInstance(MapConvertor.class);
		
		// 进行序列化和反序列化
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		marshaller.marshal(convertor, byteOut);
		String xml = byteOut.toString("UTF-8");
		System.out.println(xml);
		System.out.println("===================================");
	}
	
	@Test 
	public void testMarchal_Unmarshal_AssignmentHandlers() throws JAXBException,
	UnsupportedEncodingException, IOException, TransformerException {
		Map<String,DynamicAssignmentHandler> handlers = new HashMap<String,DynamicAssignmentHandler>();
		
		//handler1
		DynamicAssignmentHandler hd1 = new DynamicAssignmentHandler();
		List<User> owners = new ArrayList<User>();
		UserImpl u1 = new UserImpl();
		u1.setId("zhangsan");
		u1.setName("张三");
		u1.getProperties().put("prop1", "value1");
		owners.add(u1);
		
		UserImpl u2 = new UserImpl();
		u2.setId("lisi");
		u2.setName("李四");
		u2.setDeptId("salse");
		u2.setDeptName("销售部");
		owners.add(u2);
		
		hd1.setPotentialOwners(owners);
		
		List<User> reader1 = new ArrayList<User>();
		UserImpl u3 = new UserImpl();
		u3.setId("wangwu");
		u3.setName("王五");
		u3.getProperties().put("prop1", "value1");
		reader1.add(u3);
		
		hd1.setReaders(reader1);
		
		handlers.put("activity1", hd1);
		
		//handler2
		DynamicAssignmentHandler hd2 = new DynamicAssignmentHandler();
		List<User> owners2 = new ArrayList<User>();
		UserImpl u4 = new UserImpl();
		u4.setId("sun");
		u4.setName("孙");
		u4.getProperties().put("prop1", "value1");
		owners2.add(u4);
		
		UserImpl u5 = new UserImpl();
		u5.setId("wang");
		u5.setName("王");
		u5.setDeptId("salse");
		u5.setDeptName("销售部");
		owners2.add(u5);
		
		hd2.setPotentialOwners(owners2);
		
		List<User> reader2 = new ArrayList<User>();
		UserImpl u6 = new UserImpl();
		u6.setId("li");
		u6.setName("李");
		u6.getProperties().put("prop1", "value1");
		reader2.add(u6);
		
		hd2.setReaders(reader2);
		
		handlers.put("activity2", hd2);
		
		MapConvertor convertor = new MapConvertor();
		convertor.putAll(handlers,MapConvertor.MAP_TYPE_ASSIGNMENT_HANDLER);

		JAXBContext jc = JAXBContext.newInstance(MapConvertor.class);
		System.out.println("=========================================");
		// 进行序列化和反序列化
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		marshaller.marshal(convertor, byteOut);

		String xml = byteOut.toString("UTF-8");
		System.out.println(xml);
		System.out.println("===================================");
		ByteArrayInputStream inStream = new ByteArrayInputStream(
				xml.getBytes("UTF-8"));
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object obj = unmarshaller.unmarshal(inStream);

		Assert.assertTrue(obj instanceof MapConvertor);
		Map assignmentHandlerMap = ((MapConvertor)obj).getMap();
		Assert.assertEquals(2, assignmentHandlerMap.size());
		DynamicAssignmentHandler handler = (DynamicAssignmentHandler)assignmentHandlerMap.get("activity1");
		Assert.assertNotNull(handler);
		
		List<User> newOwners = handler.getPotentialOwners();
		Assert.assertNotNull(newOwners);
		Assert.assertEquals(2, newOwners.size());
		
		User u = newOwners.get(0);
		Assert.assertNotNull(u);
		Assert.assertEquals("zhangsan", u.getId());
		Assert.assertEquals("value1", u.getProperty("prop1"));
		

	}
	
	@Test
	public void testMarshal_Unmarshal_Vars() throws JAXBException,
			UnsupportedEncodingException, IOException, TransformerException {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("id", "id-123");
		vars.put("age", 30);
		Date now = new Date();
		vars.put("birthday", now);

		SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = dformat.format(now);

		MapConvertor convertor = new MapConvertor();
		convertor.putAll(vars,MapConvertor.MAP_TYPE_VARIABLE);

		JAXBContext jc = JAXBContext.newInstance(MapConvertor.class);
		// 生成schema
		final List<Result> results = new ArrayList<Result>();
		jc.generateSchema(new SchemaOutputResolver() {
			@Override
			public Result createOutput(String ns, String file)
					throws IOException {
				// save the schema to the list
				DOMResult result = new DOMResult();
				result.setSystemId(file);
				results.add(result);
				return result;
			}
		});
		for (Result result : results) {
			Document doc = (Document) ((DOMResult) result).getNode();
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer
					.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");

			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");

			transformer.transform(new DOMSource(doc), new StreamResult(
					System.out));

		}
		System.out.println("=========================================");
		// 进行序列化和反序列化
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		marshaller.marshal(convertor, byteOut);

		String xml = byteOut.toString("UTF-8");
		System.out.println(xml);

		ByteArrayInputStream inStream = new ByteArrayInputStream(
				xml.getBytes("UTF-8"));
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object obj = unmarshaller.unmarshal(inStream);

		Assert.assertTrue(obj instanceof MapConvertor);
		MapConvertor convertor2 = (MapConvertor) obj;
		Map<String, ? extends Object> entries = convertor2.getMap();
		Assert.assertNotNull(entries);
		Assert.assertEquals(3, entries.size());

		Assert.assertEquals("id-123", entries.get("id"));

		Assert.assertEquals(30, entries.get("age"));

		Date d = (Date) entries.get("birthday");
		String dateStr2 = dformat.format(d);
		Assert.assertEquals(dateStr, dateStr2);

	}
}
