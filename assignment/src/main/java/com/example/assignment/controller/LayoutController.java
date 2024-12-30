package com.example.assignment.controller;
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
    private Button homeButton, reportButton, manageButton, settingsButton;

    private List<Button> menuButtons;

    @FXML
    public void initialize() {
        menuButtons = List.of(homeButton, reportButton, manageButton, settingsButton);
        setCenterContent("/com/example/assignment/view/Home.fxml", homeButton);
    }

    @FXML
    private void handleHomeButton() {
        setCenterContent("/com/example/assignment/view/Home.fxml", homeButton);
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
        setCenterContent("/com/example/assignment/view/Settings.fxml", settingsButton);
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
}
