package org.sil.xlingdoc;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.sil.xlingdoc.view.MainController;

public class Main extends Application {
	BorderPane rootLayout;
	Stage primaryStage;
	MainController controller;
	Locale locale;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
            primaryStage.setTitle("Edit XML as web testing");
            initRootLayout();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	void initRootLayout() {
//		locale = Locale.of(applicationPreferences.getLastLocaleLanguage());
		locale = Locale.of("en");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/fxml/Main.fxml"));
		ResourceBundle bundle = ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale);
		loader.setResources(bundle);
		try {
			rootLayout = (BorderPane) loader.load();
			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().add(getClass().getResource("view/fxml/application.css").toExternalForm());
			primaryStage.setScene(scene);
			controller = loader.getController();
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
}
