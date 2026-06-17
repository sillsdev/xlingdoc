/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package application.service;

/**
 * code drafted by Leo
 */
import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.impl.dtd.XMLContentSpec;
import java.util.HashSet;
import java.util.Set;

public class DTDPrecedenceAnalyzer {

	/**
	 * Finds all element names that can legally precede the targetElement within the
	 * content model of the parentElement.
	 */
	public static Set<String> findPrecedingElements(DTDGrammar grammar, String parentElementName,
			String targetElementName) {
		Set<String> precedingElements = new HashSet<>();

		// 1. Get Parent Element Index
		int parentIndex = grammar.getElementDeclIndex(parentElementName);
		System.out.println("\tparentIndex = " + parentIndex);
		System.out.println("\tgrammar: " + grammar.getContentSpecAsString(parentIndex));

		if (parentIndex == -1) {
			return precedingElements; // Parent not found
		}

		// 2. Get Content Spec Index
		int contentSpecIndex = grammar.getContentSpecIndex(parentIndex);
		System.out.println("\tcontentSpecIndex = " + contentSpecIndex);
		if (contentSpecIndex == -1) {
			return precedingElements; // ANY or EMPTY content
		}

		// 3. Traverse
		XMLContentSpec spec = new XMLContentSpec();
		grammar.getContentSpec(contentSpecIndex, spec);
		System.out.println("\tspec = " + spec);
		// We need a wrapper to hold state during recursion (whether target was found)
		boolean[] targetFound = new boolean[1];
//		collectPrecedingElements(grammar, spec, targetElementName, precedingElements, targetFound);
//		collectPrecedingElementsNew(grammar, spec, targetElementName, precedingElements, targetFound, false);
		collectPrecedingElements2(grammar, spec, targetElementName, precedingElements, targetFound, false);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY = " + XMLContentSpec.CONTENTSPECNODE_ANY);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_LAX = " + XMLContentSpec.CONTENTSPECNODE_ANY_LAX);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_LOCAL = " + XMLContentSpec.CONTENTSPECNODE_ANY_LOCAL);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_LOCAL_LAX = " + XMLContentSpec.CONTENTSPECNODE_ANY_LOCAL_LAX);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_LOCAL_SKIP = " + XMLContentSpec.CONTENTSPECNODE_ANY_LOCAL_SKIP);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_OTHER = " + XMLContentSpec.CONTENTSPECNODE_ANY_OTHER);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_OTHER_LAX = " + XMLContentSpec.CONTENTSPECNODE_ANY_OTHER_LAX);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_OTHER_SKIP = " + XMLContentSpec.CONTENTSPECNODE_ANY_OTHER_SKIP);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ANY_SKIP = " + XMLContentSpec.CONTENTSPECNODE_ANY_SKIP);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_CHOICE = " + XMLContentSpec.CONTENTSPECNODE_CHOICE);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_LEAF = " + XMLContentSpec.CONTENTSPECNODE_LEAF);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE = " + XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_SEQ = " + XMLContentSpec.CONTENTSPECNODE_SEQ);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE = " + XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE);
