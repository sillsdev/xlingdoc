/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package application.service;

import org.apache.xerces.impl.dtd.DTDGrammar;
/**
 * code drafted by Leo
 */
import org.w3c.dom.*;
import java.util.*;

public class DynamicPrecedenceChecker {

    /**
     * Returns DTD-allowed predecessors that have NOT yet appeared as preceding siblings.
     */
    public static Set<String> getRemainingAllowedPredecessors(DTDGrammar grammar, 
                                                              Element targetElement, 
                                                              String parentElementName) {
		String targetName = XmlNameMapper.getMappedElementName(targetElement.getTagName().toLowerCase());
        // 1. Get all theoretically allowed predecessors from DTD
        Set<String> allowedPredecessors = DTDPrecedenceAnalyzer.findPrecedingElements(
            grammar, parentElementName, targetName);

        // 2. Get elements that actually appeared before this target
        Set<String> actualPrecedingSiblings = new HashSet<>();
        Node sibling = targetElement.getPreviousSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
            	String siblingName = XmlNameMapper.getMappedElementName(sibling.getNodeName().toLowerCase());
                actualPrecedingSiblings.add(siblingName);
            }
            sibling = sibling.getPreviousSibling();
        }

        // 3. Filter: Remove actual siblings from the allowed list
        allowedPredecessors.removeAll(actualPrecedingSiblings);

        return allowedPredecessors;
    }
}   