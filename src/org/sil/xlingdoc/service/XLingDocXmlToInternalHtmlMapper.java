/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sil.utility.StringUtilities;
import org.sil.xlingdoc.Constants;
import org.sil.xlingdoc.model.CollapsingInfo;
import org.sil.xlingdoc.model.InputBoxInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javafx.scene.web.WebEngine;

/**
 * 
 */
public class XLingDocXmlToInternalHtmlMapper {

	static String abbreviationBackgroundColor = "#FFFFBB";
	static String annotatedbibliographytypeBackgroundColor = "#FBDFFB";
	static String annotationBackgroundColor = "#F6CAF6";
	static String appendixBackgroundColor = "#D8BFD8";
	static String authorContactBackgroundColor = "#FFD788";
	static String chapterBackgroundColor = "#D8BFD8";
	static String citationBackgroundColor = "#F09FF0";
	static String commentBackgroundColor = " yellow";
	static String endnoteBackgroundColor = "#E0E0E0";
	static String exampleBackgroundColor = "#F5DEB3";
	static String figureBackgroundColor = "#FFCCA0";
	static String genericBackgroundColor = "#AFEEEE";
	static String glossarytermBackgroundColor = "#FFB6C1";
	static String indexBackgroundColor = "#ccffcc";
	static String indexRangeBackgroundColor = "#ccffaa";
	static String interlinearSourceBackgroundColor = "#F0D0B0";
	static String iso6393codeBackgroundColor = "#B0E0E6";
	static String objectBackgroundColor = "#CCCCCC";
	static String partBackgroundColor = "#B0B0F0";
	static String sectionBackgroundColor = "#BFD0FF";
	static String tablenumberedBackgroundColor = "#F0CCA0";

	private static final Map<String, InputBoxInfo> elementInputBoxAttributeMap = new HashMap<String, InputBoxInfo>();

	public static void resetElementInputBoxAtrributeMap(WebEngine webEngine) {
		if (webEngine != null) {
			setBackgroundColors(webEngine);
		}
		setElementInputBoxAtrributeMap();
	}

	protected static void setElementInputBoxAtrributeMap() {
		elementInputBoxAttributeMap.clear();
		elementInputBoxAttributeMap.put("abbreviation", new InputBoxInfo("id", "15", abbreviationBackgroundColor, false, ""));
		elementInputBoxAttributeMap.put("annotatedBibliographyType", new InputBoxInfo("id", "15", annotatedbibliographytypeBackgroundColor, false, ""));
		elementInputBoxAttributeMap.put("annotation", new InputBoxInfo("id", "15", annotationBackgroundColor, false, ""));
		elementInputBoxAttributeMap.put("authorContact", new InputBoxInfo("id", "15", authorContactBackgroundColor, false, ""));
		elementInputBoxAttributeMap.put("chapter", new InputBoxInfo("id", "15", chapterBackgroundColor, true, ""));
		elementInputBoxAttributeMap.put("chapterBeforePart", new InputBoxInfo("id", "15", chapterBackgroundColor, true, ""));
		elementInputBoxAttributeMap.put("chapterInCollection", new InputBoxInfo("id", "15", chapterBackgroundColor, true, ""));
		elementInputBoxAttributeMap.put("contentType", new InputBoxInfo("id", "15", "", false, ""));
		elementInputBoxAttributeMap.put("endnote", new InputBoxInfo("id", "15", endnoteBackgroundColor, false, ")"));
		elementInputBoxAttributeMap.put("example", new InputBoxInfo("num", "15", exampleBackgroundColor, false, ")"));
		elementInputBoxAttributeMap.put("figure", new InputBoxInfo("id", "15", figureBackgroundColor, false, ")"));
		elementInputBoxAttributeMap.put("framedType", new InputBoxInfo("id", "15", "", false, ""));
		elementInputBoxAttributeMap.put("genericTarget", new InputBoxInfo("id", "15", genericBackgroundColor, false, ""));
		elementInputBoxAttributeMap.put("glossaryTerm", new InputBoxInfo("id", "15", glossarytermBackgroundColor, false, ""));
		elementInputBoxAttributeMap.put("indexTerm", new InputBoxInfo("id", "15", indexBackgroundColor, false, ""));
		elementInputBoxAttributeMap.put("langName", new InputBoxInfo("id", "15", "", false, ""));
		elementInputBoxAttributeMap.put("language", new InputBoxInfo("id", "15", "", false, ""));
		elementInputBoxAttributeMap.put("object", new InputBoxInfo("type", "15", objectBackgroundColor, false, ""));
		elementInputBoxAttributeMap.put("part", new InputBoxInfo("id", "15", partBackgroundColor, false, ""));
		elementInputBoxAttributeMap.put("refAuthor", new InputBoxInfo("citename", "15", citationBackgroundColor, false, ""));
		elementInputBoxAttributeMap.put("refAuthorname", new InputBoxInfo("name", "40", "", false, "\u00a0\u00a0\u00a0"));
		elementInputBoxAttributeMap.put("refWork", new InputBoxInfo("id", "15", citationBackgroundColor, false, ""));
		elementInputBoxAttributeMap.put("section1", new InputBoxInfo("id", "15", sectionBackgroundColor, true, ""));
		elementInputBoxAttributeMap.put("section2", new InputBoxInfo("id", "15", sectionBackgroundColor, true, ""));
		elementInputBoxAttributeMap.put("section3", new InputBoxInfo("id", "15", sectionBackgroundColor, true, ""));
		elementInputBoxAttributeMap.put("section4", new InputBoxInfo("id", "15", sectionBackgroundColor, true, ""));
		elementInputBoxAttributeMap.put("section5", new InputBoxInfo("id", "15", sectionBackgroundColor, true, ""));
		elementInputBoxAttributeMap.put("section6", new InputBoxInfo("id", "15", sectionBackgroundColor, true, ""));
		elementInputBoxAttributeMap.put("tablenumbered", new InputBoxInfo("id", "15", tablenumberedBackgroundColor, false, ""));
		elementInputBoxAttributeMap.put("type", new InputBoxInfo("id", "15", "", false, ""));
	}

