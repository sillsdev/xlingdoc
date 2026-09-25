/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

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

/**
 * 
 */
public class XLingDocXmlToInternalHtmlMapper {
	
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
		if (elementInputBoxAttributeLightModeMap.containsKey(tagName)) {
			InputBoxInfo info = elementInputBoxAttributeLightModeMap.get(tagName);
			addInputBox(elNew, info, docNew);
			if (tagName.equals("refAuthor")) {
				// refAutor exceptionally needs two input boxes
				// we add the name of the attribute to the tagname to avoid a duplicate in the map
				info = elementInputBoxAttributeLightModeMap.get("refAuthorname");
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

	private static final Map<String, InputBoxInfo> elementInputBoxAttributeLightModeMap = Map.ofEntries(
			Map.entry("abbreviation", new InputBoxInfo("id", "15", "#FFFFBB", false, "")),
			Map.entry("annotatedBibliographyType", new InputBoxInfo("id", "15", "#FFFFBB", false, "")),
			Map.entry("annotation", new InputBoxInfo("id", "15", "#F6CAF6", false, "")),
			Map.entry("authorContact", new InputBoxInfo("id", "15", "#FFD788", false, "")),
			Map.entry("chapter", new InputBoxInfo("id", "15", "##D8BFD8", true, "")),
			Map.entry("chapterBeforePart", new InputBoxInfo("id", "15", "##D8BFD8", true, "")),
			Map.entry("chapterInCollection", new InputBoxInfo("id", "15", "##D8BFD8", true, "")),
			Map.entry("contentType", new InputBoxInfo("id", "15", "", false, "")),
			Map.entry("endnote", new InputBoxInfo("id", "15", "#E0E0E0", false, ")")),
			Map.entry("example", new InputBoxInfo("num", "15", "#F5DEB3", false, ")")),
			Map.entry("figure", new InputBoxInfo("id", "15", "#FFCCA0", false, ")")),
			Map.entry("framedType", new InputBoxInfo("id", "15", "", false, "")),
			Map.entry("genericTarget", new InputBoxInfo("id", "15", "#AFEEEE", false, "")),
			Map.entry("glossaryTerm", new InputBoxInfo("id", "15", "#FFB6C1", false, "")),
			Map.entry("indexTerm", new InputBoxInfo("id", "15", "#ccffcc", false, "")),
			Map.entry("langName", new InputBoxInfo("id", "15", "", false, "")),
			Map.entry("language", new InputBoxInfo("id", "15", "", false, "")),
			Map.entry("object", new InputBoxInfo("type", "15", "#CCCCCC", false, "")),
			Map.entry("part", new InputBoxInfo("id", "15", "#B0B0F0", false, "")),
			Map.entry("refAuthor", new InputBoxInfo("citename", "15", "#F09FF0", false, "")),
			Map.entry("refAuthorname", new InputBoxInfo("name", "40", "", false, "\u00a0\u00a0\u00a0")),
			Map.entry("refWork", new InputBoxInfo("id", "15", "#F09FF0", false, "")),
			Map.entry("section1", new InputBoxInfo("id", "15", "#BFD0FF", true, "")),
			Map.entry("section2", new InputBoxInfo("id", "15", "#BFD0FF", true, "")),
			Map.entry("section3", new InputBoxInfo("id", "15", "#BFD0FF", true, "")),
			Map.entry("section4", new InputBoxInfo("id", "15", "#BFD0FF", true, "")),
			Map.entry("section5", new InputBoxInfo("id", "15", "#BFD0FF", true, "")),
			Map.entry("section6", new InputBoxInfo("id", "15", "#BFD0FF", true, "")),
			Map.entry("tablenumbered", new InputBoxInfo("id", "15", "#F0CCA0", false, "")),
			Map.entry("type", new InputBoxInfo("id", "15", "", false, ""))
	);

	// elements with same name as in HTML may not load the way we want.
	public final static List<String> elementsToRename = List.of("br",
//			"li",
//			"ol",
//			"p",
//			"table",
//			"td",
//			"th",
			"title"
//			"ul"
	);


}
