package org.fireflow.engine.modules.persistence.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class MapStringTypeTest {
	private static Map<String,String> mapOriginal = new HashMap<String,String>();
	private static Map<String,String> emptyMapOriginal = new HashMap<String,String>();
	
	String mapXml = null;
	String emptyMapXml = null;
	
	@BeforeClass
	public static void setUP(){
		mapOriginal.put("key1", "abc");
		mapOriginal.put("key2", "a;ksdfj3\"kfda;jfd==");
		mapOriginal.put("key3", "");
		mapOriginal.put("key4", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Map></Map>");
	}
	
	@Test
	public void testDisassemble() {
		VariableHeaderType type = new VariableHeaderType();
		mapXml = (String)type.map2XmlString(mapOriginal);
		Assert.assertNotNull(mapXml);
		System.out.println(mapXml);
		
		emptyMapXml = (String)type.map2XmlString(emptyMapOriginal);
		Assert.assertNotNull(emptyMapXml);
		System.out.println(emptyMapXml);
	}
	
	@Test
	public void testAssemble() {
		testDisassemble();//首先生成xml
		
		VariableHeaderType type = new VariableHeaderType();
		Map<String,String> map1 = (Map<String,String>)type.xmlString2Map(mapXml);
		Assert.assertNotNull(map1);
		Assert.assertEquals(4, map1.size());
		Assert.assertTrue(map1.containsKey("key1"));
		Assert.assertEquals(mapOriginal.get("key1"), map1.get("key1"));
		
		Assert.assertTrue(map1.containsKey("key2"));
		Assert.assertEquals(mapOriginal.get("key2"), map1.get("key2"));
		
		Assert.assertTrue(map1.containsKey("key3"));
		Assert.assertEquals(mapOriginal.get("key3"), map1.get("key3"));
		
		Assert.assertTrue(map1.containsKey("key4"));
		Assert.assertEquals(mapOriginal.get("key4"), map1.get("key4"));
		
		Map<String,String> map2 = (Map<String,String>)type.xmlString2Map(emptyMapXml);
		Assert.assertNotNull(map2);
		Assert.assertEquals(0, map2.size());
		
		String s = (String)type.map2XmlString(map1);
		System.out.println("=================================");
		System.out.println(s);
	}



}
