/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.sil.xlingdoc.Constants;
import org.sil.xlingdoc.service.fileio.XLingDocLoader;
import org.sil.xlingdoc.service.fileio.XLingDocSaver;
import org.sil.xlingdoc.service.WebPageInteractor;
import org.sil.xlingdoc.service.WebPageUtilities;
import org.sil.xlingdoc.service.dtdhandling.DtdInspector;
import org.sil.xlingdoc.service.dtdhandling.XmlDocumentManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
	@FXML
	private TextFlow componentPathBar;
	private DtdInspector dtdInspector;
	private XmlDocumentManager manager;
	private WebPageInteractor webPageInteractor;
	private ComponentPathBarHandler componentPathBarHandler;

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
		dtdInspector = new DtdInspector(Constants.DTD_LOCATION, resources.getString("element.text"));
		webPageInteractor = new WebPageInteractor();
		componentPathBarHandler = new ComponentPathBarHandler();
		String xmlFilePath = Constants.UNIT_TEST_DATA_FILE;
//		String xmlFilePath = Constants.UNIT_TEST_XINCLUDE_DATA_FILE;
		String htmlContent = XLingDocLoader.loadFileIntoNeededHTML(manager, dtdInspector, xmlFilePath);

		webEngine.loadContent(htmlContent);
		webEngine.getLoadWorker().stateProperty().addListener((_, _, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				webEngine = WebPageUtilities.allowConsoleLogViaJavaScript(webEngine, webPageInteractor);
				Document doc = webEngine.getDocument();
//				doc = WebPageUtilities.removeIncorrectEmbedding(doc);
				webPageInteractor.setDocument(doc);
//				WebPageUtilities.addInputBoxes(webEngine);
				Element target = doc.getDocumentElement();
				if (target != null) {
					((EventTarget) target).addEventListener("click", (org.w3c.dom.events.Event ev) -> {
						Element clicked = (Element) ev.getTarget();
						System.out.println("New Clicked: " + clicked.getTagName());
						componentPathBarHandler.updateComponentPathBar(clicked, componentPathBar);
					}, false);
				}
			}
		});

		//		TODO: is this needed in any way now??
//		webView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
//			if (event.getButton() == MouseButton.PRIMARY) {
//				componentPathBarHandler.updateComponentPathBar(webEngine, componentPathBar, event);
//				componentsInPathBar = componentPathBarHandler.getComponentsInPathBar();
//				Element selectedElement = componentPathBarHandler.getElementSelected();
//				if (selectedElement != null) {
//					SortedSet<String> before = dtdInspector.getValidAdjacentElements(selectedElement, manager, true);
//					SortedSet<String> after = dtdInspector.getValidAdjacentElements(selectedElement, manager, false);
//					if (before.size() == -1 || after.size() == -1)
//						System.out.println("-1 found");
//				}
//			}
			// Potential code for checking spelling of a word which has been right-clicked on
			//else if (event.getButton() == MouseButton.SECONDARY) {

//		        double x = event.getX();
//		        double y = event.getY();
//
//		        // 1. Get the word clicked inside WebEngine
//		        String script = String.format(
//		            "var range = document.caretRangeFromPoint(%f, %f); " +
//		            "if (range) { " +
//		            "   range.expand('word'); " +
//		            "   range.toString().trim(); " +
//		            "} else { ''; }", x, y
//		        );
//		        String wordAtClick = (String) webEngine.executeScript(script);
//		        System.out.println("word = '" + wordAtClick + "'");
//
////		        if (wordAtClick != null && !wordAtClick.isEmpty() && isMisspelled(wordAtClick)) {
////		            event.consume(); // Suppress the default browser context menu
////
////		            // 2. Generate suggestions
////		            List<String> suggestions = getSpellingSuggestions(wordAtClick);
////		            ContextMenu spellMenu = new ContextMenu();
////
////		            // 3. Build context menu items
////		            for (String suggestion : suggestions) {
////		                MenuItem item = new MenuItem(suggestion);
////		                item.setOnAction(e -> {
////		                    // Replace the word in the DOM using JavaFX to JS call
////		                    webEngine.executeScript(String.format(
////		                        "var range = document.caretRangeFromPoint(%f, %f); " +
////		                        "if (range) { " +
////		                        "   range.expand('word'); " +
////		                        "   range.deleteContents(); " +
////		                        "   range.insertNode(document.createTextNode('%s')); " +
////		                        "}", x, y, suggestion
////		                    ));
////		                });
////		                spellMenu.getItems().add(item);
////		            }
////
////		            // Show the context menu at the screen mouse position
////		            spellMenu.show(webView, event.getScreenX(), event.getScreenY());
////		        }
//		    }
//		});

		componentPathBar.setOnMouseClicked(event -> {
			if (event.getTarget() instanceof Text) {
				Text clickedText = (Text) event.getTarget();
				System.out.println("\nClicked text: " + clickedText.getText());
				Object obj = clickedText.getUserData();
				if (obj instanceof Element el) {
					System.out.println("el = '" + el.getTagName());
					componentPathBarHandler.highlightSelectedElement(el);
//					highlightDomElement(cpItem);
				}
			}
		});
		// TODO: use correct top item name (xlingpaper or lingPaper)
		Text top = new Text(" lingPaper");
		top.setFill(componentPathBarHandler.getComponentPathItemColor());
		componentPathBar.getChildren().add(top);

//		webView.setOnContextMenuRequested(null);
	}

	@FXML
	private void handleSave() {
		File f = new File("data/SamplePaperSaved.xml");
		try {
			XLingDocSaver.saveXLingDoc(webEngine.getDocument(), f);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
