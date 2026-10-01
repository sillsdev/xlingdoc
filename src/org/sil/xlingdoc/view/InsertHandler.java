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
import org.sil.xlingdoc.service.dtdhandling.DtdInspector;
import org.sil.xlingdoc.service.dtdhandling.XmlDocumentManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.scene.control.TextInputDialog;

/**
 * 
 */
public class InsertHandler {

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

}