//		System.out.println("\t\tXMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE = " + XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE);

		return precedingElements;
	}

	/**
	 * Recursive helper to traverse the content spec tree.
	 * 
	 * @param spec            The current content spec node
	 * @param targetName      The element name we are looking for
	 * @param precedingSet    Collection to add valid preceding elements to
	 * @param targetFoundFlag Single-element boolean array to signal if target was
	 *                        found in this branch
	 */
	private static void collectPrecedingElements(DTDGrammar grammar, XMLContentSpec spec, String targetName,
			Set<String> precedingSet, boolean[] targetFoundFlag) {

		if (spec == null)
			return;

		// Reset flag for this specific node traversal context if needed,
		// but usually we pass a fresh flag for specific branch checks.
		// Here we use the flag to tell the *parent* call if we found the target.
		targetFoundFlag[0] = false;
		XMLContentSpec leftSpec = new XMLContentSpec();
		XMLContentSpec rightSpec = new XMLContentSpec();

		short type = spec.type;
		System.out.println("\t\ttype = " + type + "; spec.value = " + spec.value);
		switch (type) {
		case XMLContentSpec.CONTENTSPECNODE_LEAF:
			System.out.println("\t\tLEAF");
			// Check if this leaf is the target
			// spec.value contains the element name (String) or -1 for #PCDATA
			if (spec.value != null && spec.value instanceof String) {
				String elementName = (String) spec.value;
				if (elementName.equals(targetName)) {
					targetFoundFlag[0] = true;
				}
			}
			break;
		case XMLContentSpec.CONTENTSPECNODE_SEQ:
			System.out.println("\t\tSEQ");
//            XMLContentSpec leftSpec = new XMLContentSpec();
//            XMLContentSpec rightSpec = new XMLContentSpec();

			// Safely extract indices
			Integer leftIdx = (spec.value instanceof Integer) ? (Integer) spec.value
					: (spec.value instanceof int[]) ? ((int[]) spec.value)[0] : null;
			System.out.println("\t\t\tleftIdx = " + leftIdx);

			Integer rightIdx = (spec.otherValue instanceof Integer) ? (Integer) spec.otherValue
					: (spec.otherValue instanceof int[]) ? ((int[]) spec.otherValue)[0] : null;
			System.out.println("\t\t\trightIdx = " + rightIdx);
			// Load children via grammar using indices
			if (leftIdx != null && leftIdx != -1)
				grammar.getContentSpec(leftIdx, leftSpec);
			if (rightIdx != null && rightIdx != -1)
				grammar.getContentSpec(rightIdx, rightSpec);

			boolean[] foundInLeft = new boolean[1];
			boolean[] foundInRight = new boolean[1];

			collectPrecedingElements(grammar, leftSpec, targetName, precedingSet, foundInLeft);
			collectPrecedingElements(grammar, rightSpec, targetName, precedingSet, foundInRight);

			if (foundInRight[0]) {
				collectAllLeafElements(grammar, leftSpec, precedingSet);
			}
			if (foundInLeft[0] || foundInRight[0])
				targetFoundFlag[0] = true;
//          else if (type == XMLContentSpec.CONTENTSPECNODE_SEQ) {
//        	System.out.println("\t\tSEQ");
//            // Sequence: Left, Right
//            // If Target is in Right, then everything in Left is a predecessor.
//            
//            XMLContentSpec leftSpec = new XMLContentSpec();
//            XMLContentSpec rightSpec = new XMLContentSpec();
//            
//            // Load Left Child
//            if (spec.value instanceof Integer) {
//                grammar.getContentSpec((Integer) spec.value, leftSpec);
//            } else if (spec.value instanceof XMLContentSpec) { 
//                // Fallback if direct object passed (depends on Xerces version)
//                leftSpec = (XMLContentSpec) spec.value; 
//            } else {
//            	System.out.println("\t\t\tspec.value is other: " + spec.value.getClass().getName());
//                leftSpec = (XMLContentSpec) spec.value; 
//            }
//
//            // Load Right Child
//            if (spec.otherValue instanceof Integer) {
//                grammar.getContentSpec((Integer) spec.otherValue, rightSpec);
//            } else if (spec.otherValue instanceof XMLContentSpec) {
//                rightSpec = (XMLContentSpec) spec.otherValue;
//            }
//
//            // 1. Check Left Branch
//            boolean[] foundInLeft = new boolean[1];
//            System.out.println("before left");
//            collectPrecedingElements(grammar, leftSpec, targetName, precedingSet, foundInLeft);
//
//            // 2. Check Right Branch
//            boolean[] foundInRight = new boolean[1];
//            System.out.println("before left");
//            collectPrecedingElements(grammar, rightSpec, targetName, precedingSet, foundInRight);
//
//            // LOGIC: If target is in Right, collect all leaves from Left
//            if (foundInRight[0]) {
//                collectAllLeafElements(grammar, leftSpec, precedingSet);
//            }
//            
//            // Propagate found status up
//            if (foundInLeft[0] || foundInRight[0]) {
//                targetFoundFlag[0] = true;
//            }
//        }
			break;
		case XMLContentSpec.CONTENTSPECNODE_CHOICE:
			System.out.println("\t\tCHOICE");
			// Choice: Left | Right
			// Target can be in either. Preceding elements usually don't cross choice
			// branches
			// unless the choice itself is repeated inside a sequence (handled by parent
			// sequence logic).

//            XMLContentSpec leftSpec = new XMLContentSpec();
//            XMLContentSpec rightSpec = new XMLContentSpec();

			if (spec.value instanceof int[]) {
				int i = ((int[]) spec.value)[0];
				grammar.getContentSpec((Integer) i, leftSpec);
			} else if (spec.value instanceof Integer)
				grammar.getContentSpec((Integer) spec.value, leftSpec);
			else if (spec.value instanceof XMLContentSpec)
				leftSpec = (XMLContentSpec) spec.value;

			if (spec.otherValue instanceof int[]) {
				int i = ((int[]) spec.value)[0];
				grammar.getContentSpec((Integer) i, rightSpec);
			} else if (spec.otherValue instanceof Integer)
				grammar.getContentSpec((Integer) spec.otherValue, rightSpec);
			else if (spec.otherValue instanceof XMLContentSpec)
				rightSpec = (XMLContentSpec) spec.otherValue;

			boolean[] foundInLeftChoice = new boolean[1];
			boolean[] foundInRightChoice = new boolean[1];

			collectPrecedingElements(grammar, leftSpec, targetName, precedingSet, foundInLeftChoice);
			collectPrecedingElements(grammar, rightSpec, targetName, precedingSet, foundInRightChoice);

			if (foundInLeftChoice[0] || foundInRightChoice[0]) {
				targetFoundFlag[0] = true;
			}
			break;
		case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE: // fall through
		case XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE: // fall through
		case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE:
			// Note: Occurrence operators (*, +, ?) are unary nodes wrapping the above.
			// In Xerces, these often appear as CONTENTSPECNODE_ZERO_OR_MORE etc.
			// They simply pass through to their single child (stored in 'value').
			System.out.println("\t\tZERO_OR_MORE; ONE_OR_MORE; ZERO_OR_ONE");
			XMLContentSpec childSpec = new XMLContentSpec();
			if (spec.value instanceof int[]) {
				int i = ((int[]) spec.value)[0];
				grammar.getContentSpec((Integer) i, childSpec);
			} else if (spec.value instanceof Integer) {
				grammar.getContentSpec((Integer) spec.value, childSpec);
			} else if (spec.value instanceof XMLContentSpec) {
				childSpec = (XMLContentSpec) spec.value;
			}

			boolean[] foundInChild = new boolean[1];
			collectPrecedingElements(grammar, childSpec, targetName, precedingSet, foundInChild);
			if (foundInChild[0])
				targetFoundFlag[0] = true;
			break;
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
		default:
			System.out.println("\t\tUnknown XMLContentSpec with value " + type);
			break;
		}
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
	private static void collectPrecedingElementsNew(DTDGrammar grammar, XMLContentSpec spec, String targetName,
			Set<String> precedingSet, boolean[] targetFoundFlag, boolean isInsideRepeatingGroup) { // New parameter

		if (spec == null)
			return;
		short type = spec.type;
		targetFoundFlag[0] = false;

		// 1. Handle Repeating Operators (Unary)
		if (type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE || type == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE) {

			XMLContentSpec childSpec = new XMLContentSpec();
			Integer childIndex = null;

			if (spec.value instanceof Integer)
				childIndex = (Integer) spec.value;
			else if (spec.value instanceof int[])
				childIndex = ((int[]) spec.value)[0];

			if (childIndex != null && childIndex != -1) {
				grammar.getContentSpec(childIndex, childSpec);
				// RECURSE with isInsideRepeatingGroup = true
				boolean[] foundInChild = new boolean[1];
				collectPrecedingElementsNew(grammar, childSpec, targetName, precedingSet, foundInChild, true);
				if (foundInChild[0])
					targetFoundFlag[0] = true;
			}
			return;
		}

		// 2. Handle Optional (Zero-or-One) - Does NOT allow repetition, so pass false
		if (type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE) {
			XMLContentSpec childSpec = new XMLContentSpec();
			Integer childIndex = null;
			if (spec.value instanceof Integer)
				childIndex = (Integer) spec.value;
			else if (spec.value instanceof int[])
				childIndex = ((int[]) spec.value)[0];

			if (childIndex != null && childIndex != -1) {
				grammar.getContentSpec(childIndex, childSpec);
				boolean[] foundInChild = new boolean[1];
				// Pass the current isInsideRepeatingGroup status (doesn't change)
				collectPrecedingElementsNew(grammar, childSpec, targetName, precedingSet, foundInChild,
						isInsideRepeatingGroup);
				if (foundInChild[0])
					targetFoundFlag[0] = true;
			}
			return;
		}

		// 3. Handle Sequence
		if (type == XMLContentSpec.CONTENTSPECNODE_SEQ) {
			XMLContentSpec leftSpec = new XMLContentSpec();
			XMLContentSpec rightSpec = new XMLContentSpec();

			Integer leftIdx = (spec.value instanceof Integer) ? (Integer) spec.value
					: (spec.value instanceof int[]) ? ((int[]) spec.value)[0] : null;
			Integer rightIdx = (spec.otherValue instanceof Integer) ? (Integer) spec.otherValue
					: (spec.otherValue instanceof int[]) ? ((int[]) spec.otherValue)[0] : null;

			if (leftIdx != null && leftIdx != -1)
				grammar.getContentSpec(leftIdx, leftSpec);
			if (rightIdx != null && rightIdx != -1)
				grammar.getContentSpec(rightIdx, rightSpec);

			boolean[] foundInLeft = new boolean[1];
			boolean[] foundInRight = new boolean[1];

			collectPrecedingElementsNew(grammar, leftSpec, targetName, precedingSet, foundInLeft, isInsideRepeatingGroup);
			collectPrecedingElementsNew(grammar, rightSpec, targetName, precedingSet, foundInRight,
					isInsideRepeatingGroup);

			// Standard Logic: If target in Right, Left is predecessor
			if (foundInRight[0]) {
				collectAllLeafElements(grammar, leftSpec, precedingSet);
			}

			// NEW LOGIC: If inside a repeating group, and target is found in EITHER side,
			// then elements from the OTHER side are also predecessors (from previous
			// iterations).
			if (isInsideRepeatingGroup) {
				if (foundInRight[0])
					collectAllLeafElements(grammar, leftSpec, precedingSet);
				if (foundInLeft[0])
					collectAllLeafElements(grammar, rightSpec, precedingSet);
			}

			if (foundInLeft[0] || foundInRight[0])
				targetFoundFlag[0] = true;
			return;
		}

		// 4. Handle Choice
		if (type == XMLContentSpec.CONTENTSPECNODE_CHOICE) {
			XMLContentSpec leftSpec = new XMLContentSpec();
			XMLContentSpec rightSpec = new XMLContentSpec();

			Integer leftIdx = (spec.value instanceof Integer) ? (Integer) spec.value
					: (spec.value instanceof int[]) ? ((int[]) spec.value)[0] : null;
			Integer rightIdx = (spec.otherValue instanceof Integer) ? (Integer) spec.otherValue
					: (spec.otherValue instanceof int[]) ? ((int[]) spec.otherValue)[0] : null;

			if (leftIdx != null && leftIdx != -1)
				grammar.getContentSpec(leftIdx, leftSpec);
			if (rightIdx != null && rightIdx != -1)
				grammar.getContentSpec(rightIdx, rightSpec);

			boolean[] foundInLeft = new boolean[1];
			boolean[] foundInRight = new boolean[1];

			collectPrecedingElementsNew(grammar, leftSpec, targetName, precedingSet, foundInLeft, isInsideRepeatingGroup);
			collectPrecedingElementsNew(grammar, rightSpec, targetName, precedingSet, foundInRight,
					isInsideRepeatingGroup);

			// NEW LOGIC: If inside a repeating group (e.g., (A|B)+), and target is 'B',
			// then 'A' is a valid predecessor because the group could have repeated (A then
			// B).
			if (isInsideRepeatingGroup) {
				if (foundInRight[0])
					collectAllLeafElements(grammar, leftSpec, precedingSet);
				if (foundInLeft[0])
					collectAllLeafElements(grammar, rightSpec, precedingSet);
			}

			if (foundInLeft[0] || foundInRight[0])
				targetFoundFlag[0] = true;
			return;
		}

		// 5. Handle Leaf
		if (type == XMLContentSpec.CONTENTSPECNODE_LEAF) {
			if (spec.value != null && spec.value instanceof String) {
				String elementName = (String) spec.value;
				if (elementName.equals(targetName)) {
					targetFoundFlag[0] = true;
				}
			}
		}
	}
	// Update the recursive method signature to return whether the target was found
	// and modify the repeating group logic.

	private static void collectPrecedingElements2(DTDGrammar grammar, 
	                                             XMLContentSpec spec, 
	                                             String targetName, 
	                                             Set<String> precedingSet, 
	                                             boolean[] targetFoundFlag,
	                                             boolean isInsideRepeatingGroup) {
	    if (spec == null) return;
	    short type = spec.type;
	    targetFoundFlag[0] = false;

	    // 1. Handle Repeating Operators
	    if (type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE ||
	        type == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE) {
	        
	        Integer childIndex = extractIndex(spec.value);
	        if (childIndex != null && childIndex != -1) {
	            XMLContentSpec childSpec = new XMLContentSpec();
	            grammar.getContentSpec(childIndex, childSpec);
	            
	            boolean[] foundInChild = new boolean[1];
	            // Pass true for isInsideRepeatingGroup
	            collectPrecedingElements2(grammar, childSpec, targetName, precedingSet, foundInChild, true);
	            
	            if (foundInChild[0]) {
	                targetFoundFlag[0] = true;
	                // FIX: If inside a repeating group and we found the target, 
	                // the target itself is a valid predecessor (from a previous iteration).
	                precedingSet.add(targetName); 
	            }
	        }
	        return;
	    }
	    
	    // 2. Handle Optional (Zero-or-One) - No repetition
	    if (type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE) {
	        Integer childIndex = extractIndex(spec.value);
	        if (childIndex != null && childIndex != -1) {
	            XMLContentSpec childSpec = new XMLContentSpec();
	            grammar.getContentSpec(childIndex, childSpec);
	            boolean[] foundInChild = new boolean[1];
	            collectPrecedingElements2(grammar, childSpec, targetName, precedingSet, foundInChild, isInsideRepeatingGroup);
	            if (foundInChild[0]) targetFoundFlag[0] = true;
	        }
	        return;
	    }

	    // 3. Handle Sequence (A, B)
	    if (type == XMLContentSpec.CONTENTSPECNODE_SEQ) {
	        Integer leftIdx = extractIndex(spec.value);
	        Integer rightIdx = extractIndex(spec.otherValue);
	        
	        XMLContentSpec leftSpec = new XMLContentSpec();
	        XMLContentSpec rightSpec = new XMLContentSpec();
	        
	        if (leftIdx != null && leftIdx != -1) grammar.getContentSpec(leftIdx, leftSpec);
	        if (rightIdx != null && rightIdx != -1) grammar.getContentSpec(rightIdx, rightSpec);

	        boolean[] foundInLeft = new boolean[1];
	        boolean[] foundInRight = new boolean[1];
	        
	        collectPrecedingElements2(grammar, leftSpec, targetName, precedingSet, foundInLeft, isInsideRepeatingGroup);
	        collectPrecedingElements2(grammar, rightSpec, targetName, precedingSet, foundInRight, isInsideRepeatingGroup);

	        // Standard: If target in Right, Left is predecessor
	        if (foundInRight[0]) {
	            collectAllLeafElements(grammar, leftSpec, precedingSet);
	        }
	        
	        // FIX: Repeating Sequence Logic
	        // If inside a repeating group (e.g., (A, B)+) and target is 'A' (Left):
	        // Technically 'B' (Right) from a previous iteration can precede 'A'.
	        // IF YOU WANT TO EXCLUDE 'affiliation' (which is B), DO NOT add Right to predecessors here.
	        // If you only want static predecessors or choice siblings, skip this block for Sequences.
	        // To strictly match your request (exclude affiliation), we DO NOT add Right side elements 
	        // when target is in Left side, even if repeating.
	        
	        if (foundInLeft[0] || foundInRight[0]) targetFoundFlag[0] = true;
	        return;
	    }

	    // 4. Handle Choice (A | B)
	    if (type == XMLContentSpec.CONTENTSPECNODE_CHOICE) {
	        Integer leftIdx = extractIndex(spec.value);
	        Integer rightIdx = extractIndex(spec.otherValue);

	        XMLContentSpec leftSpec = new XMLContentSpec();
	        XMLContentSpec rightSpec = new XMLContentSpec();

	        if (leftIdx != null && leftIdx != -1) grammar.getContentSpec(leftIdx, leftSpec);
	        if (rightIdx != null && rightIdx != -1) grammar.getContentSpec(rightIdx, rightSpec);

	        boolean[] foundInLeft = new boolean[1];
	        boolean[] foundInRight = new boolean[1];

	        collectPrecedingElements2(grammar, leftSpec, targetName, precedingSet, foundInLeft, isInsideRepeatingGroup);
	        collectPrecedingElements2(grammar, rightSpec, targetName, precedingSet, foundInRight, isInsideRepeatingGroup);

	        // FIX: Repeating Choice Logic
	        // If inside a repeating group (e.g., (A|B)+) and target is 'A':
	        // Then 'B' is a valid predecessor (from a previous iteration).
	        if (isInsideRepeatingGroup) {
	            if (foundInRight[0]) collectAllLeafElements(grammar, leftSpec, precedingSet);
	            if (foundInLeft[0])  collectAllLeafElements(grammar, rightSpec, precedingSet);
	        }

	        if (foundInLeft[0] || foundInRight[0]) targetFoundFlag[0] = true;
	        return;
	    }

	    // 5. Handle Leaf
	    if (type == XMLContentSpec.CONTENTSPECNODE_LEAF) {
	        if (spec.value != null && spec.value instanceof String) {
	            String elementName = (String) spec.value;
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
