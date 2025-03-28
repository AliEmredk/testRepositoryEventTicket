package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AdminDashboard extends BaseDashboard {

    private Button userManagementBtn;
    private Pane userManagementPane;

    @Override
    protected void addCustomButtons(VBox sidebar, StackPane contentArea) {
        // Create "User Management" sidebar button
        userManagementBtn = createSidebarButton("👥", "User Management");

        // Load the actual FXML-based user management view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserManagement.fxml"));
            userManagementPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            userManagementPane = createContentPane("⚠ Failed to load User Management FXML");
        }

        // Add to content area but keep hidden initially
        contentArea.getChildren().add(userManagementPane);
        userManagementPane.setVisible(false);

        // Button logic
        userManagementBtn.setOnAction(e -> {
            switchPane(userManagementPane);
            setActiveButton(userManagementBtn, userManagementBtn, eventsBtn, settingsBtn);
        });

        // Insert the button right after the logo (at index 1)
        sidebar.getChildren().add(1, userManagementBtn);
    }

    public static void main(String[] args) {
        new AdminDashboard().start(new javafx.stage.Stage());
    }
}