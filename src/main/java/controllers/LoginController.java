package controllers;

import bll.LoginCheck;
import bll.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import controllers.EventCoordinatorDashboard;
import controllers.AdminDashboard;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private Button loginButton;
    @FXML private ImageView backgroundView;

    private final LoginCheck loginCheck = new LoginCheck();

    @FXML
    private void initialize() {
        // Load background image
        Image image = new Image(getClass().getResource("/images/easvticket.jpg").toExternalForm());
        backgroundView.setImage(image);
        backgroundView.setEffect(new GaussianBlur(20)); //

        // Toggle password visibility
        visiblePasswordField.managedProperty().bind(showPasswordCheckBox.selectedProperty());
        visiblePasswordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty());

        passwordField.managedProperty().bind(showPasswordCheckBox.selectedProperty().not());
        passwordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty().not());

        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        // Handle login
        loginButton.setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = showPasswordCheckBox.isSelected() ? visiblePasswordField.getText() : passwordField.getText();

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
            case "User not found", "Wrong password", "Unknown" -> showAlert(result);
        }
    }

    private void openAdminDashboard() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            new controllers.AdminDashboard().start(stage);
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
