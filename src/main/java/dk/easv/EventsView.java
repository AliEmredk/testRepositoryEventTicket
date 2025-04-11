package dk.easv;

import be.Event;
import be.User;
import dal.CoordinatorEventDAO;
import dal.EventDAO;
import dal.UserDAO;
import controllers.EventMainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

public class EventsView extends StackPane {

    private String role;
    private TilePane eventContainer;
    private EventDAO eventDAO = new EventDAO();
    private Event selectedEvent;
    private Button editEventBtn, deleteEventBtn, assignCoordinatorBtn;
    private List<Event> masterEventList = new ArrayList<>();
    private EventMainController eventMainController;
    private final User loggedInUser;
    private final CoordinatorEventDAO coordinatorEventDAO = new CoordinatorEventDAO();

    public EventsView(User loggedInUser, EventMainController eventMainController) {
        BorderPane mainLayout = new BorderPane();
        this.loggedInUser = loggedInUser;
        this.eventMainController = eventMainController;
        this.role = loggedInUser.getRole();


        VBox eventsPane = new VBox(10);
        eventsPane.setPadding(new Insets(15));
        eventsPane.setStyle("-fx-background-color: white;");

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0,0,10,0));

        Button addEventBtn = new Button("Add");
        addEventBtn.setOnAction(e -> openAddEventWindow());

        editEventBtn = new Button("Edit");
        editEventBtn.setDisable(true);
        editEventBtn.setOnAction(e -> openEditEventWindow());

        deleteEventBtn = new Button("Delete");
        deleteEventBtn.setDisable(true);
        deleteEventBtn.setOnAction(e -> openDeleteEventWindow());

        assignCoordinatorBtn = new Button("Assign Coordinator");
        assignCoordinatorBtn.setDisable(true);
        assignCoordinatorBtn.setOnAction(e -> openAssignCoordinatorWindow());

        if(!this.role.equalsIgnoreCase("Admin")) {
            assignCoordinatorBtn.setVisible(false);
            assignCoordinatorBtn.setManaged(false);
        }

        // Handle Sorting
        ComboBox<String> sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("None", "Sort by Date", "Sort by Price", "Sort by Location");
        sortComboBox.setOnAction(e -> {
            String sortCriteria = sortComboBox.getValue();
            if (sortCriteria.equals("None")) {
                eventMainController.loadEvents(); // Reset sort by reloading full list
            } else {
                eventMainController.sortEvents(sortCriteria);
            }
            refreshEventList();
        });

        // Handle Filtering
        ComboBox<String> filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll("None", "Free Events", "Paid Events", "Today’s Events", "This Month’s Events");
        filterComboBox.setOnAction(e -> {
            String filter = filterComboBox.getValue();
            eventMainController.filterEvents(filter);  // Delegate filter action to the controller
            refreshEventList();  // Refresh list after filtering
        });

        TextField searchField = new TextField();
        searchField.setPromptText("Search events...");
        Button searchBtn = new Button("\uD83D\uDD0E"); //Magnifying Glass Emoji/Icon

        if(this.role.equals("Admin")) {
            addEventBtn.setVisible(false);
            addEventBtn.setManaged(false);
        }

        topBar.getChildren().addAll(addEventBtn, editEventBtn, deleteEventBtn, assignCoordinatorBtn, sortComboBox, filterComboBox, searchField, searchBtn);

        searchBtn.setOnAction(e -> {
            String searchQuery = searchField.getText();
            eventMainController.searchEvents(searchQuery);  // Delegate to the controller
            refreshEventList();  // Refresh the list to display the filtered events
        });
        if(this.role.equals("Admin")) {
            editEventBtn.setVisible(false);
            editEventBtn.setManaged(false);
        }

        eventContainer = new TilePane();
        eventContainer.setHgap(10);
        eventContainer.setVgap(10);
        eventContainer.setPrefColumns(2);
        eventContainer.setPadding(new Insets(10));

        refreshEventList();
        //This part duplicates the Events in the Event list, so it's commented out for now
