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
import org.junit.Rule;
import org.junit.Test;
import org.sil.utility.view.JavaFXThreadingRule;
import org.sil.xlingdoc.service.dtdhandling.XmlDocumentManager;
import org.w3c.dom.Document;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * 
 */
public class WebPageUtilitiesTests {

	@Rule
	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

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
	public void removeEmbeddingInLanguagesAndTypesTest() {
		WebView webView = new WebView();
		WebEngine webEngine = webView.getEngine();
	    File file = new File("test/testData/CorrectlyEmbeddedElements.html");
		try {
			String expected = Files.readString(file.toPath()).replaceAll("\r", "");
			System.out.println("expected html ==============================");
			System.out.println(expected);
			System.out.println("expected html ==============================");
//		    File fileInput = new File("test/testData/SamplePaperInternalHtml.html");
		    File fileInput = new File("test/testData/IncorrectlyEmbeddedElements.html");
	        String htmlContent = Files.readString(fileInput.toPath()).replaceAll("\r", "");
			webEngine.loadContent(htmlContent);
			webEngine.getLoadWorker().stateProperty().addListener((_, _, newState) -> {
			    if (newState == Worker.State.SUCCEEDED) {
//			        webEngine = WebPageUtilities.allowConsoleLogViaJavaScript(webEngine, webPageInteractor);
					Document doc = webEngine.getDocument();
					doc = WebPageUtilities.removeIncorrectEmbedding(doc);
					try {
						// already have nested type elements
						XmlDocumentManager manager = new XmlDocumentManager();
						String html = manager.documentToString(doc);
						System.out.println("load succeeded html ==============================");
						System.out.println(html);
						System.out.println("load succeeded html ==============================");
						Assert.assertEquals(expected, html);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					WebPageUtilities.addInputBoxes(webEngine);
			    }
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
