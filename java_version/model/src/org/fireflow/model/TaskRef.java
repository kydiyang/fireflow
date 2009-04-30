/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.model;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on Mar 15, 2009
 */
public class TaskRef extends AbstractWFElement {
    Task referencedTask = null;
    public TaskRef(IWFElement parent ,Task task){
        super(parent,task.getName());
        referencedTask = task;
    }

    public TaskRef(Task task){
        referencedTask = task;
    }

    public Task getReferencedTask(){
        return referencedTask;
    }


    public String getName() {
        return referencedTask.getName();
    }

    public void setName(String name) {

    }

    public String getDescription() {
        return referencedTask.getDescription();
    }

    public void setDescription(String description) {

    }



    @Override
    public String toString() {
        return referencedTask.toString();
    }

    public String getDisplayName() {
        return referencedTask.getDisplayName();
    }

    public void setDisplayName(String label) {

    }

}
