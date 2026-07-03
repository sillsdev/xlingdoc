/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

/**
 * code drafted by Leo
 */
import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.impl.dtd.XMLContentSpec;
import java.util.HashSet;
import java.util.Set;

public class DTDReplaceAnalyzer {

	/**
	 * Finds all element names that can legally replace the targetElement within the
	 * content model of the parentElement.
	 */
	public static Set<String> findReplaceElements(DTDGrammar grammar, String parentElementName,
			String targetElementName) {
		Set<String> replaceElements = new HashSet<>();

		// 1. Get Parent Element Index
		int parentIndex = grammar.getElementDeclIndex(parentElementName);
//		System.out.println("\tparentIndex = " + parentIndex);
//		System.out.println("\tgrammar: " + grammar.getContentSpecAsString(parentIndex));

		if (parentIndex == -1) {
			return replaceElements; // Parent not found
		}

		// 2. Get Content Spec Index
		int contentSpecIndex = grammar.getContentSpecIndex(parentIndex);
//		System.out.println("\tcontentSpecIndex = " + contentSpecIndex);
		if (contentSpecIndex == -1) {
			return replaceElements; // ANY or EMPTY content
		}

		// 3. Traverse
		XMLContentSpec spec = new XMLContentSpec();
		grammar.getContentSpec(contentSpecIndex, spec);
//		System.out.println("\tspec = " + spec);
		// We need a wrapper to hold state during recursion (whether target was found)
		boolean[] targetFound = new boolean[1];
		collectReplaceElements(grammar, spec, targetElementName, replaceElements, targetFound, false);

		return replaceElements;
	}


	/**
	 * Helper to collect ALL element names from a subtree (used for the "Left" side
	 * of a sequence).
	 */
	private static void collectAllLeafElements(DTDGrammar grammar, XMLContentSpec spec, Set<String> elements) {
		if (spec == null)
			return;
		short type = spec.type;

		if (type == XMLContentSpec.CONTENTSPECNODE_LEAF) {
			if (spec.value != null && spec.value instanceof String) {
//				System.out.println("\t\tadding " + (String) spec.value);
				elements.add((String) spec.value);
			}
		} else if (type == XMLContentSpec.CONTENTSPECNODE_SEQ || type == XMLContentSpec.CONTENTSPECNODE_CHOICE) {

			XMLContentSpec leftSpec = new XMLContentSpec();
			XMLContentSpec rightSpec = new XMLContentSpec();

			if (spec.value instanceof int[]) {
				int i = ((int[]) spec.value)[0];
				grammar.getContentSpec((Integer) i, leftSpec);
			} else if (spec.value instanceof Integer)
				grammar.getContentSpec((Integer) spec.value, leftSpec);
			else if (spec.value instanceof XMLContentSpec)
				leftSpec = (XMLContentSpec) spec.value;

			if (spec.otherValue instanceof int[]) {
				int i = ((int[]) spec.otherValue)[0];
				grammar.getContentSpec((Integer) i, rightSpec);
			} else if (spec.otherValue instanceof Integer)
				grammar.getContentSpec((Integer) spec.otherValue, rightSpec);
			else if (spec.otherValue instanceof XMLContentSpec)
				rightSpec = (XMLContentSpec) spec.otherValue;

			collectAllLeafElements(grammar, leftSpec, elements);
			collectAllLeafElements(grammar, rightSpec, elements);
		} else if (type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE
				|| type == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE
				|| type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE) {

			XMLContentSpec childSpec = new XMLContentSpec();
			if (spec.value instanceof int[]) {
				int i = ((int[]) spec.value)[0];
				grammar.getContentSpec((Integer) i, childSpec);
			} else if (spec.value instanceof Integer)
				grammar.getContentSpec((Integer) spec.value, childSpec);
			else if (spec.value instanceof XMLContentSpec)
				childSpec = (XMLContentSpec) spec.value;

			collectAllLeafElements(grammar, childSpec, elements);
		}
	}

