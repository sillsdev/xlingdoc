/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package application.service;

/**
 * code drafted by Gemini
 */
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XmlDocumentManager {
    private Document masterXmlDoc;
    private String dtdSystemId;

    public void loadXmlDocument(File xmlFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        // Turn on DTD validation for the Java side
        factory.setValidating(true); 
        factory.setNamespaceAware(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        
        // Set an error handler to catch DTD validation errors on load
        builder.setErrorHandler(new org.xml.sax.ErrorHandler() {
            public void warning(org.xml.sax.SAXParseException e) {
            	System.out.println("Warning:\n" + buildExceptionMessage(e));
            }
            public void error(org.xml.sax.SAXParseException e) {
            	System.out.println("Error:\n" + buildExceptionMessage(e));
//            	throws org.xml.sax.SAXException { throw e; }
            }
            public void fatalError(org.xml.sax.SAXParseException e) {
            	System.out.println("Fatal error:\n" + buildExceptionMessage(e));
//            	throws org.xml.sax.SAXException { throw e; }
            }
        });

        // Parse your file into standard Java space
        this.masterXmlDoc = builder.parse(xmlFile);

        // Capture the DTD identifiers for later use
        DocumentType doctype = masterXmlDoc.getDoctype();
        if (doctype != null) {
            this.dtdSystemId = doctype.getSystemId(); // e.g., "XLingPaper.dtd"
        }
    }

    String buildExceptionMessage(SAXParseException e) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(e.getMessage());
    	sb.append(" At line ");
    	sb.append(e.getLineNumber());
    	sb.append(" at column ");
    	sb.append(e.getColumnNumber());
    	sb.append(".");
    	// TODO: get the location so we can create a link for the user to click on to go to the offending location.
//    	sb.append(".\n\t");
//    	try {
//			String fileContent = Files.readString(Paths.get(xmlFile.getAbsolutePath()));
//			Stream<String> lines = fileContent.lines();
//			sb.append(lines.toArray()[]);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
    	
    	return sb.toString();
    }
    
    public Document getMasterXmlDoc() { return masterXmlDoc; }
    public String getDtdSystemId() { return dtdSystemId; }
}