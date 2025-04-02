package controllers;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EventCoordinatorDashboard extends BaseDashboard {

    private Button ticketBtn;
    private Pane ticketPane;

    @Override
    protected void addCustomButtons(VBox sidebar, StackPane contentArea) {
        ticketBtn = createSidebarButton("🎫", "Ticket Management");
        TicketManagementController ticketController = new TicketManagementController();
        ticketPane = ticketController.loadTicketManagerView();

        contentArea.getChildren().add(ticketPane);
        ticketPane.setVisible(false);

        ticketBtn.setOnAction(e -> {
            switchPane(ticketPane);
            setActiveButton(ticketBtn, eventsBtn, ticketBtn, settingsBtn);
        });

        int insertAfterEventsBtn = sidebar.getChildren().indexOf(eventsBtn) + 1;
        sidebar.getChildren().add(insertAfterEventsBtn, ticketBtn);
    }
}
