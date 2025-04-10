package controllers;

import bll.LoginCheck;
import bll.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    @FXML public VBox loginContainer;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button togglePasswordVisibility;
    @FXML private Button loginButton;
    @FXML private ImageView backgroundView;

    private final LoginCheck loginCheck = new LoginCheck();
    private boolean isPasswordVisible = false;

    @FXML
    private void initialize() {
        // Background image and blur
        Image image = new Image(getClass().getResource("/images/easvticket.jpg").toExternalForm());
        backgroundView.setImage(image);
        backgroundView.setEffect(new GaussianBlur(10));

        // Initial visibility: show password field (dots), hide text field
        isPasswordVisible = false;
        passwordField.setVisible(true);
        visiblePasswordField.setVisible(false);

        passwordField.managedProperty().bind(passwordField.visibleProperty());
        visiblePasswordField.managedProperty().bind(visiblePasswordField.visibleProperty());

        // Sync text
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        // Toggle visibility
        togglePasswordVisibility.setOnAction(e -> {
            isPasswordVisible = !isPasswordVisible;
            visiblePasswordField.setVisible(isPasswordVisible);
            passwordField.setVisible(!isPasswordVisible);
            togglePasswordVisibility.setText(isPasswordVisible ? "🙈" : "👁");
        });

        // Login button
        loginButton.setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = isPasswordVisible
                ? visiblePasswordField.getText()
                : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Please fill in both fields.");
            return;
        }

        String result = loginCheck.checkLogin(username, password);

        switch (result) {
            case "Admin" -> {
                UserSession.setRole("Admin");
                openAdminDashboard();
            }
            case "Event Coordinator" -> {
                UserSession.setRole("Event Coordinator");
                openEventCoordinatorDashboard();
            }
            case "Wrong username", "Wrong password", "Unknown" -> showAlert(result);
        }
    }

    private void openAdminDashboard() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            new AdminDashboard().start(stage);
            closeCurrentStage();
        });
    }

    private void openEventCoordinatorDashboard() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            new EventCoordinatorDashboard().start(stage);
            closeCurrentStage();
        });
    }

    private void closeCurrentStage() {
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
