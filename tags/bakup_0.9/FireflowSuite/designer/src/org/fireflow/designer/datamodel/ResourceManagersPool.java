/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.datamodel;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.fireflow.model.reference.IResourceManager;
import org.fireflow.model.reference.ResourceManager4XmlFile;

/**
 *
 * @author chennieyun
 */
public class ResourceManagersPool {
    static Map<String,Date> resourceFileUpdateTime = new HashMap<String,Date>();
    static Map<String,IResourceManager> resourceManagers = new HashMap<String,IResourceManager>();
    
    private static ResourceManagersPool resourceManagersPool = null;
    
    private ResourceManagersPool(){
        
    }
    
    public static ResourceManagersPool getInstance(){
        if (resourceManagersPool==null){
            resourceManagersPool = new ResourceManagersPool();
        }
        return resourceManagersPool;
    }
    
    public  IResourceManager getResourceManager(String resourceFileName ,
            Date lastUpdateTime ,InputStream in)throws Exception{
        IResourceManager resourceMgr = resourceManagers.get(resourceFileName);

        if (resourceMgr!=null){
            Date d = resourceFileUpdateTime.get(resourceFileName);

            if (lastUpdateTime.equals(d)){//
                return resourceMgr;
            }
        }

        ResourceManager4XmlFile resourceManager4Xml = new ResourceManager4XmlFile();
        resourceManager4Xml.parseResource(in);
        resourceManagers.put(resourceFileName, resourceManager4Xml);
        resourceFileUpdateTime.put(resourceFileName, lastUpdateTime);
        return resourceManager4Xml;
    }
}
