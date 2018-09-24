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

    private static Parent mainPanel;
    private static Scene mainScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = initMainScene();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static Scene initMainScene() throws IOException {
        FXMLLoader.setDefaultClassLoader(ViewApp.class.getClassLoader());
        FXMLLoader fxmlLoader = new FXMLLoader(ViewApp.class.getResource("/fxml/RenameView.fxml"));
        fxmlLoader.setResources(getLanguage());
        mainPanel = fxmlLoader.load();
        mainScene = new Scene(mainPanel);
        return mainScene;
    }

    private static ResourceBundle getLanguage() {
        return ResourceBundle.getBundle("lang.language_de", Locale.ENGLISH);
    }

}
