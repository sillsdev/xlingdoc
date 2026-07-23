/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class SpellngChecker {

	/**
	 * 
	 */
	public SpellngChecker() {
		// TODO Auto-generated constructor stub
	}

	public static List<String> collectWordsInText(String text, Locale locale) {
		List<String> words = new ArrayList<String>();
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
				words.add(word);
			}
			start = end;
			end = iterator.next();
		}
		return words;
	}

	public static int checkSpellingInDocument(Document doc, Locale locale) {
		// mispelledWordsCount is mainly for testing
		int mispelledWordsCount = 0;
		NodeList tops = doc.getElementsByTagName("lingPaper");
		Element element = (Element) tops.item(0);
		mispelledWordsCount = checkElement(element, mispelledWordsCount, locale);
		return mispelledWordsCount;
	}

	private static int checkElement(Element element, int mispellings, Locale locale) {
		List<String> words = new ArrayList<String>();
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				words = collectWordsInText(child.getTextContent(), locale);
				mispellings += checkSpelling(words, locale);
			}
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				mispellings = checkElement((Element) child, mispellings, locale);
			}
		}
		return mispellings;
	}

	private static int checkSpelling(List<String> words, Locale locale) {
		// TODO: use Hunspell or Java sumpin to look up words
		List<String> badSpellings = List.of("XLingPaper", "sectionRef", "exampleRef");
		int count = 0;
		for (String s : words) {
			if (badSpellings.contains(s)) {
				count++;
			}
		}
		return count;
	}
}
