/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.languagetool.JLanguageTool;
import org.languagetool.Languages;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class SpellngChecker {

	JLanguageTool langTool;
	List<String> elementExceptions = List.of(
			"langData",
			"tree",
			"url"
			);
	List<WordLocationInText> words = new ArrayList<WordLocationInText>();
	List<WordLocationInText> misspelledWords = new ArrayList<WordLocationInText>();

	public SpellngChecker() {
		initializeSpellingChecker("en-US");
	}

	public SpellngChecker(Locale locale) {
		initializeSpellingChecker(locale.getLanguage());
	}

	private void initializeSpellingChecker(String code) {
		// Force Java's SAXParserFactory to use the built-in JDK parser for rule loading
		System.setProperty("javax.xml.parsers.SAXParserFactory",
		                   "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
		langTool = new JLanguageTool(Languages.getLanguageForShortCode(code));
		for (Rule rule : langTool.getAllRules()) {
			if (!rule.isDictionaryBasedSpellingRule()) {
				langTool.disableRule(rule.getId());
			}
		}
	}

	public List<WordLocationInText> getMisspelledWords() {
		return misspelledWords;
	}

	public List<WordLocationInText> getWords() {
		return words;
	}

	public void collectWordsInText(String text, Locale locale) {
		words.clear();
		// taken from answer from Leo AI on July 22, 2026
		// Get a BreakIterator for word boundaries specific to the locale
		BreakIterator iterator = BreakIterator.getWordInstance(locale);
		iterator.setText(text);
		int start = iterator.first();
		int end = iterator.next();
		while (end != BreakIterator.DONE) {
			String word = text.substring(start, end);
			// BreakIterator includes punctuation as separate "words"; filter them out
			if (Character.isLetterOrDigit(word.charAt(0))) {
				WordLocationInText wl = new WordLocationInText(word, start);
				words.add(wl);
			}
			start = end;
			end = iterator.next();
		}
	}

	public void checkSpellingInDocument(Document doc, Locale locale) {
		words.clear();
		misspelledWords.clear();
		NodeList tops = doc.getElementsByTagName("lingPaper");
		Element element = (Element) tops.item(0);
		checkElement(element, locale);
	}

	private void checkElement(Element element, Locale locale) {
		words.clear();
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				collectWordsInText(child.getTextContent(), locale);
				checkSpelling();
			}
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (elementExceptions.contains(child.getNodeName())) {
					continue;
				}
				checkElement((Element) child, locale);
			}
		}
	}

	private void checkSpelling() {
		for (WordLocationInText word : words) {
			try {
				List<RuleMatch> matches = langTool.check(word.word());
				if (matches.size() > 0) {
					misspelledWords.add(word);
//					System.out.println("typo: '" + match.getMessage() + "; word ='" + word.word() + "'");
//					System.out.println("\tsuggested correction(s):" + match.getSuggestedReplacements());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
