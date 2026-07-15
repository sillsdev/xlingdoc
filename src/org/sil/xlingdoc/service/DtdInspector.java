/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

/**
 * code drafted by Leo
 */
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.impl.dtd.XMLDTDLoader;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.sil.utility.StringUtilities;
import org.sil.xlingdoc.Constants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;

public class DtdInspector {

	private DTDGrammar grammar;
	private PcDataElementCollector dtdHandler;
	private String pcDataIndicator = "(??)";
	private final String kSplitRegExpr = "[,+?\\|\\*\\(\\)]";

	final String kOpenWedge = "<";
	final String kEndingOpenWedge = "</";
	final String kCloseWedge = ">";
	final String kEndingCloseWedge = "/>";

	// TODO: flesh out all required entries where an element has required subelements
	Map<String,String> requiredSubElements = Map.ofEntries(
			Map.entry("abstract","<p/>"),
			Map.entry("acknowledgements","<p/>"),
			Map.entry("annotatedBibliographyTypes","<annotatedBibliographyType/>"),
			Map.entry("appendix","<secTitle/><p/>"),
			Map.entry("contentControl","<contentTypes><contentType/></contentTypes><contentControlChoices><contentControlChoice/></contentControlChoices>"),
			Map.entry("example","<chart/>"),
			Map.entry("framedTypes","<framedType/>"),
			Map.entry("framedUnit","<p/>"),
			Map.entry("frontMatter","<title/><author/>"),
			Map.entry("glossary","<p/>"),
			Map.entry("interlinear","<free/>"),
			Map.entry("labelContentChoices","<labelContent/><labelContent/>"),
			Map.entry("landscape","<p/>"),
			Map.entry("languages","<language/>"),
			Map.entry("listDefinition","<definition/>"),
			Map.entry("listInterlinear","<free/>"),
			Map.entry("listSingle","<langData/>"),
			Map.entry("listWord","<langData/>"),
			Map.entry("preface","<p/>"),
			Map.entry("refAuthor","<refWork><refDate/><refTitle/><ms><empty/></ms></refWork>"),
			Map.entry("referencedInterlinearTexts","<referencedInterlinearText><interlinear-text/></referencedInterlinearText>"),
			Map.entry("refWork","<refDate/><refTitle/><ms><empty/></ms>"),
			Map.entry("selectedBibliography","<citation/>"),
			Map.entry("single","<langData/>"),
			Map.entry("word","<langData/>")
			);

