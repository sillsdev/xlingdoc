/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package application.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class InternalToExternalNameMapper {

	private static final Map<String, String> elementCaseMap = new HashMap<>();
	private static final Map<String, String> attributeCaseMap = new HashMap<>();

	static {
		elementCaseMap.put("ARTICLE", "article");
    	elementCaseMap.put("AUTHOR", "author");
    	elementCaseMap.put("BACKMATTER", "backMatter");
    	elementCaseMap.put("BOOK", "book");
    	elementCaseMap.put("COMMENT", "comment");
    	elementCaseMap.put("ENDNOTES", "endnotes");
    	elementCaseMap.put("EXAMPLE", "example");
    	elementCaseMap.put("EXAMPLEREF", "exampleRef");
    	elementCaseMap.put("FREE", "free");
    	elementCaseMap.put("FRONTMATTER", "frontMatter");
    	elementCaseMap.put("GLOSS", "gloss");
    	elementCaseMap.put("INSTITUTION", "institution");
    	elementCaseMap.put("INTERLINEAR", "interlinear");
    	elementCaseMap.put("JPAGES", "jPages");
    	elementCaseMap.put("JTITLE", "jTitle");
    	elementCaseMap.put("JVOL", "jVol");
    	elementCaseMap.put("LANGDATA", "langData");
    	elementCaseMap.put("LANGUAGE", "language");
    	elementCaseMap.put("LANGUAGES", "languages");
    	elementCaseMap.put("LINE", "line");
    	elementCaseMap.put("LINEGROUP", "lineGroup");
    	elementCaseMap.put("LINGPAPER", "lingPaper");
    	elementCaseMap.put("LOCATION", "location");
    	elementCaseMap.put("MS", "ms");
    	elementCaseMap.put("P", "p");
    	elementCaseMap.put("PUBLISHER", "publisher");
    	elementCaseMap.put("REFAUTHOR", "refAuthor");
    	elementCaseMap.put("REFDATE", "refDate");
    	elementCaseMap.put("REFTITLE", "refTitle");
    	elementCaseMap.put("REFWORK", "refWork");
    	elementCaseMap.put("REFERENCES", "references");
    	elementCaseMap.put("SECTITLE", "secTitle");
    	elementCaseMap.put("SECTION1", "section1");
    	elementCaseMap.put("SECTIONREF", "sectionRef");
    	elementCaseMap.put("TABLE", "table");
    	elementCaseMap.put("TD", "td");
    	elementCaseMap.put("TH", "th");
    	elementCaseMap.put("TITLE", "title");
    	elementCaseMap.put("TR", "tr");
    	elementCaseMap.put("TYPE", "type");
    	elementCaseMap.put("TYPES", "types");

		// Register your structural attribute names here
		attributeCaseMap.put("CSSSPECIAL", "cssSpecial");
		attributeCaseMap.put("XELATEXSPECIAL", "XeLaTeXSpecial");
		attributeCaseMap.put("XSL-FOSPECIAL", "xsl-foSpecial");
	}

	/**
	 * 
	 */
	public static String mapName(String nameToMap) {
		String result = nameToMap;
		if (elementCaseMap.containsKey(nameToMap)) {
			result = elementCaseMap.get(nameToMap);
		} else if (attributeCaseMap.containsKey(nameToMap)) {
			result = attributeCaseMap.get(nameToMap);
		}
		return result;	
	}

}
