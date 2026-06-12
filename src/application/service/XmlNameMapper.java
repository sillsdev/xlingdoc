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

public class XmlNameMapper {

	// 1. Maintain a master index matching lowercased keys to CamelCase targets
	private static final Map<String, String> elementNameMap = new HashMap<>();
	private static final Map<String, String> attributeNameMap = new HashMap<>();
	final static String kDetailsSummaryBegin = "<details><summary>";
	final static String kSummaryEnd = "</summary";
	final static String kDetailsEnd = "</details>";

	static {
		// Element names end up being lower case because the WebView runs a WebKit
		// rendering engine, which strictly treats content as HTML5. Under the HTML5 specification,
		// tag and attribute names are treated as case-insensitive and are automatically
		// normalized to lowercase when you extract markup using .outerHTML.
		// So we need to map any camel-case names here.
		elementNameMap.put("backmatter", "backMatter");
		elementNameMap.put("exampleref", "exampleRef");
		elementNameMap.put("frontmatter", "frontMatter");
		elementNameMap.put("jpages", "jPages");
		elementNameMap.put("jtitle", "jTitle");
		elementNameMap.put("jvol", "jVol");
		elementNameMap.put("langdata", "langData");
		elementNameMap.put("linegroup", "lineGroup");
		elementNameMap.put("lingpaper", "lingPaper");
		elementNameMap.put("refauthor", "refAuthor");
		elementNameMap.put("refdate", "refDate");
		elementNameMap.put("reftitle", "refTitle");
		elementNameMap.put("refwork", "refWork");
		elementNameMap.put("sectitle", "secTitle");
		elementNameMap.put("sectionref", "sectionRef");
		elementNameMap.put("xlp-br", "br");
		// We may need to do something specific for tables...
//		elementNameMap.put("xlp-table", "table");
//		elementNameMap.put("xlp-td", "td");
//		elementNameMap.put("xlp-th", "th");
//		elementNameMap.put("xlp-tr", "tr");

		// Register your structural attribute names here
		attributeNameMap.put("cssspecial", "cssSpecial");
		attributeNameMap.put("xelatexspecial", "XeLaTeXSpecial");
		attributeNameMap.put("xsl-fospecial", "xsl-foSpecial");
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
			elementNameMap.put(originalName.toLowerCase(), originalName);
		}
	}
}
