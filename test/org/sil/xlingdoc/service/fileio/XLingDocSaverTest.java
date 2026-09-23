/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service.fileio;

import java.io.File;
import java.nio.file.Files;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sil.xlingdoc.Constants;
import org.sil.xlingdoc.service.dtdhandling.DtdInspector;
import org.sil.xlingdoc.service.dtdhandling.XmlDocumentManager;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XLingDocSaverTest {
	private DtdInspector dtdInspector;
	private XmlDocumentManager manager;
	private ResourceBundle resources;

	@Before
	public void setUp() throws Exception {
		dtdInspector = new DtdInspector(Constants.DTD_LOCATION, "(text)");
		manager = new XmlDocumentManager();
		resources = ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, Locale.of("en"));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void saveTest() {
		String html = XLingDocLoader.loadFileIntoNeededHTML(manager, dtdInspector, Constants.UNIT_TEST_DATA_FILE, resources);
		checkHtmlToExpected(html, Constants.UNIT_TEST_DATA_FILE);
		html = XLingDocLoader.loadFileIntoNeededHTML(manager, dtdInspector, Constants.UNIT_TEST_XINCLUDE_DATA_FILE, resources);
		checkHtmlToExpected(html, Constants.UNIT_TEST_XINCLUDE_DATA_FILE);
	}

	private void checkHtmlToExpected(String html, String expectedFile) {
		try {
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
