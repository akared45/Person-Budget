module com.example.assignment {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires json.simple;

    opens com.example.assignment to javafx.fxml;
    exports com.example.assignment;
    exports com.example.assignment.controller;
    opens com.example.assignment.controller to javafx.fxml;
    opens com.example.assignment.model to javafx.base, javafx.fxml;
    opens com.example.assignment.data to javafx.base, javafx.fxml;
}