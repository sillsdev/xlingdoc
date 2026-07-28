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
	SpellngChecker checker;
	
	@Before
	public void setUp() throws Exception {
		locale = Locale.of("en");
		words.clear();
		checker = new SpellngChecker();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void collectWordsTest() {
		// empty
		checker.collectWordsInText("", locale);
		words = checker.getWords();
		assertEquals(0, words.size());
		// basic English
		checker.collectWordsInText("The quick brown fox jumped over the lazy dog.", locale);
		words = checker.getWords();
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
		checker.collectWordsInText("He shouted, “John says, ‘Mary's home!’ and \"(Can I believe it?\"  No!!) It's way too soon.”", locale);
		words = checker.getWords();
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
		checker.collectWordsInText("¿Como estas, niño? ¡Bien!", locale);
		words = checker.getWords();
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
		checker.checkSpellingInDocument(doc, locale);
		List<WordLocationInText> misspelledWords = checker.getMisspelledWords();
		assertEquals(8, misspelledWords.size());
		assertEquals("XLingPaper", misspelledWords.get(0).word());
		assertEquals("XLingPaper", misspelledWords.get(1).word());
		assertEquals("XLingPaper", misspelledWords.get(2).word());
		assertEquals("XLingPaper", misspelledWords.get(3).word());
		assertEquals("XLingPaper", misspelledWords.get(4).word());
		assertEquals("XLingPaper", misspelledWords.get(5).word());
		assertEquals("sectionRef", misspelledWords.get(6).word());
		assertEquals("exampleRef", misspelledWords.get(7).word());
	}

}
