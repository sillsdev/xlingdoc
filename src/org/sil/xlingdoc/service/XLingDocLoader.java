/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
	public static String loadFileIntoNeededHTML(XmlDocumentManager manager, DtdInspector inspector, String filePath) {
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
				// See if it has all of the DTDs
				XmlNameMapper.populateMapsFromDtd(inspector.getGrammar());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fileContent = Files.readString(Paths.get(filePath));
				int iBegin = fileContent.indexOf("<lingPaper");
				fileContent = fileContent.substring(iBegin);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fileContent = XmlNameMapper.mapInputFromXLingPaperToHTML(fileContent);
		sb.append(fileContent);
		sb.append("</body>\n");
		sb.append("</html>\n");
//		System.out.print(sb.toString());
		return sb.toString();
	}

}
