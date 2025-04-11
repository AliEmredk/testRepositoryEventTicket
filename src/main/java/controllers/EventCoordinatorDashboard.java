package controllers;

import be.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class EventCoordinatorDashboard extends BaseDashboard {

    private Button ticketBtn;
    private Pane ticketPane;
    private final User user;

    @Override
    protected void addCustomButtons(VBox customButtons, StackPane contentArea) {
        ticketBtn = createSidebarButton("🎫", "Ticket");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TicketPreviewPage.fxml"));
            ticketPane = loader.load();
            TicketManagementController ticketController = loader.getController();

            contentArea.getChildren().add(ticketPane);
            ticketPane.setVisible(false);

            ticketBtn.setOnAction(e -> {
                switchPane(ticketPane);
                setActiveButton(ticketBtn, eventsBtn, settingsBtn, ticketBtn);
            });

            // Add the button before settingsBtn visually
            int settingsIndex = customButtons.getChildren().indexOf(settingsBtn);
            if (settingsIndex >= 0) {
                customButtons.getChildren().add(settingsIndex, ticketBtn);
            } else {
                customButtons.getChildren().add(ticketBtn);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load TicketPreviewPage.fxml");
        }
    }

    public EventCoordinatorDashboard(User user) {
        this.user = user;
    }
}
