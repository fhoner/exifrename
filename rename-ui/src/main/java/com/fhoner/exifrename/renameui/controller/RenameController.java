package com.fhoner.exifrename.renameui.controller;

import com.fhoner.exifrename.core.model.FileServiceUpdate;
import com.fhoner.exifrename.core.service.FileService;
import com.fhoner.exifrename.core.util.FilenamePattern;
import com.fhoner.exifrename.renameui.util.DialogUtil;
import com.fhoner.exifrename.renameui.util.GitRevisionUtil;
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
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j
public class RenameController implements Initializable, Observer {

    private static final String VARIABLES_REGEX = ".* \\((\\%.)\\)";
    private static final Pattern VARIABLES_PATTERN = Pattern.compile(VARIABLES_REGEX);

    private static final String PREF_SOURCE = "source";
    private static final String PREF_DESTINATION = "destination";
    private static final String PREF_PATTERN = "pattern";

    private static final String DEFAULT_PATTERN = "%y-%m-%d-%h-%M-%s My Tour %r-%t";

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

    @FXML
    private Label lblVersion;

    private SimpleBooleanProperty isRunningProp = new SimpleBooleanProperty();
    private File lastSelectedFolder = new File(System.getProperty("user.home"));
    private Preferences userPrefs = Preferences.userNodeForPackage(RenameController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showVersion();
        addButtonDisabledBinding();
        loadPreferences();
    }

    @FXML
    void btnBrowseSource(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select source directory");
        File defaultDirectory = lastSelectedFolder;
        chooser.setInitialDirectory(defaultDirectory);
        Stage stage = new Stage();
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            lastSelectedFolder = selectedDirectory;
            txtSource.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    void btnBrowseDestination(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select destination directory");
        File defaultDirectory = lastSelectedFolder;
        chooser.setInitialDirectory(defaultDirectory);
        Stage stage = new Stage();
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            lastSelectedFolder = selectedDirectory;
            txtDestination.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void insertVariable(ActionEvent event) {
        Button sourceBtn = (Button) event.getSource();
        Matcher matcher = VARIABLES_PATTERN.matcher(sourceBtn.getText());
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
        userPrefs.put(PREF_SOURCE, txtSource.getText());
        userPrefs.put(PREF_DESTINATION, txtDestination.getText());
        userPrefs.put(PREF_PATTERN, txtPattern.getText());
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
                    Platform.runLater(() -> DialogUtil.showInfoDialog(
                            "Done",
                            "Success",
                            fs.getFiles().size() + " images have been copied to destination folder.",
                            null));
                } catch (Exception ex) {
                    log.error("could not finish", ex);
                    Platform.runLater(() -> DialogUtil.showErrorDialog("Error", "Error", "An error occurred:" + ex.getMessage(), null));
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

    private void showVersion() {
        try {
            GitRevisionUtil git = new GitRevisionUtil();
            String hash = "#" + git.get(GitRevisionUtil.Key.HASH);
            lblVersion.setText(hash);
        } catch (IOException ex) {
            lblVersion.setText("version unavailable");
        }
    }

    private void loadPreferences() {
        txtSource.setText(userPrefs.get(PREF_SOURCE, ""));
        txtDestination.setText(userPrefs.get(PREF_DESTINATION, ""));
        txtPattern.setText(userPrefs.get(PREF_PATTERN, DEFAULT_PATTERN));
    }

    @Override
    public void update(Observable o, Object arg) {
        FileServiceUpdate update = (FileServiceUpdate) arg;
        Platform.runLater(() -> lblProgress.setText("Progress: " + update.getFilesDone() + "/" + update.getFilesCount()));
        log.debug("fileservice update: " + update.getFilesDone() + "/" + update.getFilesCount());
    }

}
