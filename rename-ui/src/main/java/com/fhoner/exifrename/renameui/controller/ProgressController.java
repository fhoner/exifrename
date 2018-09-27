package com.fhoner.exifrename.renameui.controller;

import com.fhoner.exifrename.core.model.FileServiceUpdate;
import com.fhoner.exifrename.core.service.FileService;
import com.fhoner.exifrename.core.util.FilenamePattern;
import com.fhoner.exifrename.renameui.util.DialogUtil;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

@Log4j
public class ProgressController implements Initializable, Observer {

    private Thread thread;
    private SimpleBooleanProperty isRunningProp = new SimpleBooleanProperty();

    @FXML
    public ResourceBundle bundle;

    @FXML
    private Label lblProgress;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnAbort;

    @FXML
    private ProgressBar pgrBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;

        BooleanBinding bb = new BooleanBinding() {
            {
                super.bind(isRunningProp);
            }

            @Override
            protected boolean computeValue() {
                return isRunningProp.getValue();
            }
        };

        btnClose.disableProperty().bind(bb);
    }

    @FXML
    private void abort(ActionEvent event) {

    }

    @FXML
    private void close(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void start() {
        thread.start();
    }

    public void init(String schema, String sourceDir, String destDir) {
        ProgressController ref = this;
        if (thread == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        isRunningProp.set(true);
                        FileService fs = new FileService();
                        fs.addObserver(ref);
                        fs.addFiles(sourceDir);
                        FilenamePattern pattern = FilenamePattern.fromString(schema);
                        fs.formatFiles(pattern, destDir);
                        Platform.runLater(() -> DialogUtil.showInfoDialog(
                                bundle.getString("done"),
                                bundle.getString("success"),
                                MessageFormat.format(bundle.getString("imagesCopiedDialog"), fs.getFiles().size()),
                                null));
                    } catch (Exception ex) {
                        log.error("could not finish", ex);
                        Platform.runLater(() -> DialogUtil.showErrorDialog(
                                bundle.getString("error"),
                                bundle.getString("error"),
                                bundle.getString("couldNotFinish"), null));
                    } finally {
                        isRunningProp.set(false);
                    }
                }
            });
        } else {
            throw new RuntimeException("thread already running");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        FileServiceUpdate update = (FileServiceUpdate) arg;
        Platform.runLater(() -> {
            lblProgress.setText(update.getFilesDone() + "/" + update.getFilesCount());
            pgrBar.setProgress(update.getFilesDone() / (double) update.getFilesCount());
        });
        log.debug("progress changed: " + update.getFilesDone() + "/" + update.getFilesCount());
    }

}
