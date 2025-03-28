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
        ticketPane = createContentPane("Ticket Management Content");

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
