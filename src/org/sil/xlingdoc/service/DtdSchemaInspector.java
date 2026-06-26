/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

/**
 * code drafted by Leo
 */
import org.apache.xerces.impl.dtd.XMLElementDecl;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.impl.dtd.XMLContentSpec;
import org.apache.xerces.impl.dtd.XMLDTDLoader;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.w3c.dom.Element;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class DtdSchemaInspector {

	private DTDGrammar grammar;

	public DtdSchemaInspector(String dtdPath) {
		try {
			// 1. Xerces loaders require a shared SymbolTable to track string allocations
			// safely
			SymbolTable symbolTable = new SymbolTable();

			// 2. Instantiate the correct component loader
			XMLDTDLoader dtdLoader = new XMLDTDLoader(symbolTable);

			// 3. Prepare the input stream source pointing to your target schema file
			XMLInputSource source = new XMLInputSource(null, dtdPath, null, new FileReader(dtdPath), null);

			// 4. Parse and capture the populated Grammar object reference safely
			this.grammar = (DTDGrammar) dtdLoader.loadGrammar(source);

//            System.out.println("Successfully parsed and loaded DTD grammar elements!");

		} catch (Exception e) {
			System.err.println("CRITICAL: Failed to initialize DTD Grammar pool: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Determines which elements can legally be inserted adjacent to the target
	 * node. * @param targetElement The element currently selected in the WebView
	 * 
	 * @param insertBefore True for "Insert After" context, False for "Insert Before"
	 * @return List of valid element tag names
	 */
	public SortedSet<String> getValidAdjacentElements(Element targetElement, XmlDocumentManager manager, boolean insertBefore) {
		SortedSet<String> validChoices = new TreeSet<>();

		// 1. Identify the parent context and current sibling sequence
		Element parent = (Element) targetElement.getParentNode();
		if (parent == null)
			return validChoices; // Root node has no siblings

		String parentTag = XmlNameMapper.getMappedElementName(parent.getTagName().toLowerCase());
		if (parentTag.equals("summary")) {
			parent = (Element) parent.getParentNode();
			parentTag = XmlNameMapper.getMappedElementName(parent.getTagName().toLowerCase());
		}
		if (parentTag.equals("details")) {
			parent = (Element) parent.getParentNode();
			parentTag = XmlNameMapper.getMappedElementName(parent.getTagName().toLowerCase());
		}
		String targetName = XmlNameMapper.getMappedElementName(targetElement.getTagName().toLowerCase());
		Set<String> predecessors = DTDPrecedenceAnalyzer.findPrecedingElements(
				grammar, parentTag, targetName, insertBefore);


		String dir = insertBefore? "before" : "after";
		System.out.println("size = " + predecessors.size());
		System.out.println("Elements allowed " + dir + " " + targetName + ": " + predecessors);
		for (String elem : predecessors) {
			if (manager.isValidInsertion(manager.getBuilder(), targetElement, elem, insertBefore)) {
				validChoices.add(elem);
			}
		}
		System.out.println("results size = " + validChoices.size());
		System.out.println("Elements allowed " + dir + " " + targetName + ": " + validChoices);
		return  validChoices;
	}

	public DTDGrammar getGrammar() {
		return grammar;
	}

	// 2. Fetch all possible elements that are allowed anywhere inside this parent
	private List<String> getAllowedChildrenForParent(String parentTag) {
		List<String> children = new ArrayList<>();
		XMLElementDecl parentDecl = new XMLElementDecl();

		// Query Xerces for the element declaration index
		int parentIndex = grammar.getElementDeclIndex(parentTag);
		System.out.println("\tparent tag = '" + parentTag + "'; parentIndex = " + parentIndex);
		if (parentIndex != -1) {
			System.out.println("\tgrammar: " + grammar.getContentSpecAsString(parentIndex));
			int contentSpecIndex = grammar.getContentSpecIndex(parentIndex);
			System.out.println("\tcontentSpecIndex = " + contentSpecIndex);
			if (contentSpecIndex != -1) {
				// 3. Retrieve the root of the content model tree
				XMLContentSpec contentSpec = new XMLContentSpec();
				grammar.getContentSpec(contentSpecIndex, contentSpec);
				System.out.println("\tcontentSpec = " + contentSpec.type);
				switch (contentSpec.type) {
				case XMLContentSpec.CONTENTSPECNODE_ANY:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY");
					break;
				case XMLContentSpec.CONTENTSPECNODE_ANY_LAX:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_LAX");
					break;
				case XMLContentSpec.CONTENTSPECNODE_ANY_LOCAL:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_LOCAL");
					break;
				case XMLContentSpec.CONTENTSPECNODE_ANY_LOCAL_LAX:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_LOCAL_LAX");
					break;
				case XMLContentSpec.CONTENTSPECNODE_ANY_LOCAL_SKIP:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_LOCAL_SKIP");
					break;
				case XMLContentSpec.CONTENTSPECNODE_ANY_OTHER:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_OTHER");
					break;
				case XMLContentSpec.CONTENTSPECNODE_ANY_OTHER_LAX:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_OTHER_LAX");
					break;
				case XMLContentSpec.CONTENTSPECNODE_ANY_OTHER_SKIP:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_OTHER_SKIP");
					break;
				case XMLContentSpec.CONTENTSPECNODE_ANY_SKIP:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_SKIP");
					break;
				case XMLContentSpec.CONTENTSPECNODE_CHOICE:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_CHOICE");
					break;
				case XMLContentSpec.CONTENTSPECNODE_LEAF:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_LEAF");
					break;
				case XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE");
					break;
				case XMLContentSpec.CONTENTSPECNODE_SEQ:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_SEQ");
					break;
				case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE");
					break;
				case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE:
					System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE");
					break;
				default:
					System.out.println("\t\tUNKOWN");
					break;
				}

				// 4. Recursively analyze 'contentSpec' to find "targetElementName"
				// and collect all valid elements found in SEQUENCE nodes to its left.
//			    findPrecedingElements(grammar, contentSpec, "targetElementName", ...);
			}
			grammar.getElementDecl(parentIndex, parentDecl);
//			elementDecl.contentModelValidator.validate(elementDecl.name, elementIndex, elementIndex);

			// For a production system like XLingPaper, you can programmatically extract
			// tokens from elementDecl.contentModelValidator, or fall back to parsing
			// a simplified mapping index for complex content models.
		}
		return children;
	}

}