	public DtdInspector(String dtdPath, String pcDataIndicator) {
		this.pcDataIndicator = pcDataIndicator;
		try {
			SymbolTable symbolTable = new SymbolTable();
			XMLDTDLoader dtdLoader = new XMLDTDLoader(symbolTable);
			dtdHandler = new PcDataElementCollector();
	        dtdLoader.setDTDContentModelHandler(dtdHandler);
			XMLInputSource source = new XMLInputSource(null, dtdPath, null, new FileReader(dtdPath), null);
			this.grammar = (DTDGrammar) dtdLoader.loadGrammar(source);
		} catch (Exception e) {
			System.err.println("CRITICAL: Failed to initialize DTD Grammar pool: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public SortedSet<String> getValidAdjacentElements(Element targetElement, XmlDocumentManager manager, boolean insertBefore) {
		SortedSet<String> validChoices = new TreeSet<>();
		Element parentElement = (Element) targetElement.getParentNode();
		if (parentElement == null)
			return validChoices;
		String parentName = findParentNameToUse(parentElement);
		int parentIndex = grammar.getElementDeclIndex(parentName);
		if (parentIndex == -1) {
			return validChoices;
		}
		if (dtdHandler.hasPcData(parentName)) {
			validChoices.add(pcDataIndicator);
		}
		String rep = grammar.getContentSpecAsString(parentIndex);
//		System.out.println("rep = " + rep);
		if (!StringUtilities.isNullOrEmpty(rep)) {
			StringBuilder sbBefore = new StringBuilder();
			buildDoctype(parentName, sbBefore);
			// include all preceding siblings since one or more may be required.
			includePrecedingSiblingNames(targetElement, sbBefore);
			StringBuilder sbAfter = new StringBuilder();
			// include all following siblings since one or more may be required.
			includeFollowingSiblingNames(targetElement, sbAfter);
			buildFinalElement(parentName, sbAfter);
			String[] items = rep.split(kSplitRegExpr);
			for (int i = 0; i < items.length; i++) {
				if (!StringUtilities.isNullOrEmpty(items[i])) {
					if (isValidInsertion(manager, targetElement, sbBefore.toString(), items[i], sbAfter.toString(), insertBefore)) {
						validChoices.add(items[i]);
					}
				}
			}
		}
		return  validChoices;
	}

	protected void buildFinalElement(String parentName, StringBuilder sbAfter) {
		sbAfter.append("</").append(parentName).append(">");
	}

	protected void buildDoctype(String parentName, StringBuilder sbBefore) {
		sbBefore.append("<!DOCTYPE ").append(parentName).append(" SYSTEM \"").append(Constants.ELEMENT_ONLY_DTD_LOCATION).append("\">");
		sbBefore.append(kOpenWedge).append(parentName).append(">");
	}

	public SortedSet<String> getValidInsertElements(Element targetElement, XmlDocumentManager manager) {
		SortedSet<String> validChoices = new TreeSet<>();
		if(targetElement == null) {
			return validChoices;
		}
		String targetName = XmlNameMapper.getMappedElementName(targetElement.getTagName());
		int targetIndex = grammar.getElementDeclIndex(targetName);
		if (targetIndex == -1) {
			return validChoices;
		}
		String rep = grammar.getContentSpecAsString(targetIndex);
		if (!StringUtilities.isNullOrEmpty(rep)) {
			String[] items = rep.split(kSplitRegExpr);
			for (int i = 0; i < items.length; i++) {
				if (!StringUtilities.isNullOrEmpty(items[i])) {
					validChoices.add(items[i]);
				}
			}
		}
		return validChoices;
	}

	public SortedSet<String> getValidReplaceElements(Element targetElement, XmlDocumentManager manager) {
		SortedSet<String> validChoices = new TreeSet<>();
		Element parentElement = (Element) targetElement.getParentNode();
		if (parentElement == null)
			return validChoices; // Root node has no siblings
		String parentName = findParentNameToUse(parentElement);
		int parentIndex = grammar.getElementDeclIndex(parentName);
		if (parentIndex == -1) {
			return validChoices;
		}
		String rep = grammar.getContentSpecAsString(parentIndex);
		if (!StringUtilities.isNullOrEmpty(rep)) {
			StringBuilder sbBefore = new StringBuilder();
			buildDoctype(parentName, sbBefore);
			// include all preceding siblings since one or more may be required.
			includePrecedingSiblingNames(targetElement, sbBefore);
			StringBuilder sbAfter = new StringBuilder();
			// include all following siblings since one or more may be required.
			includeFollowingSiblingNames(targetElement, sbAfter);
			buildFinalElement(parentName, sbAfter);
			String[] items = rep.split(kSplitRegExpr);
			for (int i = 0; i < items.length; i++) {
				if (!StringUtilities.isNullOrEmpty(items[i])) {
					if (isValidReplace(manager, sbBefore.toString(), items[i], sbAfter.toString())) {
						validChoices.add(items[i]);
					}
				}
			}
		}
		return validChoices;
	}

	public SortedSet<String> getValidConvertElements(Element targetElement, XmlDocumentManager manager) {
		SortedSet<String> validChoices = new TreeSet<>();
		Element parentElement = (Element) targetElement.getParentNode();
		if (parentElement == null)
			return validChoices; // Root node has no siblings
		String parentName = findParentNameToUse(parentElement);
		int parentIndex = grammar.getElementDeclIndex(parentName);
		if (parentIndex == -1) {
			return validChoices;
		}
		String rep = grammar.getContentSpecAsString(parentIndex);
		if (!StringUtilities.isNullOrEmpty(rep)) {
			StringBuilder sbBefore = new StringBuilder();
			buildDoctype(parentName, sbBefore);
			// include all preceding siblings since one or more may be required.
			includePrecedingSiblingNames(targetElement, sbBefore);
			StringBuilder sbAfter = new StringBuilder();
			// include all following siblings since one or more may be required.
			includeFollowingSiblingNames(targetElement, sbAfter);
			buildFinalElement(parentName, sbAfter);
			String targetName = targetElement.getLocalName();
			String[] items = rep.split(kSplitRegExpr);
			for (int i = 0; i < items.length; i++) {
				if (!StringUtilities.isNullOrEmpty(items[i])) {
					StringBuilder sbCandidateConstruct = new StringBuilder();
					String candidateName = items[i];
					if (!candidateName.equals(targetName)) {
						sbCandidateConstruct.append(kOpenWedge).append(candidateName).append(kCloseWedge);
						for (int iChild = 0; iChild < targetElement.getChildNodes().getLength(); iChild++) {
							Node n = targetElement.getChildNodes().item(iChild);
							if (n instanceof Text t) {
								sbCandidateConstruct.append("t");
							} else if (n instanceof Element el) {
								String childName = el.getLocalName();
								appendElementName(childName, sbCandidateConstruct);
							}
						}
						sbCandidateConstruct.append(kEndingOpenWedge).append(candidateName).append(kCloseWedge);
						if (isValidConvert(manager, sbBefore.toString(), sbCandidateConstruct.toString(), sbAfter.toString())) {
							validChoices.add(items[i]);
						}
					}
				}
			}
		}
		return validChoices;
	}

	public SortedSet<String> getValidConvertWrapElements(Element targetElement, XmlDocumentManager manager) {
		SortedSet<String> validChoices = new TreeSet<>();
		Element parentElement = (Element) targetElement.getParentNode();
		if (parentElement == null)
			return validChoices; // Root node has no siblings
		String parentName = findParentNameToUse(parentElement);
		int parentIndex = grammar.getElementDeclIndex(parentName);
		if (parentIndex == -1) {
			return validChoices;
		}
		String rep = grammar.getContentSpecAsString(parentIndex);
		if (!StringUtilities.isNullOrEmpty(rep)) {
			StringBuilder sbBefore = new StringBuilder();
			buildDoctype(parentName, sbBefore);
			// include all preceding siblings since one or more may be required.
			includePrecedingSiblingNames(targetElement, sbBefore);
			StringBuilder sbAfter = new StringBuilder();
			// include all following siblings since one or more may be required.
			includeFollowingSiblingNames(targetElement, sbAfter);
			buildFinalElement(parentName, sbAfter);
			String targetName = targetElement.getLocalName();
			String[] items = rep.split(kSplitRegExpr);
			for (int i = 0; i < items.length; i++) {
				if (!StringUtilities.isNullOrEmpty(items[i])) {
					StringBuilder sbCandidateConstruct = new StringBuilder();
					String candidateName = items[i];
					if (!candidateName.equals(targetName)) {
						sbCandidateConstruct.append(kOpenWedge).append(candidateName).append(kCloseWedge);
						sbCandidateConstruct.append(kOpenWedge).append(targetName).append(kCloseWedge);
						for (int iChild = 0; iChild < targetElement.getChildNodes().getLength(); iChild++) {
							Node n = targetElement.getChildNodes().item(iChild);
							if (n instanceof Text t) {
								sbCandidateConstruct.append("t");
							} else if (n instanceof Element el) {
								String childName = el.getLocalName();
								appendElementName(childName, sbCandidateConstruct);
							}
						}
						sbCandidateConstruct.append(kEndingOpenWedge).append(targetName).append(kCloseWedge);
						sbCandidateConstruct.append(kEndingOpenWedge).append(candidateName).append(kCloseWedge);
						if (isValidConvert(manager, sbBefore.toString(), sbCandidateConstruct.toString(), sbAfter.toString())) {
							validChoices.add(items[i]);
						}
					}
				}
			}
		}
		return validChoices;
	}

	public DTDGrammar getGrammar() {
		return grammar;
	}

	public boolean isValidConvert(XmlDocumentManager manager, String sBefore, String candidateConstruct, String sAfter) {
		DocumentBuilder builder = manager.getBuilder();
		StringBuilder sb = new StringBuilder();
		sb.append(sBefore);
		sb.append(candidateConstruct);
		sb.append(sAfter);
//		System.out.println(sb.toString());
		return parseXmlSnippet(manager, builder, sb);
	}

	public boolean isValidInsertion(XmlDocumentManager manager, Element targetElement, String sBefore, String candidateName, String sAfter, boolean insertBefore) {
		DocumentBuilder builder = manager.getBuilder();
		StringBuilder sb = new StringBuilder();
		sb.append(sBefore);
		String targetName = XmlNameMapper.getMappedElementName(targetElement.getNodeName());
		if (insertBefore) {
			appendElementName(candidateName, sb);
		}
		appendElementName(targetName, sb);
		if (!insertBefore) {
			appendElementName(candidateName, sb);
		}
		sb.append(sAfter);
		return parseXmlSnippet(manager, builder, sb);
	}

	public boolean isValidReplace(XmlDocumentManager manager, String sBefore, String candidateName, String sAfter) {
		DocumentBuilder builder = manager.getBuilder();
		StringBuilder sb = new StringBuilder();
		sb.append(sBefore);
		appendElementName(candidateName, sb);
		sb.append(sAfter);
//		System.out.println(sb.toString());
		return parseXmlSnippet(manager, builder, sb);
	}

	protected boolean parseXmlSnippet(XmlDocumentManager manager, DocumentBuilder builder, StringBuilder sb) {
		InputStream is = new ByteArrayInputStream(sb.toString().getBytes() );
		try {
			manager.resetCounters();
			builder.parse(is);
			if (manager.getErrorsCount() > 0) {
//				System.out.println("\t" + sb.toString());
				return false;
			}
			return true; // Validation passed
		} catch (Exception e) {
			// We actually never get here; any exceptions are caught in loadXmlDocument().
			return false; // Validation failed
		}
	}

	protected String findParentNameToUse(Element parentElement) {
		String parentName;
		parentName = XmlNameMapper.getMappedElementName(parentElement.getTagName());
		if (parentName.equals("summary")) {
			parentElement = (Element) parentElement.getParentNode();
			parentName = XmlNameMapper.getMappedElementName(parentElement.getTagName());
		}
		if (parentName.equals("details")) {
			parentElement = (Element) parentElement.getParentNode();
			parentName = XmlNameMapper.getMappedElementName(parentElement.getTagName());
		}
		return parentName;
	}

	public String findParentName(Element element) {
//		System.out.println("findParentName on " + element.getNodeName());
		String parentName = "";
		if (element.getParentNode() instanceof Element parent) {
			parentName = XmlNameMapper.getMappedElementName(parent.getNodeName());
			while ("details".equals(parentName) || "summary".equals(parentName)) {
				parent = (Element) parent.getParentNode();
				parentName = XmlNameMapper.getMappedElementName(parent.getNodeName());
			}
//			System.out.println("\t" + parentName);
		}
		return parentName;
	}

	protected void appendElementName(String elementName, StringBuilder sb) {
		if (requiredSubElements.containsKey(elementName)) {
			sb.append(kOpenWedge).append(elementName).append(kCloseWedge);
			sb.append(requiredSubElements.get(elementName));
			sb.append(kEndingOpenWedge).append(elementName).append(kCloseWedge);
		} else {
			sb.append(kOpenWedge).append(elementName).append(kEndingCloseWedge);
		}
	}

	protected void includeFollowingSiblingNames(Element targetElement, StringBuilder sb) {
		String siblingName;
		Node node;
		List<String> followingNames = new ArrayList<String>();
		node = targetElement.getNextSibling();
		while (node != null) {
			if (node instanceof Element el) {
				siblingName = XmlNameMapper.getMappedElementName(node.getNodeName());
				if ("summary".equals(siblingName)) {
					Element sibling = (Element)node.getFirstChild();
					siblingName = XmlNameMapper.getMappedElementName(sibling.getNodeName());
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
				siblingName = XmlNameMapper.getMappedElementName(node.getNodeName());
				if ("summary".equals(siblingName)) {
					Element sibling = (Element)node.getFirstChild();
					siblingName = XmlNameMapper.getMappedElementName(sibling.getNodeName());
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
