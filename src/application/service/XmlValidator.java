/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package application.service;

/**
 * code drafted by Gemini
 */
import javafx.scene.web.WebEngine;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XmlValidator {

    public static boolean validateWebViewContent(WebEngine webEngine, String dtdSystemId) {
        try {
            // 1. Extract the raw document string from WebKit's memory
            String htmlContent = (String) webEngine.executeScript(
                "document.getElementsByTagName('paws-document')[0].outerHTML;"
            );

            // 2. Wrap it back inside a clean XML string header including the original DTD link
            String fullXmlString = String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE paws-document SYSTEM \"%s\">\n%s",
                dtdSystemId, htmlContent
            );

            // 3. Set up a fresh validating parser instance
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            // Point the entity resolver to your local DTD file path if it's stored locally
            builder.setEntityResolver((publicId, systemId) -> {
                if (systemId.contains(dtdSystemId)) {
                    return new org.xml.sax.InputSource(new java.io.FileReader("data/" + dtdSystemId));
                }
                return null;
            });

            // 4. Parse the byte stream. If this throws a SAXParseException, the DOM is invalid.
            builder.parse(new ByteArrayInputStream(fullXmlString.getBytes(StandardCharsets.UTF_8)));
            
            System.out.println("Validation Success: Document aligns perfectly with DTD schema constraints!");
            return true;

        } catch (org.xml.sax.SAXParseException e) {
            System.err.println("DTD Validation Failure at Line " + e.getLineNumber() + ": " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}