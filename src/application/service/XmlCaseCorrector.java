/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package application.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.HashMap;
import java.util.Map;

public class XmlCaseCorrector {

	// 1. Maintain a master index matching lowercased keys to CamelCase targets
	private static final Map<String, String> elementCaseMap = new HashMap<>();
	private static final Map<String, String> attributeCaseMap = new HashMap<>();

	static {
		// Element names end up being lower case because the WebView runs a WebKit
		// rendering engine, which strictly treats content as HTML5. Under the HTML5 specification,
		// tag and attribute names are treated as case-insensitive and are automatically
		// normalized to lowercase when you extract markup using .outerHTML.
		// So we need to map any camel-case names here.
		elementCaseMap.put("backmatter", "backMatter");
		elementCaseMap.put("exampleref", "exampleRef");
		elementCaseMap.put("frontmatter", "frontMatter");
		elementCaseMap.put("jpages", "jPages");
		elementCaseMap.put("jtitle", "jTitle");
		elementCaseMap.put("jvol", "jVol");
		elementCaseMap.put("langdata", "langData");
		elementCaseMap.put("linegroup", "lineGroup");
		elementCaseMap.put("lingpaper", "lingPaper");
		elementCaseMap.put("refauthor", "refAuthor");
		elementCaseMap.put("refdate", "refDate");
		elementCaseMap.put("reftitle", "refTitle");
		elementCaseMap.put("refwork", "refWork");
		elementCaseMap.put("sectitle", "secTitle");
		elementCaseMap.put("sectionref", "sectionRef");

		// Register your structural attribute names here
		attributeCaseMap.put("cssspecial", "cssSpecial");
		attributeCaseMap.put("xelatexspecial", "XeLaTeXSpecial");
		attributeCaseMap.put("xsl-fospecial", "xsl-foSpecial");
	}

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

			if (attributeCaseMap.containsKey(lowerName)) {
				String correctCaseName = attributeCaseMap.get(lowerName);
				String value = attr.getNodeValue();

				// Strip the old lowercased attribute and write the correctly cased version
				element.removeAttribute(attr.getNodeName());
				element.setAttribute(correctCaseName, value);
			}
		}

		// 5. Correct Tag/Element Casing
		String lowerTagName = element.getTagName().toLowerCase();
		if (elementCaseMap.containsKey(lowerTagName)) {
			String correctTagName = elementCaseMap.get(lowerTagName);

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

	// We can try and see if this will generate all the elements we need without
	// having to key them one by one.
	public static void populateMapFromDtd(org.w3c.dom.DocumentType doctype) {
		if (doctype == null)
			return;

		// Read entity declarations directly from your active schema definition
		// parameters
		org.w3c.dom.NamedNodeMap elements = doctype.getEntities();
		for (int i = 0; i < elements.getLength(); i++) {
			String originalName = elements.item(i).getNodeName();
			elementCaseMap.put(originalName.toLowerCase(), originalName);
		}
	}
}
