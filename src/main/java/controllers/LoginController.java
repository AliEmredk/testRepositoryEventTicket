package controllers;

import bll.LoginCheck;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private CheckBox showPasswordCheckBox;
    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameField;

    private final LoginCheck loginCheck = new LoginCheck();

    @FXML
    public void initialize() {
        // Sync the visible password field with the actual password field
        visiblePasswordField.managedProperty().bind(showPasswordCheckBox.selectedProperty());
        visiblePasswordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty());
        passwordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty().not());

        // Mirror text between PasswordField and TextField
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        // Login button action (basic example)
        loginButton.setOnAction(event -> handleLogin());

    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        String role = loginCheck.checkLogin(username, password);

        if (role.equals("Admin")) {
            switchScene("AdminMain.fxml", "Admin Panel");
        } else if (role.equals("Event Coordinator")) {
            switchScene("EventCMain.fxml", "Event Coordinator Panel");
        } else {
            showAlert("Login Failed", role);
        }
    }

    private void switchScene(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error","Could not load the next scene.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
