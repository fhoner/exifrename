package com.fhoner.exifrename.renameui.controller;

import com.fhoner.exifrename.renameui.RenameUIVersion;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class RenameController implements Initializable {

    private static final String VARIABLES_REGEX = ".* \\((\\%.)\\)";
    private static final Pattern VARIABLES_PATTERN = Pattern.compile(VARIABLES_REGEX);

    private static final String PREF_SOURCE = "source";
    private static final String PREF_DESTINATION = "destination";
    private static final String PREF_PATTERN = "pattern";
    private static final String DEFAULT_PATTERN = "%y-%m-%d-%h-%M-%s {0} %r-%t";

    @FXML
    private TextField txtSource;

    @FXML
    private TextField txtDestination;

    @FXML
    private TextField txtPattern;

    @FXML
    private Button btnMakeFiles;

    @FXML
    private Label lblVersion;

    private File lastSelectedFolder = new File(System.getProperty("user.home"));
    private Preferences userPrefs = Preferences.userNodeForPackage(RenameController.class);

    @FXML
    public ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
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
            txtDestination.setText(lastSelectedFolder.getAbsolutePath() + "/" + bundle.getString("new"));
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

        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProgressView.fxml"), bundle);
            root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnMakeFiles.getScene().getWindow());
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("My New Stage Title");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            ProgressController controller = (ProgressController) loader.getController();
            controller.init(txtPattern.getText(), txtSource.getText(), txtDestination.getText());
            controller.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addButtonDisabledBinding() {
        BooleanBinding bb = new BooleanBinding() {
            {
                super.bind(txtSource.textProperty(),
                        txtDestination.textProperty(),
                        txtPattern.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return (txtSource.getText().isEmpty()
                        || txtDestination.getText().isEmpty()
                        || txtPattern.getText().isEmpty());
            }
        };

        btnMakeFiles.disableProperty().bind(bb);
    }

    private void showVersion() {
        String rev = "rev" + RenameUIVersion.GIT_REVISION;
        if (RenameUIVersion.DIRTY == 1) {
            rev += "-dirty";
        }
        lblVersion.setText(rev);
    }

    private void loadPreferences() {
        txtSource.setText(userPrefs.get(PREF_SOURCE, ""));
        txtDestination.setText(userPrefs.get(PREF_DESTINATION, ""));
        txtPattern.setText(userPrefs.get(PREF_PATTERN, MessageFormat.format(DEFAULT_PATTERN, bundle.getString("myTour"))));
    }

}
