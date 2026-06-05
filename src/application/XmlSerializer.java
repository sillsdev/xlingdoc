/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package application;

import javafx.scene.web.WebEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.transform.*;

public class XmlSerializer {

    public static void exportWebViewToXml(WebEngine webEngine, java.io.File outputFile, String dtdSystemId) throws Exception {
        
        // 1. Instead of using webEngine.getDocument() and native DOM methods,
        // extract the exact XML segment string directly out of WebKit's memory via JS
        String xmlContentString = (String) webEngine.executeScript(
            "document.getElementsByTagName('lingPaper')[0].outerHTML;"
        );

        if (xmlContentString == null || xmlContentString.isEmpty()) {
            throw new IllegalArgumentException("Could not extract the <lingPaper> markup from WebView.");
        }
        xmlContentString = xmlContentString.replace("<br><br>", "<br></br>");

        // 2. Instantiate a safe standard Java XML parser
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        
        // 3. Parse the string directly into a standard Java org.w3c.dom.Document
        // This avoids any com.sun.webkit native object attachments!
        Document xmlDoc = factory.newDocumentBuilder().parse(
            new ByteArrayInputStream(xmlContentString.getBytes(StandardCharsets.UTF_8))
        );

     // Resolve case variations and clean up runtime attributes
        XmlCaseCorrector.sanitizeAndFixCasing(xmlDoc.getDocumentElement(), xmlDoc);
        
        SCMFriendlyXmlFormatter.saveWithCustomFormat(xmlDoc, outputFile);
//        Element rootElement = xmlDoc.getDocumentElement();
//
//        // 4. Run the rest of your cleanup rules safely in standard Java space
//        normalizeInlineElements(rootElement);
//        sanitizeXmlTree(rootElement);
//
//        // 5. Serialize out to disk using your Transformer setup
//        javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
//        javax.xml.transform.Transformer transformer = tf.newTransformer();
//        
//        transformer.setOutputProperty(OutputKeys.INDENT, "no");
//        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
//        
//        if (dtdSystemId != null) {
//            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
//            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdSystemId);
//            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//XMLmind//DTD XLingPap//EN");
//        }
//
//        javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(xmlDoc);
//        javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(outputFile);
//        transformer.transform(source, result);
    }
    
    /**
     * Recursively strips out browser-specific mutations like "contenteditable"
     * or temporary UI IDs so they don't pollute your clean production XML data.
     */
    private static void sanitizeXmlTree(Element element) {
        // Remove editing attributes we used inside the WebView
        element.removeAttribute("contenteditable");
        
        // If you generated temporary auto-IDs for synchronization, strip them here if needed
        if (element.getAttribute("id").startsWith("gen_")) {
            element.removeAttribute("id");
        }

        // Walk down the child elements
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                sanitizeXmlTree((Element) child);
            }
        }
    }

    public static void normalizeInlineElements(Element root) {
        Node current = root.getFirstChild();
        while (current != null) {
            Node next = current.getNextSibling();
            
            // If it's an element, recurse deeper first
            if (current.getNodeType() == Node.ELEMENT_NODE) {
                normalizeInlineElements((Element) current);
                
                // Look ahead: if the next sibling is an element of the exact same type, merge them
                if (next != null && next.getNodeType() == Node.ELEMENT_NODE) {
                    if (current.getNodeName().equals(next.getNodeName()) && isInlineElement(current.getNodeName())) {
                        
                        // Move all children of the 'next' node into the 'current' node
                        while (next.hasChildNodes()) {
                            current.appendChild(next.getFirstChild());
                        }
                        
                        // Remove the now-empty 'next' node from the DOM
                        root.removeChild(next);
                        
                        // Reset 'next' so we re-evaluate this position on the next loop iteration
                        next = current.getNextSibling(); 
                    }
                }
                
                // Clean up accidental empty inline elements (e.g., <bold-tag></bold-tag>)
                if (!current.hasChildNodes() && isInlineElement(current.getNodeName())) {
                    root.removeChild(current);
                }
            }
            current = next;
        }
    }

    private static boolean isInlineElement(String tagName) {
        // Check against your list of DTD inline tags
        return tagName.equals("bold-tag") || tagName.equals("gloss-tag") || tagName.equals("foreign-word");
    }
}