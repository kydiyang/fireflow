/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.engine;

import org.fireflow.engine.calendar.DefaultCalendarService;
import org.fireflow.engine.condition.ConditionResolver;
import org.fireflow.engine.definition.DefinitionService4DBMS;

/**
 *
 * @author chennieyun
 */
public class RuntimeContextFactory {
    private static RuntimeContext ctx = null;
    
//    public static getRuntimeContext(){
//        if (ctx==null){
//            ctx = RuntimeContext.getInstance();
//            ctx.setCalendarService(new DefaultCalendarService());
//            ctx.setConditionResolver(new ConditionResolver());
//            ctx.setDefinitionService(new DefinitionService4DBMS());
//        }
//    }
}
