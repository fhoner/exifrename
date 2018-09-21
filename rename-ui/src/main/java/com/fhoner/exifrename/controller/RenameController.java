package com.fhoner.exifrename.controller;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.extern.log4j.Log4j;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j
public class RenameController implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addButtonDisabledBinding();
    }

    @FXML
    private void insertVariable(ActionEvent event) {
        Button sourceBtn = (Button) event.getSource();
        Matcher matcher = PATTERN.matcher(sourceBtn.getText());
        while (matcher.find()) {
            String var = matcher.group(1);
            log.debug("inserting variable " + var);
            txtPattern.appendText(var);
        }
    }

    @FXML
    void makeFiles(ActionEvent event) {

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

}
