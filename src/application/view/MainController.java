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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
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

					System.out.println("\n--- Elements at click layer hierarchy ---");

					// 3. Iterate through the array slots from topmost to bottommost
					for (int i = 0; i < length; i++) {
						Object arrayItem = elementList.getSlot(i);

						// Each item inside the slot implements the standard org.w3c.dom.Element
						// interface!
						if (arrayItem instanceof Element) {
							Element domElement = (Element) arrayItem;

							String tagName = domElement.getTagName();
							if (tagName.equals("BODY") || tagName.equals("HTML")) {
								continue;
							}
							String elementId = domElement.getAttribute("id");

							System.out.printf("[%d] Tag: %s | ID: %s%n", i, InternalToExternalNameMapper.mapName(tagName),
									elementId != null && !elementId.isEmpty() ? elementId : "");

//		                    // Example Usage:
//		                    // If you are looking for the first specific DTD container block
//		                    // beneath a transient selection highlight or inline node:
//		                    if (tagName.equalsIgnoreCase("paragraph-node") || tagName.equalsIgnoreCase("frontMatter")) {
//		                        System.out.println("   -> Target XML element captured: " + tagName);
//		                        // Do your work with domElement here...
//		                    }
						}
					}
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
