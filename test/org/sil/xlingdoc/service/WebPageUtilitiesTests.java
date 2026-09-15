/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

import java.io.File;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sil.xlingdoc.service.dtdhandling.XmlDocumentManager;
import org.w3c.dom.Document;

/**
 * 
 */
public class WebPageUtilitiesTests {

//	@Rule
//	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void removeEmbeddingTest() {
		try {
		    File file = new File("test/testData/CorrectlyEmbeddedElements.html");
			String expectedHtml = Files.readString(file.toPath());
		    File fileInput = new File("test/testData/IncorrectlyEmbeddedElements.html");
	        String incorrectHtml = Files.readString(fileInput.toPath()).replaceAll("\r", "");
			XmlDocumentManager manager = new XmlDocumentManager();
	        Document doc = manager.loadXMLFromString(incorrectHtml);
			doc = WebPageUtilities.removeIncorrectEmbedding(doc);
			String adjustedHtml = manager.documentToString(doc);
			Assert.assertEquals(expectedHtml, adjustedHtml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
