package com.fhoner.exifrename.renameui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.util.Optional;

public class DialogUtil {

    public static void showInfoDialog(String title, String header, String content, Window owner) {
        showSimpleDialog(AlertType.INFORMATION, title, header, content, owner);
    }

    public static void showWarningDialog(String title, String header, String content, Window owner) {
        showSimpleDialog(AlertType.WARNING, title, header, content, owner);
    }

    public static void showErrorDialog(String title, String header, String content, Window owner) {
        showSimpleDialog(AlertType.ERROR, title, header, content, owner);
    }

    public static Optional<ButtonType> showSimpleDialog(AlertType type, String title, String header, String content, Window owner) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(owner);
        return alert.showAndWait();
    }

}
