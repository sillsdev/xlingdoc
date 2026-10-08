/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.view;

import java.util.List;

import org.sil.xlingdoc.Constants;
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
	public final String kComponentGap = " " + Character.toString(0x227a);
	public final String kStyleOfFinal = "-fx-font-weight: bold;";
	public final String kStyleOfText = "-fx-font-style: italic;";
	final String kComponentBreak = " > ";
	final Color kComponentPathItemColor = Color.MAROON;
	private final String kClass = "class";
	private final String kComponentSelected = "component-selected";
	Element lastElementHighlighted = null;
	String textLabel = "text";

	Element elementSelected = null;
	private final List<String> elementsToIgnore = List.of(
			"body",
			"details",
			"div",
			"html",
			"input",
			"span",
			"summary"
			);

	public Element getElementSelected() {
		return elementSelected;
	}

	public Color getComponentPathItemColor() {
		return kComponentPathItemColor;
	}

	public void updateComponentPathBar(Element element, TextFlow componentPathBar, String textLabel) {
		Platform.runLater(() -> {
			this.textLabel = textLabel;
			removeHighlightFromLastElementHighlighted();
			componentPathBar.getChildren().clear();
			addElementToComponentPathBar(element, componentPathBar);
			markLastElement(element, componentPathBar);
		});
	}

	// public for testing
	public void addElementToComponentPathBar(Element element, TextFlow componentPathBar) {
		String tagName = element.getTagName();
		if (tagName.equals("BODY")) {
			// no need to look further up
			return;
		}
		if (!(element.getParentNode() instanceof Element)) {
			return;
		}
		addElementToComponentPathBar((Element) element.getParentNode(), componentPathBar);
		String adjustedTagName = XmlNameMapper.getMappedElementName(tagName);
		if (elementsToIgnore.contains(adjustedTagName)) {
			return;
		}
		if (tagName.equals("th") || tagName.equals("td")) {
			Text tTr = new Text(" tr");
			tTr.setFill(kComponentPathItemColor);
			Text tTrGap = new Text(kComponentGap);
			tTr.setUserData(element.getParentNode());
			componentPathBar.getChildren().addAll(tTr, tTrGap);
		}
		Text t = new Text(" " + adjustedTagName.replace(Constants.ELEMENT_RENAME_PREFIX, ""));
		t.setUserData(element);
		t.setFill(kComponentPathItemColor);
		componentPathBar.getChildren().add(t);
		addComponentGap(componentPathBar);
	}

	private void addComponentGap(TextFlow componentPathBar) {
		Text tGap = new Text(kComponentGap);
		tGap.setUserData("gap");
		componentPathBar.getChildren().add(tGap);
	}

	// public for testing
	public void markLastElement(Element element, TextFlow componentPathBar) {
		ObservableList<Node> children = componentPathBar.getChildren();
		int lastChild = children.size();
		if (lastChild > 1) {
			Node lastNode = children.get(lastChild - 2);
			if (lastNode instanceof Text lastText) {
				lastText.setStyle(kStyleOfFinal);
				children.removeLast();
				children.removeLast();
				children.add(lastText);
				elementSelected = element;
				if (hasTextNode(element)) {
					addComponentGap(componentPathBar);
					Text t = new Text(" " + textLabel);
					t.setStyle(kStyleOfText);
					t.setUserData(element);
					componentPathBar.getChildren().add(t);
				}
			}
		}
	}

	public void highlightSelectedElement(Element element) {
		Platform.runLater(() -> {
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
			if (cssClass == null) {
				cssClass = "";
			}
			cssClass = cssClass.replaceAll(kComponentSelected, "");
			lastElementHighlighted.setAttribute(kClass, cssClass);
		}
	}

	private boolean hasTextNode(Element element) {
		if (!element.getTagName().equals("SPAN")
				&& elementsToIgnore.contains(XmlNameMapper.getMappedElementName(element.getTagName()))) {
			return false;
		}
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			if (element.getChildNodes().item(i).getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
				return true;
			}
		}
		return false;
	}
}
