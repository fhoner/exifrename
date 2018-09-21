package com.fhoner.exifrename.renameui;

import com.fhoner.exifrename.renameui.controller.RenameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewApp extends Application {

    private static Parent mainPanel;
    private static Scene mainScene = null;
    private static RenameController mainSceneController = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane pane = new BorderPane();
        Scene scene = initMainScene();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static Scene initMainScene() throws IOException {
        FXMLLoader.setDefaultClassLoader(ViewApp.class.getClassLoader());
        FXMLLoader fxmlLoader = new FXMLLoader(ViewApp.class.getResource("/fxml/RenameView.fxml"));
        mainPanel = (Parent) fxmlLoader.load();
        mainSceneController = fxmlLoader.<RenameController>getController();
        mainScene = new Scene(mainPanel);
        return mainScene;
    }

}
