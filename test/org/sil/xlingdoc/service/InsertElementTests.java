/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.SortedSet;

import javax.xml.parsers.DocumentBuilder;

import org.junit.After;
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
		nl = doc.getElementsByTagName("author");
		el = (Element)nl.item(0);
//		System.out.println("el = " + el.getTagName());
//		System.out.println("el parent = " + el.getParentNode());
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
	}

	@Test
	public void insertAfterTests() {
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
	}

	protected void checkElement(Element el, boolean insertBefore, String expected) {
		before = inspector.getValidAdjacentElements(el, manager, insertBefore);
		actual = before.toString();
		assertEquals(expected, actual);
	}

}
