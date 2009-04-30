

package org.fireflow.designer.util;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class WrappedListModel implements ListModel{

	public WrappedListModel(List data){
		this.data = data;
        this.listDataListeners = new ArrayList<ListDataListener>();
	}

	public int getSize(){
		return data.size();
	}

	public Object getElementAt(int index){
		return data.get(index);
	}

    public void fireIntervalAdded(int index0, int index1){
        ListDataEvent evt = new ListDataEvent(this,
            ListDataEvent.INTERVAL_ADDED, index0, index1);
        ArrayList l = null;

        synchronized(this){
            l = (ArrayList)(listDataListeners.clone());
        }

        Iterator i = l.iterator();
        while(i.hasNext()){
            ((ListDataListener)i.next()).intervalAdded(evt);
        }
    }

    public void fireIntervalRemoved(int index0, int index1){
        ListDataEvent evt = new ListDataEvent(this,
            ListDataEvent.INTERVAL_REMOVED, index0, index1);
        ArrayList l = null;

        synchronized(this){
            l = (ArrayList)(listDataListeners.clone());
        }

        Iterator i = l.iterator();
        while(i.hasNext()){
            ((ListDataListener)i.next()).intervalRemoved(evt);
        }
    }

    public void fireContentsChanged(int index0, int index1){
        ListDataEvent evt = new ListDataEvent(this,
            ListDataEvent.CONTENTS_CHANGED, index0, index1);
        ArrayList l = null;

        synchronized(this){
            l = (ArrayList)(listDataListeners.clone());
        }

        Iterator i = l.iterator();
        while(i.hasNext()){
            ((ListDataListener)i.next()).contentsChanged(evt);
        }
    }

	public void addListDataListener(ListDataListener l){
		listDataListeners.add(l);
	}

	public void removeListDataListener(ListDataListener l){
		listDataListeners.remove(l);
	}

	private List data;
    private ArrayList<ListDataListener> listDataListeners;

}
