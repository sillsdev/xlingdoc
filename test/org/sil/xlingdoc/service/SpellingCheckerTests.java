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
	List<String> words = new ArrayList<String>();
	
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
		words = SpellngChecker.collectWordsInText("", locale);
		assertEquals(0, words.size());
		// basic English
		words = SpellngChecker.collectWordsInText("The quick brown fox jumped over the lazy dog.", locale);
		assertEquals(9, words.size());
		assertEquals("The", words.get(0));
		assertEquals("quick", words.get(1));
		assertEquals("brown", words.get(2));
		assertEquals("fox", words.get(3));
		assertEquals("jumped", words.get(4));
		assertEquals("over", words.get(5));
		assertEquals("the", words.get(6));
		assertEquals("lazy", words.get(7));
		assertEquals("dog", words.get(8));
		// English with lots of punctuation marks.
		words = SpellngChecker.collectWordsInText("He shouted, “John says, ‘Mary's home!’ and \"(Can I believe it?\"  No!!) It's way too soon.”", locale);
		assertEquals(16, words.size());
		assertEquals("He", words.get(0));
		assertEquals("shouted", words.get(1));
		assertEquals("John", words.get(2));
		assertEquals("says", words.get(3));
		assertEquals("Mary's", words.get(4));
		assertEquals("home", words.get(5));
		assertEquals("and", words.get(6));
		assertEquals("Can", words.get(7));
		assertEquals("I", words.get(8));
		assertEquals("believe", words.get(9));
		assertEquals("it", words.get(10));
		assertEquals("No", words.get(11));
		assertEquals("It's", words.get(12));
		assertEquals("way", words.get(13));
		assertEquals("too", words.get(14));
		assertEquals("soon", words.get(15));
		// Spanish punctuation
		locale = Locale.of("es");
		words = SpellngChecker.collectWordsInText("¿Como estas, niño? ¡Bien!", locale);
		assertEquals(4, words.size());
		assertEquals("Como", words.get(0));
		assertEquals("estas", words.get(1));
		assertEquals("niño", words.get(2));
		assertEquals("Bien", words.get(3));
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
