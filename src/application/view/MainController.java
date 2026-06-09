/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package application.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.w3c.dom.Element;

import application.service.InternalToExternalNameMapper;
import application.service.XmlNameMapper;
import application.service.XmlSerializer;
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

	public MainController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		webEngine = webView.getEngine();
		String filePath = "data/XLingPaper.css";
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
		webEngine.loadContent(loadFileIntoNeededHTML());

		webView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			updateComponentPathBar(event);
		});

		componentPathBar.setOnMouseClicked(event -> {
			if (event.getTarget() instanceof Text) {
				Text clickedText = (Text) event.getTarget();
				System.out.println("\nClicked text: " + clickedText.getText());
				Object obj = clickedText.getUserData();
				if (obj instanceof Element el) {
					System.out.println("el = '" + el.getNodeName());
				}
				// Perform action with clickedText
			}
		});
		Text top = new Text(" lingPaper");
		top.setFill(kComponentPathItemColor);
		componentPathBar.getChildren().add(top);
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
					System.out.println("length = " + length);
					StringBuilder sb = new StringBuilder();
					sb.append(" ");
					componentPathBar.getChildren().clear();
					componentPathBar.requestLayout();
					// 3. Iterate through the array slots from topmost to bottommost
					for (int i = length-1; i >= 0; i--) {
						Object arrayItem = elementList.getSlot(i);
						// Each item inside the slot implements the standard org.w3c.dom.Element
						// interface!
						if (arrayItem instanceof Element) {
							Element domElement = (Element) arrayItem;
							String tagName = domElement.getTagName();
							if (tagName.equals("BODY") || tagName.equals("HTML")) {
								continue;
							}
							if (tagName.equals("TH") | tagName.equals("TD")) {
								sb.append("tr > ");
								Text tTr = new Text(" tr");
								tTr.setFill(kComponentPathItemColor);
								Text tTrGap = new Text(kComponentGap);
								tTr.setUserData(domElement.getParentNode());
								componentPathBar.getChildren().addAll(tTr, tTrGap);
							}
							sb.append(InternalToExternalNameMapper.mapName(tagName));
							Text t = new Text(" " + InternalToExternalNameMapper.mapName(tagName));
							t.setFill(kComponentPathItemColor);
							t.setUserData(domElement);
							if (i == 0) {
								t.setStyle("-fx-font-weight: bold;");
							}
							componentPathBar.getChildren().add(t);
							if (i > 0) {
								Text tGap = new Text(kComponentGap);
								tGap.setUserData("gap");
								componentPathBar.getChildren().add(tGap);
								sb.append(" > ");
							}
							componentPathBar.requestLayout();
						}
					}
					rootLayout.requestLayout();
					System.out.println(sb.toString());
				}
			}
		});
	}

	String loadFileIntoNeededHTML() {
		StringBuilder sb= new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<!DOCTYPE html>\n");
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("</head>\n");
		sb.append("<body contenteditable=\"true\">\n");
		String filePath = "data/SamplePaper.xml";
		String fileContent = "";
		File f = new File(filePath);
		if (!f.exists()) {
			System.out.println(filePath + " not found");
		} else {
			try {
				fileContent = Files.readString(Paths.get(filePath));
				int iBegin = fileContent.indexOf("<lingPaper");
				fileContent = fileContent.substring(iBegin);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fileContent = XmlNameMapper.mapInputFromXLingPaperToHTML(fileContent);
		sb.append(fileContent);
		sb.append("</body>\n");
		sb.append("</html>\n");
		return sb.toString();
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
