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

import org.sil.utility.view.ControllerUtilities;
import org.sil.xlingdoc.Constants;
import org.sil.xlingdoc.Main;
import org.sil.xlingdoc.service.fileio.XLingDocLoader;
import org.sil.xlingdoc.service.fileio.XLingDocSaver;
import org.sil.xlingdoc.service.WebPageInteractor;
import org.sil.xlingdoc.service.WebPageUtilities;
import org.sil.xlingdoc.service.XLingDocXmlToInternalHtmlMapper;
import org.sil.xlingdoc.service.dtdhandling.ComponentToolOperationType;
import org.sil.xlingdoc.service.dtdhandling.DtdInspector;
import org.sil.xlingdoc.service.dtdhandling.XmlDocumentManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
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
	private ComponentToolOperationType componentToolOperation = ComponentToolOperationType.Insert;
	private ResourceBundle bundle;
	private Element elementClickedOn;
	private boolean escPressed;
	Clipboard systemClipboard = Clipboard.getSystemClipboard();
	Main main;
	KeyCombination ctrlI = new KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN);

	@FXML
	private MenuItem menuItemEditCopy;
	@FXML
	private MenuItem menuItemEditCut;
	@FXML
	private MenuItem menuItemEditPaste;
	@FXML
	private Button buttonComponentToolCancel;
	@FXML
	private Button buttonComponentToolConvert;
	@FXML
	private Button buttonComponentToolConvertWrap;
	@FXML
	private Button buttonComponentToolInsert;
	@FXML
	private Button buttonComponentToolInsertAfter;
	@FXML
	private Button buttonComponentToolInsertBefore;
	@FXML
	private Button buttonComponentToolOK;
	@FXML
	private Button buttonComponentToolReplace;
	@FXML
	private Button buttonToolbarEditCopy;
	@FXML
	private Button buttonToolbarEditCut;
	@FXML
	private Button buttonToolbarEditInsert;
	@FXML
	private Button buttonToolbarEditPaste;
	@FXML
	private Button buttonToolbarEditDelete;
	@FXML
	private Button buttonToolbarElementFind;
	@FXML
	private Button buttonToolbarFileNew;
	@FXML
	private Button buttonToolbarFileOpen;
	@FXML
	private Button buttonToolbarFileSave;
	@FXML
	private Button buttonToolbarProducePdf;
	@FXML
	private Button buttonToolbarProduceWebPage;
	@FXML
	private Button buttonToolbarTextFind;
	@FXML
	private Tooltip tooltipToolbarEditCopy;
	@FXML
	private Tooltip tooltipToolbarEditCut;
	@FXML
	private Tooltip tooltipToolbarEditInsert;
	@FXML
	private Tooltip tooltipToolbarEditPaste;
	@FXML
	private Tooltip tooltipToolbarEditDelete;
	@FXML
	private Tooltip tooltipToolbarElementFind;
	@FXML
	private Tooltip tooltipToolbarFileNew;
	@FXML
	private Tooltip tooltipToolbarFileOpen;
	@FXML
	private Tooltip tooltipToolbarFileSave;
	@FXML
	private Tooltip tooltipToolbarProducePdf;
	@FXML
	private Tooltip tooltipToolbarProduceWebPage;
	@FXML
	private Tooltip tooltipToolbarTextFind;
	@FXML
	private TextField textFieldComponentTool;
	@FXML
	private Tooltip tooltipComponentToolCancel;
	@FXML
	private Tooltip tooltipComponentToolConvert;
	@FXML
	private Tooltip tooltipComponentToolConvertWrap;
	@FXML
	private Tooltip tooltipComponentToolInsert;
	@FXML
	private Tooltip tooltipComponentToolInsertAfter;
	@FXML
	private Tooltip tooltipComponentToolInsertBefore;
	@FXML
	private Tooltip tooltipComponentToolOK;
	@FXML
	private Tooltip tooltipComponentToolReplace;

	public MainController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
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
		dtdInspector = new DtdInspector(Constants.DTD_LOCATION, "(text)");
		webPageInteractor = new WebPageInteractor();
		componentPathBarHandler = new ComponentPathBarHandler();
		String xmlFilePath = Constants.UNIT_TEST_DATA_FILE;
