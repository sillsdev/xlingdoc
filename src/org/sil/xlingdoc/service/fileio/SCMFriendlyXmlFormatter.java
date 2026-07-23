/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service.fileio;
/**
 * code drafted by Gemini
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class SCMFriendlyXmlFormatter {

	public static void saveWithCustomFormat(Document doc, File outputFile) throws Exception {
		StringBuilder sb = new StringBuilder();

		// Include standard XML declaration header
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		// DO DOCTYPE
		sb.append("<!DOCTYPE ").append("lingPaper")
				.append(" PUBLIC \"-//XMLmind//DTD XLingPap//EN\"\n\"XLingPap.dtd\">\n");

		// Start recursively walking the DOM tree from the root element
		serializeElement(doc.getDocumentElement(), sb);

		// Save string directly out to disk
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
			writer.write(sb.toString());
		}
	}

	private static void serializeElement(Element element, StringBuilder sb) {
		String tagName = element.getTagName();

		// 1. Open the tag name
		sb.append("<").append(tagName);

		// 2. Format attributes so they stack on new lines
		NamedNodeMap attributes = element.getAttributes();
		if (attributes.getLength() > 0) {
			sb.append("\n"); // Move down before attributes start
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attr = attributes.item(i);
				sb.append(attr.getNodeName()).append("=\"").append(attr.getNodeValue().replace("\"", "&quot;"))
						.append("\"");
				if (i < attributes.getLength() - 1) {
					sb.append("\n");
				}
			}
		}

		// 3. Close the opening tag on its own newline
		sb.append("\n>");

		// 4. Process children (Text nodes or sub-elements)
		NodeList children = element.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				serializeElement((Element) child, sb);
			} else if (child.getNodeType() == Node.TEXT_NODE) {
				// Directly append raw #PCDATA text contents without adding arbitrary wraps
				sb.append(child.getNodeValue());
			}
		}

		// 5. Build the closing tag matching your formatting block snippet
		// Example: </urlLinkLayout\n>
		sb.append("</").append(tagName).append("\n>");
	}
}