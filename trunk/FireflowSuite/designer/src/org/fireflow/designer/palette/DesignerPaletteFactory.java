/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.palette;

import java.io.IOException;
import org.netbeans.spi.palette.PaletteController;
import org.netbeans.spi.palette.PaletteFactory;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author chennieyun
 */
public class DesignerPaletteFactory {
    public static final String FIREFLOW_DESIGNER_PALETTE_FOLDER = "FireflowDesignerPalette";
    
    private static PaletteController palette = null;
    
    public static PaletteController getPalette() throws IOException {
        AbstractNode paletteRoot = new AbstractNode(Children.LEAF);
        if (palette == null)
            palette = PaletteFactory.createPalette(paletteRoot, new DesignerPaletteActions()); 
        return palette;
    }
}
