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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.Map;

public class XmlNameMapper {

	// The element name and attribute name maps are at the end of this class

	final static String kDetailsSummaryBegin = "<details><summary>";
	final static String kSummaryEnd = "</summary";
	final static String kDetailsEnd = "</details>";

		// Element names end up being lower case because the WebView runs a WebKit
		// rendering engine, which strictly treats content as HTML5. Under the HTML5 specification,
		// tag and attribute names are treated as case-insensitive and are automatically
		// normalized to lower case when you extract mark-up using .outerHTML.
		// So we need to map any camel-case names.

	public static Document sanitizeAndFixCasing(Element element, Document doc) {
		System.out.println("element = '" + element.getTagName() + "'");
		// 2. Clear out contenteditable fields as before
		element.removeAttribute("contenteditable");

		// 3. Process children recursively FIRST to keep references stable
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				doc = sanitizeAndFixCasing((Element) child, doc);
			}
		}

		// 4. Correct Attribute Casing
		// Standard DOM maps don't let you rename attributes, so you replace them
		org.w3c.dom.NamedNodeMap attributes = element.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attr = attributes.item(i);
			String lowerName = attr.getNodeName().toLowerCase();
			System.out.println("\tattr = '" + attr.getNodeName() + "'");

			if (attributeNameMap.containsKey(lowerName)) {
				String correctCaseName = attributeNameMap.get(lowerName);
				String value = attr.getNodeValue();

				// Strip the old lowercased attribute and write the correctly cased version
				element.removeAttribute(attr.getNodeName());
				element.setAttribute(correctCaseName, value);
			}
		}

		// 5. Correct Tag/Element Casing
		String lowerTagName = element.getTagName().toLowerCase();
		System.out.println("\t\tlowerTagName = '" + lowerTagName + "'");
		if (elementNameMap.containsKey(lowerTagName)) {
			String correctTagName = elementNameMap.get(lowerTagName);
			System.out.println("\t\tcorrectTagName = '" + correctTagName + "'");

			if (!element.getTagName().equals(correctTagName)) {
				// Rename the node by creating a replacement element shell
				Element renamedElement = doc.createElement(correctTagName);

				// Migrate attributes
				while (element.getAttributes().getLength() > 0) {
					Node attr = element.getAttributes().item(0);
					element.getAttributes().removeNamedItem(attr.getNodeName());
					renamedElement.setAttributeNode((org.w3c.dom.Attr) attr);
				}

				// Migrate children
				while (element.hasChildNodes()) {
					renamedElement.appendChild(element.getFirstChild());
				}

				// Swap the old lowercased placeholder shell out of the tree structure
				element.getParentNode().replaceChild(renamedElement, element);
			}
		}
		System.out.println("\telement = '" + element.getTagName() + "'");
		return doc;
	}

	public static String mapElementName(String name) {
		String nameToUse = name.toLowerCase();
		return elementNameMap.getOrDefault(nameToUse, nameToUse);
	}

	public static String mapAttributeName(String name) {
		String nameToUse = name.toLowerCase();
		return attributeNameMap.getOrDefault(nameToUse, nameToUse);
	}

	public static String mapInputFromXLingPaperToHTML(String fileContent) {
		fileContent = mapElementName(fileContent, "br");
		// We may need to do something specific for tables...
//		fileContent = mapElementName(fileContent, "table");
//		fileContent = mapElementName(fileContent, "td");
//		fileContent = mapElementName(fileContent, "th");
//		fileContent = mapElementName(fileContent, "tr");
		fileContent = insertWrapping(fileContent);
		return fileContent;
	}

	static String mapElementName(String fileContent, String elementName) {
		fileContent = fileContent.replaceAll("<" + elementName, "<xlp-" + elementName);
		fileContent = fileContent.replaceAll("</" + elementName, "</xlp-" + elementName);
		return fileContent;
	}

	static String insertWrapping(String fileContent) {
		fileContent = fileContent.replaceAll("<secTitle", kDetailsSummaryBegin + "<secTitle");
		fileContent = fileContent.replaceAll("</secTitle", "</secTitle>" + kSummaryEnd);
		fileContent = fileContent.replaceAll("</section1", kDetailsEnd + "</section1");

		// TODO: be sure to use localized value for the wrap summary string
		fileContent = wrapElement(fileContent, "languages", "Languages");
		fileContent = wrapElement(fileContent, "types", "Types");

		return fileContent;
	}

	protected static String wrapElement(String fileContent, String elementToWrap, String wrapSummary) {
		fileContent = fileContent.replace("<" + elementToWrap, "<" + elementToWrap + ">" + kDetailsSummaryBegin + wrapSummary + kSummaryEnd);
		fileContent = fileContent.replace("</" + elementToWrap, kDetailsEnd + "</" + elementToWrap);
		return fileContent;
	}

	public static String getMappedElementName(String elementName) {
		String lowercaseName = elementName.toLowerCase();
		if (elementNameMap.containsKey(lowercaseName)) {
			return elementNameMap.get(lowercaseName);
		} else {
			return lowercaseName;
		}
	}
	private static final Map<String, String> elementNameMap = Map.ofEntries(
			Map.entry("abbrdefinition", "abbrDefinition"),
			Map.entry("abbrinlang", "abbrInLang"),
			Map.entry("abbrref", "abbrRef"),
			Map.entry("abbrreflinklayout", "abbrRefLinkLayout"),
			Map.entry("abbrterm", "abbrTerm"),
			Map.entry("abbreviationsinfootnotelayout", "abbreviationsInFootnoteLayout "), 
			Map.entry("abbreviationsintablelayout", "abbreviationsInTableLayout "), 
			Map.entry("abbreviationsshownhere", "abbreviationsShownHere"),
			Map.entry("abstractlayout", "abstractLayout"),
			Map.entry("abstracttextfontinfo", "abstractTextFontInfo"),
			Map.entry("acknowledgementslayout", "acknowledgementsLayout"),
			Map.entry("affiliationlayout", "affiliationLayout"),
			Map.entry("afterterm", "afterTerm"),
			Map.entry("annotatedbibliography", "annotatedBibliography"),
			Map.entry("annotatedbibliographytype", "annotatedBibliographyType"),
			Map.entry("annotatedbibliographytypes", "annotatedBibliographyTypes"),
			Map.entry("annotationlayout", "annotationLayout"),
			Map.entry("annotationref", "annotationRef"),
			Map.entry("appendicestitlepagelayout", "appendicesTitlePageLayout"),
			Map.entry("appendixlabelcontentchoices", "appendixLabelContentChoices"),
			Map.entry("appendixlayout", "appendixLayout"),
			Map.entry("appendixref", "appendixRef"),
			Map.entry("appendixreflayout", "appendixRefLayout"),
			Map.entry("appendixreflinklayout", "appendixRefLinkLayout"),
			Map.entry("appendixreftitlelayout", "appendixRefTitleLayout"),
			Map.entry("appendixtitlelayout", "appendixTitleLayout"),
			Map.entry("articlelayout", "articleLayout"),
			Map.entry("articlelayouts", "articleLayouts"),
			Map.entry("authorcontact", "authorContact"),
			Map.entry("authorcontactinfo", "authorContactInfo"),
			Map.entry("authorcontactinfolayout", "authorContactInfoLayout"),
			Map.entry("authorcontacts", "authorContacts"),
			Map.entry("authorlayout", "authorLayout"),
			Map.entry("authorrole", "authorRole"),
			Map.entry("authorroleitem", "authorRoleItem"),
			Map.entry("bvol", "bVol"),
			Map.entry("bvolitem", "bVolItem"),
			Map.entry("backmatter", "backMatter"),
			Map.entry("backmatterlayout", "backMatterLayout"),
			Map.entry("basicpointsize", "basicPointSize"),
			Map.entry("beforeterm", "beforeTerm"),
			Map.entry("blockquoteindent", "blockQuoteIndent"),
			Map.entry("blockquotelayout", "blockQuoteLayout"),
			Map.entry("bodylayout", "bodyLayout"),
			Map.entry("booklayout", "bookLayout"),
			Map.entry("booklayouts", "bookLayouts"),
			Map.entry("booktotalpages", "bookTotalPages"),
			Map.entry("booktotalpagesitem", "bookTotalPagesItem"),
			Map.entry("bookversionitem", "bookversionItem"),
			Map.entry("centerheaderfooteritem", "centerHeaderFooterItem"),
			Map.entry("chapterbeforepart", "chapterBeforePart"),
			Map.entry("chapterincollection", "chapterInCollection"),
			Map.entry("chapterincollectionauthor", "chapterInCollectionAuthor"),
			Map.entry("chapterincollectionbackmatterlayout", "chapterInCollectionBackMatterLayout"),
			Map.entry("chapterincollectionfrontmatterlayout", "chapterInCollectionFrontMatterLayout"),
			Map.entry("chapterincollectionlayout", "chapterInCollectionLayout"),
			Map.entry("chapterlabelcontentchoices", "chapterLabelContentChoices"),
			Map.entry("chapterlayout", "chapterLayout"),
			Map.entry("chapternumber", "chapterNumber"),
			Map.entry("chaptertitle", "chapterTitle"),
			Map.entry("chaptertitlelayout", "chapterTitleLayout"),
			Map.entry("citationlayout", "citationLayout"),
			Map.entry("citationlinklayout", "citationLinkLayout"),
			Map.entry("citename", "citeName"),
			Map.entry("collcitation", "collCitation"),
			Map.entry("collcitationitem", "collCitationItem"),
			Map.entry("colled", "collEd"),
			Map.entry("colledinitials", "collEdInitials"),
			Map.entry("colleditem", "collEdItem"),
			Map.entry("colledsurnamegivenname", "collEdSurnameGivenName"),
			Map.entry("collpages", "collPages"),
			Map.entry("collpagesitem", "collPagesItem"),
			Map.entry("colltitle", "collTitle"),
			Map.entry("colltitleitem", "collTitleItem"),
			Map.entry("colltitlelowercase", "collTitleLowerCase"),
			Map.entry("collvol", "collVol"),
			Map.entry("collvolitem", "collVolItem"),
			Map.entry("collectionlayout", "collectionLayout"),
			Map.entry("collectionlayouts", "collectionLayouts"),
			Map.entry("conferenceitem", "conferenceItem"),
			Map.entry("conflatedline", "conflatedLine"),
			Map.entry("contactaddress", "contactAddress"),
			Map.entry("contactaddresslayout", "contactAddressLayout"),
			Map.entry("contactaffiliation", "contactAffiliation"),
			Map.entry("contactaffiliationlayout", "contactAffiliationLayout"),
			Map.entry("contactelectronic", "contactElectronic"),
			Map.entry("contactelectroniclayout", "contactElectronicLayout"),
			Map.entry("contactemail", "contactEmail"),
			Map.entry("contactemaillayout", "contactEmailLayout"),
			Map.entry("contactname", "contactName"),
			Map.entry("contactnamelayout", "contactNameLayout"),
			Map.entry("contactphone", "contactPhone"),
			Map.entry("contactphonelayout", "contactPhoneLayout"),
			Map.entry("contentcontrol", "contentControl"),
			Map.entry("contentcontrolchoice", "contentControlChoice"),
			Map.entry("contentcontrolchoices", "contentControlChoices"),
			Map.entry("contentlayout", "contentLayout"),
			Map.entry("contenttype", "contentType"),
			Map.entry("contenttypes", "contentTypes"),
			Map.entry("contentslayout", "contentsLayout"),
			Map.entry("contentslinklayout", "contentsLinkLayout"),
			Map.entry("copyrightdate", "copyrightDate"),
			Map.entry("copyrightholder", "copyrightHolder"),
			Map.entry("copyrightpagelayout", "copyrightPageLayout"),
			Map.entry("dateaccessed", "dateAccessed"),
			Map.entry("dateaccesseditem", "dateAccessedItem"),
			Map.entry("datelayout", "dateLayout"),
			Map.entry("defaultfontfamily", "defaultFontFamily"),
			Map.entry("definitionlistlayout", "definitionListLayout"),
			Map.entry("dissertationlabelitem", "dissertationLabelItem"),
			Map.entry("dissertationlayout", "dissertationLayout"),
			Map.entry("dissertationlayouts", "dissertationLayouts"),
			Map.entry("doiitem", "doiItem"),
			Map.entry("doilinklayout", "doiLinkLayout"),
			Map.entry("editionitem", "editionItem"),
			Map.entry("editorinitials", "editorInitials"),
			Map.entry("editoritem", "editorItem"),
			Map.entry("editorsurnamegivenname", "editorSurnameGivenName"),
			Map.entry("emailaddress", "emailAddress"),
			Map.entry("emailaddresslayout", "emailAddressLayout"),
			Map.entry("emptyitem", "emptyItem"),
			Map.entry("endcaption", "endCaption"),
			Map.entry("endnoteref", "endnoteRef"),
			Map.entry("endnotereflayout", "endnoteRefLayout"),
			Map.entry("endnotereflinklayout", "endnoteRefLinkLayout"),
			Map.entry("exampleheading", "exampleHeading"),
			Map.entry("examplelayout", "exampleLayout"),
			Map.entry("exampleref", "exampleRef"),
			Map.entry("examplereflinklayout", "exampleRefLinkLayout"),
			Map.entry("fieldnotes", "fieldNotes"),
			Map.entry("fieldnoteslayout", "fieldNotesLayout"),
			Map.entry("fieldnoteslayouts", "fieldNotesLayouts"),
			Map.entry("figurecaptionlayout", "figureCaptionLayout"),
			Map.entry("figurelabellayout", "figureLabelLayout"),
			Map.entry("figurelayout", "figureLayout"),
			Map.entry("figurenumberlayout", "figureNumberLayout"),
			Map.entry("figureref", "figureRef"),
			Map.entry("figurerefcaptionlayout", "figureRefCaptionLayout"),
			Map.entry("figurereflayout", "figureRefLayout"),
			Map.entry("figurereflinklayout", "figureRefLinkLayout"),
			Map.entry("fixedtext", "fixedText"),
			Map.entry("footermargin", "footerMargin"),
			Map.entry("footnoteindent", "footnoteIndent"),
			Map.entry("footnotelayout", "footnoteLayout"),
			Map.entry("footnoteline", "footnoteLine"),
			Map.entry("footnotepointsize", "footnotePointSize"),
			Map.entry("framedtype", "framedType"),
			Map.entry("framedtypes", "framedTypes"),
			Map.entry("framedunit", "framedUnit"),
			Map.entry("freelayout", "freeLayout"),
			Map.entry("frontmatter", "frontMatter"),
			Map.entry("frontmatterlayout", "frontMatterLayout"),
			Map.entry("genericref", "genericRef"),
			Map.entry("genericreflinklayout", "genericRefLinkLayout"),
			Map.entry("generictarget", "genericTarget"),
			Map.entry("glossinexamplelayout", "glossInExampleLayout"),
			Map.entry("glossinlistwordlayout", "glossInListWordLayout"),
			Map.entry("glossinproselayout", "glossInProseLayout"),
			Map.entry("glossintablelayout", "glossInTableLayout"),
			Map.entry("glosslayout", "glossLayout"),
			Map.entry("glossarylayout", "glossaryLayout"),
			Map.entry("glossaryterm", "glossaryTerm"),
			Map.entry("glossarytermdefinition", "glossaryTermDefinition"),
			Map.entry("glossarytermdefinitionindefinitionlistlayout", "glossaryTermDefinitionInDefinitionListLayout "), 
			Map.entry("glossaryterminlang", "glossaryTermInLang"),
			Map.entry("glossarytermref", "glossaryTermRef"),
			Map.entry("glossarytermreflinklayout", "glossaryTermRefLinkLayout"),
			Map.entry("glossarytermterm", "glossaryTermTerm"),
			Map.entry("glossarytermtermindefinitionlistlayout", "glossaryTermTermInDefinitionListLayout "), 
			Map.entry("glossaryterms", "glossaryTerms"),
			Map.entry("glossarytermsindefinitionlistlayout", "glossaryTermsInDefinitionListLayout"),
			Map.entry("glossarytermsintablelayout", "glossaryTermsInTableLayout "), 
			Map.entry("glossarytermsshownhere", "glossaryTermsShownHere"),
			Map.entry("glossarytermsshownhereasdefinitionlist", "glossaryTermsShownHereAsDefinitionList"),
			Map.entry("hangingindent", "hangingIndent"),
			Map.entry("hangingindentinitialindent", "hangingIndentInitialIndent"),
			Map.entry("hangingindentnormalindent", "hangingIndentNormalIndent"),
			Map.entry("headercol", "headerCol"),
			Map.entry("headerfooterevenpage", "headerFooterEvenPage"),
			Map.entry("headerfooterfirstpage", "headerFooterFirstPage"),
			Map.entry("headerfooteroddevenpages", "headerFooterOddEvenPages"),
			Map.entry("headerfooteroddpage", "headerFooterOddPage"),
			Map.entry("headerfooterpage", "headerFooterPage"),
			Map.entry("headerfooterpagestyles", "headerFooterPageStyles"),
			Map.entry("headermargin", "headerMargin"),
			Map.entry("headerrow", "headerRow"),
			Map.entry("imageborderlayout", "imageBorderLayout"),
			Map.entry("indexlayout", "indexLayout"),
			Map.entry("indexlinklayout", "indexLinkLayout"),
			Map.entry("indexterm", "indexTerm"),
			Map.entry("indexterms", "indexTerms"),
			Map.entry("indexeditem", "indexedItem"),
			Map.entry("indexedrangebegin", "indexedRangeBegin"),
			Map.entry("indexedrangeend", "indexedRangeEnd"),
			Map.entry("institutionitem", "institutionItem"),
			Map.entry("interlinearalignedwordspacing", "interlinearAlignedWordSpacing"),
			Map.entry("interlinearmultiplelinegrouplayout", "interlinearMultipleLineGroupLayout"),
			Map.entry("interlinearref", "interlinearRef"),
			Map.entry("interlinearrefcitation", "interlinearRefCitation"),
			Map.entry("interlinearrefcitationtitlelayout", "interlinearRefCitationTitleLayout"),
			Map.entry("interlinearreflinklayout", "interlinearRefLinkLayout"),
			Map.entry("interlinearsource", "interlinearSource"),
			Map.entry("interlinearsourcestyle", "interlinearSourceStyle"),
			Map.entry("interlineartextlayout", "interlinearTextLayout"),
			Map.entry("iso639-3codesintablelayout", "iso639-3CodesInTableLayout "),
			Map.entry("iso639-3codeslinklayout", "iso639-3CodesLinkLayout"),
			Map.entry("iso639-3codeitem", "iso639-3codeItem"),
			Map.entry("iso639-3codeitemref", "iso639-3codeItemRef"),
			Map.entry("iso639-3coderef", "iso639-3codeRef"),
			Map.entry("iso639-3codesshownhere", "iso639-3codesShownHere"),
			Map.entry("jarticlenumber", "jArticleNumber"),
			Map.entry("jarticlenumberitem", "jArticleNumberItem"),
			Map.entry("jissuenumber", "jIssueNumber"),
			Map.entry("jissuenumberitem", "jIssueNumberItem"),
			Map.entry("jpages", "jPages"),
			Map.entry("jpagesitem", "jPagesItem"),
			Map.entry("jtitle", "jTitle"),
			Map.entry("jtitleitem", "jTitleItem"),
			Map.entry("jvol", "jVol"),
			Map.entry("jvolitem", "jVolItem"),
			Map.entry("keyterm", "keyTerm"),
			Map.entry("keywordlayout", "keywordLayout"),
			Map.entry("keywordslayout", "keywordsLayout"),
			Map.entry("keywordsshownhere", "keywordsShownHere"),
			Map.entry("labelcontent", "labelContent"),
			Map.entry("labelcontentchoices", "labelContentChoices"),
			Map.entry("langdata", "langData"),
			Map.entry("langdatainexamplelayout", "langDataInExampleLayout"),
			Map.entry("langdatainlistwordlayout", "langDataInListWordLayout"),
			Map.entry("langdatainproselayout", "langDataInProseLayout"),
			Map.entry("langdataintablelayout", "langDataInTableLayout"),
			Map.entry("langdatalayout", "langDataLayout"),
			Map.entry("langname", "langName"),
			Map.entry("leftheaderfooteritem", "leftHeaderFooterItem"),
			Map.entry("linegroup", "lineGroup"),
			Map.entry("lineset", "lineSet"),
			Map.entry("linesetrow", "lineSetRow"),
			Map.entry("linespacing", "lineSpacing"),
			Map.entry("lingpaper", "lingPaper"),
			Map.entry("linklayout", "linkLayout"),
			Map.entry("linklinklayout", "linkLinkLayout"),
			Map.entry("listdefinition", "listDefinition"),
			Map.entry("listinterlinear", "listInterlinear"),
			Map.entry("listlayout", "listLayout"),
			Map.entry("listoffiguresshownhere", "listOfFiguresShownHere"),
			Map.entry("listoftablesshownhere", "listOfTablesShownHere"),
			Map.entry("listsingle", "listSingle"),
			Map.entry("listword", "listWord"),
			Map.entry("literalcontentlayout", "literalContentLayout"),
			Map.entry("literallabellayout", "literalLabelLayout"),
			Map.entry("literallayout", "literalLayout"),
			Map.entry("locationitem", "locationItem"),
			Map.entry("locationpublisherlayout", "locationPublisherLayout"),
			Map.entry("locationpublisherlayouts", "locationPublisherLayouts"),
			Map.entry("locationpublisherlayoutsref", "locationPublisherLayoutsRef"),
			Map.entry("magnificationfactor", "magnificationFactor"),
			Map.entry("mediaobject", "mediaObject"),
			Map.entry("missingitem", "missingItem"),
			Map.entry("mslayout", "msLayout"),
			Map.entry("mslayouts", "msLayouts"),
			Map.entry("msversion", "msVersion"),
			Map.entry("msversionitem", "msVersionItem"),
			Map.entry("multivolumework", "multivolumeWork"),
			Map.entry("multivolumeworkitem", "multivolumeWorkItem"),
			Map.entry("numberlayout", "numberLayout"),
			Map.entry("pagebottommargin", "pageBottomMargin"),
			Map.entry("pageheight", "pageHeight"),
			Map.entry("pageinsidemargin", "pageInsideMargin"),
			Map.entry("pagelayout", "pageLayout"),
			Map.entry("pagenumber", "pageNumber"),
			Map.entry("pageoutsidemargin", "pageOutsideMargin"),
			Map.entry("pagetopmargin", "pageTopMargin"),
			Map.entry("pagewidth", "pageWidth"),
			Map.entry("paperauthor", "paperAuthor"),
			Map.entry("paperlabelitem", "paperLabelItem"),
			Map.entry("paperlayout", "paperLayout"),
			Map.entry("paperlayouts", "paperLayouts"),
			Map.entry("paperpublishingblurb", "paperPublishingBlurb"),
			Map.entry("papertitle", "paperTitle"),
			Map.entry("paragraphalignment", "paragraphAlignment"),
			Map.entry("paragraphindent", "paragraphIndent"),
			Map.entry("paragraphlayout", "paragraphLayout"),
			Map.entry("partlabelcontentchoices", "partLabelContentChoices"),
			Map.entry("partlayout", "partLayout"),
			Map.entry("parttitlelayout", "partTitleLayout"),
			Map.entry("prefacelayout", "prefaceLayout"),
			Map.entry("presentedat", "presentedAt"),
			Map.entry("presentedatlayout", "presentedAtLayout"),
			Map.entry("proccitation", "procCitation"),
			Map.entry("proccitationitem", "procCitationItem"),
			Map.entry("proced", "procEd"),
			Map.entry("procedinitials", "procEdInitials"),
			Map.entry("proceditem", "procEdItem"),
			Map.entry("procedsurnamegivenname", "procEdSurnameGivenName"),
			Map.entry("procpages", "procPages"),
			Map.entry("procpagesitem", "procPagesItem"),
			Map.entry("proctitle", "procTitle"),
			Map.entry("proctitleitem", "procTitleItem"),
			Map.entry("proctitlelowercase", "procTitleLowerCase"),
			Map.entry("procvol", "procVol"),
			Map.entry("procvolitem", "procVolItem"),
			Map.entry("proceedingslayout", "proceedingsLayout"),
			Map.entry("proceedingslayouts", "proceedingsLayouts"),
			Map.entry("prose-texttextlayout", "prose-textTextLayout"),
			Map.entry("pubdate", "pubDate"),
			Map.entry("pubdateitem", "pubDateItem"),
			Map.entry("publishedcollection", "publishedCollection "), 
			Map.entry("publisheddocument", "publishedDocument"),
			Map.entry("publishedlayout", "publishedLayout"),
			Map.entry("publishedlayoutref", "publishedLayoutRef"),
			Map.entry("publishedpaper", "publishedPaper"),
			Map.entry("publisheritem", "publisherItem"),
			Map.entry("publisherstylesheet", "publisherStyleSheet"),
			Map.entry("publisherstylesheetdate", "publisherStyleSheetDate"),
			Map.entry("publisherstylesheetdateaccessed", "publisherStyleSheetDateAccessed"),
			Map.entry("publisherstylesheetname", "publisherStyleSheetName"),
			Map.entry("publisherstylesheetpublisher", "publisherStyleSheetPublisher "), 
			Map.entry("publisherstylesheetreferencesname", "publisherStyleSheetReferencesName"),
			Map.entry("publisherstylesheetreferencesversion", "publisherStyleSheetReferencesVersion"),
			Map.entry("publisherstylesheeturl", "publisherStyleSheetUrl"),
			Map.entry("publisherstylesheetversion", "publisherStyleSheetVersion"),
			Map.entry("publishingblurb", "publishingBlurb"),
			Map.entry("publishingblurblayout", "publishingBlurbLayout"),
			Map.entry("publishinginfo", "publishingInfo"),
			Map.entry("quotelayout", "quoteLayout"),
			Map.entry("refauthor", "refAuthor"),
			Map.entry("refauthorinitials", "refAuthorInitials"),
			Map.entry("refauthoritem", "refAuthorItem"),
			Map.entry("refauthorlastname", "refAuthorLastName"),
			Map.entry("refauthorlastnamelayout", "refAuthorLastNameLayout"),
			Map.entry("refauthorlayout", "refAuthorLayout"),
			Map.entry("refauthorlayouts", "refAuthorLayouts"),
			Map.entry("refauthorname", "refAuthorName"),
			Map.entry("refauthornamechange", "refAuthorNameChange"),
			Map.entry("refauthorsurnamegivenname", "refAuthorSurnameGivenName"),
			Map.entry("refdate", "refDate"),
			Map.entry("refdateitem", "refDateItem"),
			Map.entry("reftitle", "refTitle"),
			Map.entry("reftitleitem", "refTitleItem"),
			Map.entry("reftitlelowercase", "refTitleLowerCase"),
			Map.entry("refwork", "refWork"),
			Map.entry("referencedinterlineartext", "referencedInterlinearText"),
			Map.entry("referencedinterlineartexts", "referencedInterlinearTexts"),
			Map.entry("referenceslayout", "referencesLayout"),
			Map.entry("referencestitlelayout", "referencesTitleLayout"),
			Map.entry("reprintinfo", "reprintInfo"),
			Map.entry("reprintinfoitem", "reprintInfoItem"),
			Map.entry("rightheaderfooteritem", "rightHeaderFooterItem"),
			Map.entry("sectitle", "secTitle"),
			Map.entry("section1layout", "section1Layout"),
			Map.entry("section2layout", "section2Layout"),
			Map.entry("section3layout", "section3Layout"),
			Map.entry("section4layout", "section4Layout"),
			Map.entry("section5layout", "section5Layout"),
			Map.entry("section6layout", "section6Layout"),
			Map.entry("sectionnumber", "sectionNumber"),
			Map.entry("sectionref", "sectionRef"),
			Map.entry("sectionreflayout", "sectionRefLayout"),
			Map.entry("sectionreflinklayout", "sectionRefLinkLayout"),
			Map.entry("sectionreftitlelayout", "sectionRefTitleLayout"),
			Map.entry("sectiontitle", "sectionTitle"),
			Map.entry("sectiontitlelayout", "sectionTitleLayout"),
			Map.entry("seealsoterm", "seeAlsoTerm"),
			Map.entry("seedefinition", "seeDefinition"),
			Map.entry("seedefinitions", "seeDefinitions"),
			Map.entry("seeterm", "seeTerm"),
			Map.entry("selectedbibliography", "selectedBibliography"),
			Map.entry("seriesed", "seriesEd"),
			Map.entry("seriesedinitials", "seriesEdInitials"),
			Map.entry("serieseditem", "seriesEdItem"),
			Map.entry("seriesedsurnamegivenname", "seriesEdSurnameGivenName"),
			Map.entry("seriesitem", "seriesItem"),
			Map.entry("shortauthor", "shortAuthor"),
			Map.entry("shortcaption", "shortCaption"),
			Map.entry("shortsectiontitlelayout", "shortSectionTitleLayout"),
			Map.entry("shortsubsectionlayout", "shortSubsectionLayout"),
			Map.entry("shorttitle", "shortTitle"),
			Map.entry("styledpaper", "styledPaper"),
			Map.entry("subtitlelayout", "subtitleLayout"),
			Map.entry("tablecaptionlayout", "tableCaptionLayout"),
			Map.entry("tablenumberedcaptionlayout", "tablenumberedCaptionLayout"),
			Map.entry("tablenumberedlabellayout", "tablenumberedLabelLayout"),
			Map.entry("tablenumberedlayout", "tablenumberedLayout"),
			Map.entry("tablenumberednumberlayout", "tablenumberedNumberLayout"),
			Map.entry("tablenumberedref", "tablenumberedRef"),
			Map.entry("tablenumberedrefcaptionlayout", "tablenumberedRefCaptionLayout"),
			Map.entry("tablenumberedreflayout", "tablenumberedRefLayout"),
			Map.entry("tablenumberedreflinklayout", "tablenumberedRefLinkLayout"),
			Map.entry("textinfo", "textInfo"),
			Map.entry("texttitle", "textTitle"),
			Map.entry("thesislabelitem", "thesisLabelItem"),
			Map.entry("thesislayout", "thesisLayout"),
			Map.entry("thesislayouts", "thesisLayouts"),
			Map.entry("titlecontent", "titleContent"),
			Map.entry("titlecontentchoices", "titleContentChoices"),
			Map.entry("titleheaderfooterpagestyles", "titleHeaderFooterPageStyles"),
			Map.entry("titlelayout", "titleLayout"),
			Map.entry("titlepageback", "titlePageBack"),
			Map.entry("titlepageextras", "titlePageExtras"),
			Map.entry("titlepageitem", "titlePageItem"),
			Map.entry("translatedby", "translatedBy"),
			Map.entry("translatedbyitem", "translatedByItem"),
			Map.entry("urldateaccessedlayout", "urlDateAccessedLayout"),
			Map.entry("urldateaccessedlayouts", "urlDateAccessedLayouts"),
			Map.entry("urldateaccessedlayoutsref", "urlDateAccessedLayoutsRef"),
			Map.entry("urlitem", "urlItem"),
			Map.entry("urllinklayout", "urlLinkLayout"),
			Map.entry("useendnoteslayout", "useEndNotesLayout"),
			Map.entry("usethesissubmissionstyle", "useThesisSubmissionStyle"),
			Map.entry("versionlayout", "versionLayout"),
			Map.entry("volumeauthor", "volumeAuthor"),
			Map.entry("volumeauthorref", "volumeAuthorRef"),
			Map.entry("volumelayout", "volumeLayout"),
			Map.entry("volumetitle", "volumeTitle"),
			Map.entry("volumetitleref", "volumeTitleRef"),
			Map.entry("webpage", "webPage"),
			Map.entry("webpagelayout", "webPageLayout"),
			Map.entry("webpagelayouts", "webPageLayouts")
			);
	private static final Map<String, String> attributeNameMap = Map.ofEntries(
			Map.entry("addperiodafterfinaldigit", "AddPeriodAfterFinalDigit"),
			Map.entry("iso639-3code", "ISO639-3Code"),
			Map.entry("orcid", "ORCID"),
			Map.entry("xelatexspecial", "XeLaTeXSpecial"),
			Map.entry("abbrwidth", "abbrWidth"),
			Map.entry("adjustindentofnoninitiallineby", "adjustIndentOfNonInitialLineBy"),
			Map.entry("appendedabbreviation", "appendEdAbbreviation"),
			Map.entry("appendixrefcapitalizedplurallabel", "appendixRefCapitalizedPluralLabel"),
			Map.entry("appendixrefcapitalizedsingularlabel", "appendixRefCapitalizedSingularLabel"),
			Map.entry("appendixrefdefault", "appendixRefDefault"),
			Map.entry("appendixrefplurallabel", "appendixRefPluralLabel"),
			Map.entry("appendixrefsingularlabel", "appendixRefSingularLabel"),
			Map.entry("authornameswordforand", "authorNamesWordForAnd"),
			Map.entry("backmattershowlevel", "backmattershowLevel"),
			Map.entry("bibtexfile", "bibtexFile"),
			Map.entry("bookmarksshowlevel", "bookmarksShowLevel"),
			Map.entry("captionlocation", "captionLocation"),
			Map.entry("codewidth", "codeWidth"),
			Map.entry("contentbetweenfootnotenumberandfootnotecontent", "contentBetweenFootnoteNumberAndFootnoteContent"),
			Map.entry("contentbetweenlabelandnumber", "contentBetweenLabelAndNumber"),
			Map.entry("contentbetweenmultiplefootnotenumbersintext", "contentBetweenMultipleFootnoteNumbersInText"),
			Map.entry("contenttype", "contentType"),
			Map.entry("counternumberformat", "counterNumberFormat"),
			Map.entry("cssspecial", "cssSpecial"),
			Map.entry("dateindentauthoroverdatestyle", "dateIndentAuthorOverDateStyle"),
			Map.entry("datetoentryspaceauthoroverdatestyle", "dateToEntrySpaceAuthorOverDateStyle"),
			Map.entry("defaultglosslanguage", "defaultGlossLanguage"),
			Map.entry("defaultvernacularlanguage", "defaultVernacularLanguage"),
			Map.entry("definitionwidth", "definitionWidth"),
			Map.entry("doublecolumnseparation", "doubleColumnSeparation"),
			Map.entry("edtextplural", "edTextPlural"),
			Map.entry("edtextsingular", "edTextSingular"),
			Map.entry("edtextafter", "edTextafter"),
			Map.entry("edtextbefore", "edTextbefore"),
			Map.entry("equalswidth", "equalsWidth"),
			Map.entry("ethncode", "ethnCode"),
			Map.entry("examplenumbermaxwidthinems", "exampleNumberMaxWidthInEms"),
			Map.entry("excludeshortsubsectionsfromcontents", "excludeShortSubsectionsFromContents"),
			Map.entry("externalid", "externalID"),
			Map.entry("figurelabel", "figureLabel"),
			Map.entry("figurelabelandcaptionlocation", "figureLabelAndCaptionLocation"),
			Map.entry("figurerefcapitalizedplurallabel", "figureRefCapitalizedPluralLabel"),
			Map.entry("figurerefcapitalizedsingularlabel", "figureRefCapitalizedSingularLabel"),
			Map.entry("figurerefdefault", "figureRefDefault"),
			Map.entry("figurerefplurallabel", "figureRefPluralLabel"),
			Map.entry("figurerefsingularlabel", "figureRefSingularLabel"),
			Map.entry("firstparagraphhasindent", "firstParagraphHasIndent"),
			Map.entry("glossaryterm", "glossaryTerm"),
			Map.entry("glossarytermwidth", "glossaryTermWidth"),
			Map.entry("hangingindent", "hangingIndent"),
			Map.entry("hangingindentinitialindent", "hangingIndentInitialIndent"),
			Map.entry("hangingindentnormalindent", "hangingIndentNormalIndent"),
			Map.entry("hyphenationexceptionsfile", "hyphenationExceptionsFile"),
			Map.entry("ignoredateaccessed", "ignoreDateAccessed"),
			Map.entry("ignoredoi", "ignoreDoi"),
			Map.entry("ignorelocations", "ignoreLocations"),
			Map.entry("ignorepagewidthforwebpageoutput", "ignorePageWidthForWebPageOutput"),
			Map.entry("ignoreurl", "ignoreUrl"),
			Map.entry("indentofinitialgroup", "indentOfInitialGroup"),
			Map.entry("indentofnoninitialgroup", "indentOfNonInitialGroup"),
			Map.entry("initialindent", "initialIndent"),
			Map.entry("keywordlabelonsamelineaskeywords", "keywordLabelOnSameLineAsKeywords"),
			Map.entry("labeldissertation", "labelDissertation"),
			Map.entry("labelpaper", "labelPaper"),
			Map.entry("labelthesis", "labelThesis"),
			Map.entry("languagenamewidth", "languageNameWidth"),
			Map.entry("letteronly", "letterOnly"),
			Map.entry("linenumberonly", "lineNumberOnly"),
			Map.entry("linestoinclude", "linesToInclude"),
			Map.entry("linknumbertotext", "linkNumberToText"),
			Map.entry("listitemshavepareninsteadofperiod", "listItemsHaveParenInsteadOfPeriod"),
			Map.entry("listoffigureshangingindent", "listOfFiguresHangingIndent"),
			Map.entry("listoffiguresusesfigureandpageheaders", "listOfFiguresUsesFigureAndPageHeaders"),
			Map.entry("listoffiguresusesfigureheader", "listOfFiguresUsesFigureHeader"),
			Map.entry("listoftableshangingindent", "listOfTablesHangingIndent"),
			Map.entry("listoftablesusestableandpageheaders", "listOfTablesUsesTableAndPageHeaders"),
			Map.entry("listoftablesusestableheader", "listOfTablesUsesTableHeader"),
			Map.entry("literallabel", "literalLabel"),
			Map.entry("numberformat", "numberFormat"),
			Map.entry("numberlevel", "numberLevel"),
			Map.entry("numberproperaddperiodafterfinaldigit", "numberProperAddPeriodAfterFinalDigit"),
			Map.entry("numberproperuseparens", "numberProperUseParens"),
			Map.entry("numeralformat", "numeralFormat"),
			Map.entry("openinnewtab", "openInNewTab"),
			Map.entry("pagelabelinlistoffigures", "pageLabelInListOfFigures"),
			Map.entry("pagelabelinlistoftables", "pageLabelInListOfTables"),
			Map.entry("pagerangeseparator", "pageRangeSeparator"),
			Map.entry("partcentered", "partCentered"),
			Map.entry("partcontentbetweenlabelandnumber", "partContentBetweenLabelAndNumber"),
			Map.entry("partcontentbetweennumberandtitle", "partContentBetweenNumberAndTitle"),
			Map.entry("partshowpagenumber", "partShowPageNumber"),
			Map.entry("partspaceafter", "partSpaceAfter"),
			Map.entry("partspacebefore", "partSpaceBefore"),
			Map.entry("reftobook", "refToBook"),
			Map.entry("referencesuseparens", "referencesUseParens"),
			Map.entry("resetendnotenumbering", "resetEndnoteNumbering"),
			Map.entry("restartcount", "restartCount"),
			Map.entry("rightindent", "rightIndent"),
			Map.entry("sectionrefcapitalizedplurallabel", "sectionRefCapitalizedPluralLabel"),
			Map.entry("sectionrefcapitalizedsingularlabel", "sectionRefCapitalizedSingularLabel"),
			Map.entry("sectionrefdefault", "sectionRefDefault"),
			Map.entry("sectionrefplurallabel", "sectionRefPluralLabel"),
			Map.entry("sectionrefsingularlabel", "sectionRefSingularLabel"),
			Map.entry("showasfootnoteatendofabstract", "showAsFootnoteAtEndOfAbstract"),
			Map.entry("showauthorname", "showAuthorName"),
			Map.entry("showcaption", "showCaption"),
			Map.entry("showchapternumberbeforeexamplenumber", "showChapterNumberBeforeExampleNumber"),
			Map.entry("showexampleidonhoverinwebpage", "showExampleIdOnHoverInWebpage"),
			Map.entry("showinheader", "showInHeader"),
			Map.entry("showlevel", "showLevel"),
			Map.entry("showlinenumbers", "showLineNumbers"),
			Map.entry("shownumber", "showNumber"),
			Map.entry("shownumberonly", "showNumberOnly"),
			Map.entry("showonlychosenitemsineditor", "showOnlyChosenItemsInEditor"),
			Map.entry("showtitle", "showTitle"),
			Map.entry("showtitleonly", "showTitleOnly"),
			Map.entry("showvolumeincontents", "showVolumeInContents"),
			Map.entry("showddonnewlineinpdf", "showddOnNewLineInPDF"),
			Map.entry("showreftitleinboldineditor", "showrefTitleInBoldInEditor"),
			Map.entry("showrefworkiconsineditor", "showrefWorkIconsInEditor"),
			Map.entry("sortby", "sortBy"),
			Map.entry("sortrefsabbrsbydocumentlanguage", "sortRefsAbbrsByDocumentLanguage"),
			Map.entry("spaceafter", "spaceAfter"),
			Map.entry("spacebefore", "spaceBefore"),
			Map.entry("spacebetweenentriesauthoroverdatestyle", "spaceBetweenEntriesAuthorOverDateStyle"),
			Map.entry("spacebetweenentryandauthorinauthoroverdatestyle", "spaceBetweenEntryAndAuthorInAuthorOverDateStyle"),
			Map.entry("spacebetweenfigureandcaption", "spaceBetweenFigureAndCaption"),
			Map.entry("spacebetweengroups", "spaceBetweenGroups"),
			Map.entry("spacebetweenparagraphs", "spaceBetweenParagraphs"),
			Map.entry("spacebetweentableandcaption", "spaceBetweenTableAndCaption"),
			Map.entry("spacebetweenunits", "spaceBetweenUnits"),
			Map.entry("startnumberingoverateachchapter", "startNumberingOverAtEachChapter"),
			Map.entry("startsection1numberingatzero", "startSection1NumberingAtZero"),
			Map.entry("startingpagenumber", "startingPageNumber"),
			Map.entry("startingpagenumberinbook", "startingPageNumberInBook"),
			Map.entry("subsectionsareshort", "subsectionsAreShort"),
			Map.entry("symboloverride", "symbolOverride"),
			Map.entry("tablenumberedlabel", "tablenumberedLabel"),
			Map.entry("tablenumberedlabelandcaptionlocation", "tablenumberedLabelAndCaptionLocation"),
			Map.entry("tablenumberedrefcapitalizedplurallabel", "tablenumberedRefCapitalizedPluralLabel"),
			Map.entry("tablenumberedrefcapitalizedsingularlabel", "tablenumberedRefCapitalizedSingularLabel"),
			Map.entry("tablenumberedrefdefault", "tablenumberedRefDefault"),
			Map.entry("tablenumberedrefplurallabel", "tablenumberedRefPluralLabel"),
			Map.entry("tablenumberedrefsingularlabel", "tablenumberedRefSingularLabel"),
			Map.entry("textbefore", "textBefore"),
			Map.entry("textbeforecapitalizedpluraloverride", "textBeforeCapitalizedPluralOverride"),
			Map.entry("textbeforecapitalizedsingularoverride", "textBeforeCapitalizedSingularOverride"),
			Map.entry("textbeforepluraloverride", "textBeforePluralOverride"),
			Map.entry("textbeforeseealso", "textBeforeSeeAlso"),
			Map.entry("textbeforesingularoverride", "textBeforeSingularOverride"),
			Map.entry("textbetweenabbreviationanddefinition", "textBetweenAbbreviationAndDefinition"),
			Map.entry("textbetweenchapternumberandexamplenumber", "textBetweenChapterNumberAndExampleNumber"),
			Map.entry("textbetweenkeywords", "textBetweenKeywords"),
			Map.entry("textafterreferencenumber", "textafterReferenceNumber"),
			Map.entry("textbeforereferencenumber", "textbeforeReferenceNumber"),
			Map.entry("useauthoroverdatestyle", "useAuthorOverDateStyle"),
			Map.entry("useauthorsurnamecommagivennameincitations", "useAuthorSurnameCommaGivenNameInCitations"),
			Map.entry("useauthors", "useAuthors"),
			Map.entry("usedigitsforendnotenumbering", "useDigitsForEndnoteNumbering"),
			Map.entry("usedoublecolumns", "useDoubleColumns"),
			Map.entry("useequalsignscolumn", "useEqualSignsColumn"),
			Map.entry("usefootnotesymbols", "useFootnoteSymbols"),
			Map.entry("useimagewidthsettowidthofexamplefigureorchart", "useImageWidthSetToWidthOfExampleFigureOrChart"),
			Map.entry("uselabel", "useLabel"),
			Map.entry("usepageheader", "usePageHeader"),
			Map.entry("usepageheaderlabel", "usePageHeaderLabel"),
			Map.entry("useraggedright", "useRaggedRight"),
			Map.entry("usesinglespacing", "useSingleSpacing"),
			Map.entry("usesinglespacingforlongcaptions", "useSingleSpacingForLongCaptions"),
			Map.entry("verticaladjustment", "verticalAdjustment"),
			Map.entry("volumeauthorref", "volumeAuthorRef"),
			Map.entry("whichvolumetoshowincontents", "whichVolumeToShowInContents"),
			Map.entry("xsl-fospecial", "xsl-foSpecial")
			);
}
