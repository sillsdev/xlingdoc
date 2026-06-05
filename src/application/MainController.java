/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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
