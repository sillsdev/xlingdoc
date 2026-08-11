/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.xlingdoc.service.fileio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.sil.xlingdoc.service.dtdhandling.XmlNameMapper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Some code adapted based on suggestions by Gemini
 */
public class XLingDocSaver {
	
	public static void saveXLingDoc(Document doc, File outputFile) throws Exception {
		StringBuilder sb = new StringBuilder();
		NodeList rootElements = doc.getElementsByTagName("xlingpaper");
		if (rootElements.getLength() == 0) {
			rootElements = doc.getElementsByTagName("LINGPAPER");
		}
		Element root = (Element) rootElements.item(0);
//		Element root = doc.getDocumentElement();
//		XmlNameMapper.sanitizeAndFixCasing(root, doc);
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<!DOCTYPE ").append("lingPaper")
				.append(" PUBLIC \"-//XMLmind//DTD XLingPap//EN\"\n\"../../resources/dtds/XLingPap.dtd\">\n");
		serializeElement(root, sb);
//		serializeElement(doc.getDocumentElement(), sb);
		sb.append("\n");  // add extra nl at end
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
			writer.write(sb.toString());
		}
	}

	private static void serializeElement(Element element, StringBuilder sb) {
		if (isXInclude(element, sb)) {
			return;
		}
		String tagName = XmlNameMapper.mapElementName(element.getTagName());
//		System.out.println("element = '" + tagName + "'");
		if (tagName.startsWith("xlp-")) {
			tagName = tagName.substring(4);
		}
		boolean use = !tagName.equals("details") && !tagName.equals("summary");
		if (use)
		{
			sb.append("<").append(tagName);
			NamedNodeMap attributes = element.getAttributes();
			if (attributes.getLength() > 0) {
				sb.append("\n"); // Move down before attributes start
				for (int i = 0; i < attributes.getLength(); i++) {
					Node attr = attributes.item(i);
//					System.out.println("\t\t\tattr name = '" + attr.getNodeName());
					String attrName = XmlNameMapper.mapAttributeName(attr.getNodeName());
//					System.out.println("\t\t\tattr name = '" + attrName);
					sb.append(attrName).append("=\"").append(attr.getNodeValue().replace("\"", "&quot;"))
							.append("\"");
					if (i < attributes.getLength() - 1) {
						sb.append("\n");
					}
				}
			}
			sb.append("\n>");
		}
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				serializeElement((Element) child, sb);
			} else if (child.getNodeType() == Node.TEXT_NODE) {
				if (!tagName.equals("summary")) {
					// Directly append raw #PCDATA text contents without adding arbitrary wraps
					sb.append(child.getNodeValue());
				}
			}
		}
		if (use) {
			sb.append("</").append(tagName).append("\n>");
		}
	}

	private static boolean isXInclude(Element element, StringBuilder sb) {
		Attr xinclude = element.getAttributeNode("xml:base");
		if (xinclude != null) {
			sb.append("<xi:include\nhref=\"");
			sb.append(xinclude.getValue());
			sb.append("\"\nxpointer=\"element(/1)\"\n");
			sb.append("xmlns:xi=\"http://www.w3.org/2001/XInclude\"\n");
			sb.append("></xi:include\n>");
			return true;
		}
		return false;
	}
}
