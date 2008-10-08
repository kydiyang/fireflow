/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.kenel.event;

import java.util.EventObject;
import org.fireflow.kenel.IToken;

/**
 *
 * @author chennieyun
 */
public class TransitionInstanceEvent extends EventObject {

    public static final int ON_TAKING_THE_TOKEN = 1;
    int eventType = -1;
    private IToken token = null;

    private TransitionInstanceEvent() {
        super(null);
    }

    public TransitionInstanceEvent(Object source) {
        super(source);
    }

    public IToken getToken() {
        return token;
    }

    public void setToken(IToken tk) {
        this.token = tk;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
}
