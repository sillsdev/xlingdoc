/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.model;

import org.w3c.dom.Element;

import javafx.scene.text.Text;

/**
 * 
 */
public class ComponentPathItem {

	String name;
	Element element;
	Text text;
	/**
	 * @param name
	 * @param element
	 * @param text TODO
	 */
	public ComponentPathItem(String name, Element element, Text text) {
		super();
		this.name = name;
		this.element = element;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Element getElement() {
		return element;
	}
	public void setElement(Element element) {
		this.element = element;
	}
	public Text getText() {
		return text;
	}
	public void setText(Text text) {
		this.text = text;
	}

}
