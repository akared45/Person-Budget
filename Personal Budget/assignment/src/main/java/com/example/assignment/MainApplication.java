package com.example.assignment;
import com.example.assignment.data.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        LanguageManager.setOnLanguageChangeListener(() -> {
            System.out.println("Default language change listener triggered.");
        });
        LanguageManager.loadLanguage("en");
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("view/MainLayout.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        Image logo = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("/com/example/assignment/assets/logo.png")));
        stage.getIcons().add(logo);
        stage.setTitle("Personal Budget");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
