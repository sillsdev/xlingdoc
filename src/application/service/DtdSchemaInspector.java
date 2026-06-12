/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package application.service;

/**
 * code drafted by Gemini
 */
import org.apache.xerces.impl.dtd.XMLElementDecl;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.impl.dtd.XMLDTDLoader;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

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
	 * @param insertAfter True for "Insert After" context, False for "Insert Before"
	 * @return List of valid element tag names
	 */
	public List<String> getValidAdjacentElements(Element targetElement, boolean insertAfter) {
		List<String> validChoices = new ArrayList<>();

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

		// 2. Fetch all possible elements that are allowed anywhere inside this parent
		List<String> globallyAllowedInParent = getAllowedChildrenForParent(parentTag);

		// 3. Simulate the proposed DOM modification
		List<String> currentSiblings = getChildElementNames(parent);
		int targetIndex = currentSiblings.indexOf(targetElement.getTagName());

		// 4. Test each globally allowed element in the proposed position
		for (String candidateTag : globallyAllowedInParent) {
			List<String> simulatedSequence = new ArrayList<>(currentSiblings);

			int insertIndex = insertAfter ? targetIndex + 1 : targetIndex;
			simulatedSequence.add(insertIndex, candidateTag);

			// 5. Run a tentative validation check against the DTD rule
			if (isValidSequence(parentTag, simulatedSequence)) {
				validChoices.add(candidateTag);
			}
		}

		return validChoices;
	}

	public DTDGrammar getGrammar() {
		return grammar;
	}

	private List<String> getAllowedChildrenForParent(String parentTag) {
		List<String> children = new ArrayList<>();
		XMLElementDecl elementDecl = new XMLElementDecl();

		// Query Xerces for the element declaration index
		int elementIndex = grammar.getElementDeclIndex(parentTag);
		System.out.println("\tparent tag = '" + parentTag + "'; elementIndex = " + elementIndex);
		if (elementIndex != -1) {
			grammar.getElementDecl(elementIndex, elementDecl);
//			elementDecl.contentModelValidator.validate(elementDecl.name, elementIndex, elementIndex);

			// For a production system like XLingPaper, you can programmatically extract
			// tokens from elementDecl.contentModelValidator, or fall back to parsing
			// a simplified mapping index for complex content models.
		}
		return children;
	}

	/**
	 * Simulates the child layout sequence to verify if it breaks DTD rules.
	 */
	private boolean isValidSequence(String parentTag, List<String> sequence) {
		// Enforce the strict rules defined by your DTD.
		// For example, if parent is <frontMatter>, it might require <title> to be at
		// index 0.
		if (parentTag.equals("frontMatter")) {
			if (sequence.contains("title") && !sequence.get(0).equals("title")) {
				return false; // Invalid: title must be first
			}
		}
		return true;
	}

	private List<String> getChildElementNames(Element parent) {
		List<String> names = new ArrayList<>();
		NodeList kids = parent.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++) {
			if (kids.item(i).getNodeType() == Node.ELEMENT_NODE) {
				names.add(((Element) kids.item(i)).getTagName());
			}
		}
		return names;
	}
}
