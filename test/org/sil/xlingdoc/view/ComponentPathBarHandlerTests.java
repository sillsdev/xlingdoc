/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.view;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sil.xlingdoc.service.dtdhandling.XmlDocumentManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * 
 */
public class ComponentPathBarHandlerTests {

	ComponentPathBarHandler componentPathBarHandler;
	TextFlow componentPathBar;
	Document doc;
	NodeList nodelist;
	ObservableList<Node> contents;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		componentPathBarHandler = new ComponentPathBarHandler();
		componentPathBar = new TextFlow();
		File file = new File("test/testData/SampleWithInputElements.html");
		String expectedHtml = Files.readString(file.toPath(), StandardCharsets.UTF_8);
		XmlDocumentManager manager = new XmlDocumentManager();
		doc = manager.loadXMLFromString(expectedHtml);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void addElementInputInDetailsTest() {
		nodelist = doc.getElementsByTagName("INPUT");
		Assert.assertEquals(29, nodelist.getLength());
		Element el = (Element) nodelist.item(0);
		componentPathBarHandler.addElementToComponentPathBar(el, componentPathBar);
		componentPathBarHandler.markLastElement(el, componentPathBar);
		contents = componentPathBar.getChildrenUnmodifiable();
//		showContents();
		Assert.assertEquals(3, contents.size());
		checkContents(0, " lingPaper", true, false);
		checkContents(1, componentPathBarHandler.kComponentGap, false, false);
		checkContents(2, " section1", true, true);
		int index = 2;
		Text t = (Text) contents.get(index);
		Assert.assertEquals(componentPathBarHandler.kStyleOfFinal, t.getStyle());
	}

	@Test
	public void addElementExampleInputTest() {
		nodelist = doc.getElementsByTagName("INPUT");
		Assert.assertEquals(29, nodelist.getLength());
		Element el = (Element) nodelist.item(1);
		componentPathBarHandler.addElementToComponentPathBar(el, componentPathBar);
		componentPathBarHandler.markLastElement(el, componentPathBar);
		contents = componentPathBar.getChildrenUnmodifiable();
//		showContents();
		Assert.assertEquals(5, contents.size());
		checkContents(0, " lingPaper", true, false);
		checkContents(1, componentPathBarHandler.kComponentGap, false, false);
		checkContents(2, " section1", true, false);
		checkContents(3, componentPathBarHandler.kComponentGap, false, false);
		checkContents(4, " example", true, true);
	}

	@Test
	public void addElementLanguagesInputTest() {
		nodelist = doc.getElementsByTagName("INPUT");
		Assert.assertEquals(29, nodelist.getLength());
		Element el = (Element) nodelist.item(11);
		componentPathBarHandler.addElementToComponentPathBar(el, componentPathBar);
		componentPathBarHandler.markLastElement(el, componentPathBar);
		contents = componentPathBar.getChildrenUnmodifiable();
//		showContents();
		Assert.assertEquals(5, contents.size());
		checkContents(0, " lingPaper", true, false);
		checkContents(1, componentPathBarHandler.kComponentGap, false, false);
		checkContents(2, " languages", true, false);
		checkContents(3, componentPathBarHandler.kComponentGap, false, false);
		checkContents(4, " language", true, true);
	}

	@Test
	public void addElementLineTest() {
		nodelist = doc.getElementsByTagName("LANGDATA");
		Assert.assertEquals(1, nodelist.getLength());
		Element el = (Element) nodelist.item(0);
		componentPathBarHandler.addElementToComponentPathBar(el, componentPathBar);
		componentPathBarHandler.markLastElement(el, componentPathBar);
		contents = componentPathBar.getChildrenUnmodifiable();
//		showContents();
		Assert.assertEquals(13, contents.size());
		checkContents(0, " lingPaper", true, false);
		checkContents(1, componentPathBarHandler.kComponentGap, false, false);
		checkContents(2, " section1", true, false);
		checkContents(3, componentPathBarHandler.kComponentGap, false, false);
		checkContents(4, " example", true, false);
		checkContents(5, componentPathBarHandler.kComponentGap, false, false);
		checkContents(6, " interlinear", true, false);
		checkContents(7, componentPathBarHandler.kComponentGap, false, false);
		checkContents(8, " lineGroup", true, false);
		checkContents(9, componentPathBarHandler.kComponentGap, false, false);
		checkContents(10, " line", true, false);
		checkContents(11, componentPathBarHandler.kComponentGap, false, false);
		checkContents(12, " langData", true, true);
	}

//	private void showContents() {
//		System.out.print("flow = ");
//		StringBuilder sb = new StringBuilder();
//		for (Node n : contents) {
//			if (n instanceof Text t) {
//				sb.append(t.getText());
//			}
//		}
//		System.out.print(sb.toString() + "\n");
//	}

	private void checkContents(int index, String sExpected, boolean hasElement, boolean isFinal) {
		Text t = (Text) contents.get(index);
		Assert.assertEquals(sExpected, t.getText());
		if (hasElement) {
			Element e = (Element)t.getUserData();
			Assert.assertEquals(sExpected.toUpperCase().trim(), e.getTagName());			
		}
		if (isFinal) {
			Assert.assertEquals(componentPathBarHandler.kStyleOfFinal, t.getStyle());
		}
		else {
			Assert.assertEquals("", t.getStyle());
		}
	}

}