//        EventDAO eventDAO = new EventDAO();
//        for (Event event : eventDAO.getAllEvents()) {
//            VBox eventCard = createEventCard(event);
//            eventContainer.getChildren().add(eventCard);
//        }

        ScrollPane scrollPane = new ScrollPane(eventContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane.setStyle("-fx-background: white;");

        eventsPane.getChildren().addAll(topBar, scrollPane);
        mainLayout.setCenter(eventsPane);
        this.getChildren().add(mainLayout);
    }

    private VBox createEventCard(Event event) {
        System.out.println("Creating tile for event: " + event.getEventName()); // Debugging line

        VBox card = new VBox();
        card.setPadding(new Insets(10));
        card.setSpacing(5);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);
        card.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(card, Priority.ALWAYS);
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

        card.setOnMouseClicked(e -> {
            if(e.getClickCount() == 2) {
                BorderPane mainLayout = (BorderPane) this.getChildren().get(0);
                showEventDetailsSideBar(event, mainLayout);
            } else {
                setSelectedEvent(event, card);
            }
        });

        return card;
    }

    private void refreshEventList() {
        eventMainController.loadEvents();
        eventContainer.getChildren().clear();
        List<Event> filteredEvents = eventMainController.getFilteredEvents();  // Get the filtered list from the controller
        for (Event event : filteredEvents) {
            eventContainer.getChildren().add(createEventCard(event));
        /* TODO - check this!
        for(Event event : eventDAO.getAllEvents()) {
            VBox eventCard = createEventCard(event);
            eventContainer.getChildren().add(eventCard);
            */
        }
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
        ObservableList<User> coordinatorObservableList = FXCollections.observableArrayList(coordinators);
        ListView<User> coordinatorListView = new ListView<>();
        coordinatorListView.setItems(coordinatorObservableList);
        coordinatorListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //List<Integer> assignedCoordinatorIds = eventDAO.getCoordinatorIdsForEvent(selectedEvent.getEventId());
//        for(User user : coordinators) {
//            if(assignedCoordinatorIds.contains(user.getUser_Id())) {
//                coordinatorListView.getSelectionModel().select(user);
//            }
//        }

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(event -> {

            String eventName = nameField.getText().trim();
            String location = locationField.getText().trim();

            if(nameField.getText().isEmpty() || locationField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            int price;
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
            CoordinatorEventDAO coordinatorEventDAO = new CoordinatorEventDAO();

            //eventDAO.clearCoordinatorsForEvent(selectedEvent.getEventId());
            for(User selectedUser : coordinatorListView.getSelectionModel().getSelectedItems()) {
                String username = selectedUser.getUsername();
                coordinatorEventDAO.assignCoordinatorToEventByNames(username, eventName);
            }

            refreshEventList();
            addEventsStage.close();
        });

        vbox.getChildren().addAll(nameField, locationField, datePicker, startTimeField, endTimeField, noteField, priceField, locationGuidanceField, new Label("Assign Coordinators:"), coordinatorListView, saveBtn);

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

        editEventBtn.setDisable(false);
        deleteEventBtn.setDisable(false);
        assignCoordinatorBtn.setDisable(false);
    }

    private void openEditEventWindow() {
        if(selectedEvent == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an event", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Stage editEventsStage = new Stage();
        editEventsStage.initModality(Modality.APPLICATION_MODAL);
        editEventsStage.setTitle("Edit Event");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);

        TextField nameField = new TextField(selectedEvent.getEventName());
        nameField.setPromptText("Event Name");
        TextField locationField = new TextField(selectedEvent.getLocation());
        locationField.setPromptText("Event Location");
        DatePicker datePicker = new DatePicker(LocalDate.parse(selectedEvent.getDate()));
        TextField startTimeField = new TextField(selectedEvent.getStartTime());
        startTimeField.setPromptText("Start Time");
        TextField endTimeField = new TextField(selectedEvent.getEndTime());
        endTimeField.setPromptText("End Time");
        TextArea noteField = new TextArea(selectedEvent.getNote());
        noteField.setPromptText("Notes");
        TextField priceField = new TextField(String.valueOf(selectedEvent.getPrice()));
        priceField.setPromptText("Price");
        TextField locationGuidanceField = new TextField(selectedEvent.getLocation_Guidance());
        locationGuidanceField.setPromptText("Location Guidance");
        ListView<User> coordinatorListView = new ListView<>();


        Button saveBtn = new Button("Save Changes");
        saveBtn.setOnAction(e -> {
            if(nameField.getText().isEmpty() || locationField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            int price;
            try {
                price = Integer.parseInt(priceField.getText());
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid price format!", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            selectedEvent.setEventName(nameField.getText());
            selectedEvent.setLocation(locationField.getText());
            selectedEvent.setDate(datePicker.getValue().toString());
            selectedEvent.setStartTime(startTimeField.getText());
            selectedEvent.setEndTime(endTimeField.getText());
            selectedEvent.setNote(noteField.getText());
            selectedEvent.setPrice(price);
            selectedEvent.setLocation_Guidance(locationGuidanceField.getText());

            eventDAO.updateEvent(selectedEvent.getEventId(), selectedEvent);
            refreshEventList();
            editEventsStage.close();
            editEventBtn.setDisable(true);
            deleteEventBtn.setDisable(true);
        });

        vbox.getChildren().addAll(nameField, locationField, datePicker, startTimeField, endTimeField, noteField, priceField, locationGuidanceField, saveBtn);

        Scene scene = new Scene(vbox, 400, 500);
        editEventsStage.setScene(scene);
        editEventsStage.show();
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
                eventDAO.deleteEvent(selectedEvent.getEventId());

                refreshEventList();
            }
            deleteEventBtn.setDisable(true);
        });
    }

    private void showEventDetailsSideBar(Event event, BorderPane mainLayout) {
        VBox sideBar = new VBox(10);
        sideBar.setPadding(new Insets(15));
        sideBar.setStyle("-fx-background-color: #FFF8E1;");
        sideBar.setPrefWidth(300);

        HBox header = new HBox();
        Label title = new Label("Event Details");
        title.setFont(new Font("Arial", 18));
        title.setStyle("-fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("❌");
        closeBtn.setStyle("-fx-background-color: #FF6F61; -fx-font-size: 14;");
        closeBtn.setOnAction(e -> mainLayout.setRight(null)); //Removes sidebar

        header.getChildren().addAll(title, spacer, closeBtn);

        Label name = new Label("Name: " + event.getEventName());
        Label location = new Label("Location: " + event.getLocation());
        Label date = new Label("Date: " + event.getDate());
        Label time = new Label("Time: " + event.getStartTime() + " - " + event.getEndTime());
        Label price = new Label("Price: " + event.getPrice() + " DKK");
        Label notes = new Label("Notes: " + event.getNote());
        Label guidance = new Label("Location Guidance: " + event.getLocation_Guidance());

        for (Label lbl : List.of(location, date, time, price, notes, guidance)) {
            lbl.setFont(new Font("Arial", 13));
        }

        sideBar.getChildren().addAll(header, name, location, date, time, price, notes, guidance);
        mainLayout.setRight(sideBar);
    }

    private void openAssignCoordinatorWindow() {
        if(selectedEvent == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an event to assign coordinator!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Stage assignStage = new Stage();
        assignStage.setTitle("Assign Coordinator");
        assignStage.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);

        UserDAO userDAO = new UserDAO();
        List<User> coordinators = userDAO.getAllEventCoordinators();
        ObservableList<User> coordinatorsObservableList = FXCollections.observableList(coordinators);

        ListView<User> coordinatorListView = new ListView<>(coordinatorsObservableList);
        coordinatorListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        List<Integer> assignedCoordinatorIds = coordinatorEventDAO.getCoordinatorIdsForEvent(selectedEvent.getEventId());
        for(User user : coordinators) {
            if(assignedCoordinatorIds.contains(user.getUser_Id())) {
                coordinatorListView.getSelectionModel().select(user);
            }
        }

        Button assignBtn = new Button("Assign");
        assignBtn.setOnAction(e -> {
            coordinatorEventDAO.clearCoordinatorsForEvent(selectedEvent.getEventId());
            for(User user : coordinatorListView.getSelectionModel().getSelectedItems()) {
                coordinatorEventDAO.assignCoordinatorToEventById(user.getUser_Id(), selectedEvent.getEventId());
                System.out.println("Assigned UserId " + user.getUser_Id());
            }
            assignStage.close();
        });

        vbox.getChildren().addAll(new Label("Select Coordinator:"), coordinatorListView, assignBtn);
        Scene scene = new Scene(vbox, 400, 400);
        assignStage.setScene(scene);
        assignStage.show();
    }
}
