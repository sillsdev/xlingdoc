/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

import static org.junit.Assert.*;

import java.util.SortedSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.xlingdoc.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class InsertElementTests {
	private DtdInspector dtdInspector;
	private XmlDocumentManager manager;
	private Document doc;
	String fileContent = "";
	NodeList nl;
	Element el;
	SortedSet<String> result;
	String actual;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dtdInspector = new DtdInspector(Constants.DTD_LOCATION, "(text)");
		manager = new XmlDocumentManager();
		XLingDocLoader.loadFileIntoNeededHTML(manager, dtdInspector, Constants.UNIT_TEST_DATA_FILE);
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
		checkElement(el, EditOperationType.InsertBefore, "[comment]");
		nl = doc.getElementsByTagName("title");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[]");
		nl = doc.getElementsByTagName("author");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[author, authorContactInfo, shortTitle, subtitle]");
		nl = doc.getElementsByTagName("p");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, shortTitle, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(1);
		checkElement(el, EditOperationType.InsertBefore, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(2);
		checkElement(el, EditOperationType.InsertBefore, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, shortTitle, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(3);
		checkElement(el, EditOperationType.InsertBefore, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(4);
		checkElement(el, EditOperationType.InsertBefore, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(5);
		checkElement(el, EditOperationType.InsertBefore, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		nl = doc.getElementsByTagName("section1");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[section1]");
		el = (Element)nl.item(1);
		checkElement(el, EditOperationType.InsertBefore, "[section1]");
		nl = doc.getElementsByTagName("secTitle");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[]");
		nl = doc.getElementsByTagName("endnotes");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[abbreviations, acknowledgements, appendix, glossary, glossaryTerms]");
		nl = doc.getElementsByTagName("references");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[]");
		nl = doc.getElementsByTagName("refAuthor");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[labelContentChoices, refAuthor, shortTitle]");
		el = (Element)nl.item(1);
		checkElement(el, EditOperationType.InsertBefore, "[refAuthor]");
		nl = doc.getElementsByTagName("refWork");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[citeName, refAuthorInitials, refAuthorName, refAuthorSurnameGivenName, refWork]");
		el = (Element)nl.item(1);
		checkElement(el, EditOperationType.InsertBefore, "[refWork]");
		nl = doc.getElementsByTagName("languages");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[referencedInterlinearTexts]");
		nl = doc.getElementsByTagName("language");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[language]");
		nl = doc.getElementsByTagName("types");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[]");
		nl = doc.getElementsByTagName("type");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[comment, type]");
		nl = doc.getElementsByTagName("exampleRef");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertBefore, "[(text), abbrRef, abbreviationsShownHere, appendixRef, br, citation, comment, definition, endnote, endnoteRef, exampleRef, figureRef, genericRef, genericTarget, gloss, glossaryTermRef, img, indexedItem, indexedRangeBegin, indexedRangeEnd, interlinearRefCitation, iso639-3codeRef, iso639-3codesShownHere, langData, link, mediaObject, object, q, sectionRef, tablenumberedRef]");
	}

	@Test
	public void insertAfterTests() {
		nl = doc.getElementsByTagName("frontMatter");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[section1]");
		nl = doc.getElementsByTagName("title");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[author, authorContactInfo, shortTitle, subtitle]");
		nl = doc.getElementsByTagName("author");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[abstract, acknowledgements, affiliation, author, authorContactInfo, contents, date, emailAddress, keywordsShownHere, preface, presentedAt, shortAuthor, version, volume]");
		nl = doc.getElementsByTagName("p");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(1);
		checkElement(el, EditOperationType.InsertAfter, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, section2, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(3);
		checkElement(el, EditOperationType.InsertAfter, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(4);
		checkElement(el, EditOperationType.InsertAfter, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(5);
		checkElement(el, EditOperationType.InsertAfter, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, section2, table, tablenumbered, tree, ul]");
		nl = doc.getElementsByTagName("section1");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[section1]");
		el = (Element)nl.item(1);
		checkElement(el, EditOperationType.InsertAfter, "[section1]");
		nl = doc.getElementsByTagName("secTitle");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, shortTitle, table, tablenumbered, tree, ul]");
		nl = doc.getElementsByTagName("endnotes");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[]");
		nl = doc.getElementsByTagName("references");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[authorContactInfo, index, keywordsShownHere, selectedBibliography]");
		nl = doc.getElementsByTagName("refAuthor");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[refAuthor]");
		el = (Element)nl.item(1);
		checkElement(el, EditOperationType.InsertAfter, "[annotatedBibliographyTypes, refAuthor]");
		nl = doc.getElementsByTagName("refWork");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[refWork]");
		el = (Element)nl.item(1);
		checkElement(el, EditOperationType.InsertAfter, "[refWork]");
		nl = doc.getElementsByTagName("languages");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[]");
		nl = doc.getElementsByTagName("language");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[language]");
		nl = doc.getElementsByTagName("types");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[comment, contentControl, framedTypes, indexTerms, publishingInfo, validation]");
		nl = doc.getElementsByTagName("type");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[comment, type]");
		nl = doc.getElementsByTagName("exampleRef");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.InsertAfter, "[(text), abbrRef, abbreviationsShownHere, appendixRef, br, citation, comment, definition, endnote, endnoteRef, exampleRef, figureRef, genericRef, genericTarget, gloss, glossaryTermRef, img, indexedItem, indexedRangeBegin, indexedRangeEnd, interlinearRefCitation, iso639-3codeRef, iso639-3codesShownHere, langData, link, mediaObject, object, q, sectionRef, tablenumberedRef]");
	}

	@Test
	public void insertTests() {
		nl = doc.getElementsByTagName("title");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Insert, "[abbrRef, appendixRef, br, citation, comment, endnote, endnoteRef, exampleRef, figureRef, genericRef, genericTarget, gloss, glossaryTermRef, img, indexedItem, indexedRangeBegin, indexedRangeEnd, interlinearRefCitation, iso639-3codeRef, langData, link, mediaObject, object, q, sectionRef, tablenumberedRef, titleContentChoices]");
		nl = doc.getElementsByTagName("author");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Insert, "[comment, endnote]");
		nl = doc.getElementsByTagName("p");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Insert, "[abbrRef, abbreviationsShownHere, appendixRef, br, citation, comment, definition, endnote, endnoteRef, exampleRef, figureRef, genericRef, genericTarget, gloss, glossaryTermRef, img, indexedItem, indexedRangeBegin, indexedRangeEnd, interlinearRefCitation, iso639-3codeRef, iso639-3codesShownHere, langData, link, mediaObject, object, q, sectionRef, tablenumberedRef]");
		nl = doc.getElementsByTagName("chart");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Insert, "[abbrRef, appendixRef, br, citation, comment, dl, endnote, endnoteRef, exampleRef, figureRef, genericRef, genericTarget, gloss, glossaryTermRef, hangingIndent, img, indexedItem, indexedRangeBegin, indexedRangeEnd, interlinearRefCitation, iso639-3codeRef, langData, link, mediaObject, object, ol, q, sectionRef, tablenumberedRef, ul]");
		nl = doc.getElementsByTagName("jVol");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Insert, "[]");
	}

	@Test
	public void replaceTests() {
		nl = doc.getElementsByTagName("title");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Replace, "[title]");
		nl = doc.getElementsByTagName("author");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Replace, "[author, authorContactInfo]");
		nl = doc.getElementsByTagName("p");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Replace, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, shortTitle, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(1);
		checkElement(el, EditOperationType.Replace, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, section2, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(2);
		checkElement(el, EditOperationType.Replace, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, shortTitle, table, tablenumbered, tree, ul]");
		el = (Element)nl.item(3);
		checkElement(el, EditOperationType.Replace, "[abbreviationsShownHere, annotationRef, blockquote, chart, dl, example, figure, framedUnit, hangingIndent, interlinear-text, landscape, ol, p, pc, prose-text, table, tablenumbered, tree, ul]");
		nl = doc.getElementsByTagName("chart");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Replace, "[annotationRef, chart, definition, interlinear, interlinearRef, listDefinition, listInterlinear, listSingle, listWord, single, table, tree, word]");
		nl = doc.getElementsByTagName("jVol");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Replace, "[jVol]");
	}

	@Test
	public void convertTests() {
		nl = doc.getElementsByTagName("title");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Convert, "[]");
		nl = doc.getElementsByTagName("author");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Convert, "[]");
		nl = doc.getElementsByTagName("p");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Convert, "[blockquote, chart, hangingIndent, pc, shortTitle, tree]");
		el = (Element)nl.item(1);
		checkElement(el, EditOperationType.Convert, "[blockquote, chart, hangingIndent, pc, tree]");
		nl = doc.getElementsByTagName("example");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Convert, "[interlinear-text]");
		el = (Element)nl.item(1);
		checkElement(el, EditOperationType.Convert, "[blockquote, figure, framedUnit, landscape]");
		nl = doc.getElementsByTagName("exampleRef");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Convert, "[abbrRef, abbreviationsShownHere, appendixRef, br, citation, comment, definition, endnote, endnoteRef, figureRef, genericRef, genericTarget, gloss, glossaryTermRef, img, indexedItem, indexedRangeBegin, indexedRangeEnd, interlinearRefCitation, iso639-3codeRef, iso639-3codesShownHere, langData, link, mediaObject, object, q, sectionRef, tablenumberedRef]");
		nl = doc.getElementsByTagName("citation");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Convert, "[abbrRef, abbreviationsShownHere, appendixRef, br, comment, definition, endnote, endnoteRef, exampleRef, figureRef, genericRef, genericTarget, gloss, glossaryTermRef, img, indexedItem, indexedRangeBegin, indexedRangeEnd, interlinearRefCitation, iso639-3codeRef, iso639-3codesShownHere, langData, link, mediaObject, object, q, sectionRef, tablenumberedRef]");
		nl = doc.getElementsByTagName("chart");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Convert, "[definition, tree]");
		nl = doc.getElementsByTagName("jVol");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.Convert, "[]");
	}

	@Test
	public void convertWrapTests() {
		nl = doc.getElementsByTagName("title");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.ConvertWrap, "[]");
		nl = doc.getElementsByTagName("author");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.ConvertWrap, "[]");
		nl = doc.getElementsByTagName("p");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.ConvertWrap, "[blockquote, framedUnit, interlinear-text, landscape, prose-text, tree]");
		el = (Element)nl.item(1);
		checkElement(el, EditOperationType.ConvertWrap, "[blockquote, framedUnit, interlinear-text, landscape, prose-text, tree]");
		nl = doc.getElementsByTagName("example");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.ConvertWrap, "[blockquote, framedUnit, landscape]");
		el = (Element)nl.item(1);
		checkElement(el, EditOperationType.ConvertWrap, "[blockquote, framedUnit, landscape]");
		nl = doc.getElementsByTagName("exampleRef");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.ConvertWrap, "[comment, definition, genericRef, gloss, langData, link, q]");
		nl = doc.getElementsByTagName("citation");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.ConvertWrap, "[comment, definition, genericRef, gloss, langData, link, q]");
		nl = doc.getElementsByTagName("chart");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.ConvertWrap, "[]");
		nl = doc.getElementsByTagName("jVol");
		el = (Element)nl.item(0);
		checkElement(el, EditOperationType.ConvertWrap, "[]");
	}

	protected void checkElement(Element el, EditOperationType op, String expected) {
		switch (op) {
		case Convert:
			result = dtdInspector.getValidConvertElements(el, manager);
			break;
		case ConvertWrap:
			result = dtdInspector.getValidConvertWrapElements(el, manager);
			break;
		case Insert:
			result = dtdInspector.getValidInsertElements(el, manager);
			break;
		case InsertAfter:
			result = dtdInspector.getValidAdjacentElements(el, manager, false);
			break;
		case InsertBefore:
			result = dtdInspector.getValidAdjacentElements(el, manager, true);
			break;
		case Replace:
			result = dtdInspector.getValidReplaceElements(el, manager);
			break;
		default:
			break;
		}
		actual = result.toString();
		assertEquals(expected, actual);
	}

}
