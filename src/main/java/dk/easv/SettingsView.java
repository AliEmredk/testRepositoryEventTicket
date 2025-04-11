package dk.easv;

import bll.UserManagement;
import bll.UserSession;
import dal.UserDAO;
import be.User;
import controllers.UserManagementController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SettingsView extends StackPane {

    private final UserDAO dao = new UserDAO();

    public SettingsView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Account Settings");
        title.setFont(Font.font("Arial", 24));
        title.setTextFill(Color.web("#2A0B06"));

        TextField usernameField = new TextField();
        User currentUser = UserSession.getLoggedInUser();

        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
        }

        usernameField.setEditable(false);
        usernameField.setFocusTraversable(false);
        usernameField.setMouseTransparent(true);

        usernameField.setStyle("""
    -fx-background-color: #f0f0f0;
                        -fx-text-fill: #555;
                        -fx-border-color: #ccc;
                        -fx-border-radius: 10;
                        -fx-background-radius: 10;
                        -fx-font-size: 14px;
                        -fx-padding: 10px;
                        -fx-opacity: 1;
                        -fx-text-inner-color: #888;
""");

        usernameField.setStyle(usernameField.getStyle() + "-fx-text-inner-color: #888;");


        PasswordField currentPass = new PasswordField();
        currentPass.setPromptText("Current Password");
        stylePasswordField(currentPass);

        PasswordField newPass = new PasswordField();
        newPass.setPromptText("New Password");
        stylePasswordField(newPass);

        PasswordField confirmPass = new PasswordField();
        confirmPass.setPromptText("Confirm New Password");
        stylePasswordField(confirmPass);

        Button uploadIconBtn = new Button("Change Profile Icon");
        uploadIconBtn.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 10 20;
            -fx-background-radius: 20;
            -fx-cursor: hand;
        """);

        uploadIconBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Profile Picture");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );

            File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (selectedFile != null) {
                if (currentUser == null) {
                    showAlert(Alert.AlertType.ERROR, "No user is currently logged in.");
                    return;
                }

                try {
                    byte[] imageBytes = Files.readAllBytes(selectedFile.toPath());

                    dao.updateProfileImage(currentUser.getUser_Id(), imageBytes);
                    currentUser.setProfileImage(imageBytes);

                    showAlert(Alert.AlertType.INFORMATION, "Profile picture updated!");
                    UserManagementController.refreshIfOpen();

                } catch (IOException | SQLException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Failed to save profile picture.");
                }
            }
        });

        Button saveBtn = new Button("Save Password");
        saveBtn.setStyle("""
            -fx-background-color: #2196F3;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 10 25 10 25;
            -fx-background-radius: 20;
            -fx-cursor: hand;
        """);

        saveBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String current = currentPass.getText();
            String newPassword = newPass.getText();
            String confirmPassword = confirmPass.getText();

            if (username.isEmpty() || current.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Please fill in all fields.");
                return;
            }

            User user = dao.getUserByUsername(username);

            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "User not found.");
                return;
            }

            if (!user.getPassword().equals(current)) {
                showAlert(Alert.AlertType.ERROR, "Current password is incorrect.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "New passwords do not match.");
                return;
            }

            user.setPassword(newPassword);
            dao.updatePassword(user);

            currentPass.clear();
            newPass.clear();
            confirmPass.clear();

            showAlert(Alert.AlertType.INFORMATION, "Password updated successfully. You will be logged out of the application.");

            try {
                UserSession.clearSession();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginMain.fxml"));
                Parent loginRoot = loader.load();

                Scene loginScene = new Scene(loginRoot, 700, 500);
                URL cssUrl = getClass().getResource("/view/loginstyle.css");
                if (cssUrl != null) {
                    loginScene.getStylesheets().add(cssUrl.toExternalForm());
                }

                Stage loginStage = new Stage();
                loginStage.setTitle("Login");
                loginStage.setScene(loginScene);
                loginStage.show();

                Stage currentStage = (Stage) saveBtn.getScene().getWindow();
                currentStage.close();

            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Something went wrong logging out.");
            }
        });

        container.getChildren().addAll(
                title,
                usernameField,
                currentPass,
                newPass,
                confirmPass,
                uploadIconBtn,
                saveBtn
        );
        this.getChildren().add(container);
        this.setStyle("-fx-background-color: white;");
    }

    private void stylePasswordField(PasswordField field) {
        field.setStyle("""
            -fx-pref-width: 300px;
            -fx-font-size: 14px;
            -fx-padding: 10px;
            -fx-background-radius: 10;
            -fx-border-color: #ccc;
            -fx-border-width: 1px;
        """);
    }

    private void styleTextField(TextField field) {
        field.setStyle("""
            -fx-pref-width: 300px;
            -fx-font-size: 14px;
            -fx-padding: 10px;
            -fx-background-radius: 10;
            -fx-border-color: #ccc;
            -fx-border-width: 1px;
        """);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Settings");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
