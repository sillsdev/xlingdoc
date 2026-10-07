/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.view;

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.SortedSet;

import org.controlsfx.control.textfield.TextFields;
import org.sil.utility.view.ControllerUtilities;
import org.sil.xlingdoc.Main;
import org.sil.xlingdoc.service.XLingDocXmlToInternalHtmlMapper;
import org.sil.xlingdoc.service.dtdhandling.ComponentToolOperationType;
import org.sil.xlingdoc.service.dtdhandling.DtdInspector;
import org.sil.xlingdoc.service.dtdhandling.XmlDocumentManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;

/**
 * 
 */
public class ComponentToolHandler {

	public static void askForElementToInsert(Document doc, Element element, XmlDocumentManager manager, DtdInspector dtdInspector, Main main, ResourceBundle bundle) {
		try {
			// TextFields insert component
			String title = bundle.getString("program.name");
			String contentText = bundle.getString("label.insertcomponent");
			TextInputDialog dialog = ControllerUtilities.getTextInputDialog(main, title,
					contentText, bundle);

			SortedSet<String> candidates = dtdInspector.getValidInsertElements(element, manager);
//			ObservableList<String> listOfWords = FXCollections.observableArrayList();
//			ObservableList<Word> wordsToUse = words;
//			for (Word word : wordsToUse) {
//				listOfWords.add(word.getWord());
//			}
			System.out.println("candidates = " + candidates);
			TextFields.bindAutoCompletion(dialog.getEditor(), candidates);
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(candidate -> {
				System.out.println("askForElementToInsert: result is '" + result.get() + "'");
				System.out.println("\tcandidate = " + candidate);
//				int index = candidates..getFirst().indexOf(result.get());
//					handleCVWords(index, true, true);
			});

		} catch (Exception e) {
			e.printStackTrace();
			Main.reportException(e, bundle);
		}
	}

	public static ListView<String> fillComponentToolCandidates(Element element, ComponentToolOperationType op, XmlDocumentManager manager, DtdInspector dtdInspector) {
//		listView.getChildrenUnmodifiable().clear();
		SortedSet<String> candidates = null;
		switch (op) {
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
		System.out.println("\tcandidates size = " + candidates.size());
		ObservableList<String> obsList = FXCollections.observableArrayList();
		for (String s : candidates) {
			obsList.add(s);
		}
		System.out.println("\tobsList size = " + obsList.size());
		ListView<String> listView = new ListView<String>(obsList);
		System.out.println("\tlist view size = " + listView.getItems().size());
		return listView;
	}

	public static Element determineElementToUse(Element elementClickedOn, XmlDocumentManager manager) {
		Element element = elementClickedOn;
		if (elementClickedOn.getTagName().equals("SPAN") || elementClickedOn.getTagName().equals("DIV")) {
			element = (Element) element.getParentNode();
			System.out.println("\t\tusing parent: " + element.getTagName());
		}
		String nameToUse = XLingDocXmlToInternalHtmlMapper.getRenamedElement(element.getTagName());
		element = manager.getMasterXmlDoc().createElement(nameToUse);
		System.out.println("\telement passed in = " + element);
		return element;
	}

}
