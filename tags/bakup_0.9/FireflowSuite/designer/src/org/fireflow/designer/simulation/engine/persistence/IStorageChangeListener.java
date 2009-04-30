/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.simulation.engine.persistence;

/**
 *
 * @author chennieyun
 */
public interface IStorageChangeListener {
    public void onStorageChanged(StorageChangedEvent e);
}
