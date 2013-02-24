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
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

import org.fireflow.server.support.PropertiesConvertor;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class PropertiesConvertorTest {
	@Test
	public void testMarshal_Unmarshal() throws JAXBException,
			UnsupportedEncodingException, IOException, TransformerException {
		Properties vars = new Properties();
		vars.put("id", "id-123");
		vars.put("age", "30");


		PropertiesConvertor convertor = new PropertiesConvertor();
		convertor.putAll(vars);
		

		JAXBContext jc = JAXBContext.newInstance(PropertiesConvertor.class);
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

		Assert.assertTrue(obj instanceof PropertiesConvertor);
		PropertiesConvertor convertor2 = (PropertiesConvertor) obj;
		Properties props = convertor2.getProperties();
		Assert.assertNotNull(props);
		Assert.assertEquals(2, props.size());

		Assert.assertEquals("id-123", props.get("id"));

		Assert.assertEquals("30", props.get("age"));


	}
}
