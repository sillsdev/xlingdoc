module xmleditorplay {
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.web; // If using WebView
	requires java.xml; // For DOM and Parsers
//	requires jdk.xml.dom; // For extended DOM features if needed
	requires transitive javafx.graphics;
	requires jdk.jsobject;
	requires xercesImplNo.org.w3c.dom.html;
	requires junit;

	opens org.sil.xlingdoc to javafx.graphics, javafx.fxml;
	opens org.sil.xlingdoc.view to javafx.graphics, javafx.fxml;

	exports org.sil.xlingdoc;
	exports org.sil.xlingdoc.model;
	exports org.sil.xlingdoc.view;
}
