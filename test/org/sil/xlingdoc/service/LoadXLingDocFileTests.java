/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sil.xlingdoc.Constants;

/**
 * 
 */
public class LoadXLingDocFileTests {
	private DtdInspector dtdInspector;
	private XmlDocumentManager manager;

	@Before
	public void setUp() throws Exception {
		dtdInspector = new DtdInspector(Constants.DTD_LOCATION, "(text)");
		manager = new XmlDocumentManager();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void loadValidFileTest() {
		String html = XLingDocLoader.loadFileIntoNeededHTML(manager, dtdInspector, Constants.UNIT_TEST_DATA_FILE);
		html = html.replace("\r", "");
		Assert.assertEquals(0,  manager.errorsCount);
		Assert.assertEquals(0,  manager.fatalErrorsCount);
		Assert.assertEquals(0,  manager.warningsCount);
		File file = new File("test/testData/TestSampleExpectedHtml.html");
		try {
			String expected = new String(Files.readString(file.toPath()));
			expected = expected.replaceAll("\r", "");
			Assert.assertEquals(expected, html);
			} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void loadInvalidFileTest() {
		XLingDocLoader.loadFileIntoNeededHTML(manager, dtdInspector, Constants.UNIT_TEST_INVALID_DATA_FILE);
		Assert.assertEquals(5,  manager.errorsCount);
		Assert.assertEquals(0,  manager.fatalErrorsCount);
		Assert.assertEquals(0,  manager.warningsCount);
	}

}
