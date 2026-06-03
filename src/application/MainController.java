/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package application;

import java.net.URL;
import java.util.ResourceBundle;

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
	private Button btnBottom;

	public MainController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		webEngine = webView.getEngine();
		webEngine.loadContent("<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n" + "    <style>\r\n"
				+ "        /* Force XML elements to look like block-level paragraphs */\r\n"
				+ "        paragraph-node { display: block; margin: 10px 0; min-height: 1.2em; border-left: 2px solid #ccc; padding-left: 5px; }\r\n"
				+ "        bold-node { font-weight: bold; display: inline; }\r\n" + "    </style>\r\n" + "</head>\r\n"
				+ "<body contenteditable=\"true\">\r\n"
				+ "    <paragraph-node id=\"node_1\">This is an example paragraph node matching your DTD.</paragraph-node>\r\n"
				+ "    <paragraph-node id=\"node_2\">You can key text content right into here.</paragraph-node>\r\n"
				+ "</body>\r\n" + "</html>\r\n" + "");

	}

}
