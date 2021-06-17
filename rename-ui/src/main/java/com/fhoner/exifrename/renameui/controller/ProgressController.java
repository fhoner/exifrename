package com.fhoner.exifrename.renameui.controller;

import com.fhoner.exifrename.core.model.FileServiceUpdate;
import com.fhoner.exifrename.core.service.GuiceModule;
import com.fhoner.exifrename.core.service.FileService;
import com.fhoner.exifrename.core.service.FileServiceImpl;
import com.fhoner.exifrename.renameui.util.DialogUtil;
import com.google.inject.Guice;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

@Log4j2
public class ProgressController implements Initializable, Observer {

    private Thread thread;
    private FileServiceImpl fileService;
    private final SimpleBooleanProperty isRunningProp = new SimpleBooleanProperty();

    @FXML
    public ResourceBundle bundle;

    @FXML
    private Label lblProgress;

    @FXML
    private Button btnAbort;

    @FXML
    private ProgressBar pgrBar;

    @FXML
    private Label lblAborting;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
    }

    @FXML
    private void abort(ActionEvent event) {
        fileService.cancel();
        btnAbort.setDisable(true);
        lblAborting.setVisible(true);
    }

    public void start() {
        thread.start();
    }

    public void init(String schema, String sourceDir, String destDir) {
        ProgressController ref = this;
        if (thread == null) {
            thread = new Thread(() -> {
                try {
                    var injector = Guice.createInjector(new GuiceModule());
                    isRunningProp.set(true);
                    fileService = (FileServiceImpl) injector.getInstance(FileService.class);
                    fileService.addObserver(ref);
                    fileService.addFiles(sourceDir);
                    fileService.formatFiles(schema, destDir);
                } catch (Exception ex) {
                    log.error("could not finish", ex);
                    Platform.runLater(() -> DialogUtil.showErrorDialog(
                            bundle.getString("error"),
                            bundle.getString("error"),
                            bundle.getString("couldNotFinish"), null));
                } finally {
                    isRunningProp.set(false);
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
            switch (update.getReason()) {
                case PROGRESS:
                    lblProgress.setText(update.getFilesDone() + "/" + update.getFilesCount());
                    pgrBar.setProgress(update.getFilesDone() / (double) update.getFilesCount());
                    if (update.isDone()) {
                        DialogUtil.showInfoDialog(
                                bundle.getString("done"),
                                bundle.getString("success"),
                                MessageFormat.format(bundle.getString("imagesCopiedDialog"), fileService.getFiles().size()),
                                null);
                        ((Stage) btnAbort.getScene().getWindow()).close();
                    }
                    break;
                case ABORT:
                    String header = bundle.getString("abortHeader");
                    String msg = MessageFormat.format(bundle.getString("abortMessage"), update.getFilesDone(), update.getFilesCount());
                    DialogUtil.showWarningDialog(header, header, msg, null);
                    ((Stage) btnAbort.getScene().getWindow()).close();
                    break;
            }
        });
        log.debug("progress changed: " + update.getFilesDone() + "/" + update.getFilesCount());
    }

}
