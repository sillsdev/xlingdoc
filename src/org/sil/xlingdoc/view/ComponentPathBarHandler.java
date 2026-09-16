/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.view;

import java.util.ArrayList;
import java.util.List;

import org.sil.xlingdoc.model.ComponentPathItem;
import org.sil.xlingdoc.service.dtdhandling.XmlNameMapper;
import org.w3c.dom.Element;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

/**
 * 
 */
public class ComponentPathBarHandler {
//	private final String kClass = "class";
	List<ComponentPathItem> componentsInPathBar = new ArrayList<ComponentPathItem>();
	final String kComponentGap = " " + Character.toString(0x227a);
	final String kComponentBreak = " > ";
	final Color kComponentPathItemColor = Color.MAROON;
	Element elementSelected = null;
	private final List<String> elementsToIgnore = List.of(
			"BODY",
			"DETAILS",
			"HTML",
//			"INPUT",
			"SUMMARY"
			);

	public Element getElementSelected() {
		return elementSelected;
	}

	public Color getComponentPathItemColor() {
		return kComponentPathItemColor;
	}

	public List<ComponentPathItem> getComponentsInPathBar() {
		return componentsInPathBar;
	}

	public void updateComponentPathBar(WebEngine webEngine, TextFlow componentPathBar, MouseEvent event) {
		Platform.runLater(() -> {
			// Always use coordinate locations relative strictly to the WebView viewport
			// boundaries
			double x = event.getX();
			double y = event.getY();
			// 1. Execute the plural elementsFromPoint script
			String script = String.format("document.elementsFromPoint(%f, %f);", x, y);
			Object result = webEngine.executeScript(script);
			// 2. The browser returns an array-like collection wrapped as a JSObject
			if (result instanceof JSObject) {
				JSObject elementList = (JSObject) result;
				// Evaluate the length of the array returned by WebKit
				Object lengthObj = elementList.getMember("length");
				if (lengthObj instanceof Number) {
					int length = ((Number) lengthObj).intValue();
					componentPathBar.getChildren().clear();
					componentsInPathBar.clear();
					// 3. Iterate through the array slots from topmost to bottommost
					for (int i = length-1; i >= 0; i--) {
						Object arrayItem = elementList.getSlot(i);
						if (arrayItem instanceof Element) {
							Element domElement = (Element) arrayItem;
							String tagName = domElement.getTagName();
							if (elementsToIgnore.contains(tagName)) {
								continue;
							}
							if (tagName.equals("INPUT")) {
								ObservableList<Node> children = componentPathBar.getChildren();
								int lastChild = children.size();
								Node lastNode = children.get(lastChild - 2);
								if (lastNode instanceof Text lastText) {
									lastText.setStyle("-fx-font-weight: bold;");
									children.removeLast();
									children.removeLast();
									children.add(lastText);
									elementSelected = domElement;
								}
								return;
							}
							if (tagName.equals("TH") || tagName.equals("TD")) {
								Text tTr = new Text(" tr");
								tTr.setFill(kComponentPathItemColor);
								Text tTrGap = new Text(kComponentGap);
								ComponentPathItem trItem = new ComponentPathItem("tr", (Element)domElement.getParentNode());
								tTr.setUserData(trItem);
								componentPathBar.getChildren().addAll(tTr, tTrGap);
								componentsInPathBar.add(trItem);
							}
							String adjustedTagName = XmlNameMapper.getMappedElementName(tagName);
							Text t = new Text(" " + adjustedTagName);
							t.setFill(kComponentPathItemColor);
							if (i == 0) {
								t.setStyle("-fx-font-weight: bold;");
							}
							componentPathBar.getChildren().add(t);
							ComponentPathItem cpItem = new ComponentPathItem(adjustedTagName, domElement);
							componentsInPathBar.add(cpItem);
							t.setUserData(cpItem);
							if (i > 0) {
								Text tGap = new Text(kComponentGap);
								tGap.setUserData("gap");
								componentPathBar.getChildren().add(tGap);
							} else {
								System.out.println("Clicked on this element via handler: '" + adjustedTagName + "'");
								elementSelected = domElement;
							}
						}
					}
				}
			}
		});
	}
}
