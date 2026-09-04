/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.sil.utility.StringUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class WebPageInteractor {

	Document document;
	
	public WebPageInteractor() {
		// TODO Auto-generated constructor stub
	}
	
	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	/**
     * Called directly from JavaScript when a text box value changes.
     * 
     * @param elementId The internal ID or path identifying the XML element.
     * @param attributeName The attribute being edited (e.g., "id").
     * @param newValue The new value typed into the text box by the user.
	 * @throws XPathExpressionException 
     */
    public void updateAttribute(String elementName, String attributeName, String attributeValue, String newValue) {
//        System.out.println("Updating XML element [" + elementName + "] " + attributeName + " = " + newValue);

//        Element el = document.getElementById(elementId);
        if (StringUtilities.isNullOrEmpty(elementName)) {
			try {
	        	XPath xPath = XPathFactory.newInstance().newXPath();
	        	String sXPath = buildXPath(elementName, attributeName, attributeValue);;
	        	NodeList nodes = (NodeList)xPath.evaluate(sXPath, document, XPathConstants.NODESET);
	        	if (nodes.getLength() > 0) {
	        		Element element = (Element) nodes.item(0);
	            	String sIdBefore = element.getAttribute(attributeName);
	            	element.setAttribute(attributeName, newValue);
	            	String sIdAfter = element.getAttribute(attributeName);
//	            	System.out.println(element.getNodeName() + " " + attributeName + " changed from " + sIdBefore + " to " + sIdAfter);
	        	}
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

	private String buildXPath(String elementName, String attributeName, String attributeValue) {
		StringBuilder sb = new StringBuilder();
		sb.append("/HTML/BODY//");
		sb.append(elementName.toUpperCase());
		sb.append("[@");
		sb.append(attributeName);
		sb.append("='");
		sb.append(attributeValue);
		sb.append("']");
		return sb.toString();
	}
}
