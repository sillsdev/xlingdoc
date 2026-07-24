package org.sil.xlingdoc.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.xlingdoc.Constants;
import org.sil.xlingdoc.service.dtdhandling.DtdInspector;
import org.sil.xlingdoc.service.dtdhandling.XmlDocumentManager;
import org.sil.xlingdoc.service.fileio.XLingDocLoader;
import org.w3c.dom.Document;

public class SpellingCheckerTests {

	Locale locale;
	List<WordLocationInText> words = new ArrayList<WordLocationInText>();
	
	@Before
	public void setUp() throws Exception {
		locale = Locale.of("en");
		words.clear();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void collectWordsTest() {
		// empty
		words = SpellngChecker.collectWordsInText("", words, locale);
		assertEquals(0, words.size());
		// basic English
		words = SpellngChecker.collectWordsInText("The quick brown fox jumped over the lazy dog.", words, locale);
		assertEquals(9, words.size());
		checkWordAndLocation(0, "The", 0);
		checkWordAndLocation(1, "quick", 4);
		checkWordAndLocation(2, "brown", 10);
		checkWordAndLocation(3, "fox", 16);
		checkWordAndLocation(4, "jumped", 20);
		checkWordAndLocation(5, "over", 27);
		checkWordAndLocation(6, "the", 32);
		checkWordAndLocation(7, "lazy", 36);
		checkWordAndLocation(8, "dog", 41);
		// English with lots of punctuation marks.
		words = SpellngChecker.collectWordsInText("He shouted, “John says, ‘Mary's home!’ and \"(Can I believe it?\"  No!!) It's way too soon.”", words, locale);
		assertEquals(16, words.size());
		checkWordAndLocation(0, "He", 0);
		checkWordAndLocation(1, "shouted", 3);
		checkWordAndLocation(2, "John", 13);
		checkWordAndLocation(3, "says", 18);
		checkWordAndLocation(4, "Mary's", 25);
		checkWordAndLocation(5, "home", 32);
		checkWordAndLocation(6, "and", 39);
		checkWordAndLocation(7, "Can", 45);
		checkWordAndLocation(8, "I", 49);
		checkWordAndLocation(9, "believe", 51);
		checkWordAndLocation(10, "it", 59);
		checkWordAndLocation(11, "No", 65);
		checkWordAndLocation(12, "It's", 71);
		checkWordAndLocation(13, "way", 76);
		checkWordAndLocation(14, "too", 80);
		checkWordAndLocation(15, "soon", 84);
		// Spanish punctuation
		locale = Locale.of("es");
		words = SpellngChecker.collectWordsInText("¿Como estas, niño? ¡Bien!", words, locale);
		assertEquals(4, words.size());
		checkWordAndLocation(0, "Como", 1);
		checkWordAndLocation(1, "estas", 6);
		checkWordAndLocation(2, "niño", 13);
		checkWordAndLocation(3, "Bien", 20);
	}

	private void checkWordAndLocation(int index,  String word, int location) {
		assertEquals(word, words.get(index).word());
		assertEquals(location, words.get(index).location());
	}

	@Test
	public void checkSpellingInDocumentTest() {
		XmlDocumentManager manager = new XmlDocumentManager();
		DtdInspector dtdInspector = new DtdInspector(Constants.DTD_LOCATION, "(text)");
		XLingDocLoader.loadFileIntoNeededHTML(manager, dtdInspector, Constants.UNIT_TEST_DATA_FILE);
		Document doc = manager.getMasterXmlDoc();
		int mispelledWordsCount = -1;
		mispelledWordsCount = SpellngChecker.checkSpellingInDocument(doc, locale);
		assertEquals(8, mispelledWordsCount);

	}

}
