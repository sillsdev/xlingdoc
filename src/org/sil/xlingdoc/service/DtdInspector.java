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
		Element parent = (Element) targetElement.getParentNode();
		if (parent == null)
			return validChoices; // Root node has no siblings
		String parentTag = XmlNameMapper.getMappedElementName(parent.getTagName());
		if (parentTag.equals("summary")) {
			parent = (Element) parent.getParentNode();
			parentTag = XmlNameMapper.getMappedElementName(parent.getTagName());
		}
		if (parentTag.equals("details")) {
			parent = (Element) parent.getParentNode();
			parentTag = XmlNameMapper.getMappedElementName(parent.getTagName());
		}
		String targetName = XmlNameMapper.getMappedElementName(targetElement.getTagName());
		Set<String> adjacents = DTDAdjacentAnalyzer.findAdjacentElements(
				grammar, parentTag, targetName, insertBefore);
//		String dir = insertBefore? "before" : "after";
//		System.out.println("size = " + adjacents.size());
//		System.out.println("Unsorted elements allowed " + dir + " " + targetName + ": " + adjacents);
//		dtdHandler.dumpContentElements();
		if (dtdHandler.hasPcData(parentTag)) {
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

	public DTDGrammar getGrammar() {
		return grammar;
	}

}
