/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.pdl.fpdl20.diagram.figure.part;

import java.awt.Font;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class LabelImpl implements Label {
	private String text = Label.CONTENT_FROM_WORKFLOW_ELEMENT;
	private String direction = Label.TEXT_HORIZANTAL;//横向放置
	private String fontName = "";//缺省为空
	private int fontSize = 10;//
	private String fontColor = "#000000";//缺省为黑色
	private String weight = Label.FONT_STYLE_NORMAL;//正常字体，

	public LabelImpl(){
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.Label#getContent()
	 */
	public String getText() {
		return text;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.Label#setContent(java.lang.String)
	 */
	public void setText(String content) {
		this.text = content;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.Label#getTextDirection()
	 */
	@Override
	public String getTextDirection() {
		return direction;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.Label#setTextDirection(java.lang.String)
	 */
	@Override
	public void setTextDirection(String d) {
		direction = d;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.Label#getFontName()
	 */
	@Override
	public String getFontName() {
		return fontName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.Label#setFontName(java.lang.String)
	 */
	@Override
	public void setFontName(String fontName) {
		this.fontName = fontName;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.Label#getFontSize()
	 */
	@Override
	public int getFontSize() {
		return fontSize;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.Label#setFontSize(int)
	 */
	@Override
	public void setFontSize(int size) {
		fontSize = size;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.Label#getFontColor()
	 */
	@Override
	public String getFontColor() {
		return fontColor;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.Label#setFontColor(java.lang.String)
	 */
	@Override
	public void setFontColor(String cl) {
		fontColor = cl;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.Label#getWeight()
	 */
	@Override
	public String getFontStyle() {
		return weight;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.figure.part.Label#setWeight(java.lang.String)
	 */
	@Override
	public void setFontStyle(String wt) {
		weight = wt;
		
	}

}
