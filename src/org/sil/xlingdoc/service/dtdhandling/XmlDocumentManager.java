/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service.dtdhandling;

/**
 * code drafted by Gemini and Leo
 */
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.StringWriter;

public class XmlDocumentManager {
	private Document masterXmlDoc;
	private String dtdSystemId;
	private DocumentBuilder builder;
	int warningsCount = 0;
	int errorsCount = 0;
	int fatalErrorsCount = 0;

	public Document getMasterXmlDoc() {
		return masterXmlDoc;
	}

	public String getDtdSystemId() {
		return dtdSystemId;
	}

	public DocumentBuilder getBuilder() {
		return builder;
	}

	public int getWarningsCount() {
		return warningsCount;
	}

	public void setWarningsCount(int warningsCount) {
		this.warningsCount = warningsCount;
	}

	public int getErrorsCount() {
		return errorsCount;
	}

	public void setErrorsCount(int errorsCount) {
		this.errorsCount = errorsCount;
	}

	public int getFatalErrorsCount() {
		return fatalErrorsCount;
	}

	public void setFatalErrorsCount(int fatalErrorsCount) {
		this.fatalErrorsCount = fatalErrorsCount;
	}

	public void resetCounters() {
		errorsCount = 0;
		fatalErrorsCount = 0;
		warningsCount = 0;
	}

	public void loadXmlDocument(File xmlFile) throws Exception {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	    // 1. Turn OFF DTD validation during initial load so xi:include won't fail
	    factory.setValidating(false);
	    factory.setNamespaceAware(true);
	    factory.setXIncludeAware(true);

	    builder = factory.newDocumentBuilder();
	    
	    this.masterXmlDoc = builder.parse(xmlFile);

	    // Capture the DTD identifiers for later use
	    DocumentType doctype = masterXmlDoc.getDoctype();
	    if (doctype != null) {
	        this.dtdSystemId = doctype.getSystemId(); // e.g., "XLingPaper.dtd"
	    }

	    
	    String content = documentToString(masterXmlDoc);
//	    content = content.replace("?><", "?><!DOCTYPE lingPaper PUBLIC \"-//XMLmind//DTD XLingPap//EN\" \"../resources/dtds/XLingPap.dtd\"><");
	    content = content.replace("?><", "?><!DOCTYPE lingPaper PUBLIC \"-//XMLmind//DTD XLingPap//EN\" \"test/testdata/XLingPap.dtd\"><");
//	    System.out.println(content);
//	    DtdInspector dtdInspector = new DtdInspector(Constants.DTD_LOCATION, "(text)");
	    DtdInspector dtdInspector = new DtdInspector("test/testdata/XLingPap.dtd", "(text)");
	    factory.setValidating(true);
	    builder = factory.newDocumentBuilder();
	    builder.setErrorHandler(new org.xml.sax.ErrorHandler() {
	        public void warning(org.xml.sax.SAXParseException e) {
	            System.out.println("Warning:\n" + buildExceptionMessage(e));
	            warningsCount++;
	        }

	        public void error(org.xml.sax.SAXParseException e) {
//	            System.out.println("Error:\n" + buildExceptionMessage(e));
	            errorsCount++;
	        }

	        public void fatalError(org.xml.sax.SAXParseException e) {
	            System.out.println("Fatal error:\n" + buildExceptionMessage(e));
	            fatalErrorsCount++;
	        }
	    });

	    dtdInspector.parseXmlSnippet(this, builder, content);
	}

	public String documentToString(org.w3c.dom.Document doc) throws Exception {
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    
	    // Optional: Configure output properties
	    transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "no");
	    transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "no");
	    
	    DOMSource source = new DOMSource(doc);
	    StringWriter writer = new StringWriter();
	    StreamResult result = new StreamResult(writer);
	    
	    transformer.transform(source, result);
	    return writer.toString();
	}

	String buildExceptionMessage(SAXParseException e) {
		StringBuilder sb = new StringBuilder();
		sb.append(e.getMessage());
		sb.append(" At line ");
		sb.append(e.getLineNumber());
		sb.append(" at column ");
		sb.append(e.getColumnNumber());
		sb.append(".");
		// TODO: get the location so we can create a link for the user to click on to go
		// to the offending location.
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
}