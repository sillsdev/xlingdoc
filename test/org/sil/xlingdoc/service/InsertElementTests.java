/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

import static org.junit.Assert.*;

import java.util.SortedSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class InsertElementTests {
	private DtdSchemaInspector inspector;
	private XmlDocumentManager manager;
	private Document doc;
	String fileContent = "";
	NodeList nl;
	Element el;
	SortedSet<String> before;
	SortedSet<String> after;
	String actual;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		inspector = new DtdSchemaInspector("resources/dtds/XLingPap.dtd");
		manager = new XmlDocumentManager();
		XLingDocLoader.loadFileIntoNeededHTML(manager, inspector, "test/testdata/TestSample.xml");
		doc = manager.getMasterXmlDoc();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void insertBeforeTests() {
		nl = doc.getElementsByTagName("frontMatter");
		el = (Element)nl.item(0);
		checkElement(el, true, "[comment]");
		nl = doc.getElementsByTagName("title");
		el = (Element)nl.item(0);
		checkElement(el, true, "[]");
		nl = doc.getElementsByTagName("author");
		el = (Element)nl.item(0);
		checkElement(el, true, "[author, authorContactInfo, shortTitle, subtitle]");
		nl = doc.getElementsByTagName("p");
		el = (Element)nl.item(0);
		checkElement(el, true, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, shortTitle, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(1);
		checkElement(el, true, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(2);
		checkElement(el, true, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, shortTitle, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(3);
		checkElement(el, true, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(4);
		checkElement(el, true, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(5);
		checkElement(el, true, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		nl = doc.getElementsByTagName("section1");
		el = (Element)nl.item(0);
		checkElement(el, true, "[section1]");
		el = (Element)nl.item(1);
		checkElement(el, true, "[section1]");
		nl = doc.getElementsByTagName("secTitle");
		el = (Element)nl.item(0);
		checkElement(el, true, "[]");
		nl = doc.getElementsByTagName("endnotes");
		el = (Element)nl.item(0);
		checkElement(el, true, "[abbreviations, acknowledgements, appendix, glossary, glossaryTerms]");
		nl = doc.getElementsByTagName("references");
		el = (Element)nl.item(0);
		checkElement(el, true, "[]");
		nl = doc.getElementsByTagName("refAuthor");
		el = (Element)nl.item(0);
		checkElement(el, true, "[labelContentChoices, refAuthor, shortTitle]");
		el = (Element)nl.item(1);
		checkElement(el, true, "[refAuthor]");
		nl = doc.getElementsByTagName("refWork");
		el = (Element)nl.item(0);
		checkElement(el, true, "[citeName, refAuthorInitials, refAuthorName, refAuthorSurnameGivenName, refWork]");
		el = (Element)nl.item(1);
		checkElement(el, true, "[refWork]");
		nl = doc.getElementsByTagName("languages");
		el = (Element)nl.item(0);
		checkElement(el, true, "[referencedInterlinearTexts]");
		nl = doc.getElementsByTagName("language");
		el = (Element)nl.item(0);
		checkElement(el, true, "[language]");
		nl = doc.getElementsByTagName("types");
		el = (Element)nl.item(0);
		checkElement(el, true, "[]");
		nl = doc.getElementsByTagName("type");
		el = (Element)nl.item(0);
		checkElement(el, true, "[comment, type]");
		nl = doc.getElementsByTagName("exampleRef");
		el = (Element)nl.item(0);
		checkElement(el, true, "[abbrRef, abbreviationsShownHere, appendixRef, br, citation, comment, definition, endnote, endnoteRef, exampleRef, figureRef, genericRef, genericTarget, gloss, glossaryTermRef, img, indexedItem, indexedRangeBegin, indexedRangeEnd, interlinearRefCitation, iso639-3codeRef, iso639-3codesShownHere, langData, link, mediaObject, object, q, sectionRef, tablenumberedRef]");
	}

	@Test
	public void insertAfterTests() {
		nl = doc.getElementsByTagName("frontMatter");
		el = (Element)nl.item(0);
		checkElement(el, false, "[section1]");
		nl = doc.getElementsByTagName("title");
		el = (Element)nl.item(0);
		checkElement(el, false, "[author, authorContactInfo, shortTitle, subtitle]");
		nl = doc.getElementsByTagName("author");
		el = (Element)nl.item(0);
		checkElement(el, false, "[abstract, acknowledgements, affiliation, author, authorContactInfo, contents, date, emailAddress, keywordsShownHere, preface, presentedAt, shortAuthor, version, volume]");
		nl = doc.getElementsByTagName("p");
		el = (Element)nl.item(0);
		checkElement(el, false, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(1);
		checkElement(el, false, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, section2, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(3);
		checkElement(el, false, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(4);
		checkElement(el, false, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(5);
		checkElement(el, false, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, section2, table, tablenumbered, tree, ul]");
		nl = doc.getElementsByTagName("section1");
		el = (Element)nl.item(0);
		checkElement(el, false, "[section1]");
		el = (Element)nl.item(1);
		checkElement(el, false, "[section1]");
		nl = doc.getElementsByTagName("secTitle");
		el = (Element)nl.item(0);
		checkElement(el, false, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, shortTitle, table, tablenumbered, tree, ul]");
		nl = doc.getElementsByTagName("endnotes");
		el = (Element)nl.item(0);
		checkElement(el, false, "[]");
		nl = doc.getElementsByTagName("references");
		el = (Element)nl.item(0);
		checkElement(el, false, "[authorContactInfo, index, keywordsShownHere, selectedBibliography]");
		nl = doc.getElementsByTagName("refAuthor");
		el = (Element)nl.item(0);
		checkElement(el, false, "[refAuthor]");
		el = (Element)nl.item(1);
		checkElement(el, false, "[annotatedBibliographyTypes, refAuthor]");
		nl = doc.getElementsByTagName("refWork");
		el = (Element)nl.item(0);
		checkElement(el, false, "[refWork]");
		el = (Element)nl.item(1);
		checkElement(el, false, "[refWork]");
		nl = doc.getElementsByTagName("languages");
		el = (Element)nl.item(0);
		checkElement(el, false, "[]");
		nl = doc.getElementsByTagName("language");
		el = (Element)nl.item(0);
		checkElement(el, false, "[language]");
		nl = doc.getElementsByTagName("types");
		el = (Element)nl.item(0);
		checkElement(el, false, "[comment, contentControl, framedTypes, indexTerms, publishingInfo, validation]");
		nl = doc.getElementsByTagName("type");
		el = (Element)nl.item(0);
		checkElement(el, false, "[comment, type]");
		nl = doc.getElementsByTagName("exampleRef");
		el = (Element)nl.item(0);
		checkElement(el, false, "[abbrRef, abbreviationsShownHere, appendixRef, br, citation, comment, definition, endnote, endnoteRef, exampleRef, figureRef, genericRef, genericTarget, gloss, glossaryTermRef, img, indexedItem, indexedRangeBegin, indexedRangeEnd, interlinearRefCitation, iso639-3codeRef, iso639-3codesShownHere, langData, link, mediaObject, object, q, sectionRef, tablenumberedRef]");
	}

	protected void checkElement(Element el, boolean insertBefore, String expected) {
		before = inspector.getValidAdjacentElements(el, manager, insertBefore);
		actual = before.toString();
		assertEquals(expected, actual);
	}

}
