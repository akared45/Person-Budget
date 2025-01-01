package com.example.assignment.controller;

import com.example.assignment.data.LanguageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class LayoutController {

    @FXML
    private BorderPane mainLayout;

    @FXML
    private Button dashBoardButton, reportButton, manageButton, settingsButton;

    private List<Button> menuButtons;

    @FXML
    public void initialize() {
        menuButtons = List.of(dashBoardButton, reportButton, manageButton, settingsButton);
        setCenterContent("/com/example/assignment/view/DashBoard.fxml", dashBoardButton);
        updateUI();
        LanguageManager.addLanguageChangeListener(this::updateUI);
    }

    @FXML
    private void handleHomeButton() {
        setCenterContent("/com/example/assignment/view/DashBoard.fxml", dashBoardButton);
    }

    @FXML
    private void handleReportButton() {
        setCenterContent("/com/example/assignment/view/Report.fxml", reportButton);
    }

    @FXML
    private void handleManageButton() {
        setCenterContent("/com/example/assignment/view/Manage.fxml", manageButton);
    }

    @FXML
    private void handleSettingsButton() {
        setCenterContent("/com/example/assignment/view/Setting.fxml", settingsButton);
    }

    private void setCenterContent(String fxmlFile, Button activeButton) {
        try {
            Node content = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            mainLayout.setCenter(content);
            updateButtonStyles(activeButton);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateButtonStyles(Button activeButton) {
        for (Button button : menuButtons) {
            button.getStyleClass().remove("active-button");
        }
        activeButton.getStyleClass().add("active-button");
    }

    private void updateUI() {
        dashBoardButton.setText(LanguageManager.get("layout.dashboard"));
        reportButton.setText(LanguageManager.get("layout.report"));
        manageButton.setText(LanguageManager.get("layout.manage"));
        settingsButton.setText(LanguageManager.get("layout.settings"));
    }


}
