package com.example.assignment.controller;

import com.example.assignment.data.LanguageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class SettingsController {

    @FXML
    private ChoiceBox<String> languageChoiceBox;

    @FXML
    private ImageView flagImage;

    @FXML
    public void initialize() {
        languageChoiceBox.getItems().addAll("English", "Vietnamese");
        String currentLanguage = LanguageManager.getCurrentLanguage().equals("en") ? "English" : "Vietnamese";
        languageChoiceBox.setValue(currentLanguage);
        updateFlag(currentLanguage);

        languageChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateFlag(newValue);
        });

        LanguageManager.addLanguageChangeListener(() -> {
            String updatedLanguage = LanguageManager.get("layout.dashboard").equals("Dashboard") ? "English" : "Vietnamese";
            languageChoiceBox.setValue(updatedLanguage);
            updateFlag(updatedLanguage);
        });
    }

    @FXML
    public void applyLanguageChange() {
        String selectedLanguage = languageChoiceBox.getValue();
        String languageCode;
        if (selectedLanguage.equals("Vietnamese")) {
            languageCode = "vi";
        } else {
            languageCode = "en";
        }

        if (!LanguageManager.getCurrentLanguage().equals(languageCode)) {
            LanguageManager.loadLanguage(languageCode);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Language has been updated to " + selectedLanguage + ".");
            alert.showAndWait();
        }
    }


    private void updateFlag(String language) {
        String flagPath = switch (language) {
            case "Vietnamese" ->
                    Objects.requireNonNull(getClass().getResource("/com/example/assignment/assets/vietnam_flag.png")).toExternalForm();
            case "English" ->
                    Objects.requireNonNull(getClass().getResource("/com/example/assignment/assets/english_flag.png")).toExternalForm();
            default ->
                    Objects.requireNonNull(getClass().getResource("/com/example/assignment/assets/vietnam_flag.png")).toExternalForm();
        };
        flagImage.setImage(new Image(flagPath));
    }
}
