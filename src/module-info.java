module xmleditorplay {
	requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.web;    // If using WebView
    requires java.xml;      // For DOM and Parsers
    requires jdk.xml.dom;   // For extended DOM features if needed
	requires transitive javafx.graphics;
	
	opens application to javafx.graphics, javafx.fxml;
	opens application.view to javafx.graphics, javafx.fxml;
	
	exports application;
	exports application.view;
}
