/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

/**
 * Based on code suggested by Leo
 */
import org.apache.xerces.xni.*;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import java.util.HashMap;
import java.util.Map;

public class DtdContentHandler implements XMLDTDContentModelHandler {

    private XMLDTDContentModelSource modelSource;
    private String sCurrentElement;
    private boolean expectingPCData;
    private final Map<String, Boolean> contentElements = new HashMap<>();

    @Override
    public void startContentModel(String elementName, Augmentations augmentations) throws XNIException {
        sCurrentElement = elementName;
        expectingPCData = false;
        // Assume false until proven true by pcdata() call
        contentElements.put(elementName, false); 
    }

    @Override
    public void startGroup(Augmentations augmentations) throws XNIException {
        // In a mixed content model, #PCDATA must appear immediately after startGroup
        expectingPCData = true;
    }

    @Override
    public void pcdata(Augmentations augmentations) throws XNIException {
        if (expectingPCData) {
            // This confirms a mixed content model: (#PCDATA | ...)*
            contentElements.put(sCurrentElement, true);
        }
        expectingPCData = false;
    }

    @Override
    public void element(String elementName, Augmentations augmentations) throws XNIException {
        // If we see a child element before #PCDATA, it's a children model, not mixed
        expectingPCData = false;
    }

    @Override
    public void separator(short separator, Augmentations augmentations) throws XNIException {
        expectingPCData = false;
    }

    @Override
    public void endGroup(Augmentations augmentations) throws XNIException {
        expectingPCData = false;
    }

    @Override
    public void endContentModel(Augmentations augmentations) throws XNIException {
        sCurrentElement = null;
    }

    // Boilerplate methods (any, empty, occurrence, etc.) can be left empty 
    // unless you need to handle those specific cases.
    @Override public void any(Augmentations a) throws XNIException {}
    @Override public void empty(Augmentations a) throws XNIException {}
    @Override public void occurrence(short o, Augmentations a) throws XNIException {}
    @Override public void setDTDContentModelSource(XMLDTDContentModelSource source) { modelSource = source; }
    @Override public XMLDTDContentModelSource getDTDContentModelSource() { return modelSource; }

    // Helper to check results
    public boolean isMixedContent(String elementName) {
        return Boolean.TRUE.equals(contentElements.get(elementName));
    }
    
    public void dumpContentElements() {
    	System.out.println("map:");
    	System.out.print(contentElements.toString());
    }
}