	// Add a flag to track if we are inside a repeating group
	// Update the recursive method signature to return whether the target was found
	// and modify the repeating group logic.
	/**
	 * Recursive helper to traverse the content spec tree.
	 *
	 * @parm grammar          The DTD
	 * @param spec            The current content spec node
	 * @param targetName      The element name we are looking for
	 * @param replaceSet     Collection to add valid adjacent elements to
	 * @param targetFoundFlag Single-element boolean array to signal if target was
	 *                              found in this branch
	 * @parm isInsideRepeatingGroup Flag for if inside repeating group
	 */
	private static void collectReplaceElements(DTDGrammar grammar,
	                                             XMLContentSpec spec, 
	                                             String targetName, 
	                                             Set<String> replaceSet, 
	                                             boolean[] targetFoundFlag,
	                                             boolean isInsideRepeatingGroup) {
	    if (spec == null) return;
	    short type = spec.type;
	    targetFoundFlag[0] = false;

	    // 1. Handle Repeating Operators
	    if (type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE ||
	        type == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE) {
//	        System.out.println("\tzero or one or more");
	        Integer childIndex = extractIndex(spec.value);
	        if (childIndex != null && childIndex != -1) {
	            XMLContentSpec childSpec = new XMLContentSpec();
	            grammar.getContentSpec(childIndex, childSpec);
	            
	            boolean[] foundInChild = new boolean[1];
	            // Pass true for isInsideRepeatingGroup
	            collectReplaceElements(grammar, childSpec, targetName, replaceSet, foundInChild, true);
	            
	            if (foundInChild[0]) {
	                targetFoundFlag[0] = true;
	                // FIX: If inside a repeating group and we found the target, 
	                // the target itself is a valid predecessor (from a previous iteration).
	                replaceSet.add(targetName); 
	            }
	        }
	        return;
	    }
	    
	    // 2. Handle Optional (Zero-or-One) - No repetition
	    if (type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE) {
//	        System.out.println("\tzero or one");
	        Integer childIndex = extractIndex(spec.value);
	        if (childIndex != null && childIndex != -1) {
	            XMLContentSpec childSpec = new XMLContentSpec();
	            grammar.getContentSpec(childIndex, childSpec);
	            boolean[] foundInChild = new boolean[1];
	            collectReplaceElements(grammar, childSpec, targetName, replaceSet, foundInChild, isInsideRepeatingGroup);
	            if (foundInChild[0]) targetFoundFlag[0] = true;
	        }
	        return;
	    }

	    // 3. Handle Sequence (A, B)
	    if (type == XMLContentSpec.CONTENTSPECNODE_SEQ) {
//	        System.out.println("\tsequence");
	        Integer leftIdx = extractIndex(spec.value);
	        Integer rightIdx = extractIndex(spec.otherValue);
	        
	        XMLContentSpec leftSpec = new XMLContentSpec();
	        XMLContentSpec rightSpec = new XMLContentSpec();
	        
	        if (leftIdx != null && leftIdx != -1) grammar.getContentSpec(leftIdx, leftSpec);
	        if (rightIdx != null && rightIdx != -1) grammar.getContentSpec(rightIdx, rightSpec);

	        boolean[] foundInLeft = new boolean[1];
	        boolean[] foundInRight = new boolean[1];
	        
	        collectReplaceElements(grammar, leftSpec, targetName, replaceSet, foundInLeft, isInsideRepeatingGroup);
	        collectReplaceElements(grammar, rightSpec, targetName, replaceSet, foundInRight, isInsideRepeatingGroup);

	        // Standard: If target in Right, Left is predecessor
	        if (foundInRight[0]) {
	            collectAllLeafElements(grammar, leftSpec, replaceSet);
	        }
	        // Standard: If target in Left, Right is follower
	        if (foundInLeft[0]) {
	            collectAllLeafElements(grammar, rightSpec, replaceSet);
	        }
	        
	        if (foundInLeft[0] || foundInRight[0]) targetFoundFlag[0] = true;
	        return;
	    }

	    // 4. Handle Choice (A | B)
	    if (type == XMLContentSpec.CONTENTSPECNODE_CHOICE) {
//	        System.out.println("\tchoice\ttarget = " + targetName);
	        Integer leftIdx = extractIndex(spec.value);
	        Integer rightIdx = extractIndex(spec.otherValue);

	        XMLContentSpec leftSpec = new XMLContentSpec();
	        XMLContentSpec rightSpec = new XMLContentSpec();

	        if (leftIdx != null && leftIdx != -1) grammar.getContentSpec(leftIdx, leftSpec);
	        if (rightIdx != null && rightIdx != -1) grammar.getContentSpec(rightIdx, rightSpec);

	        boolean[] foundInLeft = new boolean[1];
	        boolean[] foundInRight = new boolean[1];

	        collectReplaceElements(grammar, leftSpec, targetName, replaceSet, foundInLeft, isInsideRepeatingGroup);
	        collectReplaceElements(grammar, rightSpec, targetName, replaceSet, foundInRight, isInsideRepeatingGroup);

	        // FIX: Repeating Choice Logic
	        // If inside a repeating group (e.g., (A|B)+) and target is 'A':
	        // Then 'B' is a valid predecessor (from a previous iteration).
	        if (isInsideRepeatingGroup) {
	            if (foundInRight[0]) collectAllLeafElements(grammar, leftSpec, replaceSet);
	            if (foundInLeft[0])  collectAllLeafElements(grammar, rightSpec, replaceSet);
	        }

	        if (foundInLeft[0] || foundInRight[0]) targetFoundFlag[0] = true;
	        return;
	    }

	    // 5. Handle Leaf
	    if (type == XMLContentSpec.CONTENTSPECNODE_LEAF) {
//	        System.out.print("\tleaf\ttarget = " + targetName);
	        if (spec.value != null && spec.value instanceof String) {
	            String elementName = (String) spec.value;
	            replaceSet.add(elementName);
//		        System.out.println("\t = " + elementName);
	            if (elementName.equals(targetName)) {
	                targetFoundFlag[0] = true;
	            }
	        }
	    }
	}

	// Helper to extract index cleanly
	private static Integer extractIndex(Object obj) {
	    if (obj instanceof Integer) return (Integer) obj;
	    if (obj instanceof int[]) {
	        int[] arr = (int[]) obj;
	        return (arr.length > 0) ? arr[0] : null;
	    }
	    return null;
	}   
}
