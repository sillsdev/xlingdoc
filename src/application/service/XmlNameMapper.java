/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package application.service;
/**
 * code drafted by Gemini and Leo
 */

import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.impl.dtd.XMLAttributeDecl;
import org.apache.xerces.impl.dtd.XMLElementDecl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.HashMap;
import java.util.Map;

public class XmlNameMapper {

	// 1. Maintain a master index matching lowercased keys to CamelCase targets
	private static final Map<String, String> elementNameMap = new HashMap<>();
	private static final Map<String, String> attributeNameMap = new HashMap<>();
	final static String kDetailsSummaryBegin = "<details><summary>";
	final static String kSummaryEnd = "</summary";
	final static String kDetailsEnd = "</details>";

		// Element names end up being lower case because the WebView runs a WebKit
		// rendering engine, which strictly treats content as HTML5. Under the HTML5 specification,
		// tag and attribute names are treated as case-insensitive and are automatically
		// normalized to lower case when you extract mark-up using .outerHTML.
		// So we need to map any camel-case names.

	public static void sanitizeAndFixCasing(Element element, Document doc) {
		// 2. Clear out contenteditable fields as before
		element.removeAttribute("contenteditable");

		// 3. Process children recursively FIRST to keep references stable
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				sanitizeAndFixCasing((Element) child, doc);
			}
		}

		// 4. Correct Attribute Casing
		// Standard DOM maps don't let you rename attributes, so you replace them
		org.w3c.dom.NamedNodeMap attributes = element.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attr = attributes.item(i);
			String lowerName = attr.getNodeName().toLowerCase();

			if (attributeNameMap.containsKey(lowerName)) {
				String correctCaseName = attributeNameMap.get(lowerName);
				String value = attr.getNodeValue();

				// Strip the old lowercased attribute and write the correctly cased version
				element.removeAttribute(attr.getNodeName());
				element.setAttribute(correctCaseName, value);
			}
		}

		// 5. Correct Tag/Element Casing
		String lowerTagName = element.getTagName().toLowerCase();
		if (elementNameMap.containsKey(lowerTagName)) {
			String correctTagName = elementNameMap.get(lowerTagName);

			if (!element.getTagName().equals(correctTagName)) {
				// Rename the node by creating a replacement element shell
				Element renamedElement = doc.createElement(correctTagName);

				// Migrate attributes
				while (element.getAttributes().getLength() > 0) {
					Node attr = element.getAttributes().item(0);
					element.getAttributes().removeNamedItem(attr.getNodeName());
					renamedElement.setAttributeNode((org.w3c.dom.Attr) attr);
				}

				// Migrate children
				while (element.hasChildNodes()) {
					renamedElement.appendChild(element.getFirstChild());
				}

				// Swap the old lowercased placeholder shell out of the tree structure
				element.getParentNode().replaceChild(renamedElement, element);
			}
		}
	}

	public static String mapInputFromXLingPaperToHTML(String fileContent) {
		fileContent = mapElementName(fileContent, "br");
		// We may need to do something specific for tables...
//		fileContent = mapElementName(fileContent, "table");
//		fileContent = mapElementName(fileContent, "td");
//		fileContent = mapElementName(fileContent, "th");
//		fileContent = mapElementName(fileContent, "tr");
		fileContent = insertWrapping(fileContent);
		return fileContent;
	}

	static String mapElementName(String fileContent, String elementName) {
		fileContent = fileContent.replaceAll("<" + elementName, "<xlp-" + elementName);
		fileContent = fileContent.replaceAll("</" + elementName, "</xlp-" + elementName);
		return fileContent;
	}

	static String insertWrapping(String fileContent) {
		fileContent = fileContent.replaceAll("<secTitle", kDetailsSummaryBegin + "<secTitle");
		fileContent = fileContent.replaceAll("</secTitle", "</secTitle>" + kSummaryEnd);
		fileContent = fileContent.replaceAll("</section1", kDetailsEnd + "</section1");

		// TODO: be sure to use localized value for the wrap summary string
		fileContent = wrapElement(fileContent, "languages", "Languages");
		fileContent = wrapElement(fileContent, "types", "Types");

		return fileContent;
	}

	protected static String wrapElement(String fileContent, String elementToWrap, String wrapSummary) {
		fileContent = fileContent.replace("<" + elementToWrap, "<" + elementToWrap + ">" + kDetailsSummaryBegin + wrapSummary + kSummaryEnd);
		fileContent = fileContent.replace("</" + elementToWrap, kDetailsEnd + "</" + elementToWrap);
		return fileContent;
	}

	public static void populateMapsFromDtd(DTDGrammar dtdGrammar) {
		elementNameMap.clear();
		attributeNameMap.clear();

		if (dtdGrammar == null)
			return;
		XMLElementDecl xed = new XMLElementDecl();
		int elementIndex = dtdGrammar.getFirstElementDeclIndex();
		while (elementIndex != -1) {
			dtdGrammar.getElementDecl(elementIndex, xed);
			String elementRealName = xed.name.rawname;
			elementNameMap.put(elementRealName.toLowerCase(), elementRealName);
			XMLAttributeDecl xad = new XMLAttributeDecl();
			int attrIndex = dtdGrammar.getFirstAttributeDeclIndex(elementIndex);
			while (attrIndex != -1) {
				dtdGrammar.getAttributeDecl(attrIndex, xad);
				String attrRealName = xad.name.rawname;
				if (!attributeNameMap.containsValue(attrRealName)) {
					attributeNameMap.put(attrRealName.toLowerCase(), attrRealName);
				}
				attrIndex = dtdGrammar.getNextAttributeDeclIndex(attrIndex);
			}
			elementIndex = dtdGrammar.getNextElementDeclIndex(elementIndex);
		}
		// exceptions
		elementNameMap.put("xlp-br", "br");
	}

	public static String getMappedElementName(String lowercaseName) {
		if (elementNameMap.containsKey(lowercaseName)) {
			return elementNameMap.get(lowercaseName);
		} else {
			return lowercaseName;
		}
	}
}
