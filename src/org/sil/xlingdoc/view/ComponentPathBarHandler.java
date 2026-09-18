/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.view;

import java.util.List;

import org.sil.xlingdoc.service.dtdhandling.XmlNameMapper;
import org.w3c.dom.Element;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * 
 */
public class ComponentPathBarHandler {
//	private final String kClass = "class";
//	List<ComponentPathItem> componentsInPathBar = new ArrayList<ComponentPathItem>();
	public final String kComponentGap = " " + Character.toString(0x227a);
	public final String kStyleOfFinal = "-fx-font-weight: bold;";
	final String kComponentBreak = " > ";
	final Color kComponentPathItemColor = Color.MAROON;
	private final String kClass = "class";
	private final String kComponentSelected = "component-selected";
	Element lastElementHighlighted = null;

	Element elementSelected = null;
	private final List<String> elementsToIgnore = List.of(
			"BODY",
			"DETAILS",
			"HTML",
			"INPUT",
			"SUMMARY"
			);

	public Element getElementSelected() {
		return elementSelected;
	}

	public Color getComponentPathItemColor() {
		return kComponentPathItemColor;
	}

//	public List<ComponentPathItem> getComponentsInPathBar() {
//		return componentsInPathBar;
//	}

	public void updateComponentPathBar(Element element, TextFlow componentPathBar) {
		Platform.runLater(() -> {
			removeHighlightFromLastElementHighlighted();
			componentPathBar.getChildren().clear();
//			componentsInPathBar.clear();
			addElementToComponentPathBar(element, componentPathBar);
			markLastElement(element, componentPathBar);
		});
	}

	// public for testing
	public void addElementToComponentPathBar(Element element, TextFlow componentPathBar) {
		String tagName = element.getTagName();
		if (tagName.equals("BODY")) {
			// no need to look further
			return;
		}
		addElementToComponentPathBar((Element) element.getParentNode(), componentPathBar);
		if (elementsToIgnore.contains(tagName)) {
			return;
		}
		if (tagName.equals("TH") || tagName.equals("TD")) {
			Text tTr = new Text(" tr");
			tTr.setFill(kComponentPathItemColor);
			Text tTrGap = new Text(kComponentGap);
//			ComponentPathItem trItem = new ComponentPathItem("tr", (Element) element.getParentNode(), tTr);
			tTr.setUserData(element.getParentNode());
			componentPathBar.getChildren().addAll(tTr, tTrGap);
//			componentsInPathBar.add(trItem);
		}
		String adjustedTagName = XmlNameMapper.getMappedElementName(tagName);
		Text t = new Text(" " + adjustedTagName);
		t.setUserData(element);
		t.setFill(kComponentPathItemColor);
		componentPathBar.getChildren().add(t);
			Text tGap = new Text(kComponentGap);
			tGap.setUserData("gap");
			componentPathBar.getChildren().add(tGap);
	}

	// public for testing
	public void markLastElement(Element element, TextFlow componentPathBar) {
		ObservableList<Node> children = componentPathBar.getChildren();
		int lastChild = children.size();
		Node lastNode = children.get(lastChild - 2);
		if (lastNode instanceof Text lastText) {
			lastText.setStyle(kStyleOfFinal);
			children.removeLast();
			children.removeLast();
			children.add(lastText);
			elementSelected = element;
		}
	}

	public void highlightSelectedElement(Element element) {
		Platform.runLater(() -> {
			System.out.println("highlightSelectedElement; element = " + element.getTagName());
			// TODO: what if there are more CSS names in the class attribute?
			String sClass = element.getAttribute(kClass);
			sClass = sClass + " " + kComponentSelected;
			element.setAttribute(kClass, sClass);
			removeHighlightFromLastElementHighlighted();
			lastElementHighlighted = element;
		});
	}

	private void removeHighlightFromLastElementHighlighted() {
		if (lastElementHighlighted != null) {
			String cssClass = lastElementHighlighted.getAttribute(kClass);
			cssClass = cssClass.replaceAll(kComponentSelected, "");
			lastElementHighlighted.setAttribute(kClass, cssClass);
		}
	}

}
