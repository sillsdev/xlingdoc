/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.view;

import java.util.SortedSet;
import java.util.stream.Collectors;

import org.sil.xlingdoc.service.WebPageUtilities;
import org.sil.xlingdoc.service.XLingDocXmlToInternalHtmlMapper;
import org.sil.xlingdoc.service.dtdhandling.ComponentToolOperationType;
import org.sil.xlingdoc.service.dtdhandling.DtdInspector;
import org.sil.xlingdoc.service.dtdhandling.XmlDocumentManager;
import org.w3c.dom.Element;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;

/**
 * 
 */
public class ComponentToolHandler {

	String componentToolSelectedItem = "";
	private ObservableList<String> componentToolObservableList = FXCollections.observableArrayList();
	ComponentToolOperationType componentToolOperation = ComponentToolOperationType.Insert;
	XmlDocumentManager manager;
	DtdInspector dtdInspector;

	public ComponentToolHandler(XmlDocumentManager manager, DtdInspector dtdInspector) {
		super();
		this.manager = manager;
		this.dtdInspector = dtdInspector;
	}

	public String getComponentToolSelectedItem() {
		return componentToolSelectedItem;
	}

	public ListView<String> fillComponentToolCandidates(Element element) {
//		listView.getChildrenUnmodifiable().clear();
		SortedSet<String> candidates = null;
		switch (componentToolOperation) {
		case Convert:
			candidates = dtdInspector.getValidConvertElements(element, manager);
			break;
		case ConvertWrap:
			candidates = dtdInspector.getValidConvertWrapElements(element, manager);
			break;
		case Insert:
			candidates = dtdInspector.getValidInsertElements(element, manager);
			break;
		case InsertAfter:
			candidates = dtdInspector.getValidAdjacentElements(element, manager, false);
			break;
		case InsertBefore:
			candidates = dtdInspector.getValidAdjacentElements(element, manager, true);
			break;
		case Replace:
			candidates = dtdInspector.getValidReplaceElements(element, manager);
			break;
		default:
			break;
		}
//		System.out.println("\tcandidates size = " + candidates.size());
		ObservableList<String> obsList = FXCollections.observableArrayList();
		for (String s : candidates) {
			obsList.add(s);
		}
//		System.out.println("\tobsList size = " + obsList.size());
		ListView<String> listView = new ListView<String>(obsList);
//		System.out.println("\tlist view size = " + listView.getItems().size());
		return listView;
	}

	public Element determineElementToUse(Element elementClickedOn) {
		Element element = elementClickedOn;
		if (elementClickedOn.getTagName().equals("SPAN") || elementClickedOn.getTagName().equals("DIV")) {
			element = (Element) element.getParentNode();
//			System.out.println("\t\tusing parent: " + element.getTagName());
		}
		String nameToUse = XLingDocXmlToInternalHtmlMapper.getRenamedElement(element.getTagName());
		element = manager.getMasterXmlDoc().createElement(nameToUse);
//		System.out.println("\telement passed in = " + element);
		return element;
	}

	public void handleCancel(ListView<String> componentToolListView, TextInputControl componentToolTextField) {
		Platform.runLater(() -> {
			componentToolListView.getItems().clear();
			componentToolTextField.setText("");
//			webView.requestFocus();
		});
	}

	public void handleOK(ListView<String> componentToolListView, TextInputControl componentToolTextField,
			WebEngine webEngine) {
		Platform.runLater(() -> {
			System.out.println("handleComponentToolOK");
			if (componentToolListView.getItems().size() > 0) {
				componentToolSelectedItem = "";
				componentToolSelectedItem = componentToolListView.getSelectionModel().getSelectedItem();
				System.out.println(
						"\tSelected '" + componentToolSelectedItem + "'; operation = " + componentToolOperation);
				componentToolListView.getItems().clear();
				componentToolTextField.setText("");
				int pos = WebPageUtilities.obtainCurrentCursorPosition(webEngine);
				System.out.println("\tInsert position is " + pos);
			}
//			webView.requestFocus();
		});
	}

	public void setUpComponentTool(Element elementClickedOn, ListView<String> componentToolListView,
			TextInputControl componentToolTextField, ComponentToolOperationType componentToolOperation) {
		Platform.runLater(() -> {
			System.out.println("handleInsert");
//			System.out.println("\telement clicked on = " + elementClickedOn);
			Element element = determineElementToUse(elementClickedOn);
			ListView<String> listView = fillComponentToolCandidates(element);
			componentToolListView.getItems().setAll(listView.getItems());
			componentToolObservableList.setAll(componentToolListView.getItems());
//			System.out.println("list view size = " + componentToolListView.getItems().size());
			componentToolTextField.setText("");
			componentToolTextField.requestFocus();
			this.componentToolOperation = componentToolOperation;
		});
	}

	public void textFieldKeyboardHandling(KeyEvent event, ListView<String> componentToolListView,
			TextInputControl componentToolTextField, WebEngine webEngine) {
		Platform.runLater(() -> {
			switch (event.getCode()) {
			case KeyCode.DOWN:
			case KeyCode.KP_DOWN:
				if (componentToolListView.getItems().size() > 0) {
					componentToolListView.requestFocus();
					componentToolListView.getSelectionModel().selectFirst();
				}
				// the focus is in the list view, so we're done processing the text field
				return;
			case KeyCode.ENTER:
				handleOK(componentToolListView, componentToolTextField, webEngine);
				break;
			case KeyCode.ESCAPE:
				componentToolTextField.setText("");
				handleCancel(componentToolListView, componentToolTextField);
				break;
			default:
				// nothing special to do
				break;
			}
			String match = componentToolTextField.getText();
//			ObservableList<String> matches = listViewComponentTool.getItems().stream().filter(i -> i.startsWith(match))
//					.collect(Collectors.toCollection(FXCollections::observableArrayList));
			ObservableList<String> matches = componentToolObservableList.stream().filter(i -> i.contains(match))
					.collect(Collectors.toCollection(FXCollections::observableArrayList));
			componentToolListView.getItems().setAll(matches);
			if (componentToolListView.getItems().size() > 0) {
				componentToolListView.getSelectionModel().selectFirst();
			}
		});
	}
}
