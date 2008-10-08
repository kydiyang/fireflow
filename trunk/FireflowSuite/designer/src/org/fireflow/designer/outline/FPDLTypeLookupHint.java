/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.outline;

import org.netbeans.spi.navigator.NavigatorLookupHint;

/**
 *
 * @author chennieyun
 */
public class FPDLTypeLookupHint implements NavigatorLookupHint {

    public String getContentType() {
        return "text/x-fireflow+xml";
    }

}
