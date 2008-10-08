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

import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

public class OptionalLineBorder extends LineBorder {
	private final int LEFT = 0x1;
	private final int RIGHT = 0x2;
	private final int TOP = 0x4;
	private final int BOTTOM = 0x8;
	private int paintWay = 0xF;

	public OptionalLineBorder(Color color) {
		super(color);
	}

	public OptionalLineBorder(Color color, boolean top, boolean left,
			boolean bottom, boolean right) {
		this(color);
		paintTop(top);
		paintLeft(left);
		paintBottom(bottom);
		paintRight(right);
	}

	public void setColor(Color color) {
		if (color != null)
			super.lineColor = color;
	}

	public void paintLeft(boolean b) {
		paintSide(LEFT, b);
	}

	public boolean isPaintLeft() {
		return isPaintSide(LEFT);
	}

	public void paintRight(boolean b) {
		paintSide(RIGHT, b);
	}

	public boolean isPaintRight() {
		return isPaintSide(RIGHT);
	}

	public void paintTop(boolean b) {
		paintSide(TOP, b);
	}

	public boolean isPaintTop() {
		return isPaintSide(TOP);
	}

	public void paintBottom(boolean b) {
		paintSide(BOTTOM, b);
	}

	public boolean isPaintBottom() {
		return isPaintSide(BOTTOM);
	}

	private void paintSide(int side, boolean b) {
		if (b) {
			paintWay |= side;
		} else {
			paintWay &= ~side;
		}
	}

	private boolean isPaintSide(int side) {
		return (paintWay & side) == side;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		Color oldColor = g.getColor();
		g.setColor(lineColor);
		if (isPaintTop()) {
			g.drawLine(x, y, width, y);
		}
		if (isPaintLeft()) {
			g.drawLine(x, y, x, height);
		}
		if (isPaintBottom()) {
			g.drawLine(x, y + height - 1, width, y + height - 1);
		}
		if (isPaintRight()) {
			g.drawLine(x + width - 1, y, x + width - 1, height);
		}
		g.setColor(oldColor);
	}
}
