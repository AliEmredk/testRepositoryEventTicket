package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AdminDashboard extends BaseDashboard {

    @Override
    protected void addCustomButtons(VBox sidebar, StackPane contentArea) {
        // No ticket management for admins
    }

    public static void main(String[] args) {
        new AdminDashboard().start(new javafx.stage.Stage());
    }
    private Pane createAdminManagementView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMainView.fxml"));
            return loader.load(); // returns Pane that AdminMainController manages
        } catch (IOException e) {
            e.printStackTrace();
            return new StackPane(new Label("Failed to load admin view"));
        }
    }
}
