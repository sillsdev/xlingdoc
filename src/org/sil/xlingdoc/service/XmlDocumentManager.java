/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

/**
 * code drafted by Gemini and Leo
 */
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class XmlDocumentManager {
	private Document masterXmlDoc;
	private String dtdSystemId;
	private DocumentBuilder builder;
	int warningsCount = 0;
	int errorsCount = 0;
	int fatalErrorsCount = 0;
	final String kOpenWedge = "<";
	final String kEndingWedge = "</";
	final String kCloseWedge = ">";
	final String kClosingWedge = "/>";

	// TODO: flesh out all required entries where an element has required subelements
	Map<String,String> requiredSubElements = Map.ofEntries(
			Map.entry("appendix","<secTitle/><p/>"),
			Map.entry("frontMatter","<title/><author/>"),
			Map.entry("glossary","<p/>"),
			Map.entry("refAuthor","<refWork><refDate/><refTitle/></refWork>"),
			Map.entry("refWork","<refDate/><refTitle/>")
			);

	public Document getMasterXmlDoc() {
		return masterXmlDoc;
	}

	public String getDtdSystemId() {
		return dtdSystemId;
	}

	public DocumentBuilder getBuilder() {
		return builder;
	}

	public void loadXmlDocument(File xmlFile) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// Turn on DTD validation for the Java side
		factory.setValidating(true);
		factory.setNamespaceAware(true);

		builder = factory.newDocumentBuilder();

		// Set an error handler to catch DTD validation errors on load
		builder.setErrorHandler(new org.xml.sax.ErrorHandler() {
			public void warning(org.xml.sax.SAXParseException e) {
				System.out.println("Warning:\n" + buildExceptionMessage(e));
				warningsCount++;
			}

			public void error(org.xml.sax.SAXParseException e) {
//				System.out.println("Error:\n" + buildExceptionMessage(e));
				errorsCount++;
//            	throws org.xml.sax.SAXException { throw e; }
			}

			public void fatalError(org.xml.sax.SAXParseException e) {
				System.out.println("Fatal error:\n" + buildExceptionMessage(e));
				fatalErrorsCount++;
//            	throws org.xml.sax.SAXException { throw e; }
			}
		});

		// Parse your file into standard Java space
		this.masterXmlDoc = builder.parse(xmlFile);

		// Capture the DTD identifiers for later use
		DocumentType doctype = masterXmlDoc.getDoctype();
		if (doctype != null) {
			this.dtdSystemId = doctype.getSystemId(); // e.g., "XLingPaper.dtd"
		}
	}

	String buildExceptionMessage(SAXParseException e) {
		StringBuilder sb = new StringBuilder();
		sb.append(e.getMessage());
		sb.append(" At line ");
		sb.append(e.getLineNumber());
		sb.append(" at column ");
		sb.append(e.getColumnNumber());
		sb.append(".");
		// TODO: get the location so we can create a link for the user to click on to go
		// to the offending location.
//    	sb.append(".\n\t");
//    	try {
//			String fileContent = Files.readString(Paths.get(xmlFile.getAbsolutePath()));
//			Stream<String> lines = fileContent.lines();
//			sb.append(lines.toArray()[]);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		return sb.toString();
	}

	public String findParentName(Element element) {
//		System.out.println("findParentName on " + element.getNodeName());
		String parentName = "";
		if (element.getParentNode() instanceof Element parent) {
			parentName = XmlNameMapper.getMappedElementName(parent.getNodeName().toLowerCase());
			while ("details".equals(parentName) || "summary".equals(parentName)) {
				parent = (Element) parent.getParentNode();
				parentName = XmlNameMapper.getMappedElementName(parent.getNodeName().toLowerCase());
			}
//			System.out.println("\t" + parentName);
		}
		return parentName;
	}

	public String findSiblingName(Element element, boolean isPrevious) {
		String siblingName = null;
		Node immediateSibling;
		if (isPrevious) {
			immediateSibling = element.getPreviousSibling();
		} else {
			immediateSibling = element.getNextSibling();
		}
		if (immediateSibling instanceof Element sibling) {
			siblingName = XmlNameMapper.getMappedElementName(sibling.getNodeName().toLowerCase());
			if ("summary".equals(siblingName)) {
				sibling = (Element)sibling.getFirstChild();
				siblingName = XmlNameMapper.getMappedElementName(sibling.getNodeName().toLowerCase());
			}
		}
		return siblingName;
	}

	public boolean isValidInsertion(DocumentBuilder builder, Element targetElement, String candidateName, boolean insertBefore) {
		String parentName = findParentName(targetElement);
		String targetName = XmlNameMapper.getMappedElementName(targetElement.getNodeName().toLowerCase());
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE ").append(parentName).append(" SYSTEM \"").append("resources/dtdsElementSequences/XLingPap.dtd").append("\">");
		sb.append(kOpenWedge).append(parentName).append(">");
		// include all preceding siblings since one or more may be required.
		includePrecedingSiblingNames(targetElement, sb);
		if (insertBefore) {
			appendElementName(candidateName, sb);
		}
		appendElementName(targetName, sb);
		if (!insertBefore) {
			appendElementName(candidateName, sb);
		}
		// include all following siblings since one or more may be required.
		includeFollowingSiblingNames(targetElement, sb);
		sb.append("</").append(parentName).append(">");
		InputStream is = new ByteArrayInputStream(sb.toString().getBytes() );
		try {
			warningsCount = 0;
			errorsCount = 0;
			fatalErrorsCount = 0;
			builder.parse(is);
			if (errorsCount > 0) {
//				System.out.println("\t" + sb.toString());
				return false;
			}
			return true; // Validation passed
		} catch (Exception e) {
			// We actually never get here; any exceptions are caught in loadXmlDocument().
			return false; // Validation failed
		}
	}

	protected void appendElementName(String elementName, StringBuilder sb) {
//		System.out.println("\t\tappend looking for '" + elementName + "'");
		if (requiredSubElements.containsKey(elementName)) {
			sb.append(kOpenWedge).append(elementName).append(kCloseWedge);
			sb.append(requiredSubElements.get(elementName));
			sb.append(kEndingWedge).append(elementName).append(kCloseWedge);
		} else {
			sb.append(kOpenWedge).append(elementName).append(kClosingWedge);
		}
	}

	protected void includeFollowingSiblingNames(Element targetElement, StringBuilder sb) {
		String siblingName;
		Node node;
		List<String> followingNames = new ArrayList<String>();
		node = targetElement.getNextSibling();
		while (node != null) {
			if (node instanceof Element el) {
				siblingName = XmlNameMapper.getMappedElementName(node.getNodeName().toLowerCase());
				if ("summary".equals(siblingName)) {
					Element sibling = (Element)node.getFirstChild();
					siblingName = XmlNameMapper.getMappedElementName(sibling.getNodeName().toLowerCase());
				}
				followingNames.add(siblingName);
			}
			node = node.getNextSibling();
		}
		for (String name : followingNames) {
			appendElementName(name, sb);
//			sb.append(kOpenWedge).append(name).append(kClosingWedge);
		}
	}

	protected void includePrecedingSiblingNames(Element targetElement, StringBuilder sb) {
		String siblingName;
		List<String> precedingNames = new ArrayList<String>();
		Node node = targetElement.getPreviousSibling();
		while (node != null) {
			if (node instanceof Element el) {
				siblingName = XmlNameMapper.getMappedElementName(node.getNodeName().toLowerCase());
				if ("summary".equals(siblingName)) {
					Element sibling = (Element)node.getFirstChild();
					siblingName = XmlNameMapper.getMappedElementName(sibling.getNodeName().toLowerCase());
				}
				precedingNames.add(siblingName);
			}
			node = node.getPreviousSibling();
		}
		ListIterator<String> prevIter = precedingNames.listIterator(precedingNames.size());
		while (prevIter.hasPrevious()) {
			appendElementName(prevIter.previous(), sb);
		}
	}
}