//		String xmlFilePath = Constants.UNIT_TEST_XINCLUDE_DATA_FILE;
		XLingDocXmlToInternalHtmlMapper.resetElementInputBoxAtrributeMap(webEngine);
		String htmlContent = XLingDocLoader.loadFileIntoNeededHTML(manager, dtdInspector, xmlFilePath, bundle);

		webEngine.loadContent(htmlContent);
		webEngine.getLoadWorker().stateProperty().addListener((_, _, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				webEngine = WebPageUtilities.allowConsoleLogViaJavaScript(webEngine, webPageInteractor);
				Document doc = webEngine.getDocument();
				webPageInteractor.setDocument(doc);
				Element target = doc.getDocumentElement();
				if (target != null) {
					((EventTarget) target).addEventListener("click", (org.w3c.dom.events.Event ev) -> {
						// this finds the w3c DOM element that was clicked on;
						// we get the X,Y coordinate from the webView via its setOnMouseClicked() method below
						elementClickedOn = (Element) ev.getTarget();
//						System.out.println("New Clicked: " + elementClickedOn.getTagName());
						componentPathBarHandler.updateComponentPathBar(elementClickedOn, componentPathBar, bundle.getString("label.text"));
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

	    webView.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
	        if (event.getCode() == KeyCode.ESCAPE) {
	            escPressed = true;
	            event.consume();
	        } else if (escPressed && event.getCode() == KeyCode.DOWN) {
	            handleSelectAllChildren();
	            event.consume();
	        }
	        if (event.getCode() != KeyCode.ESCAPE && event.getCode() != KeyCode.DOWN) {
	            escPressed = false;
	        }
	        if (ctrlI.match(event)) {
	        	handleInsert();
	        }
	    });

	    textFieldComponentTool.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
	        if (event.getCode() == KeyCode.ESCAPE) {
	            escPressed = true;
	            event.consume();
	        } else if (escPressed && event.getCode() == KeyCode.DOWN) {
	            handleSelectAllChildren();
	            event.consume();
	        }
	        if (event.getCode() != KeyCode.ESCAPE && event.getCode() != KeyCode.DOWN) {
	            escPressed = false;
	        }
	    });

		componentPathBar.setOnMouseClicked(event -> {
			if (event.getTarget() instanceof Text) {
				Text clickedText = (Text) event.getTarget();
				Object obj = clickedText.getUserData();
				if (obj instanceof Element el) {
					componentPathBarHandler.highlightSelectedElement(el);
				}
			}
		});
		// TODO: use correct top item name (xlingpaper or lingPaper)
		Text top = new Text(" lingPaper");
		top.setFill(componentPathBarHandler.getComponentPathItemColor());
		componentPathBar.getChildren().add(top);

		createToolbarButtons(bundle);
//		webView.setOnContextMenuRequested(null);
	}

	protected void createToolbarButtons(ResourceBundle bundle) {
		tooltipToolbarFileNew = ControllerUtilities.createToolbarButtonWithImage("newAction.png",
				buttonToolbarFileNew, tooltipToolbarFileNew, bundle.getString("tooltip.new"),
				Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipToolbarFileOpen = ControllerUtilities.createToolbarButtonWithImage("openAction.png",
				buttonToolbarFileOpen, tooltipToolbarFileOpen, bundle.getString("tooltip.open"),
				Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipToolbarFileSave = ControllerUtilities.createToolbarButtonWithImage("saveAction.png",
				buttonToolbarFileSave, tooltipToolbarFileSave, bundle.getString("tooltip.save"),
				Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipToolbarEditCut = ControllerUtilities.createToolbarButtonWithImage("cutAction.png",
				buttonToolbarEditCut, tooltipToolbarEditCut, bundle.getString("tooltip.cut"),
				Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipToolbarEditCopy = ControllerUtilities.createToolbarButtonWithImage("copyAction.png",
				buttonToolbarEditCopy, tooltipToolbarEditCopy, bundle.getString("tooltip.copy"),
				Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipToolbarEditPaste = ControllerUtilities.createToolbarButtonWithImage(
				"pasteAction.png", buttonToolbarEditPaste, tooltipToolbarEditPaste,
				bundle.getString("tooltip.paste"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipToolbarEditInsert = ControllerUtilities.createToolbarButtonWithImage(
				"insertAction.png", buttonToolbarEditInsert, tooltipToolbarEditInsert,
				bundle.getString("tooltip.insert"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipToolbarEditDelete = ControllerUtilities.createToolbarButtonWithImage(
				"deleteAction.png", buttonToolbarEditDelete, tooltipToolbarEditDelete,
				bundle.getString("tooltip.delete"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipToolbarProduceWebPage = ControllerUtilities.createToolbarButtonWithImage(
				"ProduceWebPage.png", buttonToolbarProduceWebPage, tooltipToolbarProduceWebPage,
				bundle.getString("tooltip.producewebpage"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipToolbarProducePdf = ControllerUtilities.createToolbarButtonWithImage(
				"ProducePDF.png", buttonToolbarProducePdf, tooltipToolbarProducePdf,
				bundle.getString("tooltip.producepdf"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipToolbarTextFind = ControllerUtilities.createToolbarButtonWithImage(
				"TextFind.png", buttonToolbarElementFind, tooltipToolbarTextFind,
				bundle.getString("tooltip.elementfind"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipToolbarElementFind = ControllerUtilities.createToolbarButtonWithImage(
				"ElementFind.png", buttonToolbarTextFind, tooltipToolbarElementFind,
				bundle.getString("tooltip.textfind"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);

		tooltipComponentToolCancel = ControllerUtilities.createToolbarButtonWithImage(
				"cancel.png", buttonComponentToolCancel, tooltipComponentToolCancel,
				bundle.getString("tooltip.cancel"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipComponentToolConvert = ControllerUtilities.createToolbarButtonWithImage(
				"Convert.png", buttonComponentToolConvert, tooltipComponentToolConvert,
				bundle.getString("tooltip.convert"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipComponentToolConvertWrap = ControllerUtilities.createToolbarButtonWithImage(
				"ConvertWrap.png", buttonComponentToolConvertWrap, tooltipComponentToolConvertWrap,
				bundle.getString("tooltip.convertwrap"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipComponentToolInsert = ControllerUtilities.createToolbarButtonWithImage(
				"insert_into.png", buttonComponentToolInsert, tooltipComponentToolInsert,
				bundle.getString("tooltip.insert"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipComponentToolInsertAfter = ControllerUtilities.createToolbarButtonWithImage(
				"insert_after.png", buttonComponentToolInsertAfter, tooltipComponentToolInsertAfter,
				bundle.getString("tooltip.insertafter"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipComponentToolInsertBefore = ControllerUtilities.createToolbarButtonWithImage(
				"insert_before.png", buttonComponentToolInsertBefore, tooltipComponentToolInsertBefore,
				bundle.getString("tooltip.insertbefore"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipComponentToolOK = ControllerUtilities.createToolbarButtonWithImage(
				"Yes.png", buttonComponentToolOK, tooltipComponentToolOK,
				bundle.getString("tooltip.ok"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		tooltipComponentToolReplace = ControllerUtilities.createToolbarButtonWithImage(
				"replace.png", buttonComponentToolReplace, tooltipComponentToolReplace,
				bundle.getString("tooltip.replace"), Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		buttonComponentToolConvert.setStyle(Constants.COMPONENT_TOOL_BUTTON_TEXT_SIZE);
		buttonComponentToolConvertWrap.setStyle(Constants.COMPONENT_TOOL_BUTTON_TEXT_SIZE);
		buttonComponentToolInsert.setStyle(Constants.COMPONENT_TOOL_BUTTON_TEXT_SIZE);
		buttonComponentToolInsertAfter.setStyle(Constants.COMPONENT_TOOL_BUTTON_TEXT_SIZE);
		buttonComponentToolInsertBefore.setStyle(Constants.COMPONENT_TOOL_BUTTON_TEXT_SIZE);
		buttonComponentToolReplace.setStyle(Constants.COMPONENT_TOOL_BUTTON_TEXT_SIZE);
	}

	public Main getMain() {
		return main;
	}

	public void setMain(Main main) {
		this.main = main;
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

	@FXML
	private void handleAbout() {
		System.out.println("handleAbout");
	}

	@FXML
	private void handleAssociateStyleSheet() {
		System.out.println("handleAssociateStyleSheet");
	}

	@FXML
	private void handleChangeInterfaceLanguage() {
		System.out.println("handleChangeInterfaceLanguage");
	}

	@FXML
	private void handleComponentToolCancel() {
		System.out.println("handleComponentToolCancel");
	}

	@FXML
	private void handleComponentToolOK() {
		System.out.println("handleComponentToolOK");
	}

	@FXML
	private void handleConvert() {
		System.out.println("handleConvert");
	}

	@FXML
	private void handleConvertAbbrToAbbrRef() {
		System.out.println("handleConvertAbbrToAbbrRef");
	}

	@FXML
	private void handleConvertLineToWrd() {
		System.out.println("handleConvertLineToWrd");
	}

	@FXML
	private void handleConvertSvgToPdf() {
		System.out.println("handleConvertSvgToPdf");
	}

	@FXML
	private void handleConvertToBracketedConstituent() {
		System.out.println("handleConvertToBracketedConstituent");
	}

	@FXML
	private void handleConvertToEndnote() {
		System.out.println("handleConvertToEndnote");
	}

	@FXML
	private void handleConvertToGloss() {
		System.out.println("handleConvertToGloss");
	}

	@FXML
	private void handleConvertToLangData() {
		System.out.println("handleConvertToLangData");
	}

	@FXML
	private void handleConvertToObject() {
		System.out.println("handleConvertToObject");
	}

	@FXML
	private void handleConvertWrap() {
		System.out.println("handleConvertWrap");
	}

	@FXML
	private void handleCopy() {
		System.out.println("handleCopy");
	}

	@FXML
	private void handleCut() {
		System.out.println("handleCut");
	}

	@FXML
	private void handleDelete() {
		System.out.println("handleDelete");
	}

	@FXML
	private void handleDemoteSection() {
		System.out.println("handleDemoteSection");
	}

	@FXML
	private void handleDemoteSelection() {
		System.out.println("handleDemoteSelection");
	}

	@FXML
	private void handleElementFind() {
		System.out.println("handleElementFind");
	}

	@FXML
	private void handleElementReplace() {
		System.out.println("handleElementReplace");
	}

	@FXML
	private void handleExit() {
		System.out.println("handleExit");
	}

	@FXML
	private void handleExtendSelectionToFollowingSibling() {
		System.out.println("handleExtendSelectionToFollowingSibling");
	}

	@FXML
	private void handleExtendSelectionToPrecedingSibling() {
		System.out.println("handleExtendSelectionToPrecedingSibling");
	}

	@FXML
	private void handleInsert() {
		System.out.println("handleInsert");
		if (elementClickedOn == null) {
			return;
		}
		System.out.println("\telement clicked on = " + elementClickedOn);
		Element element = elementClickedOn;
		if (elementClickedOn.getTagName().equals("SPAN") || elementClickedOn.getTagName().equals("DIV")) {
			element = (Element) element.getParentNode();
			System.out.println("\t\tusing parent: " + element.getTagName());
		}
		String nameToUse = XLingDocXmlToInternalHtmlMapper.getRenamedElement(element.getTagName());
		element = manager.getMasterXmlDoc().createElement(nameToUse);
		System.out.println("\telement passed in = " + element);
		InsertHandler.askForElementToInsert(webEngine.getDocument(), element, manager, dtdInspector, main, bundle);
	}

	@FXML
	private void handleInsertAfter() {
		System.out.println("handleInsertAfter");
	}

	@FXML
	private void handleInsertBefore() {
		System.out.println("handleInsertBefore");
	}

	@FXML
	private void handleInsertSetReference() {
		System.out.println("handleInsertSetReference");
	}

	@FXML
	private void handleInsertSetReferenceRange() {
		System.out.println("handleInsertSetReferenceRange");
	}

	@FXML
	private void handleNewDocument() {
		System.out.println("handleNewDocument");
	}

	@FXML
	private void handleOpenDocument() {
		System.out.println("handleOpenDocument");
	}

	@FXML
	private void handlePaste() {
		System.out.println("handlePaste");
	}

	@FXML
	private void handlePasteAfter() {
		System.out.println("handlePasteAfter");
	}

	@FXML
	private void handlePasteBefore() {
		System.out.println("handlePasteBefore");
	}

	@FXML
	private void handleProduceEBook() {
		System.out.println("handleProduceEBook");
	}

	@FXML
	private void handleProduceOpenOffice() {
		System.out.println("handleProduceOpenOffice");
	}

	@FXML
	private void handleProducePdf() {
		System.out.println("handleProducePdf");
	}

	@FXML
	private void handleProducePdfRenderX() {
		System.out.println("handleProducePdfRenderX");
	}

	@FXML
	private void handleProduceWebPage() {
		System.out.println("handleProduceWebPage");
	}

	@FXML
	private void handleProduceWord2003() {
		System.out.println("handleProduceWord2003");
	}

	@FXML
	private void handlePromoteSection() {
		System.out.println("handlePromoteSection");
	}

	@FXML
	private void handlePublisherStyleSheetDocumentation() {
		System.out.println("handlePublisherStyleSheetDocumentation");
	}

	@FXML
	private void handleQuickReferenceGuide() {
		System.out.println("handleQuickReferenceGuide");
	}

	@FXML
	private void handleRedo() {
		System.out.println("handleRedo");
	}

	@FXML
	private void handleRemoveStyleSheet() {
		System.out.println("handleRemoveStyleSheet");
	}

	@FXML
	private void handleReplace() {
		System.out.println("handleReplace");
	}

	@FXML
	private void handleSaveDocument() {
		System.out.println("handleSaveDocument");
	}

	@FXML
	private void handleSaveDocumentAs() {
		System.out.println("handleSaveDocumentAs");
	}

	@FXML
	private void handleSelectAllChildren() {
		System.out.println("handleSelectAllChildren");
	}

	@FXML
	private void handleSelectChild() {
		System.out.println("handleSelectChild");
	}

	@FXML
	private void handleSelectFollowingSibling() {
		System.out.println("handleSelectFollowingSibling");
	}

	@FXML
	private void handleSelectParent() {
		System.out.println("handleSelectParent");
	}

	@FXML
	private void handleSelectPrecedingSibling() {
		System.out.println("handleSelectPrecedingSibling");
	}

	@FXML
	private void handleSetReference() {
		System.out.println("handleSetReference");
	}

	@FXML
	private void handleShowWebPage() {
		System.out.println("handleShowWebPage");
	}

	@FXML
	private void handleTableCopyColumn() {
		System.out.println("handleTableCopyColumn");
	}

	@FXML
	private void handleTableCopyRow() {
		System.out.println("handleTableCopyRow");
	}

	@FXML
	private void handleTableCutColumn() {
		System.out.println("handleTableCutColumn");
	}

	@FXML
	private void handleTableCutRow() {
		System.out.println("handleTableCutRow");
	}

	@FXML
	private void handleTableDecrementColumnSpan() {
		System.out.println("handleTableDecrementColumnSpan");
	}

	@FXML
	private void handleTableDecrementRowSpan() {
		System.out.println("handleTableDecrementRowSpan");
	}

	@FXML
	private void handleTableDeleteColumn() {
		System.out.println("handleTableDeleteColumn");
	}

	@FXML
	private void handleTableDeleteRow() {
		System.out.println("handleTableDeleteRow");
	}

	@FXML
	private void handleTableIncreaseSize() {
		System.out.println("handleTableIncreaseSize");
	}

	@FXML
	private void handleTableIncrementColumnSpan() {
		System.out.println("handleTableIncrementColumnSpan");
	}

	@FXML
	private void handleTableIncrementRowSpan() {
		System.out.println("handleTableIncrementRowSpan");
	}

	@FXML
	private void handleTableInsertColumnAfter() {
		System.out.println("handleTableInsertColumnAfter");
	}

	@FXML
	private void handleTableInsertColumnBefore() {
		System.out.println("handleTableInsertColumnBefore");
	}

	@FXML
	private void handleTableInsertRowAfter() {
		System.out.println("handleTableInsertRowAfter");
	}

	@FXML
	private void handleTableInsertRowBefore() {
		System.out.println("handleTableInsertRowBefore");
	}

	@FXML
	private void handleTablePasteColumnAfter() {
		System.out.println("handleTablePasteColumnAfter");
	}

	@FXML
	private void handleTablePasteColumnBefore() {
		System.out.println("handleTablePasteColumnBefore");
	}

	@FXML
	private void handleTablePasteRowAfter() {
		System.out.println("handleTablePasteRowAfter");
	}

	@FXML
	private void handleTablePasteRowBefore() {
		System.out.println("handleTablePasteRowBefore");
	}

	@FXML
	private void handleTextFind() {
		System.out.println("handleTextFind");
	}

	@FXML
	private void handleTextReplace() {
		System.out.println("handleTextReplace");
	}

	@FXML
	private void handleUndo() {
		System.out.println("handleUndo");
	}

	@FXML
	private void handleUserDocumentation() {
		System.out.println("handleUserDocumentation");
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@FXML
	public void handleShowingEditMenu() {
		System.out.println("handleShowingEditMenu");
		if (systemClipboard == null) {
			systemClipboard = Clipboard.getSystemClipboard();
		}

		if (systemClipboard.hasString()) {
			adjustForClipboardContents();
		} else {
			adjustForEmptyClipboard();
		}

//		if (currentApproachController.anythingSelected()) {
			adjustForSelection();
//
//		} else {
//			adjustForDeselection();
//		}
	}

	// TODO: put these in a separate handler (maybe)
	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	public void adjustForEmptyClipboard() {
		menuItemEditPaste.setDisable(true); // nothing to paste
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	private void adjustForClipboardContents() {
		menuItemEditPaste.setDisable(false); // something to paste
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	private void adjustForSelection() {
		menuItemEditCut.setDisable(false);
		menuItemEditCopy.setDisable(false);
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	private void adjustForDeselection() {
		menuItemEditCut.setDisable(true);
		menuItemEditCopy.setDisable(true);
	}

}
