/**
 * Copyright 2007-2008,Chen Nieyun��
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation��
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.designer.editpanel.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

public class EndNodeCellView extends VertexView {

    private static VertexRenderer myRenderer = new EndNodeRenderer();

    public EndNodeCellView(Object cell) {
        super(cell);
    }

    public CellViewRenderer getRenderer() {

        return myRenderer;
    }

    public static class EndNodeRenderer extends VertexRenderer {

        transient protected Color gradientColor = null,  gridColor = Color.black,  highlightColor = Color.black,  lockedHandleColor = Color.black,  backgroudColor = Color.white,  foregroundColor = Color.black,  borderColor = Color.black;

        public EndNodeRenderer() {
            super();
        }

        @Override
        public Component getRendererComponent(JGraph graph, CellView view,
                boolean sel, boolean focus, boolean preview) {
            Object cell = view.getCell();
            DefaultGraphCell graphCell = (DefaultGraphCell) cell;            
            backgroudColor = graph.getBackground();
            if (backgroudColor==null)backgroudColor=Color.WHITE;
            foregroundColor = GraphConstants.getForeground(view.getAllAttributes());
            if (foregroundColor==null)foregroundColor = Color.BLACK;
            borderColor = GraphConstants.getBorderColor(view.getAllAttributes());
            if (borderColor==null)borderColor= Color.BLACK;
            return super.getRendererComponent(graph, view, sel, sel, sel);
        }

        public void paint(Graphics g) {
//			super.paint(g);

            setBorder(null);
            setOpaque(false);
            Dimension d = getSize();
            int[] xArr = new int[4];
            int[] yArr = new int[4];
            xArr[0] = (int) (0.25 * (d.width));
            xArr[1] = (int) (0.75 * (d.width));
            xArr[2] = (int) (0.75 * (d.width));
            xArr[3] = (int) (0.25 * (d.width));
            yArr[0] = (int) (0.25 * (d.height));
            yArr[1] = (int) (0.25 * (d.height));
            yArr[2] = (int) (0.75 * (d.height));
            yArr[3] = (int) (0.75 * (d.height));
            Color oldColor = g.getColor();
            g.setColor(foregroundColor);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(1));

            g.drawOval(0 + 1, 0 + 1, d.width - 2, d.height - 2);
            g.fillPolygon(xArr, yArr, 4);

            g.setColor(oldColor);

        }
    }
}
