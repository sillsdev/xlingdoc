/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service.fileio;

import java.io.File;
import java.util.ResourceBundle;

import org.sil.xlingdoc.service.dtdhandling.DtdInspector;
import org.sil.xlingdoc.service.dtdhandling.XmlDocumentManager;
import org.sil.xlingdoc.service.dtdhandling.XmlNameMapper;
import org.w3c.dom.Document;

/**
 * 
 */
public class XLingDocLoader {

	/**
	 * 
	 */
	public XLingDocLoader() {
		// TODO Auto-generated constructor stub
	}
	public static String loadFileIntoNeededHTML(XmlDocumentManager manager, DtdInspector inspector, String filePath, ResourceBundle bundle) {
		StringBuilder sb= new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<!DOCTYPE html>\n");
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("</head>\n");
		sb.append("<body contenteditable=\"true\">\n");
		String fileContent = "";
		File f = new File(filePath);
		if (!f.exists()) {
			System.out.println(filePath + " not found");
		} else {
			try {
				manager.loadXmlDocument(f);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Document doc = manager.getMasterXmlDoc();
				Document newDoc;
				fileContent = manager.documentToString(doc);
//				System.out.println("before doc change ========================================");
//				System.out.println(fileContent);
//				System.out.println("before doc change ========================================");
				newDoc =  XmlNameMapper.mapInputFromXLingPaperToHTML(manager.getMasterXmlDoc(), bundle);
				fileContent = manager.documentToString(newDoc /*manager.getMasterXmlDoc()*/);
//				System.out.println("After doc change ========================================");
//				System.out.println(fileContent);
//				System.out.println("After doc change ========================================");
//				int iBegin = fileContent.indexOf("<lingPaper");
				int iBegin = fileContent.indexOf("<div>");
//				iBegin = fileContent.substring(iBegin).indexOf("<div>");
				fileContent = fileContent.substring(iBegin);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		fileContent = XmlNameMapper.mapInputFromXLingPaperToHTML(fileContent);
		sb.append(fileContent);
		sb.append("</body>\n");
		sb.append("</html>\n");
//		System.out.print(sb.toString());
		return sb.toString();
	}

}
