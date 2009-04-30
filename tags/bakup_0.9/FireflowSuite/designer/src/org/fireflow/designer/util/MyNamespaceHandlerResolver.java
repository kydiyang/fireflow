/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 *
 * @author chennieyun
 */
public class MyNamespaceHandlerResolver  implements NamespaceHandlerResolver {
	/**
	 * The location to look for the mapping files. Can be present in multiple JAR files.
	 */
	private static final String SPRING_HANDLER_MAPPINGS_LOCATION = "META-INF/spring.handlers";


	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	/** Stores the mappings from namespace URI Strings to NamespaceHandler instances */
	private Map handlerMappings;


	/**
	 * Create a new <code>DefaultNamespaceHandlerResolver</code> using the
	 * default mapping file location.
	 * <p>This constructor will result in the thread context ClassLoader being used
	 * to load resources.
	 * @see #SPRING_HANDLER_MAPPINGS_LOCATION
	 */
	public MyNamespaceHandlerResolver() {
		this(null, SPRING_HANDLER_MAPPINGS_LOCATION);
	}


	/**
	 * Create a new <code>DefaultNamespaceHandlerResolver</code> using the
	 * default mapping file location.
	 * @param classLoader the {@link ClassLoader} instance used to load mapping resources (may be <code>null</code>, in
	 * which case the thread context ClassLoader will be used) 
	 * @see #SPRING_HANDLER_MAPPINGS_LOCATION
	 */
	public MyNamespaceHandlerResolver(ClassLoader classLoader) {
		this(classLoader, SPRING_HANDLER_MAPPINGS_LOCATION);
	}

	/**
	 * Create a new <code>DefaultNamespaceHandlerResolver</code> using the
	 * supplied mapping file location.
	 * @param classLoader the {@link ClassLoader} instance used to load mapping resources (may be <code>null</code>, in
	 * which case the thread context ClassLoader will be used)
	 * @param handlerMappingsLocation the mapping file location
	 * @see #SPRING_HANDLER_MAPPINGS_LOCATION
	 */
	public MyNamespaceHandlerResolver(ClassLoader classLoader, String handlerMappingsLocation) {
		Assert.notNull(handlerMappingsLocation, "Handler mappings location must not be null");
		ClassLoader classLoaderToUse = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
		initHandlerMappings(classLoaderToUse, handlerMappingsLocation);
	}


	/**
	 * Load the namespace URI -> <code>NamespaceHandler</code> class mappings from the configured
	 * mapping file. Converts the class names into actual class instances and checks that
	 * they implement the <code>NamespaceHandler</code> interface. Pre-instantiates an instance
	 * of each <code>NamespaceHandler</code> and maps that instance to the corresponding
	 * namespace URI.
	 */
	private void initHandlerMappings(ClassLoader classLoader, String handlerMappingsLocation) {
		Properties mappings = loadMappings(classLoader, handlerMappingsLocation);
		if (logger.isDebugEnabled()) {
			logger.debug("Loaded mappings [" + mappings + "]");
		}
		this.handlerMappings = new HashMap(mappings.size());
		for (Enumeration en = mappings.propertyNames(); en.hasMoreElements();) {
			String namespaceUri = (String) en.nextElement();
			String className = mappings.getProperty(namespaceUri);
			try {
				Class handlerClass = ClassUtils.forName(className, classLoader);
				if (!NamespaceHandler.class.isAssignableFrom(handlerClass)) {
                                    continue;
//					throw new IllegalArgumentException("Class [" + className +
//							"] does not implement the NamespaceHandler interface");
				}
				NamespaceHandler namespaceHandler = (NamespaceHandler) BeanUtils.instantiateClass(handlerClass);
				namespaceHandler.init();
				this.handlerMappings.put(namespaceUri, namespaceHandler);
			}
			catch (ClassNotFoundException ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Ignoring handler [" + className + "]: class not found", ex);
				}
			}
			catch (LinkageError ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Ignoring handler [" + className + "]: problem with class file or dependent class", ex);
				}
			}
		}
	}

	private Properties loadMappings(ClassLoader classLoader, String handlerMappingsLocation) {
		try {
			return PropertiesLoaderUtils.loadAllProperties(handlerMappingsLocation, classLoader);
		}
		catch (IOException ex) {
			throw new IllegalStateException(
					"Unable to load NamespaceHandler mappings from location [" +
					handlerMappingsLocation + "]. Root cause: " + ex);
		}
	}


	/**
	 * Locate the {@link NamespaceHandler} for the supplied namespace URI
	 * from the configured mappings.
	 * @param namespaceUri the relevant namespace URI
	 * @return the located {@link NamespaceHandler}, or <code>null</code> if none found
	 */
	public NamespaceHandler resolve(String namespaceUri) {
		return (NamespaceHandler) this.handlerMappings.get(namespaceUri);
	}
}
