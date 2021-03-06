package com.fhoner.exifrename.renameui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class ViewApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = initMainScene();
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private static Scene initMainScene() throws IOException {
        FXMLLoader.setDefaultClassLoader(ViewApp.class.getClassLoader());
        FXMLLoader fxmlLoader = new FXMLLoader(ViewApp.class.getResource("/fxml/RenameView.fxml"));
        fxmlLoader.setResources(getLanguage());
        Parent mainPanel = fxmlLoader.load();
        return new Scene(mainPanel);
    }

    private static ResourceBundle getLanguage() {
        if (Locale.getDefault() == Locale.GERMAN)
            return ResourceBundle.getBundle("lang.language_de");
        return ResourceBundle.getBundle("lang.language_en");
    }

}
