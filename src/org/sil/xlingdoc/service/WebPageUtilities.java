/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

import org.sil.xlingdoc.service.dtdhandling.XmlNameMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

/**
 * 
 */
public class WebPageUtilities {

	private static String buildArguments(Element el, String sIdAttribute, String sId) {
		StringBuilder sb = new StringBuilder();
		sb.append("xLingDocApp.updateAttribute('");
		sb.append(el.getNodeName());
		sb.append("', '");
		sb.append(sIdAttribute);
		sb.append(sId);
		sb.append("', this.value)");
		return sb.toString();
	}

	public static String getCssVariableValue(WebEngine webEngine, String variableName) {
        String script = String.format(
            "window.getComputedStyle(document.documentElement).getPropertyValue('%s').trim();",
            variableName
        );
        Object result = webEngine.executeScript(script);
        return result != null ? result.toString() : "";
    }

	public static void addInputBoxes(WebEngine webEngine) {
		addInputBoxToElement(webEngine, "section1", "id", "15", "--section-background-color", true);
		addInputBoxToElement(webEngine, "section2", "id", "15", "--section-background-color", true);
		addInputBoxToElement(webEngine, "section3", "id", "15", "--section-background-color", true);
		addInputBoxToElement(webEngine, "section4", "id", "15", "--section-background-color", true);
		addInputBoxToElement(webEngine, "section5", "id", "15", "--section-background-color", true);
		addInputBoxToElement(webEngine, "section6", "id", "15", "--section-background-color", true);
		addInputBoxToElement(webEngine, "example", "num", "15", "--example-background-color", false);
		addInputBoxToElement(webEngine, "refAuthor", "name", "40", "", false);
		addInputBoxToElement(webEngine, "refAuthor", "citename", "15", "--citation-background-color", false);
		addInputBoxToElement(webEngine, "refWork", "id", "15", "--citation-background-color", false);
		addInputBoxToElement(webEngine, "language", "id", "15", "", false);
		addInputBoxToElement(webEngine, "type", "id", "15", "", false);
//		addInputBoxToElement(webEngine, "", "");
	}

	public static void addInputBoxToElement(WebEngine webEngine, String sElement, String sIdAttribute, String sSize, String sBackGroundColorCSSVariable, boolean hasDetails) {
		Document doc = webEngine.getDocument();
		NodeList nl = doc.getElementsByTagName(sElement);
		for (int i =0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n instanceof Element el) {
				String sId = el.getAttribute(sIdAttribute);
				Element input = doc.createElement("input");
				input.setAttribute("type", "text");
				input.setAttribute("value", sId);				
				String sArgs = buildArguments(el, sIdAttribute, sId); 
				input.setAttribute("oninput", sArgs);
				input.setAttribute("class", "text-box-editor");
				String sBackGroundColor = getCssVariableValue(webEngine, sBackGroundColorCSSVariable);
				input.setAttribute("style", "background-color:" + sBackGroundColor);
				// set the number of characters to show in the input box
				input.setAttribute("size", sSize);
				if (hasDetails) {
					Node summary = el.getFirstChild().getFirstChild();
					summary.insertBefore(input, summary.getFirstChild());
				} else {
					switch (XmlNameMapper.getMappedElementName(el.getTagName())) {
					case "example":
						Element spanAfter = doc.createElement("span");
						spanAfter.setTextContent(")");
						spanAfter.setAttribute("contenteditable", "false");
						el.insertBefore(spanAfter, el.getFirstChild());
						el.insertBefore(input, el.getFirstChild());
						break;
					case "refAuthor":
						if (sIdAttribute.equals("citename")) {
							Element spanSpacer = doc.createElement("span");
							spanSpacer.setTextContent("   "); // three non-breaking spaces
							spanSpacer.setAttribute("contenteditable", "false");
							el.insertBefore(spanSpacer, el.getFirstChild());
						}
						el.insertBefore(input, el.getFirstChild());
						break;
					default:
						el.insertBefore(input, el.getFirstChild());
						break;
					}
				}
			}
		}
	}

	// for some as yet unknown reason some EMPTY elements have what comes after embedded in them
	// after the HTNML is loaded into the web engine
	public static Document removeIncorrectEmbedding(Document doc) {
		doc = removeEmbedding(doc, "ENDNOTES", true);
		doc = removeEmbedding(doc, "LANGUAGE", false);
		doc = removeEmbedding(doc, "TYPE", false);
		return doc;
	}

	private static Document removeEmbedding(Document doc, String elementToUnembed, boolean includeChildren) {
		NodeList nl = doc.getElementsByTagName(elementToUnembed);
		int size = nl.getLength();
		Element parent = null;
		Element firstElement = null;
		for (int i = 0; i < size; i++) {
			if (nl.item(i) instanceof Element el) {
				if (parent == null) {
					parent = (Element) el.getParentNode();
					firstElement = el;
				}
				Element elNew = (Element) el.cloneNode(false);
				parent.appendChild(elNew);
//				System.out.println("elnew = " + elNew.getLocalName());
//				System.out.println("parent = " + parent.getLocalName());
				if (el.hasChildNodes()) {
					switch(elementToUnembed) {
					case "ENDNOTES":
						includeFirstChild(parent, el, "references");
						break;
					case "TYPE":
						includeFirstChild(parent, el, "comment");
						break;
					}
				}
			}
		}
		if (firstElement != null) {
			parent.removeChild(firstElement);
		}
		return doc;
	}

	private static void includeFirstChild(Element parent, Element el, String childName) {
		String name = el.getFirstChild().getLocalName();
		if (name.equals(childName) || name.equals(childName.toUpperCase())) {
			Element comment = (Element) el.getFirstChild().cloneNode(true);
			parent.appendChild(comment);
		}
	}
	
	public static WebEngine allowConsoleLogViaJavaScript(WebEngine webEngine, WebPageInteractor webPageInteractor) {
		// Allow use of console.log and console.error in JS
		JSObject window = (JSObject) webEngine.executeScript("window");
		window.setMember("javaOut", System.out);
		window.setMember("javaErr", System.err);
		String loggingRedirectScript =
		    "console.log = function(message) { javaOut.println('[JS Log] ' + message); };\n" +
		    "console.error = function(message) { javaErr.println('[JS Error] ' + message); };";
		webEngine.executeScript(loggingRedirectScript);
		window.setMember("xLingDocApp", webPageInteractor);
		return webEngine;
	}


}