	protected static void setBackgroundColors(WebEngine webEngine) {
		abbreviationBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine,
				"--abbreviation-background-color");
		annotatedbibliographytypeBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine,
				"--annotatedbibliographytype-background-color");
		annotationBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--annotation-background-color");
		appendixBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--appendix-background-color");
		authorContactBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine,
				"--authorContact-background-color");
		chapterBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--chapter-background-color");
		citationBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--citation-background-color");
		commentBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--comment-background-color");
		endnoteBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--endnote-background-color");
		exampleBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--example-background-color");
		figureBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--figure-background-color");
		genericBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--generic-background-color");
		glossarytermBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine,
				"--glossaryterm-background-color");
		indexBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--index-background-color");
		indexRangeBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--indexRange-background-color");
		interlinearSourceBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine,
				"--interlinearSource-background-color");
		iso6393codeBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--iso639-3code-background-color");
		objectBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--object-background-color");
		partBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--part-background-color");
		sectionBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine, "--section-background-color");
		tablenumberedBackgroundColor = WebPageUtilities.getCssVariableValue(webEngine,
				"--tablenumbered-background-color");
	}
	// TODO: add parameter for light vs. dark mode so we get the right colors for the input boxes
	// unless we can find a way to get access to the CSS file and use its variables
	public static Document mapInputFromXLingPaperToHTML(Document doc, ResourceBundle bundle) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document docNew = builder.newDocument();
			Element topDiv = docNew.createElement("div");
			docNew.appendChild(topDiv);
			Element root = doc.getDocumentElement();
			docNew = mapAndWrapInDivSpanOrDetails(doc, root, docNew, topDiv, bundle);
			return docNew;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("returning orig doc");
		return doc;
	}

	private static Document mapAndWrapInDivSpanOrDetails(Document doc, Element el, Document docNew, Element elNew, ResourceBundle bundle) {
		String elName = el.getTagName();
		Element parentNew;
		if (elNew.getParentNode() == null || elNew.getParentNode().getNodeType() == Node.DOCUMENT_NODE) {
			parentNew = elNew;
		} else {
			parentNew = (Element) elNew.getParentNode();
		}
		if (elementsToWrapInDiv.contains(elName)) {
			docNew = wrapElementIn("div", doc, el, docNew, parentNew, bundle);
		} else if(elementsToWrapInSpan.contains(elName)) {
			docNew = wrapElementIn("span", doc, el, docNew, parentNew, bundle);
		}
		return docNew;
	}

	private static Document wrapElementIn(String sWrapperName, Document doc, Element el, Document docNew, Element parentNew, ResourceBundle bundle) {
		Element wrapper = docNew.createElement(sWrapperName);
		Element elNew = (Element) cloneANode(el, docNew, false);//docNew.createElement(el.getTagName());
		Element subEl = elNew;
		String ignoreElementInSummary = "";
		String tagName = el.getTagName();
		if (elementsToRename.contains(tagName)) {
			elNew = (Element) docNew.renameNode(elNew, null, Constants.ELEMENT_RENAME_PREFIX + tagName);
		}
		CollapsingInfo collapseInfo = elementsToWrapInDetailsSummaryMap.get(tagName);
		if (collapseInfo != null) {
			ignoreElementInSummary = collapseInfo.includeElementInSummary();
		}
		if (elementsToWrapInDetailsSummaryMap.containsKey(tagName)) {
			Element summary = handleWrapInDetailsSummary(el, docNew, bundle, elNew, tagName, collapseInfo);
			subEl = summary;
		}
		if (elementInputBoxAttributeMap.containsKey(tagName)) {
			InputBoxInfo info = elementInputBoxAttributeMap.get(tagName);
			addInputBox(elNew, info, docNew);
			if (tagName.equals("refAuthor")) {
				// refAutor exceptionally needs two input boxes
				// we add the name of the attribute to the tagname to avoid a duplicate in the map
				info = elementInputBoxAttributeMap.get("refAuthorname");
				addInputBox(elNew, info, docNew);
			}
		}
		for (int i = 0; i < el.getChildNodes().getLength(); i++) {
			Node node = el.getChildNodes().item(i);
			if (node instanceof Element el2) {
				if (!ignoreElementInSummary.equals(el2.getTagName())) {
					docNew = mapAndWrapInDivSpanOrDetails(doc, el2, docNew, subEl, bundle);
				}
			} else if (node instanceof Text text) {
				Element span = docNew.createElement("span");
				Text textNew = (Text) cloneANode(text, docNew, true);
				span.appendChild(textNew);
				elNew.appendChild(span);
			}
		}
		wrapper.appendChild(elNew);
		parentNew.appendChild(wrapper);
		return docNew;
	}

	private static Node cloneANode(Node node, Document docNew, boolean deepClone) {
		Node nodeNew = node.cloneNode(deepClone);
		Node nodeImported = docNew.importNode(nodeNew, deepClone);
		return nodeImported;
	}

	private static Element handleWrapInDetailsSummary(Element el, Document docNew, ResourceBundle bundle, Element elNew,
			String tagName, CollapsingInfo info) {
		Element details = docNew.createElement("details");
		Element summary = docNew.createElement("summary");
		String attributeOverride = info.attributeOverride();
		if (!StringUtilities.isNullOrEmpty(attributeOverride)) {
			summary.setTextContent(el.getAttribute(attributeOverride));
		} else {
			String localizationKey = info.localizationKey();
			if (!StringUtilities.isNullOrEmpty(localizationKey)) {
				summary.setTextContent(bundle.getString(localizationKey));
			}  else {
				String elementInSummary = info.includeElementInSummary();
				if (!StringUtilities.isNullOrEmpty(elementInSummary)) {
					for (int i = 0; i < el.getChildNodes().getLength(); i++) {
						Node node = el.getChildNodes().item(i);
						if (node instanceof Element elSum) {
							if (elSum.getTagName().equals(elementInSummary)) {
								summary.appendChild(cloneANode(elSum, docNew, true));
								break;
							}
						}
					}
				}
			}
		}
		if (!info.beginCollapsed()) {
			details.setAttribute("open", "true");
		}
		details.appendChild(summary);
		elNew.appendChild(details);
		return summary;
	}

	private static void addInputBox(Element elNew, InputBoxInfo info, Document docNew) {
		String sId = elNew.getAttribute(info.idAttribute());
		Element input = docNew.createElement("input");
		input.setAttribute("type", "text");
		input.setAttribute("value", sId);
		String sArgs = buildArguments(elNew, info.idAttribute(), sId);
		input.setAttribute("oninput", sArgs);
		input.setAttribute("class", "text-box-editor");
		input.setAttribute("style", "background-color:" + info.backgoundColor());
		// set the number of characters to show in the input box
		input.setAttribute("size", info.boxTextSize());
		if (info.hasDetails()) {
			Node summary = elNew.getFirstChild().getFirstChild();
			summary.insertBefore(input, summary.getFirstChild());
		} else {
			if (info.textAfterBox().length() > 0) {
				Element spanAfter = docNew.createElement("span");
				spanAfter.setTextContent(info.textAfterBox());
				spanAfter.setAttribute("contenteditable", "false");
				elNew.insertBefore(spanAfter, elNew.getFirstChild());
			}
			elNew.insertBefore(input, elNew.getFirstChild());
		}
	}

	private static String buildArguments(Element el, String sIdAttribute, String sId) {
		StringBuilder sb = new StringBuilder();
		sb.append("xLingDocApp.updateAttribute('");
		sb.append(el.getNodeName());
		sb.append("', '");
		sb.append(sIdAttribute);
		sb.append(sId);
		sb.append("', this.value)");
		return sb.toString();
	}

	public static String getRenamedElement(String elementName) {
		String name = elementName;
		int index = elementsToRename.indexOf(elementName.replace(Constants.ELEMENT_RENAME_PREFIX.toUpperCase(), "").toLowerCase());
		if (index > -1) {
			name = elementsToRename.get(index);
		}
		return name;
	}

	private final static List<String> elementsToWrapInDiv = List.of(
			"annotationRef",
			"author",
			"backMatter",
			"blockquote",
			"chart",
			"chart",
			"dl",
			"endnotes",
			"example",
			"figure",
			"free",
			"frontMatter",
			"hangingIndent",
			"interlinear",
			"interlinear-text",
			"language",
			"languages",
			"line",
			"lineGroup",
			"lingPaper",
			"ol",
			"p",
			"pc",
			"prose-text",
			"refAuthor",
			"refWork",
			"references",
			"section1",
			"table",
			"tablenumbered",
			"title",
			"tree",
			"type",
			"types",
			"ul",
			"xlingpaper"
			);

	private final static List<String> elementsToWrapInSpan = List.of(
			"abbrRef",
			"annotations",
			"appendixRef",
			"article",
			"authorRole",
			"book",
			"bookTotalPages",
			"bookversion",
			"br",
			"bVol",
			"citation",
			"collection",
			"collCitation",
			"collEd",
			"collEdInitials",
			"collEdSurnameGivenName",
			"collPages",
			"collTitle",
			"collTitleLowerCase",
			"collVol",
			"comment",
			"conference",
			"dateAccessed",
			"dissertation",
			"doi",
			"edition",
			"editor",
			"editorInitials",
			"editorSurnameGivenName",
			"empty",
			"endnote",
			"endnoteRef",
			"exampleRef",
			"fieldNotes",
			"figureRef",
			"genericRef",
			"genericTarget",
			"gloss",
			"glossaryTermRef",
			"img",
			"indexedItem",
			"indexedRangeBegin",
			"indexedRangeEnd",
			"institution",
			"interlinearRefCitation",
			"iso639-3code",
			"iso639-3codeRef",
			"jArticleNumber",
			"jIssueNumber",
			"jPages",
			"jTitle",
			"jVol",
			"keywords",
			"langData",
			"link",
			"location",
			"mediaObject",
			"ms",
			"msVersion",
			"multivolumeWork",
			"object",
			"paper",
			"proceedings",
			"procCitation",
			"procEd",
			"procEdInitials",
			"procEdSurnameGivenName",
			"procPages",
			"procTitle",
			"procTitleLowerCase",
			"procVol",
			"published",
			"publisher",
			"q",
			"refDate",
			"refTitle",
			"refTitleLowerCase",
			"reprintInfo",
			"sectionRef",
			"secTitle",
			"series",
			"seriesEd",
			"seriesEdInitials",
			"seriesEdSurnameGivenName",
			"tablenumberedRef",
			"thesis",
			"series",
			"seriesEd",
			"seriesEdInitials",
			"seriesEdSurnameGivenName",
			"translatedBy",
			"url",
			"webPage",
			"CMOSNandBShortCitationTitle"
	);

	private static final Map<String, CollapsingInfo> elementsToWrapInDetailsSummaryMap = Map.ofEntries(
			Map.entry("languages", new CollapsingInfo(true, "collapsing.languages", "", "")),
			Map.entry("references", new CollapsingInfo(true, "collapsing.references", "", "label")),
			Map.entry("refAuthor", new CollapsingInfo(false, "collapsing.refworks", "", "")),
			Map.entry("section1", new CollapsingInfo(true, "", "secTitle", "")),
			Map.entry("types", new CollapsingInfo(true, "collapsing.types", "", ""))
			);

	// elements with same name as in HTML may not load the way we want.
	public final static List<String> elementsToRename = List.of("br",
//			"li",
//			"ol",
			"p",
//			"table",
//			"td",
//			"th",
			"title"
//			"ul"
	);

}
