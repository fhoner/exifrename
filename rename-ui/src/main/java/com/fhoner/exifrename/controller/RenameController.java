package com.fhoner.exifrename.controller;

import com.fhoner.exifrename.model.FileServiceUpdate;
import com.fhoner.exifrename.service.FileService;
import com.fhoner.exifrename.util.FilenamePattern;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j
public class RenameController implements Initializable, Observer {

    private static final String REGEX = ".* \\((\\%.)\\)";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    @FXML
    private TextField txtSource;

    @FXML
    private TextField txtDestination;

    @FXML
    private TextField txtPattern;

    @FXML
    private Button btnMakeFiles;

    @FXML
    private Label lblProgress;

    private SimpleBooleanProperty isRunningProp = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addButtonDisabledBinding();
    }

    @FXML
    void btnBrowseSource(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select source directory");
        File defaultDirectory = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(defaultDirectory);
        Stage stage = new Stage();
        File selectedDirectory = chooser.showDialog(stage);
        txtSource.setText(selectedDirectory.getAbsolutePath());
    }

    @FXML
    void btnBrowseDestination(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select destination directory");
        File defaultDirectory = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(defaultDirectory);
        Stage stage = new Stage();
        File selectedDirectory = chooser.showDialog(stage);
        txtDestination.setText(selectedDirectory.getAbsolutePath());
    }

    @FXML
    private void insertVariable(ActionEvent event) {
        Button sourceBtn = (Button) event.getSource();
        Matcher matcher = PATTERN.matcher(sourceBtn.getText());
        while (matcher.find()) {
            String var = matcher.group(1);
            log.debug("inserting variable " + var);
            txtPattern.appendText(var);
            txtPattern.requestFocus();
            txtPattern.positionCaret(txtPattern.getText().length());
        }
    }

    @FXML
    private void makeFiles(ActionEvent event) {
        RenameController ref = this;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    isRunningProp.set(true);
                    FileService fs = new FileService();
                    fs.addObserver(ref);
                    fs.addFiles(txtSource.getText());
                    FilenamePattern pattern = FilenamePattern.fromString(txtPattern.getText());
                    fs.createFiles(pattern, txtDestination.getText());
                } catch (Exception ex) {
                    log.error("could not finish", ex);
                } finally {
                    isRunningProp.set(false);
                }
            }
        });
        t.start();
    }

    private void addButtonDisabledBinding() {
        BooleanBinding bb = new BooleanBinding() {
            {
                super.bind(txtSource.textProperty(),
                        txtDestination.textProperty(),
                        txtPattern.textProperty(),
                        isRunningProp);
            }

            @Override
            protected boolean computeValue() {
                return (txtSource.getText().isEmpty()
                        || txtDestination.getText().isEmpty()
                        || txtPattern.getText().isEmpty()
                        || isRunningProp.getValue());
            }
        };

        btnMakeFiles.disableProperty().bind(bb);
    }

    @Override
    public void update(Observable o, Object arg) {
        FileServiceUpdate update = (FileServiceUpdate) arg;
        Platform.runLater(() -> lblProgress.setText("Progress: " + update.getFilesDone() + "/" + update.getFilesCount()));
        log.debug("fileservice update: " + update.getFilesDone() + "/" + update.getFilesCount());
    }

}
