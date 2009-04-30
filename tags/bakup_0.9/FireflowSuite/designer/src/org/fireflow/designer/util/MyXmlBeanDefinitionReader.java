/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.util;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 *
 * @author chennieyun
 */
public class MyXmlBeanDefinitionReader extends XmlBeanDefinitionReader{
	public MyXmlBeanDefinitionReader(BeanDefinitionRegistry beanFactory) {
            super(beanFactory);
        }
        
	protected NamespaceHandlerResolver createDefaultNamespaceHandlerResolver() {
            System.out.println("============使用自定义的beanFactory============");
		return new MyNamespaceHandlerResolver(getResourceLoader().getClassLoader());
	}        
}
