package com.fhoner.exifrename.renameui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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
        mainPanel = fxmlLoader.load();
        mainScene = new Scene(mainPanel);
        return mainScene;
    }

}
