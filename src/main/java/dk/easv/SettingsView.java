package dk.easv;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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

        container.getChildren().addAll(title, currentPass, newPass, confirmPass, saveBtn);
        this.getChildren().add(container);
        this.setStyle("-fx-background-color: white;");
    }

    private void stylePasswordField(PasswordField field) {
        field.setStyle(
                """
            -fx-pref-width
                : 300px;
                        -fx-font-size: 14px;
            -fx-padding: 10px;
                        -fx-background-radius: 10;
            -fx-border-color: #ccc;
        -fx-border-width: 1px;
        """);
    }
    }
