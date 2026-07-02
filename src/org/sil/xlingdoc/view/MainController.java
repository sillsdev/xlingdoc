/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedSet;

import org.sil.xlingdoc.Constants;
import org.sil.xlingdoc.model.ComponentPathItem;
import org.sil.xlingdoc.service.DtdInspector;
import org.sil.xlingdoc.service.XLingDocLoader;
import org.sil.xlingdoc.service.XmlDocumentManager;
import org.sil.xlingdoc.service.XmlNameMapper;
import org.sil.xlingdoc.service.XmlSerializer;
import org.w3c.dom.Element;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/**
 * 
 */
public class MainController implements Initializable {
	private WebEngine webEngine;
	@FXML
	private WebView webView;
	@FXML
	BorderPane rootLayout;
	@FXML
	private Button btnSave;
	@FXML
	private TextFlow componentPathBar;
	private final String kComponentGap = " " + Character.toString(0x227a);
	private final Color kComponentPathItemColor = Color.MAROON;
	private final String kClass = "class";
	private final String kComponentSelected = "component-selected";
	List<ComponentPathItem> componentsInPathBar = new ArrayList<ComponentPathItem>();
	private DtdInspector inspector;
	private XmlDocumentManager manager;

	public MainController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		webEngine = webView.getEngine();
		String filePath = Constants.CSS_LOCATION;
		File f = new File(filePath);
		if (f.exists()) {
			try {
				String cssUrl = f.toURI().toURL().toExternalForm();
				webEngine.setUserStyleSheetLocation(cssUrl);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println(filePath + " not found");
		}
		manager = new XmlDocumentManager();
		inspector = new DtdInspector(Constants.DTD_LOCATION, resources.getString("element.text"));
//		String xmlFilePath = "data/SamplePaper.xml";
		String xmlFilePath = Constants.UNIT_TEST_DATA_FILE;
		String htmlContent = XLingDocLoader.loadFileIntoNeededHTML(manager, inspector, xmlFilePath);
		webEngine.loadContent(htmlContent);


		webView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			updateComponentPathBar(event);
		});

		componentPathBar.setOnMouseClicked(event -> {
			if (event.getTarget() instanceof Text) {
				Text clickedText = (Text) event.getTarget();
				System.out.println("\nClicked text: " + clickedText.getText());
				Object obj = clickedText.getUserData();
				if (obj instanceof ComponentPathItem cpItem) {
					System.out.println("cpItem = '" + cpItem.getName());
					highlightDomElement(cpItem);
				}
			}
		});
		Text top = new Text(" lingPaper");
		top.setFill(kComponentPathItemColor);
		componentPathBar.getChildren().add(top);

//		webView.setOnContextMenuRequested(null);
	}

	protected void updateComponentPathBar(MouseEvent event) {
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
					StringBuilder sb = new StringBuilder();
					sb.append(" ");
					componentPathBar.getChildren().clear();
					componentsInPathBar.clear();
					// 3. Iterate through the array slots from topmost to bottommost
					for (int i = length-1; i >= 0; i--) {
						Object arrayItem = elementList.getSlot(i);
						// Each item inside the slot implements the standard org.w3c.dom.Element
						// interface!
						if (arrayItem instanceof Element) {
							Element domElement = (Element) arrayItem;
							String tagName = domElement.getTagName();
							if (tagName.equals("BODY") || tagName.equals("HTML")
									|| tagName.equals("DETAILS") || tagName.equals("SUMMARY")) {
								continue;
							}
							if (tagName.equals("TH") || tagName.equals("TD")) {
								sb.append("tr > ");
								Text tTr = new Text(" tr");
								tTr.setFill(kComponentPathItemColor);
								Text tTrGap = new Text(kComponentGap);
								ComponentPathItem trItem = new ComponentPathItem("tr", (Element)domElement.getParentNode());
								tTr.setUserData(trItem);
								componentPathBar.getChildren().addAll(tTr, tTrGap);
								componentsInPathBar.add(trItem);
							}
							String adjustedTagName = XmlNameMapper.getMappedElementName(tagName);
									//InternalToExternalNameMapper.mapName(tagName);
							sb.append(adjustedTagName);
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
								sb.append(" > ");
							} else {
								System.out.println("Clicked on this element: '" + adjustedTagName + "'");
								SortedSet<String> before = inspector.getValidAdjacentElements(domElement, manager, true);
								SortedSet<String> after = inspector.getValidAdjacentElements(domElement, manager, false);
							}
						}
					}
//					System.out.println(sb.toString());
				}
			}
		});
	}

	public void highlightDomElement(ComponentPathItem cpItem) {
		Element targetElement = cpItem.getElement();
		if (componentsInPathBar.contains(cpItem)) {
			int index = componentsInPathBar.lastIndexOf(cpItem);
			System.out.println("\tindex = " + index);
			for (int i = index + 1; i < componentsInPathBar.size(); i++) {
				Element el = componentsInPathBar.get(i).getElement();
				String cssClass = el.getAttribute(kClass);
				System.out.println("\tel = " + el.getTagName() + "; class='" + cssClass + "'");
				if (cssClass != null && cssClass.length() > 0) {
					cssClass = cssClass.replaceAll(kComponentSelected, "");
					el.setAttribute(kClass, cssClass);
				}
			}
		}
		// TODO: what if there are more CSS names in the class attribute?
		targetElement.setAttribute(kClass, kComponentSelected);
	}

	@FXML
	private void handleSave() {
		File f = new File("data/SamplePaperSaved.xml");
		try {
			XmlSerializer.exportWebViewToXml(webEngine, f, "XLingPap.dtd");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
