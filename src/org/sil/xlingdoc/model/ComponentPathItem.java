/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.model;

import org.w3c.dom.Element;

/**
 * 
 */
public class ComponentPathItem {

	String name;
	Element element;
	/**
	 * @param name
	 * @param element
	 */
	public ComponentPathItem(String name, Element element) {
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

}
