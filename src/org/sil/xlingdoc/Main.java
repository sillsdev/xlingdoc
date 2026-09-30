package org.sil.xlingdoc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.sil.utility.ApplicationPreferencesUtilities;
import org.sil.utility.MainAppUtilities;
import org.sil.utility.view.ControllerUtilities;
import org.sil.xlingdoc.view.MainController;

public class Main extends Application implements MainAppUtilities {
	private static final String kApplicationIconResource = "file:resources/images/icon_xlingpaper_128x128.png";

	BorderPane rootLayout;
	Stage primaryStage;
	MainController controller;
	Locale locale;

	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			initRootLayout();
		} catch (Exception e) {
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
			primaryStage.setTitle(bundle.getString("program.name"));
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(getNewMainIconImage());
			controller = loader.getController();
			controller.setMain(this);
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public ApplicationPreferencesUtilities getApplicationPreferences() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getNewMainIconImage() {
		Image img = ControllerUtilities.getIconImageFromURL(kApplicationIconResource,
				Constants.RESOURCE_SOURCE_LOCATION, Main.class);
		return img;
	}

	@Override
	public Stage getPrimaryStage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveFile(File arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateStageTitle(File arg0) {
		// TODO Auto-generated method stub

	}

	public static void reportException(Exception ex, ResourceBundle bundle) {
		String sTitle = "Error Found!";
		String sHeader = "A serious error happened.";
		String sContent = "Please copy the exception information below, email it to xlingpaper_support_lsdev@sil.org along with a description of what you were doing.";
		String sLabel = "The exception stacktrace was:";
		if (bundle != null) {
			sTitle = bundle.getString("exception.title");
			sHeader = bundle.getString("exception.header");
			sContent = bundle.getString("exception.content");
			sLabel = bundle.getString("exception.label");
		}
		ControllerUtilities.showExceptionInErrorDialog(ex, sTitle, sHeader, sContent, sLabel);
		System.exit(1);
	}
}
