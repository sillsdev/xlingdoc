/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service.fileio;

import java.io.File;
import java.nio.file.Files;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class SaveXLingDocFileTest {

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
	public void saveTest() {
		checkHtmlToExpected("test/testData/SamplePaperInternalHtml.html", "test/testData/SamplePaper.xml");
		checkHtmlToExpected("test/testData/TestSampleXIncludeInternalHtml.html", "test/testData/SamplePaperXInclude.xml");
	}

	private void checkHtmlToExpected(String htmlFile, String expectedFile) {
		try {
		    File xLingDocInternalHtmlFile = new File(htmlFile);
			String html = Files.readString(xLingDocInternalHtmlFile.toPath());
		    Document doc = parseXhtmlToDocument(html);
	        File out = File.createTempFile("testOutput", "xml");
	        XLingDocSaver.saveXLingDoc(doc, out);
	        String result = Files.readString(out.toPath()).replaceAll("\r", "");
		    File file = new File(expectedFile);
	        String expected = Files.readString(file.toPath()).replaceAll("\r", "");
	        Assert.assertEquals(expected, result);
	        out.delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Following from Gemini
	public Document parseXhtmlToDocument(String htmlString) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new java.io.StringReader(htmlString));        
        return builder.parse(is);
    }
}
