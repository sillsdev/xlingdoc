// Copyright (c) 2026 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
module org.sil.xlingdoc {
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.web; // If using WebView
	requires java.xml; // For DOM and Parsers
//	requires jdk.xml.dom; // For extended DOM features if needed
	requires transitive javafx.graphics;
	requires jdk.jsobject;
	requires xercesImplNo.org.w3c.dom.html;
	requires junit;
	requires org.sil.utility;

	opens org.sil.xlingdoc to javafx.graphics, javafx.fxml;
	opens org.sil.xlingdoc.view to javafx.graphics, javafx.fxml;

	exports org.sil.xlingdoc;
	exports org.sil.xlingdoc.model;
	exports org.sil.xlingdoc.view;
}
