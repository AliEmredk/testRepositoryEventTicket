package dk.easv;

import bll.UserSession;
import dal.UserDAO;
import be.User;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsView extends StackPane {

    public SettingsView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Change Password");
        title.setFont(Font.font("Arial", 24));
        title.setTextFill(Color.web("#2A0B06"));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        styleTextField(usernameField);

        PasswordField currentPass = new PasswordField();
        currentPass.setPromptText("Current Password");
        stylePasswordField(currentPass);

        PasswordField newPass = new PasswordField();
        newPass.setPromptText("New Password");
        stylePasswordField(newPass);

        PasswordField confirmPass = new PasswordField();
        confirmPass.setPromptText("Confirm New Password");
        stylePasswordField(confirmPass);

        Button saveBtn = new Button("Save Changes");
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

            UserDAO dao = new UserDAO();
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

            // Clear the fields first
            currentPass.clear();
            newPass.clear();
            confirmPass.clear();

            //Now show the success message
            showAlert(Alert.AlertType.INFORMATION, "Password updated successfully. You will be logged out of the application.");

            //App logs user out and goes back to the login screen
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

                // Close current stage
                Stage currentStage = (Stage) saveBtn.getScene().getWindow();
                currentStage.close();

            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Something went wrong logging out.");
            }
        });

        container.getChildren().addAll(title, usernameField, currentPass, newPass, confirmPass, saveBtn);
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
        alert.setTitle("Password Update");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
