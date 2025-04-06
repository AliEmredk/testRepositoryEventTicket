package dk.easv;

import dal.UserDAO;
import be.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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
            showAlert(Alert.AlertType.INFORMATION, "Password updated successfully.");

            currentPass.clear();
            newPass.clear();
            confirmPass.clear();
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
