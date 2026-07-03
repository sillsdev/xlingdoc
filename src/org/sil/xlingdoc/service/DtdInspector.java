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
import org.w3c.dom.Element;

import java.io.FileReader;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class DtdInspector {

	private DTDGrammar grammar;
	private PcDataElementCollector dtdHandler;
	private String pcDataIndicator = "(??)";

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

	/**
	 * Determines which elements can legally be inserted adjacent to the target
	 * node.
	 * @param  targetElement The element currently selected in the WebView
	 * @parm   manager
	 * @param  insertBefore True for "Insert After" context, False for "Insert Before"
	 * @return List of valid element tag names
	 */
	public SortedSet<String> getValidAdjacentElements(Element targetElement, XmlDocumentManager manager, boolean insertBefore) {
		SortedSet<String> validChoices = new TreeSet<>();
		// 1. Identify the parent context and current sibling sequence
		Element parentElement = (Element) targetElement.getParentNode();
		if (parentElement == null)
			return validChoices; // Root node has no siblings
		String parentName = XmlNameMapper.getMappedElementName(parentElement.getTagName());
		if (parentName.equals("summary")) {
			parentElement = (Element) parentElement.getParentNode();
			parentName = XmlNameMapper.getMappedElementName(parentElement.getTagName());
		}
		if (parentName.equals("details")) {
			parentElement = (Element) parentElement.getParentNode();
			parentName = XmlNameMapper.getMappedElementName(parentElement.getTagName());
		}
		String targetName = XmlNameMapper.getMappedElementName(targetElement.getTagName());
		Set<String> adjacents = DTDAdjacentAnalyzer.findAdjacentElements(
				grammar, parentName, targetName, insertBefore);
		if (dtdHandler.hasPcData(parentName)) {
			validChoices.add(pcDataIndicator);
		}
		for (String elem : adjacents) {
			if (manager.isValidInsertion(manager.getBuilder(), targetElement, elem, insertBefore)) {
				validChoices.add(elem);
			}
		}
//		System.out.println("\tresults size = " + validChoices.size());
//		System.out.println("\tElements allowed " + dir + " " + targetName + ": " + validChoices);
		return  validChoices;
	}

	public SortedSet<String> getValidInsertElements(Element targetElement, XmlDocumentManager manager) {
		SortedSet<String> validChoices = new TreeSet<>();
		if(targetElement == null) {
			return validChoices;
		}
		String targetName = XmlNameMapper.getMappedElementName(targetElement.getTagName());
		// Every case we have is stand alone #PCDATA or a choice so we just use the string representation.
		// This also means that we should only pass in the targetElement that has the cursor - i.e., is in n#PCDATA.
		int targetIndex = grammar.getElementDeclIndex(targetName);
		if (targetIndex == -1) {
			return validChoices;
		}
		String rep = grammar.getContentSpecAsString(targetIndex);
		if (!StringUtilities.isNullOrEmpty(rep)) {
			// remove any parentheses
			rep = rep.replace("(", "").replace(")", "");
			String[] items = rep.split("\\|");
			for (int i = 0; i < items.length; i++) {
				validChoices.add(items[i]);
			}
		}
		return validChoices;
	}

	public SortedSet<String> getValidReplaceElements(Element targetElement, XmlDocumentManager manager) {
		SortedSet<String> validChoices = new TreeSet<>();
		// 1. Identify the parent context and current sibling sequence
		Element parentElement = (Element) targetElement.getParentNode();
		if (parentElement == null)
			return validChoices; // Root node has no siblings
		String parentName = XmlNameMapper.getMappedElementName(parentElement.getTagName());
		if (parentName.equals("summary")) {
			parentElement = (Element) parentElement.getParentNode();
			parentName = XmlNameMapper.getMappedElementName(parentElement.getTagName());
		}
		if (parentName.equals("details")) {
			parentElement = (Element) parentElement.getParentNode();
			parentName = XmlNameMapper.getMappedElementName(parentElement.getTagName());
		}
		String targetName = XmlNameMapper.getMappedElementName(targetElement.getTagName());
		Set<String> replacers = DTDReplaceAnalyzer.findReplaceElements(
				grammar, parentName, targetName);
//		if (dtdHandler.hasPcData(parentName)) {
//			validChoices.add(pcDataIndicator);
//		}
//		validChoices.addAll(adjacents);
		for (String elem : replacers) {
			if (manager.isValidReplace(manager.getBuilder(), targetElement, elem)) {
				validChoices.add(elem);
			}
		}
//		System.out.println("\tresults size = " + validChoices.size());
//		System.out.println("\tElements allowed " + dir + " " + targetName + ": " + validChoices);

		
//		String targetName = XmlNameMapper.getMappedElementName(targetElement.getTagName());
//		int targetIndex = grammar.getElementDeclIndex(targetName);
//		String rep = grammar.getContentSpecAsString(targetIndex);
		return validChoices;
	}

	public DTDGrammar getGrammar() {
		return grammar;
	}

}
