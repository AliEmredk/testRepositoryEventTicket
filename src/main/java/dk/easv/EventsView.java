package dk.easv;

import be.Event;
import be.User;
import dal.EventDAO;
import dal.UserDAO;
import controllers.EventMainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class EventsView extends StackPane {

    private String role;
    private TilePane eventContainer;
    private EventDAO eventDAO = new EventDAO();
    private Event selectedEvent;
    private List<Event> masterEventList = new ArrayList<>();
    private EventMainController eventMainController;

    public EventsView(String role, EventMainController eventMainController) {
        VBox vbox = new VBox();
        vbox.getChildren().clear();
        this.role = role;
        this.eventMainController = eventMainController;


        VBox eventsPane = new VBox(10);
        eventsPane.setPadding(new Insets(15));
        eventsPane.setStyle("-fx-background-color: white;");

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button addEventBtn = new Button("Add Event");
        addEventBtn.setOnAction(e -> openAddEventWindow());

        Button deleteEventBtn = new Button("Delete Event");
        deleteEventBtn.setOnAction(e -> openDeleteEventWindow());

        TextField searchField = new TextField();
        searchField.setPromptText("Search events...");
        Button searchBtn = new Button("\uD83D\uDD0E"); //Magnifying Glass Emoji/Icon

        if(this.role.equals("Admin")) {
            addEventBtn.setVisible(false);
            addEventBtn.setManaged(false);
        }

        topBar.getChildren().addAll(addEventBtn, deleteEventBtn, searchField, searchBtn);

        searchBtn.setOnAction(e -> {
            String searchQuery = searchField.getText();
            eventMainController.searchEvents(searchQuery);  // Delegate to the controller
            refreshEventList();  // Refresh the list to display the filtered events
        });

        // Handle Sorting
        ComboBox<String> sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("Sort by Date", "Sort by Price", "Sort by Location");
        sortComboBox.setOnAction(e -> {
            String sortCriteria = sortComboBox.getValue();
            eventMainController.sortEvents(sortCriteria);  // Delegate sort action to the controller
            refreshEventList();  // Refresh list after sorting
        });

        // Handle Filtering
        ComboBox<String> filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll("Free Events", "Paid Events", "Today’s Events", "By Location", "By Price");
        filterComboBox.setOnAction(e -> {
            String filter = filterComboBox.getValue();
            eventMainController.filterEvents(filter);  // Delegate filter action to the controller
            refreshEventList();  // Refresh list after filtering
        });

        // Pagination controls
        Button btnNextPage = new Button("Next");
        Button btnPrevPage = new Button("Previous");
        btnNextPage.setOnAction(e -> {
            eventMainController.nextPage();
            refreshEventList();
        });
        btnPrevPage.setOnAction(e -> {
            eventMainController.previousPage();
            refreshEventList();
        });

        eventContainer = new TilePane();
        eventContainer.setHgap(10);
        eventContainer.setVgap(10);
        eventContainer.setPrefColumns(3);
        eventContainer.setPadding(new Insets(10));

        refreshEventList();

        ScrollPane scrollPane = new ScrollPane(eventContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600);
        scrollPane.setStyle("-fx-background: white;");

        eventsPane.getChildren().addAll(topBar, sortComboBox, filterComboBox, searchField, searchBtn, scrollPane, btnNextPage, btnPrevPage);

        this.getChildren().add(eventsPane);
    }

    private VBox createEventCard(Event event) {
        System.out.println("Creating tile for event: " + event.getEventName()); // Debugging line

        VBox card = new VBox();
        card.setPadding(new Insets(10));
        card.setSpacing(5);
        card.setAlignment(Pos.CENTER);
        card.setStyle("""
                -fx-background-color: #FFECB3;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-padding: 10;
                fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 3);
                """);

        Label nameLabel = new Label(event.getEventName());
        nameLabel.setFont(new Font(16));

        Label locationLabel = new Label(event.getLocation());
        locationLabel.setFont(new Font(12));
        locationLabel.setTextFill(Color.DARKGRAY);

        Label dateLabel = new Label(event.getDate());
        dateLabel.setFont(new Font(12));
        dateLabel.setTextFill(Color.DARKGRAY);

        Button editBtn = new Button("Edit");

        if(this.role.equals("Admin")) {
            editBtn.setVisible(false);
            editBtn.setManaged(false);
        }

        card.getChildren().addAll(nameLabel, locationLabel, dateLabel);
        card.setOnMouseClicked(e -> setSelectedEvent(event, card));

        return card; // ✅ Return the VBox without adding it to eventContainer
    }

    private void refreshEventList() {
        eventContainer.getChildren().clear();
        List<Event> filteredEvents = eventMainController.getFilteredEvents();  // Get the filtered list from the controller
        for (Event event : filteredEvents) {
            eventContainer.getChildren().add(createEventCard(event));
        }
        //masterEventList = eventDAO.getAllEvents();  // Get data from DB
        //System.out.println("Refreshing events: " + masterEventList.size()); // Debugging line
        //for (Event event : masterEventList) {
        //    System.out.println("Loaded Event: " + event.getEventName()); // Debugging line
        //VBox eventCard = createEventCard(event);
        //    eventContainer.getChildren().add(createEventCard(event));
    }

    private void openAddEventWindow() {
        Stage addEventsStage = new Stage();
        addEventsStage.initModality(Modality.APPLICATION_MODAL);
        addEventsStage.setTitle("Add Event");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);

        TextField nameField = new TextField();
        nameField.setPromptText("Event Name");

        TextField locationField = new TextField();
        locationField.setPromptText("Event Location");

        DatePicker datePicker = new DatePicker();

        TextField startTimeField = new TextField();
        startTimeField.setPromptText("Start Time");

        TextField endTimeField = new TextField();
        endTimeField.setPromptText("End Time");

        TextArea noteField = new TextArea();
        noteField.setPromptText("Notes");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        TextField locationGuidanceField = new TextField();
        locationGuidanceField.setPromptText("Location Guidance");

        UserDAO userDAO = new UserDAO();
        List<User> coordinators = userDAO.getAllEventCoordinators();
        ListView<User> coordinatorListView = new ListView<>();
        ObservableList<User> coordinatorObservableList = FXCollections.observableArrayList(coordinators);
        coordinatorListView.setItems(coordinatorObservableList);

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(event -> {
            if(nameField.getText().isEmpty() || locationField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            int price = 0;
            try {
                price = Integer.parseInt(priceField.getText());
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid price format!", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            Event newEvent = new Event(locationField.getText(), datePicker.getValue().toString(), startTimeField.getText(),
                    endTimeField.getText(), noteField.getText(), price, locationGuidanceField.getText(), nameField.getText(), 0);

            eventDAO.createEvent(newEvent);
            refreshEventList();

            for(User selectedUser : coordinatorListView.getSelectionModel().getSelectedItems()) {
                eventDAO.assignCoordinatorToEvent(newEvent.getEventId(), selectedUser.getUser_Id());
            }
            addEventsStage.close();
        });

        vbox.getChildren().addAll(nameField, locationField, datePicker, startTimeField, endTimeField, noteField, priceField, new Label("Assign Coordinators:"), coordinatorListView, saveBtn);

        Scene scene = new Scene(vbox, 400, 500);
        addEventsStage.setScene(scene);
        addEventsStage.show();
    }

    private void setSelectedEvent(Event event, VBox eventCard) {
        this.selectedEvent = event;
        for (var node : eventContainer.getChildren()) {
            node.setStyle("-fx-background-color: #FFECB3; -fx-border-radius: 10; -fx-background-radius: 10;");
        }
        eventCard.setStyle("-fx-background-color: #FFD54F; -fx-border-radius: 10; -fx-background-radius: 10;");
    }

    private void openDeleteEventWindow() {
        if(selectedEvent == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an event to delete!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Are you sure you want to delete this event?");
        confirmAlert.setContentText("Event: " + selectedEvent.getEventName() + " will be deleted.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if(response == ButtonType.OK) {
                EventDAO eventDAO = new EventDAO();
                eventDAO.deleteEvent(selectedEvent.getEventName());
                refreshEventList();
            }
        });
    }
}
